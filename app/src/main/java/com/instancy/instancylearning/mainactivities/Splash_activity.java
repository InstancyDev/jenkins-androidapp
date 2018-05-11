package com.instancy.instancylearning.mainactivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
import com.instancy.instancylearning.asynchtask.GetSiteConfigsAsycTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;

import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.interfaces.SiteConfigInterface;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

public class Splash_activity extends Activity implements SiteConfigInterface {

    private static final int PERMISSION_REQUEST_CODE = 200;
    ImageView imageBrandLogo;
    Animation zoomin, zoomout;
    DatabaseHandler db;
    WebAPIClient webAPIClient;
    GetSiteConfigsAsycTask getSiteConfigsAsycTask;
    private static final String TAG = Splash_activity.class.getSimpleName();
    String siteUrl, siteID, userId;
    Context context;
    NetworkUtils networkUtils;
    UiSettingsModel uiSettingsModel;
    @BindView(R.id.progressBarSplash)
    ProgressBar progressBar;
    private int progressStatus = 0;
    AppUserModel appUserModel;
    PreferencesManager preferencesManager;
    AppController appController;
    boolean isFromPush = false;
    JSONObject parameters = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.bind(this);
        context = this;
        imageBrandLogo = (ImageView) findViewById(R.id.brandlogo);

        db = new DatabaseHandler(context);

        appUserModel = AppUserModel.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();
        PreferencesManager.initializeInstance(context);
        preferencesManager = PreferencesManager.getInstance();
        appController = AppController.getInstance();
        String siteUrl = preferencesManager.getStringValue(StaticValues.KEY_SITEURL);
        String siteID = preferencesManager.getStringValue(StaticValues.KEY_SITEID);
        preferencesManager.setStringValue("false", StaticValues.SUB_SITE_ENTERED);
        if (siteUrl.length() == 0) {
            preferencesManager.setStringValue(getResources().getString(R.string.app_default_url), StaticValues.KEY_SITEURL);

        }
        if (siteID.length() == 0) {

            preferencesManager.setStringValue("374", StaticValues.KEY_SITEID);
        }


//        Message contextmenuid menuid siteid contentid

        parameters = new JSONObject();

        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {

                if (key.equals("contextmenuid")) {

                    Log.d(TAG, "onCreate: contextmenuid " + getIntent().getExtras().getString("contextmenuid"));

                    String contextmenuID = getIntent().getExtras().getString("contextmenuid");

                    if (isValidString(contextmenuID)) {
                        try {
                            parameters.put("contextmenuid", contextmenuID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (key.equals("menuid")) {

                    Log.d(TAG, "onCreate: menuid " + getIntent().getExtras().getString("menuid"));

                    String menuID = getIntent().getExtras().getString("menuid");

                    if (isValidString(menuID)) {
                        try {
                            parameters.put("menuid", menuID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (key.equals("contentid")) {

                    Log.d(TAG, "onCreate: contentid " + getIntent().getExtras().getString("contentid"));

                    String contentid = getIntent().getExtras().getString("contentid");

                    if (isValidString(contentid)) {
                        try {
                            parameters.put("contentid", contentid);
                            isFromPush = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (key.equals("siteid")) {

                    Log.d(TAG, "onCreate: siteid id " + getIntent().getExtras().getString("siteid"));

                    String siteid = getIntent().getExtras().getString("siteid");

                    if (isValidString(siteid)) {
                        try {
                            parameters.put("siteid", siteid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (key.equals("fourmid")) {

                    Log.d(TAG, "onCreate: fourmid " + getIntent().getExtras().getString("fourmid"));

                    String contextmenuID = getIntent().getExtras().getString("fourmid");

                    if (isValidString(contextmenuID)) {
                        try {
                            parameters.put("fourmid", contextmenuID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }


            }

        }

        Log.d(TAG, "onCreate: parameters" + parameters);

        webAPIClient = new WebAPIClient(this);
        getSiteConfigsAsycTask = new GetSiteConfigsAsycTask(this);
        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        if (Build.VERSION.SDK_INT > 22) {
            requestPermission();

        } else {

            callWebMethods();

        }
    }

    /*
    *
    * all view are initilize here like binding views to class
    *
    * */

    public void generateNotification() {


    }

    public void callWebMethods() {

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());


        if (isNetworkConnectionAvailable(context, -1)) {

            if (getSiteConfigsAsycTask.getStatus() == AsyncTask.Status.PENDING) {
                // My getSiteConfigsAsycTask is currently doing work in doInBackground()
                getSiteConfigsAsycTask.siteConfigInterface = this;
                getSiteConfigsAsycTask.execute(appUserModel.getSiteURL());

            } else {

                LogUtils.d("already running ");
            }
        } else {

            if (uiSettingsModel.getNativeAppType().equalsIgnoreCase("course app")) {

                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getResources().getString(R.string.alert_headtext_no_internet))
                        .setContentText(getResources().getString(R.string.alert_text_check_connection))
                        .show();

            } else {


                String userID = preferencesManager.getStringValue(StaticValues.KEY_USERID);

                if (userID != null && !userID.equalsIgnoreCase("")) {
                    appController.setAlreadyViewd(true);
                    preferencesManager.setStringValue("true", StaticValues.KEY_HIDE_ANNOTATION);
                    Intent intent = new Intent(this, SideMenu.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(this, Login_activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }


            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean checkPermission() {
        int CAMERA = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int READSTORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int WRITESTORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int WRITECALENDAR = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_CALENDAR);
        int READCALENDAR = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALENDAR);
        return READSTORAGE == PackageManager.PERMISSION_GRANTED && WRITESTORAGE == PackageManager.PERMISSION_GRANTED && CAMERA == PackageManager.PERMISSION_GRANTED && WRITECALENDAR == PackageManager.PERMISSION_GRANTED && READCALENDAR == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, WRITE_CALENDAR, READ_CALENDAR}, PERMISSION_REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeCalenderAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean readCalendarAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted && cameraAccepted && writeCalenderAccepted && readCalendarAccepted) {
//                        Toast.makeText(Splash_activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        callWebMethods();


                    } else {

//                        Toast.makeText(Splash_activity.this, "Permission Denied, You cannot login.", Toast.LENGTH_SHORT).show();

                    }
                }


                break;
        }
    }

    /*
    *  Interface classes
    *
    * */
    @Override
    public void preExecuteIn() {
        progressStatus = 0;
        progressBar.setProgress(0);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void progressUpdateIn(int status) {

        Log.d(TAG, "progressUpdateIn: " + status);

        if (status > 50) {
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
        progressBar.setProgress(status);

    }

    @Override
    public void postExecuteIn(String results) {

        if (isNetworkConnectionAvailable(context, -1)) {

            CmiSynchTask cmiSynchTask = new CmiSynchTask(context);
            cmiSynchTask.execute();
        }
        progressStatus = 100;
        progressBar.setProgress(progressStatus);
//        imageBrandLogo.clearAnimation();
        final ArrayList<String> imagesArray = new ArrayList<String>();
        String splashImagesPath = getExternalFilesDir(null) + "/Mydownloads/"
                + "SplashImages" + "";
        int i = 0;
        try {
            File[] files = null;
            files = new File(splashImagesPath).listFiles();

            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        imagesArray.add(splashImagesPath + "/" + file.getName());

                        i++;
                    }
                }
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String userID = preferencesManager.getStringValue(StaticValues.KEY_USERID);

        if (userID != null && !userID.equalsIgnoreCase("")) {

            Intent intent = new Intent(this, SideMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// push values
            intent.putExtra("PUSH", isFromPush);
            intent.putExtra(StaticValues.FCM_OBJECT, (Serializable) parameters.toString());
            startActivity(intent);

        } else {

            if (i > 0) {
                Intent intent = new Intent(this, Branding_activity.class);
                intent.putStringArrayListExtra("slideimages", imagesArray);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {

                Intent intent = new Intent(this, Login_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        }
    }
}
