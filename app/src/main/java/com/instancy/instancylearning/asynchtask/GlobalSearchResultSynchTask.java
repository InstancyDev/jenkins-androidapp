package com.instancy.instancylearning.asynchtask;

import android.os.AsyncTask;
import android.text.Spanned;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.instancy.instancylearning.interfaces.GlobalSearchResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.GLobalSearchSelectedModel;
import com.instancy.instancylearning.models.GlobalSearchResultModel;
import com.instancy.instancylearning.models.GlobalSearchResultModelNew;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 5/22/2017.
 */

public class GlobalSearchResultSynchTask extends AsyncTask<String, Integer, JSONObject> {

    String TAG = GlobalSearchResultSynchTask.class.getSimpleName();

    private WebAPIClient wap;
    List<GLobalSearchSelectedModel> gLobalSearchSelectedModelList;
    AppUserModel appUserModel;
    String queryString;
    public GlobalSearchResultListner globalSearchResultListner;


    List<GlobalSearchResultModelNew> globalSearchResultModelNewList;

    public GlobalSearchResultSynchTask(List<GLobalSearchSelectedModel> gLobalSearchSelectedModelList, AppUserModel appUserModel, String queryString) {
        this.gLobalSearchSelectedModelList = gLobalSearchSelectedModelList;
        wap = new WebAPIClient();
        this.appUserModel = appUserModel;
        this.queryString = queryString;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        globalSearchResultModelNewList = new ArrayList<>();
        for (int i = 0; i < gLobalSearchSelectedModelList.size(); i++) {

            String paramsString = appUserModel.getWebAPIUrl() + "mobilelms/GetGlobalSearchResults?pageIndex=1&pageSize=10&searchStr=" + queryString +
                    "&source=0&type=0&fType=&fValue=&sortBy=PublishedDate&sortType=desc&keywords=&ComponentID=225&ComponentInsID=4021&UserID=" + appUserModel.getUserIDValue() +
                    "&SiteID=" + appUserModel.getSiteIDValue() +
                    "&OrgUnitID=" + appUserModel.getSiteIDValue() +
                    "&Locale="+ PreferencesManager.getInstance().getLocalizationStringValue("locale_name")+"&AuthorID=-1&groupBy=PublishedDate" +
                    "&objComponentList=" + gLobalSearchSelectedModelList.get(i).componentID + "&intComponentSiteID=" + gLobalSearchSelectedModelList.get(i).siteID;

            Log.d(TAG, "doInBackground: " + i);

            String responseStr = wap.getInputStreamForSearchResults(paramsString, appUserModel.getAuthHeaders());

//            Log.d(TAG, i + " doInBackground: " + responseStr);

            if (isValidString(responseStr)) {

                try {

                    globalSearchResultModelNewList.addAll(getArrayListGlobal(responseStr, gLobalSearchSelectedModelList.get(i)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        globalSearchResultListner.loopCompleted(globalSearchResultModelNewList, "completed");
        super.onPostExecute(jsonObject);
    }

    public List<GlobalSearchResultModelNew> getArrayListGlobal(String response, GLobalSearchSelectedModel selectedModel) throws JSONException {
        List<GlobalSearchResultModelNew> globalSearchResultModelList = new ArrayList<>();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            if (jsonObject.has("table0")) {
                JSONArray jsonTable = jsonObject.getJSONArray("table0");
                if (jsonTable != null && jsonTable.length() > 0) {
                    int desigredLength = jsonTable.length() > 4 ? 4 : jsonTable.length();
                    for (int i = 0; i < desigredLength; i++) {

                        GlobalSearchResultModelNew globalSearchResultModel = new GlobalSearchResultModelNew();
                        Log.d(TAG, "siteSettingJsonObj: inDB " + jsonTable);
                        JSONObject resultList = jsonTable.getJSONObject(i);
                        if (resultList.has("objectid")) {
                            globalSearchResultModel.objectid = resultList.getString("objectid");
                        }
                        if (resultList.has("scoid")) {
                            globalSearchResultModel.scoid = resultList.optInt("scoid", 0);
                        }
                        if (resultList.has("availableseats")) {
                            globalSearchResultModel.availableseats = resultList.optString("availableseats", "");
                        }
                        if (resultList.has("totalenrolls")) {
                            globalSearchResultModel.totalenrolls = resultList.optString("totalenrolls", "");

                        }
                        if (resultList.has("waitlistenrolls")) {
                            globalSearchResultModel.waitlistenrolls = resultList.optString("waitlistenrolls", "");
                        }
                        if (resultList.has("relatedconentcount")) {
                            globalSearchResultModel.relatedconentcount = resultList.optInt("relatedconentcount", 0);
                        }
                        if (resultList.has("siteid")) {
                            globalSearchResultModel.siteid = resultList.optInt("siteid");
                        }
                        if (resultList.has("sitename")) {
                            globalSearchResultModel.sitename = resultList.optString("sitename");
                        }
                        if (resultList.has("language")) {
                            globalSearchResultModel.language = resultList.optString("language");
                        }
                        if (resultList.has("totalratings")) {
                            globalSearchResultModel.totalratings = resultList.optInt("totalratings", 0);
                        }
                        if (resultList.has("ratingid")) {
                            globalSearchResultModel.ratingid = resultList.optInt("ratingid", 0);
                        }
                        if (resultList.has("currency")) {
                            globalSearchResultModel.currency = resultList.optString("currency", "");
                        }
                        if (resultList.has("componentid")) {
                            globalSearchResultModel.componentid = resultList.optInt("componentid", 0);
                        }
                        if (resultList.has("description")) {
                            globalSearchResultModel.description = resultList.optString("description", "");
                        }
                        if (resultList.has("contenttype")) {
                            globalSearchResultModel.contenttype = resultList.optString("contenttype", "");
                        }
                        globalSearchResultModel.iconpath = resultList.optString("iconpath", "");
                        globalSearchResultModel.iscontent = resultList.optBoolean("iscontent", false);
                        globalSearchResultModel.objecttypeid = resultList.optInt("objecttypeid", 0);
                        globalSearchResultModel.contenttypethumbnail = resultList.optString("contenttypethumbnail", "");
                        globalSearchResultModel.contentid = resultList.optString("contentid", "");
                        globalSearchResultModel.name = resultList.optString("name", "");
                        globalSearchResultModel.startpage = resultList.optString("startpage", "");
                        globalSearchResultModel.createduserid = resultList.optInt("createduserid", -1);
                        globalSearchResultModel.userID = resultList.optString("userid", "");
                        globalSearchResultModel.keywords = resultList.optString("keywords", "");
                        globalSearchResultModel.tags = resultList.optString("tags", "");
                        globalSearchResultModel.downloadable = resultList.optBoolean("downloadable", false);
                        globalSearchResultModel.publisheddate = resultList.optString("publisheddate", "");
                        globalSearchResultModel.status = resultList.optString("status", "");
                        globalSearchResultModel.cmsgroupid = resultList.optString("cmsgroupid", "");
                        globalSearchResultModel.folderid = resultList.optString("folderid", "");
                        globalSearchResultModel.checkedoutto = resultList.optString("checkedoutto", "");
                        Spanned longDesc = fromHtml(resultList.optString("longdescription", ""));
                        globalSearchResultModel.longdescription = longDesc.toString();
                        Spanned shortDesc = fromHtml(resultList.optString("shortdescription", ""));
                        globalSearchResultModel.shortdescription = shortDesc.toString();
                        globalSearchResultModel.mediatypeid = resultList.optString("mediatypeid", "");
                        globalSearchResultModel.publicationdate = resultList.optString("publicationdate", "");
                        globalSearchResultModel.activatedate = resultList.optString("activatedate", "");
                        globalSearchResultModel.expirydate = resultList.optString("expirydate", "");
                        globalSearchResultModel.thumbnailimagepath = resultList.optString("thumbnailimagepath", "");
                        globalSearchResultModel.downloadfile = resultList.optString("downloadfile", "");
                        globalSearchResultModel.saleprice = resultList.optString("saleprice", "");
                        globalSearchResultModel.listprice = resultList.optString("listprice", "");
                        globalSearchResultModel.folderpath = resultList.optString("folderpath", "");
                        globalSearchResultModel.modifieduserid = resultList.optString("modifieduserid", "");
                        globalSearchResultModel.currency = resultList.optString("currency", "");
                        globalSearchResultModel.viewtype = resultList.optInt("viewtype", 0);
                        globalSearchResultModel.active = resultList.optString("active", "");
                        globalSearchResultModel.enrollmentlimit = resultList.optString("enrollmentlimit", "");
                        globalSearchResultModel.presenterurl = resultList.optString("presenterurl", "");
                        globalSearchResultModel.participanturl = resultList.optString("participanturl", "");


                        String startDate, startDisplayDate, endDate, endDisplayDate, createdDate;

                        startDate = resultList.optString("eventstartdatetime", "");

                        endDate = resultList.optString("eventenddatetime", "");

                        startDisplayDate = resultList.optString("eventstartdatedisplay", "");

                        endDisplayDate = resultList.optString("eventenddatedisplay", "");

                        createdDate = resultList.optString("createddate", "");

                        globalSearchResultModel.eventstartdatedisplay = formatDate(startDisplayDate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                        globalSearchResultModel.eventenddatedisplay = formatDate(endDisplayDate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");



                        globalSearchResultModel.eventstartdatetime = formatDate(startDate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                        globalSearchResultModel.eventenddatetime = formatDate(endDate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                        globalSearchResultModel.createddate = formatDate(createdDate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                        globalSearchResultModel.presenterid = resultList.optString("presenterid", "");

                        globalSearchResultModel.contentstatus = resultList.optString("contentstatus", "");

                        globalSearchResultModel.location = resultList.optString("location", "");

                        globalSearchResultModel.conferencenumbers = resultList.optString("conferencenumbers", "");

                        globalSearchResultModel.directionurl = resultList.optString("directionurl", "");

                        globalSearchResultModel.starttime = resultList.optString("starttime", "");

                        globalSearchResultModel.duration = resultList.optString("duration", "");

                        globalSearchResultModel.noofusersenrolled = resultList.optInt("noofusersenrolled", 0);

                        globalSearchResultModel.noofuserscompleted = resultList.optInt("noofuserscompleted", 0);

                        globalSearchResultModel.eventtype = resultList.optString("eventtype", "");

                        globalSearchResultModel.eventkey = resultList.optString("eventkey", "");

                        globalSearchResultModel.timezone = resultList.optString("timezone", "");

                        globalSearchResultModel.membershiplevel = resultList.optInt("membershiplevel", 0);

                        globalSearchResultModel.membershipname = resultList.optString("membershipname", "");

                        globalSearchResultModel.medianame = resultList.optString("medianame", "");

                        globalSearchResultModel.typeofevent = resultList.optInt("typeofevent", 0);

                        globalSearchResultModel.googleproductid = resultList.optString("googleproductid", "");

                        globalSearchResultModel.activityid = resultList.optString("activityid", "");

                        globalSearchResultModel.jwvideokey = resultList.optString("jwvideokey", "");
                        globalSearchResultModel.cloudmediaplayerkey = resultList.optString("cloudmediaplayerkey", "");
                        globalSearchResultModel.eventresourcedisplayoption = resultList.optString("eventresourcedisplayoption", "");

                        globalSearchResultModel.authordisplayname = resultList.optString("authordisplayname", "");

                        globalSearchResultModel.presenter = resultList.optString("presenter", "");

                        globalSearchResultModel.isaddedtomylearning = resultList.optInt("isaddedtomylearning", 0);


                        globalSearchResultModel.siteurl = resultList.optString("siteurl", "");

                        globalSearchResultModel.componentName = selectedModel.componentName;
                        globalSearchResultModel.componentid = selectedModel.componentID;
                        globalSearchResultModel.componentInstanceID = selectedModel.componentInstancID;
//                        globalSearchResultModel.siteid = selectedModel.siteID;
                        globalSearchResultModel.menuID = selectedModel.menuId;
                        globalSearchResultModel.contextMenuId = selectedModel.contextMenuId;

                        globalSearchResultModel.headerName = selectedModel.componentName + " - " + selectedModel.siteName;

                        globalSearchResultModelList.add(globalSearchResultModel);
                    }
                }
            }
        }
        return globalSearchResultModelList;
    }
}
