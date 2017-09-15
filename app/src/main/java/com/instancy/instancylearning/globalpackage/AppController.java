package com.instancy.instancylearning.globalpackage;

import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;

/**
 * Created by Upendranath on 5/11/2017.
 * http://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
 */

public class AppController extends MultiDexApplication {


    public boolean isAlreadyViewd() {
        return isAlreadyViewd;
    }

    public void setAlreadyViewd(boolean alreadyViewd) {
        isAlreadyViewd = alreadyViewd;
    }

    public boolean isAlreadyViewdTrack() {
        return isAlreadyViewdTrack;
    }

    public void setAlreadyViewdTrack(boolean alreadyViewdTrack) {
        isAlreadyViewdTrack = alreadyViewdTrack;
    }

    public boolean isAlreadyViewdTrack;


    private boolean isAlreadyViewd = false;

    private String webApiUrl;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    private String siteId = "";

    public String getWebApiUrl() {
        return webApiUrl;
    }

    public void setWebApiUrl(String webApiUrl) {
        this.webApiUrl = webApiUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    private String siteUrl;
    private String authentication;

    public static final String TAG = AppController.class
            .getSimpleName();

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;

    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static AppController sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the singleton
        sInstance = this;
    }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized AppController getInstance() {
        return sInstance;
    }
}
