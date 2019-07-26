package com.instancy.instancylearning.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Upendranath on 5/31/2017.
 */

public class PreferencesManager {

    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(StaticValues.INSTANCYPREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            try {
//                throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
//                        " is not initialized, call initializeInstance(..) method first.");
                Log.d("PREF", "getInstance: is not initialized, call initializeInstance(..) method first.");
            } catch (IllegalStateException il) {
                il.printStackTrace();
            }

        }
        return sInstance;
    }

    public void setLongValue(long value, String KEY_VALUE) {
        mPref.edit().putLong(KEY_VALUE, value).commit();
    }

    public void setStringValue(String value, String KEY_VALUE) {
        mPref.edit().putString(KEY_VALUE, value).commit();
    }

    public String getStringValue(String KEY_VALUE) {
        return mPref.getString(KEY_VALUE, "");
    }

    public String getLocalizationStringValue(String KEY_VALUE) {
        return mPref.getString(KEY_VALUE, "en-us");
    }

    public String getLocalizationDisplayStringValue(String KEY_VALUE) {
        return mPref.getString(KEY_VALUE, "English");
    }

//    public String getLocalizationStringValue(String KEY_VALUE) {
//
//        return mPref.getString(KEY_VALUE, "es-es");
//
//    }
//
//    public String getLocalizationDisplayStringValue(String KEY_VALUE) {
//        return mPref.getString(KEY_VALUE, "Spanish");
//    }


    public void setBooleanValue(boolean value, String KEY_VALUE) {
        mPref.edit().putBoolean(KEY_VALUE, value).commit();
    }

    public boolean getBooleanValue(String KEY_VALUE) {
        return mPref.getBoolean(KEY_VALUE, false);
    }

    public void remove(String key) {
        mPref.edit().remove(key).commit();
    }

    public boolean clear() {
        return mPref.edit().clear().commit();
    }
}
