package com.instancy.instancylearning.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class GlobalSearchResultModelNew implements Serializable {


    public int menuID = 0;
    public int contextMenuId = 0;
    public String componentName = "";
    public String isListView = "false";

    public String userID = "";
    public String headerName = "";
    public String objectid = "";
    public int scoid = 0;
    public String availableseats = "";
    public String totalenrolls = "";
    public String waitlistenrolls = "";
    public int relatedconentcount = 0;
    public int siteid = 0;
    public String sitename = "";
    public String language = "";
    public int totalratings = 0;
    public int ratingid = 0;
    public int componentid = 0;
    public String description = "";
    public String contenttype = "";
    public String iconpath = "";
    public boolean iscontent = false;
    public int objecttypeid = 0;
    public String contenttypethumbnail = "";
    public String contentid = "";
    public String name = "";
    public String shortdescription = "";
    public String startpage = "";
    public int createduserid = -1;
    public String createddate = "";
    public String keywords = "";
    public String tags = "";
    public boolean downloadable = false;
    public String publisheddate = "";
    public String status = "";
    public String cmsgroupid = "";
    public String folderid = "";
    public String checkedoutto = "";
    public String longdescription = "";
    public String mediatypeid = "";
    public String publicationdate = "";
    public String activatedate = "";
    public String expirydate = "";
    public String thumbnailimagepath = "";
    public String downloadfile = "";
    public String saleprice = "";
    public String listprice = "";
    public String folderpath = "";
    public String modifieduserid = "";
    public String modifieddate = "";
    public String currency = "";
    public int viewtype = 0;
    public String active = "";
    public String enrollmentlimit = "";
    public String presenterurl = "";
    public String participanturl = "";
    public String eventstartdatetime = "";
    public String eventenddatetime = "";
    public String presenterid = "";
    public String contentstatus = "";
    public String location = "";
    public String conferencenumbers = "";
    public String directionurl = "";
    public String starttime = "";
    public String duration = "";
    public String eventkey = "";
    public int noofusersenrolled = -1;
    public int noofuserscompleted = -1;
    public String eventtype = "";
    public String timezone = "";
    public int membershiplevel = -1;
    public String membershipname = "";
    public String medianame = "";
    public int componentInstanceID = 0;
    public int typeofevent = -1;
    public String googleproductid = "";
    public String activityid = "";
    public String jwvideokey = "";
    public String cloudmediaplayerkey = "";
    public String eventresourcedisplayoption = "";
    public String contentauthordisplayname = "";
    public String authordisplayname = "";
    public String presenter = "";
    public String eventenddatedisplay = "";
    public int isaddedtomylearning = -1;
    public String eventstartdatedisplay = "";
    public String siteurl = "";
    public boolean isBadCancellationEnabled = false;


    public static HashMap<String, List<GlobalSearchResultModelNew>> fetchCategoriesData(List<GlobalSearchResultModelNew> advancedFilterModelList) {
        HashMap<String, List<GlobalSearchResultModelNew>> expandableListDetail = new HashMap<String, List<GlobalSearchResultModelNew>>();


//                HashMap<String, List<GlobalSearchCategoryModel>> studlistGrouped =
//                        studlist.stream().collect(Collectors.groupingBy(w -> w.stud_location));  upendranath reddy

        for (GlobalSearchResultModelNew globalSearchCategoryModel : advancedFilterModelList) {
            String headerName = globalSearchCategoryModel.headerName;
            if (expandableListDetail.containsKey(headerName)) {
                List<GlobalSearchResultModelNew> list = expandableListDetail.get(headerName);
                list.add(globalSearchCategoryModel);

            } else {
                List<GlobalSearchResultModelNew> list = new ArrayList<GlobalSearchResultModelNew>();
                list.add(globalSearchCategoryModel);

                expandableListDetail.put(headerName, list);
            }

        }

        return expandableListDetail;
    }

}
