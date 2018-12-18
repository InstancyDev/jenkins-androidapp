package com.instancy.instancylearning.discussionfourmsenached;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class DiscussionReplyModelDg implements Serializable {

    public int siteID = 374;
    public int userID = 0;
    public String topicID = "";
    public int replyID = 0;
    public int commentID = 0;
    public int forumID = 0;
    public String message = "";
    public String postedDate = "";
    public int postedBy = 0;
    public String replyBy = "";
    public String picture = "";
    public boolean likeState = false;
    public String replyProfile = "";
    public String dtPostedOnDate = "";


    //   public String SqlQuery = "(ID INTEGER PRIMARY KEY AUTOINCREMENT,siteID INTEGER,userID TEXT,topicID TEXT,replyID INTEGER,commentID INTEGER,forumID INTEGER,message TEXT,postedDate TEXT,postedBy INTEGER, replyBy TEXT, picture TEXT, likeState BOOLEAN,replyProfile TEXT,dtPostedOnDate TEXT)";

}
