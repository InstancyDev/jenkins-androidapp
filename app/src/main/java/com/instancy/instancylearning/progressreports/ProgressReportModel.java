package com.instancy.instancylearning.progressreports;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class ProgressReportModel implements Serializable {

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

    public List<ProgressReportChildModel> progressReportChildModelList;
}
