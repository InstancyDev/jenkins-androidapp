package com.instancy.instancylearning.localization;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;

import android.text.Html;
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
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>Localization</font>"));

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

    public void getLocaleFileForLocalazation() {
        if (isNetworkConnectionAvailable(this, -1)) {

//            String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetForumLevelLikeList?strObjectID=" + forumModel.forumID + "&intUserID=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getUserIDValue() + "&strLocale=en-us";
//
//            vollyService.getStringResponseVolley("GetForumLevelLikeList", parmStringUrl, appUserModel.getAuthHeaders());

        } else {

            Toast.makeText(this, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

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
        switch (view.getId()) {


        }
    }
}
