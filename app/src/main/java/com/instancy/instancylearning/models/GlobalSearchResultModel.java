package com.instancy.instancylearning.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class GlobalSearchResultModel implements Serializable {

    public String headerName = "";
    public String componentName = "";
    public String addLink = "";
    public String participantURL = "";
    public String contentType = "";
    public String tags = "";
    public String relatedContentLink = "";
    public String headerLocationName = "";
    public String eventAvailableSeats = "";
    public String authorDisplayName = "";
    public String longDescription = "";
    boolean listView = false;
    public String timeZone = "";
    public String recommendedLink = "";
    public int filterId = 0;
    public String siteURL = "";
    public int contentTypeId = 0;
    //    public String siteName = "";
    public String shortDescription = "";
    public String thumbnailImagePath = "";
    public String detailsLink = "";
    public int viewType = 0;
    public String enrollLink = "";
    public String membershipName = "";
    public String categorycolor = "";
    public String isRelatedcontent = "";
    public String publishedDate = "";
    public String eventEndDateTime = "";
    public int isaddtomylearninglogo = 0;
    public String namePreFix = "";
    public String eventContentProgress = "";
    public String itunesProductID = "";
    public String salePrice = "";
    public String totalRatings = "";
    public String sharetoRecommendedLink = "";
    public String authorName = "";
    public String mediaTypeID = "";
    public String locationName = "";
    public String viewProfileLink = "";
    public String waitListLink = "";
    public String joinURL = "";
    public String sCOID = "";
    public String authorWithLink = "";
    public String destinationLink = "";
    public String currency = "";
    public String sharelink = "";
    public String cancelEventLink = "";
    public int userSiteId = -1;
    public String title = "";
    public String ratingID = "";
    public String viewLink = "";
    public String contentID = "";
    public String isCoursePackage = "";
    public String suggesttoConnLink = "";
    public String freePrice = "";
    public String imageWithLink = "";
    public String noImageText = "";
    public String titleExpired = "";
    public String expandiconpath = "";
    public String createdOn = "";
    public String invitationURL = "";
    public String eventStartDateTime = "";
    public String startPage = "";
    public String suggestwithFriendLink = "";

    public int contextMenuId=0;
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

//    public static HashMap<String, List<GlobalSearchResultModel>> fetchCategoriesData(List<GlobalSearchResultModelNew> advancedFilterModelList) {
//        HashMap<String, List<GlobalSearchResultModelNew>> expandableListDetail = new HashMap<String, List<GlobalSearchResultModelNew>>();
//
//
////                HashMap<String, List<GlobalSearchCategoryModel>> studlistGrouped =
////                        studlist.stream().collect(Collectors.groupingBy(w -> w.stud_location));
//
//        for (GlobalSearchResultModelNew globalSearchCategoryModel : advancedFilterModelList) {
//            String headerName = globalSearchCategoryModel.headerName;
//            if (expandableListDetail.containsKey(headerName)) {
//                List<GlobalSearchResultModelNew> list = expandableListDetail.get(headerName);
//                list.add(globalSearchCategoryModel);
//
//            } else {
//                List<GlobalSearchResultModelNew> list = new ArrayList<GlobalSearchResultModelNew>();
//                list.add(globalSearchCategoryModel);
//
//                expandableListDetail.put(headerName, list);
//            }
//
//        }
//
//        return expandableListDetail;
//    }

}
