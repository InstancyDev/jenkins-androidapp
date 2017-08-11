package com.instancy.instancylearning.models;

import com.instancy.instancylearning.R;

/**
 * Created by Upendranath on 5/30/2017.
 */

public class AppUserModel {


    private String userName = "";

    public String getUserName() {
        return userName.isEmpty() ? "Anonymous" : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName.isEmpty() ? "Playground" : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserIDValue() {
        return userIDValue.isEmpty() ? "-1" : userIDValue;
    }

    public void setUserIDValue(String userIDValue) {
        this.userIDValue = userIDValue;
    }

    public String getSiteIDValue() {
        return siteIDValue.isEmpty() ? "374" : siteIDValue;
    }

    public void setSiteIDValue(String siteIDValue) {
        this.siteIDValue = siteIDValue;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteURL() {
        return siteURL.isEmpty() ? "" + R.string.app_default_url : siteURL;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public String getWebAPIUrl() {
        return webAPIUrl;
    }

    public void setWebAPIUrl(String webAPIUrl) {
        this.webAPIUrl = webAPIUrl;
    }

    public String getBase64Credentials() {
        return base64Credentials;
    }

    public void setBase64Credentials(String base64Credentials) {
        this.base64Credentials = base64Credentials;
    }

    public String getAuthHeaders() {
        return authHeaders;
    }

    public void setAuthHeaders(String authHeaders) {
        this.authHeaders = authHeaders;
    }

    public String getAuthSubsiteHeaders() {
        return authSubsiteHeaders;
    }

    public void setAuthSubsiteHeaders(String authSubsiteHeaders) {
        this.authSubsiteHeaders = authSubsiteHeaders;
    }

    private String password = "";
    private String displayName = "";
    private String userIDValue = "";
    private String siteIDValue = "";
    private String siteName = "";
    private String siteURL = "";
    private String webAPIUrl = "";
    private String base64Credentials = "";
    private String authHeaders = "";
    private String authSubsiteHeaders = "";

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    private String profileImage = "";


    private static AppUserModel instance;

    public static synchronized AppUserModel getInstance() {
        if (instance == null) {
            instance = new AppUserModel();
        }
        return instance;
    }

}
