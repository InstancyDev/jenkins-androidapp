package com.instancy.instancylearning.interfaces;

import org.json.JSONObject;

/**
 * Created by Upendranath on 7/26/2017 Working on InstancyLearning.
 */

public interface ResultListner {

    void statusUpdateFromServer(boolean serverUpdated, JSONObject result);

}
