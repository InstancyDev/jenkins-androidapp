package com.instancy.instancylearning.askexpertenached;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class AskExpertAnswerModelDg implements Serializable {


    public int viewsCount = 0;
    public int responseID = 0;
    public int questionID = 0;
    public int commentCount = 0;
    public int upvotesCount = 0;
    public boolean isLiked = false;
    public boolean commentAction = false;
    public int respondedUserId = 0;
    public int siteID = 0;
    public int userID = 0;
    public String response = "";
    public String respondedDate = "";
    public String respondedUserName = "";
    public String respondeDate = "";
    public String userResponseImage = "";
    public String picture = "";
    public String userResponseImagePath = "";
    public String daysAgo = "";
    public String responseUpVoters = "";
    public String isLikedStr;

    public String questionForMail;
    public String userMail;
    public String createdDateMail;


//    "(ID INTEGER PRIMARY KEY AUTOINCREMENT, questionid INTEGER, responseid INTEGER, commentCount INTEGER, upvotesCount INTEGER, IsLiked BOOLEAN,commentAction BOOLEAN, respondedUserId INTEGER, siteid INTEGER,userId INTEGER,response TEXT,respondedDate TEXT,respondedUserName TEXT,respondeDate TEXT,userResponseImage TEXT,picture TEXT,userResponseImagePath TEXT,daysAgo TEXT,responseUpVoters TEXT)");

}