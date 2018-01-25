package com.instancy.instancylearning.models;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class PeopleListingModel implements Serializable {

    public int connectionUserID = -1;
    public String jobTitle = "";
    public String mainOfficeAddress = "";
    public String memberProfileImage = "";
    public String userDisplayname = "";
    public String connectionState = "";
    public String connectionStateAccept;
    public boolean viewProfileAction = false;
    public boolean acceptAction = false;
    public boolean viewContentAction = false;
    public boolean ignoreAction = false;
    public boolean sendMessageAction = false;
    public boolean addToMyConnectionAction = false;
    public boolean removeFromMyConnectionAction = false;
    public String interestAreas = "";
    public int notaMember = -1;
    public String siteURL = "";
    public String siteID = "";
    public String userID = "";
    public String tabID = "";
    public String mainSiteUserID = "";
    public String chatConnectionUserId = "Default";

    public String chatUserStatus = "";
}
