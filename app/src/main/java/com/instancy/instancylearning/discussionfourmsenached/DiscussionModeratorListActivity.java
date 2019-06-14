package com.instancy.instancylearning.discussionfourmsenached;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class DiscussionModeratorListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = DiscussionModeratorListActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    Button btnApply;
    ResultListner resultListner = null;

    ListView discussionFourmlistView;

    DiscussionCommentsModelDg discussionCommentsModel;

    PreferencesManager preferencesManager;

    RelativeLayout relativeLayout;

    UiSettingsModel uiSettingsModel;

    Toolbar toolbar;

    DiscussionModeratorAdapter moderatorAdapter;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    View commentHeaderView;

    boolean refreshAnyThing = false;


    List<String> moderatorsList = null;

    List<DiscussionModeratorModel> discussionModeratorModelList = null;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, DiscussionModeratorListActivity.this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussionforumcategories);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();

        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);


        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);

        moderatorsList = (List<String>) getIntent().getSerializableExtra("moderatorIDArray");

        commentHeaderView = (RelativeLayout) findViewById(R.id.globalsearchheader);
        commentHeaderView.setVisibility(View.GONE);

//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
//                getLocalizationValue(JsonLocalekeys.discussionforum_label_moderatorpopuplabel).replace(":", "") + "</font>"));
        discussionModeratorModelList = new ArrayList<DiscussionModeratorModel>();
        discussionFourmlistView = (ListView) findViewById(R.id.chxlistview);
        moderatorAdapter = new DiscussionModeratorAdapter(this, BIND_ABOVE_CLIENT, discussionModeratorModelList);
        discussionFourmlistView.setAdapter(moderatorAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
        discussionFourmlistView.setEmptyView(findViewById(R.id.nodata_label));
        nodata_Label.setText("");
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        toolbar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'></font>"));
        toolbar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.discussionforum_label_moderatorpopuplabel).replace(":", "") + "</font>"));

        setSupportActionBar(toolbar);

        try {
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        if (isNetworkConnectionAvailable(this, -1)) {
            refreshMyLearning(false);
        } else {

        }

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomment, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
        applyUiColor(uiSettingsModel);
    }

    public void applyUiColor(UiSettingsModel uiSettingsModel) {

        btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(this);
        btnApply.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnApply.setVisibility(View.VISIBLE);
    }

    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/GetUserListBasedOnRoles?intSiteID=" + appUserModel.getSiteIDValue() + "&intUserID=" + appUserModel.getUserIDValue() + "&strLocale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name));

        vollyService.getStringResponseVolley("GetUserListBasedOnRoles", urlStr, appUserModel.getAuthHeaders());

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
                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                if (requestType.equalsIgnoreCase("GetUserListBasedOnRoles")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            discussionModeratorModelList = createDiscussionModeratorList(response);
                            if (moderatorsList != null && moderatorsList.size() > 0) {
                                getSelectedCategorylist(moderatorsList);
                            }
                            moderatorAdapter.refreshList(discussionModeratorModelList);
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
    public void onBackPressed() {
        finish();
    }

    public void closeForum(int position) {
        Intent intent = getIntent();
        if (position != -1) {
            intent.putExtra("ISSELECTED", true);
            intent.putExtra("moderatorModel", discussionModeratorModelList.get(position));
        } else {
            intent.putExtra("ISSELECTED", false);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    public void closeForum(String selectedModerators, List<String> selectedModeratorsIds) {
        Intent intent = getIntent();
        if (selectedModerators.length() > 0) {
            intent.putExtra("ISSELECTED", true);
            intent.putExtra("selectedModerators", selectedModerators);
            intent.putExtra("selectedModeratorsIds", (Serializable) selectedModeratorsIds);
            intent.putExtra("discussionModeratorModelList", (Serializable) discussionModeratorModelList);

            setResult(RESULT_OK, intent);
            finish();
        } else {
//            intent.putExtra("ISSELECTED", false);
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_selectmoderatoralert), Toast.LENGTH_SHORT).show();
        }
    }

    public void getSelectedCategorylist(List<String> selectedValues) {

        for (int i = 0; i < selectedValues.size(); i++) {

            for (int j = 0; j < discussionModeratorModelList.size(); j++) {

                if (Integer.parseInt(selectedValues.get(i)) == discussionModeratorModelList.get(j).userID) {

                    discussionModeratorModelList.get(j).isSelected = true;

                }

            }

        }
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
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));

//            txtSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

            // Set search view clear icon
            ImageView searchIconClearView = (ImageView) searchView
                    .findViewById(android.support.v7.appcompat.R.id.search_close_btn);

            if (searchIconClearView != null) {
                searchIconClearView.setImageResource(R.drawable.close);
            }
            // Does help!

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (isNetworkConnectionAvailable(DiscussionModeratorListActivity.this, -1)) {
                        if (query.length() == 0) {
                            Toast.makeText(DiscussionModeratorListActivity.this, getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_moderatorsearch), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(DiscussionModeratorListActivity.this, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();

                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

//                    moderatorAdapter.filter(newText.toLowerCase(Locale.getDefault()));
                    searchView.setFocusable(false);
                    filterSearch(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });


        }

        return true;
    }


    private void filterSearch(String charText) {
        //new array list that will hold the filtered data

        charText = charText.toLowerCase(Locale.getDefault());

        ArrayList<DiscussionModeratorModel> discussionModeratorModelArrayList = new ArrayList<>();

        //looping through existing elements
        for (DiscussionModeratorModel s : discussionModeratorModelList) {
            //if the existing elements contains the search input
            if (s.userName.toLowerCase(Locale.getDefault()).contains(charText) || s.userAddress.toLowerCase(Locale.getDefault()).contains(charText)) {
                discussionModeratorModelArrayList.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list

        if (discussionModeratorModelArrayList.size() == 0) {
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
        } else {
            nodata_Label.setText("");
        }

        moderatorAdapter.refreshList(discussionModeratorModelArrayList);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                closeForum(-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        DiscussionModeratorModel discussionModeratorModel = (DiscussionModeratorModel) adapterView.getItemAtPosition(i);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.chxBox);
        switch (view.getId()) {
            case R.id.card_view:
                //    closeForum(i);
                break;
            case R.id.chxBox:
                if (checkBox.isChecked()) {
                    updateListViewAtPosition(true, discussionModeratorModel);
                } else {
                    updateListViewAtPosition(false, discussionModeratorModel);
                }
                break;
            default:
        }
    }
    public void updateListViewAtPosition(boolean isChecked, DiscussionModeratorModel moderatorModel) {

        if (discussionModeratorModelList != null && discussionModeratorModelList.size() > 0) {

            for (int k = 0; k < discussionModeratorModelList.size(); k++) {
                if (discussionModeratorModelList.get(k).userID == moderatorModel.userID) {
                    discussionModeratorModelList.get(k).isSelected = isChecked;
                }
            }
//            moderatorAdapter.refreshList(discussionModeratorModelList);
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


    public List<DiscussionModeratorModel> createDiscussionModeratorList(String responseStr) throws JSONException {

        List<DiscussionModeratorModel> discussionForumModelList1 = new ArrayList<>();

        JSONArray jsonTableAry = new JSONArray(responseStr);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            DiscussionModeratorModel discussionModeratorModel = new DiscussionModeratorModel();

            discussionModeratorModel.userID = jsonMyLearningColumnObj.optInt("UserID");
            discussionModeratorModel.userThumb = jsonMyLearningColumnObj.optString("UserThumb");
            discussionModeratorModel.userName = jsonMyLearningColumnObj.optString("UserName");
            discussionModeratorModel.userDesg = jsonMyLearningColumnObj.optString("UserDesg");
            discussionModeratorModel.userAddress = jsonMyLearningColumnObj.optString("UserAddress");

            discussionForumModelList1.add(discussionModeratorModel);
        }
        return discussionForumModelList1;
    }

    @Override
    public void onClick(View view) {
        String selectedModerators = getSelectedModerators();
//        List<String> selectedModeratorsIds = getSelectedModeratorsIds();
        List<String> selectedModeratorsIds = getSelectedModeratorsIds();
        closeForum(selectedModerators, selectedModeratorsIds);
    }

    public List<String> getSelectedModeratorsIds() {
        List<String> generatedIdStr = new ArrayList<>();

        if (discussionModeratorModelList != null) {
            for (int i = 0; i < discussionModeratorModelList.size(); i++) {

                if (discussionModeratorModelList.get(i).isSelected) {
                    generatedIdStr.add("" + discussionModeratorModelList.get(i).userID);

                }
            }
        }

        return generatedIdStr;
    }


    public String getSelectedModerators() {
        String generatedStr = "";

        if (discussionModeratorModelList != null) {
            for (int i = 0; i < discussionModeratorModelList.size(); i++) {

                if (discussionModeratorModelList.get(i).isSelected) {
                    if (generatedStr.length() > 0) {
                        generatedStr = generatedStr.concat("," + discussionModeratorModelList.get(i).userName);
                    } else {
                        generatedStr = "" + discussionModeratorModelList.get(i).userName;
                    }
                }
            }
        }

        return generatedStr;
    }


//    public List<ContentValues> getSelectedCategories() {
//
//        List<ContentValues> selectedCategories = new ArrayList<ContentValues>();
//
//        if (discussionCategoriesModelList != null && discussionCategoriesModelList.size() > 0) {
//
//            discussionForumCategoriesAdapter.refreshList(discussionCategoriesModelList, false);
//
//            for (int i = 0; i < discussionCategoriesModelList.size(); i++) {
//
//                if (discussionCategoriesModelList.get(i).isSelected) {
//
//                    ContentValues cvBreadcrumbItem = new ContentValues();
//                    cvBreadcrumbItem.put("categoryid", discussionCategoriesModelList.get(i).categoryID);
//                    cvBreadcrumbItem.put("categoryname", discussionCategoriesModelList.get(i).fullName);
//                    selectedCategories.add(cvBreadcrumbItem);
//
//                }
//            }
//
//        }
//        return selectedCategories;
//
//    }


}
