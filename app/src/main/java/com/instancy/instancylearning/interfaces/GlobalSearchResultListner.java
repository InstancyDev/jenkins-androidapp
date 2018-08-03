package com.instancy.instancylearning.interfaces;

import com.instancy.instancylearning.models.GlobalSearchResultModel;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Upendranath on 7/26/2017 Working on InstancyLearning.
 */

public interface GlobalSearchResultListner {

    void loopCompleted(List<GlobalSearchResultModel> globalSearchResultModelList, String completed);

}
