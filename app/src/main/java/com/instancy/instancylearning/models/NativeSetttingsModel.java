package com.instancy.instancylearning.models;

import android.content.Context;

import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.settings.InnerSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;

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

//    public static HashMap<String, List<String>> getData(Boolean isLogin) {
//        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
//
//        if (!isLogin) {
//            List<String> cricket = new ArrayList<String>();
//            cricket.add("Reset site URL");
//            cricket.add("Change site URL");
//            expandableListDetail.put("Preferences", cricket);
//        } else {
//            List<String> football = new ArrayList<String>();
//
//            football.add("Assigned Content Target Reminder");
//            football.add("Content Assignment");
//            football.add("New Content items Available in the Catalog fragment");
//            football.add("Content items Unassigned");
//
//            List<String> basketball = new ArrayList<String>();
//            // insidesettings_tablerow_autodownloadstatuslabel
//            //   JsonLocalization.getStringForKey("insidesettings_tablerow_applanguagenamelabel",context);
//            basketball.add("Enable Auto Download of Content");
//
//            List<String> localization = new ArrayList<String>();
//            localization.add("App Language");
//
//            expandableListDetail.put("NOTIFICATION", football);
//            expandableListDetail.put("LANGUAGE", localization);
//            expandableListDetail.put("DOWNLOAD", basketball);
//
//        }
//
//
//        return expandableListDetail;
//    }

    public static HashMap<String, List<String>> getData(Boolean isLogin, Context context) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        JsonLocalization jsonLocalization = JsonLocalization.getInstance();

        if (!isLogin) {
            List<String> cricket = new ArrayList<String>();
            cricket.add(jsonLocalization.getStringForKey(JsonLocalekeys.siteurlsetting_tablerow_resetsiteurl, context));
            cricket.add(jsonLocalization.getStringForKey(JsonLocalekeys.siteurlsetting_tablerow_changesiteurl, context));
            expandableListDetail.put(jsonLocalization.getStringForKey(JsonLocalekeys.siteurlsetting_tablesection_headingpreferences, context), cricket);
        } else {
            List<String> football = new ArrayList<String>();

            football.add(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_contenttargetreminderlabel, context));
            football.add(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_contentassignmentlabel, context));
            football.add(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_contentsavailableincataloglabel, context));
            football.add(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_contentitemsunassignedlabel, context));

            List<String> basketball = new ArrayList<String>();

            basketball.add(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_autodownloadstatuslabel, context));

            List<String> localization = new ArrayList<String>();

            localization.add(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_applanguagenamelabel, context));
//            localization.add("LANG");

            expandableListDetail.put(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablesection_headingnotification, context), football);
            expandableListDetail.put(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablesection_headinglanguage, context), localization);
            expandableListDetail.put(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablesection_headingdownload, context), basketball);

        }

        return expandableListDetail;
    }


    public static HashMap<String, List<InnerSettingsModel>> getDataModelList(Boolean isLogin, Context context) {
        HashMap<String, List<InnerSettingsModel>> expandableListDetail = new HashMap<>();
        JsonLocalization jsonLocalization = JsonLocalization.getInstance();


        List<InnerSettingsModel> football = new ArrayList<>();

        InnerSettingsModel innerSettingsModel = new InnerSettingsModel();
        innerSettingsModel.settingName = jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_contenttargetreminderlabel, context);
        innerSettingsModel.settingId = 0;
        football.add(innerSettingsModel);

        innerSettingsModel = new InnerSettingsModel();
        innerSettingsModel.settingName = jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_contentassignmentlabel, context);
        innerSettingsModel.settingId = 0;
        football.add(innerSettingsModel);

        innerSettingsModel = new InnerSettingsModel();
        innerSettingsModel.settingName = jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_contentsavailableincataloglabel, context);
        innerSettingsModel.settingId = 0;
        football.add(innerSettingsModel);

        innerSettingsModel = new InnerSettingsModel();
        innerSettingsModel.settingName = jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_contentitemsunassignedlabel, context);
        innerSettingsModel.settingId = 0;
        football.add(innerSettingsModel);

        innerSettingsModel = new InnerSettingsModel();
        List<InnerSettingsModel> basketball = new ArrayList<>();
        innerSettingsModel.settingName = jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_autodownloadstatuslabel, context);
        innerSettingsModel.settingId = 1;
        basketball.add(innerSettingsModel);

        innerSettingsModel = new InnerSettingsModel();
        List<InnerSettingsModel> localization = new ArrayList<>();
        innerSettingsModel.settingName = jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablerow_applanguagenamelabel, context);
        innerSettingsModel.settingId = 2;
        localization.add(innerSettingsModel);


        expandableListDetail.put(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablesection_headingnotification, context), football);
        expandableListDetail.put(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablesection_headinglanguage, context), localization);
        expandableListDetail.put(jsonLocalization.getStringForKey(JsonLocalekeys.insidesettings_tablesection_headingdownload, context), basketball);

        return expandableListDetail;
    }


    public static HashMap<String, List<String>> getFilterData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> filtersBy = new ArrayList<String>();
        filtersBy.add("Source ");
        filtersBy.add("Type ");
        filtersBy.add("Content Type ");
        filtersBy.add("Category ");
        filtersBy.add("Group By ");
        expandableListDetail.put("Filter By", filtersBy);

        List<String> sortBy = new ArrayList<String>();
        sortBy.add("Title");
        sortBy.add("Content Type");
        sortBy.add("Purchased Date");
        sortBy.add("Status");
        sortBy.add("Author");
        expandableListDetail.put("Sort By", sortBy);
        return expandableListDetail;
    }

    public static class FilterModel implements Serializable {

        public String id = "";
        public String name = "";
        public boolean isSelected = false;
        public boolean isSorted = false;
        public String attributeConfigId = "";
        public List<FilterInnerModel> filterInnerModels = new ArrayList<>();

    }

    public static class FilterInnerModel implements Serializable {

        public String id = "";
        public String name = "";
        public boolean isSelected = false;
    }


    public static HashMap<String, List<FilterModel>> getFilterData(JSONObject jsonObject) throws JSONException {
        HashMap<String, List<FilterModel>> expandableListDetail = new HashMap<String, List<FilterModel>>();

        if (jsonObject.has("filtersortby")) {

            JSONArray jsonArray = jsonObject.getJSONArray("filtersortby");
            if (jsonArray.length() > 0) {
                List<FilterModel> filterModelList = new ArrayList<FilterModel>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("displaytext")) {
                        FilterModel model = new FilterModel();
                        model.id = object.get("attributeconfigid").toString();
                        model.name = object.get("displaytext").toString();
                        model.attributeConfigId = object.get("attributeconfigid").toString();
                        filterModelList.add(i, model);
                    }
                }


                if (FiltersApplyModel.sortSelected > 0 && FiltersApplyModel.sortSelected < filterModelList.size()) {

                    filterModelList.get(FiltersApplyModel.sortSelected).isSelected = true;

                }

                expandableListDetail.put("Sort By", filterModelList);
            }
        }


        List<FilterModel> filterModelListG = new ArrayList<FilterModel>();
        int j = 0;
        if (jsonObject.has("filterbycategory")) {
            JSONArray jsonArray = jsonObject.getJSONArray("filterbycategory");

            if (jsonArray.length() > 0) {
                FilterModel model = new FilterModel();
                model.id = "1";
                model.name = "Category";
//                List<ContentFilterByModel> filterModelList = new ArrayList<ContentFilterByModel>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    FilterInnerModel filterInnerModel = new FilterInnerModel();
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("categoryname")) {
                        filterInnerModel.name = object.getString("categoryname");
                        filterInnerModel.id = object.getString("categoryid");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(j, model);
                j++;
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
                    if (object.has("datafieldname")) {
                        filterInnerModel.name = object.getString("datafieldname");
                        filterInnerModel.id = object.getString("categoryid");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(j, model);
                j++;
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
                filterModelListG.add(j, model);
                j++;
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
                    if (object.has("choicevalue")) {
                        filterInnerModel.name = object.getString("choicetext");
                        filterInnerModel.id = object.getString("choicevalue");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(j, model);
                j++;
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
                    if (object.has("choicevalue")) {
                        filterInnerModel.name = object.getString("choicetext");
                        filterInnerModel.id = object.getString("choicevalue");
                        model.filterInnerModels.add(i, filterInnerModel);
                    }
                }
                filterModelListG.add(j, model);


            }

        }
        expandableListDetail.put("Filter By", filterModelListG);
        return expandableListDetail;
    }


}
