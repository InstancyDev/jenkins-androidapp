package com.instancy.instancylearning.models;

import java.io.Serializable;

import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class MyLearningModel implements Serializable {


    String progress = "";
    String userName = "";
    String siteID = "";
    String siteName = "";
    String siteURL = "";
    String userID = "";
    String courseName = "";
    String shortDes = "";
    String author = "";
    String contentID = "";
    String createdDate = "";
    String displayName = "";
    String durationEndDate = "";
    String objectId = "";
    String imageData = "";
    String relatedContentCount = "0";
    String isDownloaded = "";
    String courseAttempts = "";
    String objecttypeId = "";
    String scoId = "";
    String startPage = "";

    String status = "";
    String statusDisplay = "";
    String contentType = "";
    String longDes = "";
    String mediaName = "";
    String ratingId = "";
    String publishedDate = "";
    String eventstartTime = "";
    String eventendTime = "";
    String mediatypeId = "";
    String dateAssigned = "";
    String keywords = "";
    String eventContentid = "";
    Boolean eventAddedToCalender = false;
    String isExpiry = "";
    String locationName = "";
    String timeZone = "";
    String participantUrl = "";
    String password = "";
    String isListView = "";
    String thumbnailImagePath = "";

    String contentExpire = "";
    String joinurl = "";
    int typeofevent = 0;
    String downloadURL = "";
    String offlinepath = "";
    String wresult = "";
    String wmessage = "";
    String presenter = "";

    // Exclusive For Track List Model start
    String score = "";
    String timeDelay = "";
    String blockName = "";
    int sequenceNumber = 0;
    String folderID = "";
    String parentID = "";
    String trackScoid = "";
    String showStatus = "";

    public String getTrackOrRelatedContentID() {
        return trackOrRelatedContentID;
    }

    public void setTrackOrRelatedContentID(String trackOrRelatedContentID) {
        this.trackOrRelatedContentID = trackOrRelatedContentID;
    }

    String trackOrRelatedContentID = "";

    // Exclusive For Catalog Model start

    String googleProductID = "";

    String componentId = "";

    String price = "";

    int addedToMylearning = 0;

    String itemType = "";

    String viewType = "";

    String currency = "";

    public int getAddedToMylearning() {


        return addedToMylearning;
    }

    public void setAddedToMylearning(int addedToMylearning) {
        this.addedToMylearning = addedToMylearning;
    }

    public String getItemType() {

        return isValidString(itemType) ? itemType : "";
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getViewType() {

        return isValidString(viewType) ? viewType : "";
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getCurrency() {
        return isValidString(currency) ? currency : "";
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoogleProductID() {

        return isValidString(googleProductID) ? googleProductID : "";
    }

    public void setGoogleProductID(String googleProductID) {
        this.googleProductID = googleProductID;
    }

    public String getEventID() {
        return isValidString(eventID) ? eventID : "";
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    String eventID = "";

    public String getIsDiscussion() {
        return isDiscussion;
    }

    public void setIsDiscussion(String isDiscussion) {
        this.isDiscussion = isDiscussion;
    }

    String isDiscussion = "";


    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(String timeDelay) {
        this.timeDelay = timeDelay;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getFolderID() {
        return folderID;
    }

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getTrackScoid() {
        return trackScoid;
    }

    public void setTrackScoid(String trackScoid) {
        this.trackScoid = trackScoid;
    }

    public String getShowStatus() {
        return isValidString(showStatus) ? showStatus : "";
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    // Exclusive For Track List Model End


    public String getProgress() {
        return progress.equalsIgnoreCase("") ? "0" : progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getShortDes() {

        return isValidString(shortDes) ? shortDes : "";
    }

    public void setShortDes(String shortDes) {
        this.shortDes = shortDes;
    }

    public String getAuthor() {
        return isValidString(author) ? author : "";
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDurationEndDate() {
        return durationEndDate;
    }

    public void setDurationEndDate(String durationEndDate) {
        this.durationEndDate = durationEndDate;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(String isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public String getCourseAttempts() {
        return courseAttempts;
    }

    public void setCourseAttempts(String courseAttempts) {
        this.courseAttempts = courseAttempts;
    }

    public String getObjecttypeId() {
        return objecttypeId;
    }

    public void setObjecttypeId(String objecttypeId) {
        this.objecttypeId = objecttypeId;
    }

    public String getScoId() {
        return scoId;
    }

    public void setScoId(String scoId) {
        this.scoId = scoId;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getLongDes() {
        return isValidString(longDes) ? longDes : "";

    }

    public void setLongDes(String longDes) {
        this.longDes = longDes;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getEventstartTime() {
        return eventstartTime;
    }

    public void setEventstartTime(String eventstartTime) {
        this.eventstartTime = eventstartTime;
    }

    public String getEventendTime() {
        return eventendTime;
    }

    public void setEventendTime(String eventendTime) {
        this.eventendTime = eventendTime;
    }

    public String getMediatypeId() {
        return mediatypeId;
    }

    public void setMediatypeId(String mediatypeId) {
        this.mediatypeId = mediatypeId;
    }

    public String getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(String dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getEventContentid() {
        return eventContentid;
    }

    public void setEventContentid(String eventContentid) {
        this.eventContentid = eventContentid;
    }

    public Boolean getEventAddedToCalender() {
        return eventAddedToCalender != null ? eventAddedToCalender : false;
    }

    public void setEventAddedToCalender(Boolean eventAddedToCalender) {
        this.eventAddedToCalender = eventAddedToCalender;
    }

    public String getIsExpiry() {
        return isExpiry;
    }

    public void setIsExpiry(String isExpiry) {
        this.isExpiry = isExpiry;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getParticipantUrl() {
        return participantUrl;
    }

    public void setParticipantUrl(String participantUrl) {
        this.participantUrl = participantUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsListView() {

        return isValidString(isListView) ? isListView : "false";
    }

    public void setIsListView(String isListView) {
        this.isListView = isListView;
    }

    public String getThumbnailImagePath() {
        return thumbnailImagePath;
    }

    public void setThumbnailImagePath(String thumbnailImagePath) {
        this.thumbnailImagePath = thumbnailImagePath;
    }

    public String getContentExpire() {
        return contentExpire;
    }

    public void setContentExpire(String contentExpire) {
        this.contentExpire = contentExpire;
    }

    public String getJoinurl() {
        return joinurl;
    }

    public void setJoinurl(String joinurl) {
        this.joinurl = joinurl;
    }

    public int getTypeofevent() {
        return typeofevent;
    }

    public void setTypeofevent(int typeofevent) {
        this.typeofevent = typeofevent;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getOfflinepath() {
        return offlinepath;
    }

    public void setOfflinepath(String offlinepath) {
        this.offlinepath = offlinepath;
    }

    public String getWresult() {
        return wresult;
    }

    public void setWresult(String wresult) {
        this.wresult = wresult;
    }

    public String getWmessage() {
        return wmessage;
    }

    public void setWmessage(String wmessage) {
        this.wmessage = wmessage;
    }

    public String getPresenter() {
        return presenter;
    }

    public void setPresenter(String presenter) {
        this.presenter = presenter;
    }

    public String getRelatedContentCount() {
        return relatedContentCount;
    }

    public void setRelatedContentCount(String relatedContentCount) {
        this.relatedContentCount = relatedContentCount;
    }


}
