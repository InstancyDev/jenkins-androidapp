package com.instancy.instancylearning.mainactivities;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.utils.StaticValues;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.instancy.instancylearning.utils.Utilities.formatURL;

/**
 * Created by Upendranath on 5/11/2017.
 */

public class Settings_activity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = Settings_activity.class.getSimpleName();

    private AppCompatDelegate mDelegate;
    public static final String PREF_KEY_SCREEN = "pref_screen";
    public static final String PREF_KEY_CATEGORY_URL = "cat_url";
    public static final String PREF_KEY_CATEGORY_NOTIFY = "cat_notif";
    public static final String PREF_KEY_CATEGORY_DOWN = "cat_down";
    public static final String PREF_KEY_RESET = "reseturl";
    public static final String PREF_KEY_URL = "url";
    public static final String PREF_KEY_TARGET = "target_reminder";
    public static final String PREF_KEY_TARGET_2 = "target_reminder_2";
    public static final String PREF_KEY_TARGET_3 = "target_reminder_3";
    public static final String PREF_KEY_EVENT_REMINDER = "event_reminder";
    public static final String PREF_KEY_ASSIGNMENT = "assignment";
    public static final String PREF_KEY_NEW_CONTENT = "new_content";
    public static final String PREF_KEY_UNASSIGNMENT = "unassignment";
    public static final String PREF_KEY_DOWN_AUTO = "down_auto";
    public static final String PREF_KEY_DOWN_MOBILE = "down_mobile";
    public static final String PREF_KEY_DOWN_STATUS = "auto_down_status";
    public static final String PREF_KEY_NOTIF_STATUS = "push_notif_status";
    public static final String PREF_KEY_CHECK_UPDATES = "check_for_updates";

    PreferenceCategory urlCategory;
    PreferenceCategory notificationCategory;
    PreferenceCategory downloadCategory;
    PreferenceScreen preferenceScreen;
    ContentValues prefsToUpdate = null;
    boolean defaultvalue = false;

    SharedPreferences sharedPreferences;

    boolean isLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDelegate().getSupportActionBar().setTitle(getString(R.string.pref_head_preferences));
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.settings_activity);
        addPreferencesFromResource(R.xml.preference);
        sharedPreferences = getSharedPreferences(StaticValues.INSTANCYPREFS_NAME, MODE_PRIVATE);

        initilizeView();

    }

    public void initilizeView() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            isLogin = bundle.getBoolean(StaticValues.KEY_ISLOGIN);
        }

        preferenceScreen = getPreferenceScreen();
        preferenceScreen.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        urlCategory =
                (PreferenceCategory) findPreference("cat_url");
        notificationCategory = (PreferenceCategory) findPreference("cat_notif");
        downloadCategory =
                (PreferenceCategory) findPreference("cat_down");
        SwitchPreference down_auto = (SwitchPreference) preferenceScreen
                .findPreference(PREF_KEY_DOWN_AUTO);
        SwitchPreference down_mobile = (SwitchPreference) preferenceScreen
                .findPreference(PREF_KEY_DOWN_MOBILE);

        SwitchPreference reseturl = (SwitchPreference) preferenceScreen
                .findPreference(PREF_KEY_RESET);
        EditTextPreference url = (EditTextPreference) preferenceScreen
                .findPreference(PREF_KEY_URL);
        SwitchPreference target_reminder = (SwitchPreference) preferenceScreen
                .findPreference(PREF_KEY_TARGET);
        SwitchPreference assignment = (SwitchPreference) preferenceScreen
                .findPreference(PREF_KEY_ASSIGNMENT);
        SwitchPreference new_content = (SwitchPreference) preferenceScreen
                .findPreference(PREF_KEY_NEW_CONTENT);
        SwitchPreference unassignment = (SwitchPreference) preferenceScreen
                .findPreference(PREF_KEY_UNASSIGNMENT);


        if (isLogin) {
            preferenceScreen.removePreference(urlCategory);
            notificationCategory.setEnabled(true);
            downloadCategory.setEnabled(true);
        } else {
            urlCategory.setEnabled(true);
            downloadCategory.setEnabled(false);
            notificationCategory.setEnabled(false);
            preferenceScreen.removePreference(downloadCategory);
            preferenceScreen.removePreference(notificationCategory);

            resetPreferencesToDefault();

        }
        setCheckPrefChangeListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Log.d("DEBUG", "onOptionsItemSelected: ");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        updatePreference(key);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {


        String strNewValue = String.valueOf(newValue);
        Log.d(TAG, "onPreferenceChange 1: " + newValue);
        switch (preference.getKey()) {
            case PREF_KEY_RESET:
                putNewPrefToCollection(PREF_KEY_RESET, strNewValue);
                //setResult(1212);
                Log.d(TAG, "onPreferenceChange 2: " + newValue);
                break;
            case PREF_KEY_URL:
                strNewValue = formatURL(strNewValue);
                putNewPrefToCollection(PREF_KEY_URL, strNewValue);
                Log.d(TAG, "onPreferenceChange 3: " + newValue);
                //setResult(1212);
                break;

        }
        Log.d(TAG, "onPreferenceChange 4: " + newValue);
        return true;
    }

    private void putNewPrefToCollection(String strKey, String strNewValue) {
        if (prefsToUpdate != null) {
            if (prefsToUpdate.containsKey(strKey)) {
                prefsToUpdate.remove(strKey);
            }
            prefsToUpdate.put(strKey, strNewValue);
        }

    }

    private void updatePreference(String key) {

        if (key.equals(PREF_KEY_URL)) {
            final Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference) {
                EditTextPreference editTextPreference = (EditTextPreference) preference;

                String newUrl = editTextPreference.getText().trim();
                newUrl = formatURL(newUrl);

                if (newUrl.length() > 0) {
                    String tempWebAPI = "";

                    if (!tempWebAPI.equals("")) {
                        editTextPreference.setText(newUrl);
                        SharedPreferences.Editor editor = getSharedPreferences(StaticValues.INSTANCYPREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("url", newUrl);
                        editor.putString("webapiurl", tempWebAPI);
                        editor.commit();
                        Bundle b = new Bundle();
                        b.putBoolean("isrestart", true);

                    } else {
                        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this file!")
                                .setConfirmText("Yes,delete it!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                    }
                                })
                                .show();
                    }
                } else {

                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(getResources().getString(R.string.alert_headtext_no_internet))
                            .setContentText(getResources().getString(R.string.alert_text_check_connection))
                            .show();


                }
            }
        } else if (key.equals(PREF_KEY_RESET)) {

            Log.d(TAG, "PREF_KEY_RESET : " + PREF_KEY_RESET);

            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("Do you want to reset the site URL!")
                    .setConfirmText("Yes,reset it!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(PREF_KEY_RESET, getString(R.string.app_default_url));
                            editor.commit();
                            sDialog.dismissWithAnimation();
                        }
                    }).setCancelText("Cancel").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {

                    sweetAlertDialog.dismissWithAnimation();

                }
            })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (isLogin) {

        }

        finish();
        super.onBackPressed();
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private void resetPreferencesToDefault() {
        Map<String, Boolean> categories = (Map<String, Boolean>) sharedPreferences.getAll();
        for (String s : categories.keySet()) {
            if (!s.equals(PREF_KEY_RESET)) {
                Preference pref = findPreference(s);
                if (pref instanceof SwitchPreference) {
                    SwitchPreference chkpref = (SwitchPreference) pref;
                    chkpref.setChecked(false);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private void setCheckPrefChangeListener() {
        Map<String, Boolean> categories = (Map<String, Boolean>) sharedPreferences.getAll();
        for (String s : categories.keySet()) {
            Preference pref = findPreference(s);
            if (pref instanceof SwitchPreference) {
                SwitchPreference chkpref = (SwitchPreference) pref;
                chkpref.setOnPreferenceChangeListener(this);
            }
            if (pref instanceof EditTextPreference) {
                EditTextPreference ep = new EditTextPreference(this);
                ep.setOnPreferenceChangeListener(this);

            }
        }
    }
}
