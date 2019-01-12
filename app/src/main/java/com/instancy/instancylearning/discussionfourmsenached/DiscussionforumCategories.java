package com.instancy.instancylearning.discussionfourmsenached;

import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;

import com.instancy.instancylearning.globalpackage.AppController;

import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;

import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;
import java.util.Objects;


import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class DiscussionforumCategories extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = DiscussionforumCategories.class.getSimpleName();
    ListView chxListview;
    AppUserModel appUserModel;
    DiscussionFourmsDbTables db;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;


    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;

    DiscussionForumCategoriesAdapter discussionForumCategoriesAdapter;
    List<DiscussionCategoriesModel> discussionCategoriesModelList;

    RelativeLayout globalHeaderLayout;
    CheckBox chxAll;

    LinearLayout bottomBtnLayout;

    String responseReceived;

    boolean checkAllBool = false;

    SideMenusModel sideMenusModel = null;

    Toolbar toolbar;

    Button btnApply;

    List<ContentValues> selectedCategories = new ArrayList<ContentValues>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussionforumcategories);
        appUserModel = AppUserModel.getInstance();
        db = new DiscussionFourmsDbTables(this);
        uiSettingsModel = UiSettingsModel.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        preferencesManager = PreferencesManager.getInstance();
        svProgressHUD = new SVProgressHUD(this);

        vollyService = new VollyService(resultCallback, this);
        globalHeaderLayout = (RelativeLayout) findViewById(R.id.globalsearchheader);
        globalHeaderLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        bottomBtnLayout = (LinearLayout) findViewById(R.id.filter_btn_layout);
//      sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        boolean refresh = getIntent().getBooleanExtra("FILTER", false);

        if (refresh) {
            selectedCategories = (List<ContentValues>) getIntent().getExtras().getSerializable("breadcrumbItemsList");
        }
        discussionCategoriesModelList = new ArrayList<>();
        bottomBtnLayout.setVisibility(View.VISIBLE);
        btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(this);
        btnApply.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
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

        chxListview = (ListView) findViewById(R.id.chxlistview);

        discussionForumCategoriesAdapter = new DiscussionForumCategoriesAdapter(this, discussionCategoriesModelList);
        chxListview.setAdapter(discussionForumCategoriesAdapter);
        chxListview.setOnItemClickListener(this);

        discussionCategoriesModelList = db.fetchDiscussionCategories(appUserModel.getSiteIDValue());
        if (discussionCategoriesModelList != null && discussionCategoriesModelList.size() > 0) {
            discussionForumCategoriesAdapter.refreshList(discussionCategoriesModelList);
        }

        checkBoxFunctionality();

        if (refresh) {
            getSelectedCategorylist(selectedCategories);
            discussionForumCategoriesAdapter.refreshList(discussionCategoriesModelList);
        }
    }

    public void getSelectedCategorylist(List<ContentValues> selectedValues) {


        for (int i = 0; i < selectedValues.size(); i++) {

            for (int j = 0; j < discussionCategoriesModelList.size(); j++) {

                if (selectedValues.get(i).getAsInteger("categoryid") == discussionCategoriesModelList.get(j).categoryID) {

                    discussionCategoriesModelList.get(j).isSelected = true;

                }

            }

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
//          tintMenuIcon(getActivity(), item_search, R.color.colorWhite);.
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint("Search..");
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

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    discussionForumCategoriesAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    discussionForumCategoriesAdapter.refreshList(discussionCategoriesModelList);
                    return false;
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

        switch (v.getId()) {
            case R.id.btnApply:
                finishTheActivity();
                break;
            default:

        }
    }

    public void finishTheActivity() {
        List<ContentValues> selectedCategories = new ArrayList<ContentValues>();
        selectedCategories = getSelectedCategories();
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

    public List<ContentValues> getSelectedCategories() {

        List<ContentValues> selectedCategories = new ArrayList<ContentValues>();

        if (discussionCategoriesModelList != null && discussionCategoriesModelList.size() > 0) {

            discussionForumCategoriesAdapter.refreshList(discussionCategoriesModelList);

            for (int i = 0; i < discussionCategoriesModelList.size(); i++) {

                if (discussionCategoriesModelList.get(i).isSelected) {

                    ContentValues cvBreadcrumbItem = new ContentValues();
                    cvBreadcrumbItem.put("categoryid", discussionCategoriesModelList.get(i).categoryID);
                    cvBreadcrumbItem.put("categoryname", discussionCategoriesModelList.get(i).fullName);
                    selectedCategories.add(cvBreadcrumbItem);

                }
            }

        }
        return selectedCategories;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");
    }

    public void checkBoxFunctionality() {

        TextView bottomLine = (TextView) globalHeaderLayout.findViewById(R.id.bottomLine);
        CheckBox chxSelectedCategory = (CheckBox) globalHeaderLayout.findViewById(R.id.chxBox);
        bottomLine.setVisibility(View.GONE);
        chxSelectedCategory.setVisibility(View.GONE);
        chxAll = (CheckBox) globalHeaderLayout.findViewById(R.id.chxBoxAll);
        chxAll.setVisibility(View.VISIBLE);
        chxAll.setText("Check All");
        chxAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    checkAllBool = true;
                    chxAll.setText("Uncheck all");
                    updateListView(true);
                } else {
                    checkAllBool = false;
                    updateListView(false);
                    chxAll.setText("Check all");
                }

            }
        });

        chxAll.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
        chxAll.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

    }

    public void updateListView(boolean isChecked) {
        if (discussionCategoriesModelList != null && discussionCategoriesModelList.size() > 0) {

            for (int i = 0; i < discussionCategoriesModelList.size(); i++) {

                discussionCategoriesModelList.get(i).isSelected = isChecked;

            }

            discussionForumCategoriesAdapter.refreshList(discussionCategoriesModelList);
        }
    }

    public void updateListViewAtPosition(boolean isChecked, int position) {

        if (discussionCategoriesModelList != null && discussionCategoriesModelList.size() > 0) {
            discussionCategoriesModelList.get(position).isSelected = isChecked;
            discussionForumCategoriesAdapter.refreshList(discussionCategoriesModelList);
        }
        if (isALLChecked()) {
            chxAll.setChecked(true);
            checkAllBool = true;
            chxAll.setText("Uncheck all");

        }
    }


    public boolean isALLChecked() {
        boolean isAllChecked = true;

        if (discussionCategoriesModelList != null && discussionCategoriesModelList.size() > 0) {
            for (int i = 0; i < discussionCategoriesModelList.size(); i++) {

                if (!discussionCategoriesModelList.get(i).isSelected) {
                    isAllChecked = false;
                    break;
                }
            }
        }

        return isAllChecked;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.chxBox);
        switch (view.getId()) {
            case R.id.chxBox:

                if (checkBox.isChecked()) {
                    updateListViewAtPosition(true, i);
                } else {
                    updateListViewAtPosition(false, i);
                    chxAll.setChecked(false);
                    checkAllBool = false;
                    chxAll.setText("Check all");
                }
                break;
            default:


        }
    }
}
