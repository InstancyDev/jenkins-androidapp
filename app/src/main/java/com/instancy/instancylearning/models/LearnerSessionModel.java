package com.instancy.instancylearning.models;

import java.io.Serializable;

/**
 * Created by Upendranath on 8/4/2017 Working on InstancyLearning.
 */

public class LearnerSessionModel implements Serializable {


    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getScoID() {
        return scoID;
    }

    public void setScoID(String scoID) {
        this.scoID = scoID;
    }

    public String getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(String attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public String getSessionDateTime() {
        return sessionDateTime;
    }

    public void setSessionDateTime(String sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    String sessionID;
    String userID;
    String scoID;
    String attemptNumber;
    String sessionDateTime;
    String timeSpent;

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    String siteID;

}
