package com.instancy.instancylearning.chatmessanger;

import java.io.Serializable;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class BaseMessage implements Serializable {

    public String chatID = "";
    public String fromUserID = "";
    public String toUserID = "";
    public String messageChat = "";
    public String attachemnt = "";
    public String sendDateTime = "1";
    public String markAsRead = "";
    public String fromStatus = "";
    public String toStatus = "";
    public String fromUserName = "";
    public String toUsername = "";
    public String profilePic = "";
    public String sentDate = "";
    public boolean itsMe = true;

}
