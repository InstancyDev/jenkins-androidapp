package com.instancy.instancylearning.helper;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.instancy.instancylearning.models.MyLearningModel;

import org.json.JSONObject;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public interface IResult {

    public void notifySuccess(String requestType, JSONObject response);

    public void notifyError(String requestType, VolleyError error);

    public void notifySuccess(String requestType, String response);

    public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel);
}
