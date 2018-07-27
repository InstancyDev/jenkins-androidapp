package com.instancy.instancylearning.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class GlobalSearchCategoryModel implements Serializable {


    public int userid = -1;
    public int siteID = -1;
    public String siteurl = "";
    public String name = "Catalog";
    public String contextTitle = "Catalog";
    public boolean chxBoxChecked = false;
    public int componentID = -1;
    public int menuID = -1;
    public int componentInstanceID = -1;
    public String siteName = "";
    public String displayName = "";

    public static HashMap<String, List<GlobalSearchCategoryModel>> fetchCategoriesData(String response, boolean valueChecked) throws JSONException {
        HashMap<String, List<GlobalSearchCategoryModel>> expandableListDetail = new HashMap<String, List<GlobalSearchCategoryModel>>();

        JSONObject jsonObject = new JSONObject(response);


        if (jsonObject.has("SearchComponents")) {

            JSONArray jsonArray = jsonObject.getJSONArray("SearchComponents");
            if (jsonArray.length() > 0) {

                List<GlobalSearchCategoryModel> advancedFilterModelList = new ArrayList<GlobalSearchCategoryModel>();
                List<String> groupNames;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("DisplayName")) {
                        GlobalSearchCategoryModel model = new GlobalSearchCategoryModel();
                        model.name = object.optString("Name");
                        model.componentID = object.optInt("ComponentID");
                        model.menuID = object.optInt("MenuID");
                        model.siteID = object.optInt("SiteID");
                        model.componentInstanceID = object.optInt("ComponentInstanceID");
                        model.contextTitle = object.optString("ContextTitle");
                        model.siteName = object.optString("SiteName");
                        model.displayName = object.optString("DisplayName");
//                        model.chxBoxChecked = object.optBoolean("Check", false);
                        model.chxBoxChecked = valueChecked;
                        advancedFilterModelList.add(i, model);

                    }
                }

//                HashMap<String, List<GlobalSearchCategoryModel>> studlistGrouped =
//                        studlist.stream().collect(Collectors.groupingBy(w -> w.stud_location));

                for (GlobalSearchCategoryModel globalSearchCategoryModel : advancedFilterModelList) {
                    String siteName = globalSearchCategoryModel.siteName;
                    if (expandableListDetail.containsKey(siteName)) {
                        List<GlobalSearchCategoryModel> list = expandableListDetail.get(siteName);
                        list.add(globalSearchCategoryModel);

                    } else {
                        List<GlobalSearchCategoryModel> list = new ArrayList<GlobalSearchCategoryModel>();
                        list.add(globalSearchCategoryModel);
                        expandableListDetail.put(siteName, list);
                    }

                }

            }
        }

        return expandableListDetail;
    }

}
