package com.instancy.instancylearning.advancedfilters;


import com.instancy.instancylearning.models.NativeSetttingsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Upendranath on 5/29/2017.
 */

public class FiltersSerilization {

    public static class FilterModel implements Serializable {

        public String id = "";
        public String name = "";
        public boolean isSelected = false;
        public boolean isSorted = false;
        public String attributeConfigId = "";
        public List<NativeSetttingsModel.FilterInnerModel> filterInnerModels = new ArrayList<>();

    }

    public static class FilterInnerModel implements Serializable {

        public String id = "";
        public String name = "";
        public boolean isSelected = false;
    }

//    public static HashMap<String, List<FilterModel>> getFilterData(JSONObject jsonObject) throws JSONException {
//        HashMap<String, List<FilterModel>> expandableListDetail = new HashMap<String, List<FilterModel>>();
//
//        if (jsonObject.has("filtersortby")) {
//
//            JSONArray jsonArray = jsonObject.getJSONArray("filtersortby");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("Sort By", filterModelList);
//            }
//        }
//
//        if (jsonObject.has("filtersorttype")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filtersorttype");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("Sort Type", filterModelList);
//            }
//
//
//        }
//
//        if (jsonObject.has("filterbycategory")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbycategory");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Category", filterModelList);
//            }
//
//        }
//
//        if (jsonObject.has("filterbygroup")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbygroup");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Group", filterModelList);
//            }
//
//
//        }
//
//        if (jsonObject.has("filterbycontenttype")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbycontenttype");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Content Type", filterModelList);
//            }
//
//
//        }
//
//        if (jsonObject.has("filterbysource")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbysource");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Source", filterModelList);
//            }
//
//
//        }
//
//
//        if (jsonObject.has("filterbytype")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbytype");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Type", filterModelList);
//            }
//
//
//        }
//
//
//        if (jsonObject.has("filterbyskills")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbyskills");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Skills", filterModelList);
//            }
//
//
//        }
//
//        if (jsonObject.has("jobrolesinfo")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("jobrolesinfo");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//                expandableListDetail.put("By Job Roles", filterModelList);
//            }
//
//        }
//        return expandableListDetail;
//    }

    public static HashMap<String, List<AdvancedFilterModel>> fetchFilterData(JSONObject jsonObject) throws JSONException {
        HashMap<String, List<AdvancedFilterModel>> expandableListDetail = new HashMap<String, List<AdvancedFilterModel>>();

        if (jsonObject.has("filtersortby")) {

            JSONArray jsonArray = jsonObject.getJSONArray("filtersortby");
            if (jsonArray.length() > 0) {

                List<AdvancedFilterModel> advancedFilterModelList = new ArrayList<AdvancedFilterModel>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("displaytext")) {
                        AdvancedFilterModel model = new AdvancedFilterModel();
                        model.cellIdentifier=1;
                        model.attributeConfigID = object.optInt("attributeconfigid");
                        model.aliasName = object.optString("aliasname");
                        model.displayText = object.optString("displaytext");
                        model.showDefault = object.optInt("attributeconfigid");
                        advancedFilterModelList.add(i, model);
                    }
                }

                expandableListDetail.put("Sort By", advancedFilterModelList);
            }
        }

        if (jsonObject.has("filtersorttype")) {
            JSONArray jsonArray = jsonObject.getJSONArray("filtersorttype");
            if (jsonArray.length() > 0) {
                List<AdvancedFilterModel> advancedFilterModelList = new ArrayList<AdvancedFilterModel>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("sorttypeid")) {

                        AdvancedFilterModel model = new AdvancedFilterModel();
                        model.cellIdentifier=2;
                        model.sortTypeID = object.optString("sorttypeid");
                        model.sortTypeName = object.optString("sorttypename");

                        advancedFilterModelList.add(i, model);
                    }
                }


                expandableListDetail.put("Sort Type", advancedFilterModelList);
            }


        }

        if (jsonObject.has("filterbycategory")) {
            JSONArray jsonArray = jsonObject.getJSONArray("filterbycategory");
            if (jsonArray.length() > 0) {
                List<AdvancedFilterModel> advancedFilterModelList = new ArrayList<AdvancedFilterModel>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("categoryname")) {

                        AdvancedFilterModel model = new AdvancedFilterModel();
                        model.cellIdentifier=3;
                        model.categoryIcon = object.optString("categoryicon");
                        model.categoryID = object.optInt("categoryid");
                        model.categoryName = object.optString("categoryname");
                        model.column1 = object.optInt("column1");
                        model.contentCount = object.optInt("contentcount");
                        model.parentID = object.optInt("parentid");
                        model.subCategoryIcon = object.optString("subcategoryicon");


                        advancedFilterModelList.add(i, model);
                    }
                }
                expandableListDetail.put("By Category", advancedFilterModelList);
            }

        }

//        if (jsonObject.has("filterbygroup")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbygroup");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Group", filterModelList);
//            }
//
//
//        }
//
//        if (jsonObject.has("filterbycontenttype")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbycontenttype");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Content Type", filterModelList);
//            }
//
//
//        }
//
//        if (jsonObject.has("filterbysource")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbysource");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Source", filterModelList);
//            }
//
//
//        }
//
//
//        if (jsonObject.has("filterbytype")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbytype");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Type", filterModelList);
//            }
//
//
//        }
//
//
//        if (jsonObject.has("filterbyskills")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("filterbyskills");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//
//                expandableListDetail.put("By Skills", filterModelList);
//            }
//
//
//        }
//
//        if (jsonObject.has("jobrolesinfo")) {
//            JSONArray jsonArray = jsonObject.getJSONArray("jobrolesinfo");
//            if (jsonArray.length() > 0) {
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
//
//                expandableListDetail.put("By Job Roles", filterModelList);
//            }
//
//        }
        return expandableListDetail;
    }


}
