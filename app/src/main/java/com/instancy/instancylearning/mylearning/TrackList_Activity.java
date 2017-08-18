package com.instancy.instancylearning.mylearning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
import com.instancy.instancylearning.asynchtask.DownloadXmlAsynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import butterknife.Bind;

import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://www.mysamplecode.com/2012/11/android-expandablelistview-search.html
 */

public class TrackList_Activity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ExpandableListView.OnChildClickListener {
    //eclipse line 8351
    ExpandableListView trackList;
    TrackListExpandableAdapter trackListExpandableAdapter;
    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = TrackList_Activity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    MyLearningModel myLearningModel;
    DatabaseHandler db;
    HashMap<String, List<MyLearningModel>> trackListHashMap;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> blockNames;
    Boolean isTraxkList = true;
    ResultListner resultListner = null;
    WebAPIClient webAPIClient;
    @Bind(R.id.lable_catalog)
    TextView frqagmentName;
    CmiSynchTask cmiSynchTask;
    DownloadXmlAsynchTask downloadXmlAsynchTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracklist_activity);
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        trackList = (ExpandableListView) findViewById(R.id.trackexpandablelist);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipetracklist);
        swipeRefreshLayout.setOnRefreshListener(this);
        svProgressHUD = new SVProgressHUD(context);
        appUserModel = AppUserModel.getInstance();
        blockNames = new ArrayList<String>();
        db = new DatabaseHandler(this);
        webAPIClient = new WebAPIClient(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
        isTraxkList = getIntent().getBooleanExtra("ISTRACKLIST", true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                myLearningModel.getCourseName() + "</font>"));
        trackListExpandableAdapter = new TrackListExpandableAdapter(this, blockNames, trackListHashMap);
        trackList.setOnChildClickListener(this);
        // setting list adapter
        trackList.setAdapter(trackListExpandableAdapter);
        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        if (isNetworkConnectionAvailable(this, -1)) {
            refreshMyLearning(false);
//            downloadXmlAsynchTask = new DownloadXmlAsynchTask(context, isTraxkList, myLearningModel, appUserModel.getSiteURL());
//            downloadXmlAsynchTask.execute();  commented for work flow rules
        } else {
            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            injectFromDbtoModel();
        }
    }

    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }
        String paramsString = "SiteURL=" + appUserModel.getSiteURL()
                + "&ContentID=" + myLearningModel.getContentID()
                + "&userid=" + appUserModel.getUserIDValue()
                + "&DelivoryMode=1&IsDownload=0";
        vollyService.getJsonObjResponseVolley("GETCALL", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetMobileContentMetaData?" + paramsString, appUserModel.getAuthHeaders());
        swipeRefreshLayout.setRefreshing(false);
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
                            } else {
                                Toast.makeText(TrackList_Activity.this, "Related content", Toast.LENGTH_SHORT).show();
                                db.injectTracklistData(false, response, myLearningModel);
                                injectFromDbtoModel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (requestType.equalsIgnoreCase("UPDATESTATUS")) {

                    if (response != null) {

                        if (resultListner != null)
                            resultListner.statusUpdateFromServer(true, response);

                    } else {

                    }

                } else {
                }
                svProgressHUD.dismiss();
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
                svProgressHUD.dismiss();
            }
        };
    }

    public void injectFromDbtoModel() {
        List<MyLearningModel> trackListModelList = new ArrayList<MyLearningModel>();
        blockNames.clear();

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
                    trackList.expandGroup(i);
            }
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
        Intent intent = getIntent();
        intent.putExtra("myLearningDetalData", myLearningModel);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Log.d("DEBUG", "onOptionsItemSelected: ");
                Intent intent = getIntent();
                intent.putExtra("myLearningDetalData", myLearningModel);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        if (isNetworkConnectionAvailable(context, -1)) {
//            refreshMyLearning(true);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

//        Toast.makeText(this, "selected click in main activity", Toast.LENGTH_SHORT).show();

        switch (v.getId()) {
//            case R.id.btntxt_download:
//                downloadTheCourse(trackListHashMap.get(blockNames.get(groupPosition)).get(childPosition), v, childPosition, groupPosition);
//
//                break;
            case R.id.imagethumb:
                GlobalMethods.launchCourseViewFromGlobalClass(trackListHashMap.get(blockNames.get(groupPosition)).get(childPosition),
                        v.getContext());
                break;
        }

        return false;
    }

    public void downloadTheCourse(MyLearningModel learningModel, View view, int position, int groupPosition) {

        String[] startPage = null;

        String localizationFolder = "";

        if (learningModel.getStartPage().contains("/")) {
            startPage = learningModel.getStartPage().split("/");
            localizationFolder = "/" + startPage[0];
        } else {
            localizationFolder = "";
        }
        String downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID() + localizationFolder;
        String downloadSourcePath = null;

        boolean success = (new File(downloadDestFolderPath)).mkdirs();

        switch (learningModel.getObjecttypeId()) {
            case "52":
                downloadSourcePath = learningModel.getSiteURL() + "/content/sitefiles/"
                        + learningModel.getSiteID() + "/usercertificates/" + learningModel.getSiteID() + "/"
                        + learningModel.getContentID() + ".pdf";
                break;
            case "11":
            case "14":
                downloadSourcePath = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getStartPage();
                break;
            case "8":
            case "9":
            case "10":
                //downloadfiles


//                downloadSourcePath = learningModel.getSiteURL() + "content/sitefiles/"
//                        + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
//
//                int statusCode = webAPIClient.checkFileFoundOrNot(downloadSourcePath);
//
//                if (statusCode != 200) {
//                    downloadSourcePath = learningModel.getSiteURL() + "content/downloadfiles/"
//                            + "/" + learningModel.getContentID() + ".zip";
//
//                } else {
                downloadSourcePath = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
//                }

                break;
            default:
                downloadSourcePath = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID()
                        + ".zip";
                break;
        }
        downloadThin(downloadSourcePath, downloadDestFolderPath, learningModel, position, groupPosition, view);
    }

    public void downloadThin(String downloadStruri, final String downloadPath, final MyLearningModel learningModel, final int position, final int groupPosition, final View view) {

        downloadStruri = downloadStruri.replace(" ", "%20");
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        Uri downloadUri = Uri.parse(downloadStruri);
        String extensionStr = "";
        switch (learningModel.getObjecttypeId()) {
            case "52":
            case "11":
            case "14":

                String[] startPage = null;
                if (learningModel.getStartPage().contains("/")) {
                    startPage = learningModel.getStartPage().split("/");
                    extensionStr = startPage[1];
                } else {
                    extensionStr = learningModel.getStartPage();
                }
                break;
            case "8":
            case "9":
            case "10":
                extensionStr = learningModel.getContentID() + ".zip";
                break;
            default:
                extensionStr = learningModel.getContentID() + ".zip";
                break;
        }

        final String finalDownloadedFilePath = downloadPath + "/" + extensionStr;

        final Uri destinationUri = Uri.parse(finalDownloadedFilePath);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new com.thin.downloadmanager.DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Log.d(TAG, "onDownloadComplete: ");

                        if (finalDownloadedFilePath.contains(".zip")) {
                            String zipFile = finalDownloadedFilePath;
                            String unzipLocation = downloadPath;
                            UnZip d = new UnZip(zipFile,
                                    unzipLocation);
                            File zipfile = new File(zipFile);
                            zipfile.delete();
                        }
                        trackListExpandableAdapter.notifyDataSetChanged();

                        if (!learningModel.getStatus().equalsIgnoreCase("Not Started")) {
                            callMetaDataService(learningModel);
                        }
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Log.d(TAG, "onDownloadFailed: " + +errorCode);
                        Toast.makeText(TrackList_Activity.this, "Download failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {

                        updateStatus(position, progress, view);
                    }
                });
        int downloadId = downloadManager.add(downloadRequest);
    }

    public void callMetaDataService(MyLearningModel learningModel) {
        String paramsString = "_studid=" + learningModel.getUserID() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + learningModel.getSiteURL() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        vollyService.getJsonObjResponseVolley("MLADP", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?" + paramsString, appUserModel.getAuthHeaders(), learningModel);

    }

    private void updateStatus(int index, int Status, View view) {
        // Update ProgressBar
        // Update Text to ColStatus

        int firstPosition = trackList.getFirstVisiblePosition() - trackList.getHeaderViewsCount(); // This is the same as child #0
        int wantedChild = index + firstPosition;
// Say, first visible position is 8, you want position 10, wantedChild will now be 2
// So that means your view is child #2 in the ViewGroup:

// Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.
        View wantedView = trackList.getChildAt(index);

        View v = trackList.getChildAt(index - trackList.getFirstVisiblePosition());
        TextView txtBtnDownload = (TextView) wantedView.findViewById(R.id.btntxt_download);
        CircleProgressBar circleProgressBar = (CircleProgressBar) wantedView.findViewById(R.id.circle_progress_track);
        circleProgressBar.setVisibility(View.VISIBLE);
        txtBtnDownload.setVisibility(View.GONE);
        circleProgressBar.setProgress(Status);
        // Enabled Button View
        if (Status >= 100) {
            txtBtnDownload.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
            txtBtnDownload.setVisibility(View.VISIBLE);
            circleProgressBar.setVisibility(View.GONE);
            txtBtnDownload.setEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COURSE_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");
                Log.d(TAG, "onActivityResult if getCourseName :" + myLearningModel.getCourseName());
                File myFile = new File(myLearningModel.getOfflinepath());

                if (!myFile.exists()) {
                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {

                        getStatusFromServer(myLearningModel);

                    } else {

                        if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {
                            int i = -1;
                            i = db.updateContentStatusInTrackList(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
                            if (i == 1) {
                                Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();
                                injectFromDbtoModel();
                            } else {
                                Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                } else {

                    cmiSynchTask = new CmiSynchTask(context);
                    cmiSynchTask.execute();
                }
            }
        }

        if (requestCode == DETAIL_CLOSE_CODE && resultCode == RESULT_OK) {
            Toast.makeText(context, "Detail Status updated!", Toast.LENGTH_SHORT).show();
            injectFromDbtoModel();
        }
    }

    public void getStatusFromServer(final MyLearningModel myLearningModel) {
        String paramsString = "userId="
                + myLearningModel.getUserID()
                + "&scoId="
                + myLearningModel.getScoId()
                + "&TrackObjectTypeID="
                + myLearningModel.getObjecttypeId()
                + "&TrackContentID="
                + myLearningModel.getContentID()
                + "&TrackScoID=" + myLearningModel.getScoId()
                + "&SiteID=" + myLearningModel.getSiteID()
                + "&OrgUnitID=" + myLearningModel.getSiteID()
                + "&isonexist=onexit";

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
                        String status = jsonObject.get("status").toString();
                        String progress = "";
                        if (jsonObject.has("progress")) {
                            progress = jsonObject.get("progress").toString();
                        }
                        i = db.updateContentStatusInTrackList(myLearningModel, status, progress);
                        if (i == 1) {

                            injectFromDbtoModel();
                            Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
    }

    private void executeWorkFlowRules(final String workflowtype) {
        try {
            // String tlcontentid = dbh
            // .getObjectString("SELECT contentid FROM DOWNLOADDATA WHERE userid ='"
            // + bundleUserId
            // + "' AND scoid='" + bundleScoId
            // + "' AND siteid='" + bundleSiteId + "'");

            File fXmlFile = null;
            fXmlFile = new File(getExternalFilesDir(null) + "/Mydownloads/"
                    + myLearningModel.getSiteID() + "/" + myLearningModel.getContentID() + "/content.xml");
            if (fXmlFile != null && fXmlFile.exists()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);

                // optional, but recommended
                // read this -
                // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                NodeList workflowList = doc.getElementsByTagName("Workflow");
                int workflowCount = workflowList.getLength();

                ArrayList<Map<String, String>> actionsMap = null;
                ArrayList<Map<String, String>> conditionsMap = null;
                ArrayList<Map<String, String>> stepsMap = null;

                stepsMap = new ArrayList<Map<String, String>>();
                actionsMap = new ArrayList<Map<String, String>>();
                conditionsMap = new ArrayList<Map<String, String>>();

                for (int wfItem = 0; wfItem < workflowCount; wfItem++) {

                    Node workflowNode = workflowList.item(wfItem);

                    if (workflowNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element workflowElement = (Element) workflowNode;
                        if (workflowElement.getAttribute("trigger").toString()
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

            } else {

            }
        } catch (Exception e) {
            Log.e("executeWorkFlowRules", "executeWorkFlowRules");
            e.printStackTrace();

        }
    }

}

