package com.instancy.instancylearning.discussionfourmsenached;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class DiscussionTopicModelDg implements Serializable {

    public String contentID = "";
    public int forumId = 0;
    public String name = "";
    public String createdDate = "";
    public int createdUserID = 0;
    public int noOfReplies = 0;
    public int noOfViews = 0;
    public String longDescription = "";
    public String latestReplyBy = "";
    public String author = "";
    public String uploadFileName = "";
    public String updatedTime = "";
    public String createdTime = "";
    public String modifiedUserName = "";
    public String uploadedImageName = "";
    public int likes = 0;
    public boolean likeState = false;
    public String topicUserProfile = "";
    public boolean isPin = false;
    public int pinID = 0;
    public String comments = "";//BOOLEAN
    public int commentsCount = 0;
    public int siteId = 0;
    public int userId = 0;

    public String SqlQuery = "(ID INTEGER PRIMARY KEY AUTOINCREMENT,contentID TEXT,forumId INTEGER,name TEXT,createdDate INTEGER,createdUserID INTEGER,noOfReplies INTEGER,noOfViews INTEGER,createdDate TEXT,longDescription TEXT,latestReplyBy TEXT, author TEXT, uploadFileName TEXT, updatedTime TEXT,createdTime TEXT,modifiedUserName TEXT,uploadedImageName TEXT,likes INTEGER,likeState BOOLEAN,topicUserProfile TEXT, isPin BOOLEAN, pinID INTEGER,commentsCount INTEGER,siteId INTEGER,userId INTEGER)";

}
