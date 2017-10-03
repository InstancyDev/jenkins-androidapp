package com.instancy.instancylearning.utils;

import android.content.Context;
import android.content.SharedPreferences;

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
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setLongValue(long value, String KEY_VALUE) {
        mPref.edit()
                .putLong(KEY_VALUE, value)
                .commit();
    }

    public void setStringValue(String value, String KEY_VALUE) {
        mPref.edit()
                .putString(KEY_VALUE, value)
                .commit();
    }

    public String getStringValue(String KEY_VALUE) {
        return mPref.getString(KEY_VALUE, "");
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .commit();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }


}
