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
import com.instancy.instancylearning.synchtasks.WebAPIClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    List<GlobalSearchResultModel> globalSearchResultModelList;

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
        globalSearchResultModelList = new ArrayList<>();
        for (int i = 0; i < gLobalSearchSelectedModelList.size(); i++) {

            String paramsString = appUserModel.getWebAPIUrl() + "search/GetGlobalSearchResults?pageIndex=1&pageSize=10&searchStr=" + queryString +
                    "&source=0&type=0&fType=&fValue=&sortBy=PublishedDate&sortType=desc&keywords=&ComponentID=225&ComponentInsID=4021&UserID=" + appUserModel.getUserIDValue() +
                    "&SiteID=" + appUserModel.getSiteIDValue() +
                    "&OrgUnitID=" + appUserModel.getSiteIDValue() +
                    "&Locale=en-us&AuthorID=-1&groupBy=PublishedDate" +
                    "&objComponentList=" + gLobalSearchSelectedModelList.get(i).componentID + "&intComponentSiteID=" + gLobalSearchSelectedModelList.get(i).siteID;

            Log.d(TAG, "doInBackground: " + i);

            String responseStr = wap.getInputStreamForSearchResults(paramsString, appUserModel.getAuthHeaders());

            Log.d(TAG, i + " doInBackground: " + responseStr);

            if (isValidString(responseStr)) {

                try {

                    globalSearchResultModelList.addAll(getArrayListGlobal(responseStr, gLobalSearchSelectedModelList.get(i)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        globalSearchResultListner.loopCompleted(globalSearchResultModelList, "completed");
        super.onPostExecute(jsonObject);
    }

    public List<GlobalSearchResultModel> getArrayListGlobal(String response, GLobalSearchSelectedModel selectedModel) throws JSONException {
        List<GlobalSearchResultModel> globalSearchResultModelList = new ArrayList<>();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObject != null) {
            if (jsonObject.has("CourseList")) {
                JSONArray jsonTable = jsonObject.getJSONArray("CourseList");
                if (jsonTable != null && jsonTable.length() > 0) {
                    int desigredLength = jsonTable.length() > 5 ? 4 : jsonTable.length();
                    for (int i = 0; i < desigredLength; i++) {

//                    for (int i = 0; i < jsonTable.length(); i++) {
                        GlobalSearchResultModel globalSearchResultModel = new GlobalSearchResultModel();
                        Log.d(TAG, "siteSettingJsonObj: inDB " + jsonTable);
                        JSONObject resultList = jsonTable.getJSONObject(i);
                        if (resultList.has("NamePreFix")) {
                            globalSearchResultModel.namePreFix = resultList.getString("NamePreFix");
                        }
                        if (resultList.has("AddLink")) {
                            globalSearchResultModel.addLink = resultList.getString("AddLink");
                        }
                        if (resultList.has("AuthorDisplayName")) {
                            globalSearchResultModel.authorDisplayName = resultList.getString("AuthorDisplayName");
                        }
                        if (resultList.has("AuthorName")) {
                            globalSearchResultModel.authorName = resultList.getString("AuthorName");
                        }
                        if (resultList.has("AuthorWithLink")) {
                            globalSearchResultModel.authorWithLink = resultList.getString("AuthorWithLink");
                        }
                        if (resultList.has("CancelEventLink")) {
                            globalSearchResultModel.cancelEventLink = resultList.getString("CancelEventLink");
                        }
                        if (resultList.has("Categorycolor")) {
                            globalSearchResultModel.categorycolor = resultList.getString("Categorycolor");
                        }
                        if (resultList.has("ContentID")) {
                            globalSearchResultModel.contentID = resultList.getString("ContentID");
                        }
                        if (resultList.has("ContentType")) {
                            globalSearchResultModel.contentType = resultList.getString("ContentType");
                        }
                        if (resultList.has("ContentTypeId")) {
                            globalSearchResultModel.contentTypeId = resultList.getInt("ContentTypeId");
                        }
                        if (resultList.has("CreatedOn")) {
                            globalSearchResultModel.createdOn = resultList.getString("CreatedOn");
                        }
                        if (resultList.has("Currency")) {
                            globalSearchResultModel.currency = resultList.getString("Currency");
                        }
                        if (resultList.has("DestinationLink")) {
                            globalSearchResultModel.destinationLink = resultList.getString("DestinationLink");
                        }
                        if (resultList.has("SiteName")) {
                            globalSearchResultModel.siteName = resultList.getString("SiteName");
                        }
                        if (resultList.has("SiteId")) {
//                            globalSearchResultModel.siteId = resultList.getInt("SiteId");

                        }
//                            globalSearchResultModel.siteId = resultList.getInt("SiteId");
                        globalSearchResultModel.componentName = selectedModel.componentName;
                        globalSearchResultModel.componentID = selectedModel.componentID;
                        globalSearchResultModel.componentInstanceID = selectedModel.componentInstancID;
                        globalSearchResultModel.siteId = selectedModel.siteID;
                        globalSearchResultModel.menuID = selectedModel.menuId;
                        globalSearchResultModel.headerName = selectedModel.componentName + " - " + selectedModel.siteName;

                        if (resultList.has("Title")) {
                            globalSearchResultModel.title = resultList.getString("Title");
                        }

                        if (resultList.has("ShortDescription")) {
                            String shortDesc = resultList.getString("ShortDescription");
                            if (isValidString(shortDesc)) {

                                globalSearchResultModel.shortDescription = shortDesc;

                            } else {

                                globalSearchResultModel.shortDescription = "";

                            }

                        }
                        if (resultList.has("LongDescription")) {

                            String longDesc = resultList.getString("LongDescription");
                            if (isValidString(longDesc)) {
                                globalSearchResultModel.longDescription = longDesc;
                            } else {

                                globalSearchResultModel.longDescription = "";

                            }


                        }
                        if (resultList.has("AuthorDisplayName")) {
                            globalSearchResultModel.authorDisplayName = resultList.getString("AuthorDisplayName");
                        }
                        if (resultList.has("AuthorName")) {
                            globalSearchResultModel.authorName = resultList.getString("AuthorName");
                        }

                        if (resultList.has("ThumbnailImagePath")) {
                            globalSearchResultModel.thumbnailImagePath = resultList.getString("ThumbnailImagePath");
                        }

                        if (resultList.has("EventAvailableSeats")) {
                            globalSearchResultModel.eventAvailableSeats = resultList.getString("EventAvailableSeats");
                        }

                        if (resultList.has("NamePreFix")) {

                            Spanned result = fromHtml(resultList.getString("NamePreFix"));

                            globalSearchResultModel.namePreFix = result.toString();
                        }

                        if (resultList.has("RatingID")) {

                            String ratingID = resultList.getString("RatingID");

                            if (isValidString(ratingID)) {
                                globalSearchResultModel.ratingID = ratingID;
                            } else {
                                globalSearchResultModel.ratingID = "";
                            }

                        }


                        globalSearchResultModelList.add(globalSearchResultModel);
                    }
                }
            }
        }
        return globalSearchResultModelList;
    }
}
