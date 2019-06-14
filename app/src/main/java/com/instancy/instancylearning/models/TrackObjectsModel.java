package com.instancy.instancylearning.models;

/**
 * Created by Upendranath on 5/24/2017.
 */

public class TrackObjectsModel {

    private String trackSoId = "";
    private String scoId = "";

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private String siteID = "";
    private String userID = "";

    public String getTrackSoId() {
        return trackSoId;
    }

    public void setTrackSoId(String trackSoId) {
        this.trackSoId = trackSoId;
    }

    public String getScoId() {
        return scoId;
    }

    public void setScoId(String scoId) {
        this.scoId = scoId;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getObjTypeId() {
        return objTypeId;
    }

    public void setObjTypeId(String objTypeId) {
        this.objTypeId = objTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String sequenceNumber;
    private String objTypeId = "";
    private String name = "";

    public String getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(String mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    private String mediaTypeId = "";
}
