package com.instancy.instancylearning.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class AskExpertQuestionModel implements Serializable {
    public int questionID = 0;
    public String userID = "";
    public String username = "";
    public String userQuestion = "";
    public String postedDate = "";
    public String createdDate = "";
    public String answers = "";
    public String questionCategories = "";
    public String siteID = "";
    public String postedUserId = "";
    public String lastActive = "";
    public int noOfViews = 0;
    public String skillsString=null;
    public List<String> skillsTagsList = null;

}
