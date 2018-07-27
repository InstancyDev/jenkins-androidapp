package com.instancy.instancylearning.globalsearch;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.GlobalSearchCategoryModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.instancy.instancylearning.models.GlobalSearchCategoryModel.fetchCategoriesData;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class GlobalSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = GlobalSearchActivity.class.getSimpleName();
    ExpandableListView chxListview;
    GlobalSearchAdapter searchAdapter;
    AppUserModel appUserModel;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    AppController appcontroller;

    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;

    HashMap<String, List<GlobalSearchCategoryModel>> expandableListDetail;
    List<String> expandableListTitle;

    RelativeLayout globalHeaderLayout;
    CheckBox chxAll;

    CheckBox chxSelectedCategory;

    String responseReceived;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.globalsearchactivity);
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(this);
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);
        globalHeaderLayout = (RelativeLayout) findViewById(R.id.globalsearchheader);


        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'></font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        chxListview = (ExpandableListView) findViewById(R.id.chxlistview);


        // Construct our adapter, using our own layout and myTeams

        expandableListTitle = new ArrayList<>();
        expandableListDetail = new HashMap<>();

        updateListView();

        chxListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;

            }
        });

        chxListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                return false;

            }
        });


        checkBoxFunctionality();

        if (isNetworkConnectionAvailable(this, -1)) {
            refreshCatagories(false);
        } else {
            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }
    }


    public void checkBoxFunctionality() {

        chxSelectedCategory = (CheckBox) globalHeaderLayout.findViewById(R.id.chxBox);
        chxAll = (CheckBox) globalHeaderLayout.findViewById(R.id.chxBoxAll);
        chxAll.setVisibility(View.VISIBLE);

        chxAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {

                    try {
                        expandableListDetail = fetchCategoriesData(responseReceived, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

                    updateListView();
                } else {


                    try {
                        expandableListDetail = fetchCategoriesData(responseReceived, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                    updateListView();
                }


            }
        });

        chxSelectedCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {


                } else {


                }
            }
        });


    }


    public void updateListView() {
        searchAdapter = new GlobalSearchAdapter(this, expandableListTitle, expandableListDetail);
        chxListview.setAdapter(searchAdapter);
        if (expandableListDetail != null && expandableListDetail.size() > 0) {
            for (int i = 0; i < expandableListDetail.size(); i++)
                chxListview.expandGroup(i);
        }

    }

    public void refreshCatagories(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }
        String urlStr = appUserModel.getWebAPIUrl() + "search/GetSearchComponentList?";

        String paramsString = urlStr + "strLocale=en-us" +
                "&intSiteID=" + appUserModel.getSiteIDValue() +
                "&intUserID=" + appUserModel.getUserIDValue();


        vollyService.getStringResponseVolley("GLOBALCATG", paramsString, appUserModel.getAuthHeaders());

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("GLOBALCATG")) {
                    if (response != null) {
                        try {
                            Log.d(TAG, "notifySuccess: here " + response.get("SearchComponents"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }

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

                if (requestType.equalsIgnoreCase("GLOBALCATG")) {
                    if (response != null) {

                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            expandableListDetail = fetchCategoriesData(response, false);
                            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                            responseReceived = response;
                            updateListView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                }

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

        getMenuInflater().inflate(R.menu.globalsearchmenu, menu);
        MenuItem item_search = menu.findItem(R.id.globalsearch);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();

            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint("Search..");
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(GlobalSearchActivity.this, "Queried " + query, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {


                    return true;
                }
            });

        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Log.d("DEBUG", "onOptionsItemSelected: ");
                finish();
                return true;
            case R.id.globalsearch:

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");
    }
}