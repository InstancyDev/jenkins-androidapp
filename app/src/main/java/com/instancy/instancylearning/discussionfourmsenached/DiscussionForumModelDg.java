package com.instancy.instancylearning.discussionfourmsenached;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class DiscussionForumModelDg implements Serializable {


    public int forumID = 0;
    public String name = "";
    public String description = "";
    public int parentForumID = 0;
    public int displayOrder = 0;
    public int siteID = 0;
    public int createdUserID = 0;
    public String createdDate = "";
    public boolean active = false;
    public boolean requiresSubscription = false;
    public boolean createNewTopic = false;
    public boolean attachFile = false;
    public boolean likePosts = false;
    public boolean sendEmail = false;
    public boolean moderation = false;
    public boolean isPrivate = false;
    public String author = "";
    public int noOfTopics = 0;
    public int totalPosts = 0;
    public int existing = 0;
    public int totalLikes = 0;
    public String dfProfileImage = "";
    public String dfUpdateTime = "";
    public String dfChangeUpdateTime = "";
    public String forumThumbnailPath = "";
    public String descriptionWithLimit = "";
    public String moderatorID = "";
    public String updatedAuthor = "";
    public String updatedDate = "";
    public String moderatorName = "";
    public boolean allowShare = false;
    public String descriptionWithoutLimit = "";
    public String categoryIDs = "";
    public List<String> categoriesIDArray = null;
    public List<String> moderatorIDArray = null;

    public String SqlQuery = "(ID INTEGER PRIMARY KEY AUTOINCREMENT,forumID INTEGER,name TEXT,description TEXT,parentForumID INTEGER,displayOrder INTEGER,siteID INTEGER,createdUserID INTEGER,createdDate TEXT,active BOOLEAN,requiresSubscription BOOLEAN, createNewTopic BOOLEAN, attachFile BOOLEAN, likePosts BOOLEAN,sendEmail BOOLEAN,moderation BOOLEAN,isPrivate BOOLEAN,author TEXT,noOfTopics INTEGER,totalPosts INTEGER, existing INTEGER, totalLikes INTEGER,dfProfileImage TEXT,dfUpdateTime TEXT,dfChangeUpdateTime TEXT,forumThumbnailPath TEXT,descriptionWithLimit TEXT,moderatorID INTEGER,updatedAuthor TEXT,updatedDate TEXT,moderatorName TEXT,allowShare BOOLEAN,descriptionWithoutLimit TEXT)";

}
