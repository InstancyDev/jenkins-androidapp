package com.instancy.instancylearning.models;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public  class UserPrivilegesModel implements Serializable {

    public int CreateForum = 434;
    public int EditForum = 457;
    public int DeleteForum = 436;

    public int CreateTopic = 435;
    public int EditTopic = 437;
    public int DeleteTopic = 441;
    public int MoveTopic = 438;

    public int CreateReply = 439;
    public int EditReply = 709;
    public int DeleteReply = 710;

    public int DeleteQuestion = 979;
    public int ReplyQuestion = 980;

    public int CatalogDeleteDownload = 1165;
    public int CatalogShareContent = 1166;
    public int MyLearningDeleteDownload = 1167;
    public int MyLearningShareContent = 1168;

}
