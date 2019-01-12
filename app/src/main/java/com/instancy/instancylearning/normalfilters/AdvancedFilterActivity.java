package com.instancy.instancylearning.normalfilters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.filter.FilterAdapter;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.FiltersApplyModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.instancy.instancylearning.utils.StaticValues.INNER_FILTER_CLOSE;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class AdvancedFilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AdvancedFilterActivity.class.getSimpleName();
    ExpandableListView expandableListView;
    List<String> expandableListTitle;
    HashMap<String, List<AdvancedFilterModel>> expandableListDetail;
    FilterAdapter filterAdapter;
    AppUserModel appUserModel;
    String sortName = "";
    String atributeConfigId = "";
    String innerCategoryId = "";
    String innerCategorryName = "";
    boolean typeOrder = false;
    int sortedPosition = -1;

    DatabaseHandler db;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    AppController appcontroller;
    Button btnApply, btnReset;

    int isFromMylearning = 0;

    String contentKey = "", groupKey = "";

    JSONObject jsonInnerValues;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(this);
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        jsonInnerValues = new JSONObject();
        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>Filters</font>"));

        isFromMylearning = getIntent().getIntExtra("isFrom", 1);

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            applyUiColor(uiSettingsModel);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        try {

            JSONObject jsonObject = db.fetchFilterObject(appUserModel, isFromMylearning);
            if (jsonObject != null) {
                HashMap<String, List<AdvancedFilterModel>> hashMap = FiltersSerilization.fetchFilterData(jsonObject);

                expandableListDetail = FiltersSerilization.fetchFilterData(jsonObject);
                FiltersSerilization.fetchFilterData(jsonObject);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListView = (ExpandableListView) findViewById(R.id.filter_list);
        // Construct our adapter, using our own layout and myTeams
        filterAdapter = new FilterAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(filterAdapter);

//        expandableListView.setSelector(R.color.colorBlack);

//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//
//                if (groupPosition == 1) {
//
//                    FiltersSerilization.ContentFilterByModel filterModel = expandableListDetail.get("Filter By").get(childPosition);
//                    Log.d(TAG, "onChildClick: " + filterModel.name);
//
//
//                    if (filterModel.name.contains("group")) {
//                        groupKey = "groupby";
//                    } else {
//                        contentKey = "contentkey";
//                    }
//
//                    Intent intent = new Intent(AdvancedFilterActivity.this, Filter_Inner_activity.class);
//                    intent.putExtra("filtermodel", filterModel);
//                    intent.putExtra("filtername", filterModel.name);
//                    startActivityForResult(intent, INNER_FILTER_CLOSE);
//
//                } else {
//                    int index = childPosition - parent.getFirstVisiblePosition();
//                    if (index == childPosition) {
//                        expandableListDetail.get("Sort By").get(childPosition).isSelected = true;
//
//
//                        if (expandableListDetail.get("Sort By").get(childPosition).isSorted) {
//                            expandableListDetail.get("Sort By").get(childPosition).isSorted = false;
//                            typeOrder = false;
//                            sortName = expandableListDetail.get("Sort By").get(childPosition).name.toLowerCase();
//                            atributeConfigId = expandableListDetail.get("Sort By").get(childPosition).attributeConfigId.toLowerCase();
//
//                        } else {
//                            expandableListDetail.get("Sort By").get(childPosition).isSorted = true;
//                            typeOrder = true;
//                            sortedPosition = childPosition;
//                            sortName = expandableListDetail.get("Sort By").get(childPosition).name.toLowerCase();
//                            atributeConfigId = expandableListDetail.get("Sort By").get(childPosition).attributeConfigId.toLowerCase();
//                        }
//
//                    }
//
//                    filterAdapter.notifyDataSetChanged();
//
//                }
//                if (childPosition == (parent.getSelectedItemPosition())) {
//                    // Start your new activity here.
//                    Log.d("TAG", "second push"); // 16
//                }
//                return true;
//            }
//        });
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Doing nothing
                return false;
            }
        });

        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

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


    public void applyUiColor(UiSettingsModel uiSettingsModel) {

        btnApply = (Button) findViewById(R.id.btnfilterapply);
        btnApply.setOnClickListener(this);

        btnReset = (Button) findViewById(R.id.btnfilterrest);
        btnReset.setOnClickListener(this);

        btnReset.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnApply.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnfilterapply:
                insertBundleValues(false);
                break;
            case R.id.btnfilterrest:
                insertBundleValues(true);
                break;
        }
    }

    public void insertBundleValues(boolean resetFilter) {

        Intent intent = getIntent();
        intent.putExtra("coursetype", sortName);
        intent.putExtra("FILTER", resetFilter);
        intent.putExtra("sortby", typeOrder);
        intent.putExtra("configid", atributeConfigId);
        intent.putExtra("categoryid", innerCategoryId);
        intent.putExtra("jsonInnerValues", (Serializable) jsonInnerValues.toString());

        FiltersApplyModel.sortSelected = sortedPosition;
        FiltersApplyModel.sortByasC = typeOrder;
        FiltersApplyModel.sortConfigID = atributeConfigId;

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");
        if (requestCode == INNER_FILTER_CLOSE && resultCode == RESULT_OK) {

            innerCategoryId = data.getStringExtra("categoryid");
            innerCategorryName = data.getStringExtra("filtername");
            String selectedGrpBy = data.getStringExtra("groupname");

            if (innerCategorryName.contains("Group By")) {
                try {
                    jsonInnerValues.put("group", selectedGrpBy);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    jsonInnerValues.put("contentype", innerCategoryId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
