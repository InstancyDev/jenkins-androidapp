package com.instancy.instancylearning.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.CMIModel;
import com.instancy.instancylearning.models.LrsDetails;
import com.instancy.instancylearning.models.MyLearningModel;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.StringTokenizer;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Upendranath on 8/8/2017 Working on InstancyLearning.
 */

public class LRSJavaScriptInterface {

    Context mContext;
    String launchURL;
    MyLearningModel _learningModel;
    DatabaseHandler databaseHandler;
    Activity activity;
    public hideProgressListner hideProgressListner;
    String TAG = "LRS";
    boolean isDownloaded;


    public LRSJavaScriptInterface(Context c, MyLearningModel learningModel, Activity activity, hideProgressListner hideProgressListner, boolean isDownloaded) {
        mContext = c;
        _learningModel = learningModel;
        databaseHandler = new DatabaseHandler(c);
        this.activity = activity;
        this.hideProgressListner = hideProgressListner;
        this.isDownloaded = isDownloaded;
    }

    @JavascriptInterface
    public String SCORM_LMSInitialize() {
        Log.d(TAG, "SCORM_LMSInitialize: ");

        return "true";
    }


    @JavascriptInterface
    public String SCORM_LMSGetValueWithGetValue(String getvalue) {
        Log.d(TAG, "SCORM_LMSGetValueWithGetValue: " + getvalue);

        String returntring = "";
        String queryElement = "";

        if (getvalue.contains("core.lesson_mode")) {
            queryElement = "coursemode";
        } else if (getvalue.contains("lesson_status")) {
            queryElement = "status";
        } else if (getvalue.contains("lesson_location")) {
            queryElement = "location";
        } else if (getvalue.contains("suspend_data")) {
            queryElement = "suspenddata";
        } else if (getvalue.contains("score.min")) {
            queryElement = "scoremin";
        } else if (getvalue.contains("score.max")) {
            queryElement = "scoremax";
        }
        Log.d("returntring", queryElement);

        if (!queryElement.equalsIgnoreCase("")) {

            returntring = databaseHandler.checkCMIWithGivenQueryElement(queryElement, _learningModel);
        }

        return returntring;
    }


    @JavascriptInterface
    public String SCORM_LMSSetValueWithTotalValue(String totalString) {
        Log.d(TAG, "SCORM_LMSSetValueWithTotalValue: " + totalString);

        String getname = "";
        String getvalue = "";
        StringTokenizer st = new StringTokenizer(totalString, "#$&");


        totalString = totalString.replace("#$&", "=");

        String[] array = totalString.split("=", -1);
        int sizeAry = array.length;
        if (sizeAry > 1) {
            getname = array[0];
            getvalue = array[1];
        }
//        if (st.hasMoreElements()) {
//            getname = st.nextToken();
//            getvalue = st.nextToken();
//        }

        String saveValue = "";
        String queryElement = "";
        String scormExit = "true";

        if (getname.contains("cmi.core.session_time")) {
            queryElement = "timespent";
            saveValue = getvalue;

        } else if (getname.contains("cmi.core.lesson_status")) {
            queryElement = "status";
            saveValue = getvalue;

        } else if (getname.contains("cmi.suspend_data")) {
            queryElement = "suspenddata";
            saveValue = getvalue;

        } else if (getname.contains("cmi.core.lesson_location")) {
            queryElement = "location";
            saveValue = getvalue;

        } else if (getname.contains("cmi.core.score.raw")) {
            queryElement = "score";
            saveValue = getvalue;

        } else if (getname.contains("cmi.core.score.max")) {
            queryElement = "scoreMax";
            saveValue = getvalue;

        } else if (getname.contains("cmi.core.score.min")) {
            queryElement = "scoreMin";
            saveValue = getvalue;

        } else if (getname.contains("cmi.core.exit")) {
            if (getvalue.equalsIgnoreCase("")) {
                scormExit = "true";
            }
        } else {
            return "true";
        }
        if (!queryElement.equalsIgnoreCase("")) {
            CMIModel cmiDetails = new CMIModel();

            cmiDetails.set_siteId(_learningModel.getSiteID());
            cmiDetails.set_userId(Integer.parseInt(_learningModel.getUserID()));
            cmiDetails.set_scoId(Integer.parseInt(_learningModel.getScoId()));
            databaseHandler.UpdatetScormCMI(cmiDetails, queryElement, saveValue);
            scormExit = "true";
        }

        return scormExit;
    }


    @JavascriptInterface
    public String LMSGetRandomQuestionNos() {


        return "";
    }

    @JavascriptInterface
    public String LMSTrackGetValue(String value, String tracksqno) {


        return "";
    }


    // Normal
    @JavascriptInterface
    public void SaveQuestionDataWithQuestionData(String quesData) {
        Log.d("SaveQuesWD", quesData);
        String status = databaseHandler.SaveQuestionDataWithQuestionDataMethod(_learningModel, quesData, "");
    }

    @JavascriptInterface
    public void SaveLocationWithLocation(String location) {
        Log.d("SaveLocat", location);
        String status = databaseHandler.saveResponseCMI(_learningModel, "location", location);

    }

    // template view
    @JavascriptInterface
    public void SaveQuestionDataWithQuestionDataSeqID(String quesData, String seqID) {
        Log.d("SaveQuesWD", quesData);
        String status = databaseHandler.SaveQuestionDataWithQuestionDataMethod(_learningModel, quesData, seqID);
    }

    @JavascriptInterface
    public void SaveLocationWithLocationSeqID(String location, String seqID) {
        Log.d("SaveLocationWith", location);
        String status = "";
        databaseHandler.updateCMiRecordForTemplateView(_learningModel, seqID, location);
    }

    // Course close
    @JavascriptInterface
    public void OnLineCourseClose() {

        if (!isDownloaded) {
            Intent intent = activity.getIntent();
            intent.putExtra("myLearningDetalData", _learningModel);
            activity.setResult(RESULT_OK, intent);
            activity.finish();
        }
    }

    @JavascriptInterface
    public void hideNativeContentLoader() {

        if (hideProgressListner != null)
            hideProgressListner.statusUpdateFromServer();
    }

    @JavascriptInterface
    public void LMSTrackInitialize(String value, String tracksqno) {

        Log.d("SaveLocationWith", value);

    }

    //Track Template view javascriptmethods for workflow rules
    @JavascriptInterface
    public String LMSGetTrackWorkflowResultsWithTrackID(String trackId) {

        String returnTrack = databaseHandler.getTrackTemplateWorkflowResults(trackId, _learningModel);
        Log.d("LMSGetTracWithTrackID ", returnTrack);
        return returnTrack;

    }

    @JavascriptInterface
    public String LMSGetTrackAllItemsResultWithTrackID(String trackId) {

        Log.d("LMSGetTrackAllItemsckID", trackId);
        String returnTrack = databaseHandler.getTrackTemplateAllItemsResult(trackId, _learningModel);
        Log.d("LMSGetTrackAllItemsckID", returnTrack);
        return returnTrack;
    }

    @JavascriptInterface
    public void UpdateTrackWorkflowResultsWithTrackIDTrackItemIDTrackItemStateWmessageRuleIDStepID(String trackID, String trackItemId, String trackIstate, String wMessage, String ruleId, String cStepId) {

        Log.d("SaveLocationWith", trackID);
        databaseHandler.updateWorkFlowRulesInDBForTrackTemplate(trackID, trackItemId, trackIstate, wMessage, ruleId, cStepId, _learningModel.getSiteID(), _learningModel.getUserID());

    }

    @JavascriptInterface
    public String LMSGetPooledQuestionNos() {

        Log.d(TAG, "LMSGetPooledQuestionNos: ");

        return "";
    }

    public String SCORM_LMSGetValue(String getvalue, String tempsco) {

        String returntring = "";

        CMIModel cmi = new CMIModel();
        cmi = databaseHandler.getCMIDetails(_learningModel);

        if (cmi.get_location() == null)
            cmi.set_location("");
        if (cmi.get_suspenddata() == null)
            cmi.set_suspenddata("");
        if (cmi.get_status() == null)
            cmi.set_status("");
        if (cmi.get_coursemode() == null)
            cmi.set_coursemode("");

        if (getvalue.contains("cmi.core.student_id")) {
            returntring = _learningModel.getUserID();
        } else if (getvalue.contains("cmi.core.student_name")) {
            returntring = _learningModel.getUserName();
        } else if (getvalue.contains("cmi.core.lesson_status")) {
            returntring = cmi.get_status();
        } else if (getvalue.contains("cmi.core.lesson_location")) {
            returntring = cmi.get_location();
        } else if (getvalue.contains("cmi.core.total_time")) {
            returntring = cmi.get_timespent();
        } else if (getvalue.contains("cmi.suspend_data")) {
            returntring = cmi.get_suspenddata();
        } else if (getvalue.contains("cmi.core.score.raw")) {
            returntring = cmi.get_score();
        } else if (getvalue.contains("instancy.trackbookmark")) {
            returntring = cmi.get_seqNum();
        } else if (getvalue.contains("instancy.getanswer_data")) {
//            databaseHandler.objQuestinData = "";
//            databaseHandler.GetQuestionData(_learningModel.getSiteID(), _learningModel.getUserID(), tempsco);
//            returntring = databaseHandler.objQuestinData.replace("%20", "");
        } else if (getvalue.contains("instancy.suspend_data")) {
            returntring = cmi.get_suspenddata();
        } else if (getvalue.contains("instancy.lessonstatus")) {
            returntring = cmi.get_status();
        }
        // Log.d("returntring", returntring);
        return returntring;

    }

//    public String SCORM_LMSSetValue(String getname, String getvalue,
//                                    String tempsco) {
//        String tempgetname = null;
//        String tempgetvalue = getvalue;
//        Log.d("getname", getname);
//        Log.d("tempgetvalue", tempgetvalue);
//        String returntring = "true";
//
//        if (getname.contains("cmi.core.lesson_status")) {
//            tempgetname = "status";
//        } else if (getname.contains("cmi.core.lesson_location")) {
//            tempgetname = "location";
//        } else if (getname.contains("cmi.suspend_data")) {
//            tempgetname = "suspenddata";
//        } else if (getname.contains("cmi.core.session_time")) {
//            tempgetname = "timespent";
//        } else if (getname.contains("cmi.core.score.raw")) {
//            tempgetname = "score";
//        } else if (getname.contains("cmi.interaction")) {
//            int assessmentAttempt = databaseHandler.Getassessmentattempt(_learningModel,"false");
//            String[] resArray = getvalue.split("[@]");
//            if (resArray.length > 3) {
//                LearnerSessionModel stdObjResDetails = new LearnerSessionModel();
//                stdObjResDetails.setScoID(_learningModel.getScoId());
//                stdObjResDetails.setSiteID(_learningModel.getSiteID());
//                stdObjResDetails.setUserID(_learningModel.getUserID());
//
////                stdObjResDetails.set(resArray[2]);
////                stdObjResDetails.set_result(resArray[3]);
////                stdObjResDetails.set_assessmentattempt(assessmentAttempt);
////                String formattedDate = GetCurrentDateTime();
////                stdObjResDetails.set_attemptdate(formattedDate);
//
//                if (_learningModel.getObjecttypeId().equalsIgnoreCase("8")) {
//                    stdObjResDetails.set(Integer
//                            .parseInt(resArray[0])+1);
//
//                }
//                else {
//                    stdObjResDetails.set_questionid(Integer
//                            .parseInt(resArray[0]));
//                }
//
//                if (resArray.length > 4) {
//
//                    String tempOptionalNotes = resArray[4];
//                    if (tempOptionalNotes.contains("^notes^")) {
//                        tempOptionalNotes = tempOptionalNotes.replace(
//                                "^notes^", "");
//                        stdObjResDetails
//                                .set_optionalNotes(tempOptionalNotes);
//                    } else {
//                        if (resArray.length > 5) {
//                            stdObjResDetails
//                                    .set_attachfilename(resArray[4]);
//                            stdObjResDetails.set_attachfileid(resArray[5]);
//                            String strManyDirectories = bundlevalue
//                                    .substring(1,
//                                            bundlevalue.lastIndexOf("/"))
//                                    + "/Offline_Attachments/";
//                            stdObjResDetails
//                                    .set_attachedfilepath(strManyDirectories
//                                            + resArray[5]);
//                        }
//                    }
//                    if (resArray.length > 6) {//
//
//                        if (resArray[6].length() == 0
//                                && resArray[6].equals("undefined")) {
//                            stdObjResDetails.set_capturedVidFileName("");
//                            stdObjResDetails.set_capturedVidId("");
//                            stdObjResDetails.set_capturedVidFilepath("");
//                        }
//
//                        stdObjResDetails
//                                .set_capturedVidFileName(resArray[6]);
//                        stdObjResDetails.set_capturedVidId(resArray[7]);
//                        String strManyDirectories = bundlevalue.substring(
//                                1, bundlevalue.lastIndexOf("/"))
//                                + "/mediaresource/mediacapture/";
//                        stdObjResDetails
//                                .set_capturedVidFilepath(strManyDirectories
//                                        + resArray[7]);
//                    }
//                    if (resArray.length > 8) {
//
//                        if (resArray[8].length() == 0
//                                && resArray[8].equals("undefined")) {
//                            stdObjResDetails.set_capturedImgFileName("");
//                            stdObjResDetails.set_capturedImgId("");
//                            stdObjResDetails.set_capturedImgFilepath("");
//                        }
//
//                        stdObjResDetails
//                                .set_capturedImgFileName(resArray[8]);
//                        stdObjResDetails.set_capturedImgId(resArray[9]);
//                        String strManyDirectories = bundlevalue.substring(
//                                1, bundlevalue.lastIndexOf("/"))
//                                + "/mediaresource/mediacapture/";
//                        stdObjResDetails
//                                .set_capturedImgFilepath(strManyDirectories
//                                        + resArray[9]);
//                    }
//                }
//
////					dbh.insertStudentResponses(stdObjResDetails);
//                dbh.insertStudentResponses2(stdObjResDetails);
//
//            }
//            return "true";
//        } else if (getname.contains("instancy.trackbookmark")) {
//            tempgetname = "sequencenumber";
//        } else if (getname.contains("instancy.retake")) {
//            databaseHandler.retakeao(userid, siteId, tempsco);
//            return "true";
//        }
//        if (tempgetname != null) {
//            CMIModel cmiDetails = new CMIModel();
//
//            cmiDetails.set_siteId(_learningModel.getSiteID());
//            cmiDetails.set_userId(Integer.parseInt(_learningModel.getUserID()));
//            cmiDetails.set_scoId(Integer.parseInt(tempsco));
//            databaseHandler.UpdatetScormCMI(cmiDetails, tempgetname, tempgetvalue);
//            returntring = "true";
//        }
//        return returntring;
//
//    }

    // LRS Functionality started here
    @JavascriptInterface
    public void XHR_requestWithLrsUrlMethodDataAuthCallbackIgnore404(String lrs, String url, String method, String data, String auth, String callback, String ignore404, String extraHeaders, String actor) {

        LrsDetails _lrsDetails = new LrsDetails();
        _lrsDetails.lrs = lrs;
        if (url.contains("cid="))
            url = url.split("&cid")[0];
        _lrsDetails.lrsUrl = url;
        _lrsDetails.method = method;
        _lrsDetails.auth = auth;
        _lrsDetails.data = data;
        _lrsDetails.callback = callback;
        _lrsDetails.extraHeaders = extraHeaders;
        _lrsDetails.ignore404 = ignore404;
        _lrsDetails.siteId = Integer.parseInt(_learningModel.getSiteID());
        _lrsDetails.userId = Integer.parseInt(_learningModel.getUserID());
        _lrsDetails.scoId = Integer.parseInt(_learningModel.getScoId());
        _lrsDetails.isUpdated = "false";

        if (actor.equals(""))
            actor = "%7b%22mbox%22%3a%5b%22mailto%3a" + URLEncoder.encode(_learningModel.getUserName()) + "%22%5d%2c%22name%22%3a%5b%22" + URLEncoder.encode(_learningModel.getUserName()).replace("+", " ") + "%22%5d%7d";
        _lrsDetails.actor = actor;

        databaseHandler.InsertLRSStatement(_lrsDetails);

        Log.d("Data:", data);


        if (data.contains("/verbs/exited")) {
            try {
                JSONObject jsonObj = new JSONObject(data);
                // Getting JSON Array node
                JSONObject context = jsonObj.getJSONObject("context");
                JSONObject contextActivities = context.getJSONObject("contextActivities");
                JSONObject grouping = contextActivities.getJSONArray("grouping").getJSONObject(0);
                String grouptId = grouping.getString("id");
                String parentId = "";
                try {
                    JSONObject parent = contextActivities.getJSONArray("parent").getJSONObject(0);
                    parentId = parent.getString("id");
                } catch (Exception ex) {

                }
                if (parentId.equals("") || parentId.equals(grouptId)) {

                } else {

                }

            } catch (Exception jx) {
                Log.d("JSON Exception: ", jx.getMessage());
            }

        }


    }

    @JavascriptInterface
    public String XHR_GetSatate(String stateKey) {

        Log.d(TAG, "XHR_GetSatate: " + stateKey);

        return stateKey;
    }
}
