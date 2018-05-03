package com.instancy.instancylearning.mylearning;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.ReportDetail;
import com.instancy.instancylearning.models.ReportDetailsForQuestions;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;


/**
 * Created by Upendranath on 7/28/2017 Working on InstancyLearning.
 */
public class Reports_Activity extends AppCompatActivity {


    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = Reports_Activity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    DatabaseHandler db;

    ReportAdapter reportAdapter;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    ReportDetail reportDetail;
    List<ReportDetail> reportDetailList;


    List<ReportDetailsForQuestions> reportDetailsForQuestionsArrayList;

    @Nullable
    @BindView(R.id.layout_relative)
    RelativeLayout relativeLayout;

    @Nullable
    @BindView(R.id.reportslistview)
    ListView reportsListview;

    MyLearningModel learningModel;

    @Nullable
    @BindView(R.id.txt_title)
    TextView txtName;

    @Nullable
    @BindView(R.id.card_view)
    CardView card_view;

    @Nullable
    @BindView(R.id.txt_starteddate)
    TextView txtStartDate;

    @Nullable
    @BindView(R.id.txt_datecompleted)
    TextView txtDateCompleted;

    @Nullable
    @BindView(R.id.txt_status)
    TextView txtStatus;

    @Nullable
    @BindView(R.id.txt_timespent)
    TextView txtTimeSpent;

    @Nullable
    @BindView(R.id.txt_score)
    TextView txtScore;

    @Nullable
    @BindView(R.id.nodata_label)
    TextView nodataLabel;

    String typeFrom = "";

    View header;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initVolleyCallback();
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

        db = new DatabaseHandler(this);
        ButterKnife.bind(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        reportDetailList = new ArrayList<>();
        reportDetailsForQuestionsArrayList = new ArrayList<>();

        reportAdapter = new ReportAdapter(this, BIND_ABOVE_CLIENT, reportDetailList, reportDetailsForQuestionsArrayList, false);
        reportsListview.setAdapter(reportAdapter);

        header = (View) getLayoutInflater().inflate(R.layout.detailsheader, null);
        TextView headerTextView = (TextView) header.findViewById(R.id.track_details);
        headerTextView.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        learningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
        typeFrom = (String) getIntent().getStringExtra("typeFrom");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "Progress Report" + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        if (isNetworkConnectionAvailable(this, -1)) {
            getDownloadedMobileContentMetaData();

        } else {
            injectFromDbtoModel();

        }

    }

    public void getDownloadedMobileContentMetaData() {

        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        nodataLabel.setText("");
        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetMobileContentMetaData?SiteURL=" + appUserModel.getSiteURL() + "&ContentID=" + learningModel.getContentID() + "&userid=" + learningModel.getUserID() + "&DelivoryMode=1&IsDownload=0&IsDownload=0";

        vollyService.getJsonObjResponseVolley("DMCD", urlString, appUserModel.getAuthHeaders());

    }

    public void getDownloadedMobileTrackingData() {

        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?_studid=" + learningModel.getUserID() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + appUserModel.getSiteURL() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        vollyService.getJsonObjResponseVolley("DMTD", urlString, appUserModel.getAuthHeaders());

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                if (requestType.equalsIgnoreCase("DMCD")) {
                    Log.d(TAG, "notifySuccess: DMCD" + response);
                    try {
                        db.insertTrackObjectsForReports(response, learningModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getDownloadedMobileTrackingData();
                }
                if (requestType.equalsIgnoreCase("DMTD")) {

                    Log.d(TAG, "notifySuccess: DMTD" + response);

                    try {
                        db.injectCMIDataInto(response, learningModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    injectFromDbtoModel();
                }
//

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {
                svProgressHUD.dismiss();
            }
        };
    }

    public void injectFromDbtoModel() {

        if (learningModel.getObjecttypeId().equalsIgnoreCase("8") || learningModel.getObjecttypeId().equalsIgnoreCase("9") || learningModel.getObjecttypeId().equalsIgnoreCase("70") || learningModel.getObjecttypeId().equalsIgnoreCase("26")) {

            boolean isEvent = false;

            if (!learningModel.getRelatedContentCount().equalsIgnoreCase("0")) {
                isEvent = true;

            } else {
                isEvent = false;

            }

            reportDetail = db.getReportForContent(learningModel, isEvent, typeFrom);

        } else if (learningModel.getObjecttypeId().equalsIgnoreCase("10")) {

            reportDetail = db.getReportTrack(learningModel, false);

        }

        if (learningModel.getObjecttypeId().equalsIgnoreCase("8") || learningModel.getObjecttypeId().equalsIgnoreCase("9")) {

            List<ReportDetailsForQuestions> reportDetailsForQuestionsArrayList = new ArrayList<>();
            reportDetailsForQuestionsArrayList = db.fetchReportOfQuestions(learningModel);
            if (reportDetailsForQuestionsArrayList != null) {
                reportAdapter.refreshList(reportDetailList, reportDetailsForQuestionsArrayList, true);
            }
        }

        if (learningModel.getObjecttypeId().equalsIgnoreCase("10")) {

            reportDetailList = db.getReportForTrackListItems(learningModel);
            if (reportDetailList != null && reportDetailList.size() > 0) {

                reportAdapter.refreshList(reportDetailList, reportDetailsForQuestionsArrayList, false);

                // here time need to caluculate

                String getTotalTime = "0:00:00";

                if (reportDetailList.size() > 0) {
                    getTotalTime = returnTotalTime(reportDetailList);
                    reportDetail.timeSpent = getTotalTime;
                    reportsListview.addHeaderView(header);

                } else {

                    getTotalTime = "0:00:00";
                }


            }
        }

        if (reportDetail != null) {
            updateUI(reportDetail);

        }

        svProgressHUD.dismiss();
    }

    public String returnTotalTime(List<ReportDetail> reportDetailList) {

        String timeStr = "0:00:00";

        for (int i = 0; i < reportDetailList.size(); i++) {


            Log.d(TAG, "returnTotalTime: " + reportDetailList.get(i).timeSpent);

            if (isValidString(reportDetailList.get(i).timeSpent)) {
                String[] strSplitvalues = timeStr.split(":");
                String[] strSplitvalues1 = reportDetailList.get(i).timeSpent.split(":");
                if (strSplitvalues.length == 3
                        && strSplitvalues1.length == 3) {
                    try {
                        int hours1 = (Integer
                                .parseInt(strSplitvalues[0]) + Integer
                                .parseInt(strSplitvalues1[0])) * 3600;
                        int mins1 = (Integer
                                .parseInt(strSplitvalues[1]) + Integer
                                .parseInt(strSplitvalues1[1])) * 60;
                        int secs1 = (int) (Float
                                .parseFloat(strSplitvalues[2]) + Float
                                .parseFloat(strSplitvalues1[2]));

                        int totaltime = hours1 + mins1 + secs1;
                        long longVal = totaltime;

                        int hours = (int) longVal / 3600;

                        int remainder = (int) longVal - hours
                                * 3600;

                        int mins = remainder / 60;

                        remainder = remainder - mins * 60;

                        int secs = remainder;

                        timeStr = hours + ":" + mins
                                + ":" + secs;

                    } catch (Exception ex) {

                    }
                }
            }

        }

        return timeStr;
    }

    public void updateUI(ReportDetail reportDetail) {

        txtName.setText(learningModel.getCourseName());

        if (isValidString(reportDetail.dateCompleted)) {
            txtDateCompleted.setText("Date Completed: " + reportDetail.dateCompleted);
        }

        if (isValidString(reportDetail.dateStarted)) {
            txtStartDate.setText("Date Started  : " + reportDetail.dateStarted);
        }

        if (isValidString(reportDetail.timeSpent)) {
            txtTimeSpent.setText("Time Spent: " + reportDetail.timeSpent);
        } else {
            txtTimeSpent.setText("Time Spent: 0:00:00");
        }

        nodataLabel.setText("");

        txtStatus.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
//        txtStatus.setTextSize(12);

        String statusFromModel = learningModel.getStatus();

        String displayStatus = learningModel.getStatus();

        if (statusFromModel.equalsIgnoreCase("Completed") || (statusFromModel.toLowerCase().contains("passed") || statusFromModel.toLowerCase().contains("failed")) || statusFromModel.equalsIgnoreCase("completed")) {

            if (statusFromModel.toLowerCase().equalsIgnoreCase("failed")) {
                displayStatus = "Completed (failed)";
            }

            if (statusFromModel.toLowerCase().equalsIgnoreCase("passed")) {
                displayStatus = "Completed (passed)";
            }

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
            reportDetail.score = "100";
        } else if (statusFromModel.equalsIgnoreCase("Not Started")) {
            reportDetail.score = "0";
            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusNotStarted));
            displayStatus = learningModel.getStatus();
        } else if (statusFromModel.equalsIgnoreCase("incomplete") || (statusFromModel.toLowerCase().contains("inprogress")) || (statusFromModel.toLowerCase().contains("in progress"))) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusInProgress));
            reportDetail.score = "50";
            displayStatus = "In Progress";
        } else if (statusFromModel.equalsIgnoreCase("pending review") || (statusFromModel.toLowerCase().contains("pendingreview"))) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));
            reportDetail.score = "100";
        } else if (statusFromModel.equalsIgnoreCase("Registered") || (statusFromModel.toLowerCase().contains("registered"))) {

            displayStatus = "Registered";
            txtStatus.setTextColor(getResources().getColor(R.color.colorGray));


        } else if (statusFromModel.toLowerCase().contains("attended") || (statusFromModel.toLowerCase().contains("registered"))) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));
            displayStatus = learningModel.getStatus();
        } else if (statusFromModel.toLowerCase().contains("Expired")) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));
            displayStatus = "Expired";
        } else {

            txtStatus.setTextColor(getResources().getColor(R.color.colorGray));
        }

        txtScore.setText("Score: " + reportDetail.score);
        txtStatus.setText(upperCaseWords(displayStatus));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
