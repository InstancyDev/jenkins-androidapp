package com.instancy.instancylearning.interfaces;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.MyLearningModel;

/**
 * Created by Upendranath on 8/8/2017 Working on InstancyLearning.
 */

public class LRSJavaScriptInterface {

    Context mContext;
    String launchURL;
    MyLearningModel _learningModel;
    DatabaseHandler databaseHandler;

    public LRSJavaScriptInterface(Context c, MyLearningModel learningModel) {
        mContext = c;
        _learningModel = learningModel;
        databaseHandler = new DatabaseHandler(c);
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
    public String SaveQuestionDataWithQuestionData(String quesData) {
        Log.d("SaveQuesWD", quesData);
        String status = databaseHandler.SaveQuestionDataWithQuestionDataMethod(_learningModel, quesData);
        return status;
    }

    @JavascriptInterface
    public String SaveLocationWithLocation(String location) {
        Log.d("SaveLocat", location);
        String status = databaseHandler.UpdatetScormCMI(_learningModel, "location", location);

        return location;
    }

}
