package com.instancy.instancylearning.askexpertenached;

import java.io.Serializable;

/**
 * created by Upendranath on 12/12/2017.
 */

public class AskExpertCommentModel implements Serializable {

    public int questionID = 0;
    public int siteID = 0;
    public int userID = 0;
    public String commentAction = "";
    public String commentDate = "";
    public String commentDescription = "";
    public int commentID = 0;
    public String commentImage = "";
    public int commentQuestionID = 0;
    public int commentResponseID = 0;
    public int commentUserID = -1;
    public String commentedDate = "";
    public String commentedUserName = "";
    public boolean isLiked = true;
    public String imagePath = "";
    public String usercCmntImagePath = null;

}