package com.instancy.instancylearning.globalpackage;

import android.content.Context;
import android.support.multidex.MultiDex;
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

        if (AppController.context == null) {
            AppController.context = getApplicationContext();
        }

        sInstance = this;
    }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized AppController getInstance() {
        return sInstance;
    }


    private static Context context;

    public static synchronized Context getGlobalContext() {
        return context;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
