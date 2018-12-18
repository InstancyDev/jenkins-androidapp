package com.instancy.instancylearning.askexpertenached;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class AskExpertUpVoters implements Serializable {

    public int siteID = 0;
    public int objectId = 0;
    public int likeID = 0;
    public boolean isLiked = false;
    public int userID = 0;

    public String jobTitle = "";
    public String picture = "";
    public String userName = "";

//"(ID INTEGER PRIMARY KEY AUTOINCREMENT, objectId INTEGER, siteID INTEGER, likeID INTEGER, userID INTEGER, isLiked BOOLEAN, jobTitle TEXT, picture TEXT, userName TEXT)"
}