package com.instancy.instancylearning.globalsearch;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.GLobalSearchSelectedModel;
import com.instancy.instancylearning.models.GlobalSearchCategoryModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.instancy.instancylearning.models.GlobalSearchCategoryModel.fetchCategoriesData;
import static com.instancy.instancylearning.models.GlobalSearchCategoryModel.getSelectedModelList;
import static com.instancy.instancylearning.models.GlobalSearchCategoryModel.isAllCheckedBoolMethod;
import static com.instancy.instancylearning.models.GlobalSearchCategoryModel.isParentComponentExists;
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

    LinearLayout bottomBtnLayout;

    CheckBox chxSelectedCategory;

    String responseReceived;

    boolean checkSelectedBool = false;
    boolean checkAllBool = false;

    SideMenusModel sideMenusModel = null;

    Toolbar toolbar;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, GlobalSearchActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.globalsearchactivity);
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(this);
        uiSettingsModel = UiSettingsModel.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);
        globalHeaderLayout = (RelativeLayout) findViewById(R.id.globalsearchheader);
        globalHeaderLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        bottomBtnLayout = (LinearLayout) findViewById(R.id.filter_btn_layout);
        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        bottomBtnLayout.setVisibility(View.GONE);
        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        toolbar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'></font>"));
//        toolbar.setTitle("My Toolbar");

        setSupportActionBar(toolbar);


        try {
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        chxListview = (ExpandableListView) findViewById(R.id.chxlistview);

        // Construct our adapter, using our own layout and myTeams

        expandableListTitle = new ArrayList<>();
        expandableListDetail = new HashMap<>();

        updateListView(true);

        chxListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;

            }
        });
        checkBoxFunctionality();
        chxListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.chxBox);

                if (checkBox.isChecked()) {

                    expandableListDetail = GlobalSearchCategoryModel.filterCategoriesOnCheck(expandableListDetail, expandableListTitle.get(groupPosition), childPosition, true);
                } else {
                    expandableListDetail = GlobalSearchCategoryModel.filterCategoriesOnCheck(expandableListDetail, expandableListTitle.get(groupPosition), childPosition, false);
                }
                updateListView(false);
                Log.d(TAG, "onChildClick: ");

                return false;

            }
        });


        if (isNetworkConnectionAvailable(this, -1)) {
            refreshCatagories(false);
        } else {
            Toast.makeText(this, JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.network_alerttitle_nointernet, this), Toast.LENGTH_SHORT).show();

        }
    }


    public void checkBoxFunctionality() {

        chxSelectedCategory = (CheckBox) globalHeaderLayout.findViewById(R.id.chxBox);
        chxSelectedCategory.setText(sideMenusModel.getDisplayName());
        chxSelectedCategory.setChecked(true);
        checkSelectedBool = true;
        chxAll = (CheckBox) globalHeaderLayout.findViewById(R.id.chxBoxAll);
        chxAll.setVisibility(View.VISIBLE);

        chxAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    checkAllBool = true;
                    chxSelectedCategory.setChecked(true);
                    checkSelectedBool = true;
                    try {
                        expandableListDetail = fetchCategoriesData(responseReceived, true, sideMenusModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

                    updateListView(true);
                } else {
                    chxSelectedCategory.setChecked(false);
                    checkSelectedBool = false;
                    checkAllBool = false;
                    try {
                        expandableListDetail = fetchCategoriesData(responseReceived, false, sideMenusModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                    updateListView(true);
                }


            }
        });

        chxSelectedCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {

                    checkSelectedBool = true;

                } else {

                    checkSelectedBool = false;

                }
            }
        });
        chxSelectedCategory.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
        chxAll.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
        chxSelectedCategory.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        chxAll.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

    }

    public void updateListView(boolean isListClicked) {
        if (isListClicked) {
            searchAdapter = new GlobalSearchAdapter(this, expandableListTitle, expandableListDetail, chxListview);
            chxListview.setAdapter(searchAdapter);

        } else {
            searchAdapter.refreshList(expandableListTitle, expandableListDetail);

        }

        if (expandableListDetail != null && expandableListDetail.size() > 0) {
            for (int i = 0; i < expandableListDetail.size(); i++)
                chxListview.expandGroup(i);
        }

        boolean isAllChecked = isAllCheckedBoolMethod(expandableListDetail);
        if (chxAll != null) {
            chxAll.setChecked(isAllChecked);
        }

    }

    public void refreshCatagories(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }
        String urlStr = appUserModel.getWebAPIUrl() + "search/GetSearchComponentList?";

        String paramsString = urlStr + "strLocale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "" +
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
                            expandableListDetail = fetchCategoriesData(response, false, sideMenusModel);
                            boolean parentExistss = isParentComponentExists(response, sideMenusModel);
                            if (!parentExistss) { // Commented for the search issue
//                                chxSelectedCategory.setVisibility(View.GONE);
//                                checkSelectedBool = false;
                            }
                            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                            responseReceived = response;
                            updateListView(true);
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
//          tintMenuIcon(getActivity(), item_search, R.color.colorWhite);.
            item_search.setTitle(getLocalizationValue(JsonLocalekeys.search_label));
            final SearchView searchView = (SearchView) item_search.getActionView();
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint(getLocalizationValue(JsonLocalekeys.commoncomponent_label_searchlabel));
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
//            txtSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

//            ImageView searchClose = searchView.findViewById(android.support.v7.appcompat.R.id.home);
//            searchClose.setImageResource(R.drawable.ic_filter_list_black_24dp);

            // Set search view clear icon
            ImageView searchIconClearView = (ImageView) searchView
                    .findViewById(android.support.v7.appcompat.R.id.search_close_btn);

            searchView.setFocusable(true);

            if (searchIconClearView != null) {

                searchIconClearView.setImageResource(R.drawable.close);

            }
            // Does help!

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);


//            item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//
//                @Override
//                public boolean onMenuItemActionExpand(MenuItem menuItem) {
//
////                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
//
//                    return true;
//                }
//
//                @Override
//                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
////                    getSupportActionBar().setHomeAsUpIndicator(upArrow);
//                    return true;
//                }
//            });


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    if (isNetworkConnectionAvailable(GlobalSearchActivity.this, -1)) {
                        checkEverything(query);

                    } else {
                        Toast.makeText(GlobalSearchActivity.this, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();

                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {


                    return true;
                }
            });
            searchView.setIconified(false);
        }

        return true;
    }

    public void checkEverything(String queryString) {
        List<GLobalSearchSelectedModel> gLobalSearchSelectedModelList = getSelectedModelList(expandableListDetail);

        if (checkSelectedBool) {
            GLobalSearchSelectedModel gLobalSearchSelectedModel = new GLobalSearchSelectedModel();
            gLobalSearchSelectedModel.menuId = sideMenusModel.getMenuId();
            gLobalSearchSelectedModel.siteID = Integer.parseInt(appUserModel.getSiteIDValue());
            gLobalSearchSelectedModel.componentID = Integer.parseInt(sideMenusModel.getComponentId());
            gLobalSearchSelectedModel.siteName = appUserModel.getSiteName();
            gLobalSearchSelectedModel.componentName = sideMenusModel.getContextTitle();
            gLobalSearchSelectedModel.contextMenuId = Integer.parseInt(sideMenusModel.getContextMenuId());

            try {
                gLobalSearchSelectedModel.componentInstancID = Integer.parseInt(sideMenusModel.getRepositoryId());
            } catch (NumberFormatException formatExc) {
                gLobalSearchSelectedModel.componentInstancID = 0;
            }


            gLobalSearchSelectedModelList.add(gLobalSearchSelectedModel);
        }

        Intent intent = new Intent(GlobalSearchActivity.this, GlobalSearchResultsActivity.class);
        intent.putExtra("queryString", queryString);
        intent.putExtra("globalsearchlist", (Serializable) gLobalSearchSelectedModelList);

        if (checkSelectedBool && gLobalSearchSelectedModelList.size() > 1) {

            startActivity(intent);

        } else if (gLobalSearchSelectedModelList.size() >= 1 && !checkSelectedBool) {

            startActivity(intent);

        } else if (checkSelectedBool) {

            gotoParentObj(queryString);

        } else {

            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.alert_nocat_selected), Toast.LENGTH_SHORT).show();
        }

//            if (checkSelectedBool || gLobalSearchSelectedModelList.size() > 0) {
//            Intent intent = new Intent(GlobalSearchActivity.this, GlobalSearchResultsActivity.class);
//            intent.putExtra("queryString", queryString);
//            intent.putExtra("globalsearchlist", (Serializable) gLobalSearchSelectedModelList);
//            startActivity(intent);
//
//        } else if (checkSelectedBool) {
//
//            Toast.makeText(this, "Single component selected ", Toast.LENGTH_SHORT).show();
//
//        } else {
//
//            Toast.makeText(this, getString(R.string.alert_nocat_selected), Toast.LENGTH_SHORT).show();
//        }
    }

    public void gotoParentObj(String queryString) {

        Intent intent = getIntent();
        intent.putExtra("queryString", queryString);
        setResult(RESULT_OK, intent);
        finish();


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
