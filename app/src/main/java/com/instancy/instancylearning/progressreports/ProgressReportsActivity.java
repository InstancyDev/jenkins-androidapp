package com.instancy.instancylearning.progressreports;


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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;


/**
 * Created by Upendranath on 7/28/2017 Working on InstancyLearning.
 */
public class ProgressReportsActivity extends AppCompatActivity {


    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = ProgressReportsActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    ProgressReportDetailAdapter reportAdapter;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    List<ProgressReportQuestionDetailsModel> reportDetailsForQuestionsArrayList;

    ProgressReportModel progressReportModel;

    @Nullable
    @BindView(R.id.reportslistview)
    ListView reportsListview;

    @Nullable
    @BindView(R.id.nodata_label)
    TextView nodataLabel;


    View header;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressreportsummuryactivity);
        initVolleyCallback();
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

        ButterKnife.bind(this);

        uiSettingsModel = UiSettingsModel.getInstance();


        svProgressHUD = new SVProgressHUD(context);

        reportDetailsForQuestionsArrayList = new ArrayList<>();

        reportAdapter = new ProgressReportDetailAdapter(this, reportDetailsForQuestionsArrayList);
        reportsListview.setAdapter(reportAdapter);
        header = (View) getLayoutInflater().inflate(R.layout.progresssummuryview, null);
        reportsListview.addHeaderView(header);


        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);

        progressReportModel = (ProgressReportModel) getIntent().getSerializableExtra("progressReportModel");

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
            getProgressdataDetails(progressReportModel);
            getSummaryData(progressReportModel);
        } else {

            Toast.makeText(context, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public void getProgressdataDetails(ProgressReportModel reportModel) {

        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/getprogressdatadetails?CID=" + reportModel.objectID + "&ObjectTypeID=" + reportModel.objectTypeID + "&UserID=" + reportModel.userid + "&StartDate=" + reportModel.datestarted + "&EndDate=" + getCurrentDateTime("yyyy-MM-dd HH:mm:ss") +
                "&SeqID=" + reportModel.seqId + "&TrackID=" + reportModel.objectID + "&siteid=" + reportModel.siteID + "&locale=en-us&EventID=" + reportModel.SCOID;

        urlString = urlString.replaceAll(" ", "%20");

        vollyService.getJsonObjResponseVolley("getprogressdatadetails", urlString, appUserModel.getAuthHeaders());

    }

    public void getSummaryData(ProgressReportModel reportModel) {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/getsummarydata?CID=" + reportModel.objectID + "&ObjectTypeID=" + reportModel.objectTypeID + "&UserID=" + reportModel.userid + "&StartDate=" + reportModel.datestarted + "&EndDate=" + getCurrentDateTime("yyyy-MM-dd HH:mm:ss") +
                "&SeqID=" + reportModel.seqId + "&TrackID=" + reportModel.objectID + "&locale=en-us&EventID=" + reportModel.SCOID;

        urlString = urlString.replaceAll(" ", "%20");

        vollyService.getStringResponseVolley("getsummarydata", urlString, appUserModel.getAuthHeaders());

    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                if (requestType.equalsIgnoreCase("getprogressdatadetails")) {
                    if (response != null) {


                        try {
                            reportDetailsForQuestionsArrayList = generateReportsQuestionDetailList(response);

                            if (reportDetailsForQuestionsArrayList.size() > 0) {

                                reportAdapter.refreshList(reportDetailsForQuestionsArrayList);
                                nodataLabel.setText("");
                            } else {
                                nodataLabel.setText(getResources().getString(R.string.no_data));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                }
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
                if (requestType.equalsIgnoreCase("getsummarydata")) {
                    if (response != null) {

                        try {
                            updateHeaderUI(generateDetailModel(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                    svProgressHUD.dismiss();
                }


            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {
                svProgressHUD.dismiss();
            }
        };
    }


    public void updateHeaderUI(ProgressReportSummaryModel summaryModel) {


        TextView reportTitle = (TextView) header.findViewById(R.id.reportTitle);
        TextView reportSubTitle = (TextView) header.findViewById(R.id.reportSubTitle);
        TextView txtStatus = (TextView) header.findViewById(R.id.txtStatus);
        TextView lbStatus = (TextView) header.findViewById(R.id.lbStatus);
        TextView txtAssignedDate = (TextView) header.findViewById(R.id.txtAssignedDate);
        TextView txtTargetDate = (TextView) header.findViewById(R.id.txtTargetDate);
        TextView txtDateStarted = (TextView) header.findViewById(R.id.txtDateStarted);
        TextView txtDateCompleted = (TextView) header.findViewById(R.id.txtDateCompleted);
        TextView txtTimeSpent = (TextView) header.findViewById(R.id.txtTimeSpent);
        TextView txtTimeAccessedInThisPeriod = (TextView) header.findViewById(R.id.txtTimeAccessedInThisPeriod);
        TextView txtTimeAttemptInThisPeriod = (TextView) header.findViewById(R.id.txtTimeAttemptInThisPeriod);
        TextView txtScore = (TextView) header.findViewById(R.id.txtScore);
        TextView txtDetails = (TextView) header.findViewById(R.id.txtDetails);
        TextView txtPercentCompleted = (TextView) header.findViewById(R.id.txtPercentCompleted);
        CardView cardView = (CardView) header.findViewById(R.id.card_view);

        reportTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        reportSubTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        lbStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAssignedDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtTargetDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDateStarted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDateCompleted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtTimeSpent.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtTimeAccessedInThisPeriod.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtTimeAttemptInThisPeriod.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtScore.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDetails.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtPercentCompleted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
//        cardView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        if (isValidString(summaryModel.courseName)) {
            reportTitle.setText("Report:" + summaryModel.courseName);
        } else {
            reportTitle.setText("Report:");
        }

        if (isValidString(summaryModel.courseName)) {
            reportSubTitle.setText(summaryModel.courseName);
        } else {
            reportSubTitle.setText("");
        }

        if (isValidString(summaryModel.status)) {
            txtStatus.setText(summaryModel.status);
        } else {
            txtStatus.setText("");
        }

        if (isValidString(summaryModel.assignedDate)) {
            txtAssignedDate.setText("Assigned Date: " + summaryModel.assignedDate);
        } else {
            txtAssignedDate.setText("Assigned Date:");
        }

        if (isValidString(summaryModel.targetDate)) {
            txtTargetDate.setText("Target Date: " + summaryModel.targetDate);
        } else {
            txtTargetDate.setText("Target Date: ");
        }

        if (isValidString(summaryModel.dateStarted)) {
            txtDateStarted.setText("Date Started: " + summaryModel.dateStarted);
        } else {
            txtDateStarted.setText("Date Started: ");
        }

        if (isValidString(summaryModel.dateCompleted)) {
            txtDateCompleted.setText("Date Completed: " + summaryModel.dateCompleted);
        } else {
            txtDateCompleted.setText("Date Completed: ");
        }

        if (isValidString(summaryModel.timeSpent)) {
            txtTimeSpent.setText("Time Spent: " + summaryModel.timeSpent);
        } else {
            txtTimeSpent.setText("Time Spent: ");
        }

        if (isValidString(summaryModel.numberOfTimeAccessedInThisPeriod)) {
            txtTimeAccessedInThisPeriod.setText("# Times Accessed in This Period: " + summaryModel.numberOfTimeAccessedInThisPeriod);
        } else {
            txtTimeAccessedInThisPeriod.setText("# Times Accessed in This Period: ");
        }

        if (isValidString(summaryModel.numberOfAttemptsInThisPeriod)) {
            txtTimeAttemptInThisPeriod.setText("#Times Attempts in This Period: " + summaryModel.numberOfAttemptsInThisPeriod);
        } else {
            txtTimeAttemptInThisPeriod.setText("#Times Attempts in This Period: ");
        }

        if (isValidString(summaryModel.score)) {
            txtScore.setText("Score: " + summaryModel.score);
        } else {
            txtScore.setText("Score: ");
        }

        if (isValidString(summaryModel.percentageCompleted)) {
            txtPercentCompleted.setText("%Completed: " + summaryModel.percentageCompleted);
        } else {
            txtPercentCompleted.setText("%Completed: ");
        }


        if (summaryModel.status.equalsIgnoreCase("Completed") || (summaryModel.status.toLowerCase().contains("passed") || summaryModel.status.toLowerCase().contains("failed")) || summaryModel.status.equalsIgnoreCase("completed")) {


            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusCompleted));

        } else if (summaryModel.status.equalsIgnoreCase("Not Started")) {


            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusNotStarted));


        } else if (summaryModel.status.equalsIgnoreCase("incomplete") || (summaryModel.status.toLowerCase().contains("inprogress")) || (summaryModel.status.toLowerCase().contains("in progress"))) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusInProgress));

        } else if (summaryModel.status.equalsIgnoreCase("pending review") || (summaryModel.status.toLowerCase().contains("pendingreview")) || (summaryModel.status.toLowerCase().contains("grade"))) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));

        } else if (summaryModel.status.equalsIgnoreCase("Registered") || (summaryModel.status.toLowerCase().contains("registered"))) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorGray));

        } else if (summaryModel.status.toLowerCase().contains("attended") || (summaryModel.status.toLowerCase().contains("registered"))) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));

        } else if (summaryModel.status.toLowerCase().contains("Expired")) {

            txtStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));

        }

        txtStatus.setText(upperCaseWords(summaryModel.status));


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

    public List<ProgressReportQuestionDetailsModel> generateReportsQuestionDetailList(JSONObject jsonResponse) throws JSONException {

        List<ProgressReportQuestionDetailsModel> progressReportQuestionDetailsModelList = new ArrayList<>();

        if (jsonResponse != null) {
            JSONArray jsonArray = jsonResponse.getJSONArray("Table");
            if (jsonArray != null && jsonArray.length() > 0) {


                for (int i = 0; i < jsonArray.length(); i++) {
                    ProgressReportQuestionDetailsModel progressReportQuestionDetailsModel = new ProgressReportQuestionDetailsModel();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);


                    progressReportQuestionDetailsModel.pageID = jsonObject.optString("PageID");
                    progressReportQuestionDetailsModel.pageOrQuestionTitle = jsonObject.optString("Page Question Title");
                    progressReportQuestionDetailsModel.type = jsonObject.optString("Type");
                    progressReportQuestionDetailsModel.contentID = jsonObject.optString("ContentID");
                    progressReportQuestionDetailsModel.questionNo = jsonObject.optString("Question No.");
                    progressReportQuestionDetailsModel.folderPath = jsonObject.optString("FolderPath");
                    progressReportQuestionDetailsModel.questionTitle = jsonObject.optString("QuestionTitle");
                    progressReportQuestionDetailsModel.questionNumber = jsonObject.optInt("QuestionNumber");
                    progressReportQuestionDetailsModel.status = jsonObject.optString("Status");
                    progressReportQuestionDetailsModel.actions = jsonObject.optString("Actions");
                    progressReportQuestionDetailsModelList.add(progressReportQuestionDetailsModel);

                }

            }

        }

        return progressReportQuestionDetailsModelList;
    }


    public ProgressReportSummaryModel generateDetailModel(String responseRecieved) throws JSONException {

        ProgressReportSummaryModel progressReportSummaryModel = new ProgressReportSummaryModel();

        if (responseRecieved != null && responseRecieved.length() > 0) {

            JSONArray jsonArray = new JSONArray(responseRecieved);

            if (jsonArray != null && jsonArray.length() > 0) {
                progressReportSummaryModel = new ProgressReportSummaryModel();
                JSONObject columnObj = jsonArray.getJSONObject(0);

                progressReportSummaryModel.assignedDate = columnObj.optString("AssignedDate");
                progressReportSummaryModel.dateStarted = columnObj.optString("DateStarted");
                progressReportSummaryModel.dateCompleted = columnObj.optString("DateCompleted");
                progressReportSummaryModel.timeSpent = columnObj.optString("TotalTimeSpent");
                progressReportSummaryModel.numberOfTimeAccessedInThisPeriod = columnObj.optString("NumberofTimesAccessedinthisperiod");
                progressReportSummaryModel.numberOfAttemptsInThisPeriod = columnObj.optString("Numberofattemptsinthisperiod");
                progressReportSummaryModel.lastAccessedTime = columnObj.optString("LastAccessedInThisPeriod");
                progressReportSummaryModel.status = columnObj.optString("Status");
                progressReportSummaryModel.result = columnObj.optString("Result");
                progressReportSummaryModel.percentageCompleted = columnObj.optString("PercentageCompleted");
                progressReportSummaryModel.score = columnObj.optString("Score");
                progressReportSummaryModel.targetDate = columnObj.optString("TargetDate");
                progressReportSummaryModel.courseName = columnObj.optString("ContentName");
                progressReportSummaryModel.contentType = columnObj.optString("ContentType");


            }

        }

        return progressReportSummaryModel;
    }

}
