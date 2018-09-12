package com.instancy.instancylearning.helper;

import com.android.volley.VolleyError;

import com.instancy.instancylearning.models.MyLearningModel;

import org.json.JSONObject;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public interface IResult {

    void notifySuccess(String requestType, JSONObject response);

    void notifyError(String requestType, VolleyError error);

    void notifySuccess(String requestType, String response);

    void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel);
}
