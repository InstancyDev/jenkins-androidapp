package com.instancy.instancylearning.mylearning;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.catalog.ContentViewAdapter;
import com.instancy.instancylearning.catalog.ContentViewTable;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://www.mysamplecode.com/2012/11/android-expandablelistview-search.html
 */

public class SessionViewActivity extends AppCompatActivity {

    ListView contenListView;
    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = SessionViewActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    MyLearningModel myLearningModel;
    SessionViewTable db;
    ResultListner resultListner = null;
    List<MyLearningModel> contentModelList;
    PreferencesManager preferencesManager;
    LinearLayout linearLayout;
    UiSettingsModel uiSettingsModel;

    SessionViewAdapter sessionViewAdapter;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, SessionViewActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentviewactivity);
        linearLayout = (LinearLayout) findViewById(R.id.layout_linear_detail);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        db = new SessionViewTable(this);
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        linearLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        contenListView = (ListView) findViewById(R.id.contentListView);
        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.details_label_sessionstitlelable) + "</font>"));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        if (isNetworkConnectionAvailable(this, -1)) {

            refreshContentlisting();

        } else {
            Toast.makeText(this, JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.network_alerttitle_nointernet, this), Toast.LENGTH_SHORT).show();

        }
        contentModelList = new ArrayList<>();
        sessionViewAdapter = new SessionViewAdapter(this, contentModelList);
        contenListView.setAdapter(sessionViewAdapter);

    }

    public void refreshContentlisting() {
        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String paramsString = "SiteURL=" + appUserModel.getSiteURL()
                + "&ContentID=" + myLearningModel.getContentID()
                + "&UserID=" + appUserModel.getUserIDValue()
                + "&SiteID=" + appUserModel.getSiteIDValue() + "&locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name));

        vollyService.getJsonObjResponseVolley("EVENTTRACKTABS", appUserModel.getWebAPIUrl() + "/EventTrackTabs/getEventSessionData?" + paramsString, appUserModel.getAuthHeaders());

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("EVENTTRACKTABS")) {

                    if (response != null) {
                        try {
//                                Toast.makeText(TrackList_Activity.this, "Related content", Toast.LENGTH_SHORT).show();
                            db.injectContentViewData(response, myLearningModel.getScoId());
                            injectFromDbtoModel();

                            svProgressHUD.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {

                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {

            }
        };
    }

    public void injectFromDbtoModel() {
        contentModelList = new ArrayList<MyLearningModel>();
        contentModelList = db.fetchContentViewData(myLearningModel.getScoId());
        if (contentModelList != null && contentModelList.size() > 0) {
            Log.d(TAG, "dataLoaded: " + contentModelList.size());
            sessionViewAdapter.refreshList(contentModelList);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.tracklistmenu, menu); MyCiti1989
//        MenuItem itemInfo = menu.findItem(R.id.tracklist_help);
//        Drawable myIcon = getResources().getDrawable(R.drawable.help);
//        itemInfo.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
//
//        itemInfo.setVisible(false);
        return true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

