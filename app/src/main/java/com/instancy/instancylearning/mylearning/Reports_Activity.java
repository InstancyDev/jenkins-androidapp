package com.instancy.instancylearning.mylearning;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.ReportDetail;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


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

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    ReportDetail reportDetail;
    List<ReportDetail> reportDetailList;

    @Nullable
    @BindView(R.id.layout_relative)
    RelativeLayout relativeLayout;

    @Nullable
    @BindView(R.id.reportslistview)
    ListView discussionFourmlistView;

    MyLearningModel learningModel;

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

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        learningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                learningModel.getCourseName() + "</font>"));


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

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetMobileContentMetaData?SiteURL=" + appUserModel.getSiteURL() + "&ContentID=" + learningModel.getContentID() + "&userid=" + appUserModel.getUserIDValue() + "&DelivoryMode=1&IsDownload=0&IsDownload=0";

        vollyService.getJsonObjResponseVolley("DMCD", urlString, appUserModel.getAuthHeaders());


    }

    public void getDownloadedMobileTrackingData() {

        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?_studid=" + appUserModel.getUserIDValue() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + appUserModel.getUserIDValue() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        vollyService.getJsonObjResponseVolley("DMTD", urlString, appUserModel.getAuthHeaders());

    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {


                if (requestType.equalsIgnoreCase("DMCD")) {
                    Log.d(TAG, "notifySuccess: DMCD" + response);
//                    try {
//                        db.injectDiscussionTopicsList(response, reportDetail);
//                        injectFromDbtoModel();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    getDownloadedMobileTrackingData();
                }
                if (requestType.equalsIgnoreCase("DMTD")) {

                    Log.d(TAG, "notifySuccess: DMTD" + response);
                }

                svProgressHUD.dismiss();
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


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
