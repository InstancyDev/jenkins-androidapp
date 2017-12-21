package com.instancy.instancylearning.models;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class CommunitiesModel implements Serializable {


    public int learningportalid = -1;
    public String learningprovidername = "";
    public String communitydescription = "";
    public String keywords = "";
    public int userid = -1;
    public int siteid = -1;
    public String siteurl = "";
    public String parentsiteurl;
    public int parentsiteid = -1;
    public int orgunitid = -1;
    public int objectid = -1;
    public int categoryid = -1;
    public String name = "";
    public String imagepath = "";
    public String labelalreadyamember = "";
    public String labelpendingrequest = "";
    public int actiongoto = -1;
    public int actionjoincommunity = -1;

}
