package com.instancy.instancylearning.myskills;


import android.content.ContentValues;
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
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.google.gson.JsonObject;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.catalogfragment.CatalogFragmentActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


import static android.app.Activity.RESULT_OK;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


/**
 * Created by Upendranath on 5/19/2017.
 */

public class MySkillFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    String TAG = MySkillFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    MySkillDbTables db;

    @BindView(R.id.myskillList)
    ExpandableListView mySkillExpList;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    MySkillAdapter mySkillAdapter;

    List<MySkillModel> mySkillModelList = null;


    PreferencesManager preferencesManager;
    Context context;
    SideMenusModel sideMenusModel = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    JSONArray responseAry = null;
    View header;
    RelativeLayout layoutHeader;
    CustomFlowLayout tagsSkills;
    Spinner spnrCategory;
    private TextView addSkillText;
    List<String> categoriesList;
    private ArrayAdapter<String> spinnerAdapter;
    List<AddSkillModel> addSkillModelList = null;

    List<AddSkillModel> addSkillFilteredList = null;

    List<AddSkillCatModel> addSkillCatModelList = null;

    List<ContentValues> breadcrumbItemsList = null;

    public MySkillFragment() {


    }
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,getActivity());
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new MySkillDbTables(context);
        initVolleyCallback();
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        vollyService = new VollyService(resultCallback, context);
        sideMenusModel = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
        }

    }

    public void refreshMySkills(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/MySkills/GetSkilsData?SiteID=" + appUserModel.getSiteIDValue() + "&UserID=" + appUserModel.getUserIDValue() + "&ComponentID=" + sideMenusModel.getComponentId() + "&ComponentInstanceID=" + sideMenusModel.getRepositoryId();

        vollyService.getStringResponseVolley("GetSkilsData", urlStr, appUserModel.getAuthHeaders());

    }

    public void getSkiloption() {

        String urlStr = appUserModel.getWebAPIUrl() + "/MySkills/GetSkiloption";

        vollyService.getStringResponseVolley("GetSkiloption", urlStr, appUserModel.getAuthHeaders());

    }

    public void getAddSkillsdata() {

        String urlStr = appUserModel.getWebAPIUrl() + "/MySkills/GetAddSkillsdata?SiteID=" + appUserModel.getSiteIDValue() + "&UserID=" + appUserModel.getUserIDValue();

        vollyService.getStringResponseVolley("GetAddSkillsdata", urlStr, appUserModel.getAuthHeaders());

    }

    public void addSkillToMySkillApi(String preferrenceID) {

        String urlStr = appUserModel.getWebAPIUrl() + "/MySkills/AddToMySkills";

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("ComponentID", 1);
            parameters.put("PreferrenceID", preferrenceID);
            parameters.put("ExpertLevel", 1);
            parameters.put("UserID", appUserModel.getUserIDValue());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "AddToMySkills", urlStr);
    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                svProgressHUD.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);

                if (requestType.equalsIgnoreCase("GetSkilsData")) {
                    if (response != null && response.length() > 0) {
                        try {
                            db.injectMySkillData(response);
                            getSkiloption();
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                    }
                }
                if (requestType.equalsIgnoreCase("GetSkiloption")) {

                    if (response != null && response.length() > 0) {

//[{"LabelID":2,"LabelDescription":"Novice"},{"LabelID":1,"LabelDescription":"Intermediate"},{"LabelID":0,"LabelDescription":"Expert"}]
                        try {
                            responseAry = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mySkillAdapter.refreshList(mySkillModelList, responseAry);
                    }

                }

                if (requestType.equalsIgnoreCase("GetAddSkillsdata")) {
                    if (response != null && response.length() > 0) {

                        try {
                            addSkillModelList = generateaddSkillModelList(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }

                if (requestType.equalsIgnoreCase("AddToMySkills")) {
                    if (response != null && response.length() > 0) {

                        Log.d(TAG, "notifySuccess: " + response);
                        refreshMySkills(true);
                    }
                }
                if (requestType.equalsIgnoreCase("DeleteSkills")) {
                    if (response != null && response.length() > 0) {

                        Log.d(TAG, "notifySuccess: " + response);

                        if (response.equalsIgnoreCase("4")) {
                            refreshMySkills(true);
                        } else {
                            Toast.makeText(context, "Unable to Delete the selected skill", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();

            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {

                svProgressHUD.dismiss();
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<AddSkillModel> generateaddSkillModelList(String responseStr) throws JSONException {
        List<AddSkillModel> skillModelList = new ArrayList<>();
        addSkillCatModelList = new ArrayList<>();
        categoriesList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(responseStr);

        JSONObject jsonObject = jsonArray.getJSONObject(0);

        JSONArray jsonArrayCategories = jsonObject.getJSONArray("Withoutduplicate");

        JSONArray jsonArrayAddSkills = jsonObject.getJSONArray("Duplcatesall");

        if (jsonArrayAddSkills != null && jsonArrayAddSkills.length() > 0) {

            for (int i = 0; i < jsonArrayAddSkills.length(); i++) {

                JSONObject jsonObjectColum = jsonArrayAddSkills.getJSONObject(i);

                AddSkillModel addSkillModel = new AddSkillModel();

                addSkillModel.preferrencetitle = jsonObjectColum.optString("Preferrencetitle");
                addSkillModel.prefCategoryid = jsonObjectColum.optInt("PrefCategoryid");
                addSkillModel.prefCategoryTitleName = jsonObjectColum.optString("PrefCategoryTitleName");
                addSkillModel.preferenceId = jsonObjectColum.optInt("PreferenceId");
                addSkillModel.description = jsonObjectColum.optString("Description");


                skillModelList.add(addSkillModel);

            }

        }

        if (jsonArrayCategories != null && jsonArrayCategories.length() > 0) {

            for (int i = 0; i < jsonArrayCategories.length(); i++) {

                JSONObject jsonObjectColum = jsonArrayCategories.getJSONObject(i);

                AddSkillCatModel addSkillCatModel = new AddSkillCatModel();

                addSkillCatModel.prefCategoryTitleSkill = jsonObjectColum.optString("PrefCategoryTitleSkill");
                addSkillCatModel.prefCategoryIDSkill = jsonObjectColum.optInt("PrefCategoryIDSkill");
                categoriesList.add(addSkillCatModel.prefCategoryTitleSkill);
                addSkillCatModelList.add(addSkillCatModel);

            }

        }

        spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        spnrCategory.setAdapter(spinnerAdapter);

        return skillModelList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.myskill_fragment, container, false);
        header = (View) getLayoutInflater().inflate(R.layout.myskillheaderview, null);
        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        mySkillModelList = new ArrayList<MySkillModel>();
        mySkillAdapter = new MySkillAdapter(getActivity(), mySkillModelList, "", appUserModel, sideMenusModel, mySkillExpList);
        mySkillExpList.setAdapter(mySkillAdapter);
        mySkillExpList.setOnItemClickListener(this);
        mySkillExpList.setEmptyView(rootView.findViewById(R.id.nodata_label));

        mySkillExpList.addHeaderView(header, null, false);
        mySkillExpList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                switch (view.getId()) {
                    case R.id.btn_contextmenu:
                        SkillCountModel childSkillModel = mySkillModelList.get(i).skillCountModelList.get(i1);
                        mySkillContextMenu(view, i, childSkillModel);
                        break;

                }
                return false;
            }
        });
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshMySkills(false);
        } else {
            injectFromDbtoModel();
        }

        initilizeView();
        initilizeHeaderView();
        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconforum, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(context, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        return rootView;
    }

    public void injectFromDbtoModel() {

        mySkillModelList = db.fetchMySkillsData(appUserModel.getSiteIDValue());

        if (mySkillModelList != null && mySkillModelList.size() > 0) {
            mySkillAdapter.refreshList(mySkillModelList, responseAry);

        } else {
            mySkillModelList = new ArrayList<MySkillModel>();
            mySkillAdapter.refreshList(mySkillModelList, responseAry);
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
        }
    }

    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +sideMenusModel.getDisplayName()+ "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void initilizeHeaderView() {

        layoutHeader = (RelativeLayout) header.findViewById(R.id.layoutHeader);
        TextView txtSearch = (TextView) header.findViewById(R.id.txtSearch);
        txtSearch.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(context, R.string.fa_icon_search, uiSettingsModel.getAppTextColor()), null, null, null);
        txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        tagsSkills = (CustomFlowLayout) header.findViewById(R.id.cflBreadcrumb);

        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAddSkill();

            }
        });


        addSkillCatModelList = new ArrayList<>();

        addSkillModelList = new ArrayList<>();

        categoriesList = new ArrayList<>();


        spnrCategory = (Spinner) header.findViewById(R.id.spnrSkillCategory);


        spnrCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int spnrPosition,
                                       long id) {

                filterTheSelectedSkill(categoriesList.get(spnrPosition));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    public void searchAddSkill() {
        Intent intentDetail = new Intent(context, MySkillsAddListActivity.class);
        intentDetail.putExtra("addSkillFilteredList", (Serializable) addSkillFilteredList);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
    }

    public List<AddSkillModel> filterTheSelectedSkill(String selectedSkill) {

        List<AddSkillModel> skillModelListL = new ArrayList<>();

        if (addSkillCatModelList != null && addSkillCatModelList.size() > 0) {
            for (int i = 0; i < addSkillCatModelList.size(); i++) {

                if (selectedSkill.equalsIgnoreCase(addSkillCatModelList.get(i).prefCategoryTitleSkill)) {

                    int catID = addSkillCatModelList.get(i).prefCategoryIDSkill;
                    addSkillFilteredList = new ArrayList<>();
                    if (addSkillModelList != null && addSkillModelList.size() > 0) {
                        for (int k = 0; k < addSkillModelList.size(); k++) {
                            if (catID == addSkillModelList.get(k).prefCategoryid) {
                                skillModelListL.add(addSkillModelList.get(k));
                                addSkillFilteredList.add(addSkillModelList.get(k));
                            }
                        }
                    }
                }
            }


        }

        if (skillModelListL != null && skillModelListL.size() > 0) {

            breadcrumbItemsList = new ArrayList<ContentValues>();
            breadcrumbItemsList = generateTagsList(skillModelListL);

            if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {
                generateBreadcrumb(breadcrumbItemsList);

            }

        }

        return skillModelListL;
    }


    public List<ContentValues> generateTagsList(List<AddSkillModel> skillModelList) {
        List<ContentValues> tagsList = new ArrayList<>();

        int breadSkillLimit = 4;
        if (skillModelList.size() < 4)
            breadSkillLimit = skillModelList.size();
        else
            breadSkillLimit = 4;

        for (int i = 0; i < breadSkillLimit; i++) {
            ContentValues cvBreadcrumbItem = new ContentValues();
            cvBreadcrumbItem.put("categoryid", skillModelList.get(i).prefCategoryid);
            cvBreadcrumbItem.put("categoryname", skillModelList.get(i).preferrencetitle);
            cvBreadcrumbItem.put("preferenceId", skillModelList.get(i).preferenceId);
            tagsList.add(cvBreadcrumbItem);
        }

        return tagsList;
    }

    public void generateBreadcrumb(List<ContentValues> dicBreadcrumbItems) {
        boolean isFirstCategory = true;
        ContentValues cvBreadcrumbItem = null;
        // int lastCategory = 10;
        tagsSkills.removeAllViews();
        int breadcrumbCount = dicBreadcrumbItems.size();
        View.OnClickListener onBreadcrumbItemCLick = null;
        onBreadcrumbItemCLick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                String categoryId = tv.getTag(R.id.CATALOG_CATEGORY_ID_TAG)
                        .toString();
                int categoryLevel = Integer.valueOf(tv.getTag(
                        R.id.CATALOG_CATEGORY_LEVEL_TAG).toString());

                String categoryName = tv.getText().toString();

//                removeItemFromBreadcrumbList(categoryName.trim());
                removeItemFromBreadcrumbListByLevel(categoryLevel);
                generateBreadcrumb(breadcrumbItemsList);

            }
        };

        for (int i = 0; i < breadcrumbCount; i++) {
            if (i == 0) {
                isFirstCategory = true;
            } else {
                isFirstCategory = false;
            }

            TextView textView = new TextView(context);
            TextView arrowView = new TextView(context);

            Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(arrowView, iconFont);

            arrowView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppTextColor() + "'><medium><b>"
                    + context.getResources().getString(R.string.fa_icon_angle_right) + "</b></big> </font>"));

            arrowView.setTextSize(12);

            arrowView.setGravity(Gravity.CENTER | Gravity.LEFT);
            arrowView.setVisibility(View.GONE);
            // String text = coountries[i];
            cvBreadcrumbItem = dicBreadcrumbItems.get(i);
            String categoryId = cvBreadcrumbItem.getAsString("categoryid");
            String categoryName = cvBreadcrumbItem.getAsString("categoryname");

//            textView.setText(Html.fromHtml("<font color='" + context.getResources().getColor(R.color.colorInGreen) + "'><big><b>"
//                    + categoryName + "</b></small>  </font>"));

            textView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppTextColor() + "'><small>"
                    + categoryName + "<b> + </b>" + "</small>  </font>"));

            textView.setGravity(Gravity.CENTER | Gravity.CENTER);
            textView.setTag(R.id.CATALOG_CATEGORY_ID_TAG, categoryId);
            textView.setTag(R.id.CATALOG_CATEGORY_LEVEL_TAG, i);
            // textView.setBackgroundColor(R.color.alert_no_button);
//            textView.setBackgroundColor(context.getResources().getColor(R.color.colorDarkGrey));
            textView.setBackground(context.getResources().getDrawable(R.drawable.cornersround));
            textView.setOnClickListener(onBreadcrumbItemCLick);
            textView.setClickable(true);
            if (!isFirstCategory) {
                tagsSkills.addView(arrowView, new CustomFlowLayout.LayoutParams(
                        CustomFlowLayout.LayoutParams.WRAP_CONTENT, 50));
            }
            tagsSkills.addView(textView, new CustomFlowLayout.LayoutParams(
                    CustomFlowLayout.LayoutParams.WRAP_CONTENT, CustomFlowLayout.LayoutParams.WRAP_CONTENT));

        }

    }

    public void removeItemFromBreadcrumbListByLevel(int level) {

        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {

            addSkillToMySkillApi(breadcrumbItemsList.get(level).getAsString("preferenceId"));
            breadcrumbItemsList.remove(level);
        }

    }

//    public void removeItemFromBreadcrumbList(String categoryName) {
//
//        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {
//
//            for (int i = 0; i < breadcrumbItemsList.size(); i++) {
//
//                if (categoryName.equalsIgnoreCase(breadcrumbItemsList.get(i).getAsString("categoryname"))) {
//
//                    breadcrumbItemsList.remove(i);
//                }
//
//            }
//        }
//    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.myskillmenu, menu);
//        https://stablekernel.com/using-custom-views-as-menu-items/
        final MenuItem addSkillMenu = menu.findItem(R.id.add_skill_menu);
        addSkillMenu.setVisible(true);
        FrameLayout rootView = (FrameLayout) addSkillMenu.getActionView();
        addSkillText = (TextView) rootView.findViewById(R.id.txtaddskill);
        addSkillMenu.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + getLocalizationValue(JsonLocalekeys.mycompetency_label_addskilslabel) + "</font>"));
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(addSkillMenu);
            }
        });

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_skill_menu:
                if (layoutHeader.getVisibility() == View.VISIBLE) {
                    // Its visible
                    layoutHeader.setVisibility(View.GONE);
                } else {
                    // Either gone or invisible
                    layoutHeader.setVisibility(View.VISIBLE);
                    getAddSkillsdata();

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshMySkills(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK) {
            if (data != null) {
                boolean refresh = data.getBooleanExtra("ISSELECTED", false);
                if (refresh) {
                    AddSkillModel addSkillModel = (AddSkillModel) data.getSerializableExtra("addskilllmodel");

                    addSkillToMySkillApi("" + addSkillModel.preferenceId);
                }
            }
        }
    }

    public void mySkillContextMenu(final View v, final int position, final SkillCountModel skillCountModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.myskillcontextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);

        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.myskills_actionsheet_viewcontentoption));//view  ctx_view
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.myskills_actionsheet_deleteoption));//add   ctx_add

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.ctx_viewcontent:
                        Intent intentDetail = new Intent(context, CatalogFragmentActivity.class);
                        intentDetail.putExtra("SIDEMENUMODEL", sideMenusModel);
                        intentDetail.putExtra("TITLENAME", mySkillModelList.get(position).skillName);
//                        intentDetail.putExtra("SKILLID", skillModelList.get(position).sk);
                        intentDetail.putExtra("ISFROMMYCOMPETENCY", true);
                        startActivity(intentDetail);
                        break;
                    case R.id.ctx_delete:
                        if (isNetworkConnectionAvailable(context, -1)) {
                            deleteMySkill(skillCountModel.prefCatID);
                        } else {

                            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                        }

                        break;

                }

                return true;
            }
        });
        popup.show();//showing popup menu
    }


    public void deleteMySkill(int preferrenceID) {

        String urlStr = appUserModel.getWebAPIUrl() + "/MySkills/DeleteSkills";

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("PreferrenceID", preferrenceID);
            parameters.put("UserID", appUserModel.getUserIDValue());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "DeleteSkills", urlStr);
    }

}