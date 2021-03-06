package com.instancy.instancylearning.globalpackage;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.crashlytics.android.Crashlytics;
import com.instancy.instancylearning.utils.ApiConstants;

import io.fabric.sdk.android.Fabric;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
        Fabric.with(this, new Crashlytics());

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


    public void register(final Context context, String userid, String regid, String merchantid) {
        Log.i(TAG, "registering device (regId = " + regid + ")");
        /*Map<String, String> params = new HashMap<String, String>();
        params.put("did", regid);
		params.put("uid", userid);
		params.put("type", "1");*/

        String serverUrl = ApiConstants.LOGINURL + "did=" + regid + "&uid=" + userid + "&type=1" + "&mi=" + merchantid;
        Log.d(TAG, "register serverUrl: " + serverUrl);

    }


}
