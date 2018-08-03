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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.GlobalSearchResultSynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.DataCallback;
import com.instancy.instancylearning.interfaces.GlobalSearchResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.GLobalSearchSelectedModel;
import com.instancy.instancylearning.models.GlobalSearchCategoryModel;
import com.instancy.instancylearning.models.GlobalSearchResultModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.instancy.instancylearning.models.GlobalSearchResultModel.fetchCategoriesData;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class GlobalSearchResultsActivity extends AppCompatActivity implements View.OnClickListener, GlobalSearchResultListner {

    private static final String TAG = GlobalSearchResultsActivity.class.getSimpleName();
    ExpandableListView chxListview;
    GlobalSearchResultsAdapter searchAdapter;
    AppUserModel appUserModel;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    AppController appcontroller;

    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;

    HashMap<String, List<GlobalSearchResultModel>> expandableListDetail;
    List<String> expandableListTitle;

    RelativeLayout globalHeaderLayout;
    LinearLayout bottomBtnLayout;

    CheckBox chxSelectedCategory;
    TextView bottomLine;
    String queryString = "";
    List<GLobalSearchSelectedModel> gLobalSearchSelectedModelList = null;
    GlobalSearchResultSynchTask globalSearchResultSynchTask;
    DataCallback dataCallback;

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
        vollyService = new VollyService(resultCallback, this);
        globalHeaderLayout = (RelativeLayout) findViewById(R.id.globalsearchheader);
        bottomBtnLayout = (LinearLayout) findViewById(R.id.filter_btn_layout);
        chxSelectedCategory = (CheckBox) globalHeaderLayout.findViewById(R.id.chxBox);
        bottomLine = (TextView) globalHeaderLayout.findViewById(R.id.bottomLine);
        chxSelectedCategory.setVisibility(View.GONE);
        bottomBtnLayout.setVisibility(View.GONE);
        bottomLine.setVisibility(View.GONE);

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();

        gLobalSearchSelectedModelList = (List<GLobalSearchSelectedModel>) getIntent().getSerializableExtra("globalsearchlist");
        queryString = getIntent().getStringExtra("queryString");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + queryString + "</font>"));


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

        updateListView(true);

        chxListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;

            }
        });

        chxListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {

                final GlobalSearchResultModel expandedListText = (GlobalSearchResultModel) searchAdapter.getChild(groupPosition, childPosition);

//                SideMenusModel sideMenusModel=db.getSideMenuModelForGlobalSearch(expandedListText.siteId,expandedListText.menuID);

                 Toast.makeText(GlobalSearchResultsActivity.this, "groupPosition: " + groupPosition + "childPosition: " + childPosition, Toast.LENGTH_SHORT).show();

                return false;

            }
        });

        if (isNetworkConnectionAvailable(this, -1)) {
            refreshCatagories(false);
        } else {
            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }
    }

    public void updateListView(boolean isListClicked) {
        if (isListClicked) {
            searchAdapter = new GlobalSearchResultsAdapter(this, expandableListTitle, expandableListDetail, chxListview);
            chxListview.setAdapter(searchAdapter);

        } else {
            searchAdapter.refreshList(expandableListTitle, expandableListDetail);

        }

        if (expandableListDetail != null && expandableListDetail.size() > 0) {
            for (int i = 0; i < expandableListDetail.size(); i++)
                chxListview.expandGroup(i);
        }

    }

    public void refreshCatagories(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }


        if (gLobalSearchSelectedModelList != null && gLobalSearchSelectedModelList.size() > 0) {

            globalSearchResultSynchTask = new GlobalSearchResultSynchTask(gLobalSearchSelectedModelList, appUserModel, queryString);
            globalSearchResultSynchTask.globalSearchResultListner = this;

            globalSearchResultSynchTask.execute();

            //            for (int i = 0; i < gLobalSearchSelectedModelList.size(); i++) {
//                String paramsString = urlStr + "pageIndex=1&pageSize=10&searchStr=" + queryString +
//                        "&source=0&type=0&fType=&fValue=&sortBy=PublishedDate&sortType=desc&keywords=&ComponentID=225&ComponentInsID=4021&UserID=" + appUserModel.getUserIDValue() +
//                        "&SiteID=" + appUserModel.getSiteIDValue() +
//                        "&OrgUnitID=" + appUserModel.getSiteIDValue() +
//                        "&Locale=en-us&AuthorID=-1&groupBy=PublishedDate" +
//                        "&objComponentList=" + gLobalSearchSelectedModelList.get(i).componentID + "&intComponentSiteID=" + gLobalSearchSelectedModelList.get(i).siteID;
//
//
//                final int finalI = i;
//                vollyService.getSearchResults(new DataCallback() {
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        Log.d(TAG, finalI + "onSuccess: " + result);
//                        if (gLobalSearchSelectedModelList.size() == finalI) {
//                            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
//                            Log.d(TAG, "onSuccess: loop is completed ");
//                        }
//                    }
//                }, paramsString);
//
//            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.globalsearchmenu, menu);
        MenuItem item_search = menu.findItem(R.id.globalsearch);

        item_search.setVisible(false);

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
                    Toast.makeText(GlobalSearchResultsActivity.this, "Queried " + query, Toast.LENGTH_SHORT).show();
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

    @Override
    public void loopCompleted(List<GlobalSearchResultModel> globalSearchResultModelList, String completed) {
        svProgressHUD.dismiss();
        Log.d(TAG, "loopCompleted: " + globalSearchResultModelList.size());
        expandableListDetail = fetchCategoriesData(globalSearchResultModelList);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        searchAdapter.refreshList(expandableListTitle, expandableListDetail);
        if (expandableListDetail != null && expandableListDetail.size() > 0) {
            for (int i = 0; i < expandableListDetail.size(); i++)
                chxListview.expandGroup(i);
        }
    }
}
