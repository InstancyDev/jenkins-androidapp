package com.instancy.instancylearning.progressreports;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class ProgressReportModel implements Serializable {


//                "CID":detailContentModel.contentID,
//            "ObjectTypeID":detailContentModel.objectTypeID,
//            "UserID":detailContentModel.userID,
//            "StartDate":detailContentModel.startDate,
//            "EndDate":self.getCurrentDate(),
//            "SeqID":-1,//detailContentModel.sequenceNumber,
//            "TrackID":detailContentModel.trackID,
//            "siteid":detailContentModel.siteID == "" ? "374":detailContentModel.siteID,
//            "locale":""+preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))+"",
//            "EventID":detailContentModel.trackID


    public String jobrolID = "";
    public String userid = "";
    public String skillname = "";
    public String gradedColor = "";
    public String contenttitle = "";
    public String targetDate = "";
    public String objectTypeID = "";
    public String childData = "";
    public String overScore = "";
    public String objectID = "";
    public String siteID = "";
    public String cartID = "";
    public String datecompleted = "";
    public String categoryname = "";
    public String orgname = "";
    public String datestarted = "";
    public String contenttype = "";
    public String status = "";
    public String assignedOn = "";
    public String jobrolename = "";
    public String SCOID = "";
    public String categoryID = "";
    public int seqId = -1;

    public String eventID = "";
    public String trackID = "";

    public List<ProgressReportChildModel> progressReportChildModelList;
}
