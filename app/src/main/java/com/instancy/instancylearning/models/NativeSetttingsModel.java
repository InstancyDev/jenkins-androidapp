package com.instancy.instancylearning.models;

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

public class NativeSetttingsModel {

    public static HashMap<String, List<String>> getData(Boolean isLogin) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        if (!isLogin) {
            List<String> cricket = new ArrayList<String>();
            cricket.add("Reset URL");
            cricket.add("Site URL");
            expandableListDetail.put("SITE SETTINGS", cricket);
        } else {
            List<String> football = new ArrayList<String>();

            football.add("Assigned Content Target Reminder");
            football.add("Content Assignment");
            football.add("New Content items Available in the Catalog_fragment");
            football.add("Content items Unassigned");

            List<String> basketball = new ArrayList<String>();
            basketball.add("Enable Auto Download of Content");
            expandableListDetail.put("NOTIFICATION", football);
            expandableListDetail.put("DOWNLOAD", basketball);
        }


        return expandableListDetail;
    }


    public static HashMap<String, List<String>> getFilterData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();


        List<String> basketball = new ArrayList<String>();
        basketball.add("Source ");
        basketball.add("Type ");
        basketball.add("Content Type ");
        basketball.add("Category ");
        basketball.add("Group By ");
        expandableListDetail.put("Filter By", basketball);

        List<String> football = new ArrayList<String>();
        football.add("Title");
        football.add("Content Type");
        football.add("Purchased Date");
        football.add("Status");
        football.add("Author");
        expandableListDetail.put("Sort By", football);
        return expandableListDetail;
    }

    public static class FilterModel implements Serializable {

        public String id = "";
        public String name = "";
        public boolean isSelected = false;
        public boolean isSorted = false;
        public List<FilterInnerModel> filterInnerModels = new ArrayList<>();

    }

    public static class FilterInnerModel implements Serializable {

        public String id = "";
        public String name = "";
        public boolean isSelected = false;
    }


    public static HashMap<String, List<FilterModel>> getFilterData(JSONObject jsonObject) throws JSONException {
        HashMap<String, List<FilterModel>> expandableListDetail = new HashMap<String, List<FilterModel>>();

        if (jsonObject.has("filterbysorttype")) {

            JSONArray jsonArray = jsonObject.getJSONArray("filterbysorttype");
            if (jsonArray.length() > 0) {
                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("displaytext")) {
                        FilterModel model = new FilterModel();
                        model.id = object.get("attributeconfigid").toString();
                        model.name = object.get("displaytext").toString();
                        filterModelList.add(i, model);
                    }
                }
                expandableListDetail.put("Sort By", filterModelList);
            }
        }
        List<FilterModel> filterModelListG = new ArrayList<FilterModel>();
        if (jsonObject.has("filterbycategory")) {
            JSONArray jsonArray = jsonObject.getJSONArray("filterbycategory");

            if (jsonArray.length() > 0) {
                FilterModel model = new FilterModel();
                model.id = "1";
                model.name = "Category";
//                List<FilterModel> filterModelList = new ArrayList<FilterModel>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    FilterInnerModel filterInnerModel = new FilterInnerModel();
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("categoryname")) {
                        filterInnerModel.name = object.getString("categoryname");
                        filterInnerModel.id = object.getString("categoryid");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(0, model);
//                expandableListDetail.put("Filter By", filterModelList);
            }


        }
        //
        if (jsonObject.has("filterbycontenttype")) {
            JSONArray jsonArray = jsonObject.getJSONArray("filterbycontenttype");
            if (jsonArray.length() > 0) {
                FilterModel model = new FilterModel();
                model.id = "1";
                model.name = "Content Type";
                List<FilterModel> filterModelList = new ArrayList<FilterModel>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    FilterInnerModel filterInnerModel = new FilterInnerModel();
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("categoryname")) {
                        filterInnerModel.name = object.getString("categoryname");
                        filterInnerModel.id = object.getString("categoryid");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(1, model);
//                expandableListDetail.put("Filter By", filterModelList);
            }
        }
        if (jsonObject.has("filterbygroup")) {
            JSONArray jsonArray = jsonObject.getJSONArray("filterbygroup");
            if (jsonArray.length() > 0) {
                FilterModel model = new FilterModel();
                model.id = "1";
                model.name = "Group By";
                List<FilterModel> filterModelList = new ArrayList<FilterModel>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    FilterInnerModel filterInnerModel = new FilterInnerModel();
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("name")) {
                        filterInnerModel.id = "0";
                        filterInnerModel.name = object.getString("name");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(2, model);
//                expandableListDetail.put("Filter By", filterModelList);
            }

        }

        if (jsonObject.has("filterbysource")) {
            JSONArray jsonArray = jsonObject.getJSONArray("filterbysource");
            if (jsonArray.length() > 0) {
                FilterModel model = new FilterModel();
                model.id = "1";
                model.name = "Source";
                List<FilterModel> filterModelList = new ArrayList<FilterModel>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    FilterInnerModel filterInnerModel = new FilterInnerModel();
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("categoryname")) {
                        filterInnerModel.name = object.getString("categoryname");
                        filterInnerModel.id = object.getString("categoryid");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(3, model);
//                expandableListDetail.put("Filter By", filterModelList);
            }

        }
        if (jsonObject.has("filterbytype")) {
            JSONArray jsonArray = jsonObject.getJSONArray("filterbytype");
            if (jsonArray.length() > 0) {
                FilterModel model = new FilterModel();
                model.id = "1";
                model.name = "Type";
                List<FilterModel> filterModelList = new ArrayList<FilterModel>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    FilterInnerModel filterInnerModel = new FilterInnerModel();
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("categoryname")) {
                        filterInnerModel.name = object.getString("categoryname");
                        filterInnerModel.id = object.getString("categoryid");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(4, model);

            }

        }
        expandableListDetail.put("Filter By", filterModelListG);
        return expandableListDetail;
    }

}
