package com.instancy.instancylearning.localization;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.google.gson.JsonObject;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.mainactivities.NativeSettings;
import com.instancy.instancylearning.mainactivities.Splash_activity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class LocalizationSelectionActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = LocalizationSelectionActivity.class.getSimpleName();
    ListView localizationListView;
    AppUserModel appUserModel;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    LocalizationSelectionAdapter localizationSelectionAdapter;
    List<LocalizationSelectionModel> localizationSelectionModelList;
    RelativeLayout globalHeaderLayout;
    LinearLayout bottomBtnLayout;
    Button btnApply;
    private String currentLocaleName, currentLocalDisplayName;
    public final int RequestPermissionCode = 1;
    LocalizationSelectionModel localizationSelectionModel;
    ArrayList<String> arrayList = new ArrayList<>();

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, LocalizationSelectionActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussionforumcategories);
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(this);
        uiSettingsModel = UiSettingsModel.getInstance();

        preferencesManager = PreferencesManager.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);
        globalHeaderLayout = (RelativeLayout) findViewById(R.id.globalsearchheader);
        globalHeaderLayout.setVisibility(View.GONE);
        bottomBtnLayout = (LinearLayout) findViewById(R.id.filter_btn_layout);
//      sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        boolean refresh = getIntent().getBooleanExtra("FILTER", false);

        localizationSelectionModelList = new ArrayList<>();
        localizationSelectionModelList = db.fetchLocalizationList(appUserModel.getSiteIDValue());

        bottomBtnLayout.setVisibility(View.GONE);
        btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(this);
        btnApply.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + getLocalizationValue(JsonLocalekeys.insidesettings_tablesection_headinglanguage) + "</font>"));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        localizationListView = (ListView) findViewById(R.id.chxlistview);
        localizationSelectionAdapter = new LocalizationSelectionAdapter(this, localizationSelectionModelList);
        localizationListView.setAdapter(localizationSelectionAdapter);
        localizationListView.setOnItemClickListener(this);

    }

    public void getLocaleFileForLocalazation(LocalizationSelectionModel localizationSelectionModel) {
        if (isNetworkConnectionAvailable(this, -1)) {
            currentLocalDisplayName = localizationSelectionModel.getLanguageName().replace("\"", "");
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
            String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetLocalizationFile?LocaleID=" + localizationSelectionModel.getLocale().replace("\"", "") + "&siteID=" + appUserModel.getSiteIDValue();
            vollyService.getStringResponseVolley("GetLocalizationFile", parmStringUrl, appUserModel.getAuthHeaders());
        } else {
            Toast.makeText(this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocaleStringsToUI() {
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + JsonLocalization.getInstance().getStringForKey(getResources().getString(R.string.localization), LocalizationSelectionActivity.this) + "</font>"));

    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                svProgressHUD.dismiss();


            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
                svProgressHUD.dismiss();
                if (!TextUtils.isEmpty(response)) {

                    JSONObject localData = null;
                    try {
                        localData = new JSONObject(response);

                        if (localData.has("status") && localData.getString("status").toLowerCase().contains("localization not found")) {
                            Toast.makeText(LocalizationSelectionActivity.this, localData.getString("status"), Toast.LENGTH_SHORT).show();
                        } else {
                            preferencesManager.setStringValue(currentLocaleName, getResources().getString(R.string.locale_name));
                            preferencesManager.setBooleanValue(true, getResources().getString(R.string.locale_changed));
                            preferencesManager.setBooleanValue(true, getResources().getString(R.string.locale_changed));
                            preferencesManager.setStringValue(currentLocalDisplayName, getResources().getString(R.string.locale_display_name));

                            JsonLocalization.saveLocaleFileToInternalStorage(response, currentLocaleName, LocalizationSelectionActivity.this);
                            // key is the default value returned if key is not found in json
                            Intent intentBranding = new Intent(LocalizationSelectionActivity.this, Splash_activity.class);
                            intentBranding.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentBranding);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {
                svProgressHUD.dismiss();
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


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

    public static Drawable setTintDrawable(Drawable drawable, @ColorInt int color) {
        drawable.clearColorFilter();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.invalidateSelf();
        Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapDrawable, color);
        return wrapDrawable;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnApply:
                finishTheActivity();
                break;
            default:

        }
    }

    public void finishTheActivity() {
        List<ContentValues> selectedCategories = new ArrayList<ContentValues>();

        if (selectedCategories != null && selectedCategories.size() > 0) {
            Intent intent = getIntent();
            intent.putExtra("selectedCategories", (Serializable) selectedCategories);
            intent.putExtra("FILTER", true);
            setResult(RESULT_OK, intent);
            finish();

        } else {

            Toast.makeText(this, " select atleast one category", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        localizationSelectionModel = (LocalizationSelectionModel) adapterView.getItemAtPosition(i);
        currentLocaleName = localizationSelectionModel.getLocale().replace("\"", "");
        if (checkPermission())
            getLocaleFileForLocalazation(localizationSelectionModel);
        else
            requestPermission();

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(LocalizationSelectionActivity.this, new
                String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (StoragePermission && RecordPermission) {
                        getLocaleFileForLocalazation(localizationSelectionModel);
                    } else {
                        Toast.makeText(LocalizationSelectionActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }
    }


}
