package com.instancy.instancylearning.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    public int contextMenuId = 0;

    public static HashMap<String, List<GlobalSearchCategoryModel>> fetchCategoriesData(String response, boolean valueChecked, SideMenusModel sidemenuModel) throws JSONException {
        HashMap<String, List<GlobalSearchCategoryModel>> expandableListDetail = new HashMap<String, List<GlobalSearchCategoryModel>>();

        JSONObject jsonObject = new JSONObject(response);


        if (jsonObject.has("SearchComponents")) {

            JSONArray jsonArray = jsonObject.getJSONArray("SearchComponents");
            if (jsonArray.length() > 0) {

                List<GlobalSearchCategoryModel> advancedFilterModelList = new ArrayList<GlobalSearchCategoryModel>();
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
                        model.contextMenuId = object.optInt("NativeCompID", 0);
                        model.chxBoxChecked = valueChecked;
                        advancedFilterModelList.add(i, model);

                    }
                }


                if (advancedFilterModelList.size() > 0) {

                    for (int j = 0; j < advancedFilterModelList.size(); j++) {


                        if (sidemenuModel.getSiteID() == advancedFilterModelList.get(j).siteID && Integer.parseInt(sidemenuModel.getComponentId()) == advancedFilterModelList.get(j).componentID && Integer.parseInt(sidemenuModel.getRepositoryId()) == advancedFilterModelList.get(j).componentInstanceID) {
                            advancedFilterModelList.remove(j);
                            break;
                        }
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

    public static HashMap<String, List<GlobalSearchCategoryModel>> filterCategoriesOnCheck(HashMap<String, List<GlobalSearchCategoryModel>> listHashMap, String groupName, int chilPosition, boolean isChecked) {

        if (listHashMap == null) {
            listHashMap = new HashMap<>();
            return listHashMap;
        }
        List<GlobalSearchCategoryModel> listGlobal = new ArrayList<>();
        for (int i = 0; i < listHashMap.size(); i++) {
            if (listHashMap.containsKey(groupName)) {
                listGlobal = listHashMap.get(groupName);
                listGlobal.get(chilPosition).chxBoxChecked = isChecked;
                listGlobal.set(chilPosition, listGlobal.get(chilPosition));
            }
        }

        listHashMap.put(groupName, listGlobal);

        return listHashMap;
    }

    public static boolean isAllCheckedBoolMethod(HashMap<String, List<GlobalSearchCategoryModel>> listHashMap) {

        boolean isAllCheckedBool = true;
        if (listHashMap == null) {
            listHashMap = new HashMap<>();
            return false;
        }

        Iterator it = listHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            List<GlobalSearchCategoryModel> listGlobal = new ArrayList<>();
            listGlobal = (List<GlobalSearchCategoryModel>) pair.getValue();
            if (listGlobal != null && listGlobal.size() > 0)
                for (int i = 0; i < listGlobal.size(); i++) {
                    if (!listGlobal.get(i).chxBoxChecked) {
                        isAllCheckedBool = false;
                        break;
                    }
                }
        }
        return isAllCheckedBool;
    }

    public static List<GLobalSearchSelectedModel> getSelectedModelList(HashMap<String, List<GlobalSearchCategoryModel>> listHashMap) {

        List<GLobalSearchSelectedModel> gLobalSearchSelectedModelList = new ArrayList<>();
        if (listHashMap == null) {
            listHashMap = new HashMap<>();
            return gLobalSearchSelectedModelList;
        }

        Iterator it = listHashMap.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            List<GlobalSearchCategoryModel> listGlobal = new ArrayList<>();
            listGlobal = (List<GlobalSearchCategoryModel>) pair.getValue();
            JSONArray arrayObj = new JSONArray();
            if (listGlobal != null && listGlobal.size() > 0)
                for (int i = 0; i < listGlobal.size(); i++) {
                    GLobalSearchSelectedModel gLobalSearchSelectedModel = new GLobalSearchSelectedModel();
                    if (listGlobal.get(i).chxBoxChecked) {
                        gLobalSearchSelectedModel.siteID = listGlobal.get(i).siteID;
                        gLobalSearchSelectedModel.componentID = listGlobal.get(i).componentID;
                        gLobalSearchSelectedModel.siteName = listGlobal.get(i).siteName;
                        gLobalSearchSelectedModel.componentName = listGlobal.get(i).displayName;
                        gLobalSearchSelectedModel.componentInstancID = listGlobal.get(i).componentInstanceID;
                        gLobalSearchSelectedModel.menuId = listGlobal.get(i).menuID;
                        gLobalSearchSelectedModel.contextMenuId = listGlobal.get(i).contextMenuId;
                        gLobalSearchSelectedModelList.add(gLobalSearchSelectedModel);
                    }
                }
        }
        return gLobalSearchSelectedModelList;
    }

    public static boolean isParentComponentExists(String response, SideMenusModel sideMenusModel) throws JSONException {
        boolean isParentExist = false;
        JSONObject jsonObject = new JSONObject(response);

        if (jsonObject.has("SearchComponents")) {

            JSONArray jsonArray = jsonObject.getJSONArray("SearchComponents");
            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    if (object.has("DisplayName")) {
                        GlobalSearchCategoryModel model = new GlobalSearchCategoryModel();
                        model.componentID = object.optInt("ComponentID");
                        model.siteID = object.optInt("SiteID");
                        model.componentInstanceID = object.optInt("ComponentInstanceID");
                        if (sideMenusModel.getSiteID() == model.siteID && Integer.parseInt(sideMenusModel.getComponentId()) == model.componentID && Integer.parseInt(sideMenusModel.getRepositoryId()) == model.componentInstanceID) {
                            isParentExist = true;
                            break;
                        }
                    }
                }
            }
        }

        return isParentExist;
    }

}
