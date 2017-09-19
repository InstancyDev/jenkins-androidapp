package com.instancy.instancylearning.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.MyLearningModel;

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
    String TAG = LRSJavaScriptInterface.class.getSimpleName();
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
    public String LMSGetRandomQuestionNos() {


        return "";
    }

    @JavascriptInterface
    public String LMSTrackGetValue(String value, String tracksqno) {


        return "";
    }

    @JavascriptInterface
    public void SaveQuestionDataWithQuestionData(String quesData) {
        Log.d("SaveQuesWD", quesData);
        String status = databaseHandler.SaveQuestionDataWithQuestionDataMethod(_learningModel, quesData);
//        return status;
    }

    @JavascriptInterface
    public void SaveLocationWithLocation(String location) {
        Log.d("SaveLocat", location);
        String status = databaseHandler.saveResponseCMI(_learningModel, "location", location);

//        return location;
    }

    @JavascriptInterface
    public void SaveQuestionDataWithQuestionDataSeqID(String quesData, String seqID) {
        Log.d("SaveQuesWD", quesData);
        String status = databaseHandler.SaveQuestionDataWithQuestionDataMethod(_learningModel, quesData);
//        return status;
    }

    @JavascriptInterface
    public void SaveLocationWithLocationSeqID(String location, String seqID) {
        Log.d("SaveLocationWith", location);
        String status = "";
        databaseHandler.updateCMiRecordForTemplateView(_learningModel, seqID, location);
//        return location;
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

    //Track Template view javascriptmethods
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
        return returnTrack;
    }

    @JavascriptInterface
    public void UpdateTrackWorkflowResultsWithTrackIDTrackItemIDTrackItemStateWmessageRuleIDStepID(String trackID, String trackItemId, String trackIstate, String wMessage, String ruleId, String cStepId) {

        Log.d("SaveLocationWith", trackID);
        databaseHandler.updateWorkFlowRulesInDBForTrackTemplate(trackID, trackItemId, trackIstate, wMessage, ruleId, cStepId, _learningModel.getSiteID(), _learningModel.getUserID());
//        return cStepId;

    }

    @JavascriptInterface
    public String LMSGetPooledQuestionNos() {

        Log.d(TAG, "LMSGetPooledQuestionNos: ");

        return "";
    }
}
