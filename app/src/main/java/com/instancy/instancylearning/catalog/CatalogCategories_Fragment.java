package com.instancy.instancylearning.catalog;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.RecyclerViewClickListener;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CatalogCategoryButtonModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.StaticValues.CATALOG_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.Utilities.generateHashMap;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 11/27/2017.
 */

public class CatalogCategories_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    String TAG = CatalogCategories_Fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    private RecyclerViewClickListener clicklistener;
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    TextView nodata_Label;

    @BindView(R.id.catalog_recycler)
    RecyclerView recyclerView;

    PreferencesManager preferencesManager;
    String filterContentType = "", consolidationType = "all", sortBy = "", ddlsortBy = "publisheddate", breadcrumbtextcolor = "";
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    ResultListner resultListner = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    RecyclerView catalogRecycler;

    CustomFlowLayout category_breadcrumb = null;

    List<ContentValues> breadcrumbItemsList = null;

    List<CatalogCategoryButtonModel> categoryButtonModelList1;

    ButtonAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new DatabaseHandler(context);
        initVolleyCallback();
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        String isViewed = preferencesManager.getStringValue(StaticValues.KEY_HIDE_ANNOTATION);
//        synchData = new SynchData(context);
        if (isViewed.equalsIgnoreCase("true")) {
            appcontroller.setAlreadyViewd(true);
        } else {

            appcontroller.setAlreadyViewd(false);
        }
        vollyService = new VollyService(resultCallback, context);
        List<CatalogCategoryButtonModel> categoryButtonModelList;

        sideMenusModel = null;
        HashMap<String, String> responMap = null;
        HashMap<String, String> paramStrings = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
            responMap = generateConditionsHashmap(sideMenusModel.getConditions());
            paramStrings = generateParametersHashmap(sideMenusModel.getParameterStrings());
            Boolean isFromCatogories = bundle.getBoolean("ISFROMCATEGORIES");

            if (isFromCatogories) {

                breadcrumbItemsList = new ArrayList<ContentValues>();
                breadcrumbItemsList = (List<ContentValues>) bundle.getSerializable("breadicrumblist");
                generateBreadcrumb(breadcrumbItemsList);

            }

        }
        if (responMap != null && responMap.containsKey("Type")) {
            String consolidate = responMap.get("Type");
            if (consolidate.equalsIgnoreCase("consolidate")) {
                consolidationType = "consolidate";
            } else {

                // keep all
                consolidationType = "all";
            }
        } else {
            // No such key // keep all
            consolidationType = "all";
        }
        if (responMap != null && responMap.containsKey("sortby")) {
            sortBy = responMap.get("sortby");
        } else {
            // No such key
            sortBy = "";
        }

        if (paramStrings != null && paramStrings.containsKey("breadcrumbtextcolor")) {
            breadcrumbtextcolor = paramStrings.get("breadcrumbtextcolor");
        } else {
            // No such key
            breadcrumbtextcolor = uiSettingsModel.getAppHeaderColor();
        }

        if (responMap != null && responMap.containsKey("ddlSortList")) {
            ddlsortBy = getTheDatabaseColumnName(responMap.get("ddlSortList").toLowerCase());

        } else {
            // No such key
            ddlsortBy = "publisheddate";
        }

        if (responMap != null && responMap.containsKey("FilterContentType")) {
            filterContentType = responMap.get("FilterContentType");
        } else {
            // No such key
            filterContentType = "";
        }
    }

    public static String getTheDatabaseColumnName(String unformatedName) {
        String coloumnName = "publisheddate";
        switch (unformatedName) {
            case "contenttype":
                coloumnName = "objecttypeid";
                break;
            case "authordisplayname":
                coloumnName = "author";
                break;
            case "name":
                coloumnName = "coursename";
                break;
            case "publisheddate":
                coloumnName = "publisheddate";
                break;
            default:
                coloumnName = "publisheddate";
                break;
        }

        return coloumnName;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mAdapter.reloadAllContent(categoryButtonModelList1);
        mAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        if (container != null) {
            rootView = inflater.inflate(R.layout.cataloggrid_fragment, container, false);
            ButterKnife.bind(this, rootView);
            nodata_Label = (TextView) rootView.findViewById(R.id.nodata_label);
            swipeRefreshLayout.setOnRefreshListener(this);
            initilizeView();

            if (isNetworkConnectionAvailable(context, -1)) {

                if (CATALOG_FRAGMENT_OPENED_FIRSTTIME == 0) {
                    refreshCatalog(false);

                } else {
                    refreshCatalog(true);
                }
                refreshCatalogData();
            } else {

            }

            breadCrumbPlusButtonInit(rootView);

        }

        return rootView;
    }

    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");


    }

    @Override
    public void onPause() {
        super.onPause();

    }


    public void breadCrumbPlusButtonInit(View rootView) {

//        refreshPeopleListing(true);

        LinearLayout llCatalogGridCatageory = (LinearLayout) rootView.findViewById(R.id.llCatalogGridCatageory);
        llCatalogGridCatageory.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppBGColor())));
        category_breadcrumb = (CustomFlowLayout) rootView.findViewById(R.id.cflBreadcrumb);
        breadcrumbItemsList = new ArrayList<ContentValues>();
        addItemToBreadcrumbList("0", "All");

        llCatalogGridCatageory.setVisibility(View.VISIBLE);

        generateBreadcrumb(breadcrumbItemsList);

        catalogRecycler = (RecyclerView) rootView.findViewById(R.id.catalog_recycler);

        catalogRecycler.setHasFixedSize(true);

        categoryButtonModelList1 = new ArrayList<>();

        categoryButtonModelList1 = db.openNewCategoryDetailsFromSQLite(appUserModel.getSiteIDValue(), sideMenusModel.getComponentId());

        if (categoryButtonModelList1.size() == 0) {
            nodata_Label.setText(getResources().getString(R.string.no_data));
            nodata_Label.setVisibility(View.VISIBLE);
        } else {
            nodata_Label.setVisibility(View.GONE);
        }

        // specify an adapter (see also next example) 3250+
        mAdapter = new ButtonAdapter(categoryButtonModelList1, clicklistener);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            catalogRecycler.setLayoutManager(new GridLayoutManager(context, 2));
        } else {
            catalogRecycler.setLayoutManager(new GridLayoutManager(context, 5));
        }

        catalogRecycler.setAdapter(mAdapter);
        catalogRecycler.setVisibility(View.VISIBLE);

        final List<CatalogCategoryButtonModel> finalCategoryButtonModelList = categoryButtonModelList1;
        catalogRecycler.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                Log.d(TAG, "onClick: " + position);
                if (position == -1) {
                    position = 1;
                }
//                Toast.makeText(context, "positions " + finalCategoryButtonModelList.get(position).getCategoryName(), Toast.LENGTH_SHORT).show();

                List<CatalogCategoryButtonModel> categoryButtonModelList2 = db.openSubCategoryDetailsFromSQLite(appUserModel.getSiteIDValue(), sideMenusModel.getComponentId(), "" + finalCategoryButtonModelList.get(position).getCategoryId());

                if (categoryButtonModelList2.size() > 0) {
                    addItemToBreadcrumbList("" + finalCategoryButtonModelList.get(position).getCategoryId(), finalCategoryButtonModelList.get(position).getCategoryName());
                    generateBreadcrumb(breadcrumbItemsList);
                    mAdapter.reloadAllContent(categoryButtonModelList2);
                } else {

                    List<MyLearningModel> myLearningModelList = db.openCategoryContentDetailsFromSQLite("" + finalCategoryButtonModelList.get(position).getCategoryId(), sideMenusModel.getComponentId(), ddlsortBy);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    if (myLearningModelList.size() != 0) {

                        addItemToBreadcrumbList("" + finalCategoryButtonModelList.get(position).getCategoryId(), finalCategoryButtonModelList.get(position).getCategoryName());
                        generateBreadcrumb(breadcrumbItemsList);

                        Bundle bundle = new Bundle();

                        Catalog_fragment nextFrag = new Catalog_fragment();
                        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        bundle.putSerializable("sidemenumodel", sideMenusModel);
                        bundle.putSerializable("cataloglist", (Serializable) myLearningModelList);
                        try {
                            bundle.putSerializable("breadicrumblist", (Serializable) breadcrumbItemsList);
                        } catch (ClassCastException ex) {
                            ex.printStackTrace();
                        }

                        bundle.putBoolean("ISFROMCATEGORIES", true);

                        nextFrag.setArguments(bundle);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container_body, nextFrag)
                                .addToBackStack(BACK_STACK_ROOT_TAG)
                                .commit();

                    } else {

                        Toast.makeText(context, "  No Content found  ", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }));
    }

    public void refreshCatalogData() {
        String paramsString = "FilterCondition=" + filterContentType + "&SortCondition=" + sortBy + "&RecordCount=0&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=" + consolidationType + "&FilterID=-1&ComponentID=" + sideMenusModel.getComponentId() + "&CartID=&Locale=&CatalogPreferenceID=1&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=&DateOfMyLastAccess=&SingleBranchExpand=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1";

        vollyService.getJsonObjResponseVolley("CATALOGDATA", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileCatalogObjectsNew?" + paramsString, appUserModel.getAuthHeaders());
    }

    public void refreshMethod() {
        if (categoryButtonModelList1.size() == 0) {
            categoryButtonModelList1 = db.openNewCategoryDetailsFromSQLite(appUserModel.getSiteIDValue(), sideMenusModel.getComponentId());

            if (categoryButtonModelList1.size() == 0) {
                nodata_Label.setText(getResources().getString(R.string.no_data));
                nodata_Label.setVisibility(View.VISIBLE);
            } else {
                nodata_Label.setVisibility(View.GONE);
            }

            // specify an adapter (see also next example) 3250+
            mAdapter.reloadAllContent(categoryButtonModelList1);
            mAdapter.notifyDataSetChanged();
        }
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("CATALOGDATA")) {
                    if (response != null) {
                        try {
                            db.injectCatalogData(response, true,1);
                            refreshMethod();
//                            categoryButtonModelList1 = new ArrayList<>();
//                            categoryButtonModelList1 = db.openNewCategoryDetailsFromSQLite(appUserModel.getSiteIDValue(), sideMenusModel.getComponentId());
//                            mAdapter.reloadAllContent(categoryButtonModelList1);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                if (requestType.equalsIgnoreCase("CATALOGCATEGORIES")) {
                    if (response != null) {
                        try {
                            db.injectCatalogCategories(response, "" + sideMenusModel.getComponentId());

                            refreshMethod();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                svProgressHUD.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
                refreshMethod();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
                refreshMethod();
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {
                refreshMethod();
                svProgressHUD.dismiss();
            }
        };
    }

    public HashMap<String, String> generateConditionsHashmap(String conditions) {

        HashMap<String, String> responMap = null;
        if (conditions != null && !conditions.equals("")) {
            if (conditions.contains("#@#")) {
                String[] conditionsArray = conditions.split("#@#");
                int conditionCount = conditionsArray.length;
                if (conditionCount > 0) {
                    responMap = generateHashMap(conditionsArray);

                }
            }
        }
        return responMap;
    }

    public HashMap<String, String> generateParametersHashmap(String conditions) {

        HashMap<String, String> responMap = null;
        if (conditions != null && !conditions.equals("")) {
            if (conditions.contains("&")) {
                String[] conditionsArray = conditions.split("&");
                int conditionCount = conditionsArray.length;
                if (conditionCount > 0) {
                    responMap = generateHashMap(conditionsArray);

                }
            }
        }
        return responMap;
    }


// BreadCrumb code

    public void addItemToBreadcrumbList(String categoryId, String categoryName) {
        ContentValues cvBreadcrumbItem = new ContentValues();
        cvBreadcrumbItem.put("categoryid", categoryId);
        cvBreadcrumbItem.put("categoryname", categoryName);
        breadcrumbItemsList.add(cvBreadcrumbItem);
    }

    /**
     * To remove the items greater than the passed category level from
     * breadcrumb list.
     *
     * @param categoryLevel
     * @author Venu
     */
    public void removeItemFromBreadcrumbList(int categoryLevel) {
        breadcrumbItemsList = breadcrumbItemsList.subList(0, categoryLevel + 1);
    }

    public void generateBreadcrumb(List<ContentValues> dicBreadcrumbItems) {
        boolean isFirstCategory = true;
        ContentValues cvBreadcrumbItem = null;
        // int lastCategory = 10;
        category_breadcrumb.removeAllViews();
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
                if (categoryLevel == (breadcrumbItemsList.size() - 1)) {
                    return;
                }
                String categoryName = tv.getText().toString();
                // ShowAlert.alertOK(ctx, R.string.alert_headtext_No_internet,
                // "BreadCrumb item clicked..!!\nID: "
                // + categoryId + "\nName: "
                // + tv.getText().toString(),
                // R.string.alert_btntext_OK, false);

                if (categoryId.equals("0")) {

                    categoryButtonModelList1 = db.openNewCategoryDetailsFromSQLite(appUserModel.getSiteIDValue(), sideMenusModel.getComponentId());
                    mAdapter.reloadAllContent(categoryButtonModelList1);
                } else {
                    categoryButtonModelList1 = db.openSubCategoryDetailsFromSQLite(appUserModel.getSiteIDValue(), sideMenusModel.getComponentId(), categoryId);
                    mAdapter.reloadAllContent(categoryButtonModelList1);
                }
                removeItemFromBreadcrumbList(categoryLevel);
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
            TextView arrowView = new TextView(context);// &#8811;

            Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(arrowView, iconFont);

            arrowView.setText(Html.fromHtml("<font color='" + breadcrumbtextcolor + "'><medium><b>"
                    + getResources().getString(R.string.fa_icon_angle_right) + "</b></big> </font>"));

//            arrowView.setText(getResources().getString(R.string.fa_icon_forward));
//            arrowView.setTextColor(getResources().getColor(R.color.colorPrimary)));
            arrowView.setTextSize(20);

            arrowView.setGravity(Gravity.CENTER | Gravity.LEFT);
            // String text = coountries[i];
            cvBreadcrumbItem = dicBreadcrumbItems.get(i);
            String categoryId = cvBreadcrumbItem.getAsString("categoryid");
            String categoryName = cvBreadcrumbItem.getAsString("categoryname");

            textView.setText(Html.fromHtml("<font color='" + breadcrumbtextcolor + "'><big><b><u>"
                    + categoryName + "</u></b></big>  </font>"));

            textView.setGravity(Gravity.BOTTOM | Gravity.BOTTOM);
            textView.setTag(R.id.CATALOG_CATEGORY_ID_TAG, categoryId);
            textView.setTag(R.id.CATALOG_CATEGORY_LEVEL_TAG, i);
            // textView.setBackgroundColor(R.color.alert_no_button);
            textView.setOnClickListener(onBreadcrumbItemCLick);
            textView.setClickable(true);
            if (!isFirstCategory) {
                category_breadcrumb.addView(arrowView, new CustomFlowLayout.LayoutParams(
                        CustomFlowLayout.LayoutParams.WRAP_CONTENT, 50));
            }
            category_breadcrumb.addView(textView, new CustomFlowLayout.LayoutParams(
                    CustomFlowLayout.LayoutParams.WRAP_CONTENT, CustomFlowLayout.LayoutParams.WRAP_CONTENT));

        }

    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        String paramsString = "siteURL=" + appUserModel.getSiteURL() + "&componentId="
                + "" + sideMenusModel.getComponentId() + "&type=" + "cat";

        vollyService.getJsonObjResponseVolley("CATALOGCATEGORIES", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileBrowseByListNew?" + paramsString, appUserModel.getAuthHeaders());

    }

    @Override
    public void onRefresh() {
        if (isNetworkConnectionAvailable(context, -1)) {
            refreshCatalog(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    // for recycler view listners

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private RecyclerViewClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final RecyclerViewClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));

                Log.d(TAG, "onInterceptTouchEvent: ");
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void getBreadCrumbListFrom(List<ContentValues> dicBreadcrumbItems) {

        Log.d(TAG, "getBreadCrumbListFrom: " + dicBreadcrumbItems.size());
    }
}
