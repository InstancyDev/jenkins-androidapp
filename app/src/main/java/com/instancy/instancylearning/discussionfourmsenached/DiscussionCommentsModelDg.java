package com.instancy.instancylearning.discussionfourmsenached;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class DiscussionCommentsModelDg implements Serializable {

    public int userID = 0;
    public int commentID = 0;
    public String topicID = "";
    public int forumID = 0;
    public String message = "";
    public String postedDate;
    public int postedBy = 0;
    public int siteID = 374;
    public String replyID = "";
    public String commentedBy = "";
    public String commentedFromDays = "";
    public String commentFileUploadPath = "";
    public String commentFileUploadName = "";
    public String commentVideoUploadName = "";
    public String commentAudioUploadName = "";
    public String commentApplicationUploadName = "";
    public boolean likeState = false;
    public int commentLikes = 0;
    public int commentRepliesCount = 0;
    public String commentUserProfile = "";


    //   public String SqlQuery = "(ID INTEGER PRIMARY KEY AUTOINCREMENT,commentID INTEGER,topicID TEXT,forumID TEXT,message TEXT,postedDate TEXT,postedBy INTEGER,siteID INTEGER,replyID INTEGER,commentedBy TEXT, commentedFromDays TEXT, commentFileUploadPath TEXT, commentFileUploadName TEXT,commentVideoUploadName TEXT,commentAudioUploadName TEXT,commentApplicationUploadName TEXT,likeState BOOLEAN,commentLikes INTEGER,commentRepliesCount INTEGER, commentUserProfile TEXT)";

}
