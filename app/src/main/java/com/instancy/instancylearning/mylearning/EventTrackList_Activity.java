package com.instancy.instancylearning.mylearning;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
import com.instancy.instancylearning.asynchtask.DownloadXmlAsynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.interfaces.XmlDownloadListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CMIModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.Utilities.ConvertToDate;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://www.mysamplecode.com/2012/11/android-expandablelistview-search.html
 */

public class EventTrackList_Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, XmlDownloadListner {

    ExpandableListView expandableListView;
    TrackListExpandableAdapter trackListExpandableAdapter;
    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = EventTrackList_Activity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    MyLearningModel myLearningModel;
    DatabaseHandler db;
    HashMap<String, List<MyLearningModel>> trackListHashMap;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> blockNames;
    Boolean isTraxkList = true;
    Boolean isWorkFlowCompleted = false;
    ResultListner resultListner = null;
    WebAPIClient webAPIClient;

    CmiSynchTask cmiSynchTask;
    DownloadXmlAsynchTask downloadXmlAsynchTask;
    List<MyLearningModel> trackListModelList;
    Boolean iscondition = true;
    String workFlowType = "", strlaunch;
    PreferencesManager preferencesManager;
    LinearLayout linearLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    boolean firstTimeVisible = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracklist_activity);
        strlaunch = "0";
        workFlowType = "onlaunch";
        linearLayout = (LinearLayout) findViewById(R.id.layout_linear_detail);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DatabaseHandler(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        linearLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        expandableListView = (ExpandableListView) findViewById(R.id.trackexpandablelist);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipetracklist);
        swipeRefreshLayout.setOnRefreshListener(this);
        svProgressHUD = new SVProgressHUD(context);

        blockNames = new ArrayList<String>();

        webAPIClient = new WebAPIClient(this);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
        isTraxkList = getIntent().getBooleanExtra("ISTRACKLIST", true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                myLearningModel.getCourseName() + "</font>"));
        String typeFrom = "track";

        if (isTraxkList) {

            typeFrom = "track";

        } else {

            typeFrom = "event";

        }
        trackListExpandableAdapter = new TrackListExpandableAdapter(this, this, blockNames, trackListHashMap, expandableListView, typeFrom, myLearningModel);
//        expandableListView.setOnChildClickListener(this);
        // setting list adapter
        expandableListView.setAdapter(trackListExpandableAdapter);

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                appController.setAlreadyViewdTrack(true);
                preferencesManager.setStringValue("true", StaticValues.KEY_HIDE_ANNOTATION);
            }
        });
        trackListModelList = new ArrayList<MyLearningModel>();

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        if (isNetworkConnectionAvailable(this, -1)) {
            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                refreshMyLearning(false, true);
            } else {
                refreshMyLearning(false, false);
            }

        } else {
//            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

            if (isTraxkList) {
                workFlowType = "onlaunch";
                executeWorkFlowRules(workFlowType);
            } else {

                boolean isEventRules = isEventCompleted();
                if (!isEventRules) {
                    workFlowType = "onenroll";
                    executeWorkFlowRulesForEvents(workFlowType);
                }
            }
        }
    }

    public boolean isEventCompleted() {

        boolean isCompleted = true;
        String endDate = myLearningModel.getEventendTime();
        Date strDate = ConvertToDate(endDate);

        if (new Date().after(strDate)) {
// today is after date 2
            isCompleted = true;

        } else {
            isCompleted = false;
        }

        return isCompleted;
    }

    public void executeXmlWorkFlowFile() {

        downloadXmlAsynchTask = new DownloadXmlAsynchTask(context, isTraxkList, myLearningModel, appUserModel.getSiteURL());
        downloadXmlAsynchTask.xmlDownloadListner = this;
        downloadXmlAsynchTask.execute();
    }

    public void refreshMyLearning(Boolean isRefreshed, boolean isEvent) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        if (isEvent) {
            String paramsString = "contentId=" + myLearningModel.getContentID()
                    + "&userId=" + myLearningModel.getUserID()
                    + "&locale=en-us&siteid=" + appUserModel.getSiteIDValue()
                    + "&parentcomponentid=1&categoryid=-1";
            vollyService.getJsonObjResponseVolley("GETCALL", appUserModel.getWebAPIUrl() + "/MobileLMS/GetMobileEventRelatedContentMetadata?" + paramsString, appUserModel.getAuthHeaders());
            swipeRefreshLayout.setRefreshing(false);

        } else {
            String paramsString = "SiteURL=" + appUserModel.getSiteURL()
                    + "&ContentID=" + myLearningModel.getContentID()
                    + "&UserID=" + appUserModel.getUserIDValue()
                    + "&DelivoryMode=1&IsDownload=0&TrackObjectTypeID=" + myLearningModel.getObjecttypeId() + "&TrackScoID=" + myLearningModel.getScoId() + "&SiteID=" + appUserModel.getSiteIDValue() + "&OrgUnitID=" + appUserModel.getSiteIDValue();

            vollyService.getJsonObjResponseVolley("GETCALL", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetMobileContentMetaData?" + paramsString, appUserModel.getAuthHeaders());
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("GETCALL")) {

                    if (response != null) {
                        try {
                            if (isTraxkList) {
                                db.injectTracklistData(true, response, myLearningModel);
                                injectFromDbtoModel();
                                callMobileGetContentTrackedData(myLearningModel);
                                executeXmlWorkFlowFile();
//                                svProgressHUD.dismiss(); for some reason
                            } else {
//                                Toast.makeText(TrackList_Activity.this, "Related content", Toast.LENGTH_SHORT).show();
                                db.injectTracklistData(false, response, myLearningModel);
                                injectFromDbtoModel();
                                executeXmlWorkFlowFile();
                                svProgressHUD.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (requestType.equalsIgnoreCase("UPDATESTATUS")) {
                    svProgressHUD.dismiss();
                    if (response != null) {

                        if (resultListner != null)
                            resultListner.statusUpdateFromServer(true, response);

                    } else {

                    }

                } else {
                }

                swipeRefreshLayout.setRefreshing(false);
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
                if (requestType.equalsIgnoreCase("MLADP")) {

                    if (response != null) {
                        try {
                            db.injectCMIDataInto(response, myLearningModel);
//                            executeWorkFlowRules("");
                            if (isTraxkList) {
                                workFlowType = "onitemChange";
//                                Log.d(TAG, "executeWorkFlowRules: workflowtype cmi update" + workFlowType);
//                                executeWorkFlowRules(workFlowType);
                                svProgressHUD.dismiss();
                            } else {
                                svProgressHUD.dismiss();
//                                workFlowType = "onattendance";
//                                executeWorkFlowRulesForEvents(workFlowType);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
//                svProgressHUD.dismiss();
            }
        };
    }

    public void injectFromDbtoModel() {
        trackListModelList = new ArrayList<MyLearningModel>();
        blockNames = new ArrayList<String>();

        if (isTraxkList) {

            blockNames = db.fetchBlockNames(myLearningModel.getScoId(), true);
            trackListModelList = db.fetchTrackListModel(myLearningModel, true);
        } else {

            blockNames = db.fetchBlockNames(myLearningModel.getScoId(), false);
            trackListModelList = db.fetchTrackListModel(myLearningModel, false);

        }
        if (trackListModelList != null) {
            Log.d(TAG, "dataLoaded: " + trackListModelList.size());
//            trackListModel.refreshList(myLearningModelsList);
            trackListHashMap = prepareHashMap(trackListModelList, blockNames);
            trackListExpandableAdapter.refreshList(blockNames, trackListHashMap);
            if (blockNames != null && blockNames.size() > 0) {
                for (int i = 0; i < blockNames.size(); i++)
                    expandableListView.expandGroup(i);
            }
//            if (!isTraxkList && trackListModelList.size() > 0) {
//
//                if (trackListModelList.get(0).getShowStatus().toLowerCase().contains("autolaunch") && firstTimeVisible) {
//
//                    if (expandableListView != null) {
//
////                        View wantedView = expandableListView.getChildAt(0 - expandableListView.getFirstVisiblePosition() + 1);
////
////                        long id = trackListExpandableAdapter.getGroupId(0);
////
////                        expandableListView.performItemClick(wantedView, 0, id);
//
//                    }
//                    firstTimeVisible = false;
//                }
//            }

        }

        if (svProgressHUD.isShowing() && isWorkFlowCompleted) {
            dismissSvProgress();
        }
    }

    public HashMap<String, List<MyLearningModel>> prepareHashMap(List<MyLearningModel> trackList, List<String> blockNamesAry) {

        HashMap<String, List<MyLearningModel>> hashMaps = new HashMap<String, List<MyLearningModel>>();

        if (blockNamesAry != null && blockNamesAry.size() > 0) {

            for (int i = 0; i < blockNamesAry.size(); i++) {
                List<MyLearningModel> trackLiss = new ArrayList<MyLearningModel>();

                for (int j = 0; j < trackList.size(); j++) {

                    if (trackList.get(j).getBlockName().equalsIgnoreCase(blockNamesAry.get(i))) {

                        trackLiss.add(trackList.get(j));

                    }

                    hashMaps.put(blockNamesAry.get(i), trackLiss);
                }
            }
        } else {

        }
        return hashMaps;
    }

    @Override
    public void onBackPressed() {
        boolean isCompleted = false;
        if (trackListModelList != null) {
            isCompleted = onTrackListClose(myLearningModel, trackListModelList);
        }
        if (isCompleted) {
            completedTheTrack();
            db.updateCMIstatus(myLearningModel, "Completed");
            myLearningModel.setStatusActual("Completed");
            myLearningModel.setStatusDisplay(getResources().getString(R.string.status_completed));
        }
//        myLearningModel.setStatusActual("waste");
        Intent intent = getIntent();
        intent.putExtra("myLearningDetalData", myLearningModel);
        setResult(RESULT_OK, intent);
        finish();


        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tracklistmenu, menu);
        MenuItem itemInfo = menu.findItem(R.id.tracklist_help);
        Drawable myIcon = getResources().getDrawable(R.drawable.help);
        itemInfo.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));


        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.crop_life))) {
            itemInfo.setVisible(true);
        } else {
            itemInfo.setVisible(false);
        }


        return true;
    }

    public boolean onTrackListClose(MyLearningModel learningModel, List<MyLearningModel> trackList) {
        boolean isTraxkListCompleted = false;

        for (int i = 0; i < trackList.size(); i++) {

            if (trackList.get(i).getStatusActual().toLowerCase().contains("completed") || trackList.get(i).getStatusActual().toLowerCase().contains("failed") || trackList.get(i).getStatusActual().toLowerCase().contains("passed")) {

                isTraxkListCompleted = true;

            } else {
                isTraxkListCompleted = false;
                break;
            }
        }

        return isTraxkListCompleted;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Log.d("DEBUG", "onOptionsItemSelected: ");
                boolean isCompleted = false;
                if (trackListModelList != null) {
                    isCompleted = onTrackListClose(myLearningModel, trackListModelList);
                }

                if (isCompleted) {
                    completedTheTrack();
                    db.updateCMIstatus(myLearningModel, "Completed");
                    myLearningModel.setStatusActual("Completed");
                    myLearningModel.setStatusDisplay(getResources().getString(R.string.status_completed));
                }
                Intent intent = getIntent();
                intent.putExtra("myLearningDetalData", myLearningModel);
                setResult(RESULT_OK, intent);
                finish();
//                boolean isCompleted = onTrackListClose(myLearningModel, trackListModelList);

//                if (isCompleted){
////                    db.updateContentStatus(myLearningModel);
//                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
            case R.id.tracklist_help:
                Log.d("DEBUG", "");
                appController.setAlreadyViewdTrack(false);
                preferencesManager.setStringValue("false", StaticValues.KEY_HIDE_ANNOTATION);
                trackListExpandableAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    public void completedTheTrack() {


        String paramsString = "ContentID="
                + myLearningModel.getContentID() + "&UserID=" + myLearningModel.getUserID()
                + "&ScoId=" + myLearningModel.getScoId() + "&SiteID=" + myLearningModel.getSiteID();

        paramsString = paramsString.replace(" ", "%20");

        vollyService.getStringResponseVolley("COMPLETESTATUS", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileSetStatusCompleted?" + paramsString, appUserModel.getAuthHeaders());

    }


    public static Drawable setTintDrawable(Drawable drawable, @ColorInt int color) {
        drawable.clearColorFilter();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.invalidateSelf();
        Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapDrawable, color);
        return wrapDrawable;
    }

    @Override
    public void onRefresh() {
        if (isNetworkConnectionAvailable(context, -1)) {
//            refreshPeopleListing(true);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }

//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//
//        Toast.makeText(this, "selected click in main activity", Toast.LENGTH_SHORT).show();
//
//        switch (v.getId()) {
////            case R.id.btntxt_download:
////                downloadTheCourse(trackListHashMap.get(blockNames.get(groupPosition)).get(childPosition), v, childPosition, groupPosition);
////
////                break;
//            case R.id.imagethumb:
//                GlobalMethods.launchCourseViewFromGlobalClass(trackListHashMap.get(blockNames.get(groupPosition)).get(childPosition),
//                        v.getContext());
//                break;
//        }
//
//        return false;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COURSE_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                MyLearningModel myLearningModelLocal = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");
                Log.d(TAG, "onActivityResult if getCourseName :" + myLearningModelLocal.getCourseName());
                File myFile = new File(myLearningModelLocal.getOfflinepath());

                if (!myFile.exists()) {
                    if (myLearningModelLocal.getObjecttypeId().equalsIgnoreCase("8") || myLearningModelLocal.getObjecttypeId().equalsIgnoreCase("9") || myLearningModelLocal.getObjecttypeId().equalsIgnoreCase("10")) {

                        updateTrackListViewBookMark(myLearningModelLocal);
                        getStatusFromServer(myLearningModelLocal);
//                        executeWorkFlowRules("onitemChange");
                    } else {

                        if (myLearningModelLocal.getStatusActual().equalsIgnoreCase("Not Started")) {
                            int i = -1;
                            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                                i = db.updateContentStatusInTrackList(myLearningModelLocal, getResources().getString(R.string.metadata_status_progress), "50", true);

                            } else {
                                i = db.updateContentStatusInTrackList(myLearningModelLocal, getResources().getString(R.string.metadata_status_progress), "50", false);
                            }
                            if (i == 1) {
                                injectFromDbtoModel();
//                                Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                } else {

                    if (myLearningModelLocal.getStatusActual().equalsIgnoreCase("Not Started")) {
                        int i = -1;

                        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                            i = db.updateContentStatusInTrackList(myLearningModelLocal, getResources().getString(R.string.metadata_status_progress), "50", true);

                        } else {
                            i = db.updateContentStatusInTrackList(myLearningModelLocal, getResources().getString(R.string.metadata_status_progress), "50", false);
                        }
                    }
//               remove if not required
                    injectFromDbtoModel();

                    if (isTraxkList) {
                        workFlowType = "onitemChange";
                        Log.d(TAG, "executeWorkFlowRules: workflowtype activityresult" + workFlowType);
                        executeWorkFlowRules(workFlowType);
                    } else {

//                        workFlowType = "onattendance";
//                        executeWorkFlowRulesForEvents(workFlowType);
                    }


//                    injectFromDbtoModel();

                    if (isNetworkConnectionAvailable(context, -1)) {
                        cmiSynchTask = new CmiSynchTask(context);
                        cmiSynchTask.execute();
                    }

                }
            }
        }

        if (requestCode == DETAIL_CLOSE_CODE && resultCode == RESULT_OK) {
//            Toast.makeText(context, "Detail Status updated!", Toast.LENGTH_SHORT).show();
//            injectFromDbtoModel();
        }
    }


    public void updateTrackListViewBookMark(MyLearningModel learningModel) {

        String paramsString = "?UserID=" + appUserModel.getUserIDValue() + "&ScoID=" + learningModel.getScoId();

        vollyService.getStringResponseVolley("BMARK", appUserModel.getWebAPIUrl() + "/MobileLMS/UpdateTrackListViewBookMark" + paramsString, appUserModel.getAuthHeaders());
    }

    public void getStatusFromServer(final MyLearningModel myLearningModelLocal) {
        String paramsString = "userId="
                + myLearningModelLocal.getUserID()
                + "&scoId="
                + myLearningModelLocal.getScoId()
                + "&TrackObjectTypeID="
                + "10"
                + "&TrackContentID="
                + myLearningModelLocal.getTrackOrRelatedContentID()
                + "&TrackScoID=" + myLearningModelLocal.getTrackScoid()
                + "&SiteID=" + myLearningModelLocal.getSiteID()
                + "&OrgUnitID=" + myLearningModelLocal.getSiteID();

        vollyService.getJsonObjResponseVolley("UPDATESTATUS", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentStatus?" + paramsString, appUserModel.getAuthHeaders());

        resultListner = new ResultListner() {
            @Override
            public void statusUpdateFromServer(boolean serverUpdated, JSONObject result) {
                int i = -1;
                Log.d(TAG, "statusUpdateFromServer JSONObject :" + result);
                JSONArray jsonArray = null;

                try {
                    if (result.has("contentstatus")) {
                        jsonArray = result.getJSONArray("contentstatus");
                    }
                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String status = "";
                        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.app_esperanza))) {

                            status = jsonObject.optString("Name").trim();
                        } else {

                            status = jsonObject.optString("status").trim();
                        }// esperanza call

                        String progress = "";
                        if (jsonObject.has("progress")) {
                            progress = jsonObject.get("progress").toString();
                        }

                        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                            i = db.updateContentStatusInTrackList(myLearningModelLocal, status, progress, true);

                        } else {
                            i = db.updateContentStatusInTrackList(myLearningModelLocal, status, progress, false);
                        }
                        if (i == 1) {
                            injectFromDbtoModel();
//                            Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();


                        } else {

//                            Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }

                        if (isTraxkList) {
                            workFlowType = "onitemChange";
                            Log.d(TAG, "executeWorkFlowRules: workflowtype statusupdate" + workFlowType);
                            executeWorkFlowRules(workFlowType);
                        } else {

//                                workFlowType = "onattendance";
//                                executeWorkFlowRulesForEvents(workFlowType);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        ;
    }

    public void dismissSvProgress() {

        new CountDownTimer(400, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                svProgressHUD.dismissImmediately();
            }
        }.start();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isTraxkList) {
            try {
                if (strlaunch.equals("0")) {
                    strlaunch = "1";
                } else if (strlaunch.equals("1")) {
                    injectFromDbtoModel();
                    Log.d(TAG, "executeWorkFlowRules: workflowtype onresume" + workFlowType);
                    workFlowType = "onitemChange";
                    executeWorkFlowRules(workFlowType);

                }
            } catch (Exception ex) {
                Log.d("Onresume", ex.getMessage());
            }
        } else {
            try {

                if (strlaunch.equals("0")) {
                    strlaunch = "1";
                } else if (strlaunch.equals("1")) {
                    injectFromDbtoModel();
//                    workFlowType = "onattendance";
//                    executeWorkFlowRulesForEvents(workFlowType);
                }
            } catch (Exception ex) {
                Log.d("Onresume", ex.getMessage());
            }

        }


    }

    public void executeWorkFlowRules(final String workflowtype) {
        Log.d(TAG, "executeWorkFlowRules: workflowtype " + workflowtype);

        try {

            File fXmlFile = new File(getExternalFilesDir(null) + "/Mydownloads/Contentdownloads/" + myLearningModel.getContentID() + "/content.xml");

//            String fileXmls = getExternalFilesDir(null) + "/Mydownloads/Contentdownloads/" + myLearningModel.getContentID() + "/content.xml";
            if (fXmlFile.exists()) {

                if (!svProgressHUD.isShowing()) {

                    isWorkFlowCompleted = false;
                    svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
                }


                DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);

                doc.getDocumentElement().normalize();
                NodeList workflowList = doc.getElementsByTagName("Workflow");
                int workflowCount = workflowList.getLength();

                ArrayList<Map<String, String>> actionsMap = new ArrayList<Map<String, String>>();

                ArrayList<Map<String, String>> conditionsMap = new ArrayList<Map<String, String>>();

                ArrayList<Map<String, String>> stepsMap = new ArrayList<Map<String, String>>();

                for (int wfItem = 0; wfItem < workflowCount; wfItem++) {

                    Node workflowNode = workflowList.item(wfItem);

                    if (workflowNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element workflowElement = (Element) workflowNode;

                        if (workflowElement.getAttribute("trigger")
                                .equals(workflowtype)) {

                            NodeList stepList = workflowElement
                                    .getElementsByTagName("Step");

                            int stepsCount = stepList.getLength();
                            for (int stepItem = 0; stepItem < stepsCount; stepItem++) {

                                Node stepNode = stepList.item(stepItem);

                                if (stepNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element stepElement = (Element) stepNode;

                                    Map<String, String> stepmap = new HashMap<String, String>();
                                    String stepId = stepElement
                                            .getAttribute("id");
                                    stepmap.put("stepid", stepId);
                                    stepmap.put("timedelay", stepElement
                                            .getAttribute("timedelay"));
                                    stepmap.put("ruleid", workflowElement
                                            .getAttribute("ruleid"));

                                    stepsMap.add(stepmap);
                                    NodeList conditionList = stepElement
                                            .getElementsByTagName("Condition");
                                    NodeList actionList = stepElement
                                            .getElementsByTagName("Action");

                                    int conditionsCount = conditionList
                                            .getLength();
                                    int actionCount = actionList.getLength();

                                    for (int conditionItem = 0; conditionItem < conditionsCount; conditionItem++) {

                                        Node conditionNode = conditionList
                                                .item(conditionItem);

                                        if (stepNode.getNodeType() == Node.ELEMENT_NODE) {

                                            Element conditionElement = (Element) conditionNode;
                                            Map<String, String> conditionmap = new HashMap<String, String>();
                                            conditionmap
                                                    .put("conditionid",
                                                            conditionElement
                                                                    .getAttribute("conditionid"));
                                            String conditionType = conditionElement
                                                    .getAttribute("type");
                                            conditionmap.put("conditiontype",
                                                    conditionType);
                                            conditionmap
                                                    .put("conditionitemid",
                                                            conditionElement
                                                                    .getAttribute("itemid"));
                                            conditionmap
                                                    .put("conditionoperator",
                                                            conditionElement
                                                                    .getAttribute("operator"));
                                            conditionmap
                                                    .put("conditioncoperator",
                                                            conditionElement
                                                                    .getAttribute("coperator"));
                                            String conditionResult = conditionElement
                                                    .getAttribute("result");
                                            if (conditionType.equals("status")) {

                                                switch (conditionResult
                                                        .toLowerCase()) {
                                                    case "not attempted":
                                                        conditionResult = "not started";
                                                        break;
                                                    case "incomplete":
                                                    case "in progress":
                                                        conditionResult = "in progress";
                                                        break;
                                                    case "passed":
                                                        conditionResult = "completed (passed)";
                                                        break;
                                                    case "failed":
                                                        conditionResult = "completed (failed)";
                                                        break;
                                                    case "completed":
                                                        conditionResult = "completed";
                                                        break;
                                                    case "grade":
                                                        conditionResult = "pending review";
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }

                                            conditionmap.put("conditionresult",
                                                    conditionResult);
                                            conditionmap.put("conditionstepid",
                                                    stepId);
                                            conditionsMap.add(conditionmap);

                                        }
                                    }
                                    // conditionsDic.put(stepId, conditionsMap);

                                    for (int actionItem = 0; actionItem < actionCount; actionItem++) {

                                        Node actionNode = actionList
                                                .item(actionItem);

                                        if (stepNode.getNodeType() == Node.ELEMENT_NODE) {

                                            Element actionElement = (Element) actionNode;
                                            Map<String, String> actionmap = new HashMap<String, String>();
                                            actionmap
                                                    .put("actionid",
                                                            actionElement
                                                                    .getAttribute("actionid"));
                                            actionmap
                                                    .put("actiontype",
                                                            actionElement
                                                                    .getAttribute("type"));
                                            actionmap
                                                    .put("actionitemid",
                                                            actionElement
                                                                    .getAttribute("itemid"));
                                            actionmap
                                                    .put("actionstatus",
                                                            actionElement
                                                                    .getAttribute("status"));


                                            actionmap.put("actionstepid",
                                                    stepId);
                                            actionsMap.add(actionmap);


                                        }
                                    }
                                    // actionsDic.put(stepId, actionsMap);
                                }
                            }
                            wfItem = workflowCount;
                        }
                    }
                }
                // here

                Log.d(TAG, "executeWorkFlowRules: actionsMap " + actionsMap);
                Log.d(TAG, "executeWorkFlowRules  conditionsMap: " + conditionsMap);
                Log.d(TAG, "executeWorkFlowRules: stepsMap " + stepsMap);


//
                int totalStepsCount = stepsMap.size();
                for (int stepIndex = 0; stepIndex < totalStepsCount; stepIndex++) {
                    Map<String, String> stemap = stepsMap.get(stepIndex);
                    int totalConditionsCount = conditionsMap.size();

                    // int coursesCount = coursenames.size();
                    int coursesCount = trackListModelList.size();
                    for (int con = 0; con < totalConditionsCount; con++) {
                        Map<String, String> conmap = conditionsMap.get(con);
                        if (conmap.get("conditionstepid").equals(
                                stemap.get("stepid"))) {
                            if (conmap.get("conditiontype").equals("score")) {
                                float workFlowScore = Float.parseFloat(conmap
                                        .get("conditionresult"));
                                if (conmap.get("conditioncoperator").equals("")) {
                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        if (isValidString(tlItem.getScore())) {
                                            float itemScore = Float
                                                    .parseFloat(tlItem
                                                            .getScore());
                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case ">=":
                                                        try {
                                                            if (itemScore >= workFlowScore) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator >=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "<=":
                                                        try {
                                                            if (itemScore <= workFlowScore) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator <=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "==":
                                                        try {
                                                            if (itemScore == workFlowScore) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator ==",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "!=":
                                                        try {
                                                            if (itemScore != workFlowScore) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator !=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        Boolean tempbool = null;
                                        if (isValidString(tlItem.getScore())) {
                                            float itemScore = Float
                                                    .parseFloat(tlItem
                                                            .getScore());
                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case ">=":
                                                        try {
                                                            if (itemScore >= workFlowScore) {
                                                                tempbool = true;
                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator >=",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;
                                                    case "<=":
                                                        try {
                                                            if (itemScore <= workFlowScore) {
                                                                tempbool = true;
                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator <=",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;
                                                    case "==":
                                                        try {
                                                            if (itemScore == workFlowScore) {
                                                                tempbool = true;
                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator ==",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;
                                                    case "!=":
                                                        try {
                                                            if (itemScore != workFlowScore) {
                                                                iscondition = true;
                                                                tempbool = true;
                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator !=",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }

                                                switch (conmap
                                                        .get("conditioncoperator")) {
                                                    case "or":
                                                        if (tempbool || iscondition) {
                                                            iscondition = true;
                                                            it = coursesCount;
                                                        } else {
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "and":
                                                        if (tempbool && iscondition) {
                                                            iscondition = true;
                                                            it = coursesCount;
                                                        } else {
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }

                            } else if (conmap.get("conditiontype").equals(
                                    "status")) {
                                if (conmap.get("conditionstepid").equals(
                                        stemap.get("stepid"))) {
                                    if (conmap.get("conditioncoperator")
                                            .equals("")) {
                                        for (int it = 0; it < coursesCount; it++) {
                                            MyLearningModel tlItem = trackListModelList
                                                    .get(it);

                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case "==":
                                                        try {
                                                            if ((tlItem.getStatusActual()
                                                                    .toLowerCase())
                                                                    .contains(conmap
                                                                            .get("conditionresult")
                                                                            .toLowerCase())) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator ==",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                        }
                                                        break;
                                                    case "!=":
                                                        try {
                                                            if (!(tlItem
                                                                    .getStatusActual()
                                                                    .toLowerCase())
                                                                    .contains(conmap
                                                                            .get("conditionresult")
                                                                            .toLowerCase())) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator !=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }
                                                it = coursesCount;
                                            }
                                        }
                                    } else {
                                        for (int it = 0; it < coursesCount; it++) {
                                            MyLearningModel tlItem = trackListModelList
                                                    .get(it);
                                            Boolean tempbool = null;
                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case "==":
                                                        try {
                                                            if ((tlItem.getStatusActual()
                                                                    .toLowerCase())
                                                                    .contains(conmap
                                                                            .get("conditionresult")
                                                                            .toLowerCase())) {
                                                                tempbool = true;

                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator ==",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;
                                                    case "!=":
                                                        try {
                                                            if (!(tlItem
                                                                    .getStatusActual()
                                                                    .toLowerCase())
                                                                    .contains(conmap
                                                                            .get("conditionresult")
                                                                            .toLowerCase())) {
                                                                tempbool = true;

                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator !=",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }

                                                switch (conmap
                                                        .get("conditioncoperator")) {
                                                    case "or":
                                                        if (tempbool || iscondition) {
                                                            iscondition = true;
                                                        } else {
                                                            iscondition = false;
                                                        }
                                                        break;
                                                    case "and":
                                                        if (tempbool && iscondition) {
                                                            iscondition = true;
                                                        } else {
                                                            iscondition = false;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }

                                                it = coursesCount;

                                            }
                                        }
                                    }
                                }
                            } else if (conmap.get("conditiontype").equals(
                                    "timeelapsed")) {
                                if (isValidString(myLearningModel.getDateAssigned())) {
                                    float workFlowTimeElapsed = Float
                                            .parseFloat(conmap
                                                    .get("conditionresult"));
                                    if (conmap.get("conditioncoperator")
                                            .equals("")) {
                                        for (int it = 0; it < coursesCount; it++) {
                                            MyLearningModel tlItem = trackListModelList
                                                    .get(it);
                                            // // calculate the TimeDiff
                                            // between DateAssigned and current
                                            // time as itemTimeElapsed

                                            SimpleDateFormat curFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                            Date assignedDate = curFormat
                                                    .parse(myLearningModel.getDateAssigned());
                                            Calendar cal = Calendar
                                                    .getInstance();

                                            long currentTimeMillis = cal
                                                    .getTimeInMillis();
                                            long assignedTimeMillis = assignedDate
                                                    .getTime();

                                            long timeDiffMillis = currentTimeMillis
                                                    - assignedTimeMillis;

                                            float itemTimeElapsed = (float) (timeDiffMillis / 3600000);

                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case ">=":
                                                        try {
                                                            if (itemTimeElapsed >= workFlowTimeElapsed) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("time cierator >=", e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "<=":
                                                        try {
                                                            if (itemTimeElapsed <= workFlowTimeElapsed) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("timeerator <=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "==":
                                                        try {
                                                            if (itemTimeElapsed == workFlowTimeElapsed) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("timeerator ==",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    } else {
                                        for (int it = 0; it < coursesCount; it++) {
                                            MyLearningModel tlItem = trackListModelList
                                                    .get(it);
                                            Boolean tempbool = null;
                                            if (isValidString(tlItem.getScore())) {
                                                float itemScore = Float
                                                        .parseFloat(tlItem
                                                                .getScore());
                                                if (conmap
                                                        .get("conditionitemid")
                                                        .equals(tlItem
                                                                .getContentID())) {
                                                    switch (conmap
                                                            .get("conditionoperator")) {
                                                        case ">=":
                                                            try {
                                                                if (itemScore >= workFlowTimeElapsed) {
                                                                    tempbool = true;
                                                                } else {
                                                                    tempbool = false;
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e("conditionoperator >=",
                                                                        e.getMessage());
                                                                tempbool = false;
                                                            }
                                                            break;
                                                        case "<=":
                                                            try {
                                                                if (itemScore <= workFlowTimeElapsed) {
                                                                    tempbool = true;
                                                                } else {
                                                                    tempbool = false;
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e("conditionoperator <=",
                                                                        e.getMessage());
                                                                tempbool = false;
                                                            }
                                                            break;
                                                        case "==":
                                                            try {
                                                                if (itemScore == workFlowTimeElapsed) {
                                                                    tempbool = true;
                                                                } else {
                                                                    tempbool = false;
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e("conditionoperator ==",
                                                                        e.getMessage());
                                                                tempbool = false;
                                                            }
                                                            break;
                                                        default:
                                                            break;
                                                    }

                                                    switch (conmap
                                                            .get("conditioncoperator")) {
                                                        case "or":
                                                            if (tempbool
                                                                    || iscondition) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                            break;
                                                        case "and":
                                                            if (tempbool
                                                                    && iscondition) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                            break;

                                                        default:
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    iscondition = false;
                                }

                            }
                        }
                    }

                    if (iscondition) {
                        int totalActionsCount = actionsMap.size();
                        for (int act = 0; act < totalActionsCount; act++) {
                            Map<String, String> actmap = actionsMap.get(act);
                            if (actmap.get("actionstepid").equals(
                                    stemap.get("stepid"))) {
                                long currentTimeMillis = System
                                        .currentTimeMillis();
                                if (actmap.get("actionitemid").equals("all")) {
                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        if (tlItem.getStatusActual().toLowerCase()
                                                .equals("not started")) {
                                            String showStatus = "";
                                            switch (actmap.get("actiontype")) {
                                                case "disabled":

                                                    showStatus = "disabled";
                                                    trackListModelList.get(it)
                                                            .setShowStatus(
                                                                    "disabled");
                                                    break;
                                                case "show":
                                                    showStatus = "show";
                                                    trackListModelList.get(it)
                                                            .setShowStatus("show");
                                                    break;
                                                case "hide":
                                                    showStatus = "hide";
                                                    trackListModelList.get(it)
                                                            .setShowStatus("hide");
                                                    break;

                                                default:
                                                    break;
                                            }

                                            MyLearningModel cmiDetails = new MyLearningModel();
                                            cmiDetails.setSiteID(tlItem
                                                    .getSiteID());
                                            cmiDetails.setUserID(tlItem
                                                    .getUserID());
                                            cmiDetails.setScoId(tlItem
                                                    .getScoId());
                                            cmiDetails
                                                    .setShowStatus(showStatus);
                                            db.updateTrackListItemShowstatus(cmiDetails);

                                            if (isValidString(stemap
                                                    .get("timedelay"))) {
                                                long lngLastTime = 0;

                                                String strLastTime = db
                                                        .getTrackTimedelay(
                                                                tlItem.getUserID(),
                                                                tlItem.getScoId(),
                                                                tlItem.getSiteID());

                                                try {
                                                    lngLastTime = Long
                                                            .parseLong(strLastTime);
                                                    cmiDetails = new MyLearningModel();
                                                    if (lngLastTime == 0) {
                                                        lngLastTime = currentTimeMillis
                                                                * (Long.parseLong(stemap
                                                                .get("timedelay")) * 3600000);
                                                    }

                                                } catch (Exception se) {
                                                    lngLastTime = currentTimeMillis
                                                            + (Long.parseLong(stemap
                                                            .get("timedelay")) * 3600000);
                                                }
                                                cmiDetails.setTimeDelay(String
                                                        .valueOf(lngLastTime));
                                                db.updateTrackTimedelay(cmiDetails);
                                                cmiDetails = new MyLearningModel();
                                                if (currentTimeMillis >= lngLastTime) {
                                                    cmiDetails
                                                            .setShowStatus("show");
                                                    trackListModelList.get(it)
                                                            .setShowStatus(
                                                                    "show");
                                                } else {
                                                    cmiDetails
                                                            .setShowStatus("disabled");
                                                    trackListModelList
                                                            .get(it)
                                                            .setShowStatus(
                                                                    "disabled-"
                                                                            + lngLastTime);
                                                }
                                                db.updateTrackListItemShowstatus(cmiDetails);
                                            }
                                        }
                                    }
                                } else if (actmap.get("actionitemid").equals(
                                        "track")) {
                                    CMIModel cmiDetails = new CMIModel();
                                    cmiDetails.set_siteId(myLearningModel.getSiteID());
                                    cmiDetails.set_userId(Integer
                                            .parseInt(myLearningModel.getUserID()));
                                    cmiDetails
                                            .set_startdate(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
                                    cmiDetails
                                            .set_datecompleted(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
                                    cmiDetails.set_scoId(Integer
                                            .parseInt(myLearningModel.getScoId()));
                                    cmiDetails.set_isupdate("false");
                                    cmiDetails.set_status("completed");
                                    cmiDetails.set_seqNum("0");
                                    cmiDetails.set_objecttypeid("10");
                                    cmiDetails.set_timespent("");
                                    cmiDetails.set_sitrurl(myLearningModel.getSiteURL());
                                    db.insertCMI(cmiDetails, true);
                                    // TODO Here need to execute
                                    // ContentCompletionWorkflowRules

                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        if (actmap.get("actionitemid").equals(
                                                tlItem.getContentID())) {
                                            switch (actmap.get("actiontype")) {
                                                case "setscore":
                                                    if (actmap.get("scoretype")
                                                            .equals("weighted")) {

                                                    } else {

                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }

                                } else if (actmap.get("actionitemid").equals("next")) {

                                } else {
                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        if (actmap.get("actionitemid").equals(
                                                tlItem.getContentID())) {

                                            if (tlItem.getStatusActual()
                                                    .toLowerCase()
                                                    .equals("not started")) {
                                                String showStatus = "";
                                                switch (actmap
                                                        .get("actiontype")) {
                                                    case "disabled":
                                                        showStatus = "disabled";
                                                        trackListModelList.get(it)
                                                                .setShowStatus(
                                                                        "disabled");
                                                        break;
                                                    case "show":
                                                        showStatus = "show";
                                                        trackListModelList.get(it)
                                                                .setShowStatus(
                                                                        "show");
                                                        break;
                                                    case "hide":
                                                        showStatus = "hide";
                                                        trackListModelList.get(it)
                                                                .setShowStatus(
                                                                        "hide");
                                                        break;
                                                    default:
                                                        break;
                                                }

                                                MyLearningModel _cmiDetails = new MyLearningModel();
                                                _cmiDetails.setSiteID(tlItem
                                                        .getSiteID());
                                                _cmiDetails.setUserID(tlItem
                                                        .getUserID());
                                                _cmiDetails.setScoId(tlItem
                                                        .getScoId());
                                                _cmiDetails
                                                        .setShowStatus(showStatus);

                                                db.updateTrackListItemShowstatus(_cmiDetails);

                                                if (isValidString(stemap
                                                        .get("timedelay"))) {
                                                    long lngLastTime = 0;

                                                    String strLastTime = db
                                                            .getTrackTimedelay(
                                                                    tlItem.getUserID(),
                                                                    tlItem.getScoId(),
                                                                    tlItem.getSiteID());
                                                    try {
                                                        lngLastTime = Long
                                                                .parseLong(strLastTime);
                                                        if (lngLastTime == 0) {
                                                            lngLastTime = currentTimeMillis
                                                                    + (Long.parseLong(stemap
                                                                    .get("timedelay")) * 3600000);
                                                        }

                                                    } catch (SQLiteException se) {
                                                        lngLastTime = currentTimeMillis
                                                                + (Long.parseLong(stemap
                                                                .get("timedelay")) * 3600000);

                                                    }
                                                    _cmiDetails = new MyLearningModel();
                                                    _cmiDetails
                                                            .setSiteID(tlItem
                                                                    .getSiteID());
                                                    _cmiDetails
                                                            .setUserID(tlItem
                                                                    .getUserID());
                                                    _cmiDetails
                                                            .setScoId(tlItem
                                                                    .getScoId());
                                                    _cmiDetails
                                                            .setTimeDelay(String
                                                                    .valueOf(lngLastTime));
                                                    db.updateTrackTimedelay(_cmiDetails);
                                                    _cmiDetails = new MyLearningModel();
                                                    _cmiDetails
                                                            .setSiteID(tlItem
                                                                    .getSiteID());
                                                    _cmiDetails
                                                            .setUserID(tlItem
                                                                    .getUserID());
                                                    _cmiDetails
                                                            .setScoId(tlItem
                                                                    .getScoId());
                                                    if (currentTimeMillis >= lngLastTime) {
                                                        _cmiDetails
                                                                .setShowStatus("show");
                                                        trackListModelList.get(it)
                                                                .setShowStatus(
                                                                        "show");
                                                    } else {
                                                        _cmiDetails
                                                                .setShowStatus("disabled");
                                                        trackListModelList
                                                                .get(it)
                                                                .setShowStatus(
                                                                        "disabled-"
                                                                                + lngLastTime);
                                                    }
                                                    db.updateTrackListItemShowstatus(_cmiDetails);
                                                }

                                            }
                                            it = coursesCount;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
//                if (svProgressHUD.isShowing()){
//                    svProgressHUD.dismissImmediately();
//                }
                isWorkFlowCompleted = true;
                if (workFlowType.equals("onlaunch")) {
                    Log.d(TAG, "executeWorkFlowRules: workflowtype onrules " + workFlowType);
                    workFlowType = "onitemChange";
                    executeWorkFlowRules(workFlowType);

                } else {
                    List<MyLearningModel> tempTrackListItems = new ArrayList<MyLearningModel>();
                    tempTrackListItems = trackListModelList;
                    trackListModelList = new ArrayList<MyLearningModel>();
                    int itemsCount = tempTrackListItems.size();
                    for (int it = 0; it < itemsCount; it++) {
                        MyLearningModel tempTLItem = tempTrackListItems.get(it);
                        if (!tempTLItem.getShowStatus().equals("hide")) {
                            trackListModelList.add(tempTLItem);
                        }
                    }
                    workFlowType = "";
                    isWorkFlowCompleted = true;
                    injectFromDbtoModel();
                }
//                if (svProgressHUD.isShowing()){
//                    svProgressHUD.dismissImmediately();
//                }

            } else {
                defaultActionOnNoWorkflowRules();
            }
        } catch (Exception e) {
            Log.e("executeWorkFlowRules", "executeWorkFlowRules");
            e.printStackTrace();
            defaultActionOnNoWorkflowRules();
        }

//        injectFromDbtoModel();
//        if (svProgressHUD.isShowing()){
//            svProgressHUD.dismissImmediately();
//        }

    }

    private void defaultActionOnNoWorkflowRules() {
//        if (workFlowType.equals("onexit")) {
//            workFlowType = "";
//            if (isNetworkConnectionAvailable(context, -1)) {
//
////                SyncData();
//            } else {
//                finish();

//            }
//
//        } else {
        workFlowType = "onitemChange";
        injectFromDbtoModel();
//        if (svProgressHUD.isShowing()){
//            svProgressHUD.dismissImmediately();
//        }

//        }
    }

    @Override
    public void completedXmlFileDownload() {

        if (isTraxkList) {

            if (!myLearningModel.getStatusActual().toLowerCase().contains("completed")) {
                workFlowType = "onlaunch";
                executeWorkFlowRules(workFlowType);
            }

        } else {
            boolean isEventRules = isEventCompleted();
            if (!isEventRules) {
                workFlowType = "onenroll";
                executeWorkFlowRulesForEvents(workFlowType);

            }
        }

    }

    public void executeWorkFlowRulesForEvents(final String workflowtype) {

        try {

            File fXmlFile = new File(getExternalFilesDir(null) + "/Mydownloads/Contentdownloads/" + myLearningModel.getContentID() + "/content.xml");

//            String fileXmls = getExternalFilesDir(null) + "/Mydownloads/Contentdownloads/" + myLearningModel.getContentID() + "/content.xml";
            if (fXmlFile.exists()) {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);

                doc.getDocumentElement().normalize();
                NodeList workflowList = doc.getElementsByTagName("Workflow");
                int workflowCount = workflowList.getLength();

                ArrayList<Map<String, String>> actionsMap = new ArrayList<Map<String, String>>();

                ArrayList<Map<String, String>> conditionsMap = new ArrayList<Map<String, String>>();

                ArrayList<Map<String, String>> stepsMap = new ArrayList<Map<String, String>>();

                for (int wfItem = 0; wfItem < workflowCount; wfItem++) {

                    Node workflowNode = workflowList.item(wfItem);

                    if (workflowNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element workflowElement = (Element) workflowNode;

                        if (workflowElement.getAttribute("trigger")
                                .equals(workflowtype)) {

                            NodeList stepList = workflowElement
                                    .getElementsByTagName("Step");

                            int stepsCount = stepList.getLength();
                            for (int stepItem = 0; stepItem < stepsCount; stepItem++) {

                                Node stepNode = stepList.item(stepItem);

                                if (stepNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element stepElement = (Element) stepNode;

                                    Map<String, String> stepmap = new HashMap<String, String>();
                                    String stepId = stepElement
                                            .getAttribute("id");
                                    stepmap.put("stepid", stepId);
                                    stepmap.put("timedelay", stepElement
                                            .getAttribute("timedelay"));
                                    stepmap.put("ruleid", workflowElement
                                            .getAttribute("ruleid"));

                                    stepsMap.add(stepmap);
                                    NodeList conditionList = stepElement
                                            .getElementsByTagName("Condition");
                                    NodeList actionList = stepElement
                                            .getElementsByTagName("Action");

                                    int conditionsCount = conditionList
                                            .getLength();
                                    int actionCount = actionList.getLength();

                                    for (int conditionItem = 0; conditionItem < conditionsCount; conditionItem++) {

                                        Node conditionNode = conditionList
                                                .item(conditionItem);

                                        if (stepNode.getNodeType() == Node.ELEMENT_NODE) {

                                            Element conditionElement = (Element) conditionNode;
                                            Map<String, String> conditionmap = new HashMap<String, String>();
                                            conditionmap
                                                    .put("conditionid",
                                                            conditionElement
                                                                    .getAttribute("conditionid"));
                                            String conditionType = conditionElement
                                                    .getAttribute("type");
                                            conditionmap.put("conditiontype",
                                                    conditionType);
                                            conditionmap
                                                    .put("conditionitemid",
                                                            conditionElement
                                                                    .getAttribute("itemid"));
                                            conditionmap
                                                    .put("conditionoperator",
                                                            conditionElement
                                                                    .getAttribute("operator"));
                                            conditionmap
                                                    .put("conditioncoperator",
                                                            conditionElement
                                                                    .getAttribute("coperator"));
                                            String conditionResult = conditionElement
                                                    .getAttribute("result");
                                            if (conditionType.equals("status")) {

                                                switch (conditionResult.toLowerCase()) {
                                                    case "not attempted":
                                                        conditionResult = "not started";
                                                        break;
                                                    case "incomplete":
                                                    case "in progress":
                                                        conditionResult = "in progress";
                                                        break;
                                                    case "passed":
                                                        conditionResult = "completed (passed)";
                                                        break;
                                                    case "failed":
                                                        conditionResult = "completed (failed)";
                                                        break;
                                                    case "completed":
                                                        conditionResult = "completed";
                                                        break;
                                                    case "grade":
                                                        conditionResult = "pending review";
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }

                                            conditionmap.put("conditionresult",
                                                    conditionResult);
                                            conditionmap.put("conditionstepid",
                                                    stepId);
                                            conditionsMap.add(conditionmap);

                                        }
                                    }
                                    // conditionsDic.put(stepId, conditionsMap);

                                    for (int actionItem = 0; actionItem < actionCount; actionItem++) {

                                        Node actionNode = actionList
                                                .item(actionItem);

                                        if (stepNode.getNodeType() == Node.ELEMENT_NODE) {

                                            Element actionElement = (Element) actionNode;
                                            Map<String, String> actionmap = new HashMap<String, String>();
                                            actionmap
                                                    .put("actionid",
                                                            actionElement
                                                                    .getAttribute("actionid"));
                                            actionmap
                                                    .put("actiontype",
                                                            actionElement
                                                                    .getAttribute("type"));
                                            actionmap
                                                    .put("actionitemid",
                                                            actionElement
                                                                    .getAttribute("itemid"));
                                            actionmap
                                                    .put("actionstatus",
                                                            actionElement
                                                                    .getAttribute("status"));


                                            actionmap.put("actionstepid",
                                                    stepId);
                                            actionsMap.add(actionmap);


                                        }
                                    }
                                    // actionsDic.put(stepId, actionsMap);
                                }
                            }
                            wfItem = workflowCount;
                        }
                    }
                }
                // here

                Log.d(TAG, "executeWorkFlowRules: actionsMap " + actionsMap);
                Log.d(TAG, "executeWorkFlowRules  conditionsMap: " + conditionsMap);
                Log.d(TAG, "executeWorkFlowRules: stepsMap " + stepsMap);

                int totalStepsCount = stepsMap.size();
                for (int stepIndex = 0; stepIndex < totalStepsCount; stepIndex++) {
                    Map<String, String> stemap = stepsMap.get(stepIndex);
                    int totalConditionsCount = conditionsMap.size();

                    // int coursesCount = coursenames.size();
                    int coursesCount = trackListModelList.size();
                    for (int con = 0; con < totalConditionsCount; con++) {
                        Map<String, String> conmap = conditionsMap.get(con);
                        if (conmap.get("conditionstepid").equals(
                                stemap.get("stepid"))) {
                            if (conmap.get("conditiontype").equals("score")) {
                                float workFlowScore = Float.parseFloat(conmap
                                        .get("conditionresult"));
                                if (conmap.get("conditioncoperator").equals("")) {
                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        if (isValidString(tlItem.getScore())) {
                                            float itemScore = Float
                                                    .parseFloat(tlItem
                                                            .getScore());
                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case ">=":
                                                        try {
                                                            if (itemScore >= workFlowScore) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator >=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "<=":
                                                        try {
                                                            if (itemScore <= workFlowScore) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator <=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "==":
                                                        try {
                                                            if (itemScore == workFlowScore) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator ==",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "!=":
                                                        try {
                                                            if (itemScore != workFlowScore) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator !=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        Boolean tempbool = null;
                                        if (isValidString(tlItem.getScore())) {
                                            float itemScore = Float
                                                    .parseFloat(tlItem
                                                            .getScore());
                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case ">=":
                                                        try {
                                                            if (itemScore >= workFlowScore) {
                                                                tempbool = true;
                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator >=",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;
                                                    case "<=":
                                                        try {
                                                            if (itemScore <= workFlowScore) {
                                                                tempbool = true;
                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator <=",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;
                                                    case "==":
                                                        try {
                                                            if (itemScore == workFlowScore) {
                                                                tempbool = true;
                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator ==",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;
                                                    case "!=":
                                                        try {
                                                            if (itemScore != workFlowScore) {
                                                                iscondition = true;
                                                                tempbool = true;
                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator !=",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }

                                                switch (conmap
                                                        .get("conditioncoperator")) {
                                                    case "or":
                                                        if (tempbool || iscondition) {
                                                            iscondition = true;
                                                            it = coursesCount;
                                                        } else {
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "and":
                                                        if (tempbool && iscondition) {
                                                            iscondition = true;
                                                            it = coursesCount;
                                                        } else {
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }

                            } else if (conmap.get("conditiontype").equals(
                                    "status")) {
                                if (conmap.get("conditionstepid").equals(
                                        stemap.get("stepid"))) {
                                    if (conmap.get("conditioncoperator")
                                            .equals("")) {
                                        for (int it = 0; it < coursesCount; it++) {
                                            MyLearningModel tlItem = trackListModelList
                                                    .get(it);

                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case "==":
                                                        try {
                                                            if ((tlItem.getStatusActual()
                                                                    .toLowerCase())
                                                                    .contains(conmap
                                                                            .get("conditionresult")
                                                                            .toLowerCase())) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator ==",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                        }
                                                        break;
                                                    case "!=":
                                                        try {
                                                            if (!(tlItem
                                                                    .getStatusActual()
                                                                    .toLowerCase())
                                                                    .contains(conmap
                                                                            .get("conditionresult")
                                                                            .toLowerCase())) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator !=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }
                                                it = coursesCount;
                                            }
                                        }
                                    } else {
                                        for (int it = 0; it < coursesCount; it++) {
                                            MyLearningModel tlItem = trackListModelList
                                                    .get(it);
                                            Boolean tempbool = null;
                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case "==":
                                                        try {
                                                            if ((tlItem.getStatusActual()
                                                                    .toLowerCase())
                                                                    .contains(conmap
                                                                            .get("conditionresult")
                                                                            .toLowerCase())) {
                                                                tempbool = true;

                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator ==",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;
                                                    case "!=":
                                                        try {
                                                            if (!(tlItem
                                                                    .getStatusActual()
                                                                    .toLowerCase())
                                                                    .contains(conmap
                                                                            .get("conditionresult")
                                                                            .toLowerCase())) {
                                                                tempbool = true;

                                                            } else {
                                                                tempbool = false;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("conditionoperator !=",
                                                                    e.getMessage());
                                                            tempbool = false;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }

                                                switch (conmap
                                                        .get("conditioncoperator")) {
                                                    case "or":
                                                        if (tempbool || iscondition) {
                                                            iscondition = true;
                                                        } else {
                                                            iscondition = false;
                                                        }
                                                        break;
                                                    case "and":
                                                        if (tempbool && iscondition) {
                                                            iscondition = true;
                                                        } else {
                                                            iscondition = false;
                                                        }
                                                        break;

                                                    default:
                                                        break;
                                                }

                                                it = coursesCount;

                                            }
                                        }
                                    }
                                }
                            } else if (conmap.get("conditiontype").equals(
                                    "timeelapsed")) {
                                if (isValidString(myLearningModel.getDateAssigned())) {
                                    float workFlowTimeElapsed = Float
                                            .parseFloat(conmap
                                                    .get("conditionresult"));
                                    if (conmap.get("conditioncoperator")
                                            .equals("")) {
                                        for (int it = 0; it < coursesCount; it++) {
                                            MyLearningModel tlItem = trackListModelList
                                                    .get(it);
                                            // // calculate the TimeDiff
                                            // between DateAssigned and current
                                            // time as itemTimeElapsed

                                            SimpleDateFormat curFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                            Date assignedDate = curFormat
                                                    .parse(myLearningModel.getDateAssigned());
                                            Calendar cal = Calendar
                                                    .getInstance();

                                            long currentTimeMillis = cal
                                                    .getTimeInMillis();
                                            long assignedTimeMillis = assignedDate
                                                    .getTime();

                                            long timeDiffMillis = currentTimeMillis
                                                    - assignedTimeMillis;

                                            float itemTimeElapsed = (float) (timeDiffMillis / 3600000);

                                            if (conmap.get("conditionitemid")
                                                    .equals(tlItem
                                                            .getContentID())) {
                                                switch (conmap
                                                        .get("conditionoperator")) {
                                                    case ">=":
                                                        try {
                                                            if (itemTimeElapsed >= workFlowTimeElapsed) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("time cierator >=", e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "<=":
                                                        try {
                                                            if (itemTimeElapsed <= workFlowTimeElapsed) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("timeerator <=",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    case "==":
                                                        try {
                                                            if (itemTimeElapsed == workFlowTimeElapsed) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                        } catch (Exception e) {
                                                            Log.e("timeerator ==",
                                                                    e.getMessage());
                                                            iscondition = false;
                                                            it = coursesCount;
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    } else {
                                        for (int it = 0; it < coursesCount; it++) {
                                            MyLearningModel tlItem = trackListModelList
                                                    .get(it);
                                            Boolean tempbool = null;
                                            if (isValidString(tlItem.getScore())) {
                                                float itemScore = Float
                                                        .parseFloat(tlItem
                                                                .getScore());
                                                if (conmap
                                                        .get("conditionitemid")
                                                        .equals(tlItem
                                                                .getContentID())) {
                                                    switch (conmap
                                                            .get("conditionoperator")) {
                                                        case ">=":
                                                            try {
                                                                if (itemScore >= workFlowTimeElapsed) {
                                                                    tempbool = true;
                                                                } else {
                                                                    tempbool = false;
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e("conditionoperator >=",
                                                                        e.getMessage());
                                                                tempbool = false;
                                                            }
                                                            break;
                                                        case "<=":
                                                            try {
                                                                if (itemScore <= workFlowTimeElapsed) {
                                                                    tempbool = true;
                                                                } else {
                                                                    tempbool = false;
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e("conditionoperator <=",
                                                                        e.getMessage());
                                                                tempbool = false;
                                                            }
                                                            break;
                                                        case "==":
                                                            try {
                                                                if (itemScore == workFlowTimeElapsed) {
                                                                    tempbool = true;
                                                                } else {
                                                                    tempbool = false;
                                                                }
                                                            } catch (Exception e) {
                                                                Log.e("conditionoperator ==",
                                                                        e.getMessage());
                                                                tempbool = false;
                                                            }
                                                            break;
                                                        default:
                                                            break;
                                                    }

                                                    switch (conmap
                                                            .get("conditioncoperator")) {
                                                        case "or":
                                                            if (tempbool
                                                                    || iscondition) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                            break;
                                                        case "and":
                                                            if (tempbool
                                                                    && iscondition) {
                                                                iscondition = true;
                                                                it = coursesCount;
                                                            } else {
                                                                iscondition = false;
                                                                it = coursesCount;
                                                            }
                                                            break;

                                                        default:
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    iscondition = false;
                                }

                            }
                        }
                    }

                    if (iscondition) {
                        int totalActionsCount = actionsMap.size();
                        for (int act = 0; act < totalActionsCount; act++) {
                            Map<String, String> actmap = actionsMap.get(act);
                            if (actmap.get("actionstepid").equals(
                                    stemap.get("stepid"))) {
                                long currentTimeMillis = System
                                        .currentTimeMillis();
                                if (actmap.get("actionitemid").equals("all")) {
                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        if (tlItem.getStatusActual().toLowerCase()
                                                .equals("not started")) {
                                            String showStatus = "";
                                            switch (actmap.get("actiontype")) {
                                                case "disabled":
                                                    showStatus = "disabled";
                                                    trackListModelList.get(it)
                                                            .setShowStatus(
                                                                    "disabled");
                                                    break;
                                                case "show":
                                                    showStatus = "show";
                                                    trackListModelList.get(it)
                                                            .setShowStatus("show");
                                                    break;
                                                case "hide":
                                                    showStatus = "hide";
                                                    trackListModelList.get(it)
                                                            .setShowStatus("hide");
                                                    break;
                                                case "autolaunch":
                                                    showStatus = "autolaunch";
                                                    trackListModelList.get(it)
                                                            .setShowStatus("autolaunch");
                                                    break;
                                                default:
                                                    break;
                                            }

                                            MyLearningModel cmiDetails = new MyLearningModel();
                                            cmiDetails.setSiteID(tlItem
                                                    .getSiteID());
                                            cmiDetails.setUserID(tlItem
                                                    .getUserID());
                                            cmiDetails.setScoId(tlItem
                                                    .getScoId());
                                            cmiDetails
                                                    .setShowStatus(showStatus);
                                            db.updateEventTrackListItemShowstatus(cmiDetails);

                                            if (isValidString(stemap
                                                    .get("timedelay"))) {
                                                long lngLastTime = 0;

                                                String strLastTime = db
                                                        .getTrackTimedelay(
                                                                tlItem.getUserID(),
                                                                tlItem.getScoId(),
                                                                tlItem.getSiteID());

                                                try {
                                                    lngLastTime = Long
                                                            .parseLong(strLastTime);
                                                    cmiDetails = new MyLearningModel();
                                                    if (lngLastTime == 0) {
                                                        lngLastTime = currentTimeMillis
                                                                * (Long.parseLong(stemap
                                                                .get("timedelay")) * 3600000);
                                                    }

                                                } catch (Exception se) {
                                                    lngLastTime = currentTimeMillis
                                                            + (Long.parseLong(stemap
                                                            .get("timedelay")) * 3600000);
                                                }
                                                cmiDetails.setTimeDelay(String
                                                        .valueOf(lngLastTime));
                                                db.updateTrackTimedelay(cmiDetails);
                                                cmiDetails = new MyLearningModel();
                                                if (currentTimeMillis >= lngLastTime) {
                                                    cmiDetails
                                                            .setShowStatus("show");
                                                    trackListModelList.get(it)
                                                            .setShowStatus(
                                                                    "show");
                                                } else {
                                                    cmiDetails
                                                            .setShowStatus("disabled");
                                                    trackListModelList
                                                            .get(it)
                                                            .setShowStatus(
                                                                    "disabled-"
                                                                            + lngLastTime);
                                                }
                                                db.updateEventTrackListItemShowstatus(cmiDetails);
                                            }
                                        }
                                    }
                                } else if (actmap.get("actionitemid").equals(
                                        "track")) {
                                    CMIModel cmiDetails = new CMIModel();
                                    cmiDetails.set_siteId(myLearningModel.getSiteID());
                                    cmiDetails.set_userId(Integer
                                            .parseInt(myLearningModel.getUserID()));
                                    cmiDetails
                                            .set_startdate(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
                                    cmiDetails
                                            .set_datecompleted(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
                                    cmiDetails.set_scoId(Integer
                                            .parseInt(myLearningModel.getScoId()));
                                    cmiDetails.set_isupdate("false");
                                    cmiDetails.set_status("completed");
                                    cmiDetails.set_seqNum("0");
                                    cmiDetails.set_objecttypeid("10");
                                    cmiDetails.set_timespent("");
                                    cmiDetails.set_sitrurl(myLearningModel.getSiteURL());
                                    db.insertCMI(cmiDetails, true);
                                    // TODO Here need to execute
                                    // ContentCompletionWorkflowRules

                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        if (actmap.get("actionitemid").equals(
                                                tlItem.getContentID())) {
                                            switch (actmap.get("actiontype")) {
                                                case "setscore":
                                                    if (actmap.get("scoretype")
                                                            .equals("weighted")) {

                                                    } else {

                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }

                                } else if (actmap.get("actionitemid").equals("next")) {

                                } else {
                                    for (int it = 0; it < coursesCount; it++) {
                                        MyLearningModel tlItem = trackListModelList
                                                .get(it);
                                        if (actmap.get("actionitemid").equals(
                                                tlItem.getContentID())) {

                                            if (tlItem.getStatusActual()
                                                    .toLowerCase()
                                                    .equals("not started")) {
                                                String showStatus = "";
                                                switch (actmap
                                                        .get("actiontype")) {
                                                    case "disabled":
                                                        showStatus = "disabled";
                                                        trackListModelList.get(it)
                                                                .setShowStatus(
                                                                        "disabled");
                                                        break;
                                                    case "show":
                                                        showStatus = "show";
                                                        trackListModelList.get(it)
                                                                .setShowStatus(
                                                                        "show");
                                                        break;
                                                    case "hide":
                                                        showStatus = "hide";
                                                        trackListModelList.get(it)
                                                                .setShowStatus(
                                                                        "hide");
                                                        break;
                                                    case "autolaunch":
                                                        showStatus = "autolaunch";
                                                        trackListModelList.get(it)
                                                                .setShowStatus("autolaunch");
                                                        break;
                                                    default:
                                                        break;
                                                }

                                                MyLearningModel _cmiDetails = new MyLearningModel();
                                                _cmiDetails.setSiteID(tlItem
                                                        .getSiteID());
                                                _cmiDetails.setUserID(tlItem
                                                        .getUserID());
                                                _cmiDetails.setScoId(tlItem
                                                        .getScoId());
                                                _cmiDetails
                                                        .setShowStatus(showStatus);

                                                db.updateEventTrackListItemShowstatus(_cmiDetails);

                                                if (isValidString(stemap
                                                        .get("timedelay"))) {
                                                    long lngLastTime = 0;

                                                    String strLastTime = db
                                                            .getTrackTimedelayEvent(
                                                                    tlItem.getUserID(),
                                                                    tlItem.getScoId(),
                                                                    tlItem.getSiteID());
                                                    try {
                                                        lngLastTime = Long
                                                                .parseLong(strLastTime);
                                                        if (lngLastTime == 0) {
                                                            lngLastTime = currentTimeMillis
                                                                    + (Long.parseLong(stemap
                                                                    .get("timedelay")) * 3600000);
                                                        }

                                                    } catch (SQLiteException se) {
                                                        lngLastTime = currentTimeMillis
                                                                + (Long.parseLong(stemap
                                                                .get("timedelay")) * 3600000);

                                                    }
                                                    _cmiDetails = new MyLearningModel();
                                                    _cmiDetails
                                                            .setSiteID(tlItem
                                                                    .getSiteID());
                                                    _cmiDetails
                                                            .setUserID(tlItem
                                                                    .getUserID());
                                                    _cmiDetails
                                                            .setScoId(tlItem
                                                                    .getScoId());
                                                    _cmiDetails
                                                            .setTimeDelay(String
                                                                    .valueOf(lngLastTime));
                                                    db.updateTrackTimedelay(_cmiDetails);
                                                    _cmiDetails = new MyLearningModel();
                                                    _cmiDetails
                                                            .setSiteID(tlItem
                                                                    .getSiteID());
                                                    _cmiDetails
                                                            .setUserID(tlItem
                                                                    .getUserID());
                                                    _cmiDetails
                                                            .setScoId(tlItem
                                                                    .getScoId());
                                                    if (currentTimeMillis >= lngLastTime) {
                                                        _cmiDetails
                                                                .setShowStatus("show");
                                                        trackListModelList.get(it)
                                                                .setShowStatus(
                                                                        "show");
                                                    } else {
                                                        _cmiDetails
                                                                .setShowStatus("disabled");
                                                        trackListModelList
                                                                .get(it)
                                                                .setShowStatus(
                                                                        "disabled-"
                                                                                + lngLastTime);
                                                    }
                                                    db.updateEventTrackListItemShowstatus(_cmiDetails);
                                                }

                                            }
                                            it = coursesCount;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

//                if (workFlowType.equals("onexit")) {
//                    workFlowType = "";
//                    if (isNetworkConnectionAvailable(context, -1)
//                            ) {
//
////                        SyncData();
//                    } else {
//                        finish();
//                    }
//                } else
                if (workFlowType.equals("onenroll")) {

//                    workFlowType = "onattendance";
//                    executeWorkFlowRulesForEvents(workFlowType);

                } else {
                    List<MyLearningModel> tempTrackListItems = new ArrayList<MyLearningModel>();
                    tempTrackListItems = trackListModelList;
                    trackListModelList = new ArrayList<MyLearningModel>();
                    int itemsCount = tempTrackListItems.size();
                    for (int it = 0; it < itemsCount; it++) {
                        MyLearningModel tempTLItem = tempTrackListItems.get(it);
                        if (!tempTLItem.getShowStatus().equals("hide")) {
                            trackListModelList.add(tempTLItem);
                        }
                    }
                    workFlowType = "";
                    injectFromDbtoModel();
                }

            } else {
                defaultActionOnNoWorkflowRules();
            }
        } catch (Exception e) {
            Log.e("executeWorkFlowRules", "executeWorkFlowRules");
            e.printStackTrace();
            defaultActionOnNoWorkflowRules();
        }
        injectFromDbtoModel();
        svProgressHUD.dismissImmediately();
    }

    public void callMobileGetContentTrackedData(MyLearningModel learningModel) {
        String paramsString = "_studid=" + learningModel.getUserID() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + learningModel.getSiteURL() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        vollyService.getJsonObjResponseVolley("MLADP", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?" + paramsString, appUserModel.getAuthHeaders(), learningModel);

    }

}

