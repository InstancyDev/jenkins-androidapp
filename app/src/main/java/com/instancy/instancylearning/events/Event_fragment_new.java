package com.instancy.instancylearning.events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
import com.instancy.instancylearning.catalog.CatalogAdapter;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningDetail_Activity;
import com.instancy.instancylearning.utils.EndlessScrollListener;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CATALOG_CODE;
import static com.instancy.instancylearning.utils.StaticValues.EVENT_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.IAP_LAUNCH_FLOW_CODE;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.Utilities.ConvertToDate;
import static com.instancy.instancylearning.utils.Utilities.GetZeroTimeDate;
import static com.instancy.instancylearning.utils.Utilities.generateHashMap;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTimeInDate;
import static com.instancy.instancylearning.utils.Utilities.getLastDateOfMonth;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

import static com.instancy.instancylearning.utils.Utilities.returnEventCompleted;
import static com.instancy.instancylearning.utils.Utilities.showToast;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class Event_fragment_new extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, BillingProcessor.IBillingHandler, RadioGroup.OnCheckedChangeListener, CompactCalendarView.CompactCalendarViewListener {

    String TAG = Event_fragment_new.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    boolean isCalendarTabScrolled = false;
    ProgressBar progressBar;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.mylearninglistview)
    ListView myLearninglistView;

    CatalogAdapter catalogAdapter;
    List<MyLearningModel> catalogModelsList = null;
    List<MyLearningModel> contextMenuModelList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    String filterContentType = "", consolidationType = "all", sortBy = "", ddlSortList = "", ddlSortType = "";
    ResultListner resultListner = null;
    CmiSynchTask cmiSynchTask;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    BillingProcessor billingProcessor;
    boolean isFromCatogories = false;

    int pageIndex = 1, totalRecordsCount = 0, pageSize = 10;
    boolean isSearching = false;
    boolean userScrolled = false;

    @BindView(R.id.segmentedswitch)
    SegmentedGroup segmentedSwitch;

    @BindView(R.id.compactcalendar_view)
    CompactCalendarView compactCalendarView;

    @BindView(R.id.yearandmonth)
    TextView YearTitle;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    RadioButton upBtn, calenderBtn, pastBtn;

    String TABBALUE = "upcoming";
    String startDateStr = "", endDateStr = "";


    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    private SimpleDateFormat dateFormatToSendserver = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());


    private SimpleDateFormat getyearAndMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault());

    public Event_fragment_new() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new DatabaseHandler(context);
        initVolleyCallback();
        cmiSynchTask = new CmiSynchTask(context);
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        StaticValues.PASTCALLED = 0;
        StaticValues.CALENDARCALLED = 0;
        StaticValues.UPCOMINGCALLED = 0;
        String isViewed = preferencesManager.getStringValue(StaticValues.KEY_HIDE_ANNOTATION);
        if (isViewed.equalsIgnoreCase("true")) {
            appcontroller.setAlreadyViewd(true);
        } else {

            appcontroller.setAlreadyViewd(false);
        }
        vollyService = new VollyService(resultCallback, context);

        String apiKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxZKOgrgA0BACsUqzZ49Xqj1SEWSx/VNSQ7e/WkUdbn7Bm2uVDYagESPHd7xD6cIUZz9GDKczG/fkoShHZdMCzWKiq07BzWnxdSaWa4rRMr+uylYAYYvV5I/R3dSIAOCbbcQ1EKUp5D7c2ltUpGZmHStDcOMhyiQgxcxZKTec6YiJ17X64Ci4adb9X/ensgOSduwQwkgyTiHjklCbwyxYSblZ4oD8WE/Ko9003VrD/FRNTAnKd5ahh2TbaISmEkwed/TK4ehosqYP8pZNZkx/bMsZ2tMYJF0lBUl5i9NS+gjVbPX4r013Pjrnz9vFq2HUvt7p26pxpjkBTtkwVgnkXQIDAQAB";

        billingProcessor = new BillingProcessor(context, apiKey, this);

        sideMenusModel = null;
        HashMap<String, String> responMap = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
            responMap = generateConditionsHashmap(sideMenusModel.getConditions());

        }
        if (responMap != null && responMap.containsKey("Type")) {
            String consolidate = responMap.get("Type");
            if (consolidate.equalsIgnoreCase("consolidate")) {
                consolidationType = "Consolidate";
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
        if (responMap != null && responMap.containsKey("FilterContentType")) {
            filterContentType = responMap.get("FilterContentType");
        } else {
            // No such key
            filterContentType = "";
        }

        // added extra parameters
        if (responMap != null && responMap.containsKey("ddlSortList")) {
            ddlSortList = responMap.get("ddlSortList");
        } else {
            // No such key
            ddlSortList = "publisheddate";
        }

        if (responMap != null && responMap.containsKey("ddlSortType")) {
            ddlSortType = responMap.get("ddlSortType");
        } else {
            // No such key
            ddlSortType = "asc";
        }


    }

    public void refreshCatalog(Boolean isRefreshed) {

        if (isNetworkConnectionAvailable(getContext(), -1)) {

            if (!isRefreshed) {
                svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
            }
            String paramsString = "";
            if (!TABBALUE.equalsIgnoreCase("calendar")) {

                filterContentType = "";
                sortBy = "c.name%20asc";

                paramsString = "FilterCondition=" + filterContentType + "&SortCondition=" + sortBy + "&RecordCount=200&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=" + consolidationType + "&FilterID=-1&ComponentID=" + sideMenusModel.getComponentId() + "&CartID=&Locale=&CatalogPreferenceID=5&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=&DateOfMyLastAccess=&SingleBranchExpand=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&AdditionalParams=" + TABBALUE + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize + "&ddlSortType=" + ddlSortType + "&ddlSortList=" + ddlSortList;

            } else {

                filterContentType = "%20C.ObjectTypeID%20=%2070%20And%20C.bit4%20Is%20null%20";
                sortBy = "c.name%20asc";

//                paramsString = "FilterCondition=" + filterContentType + "&SortCondition=" + sortBy + "&RecordCount=300&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=" + consolidationType + "&FilterID=-1&ComponentID=" + sideMenusModel.getComponentId() + "&CartID=&Locale=&CatalogPreferenceID=5&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=&DateOfMyLastAccess=&SingleBranchExpand=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1" + "&ddlSortType=" + ddlSortType + "&ddlSortList=" + ddlSortList + "+&pageIndex=1&pageSize=100";

//                if (isValidString(uiSettingsModel.getcCEventStartdate())) {
//
//                    startDateStr = uiSettingsModel.getcCEventStartdate();
//                }

                paramsString = "FilterCondition=&SortCondition=" + ddlSortList + "&RecordCount=200&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=" + consolidationType + "&FilterID=-1&ComponentID=" + sideMenusModel.getComponentId() + "&CartID=&Locale=&CatalogPreferenceID=5&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=&DateOfMyLastAccess=&SingleBranchExpand=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1" + "&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&AdditionalParams=calendarview&pageIndex=1&pageSize=10&sortType=" + ddlSortType + "sortby" + ddlSortList + "&StartDate=" + startDateStr + "&EndDate=" + endDateStr;

            }
            vollyService.getJsonObjResponseVolley("CATALOGDATA", appUserModel.getWebAPIUrl() + "MobileLMS/MobileCatalogObjectsNew?" + paramsString, appUserModel.getAuthHeaders());

//            MenuItemCompat.collapseActionView(item_search);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            injectFromDbtoModel(true);
//            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
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

                        if (TABBALUE.equalsIgnoreCase("upcoming")) {
                            StaticValues.UPCOMINGCALLED = 1;
                        } else if (TABBALUE.equalsIgnoreCase("calendar")) {
                            pageIndex = 1;
                            StaticValues.CALENDARCALLED = 1;
                        } else if (TABBALUE.equalsIgnoreCase("past")) {
                            StaticValues.PASTCALLED = 1;
                        }
                        try {
                            db.injectEventCatalog(response, TABBALUE, pageIndex, sideMenusModel.getComponentId());
                            totalRecordsCount = countOfTotalRecords(response);
                            injectFromDbtoModel(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        nodata_Label.setText(getResources().getString(R.string.no_data));

                    }

                    svProgressHUD.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }

                svProgressHUD.dismiss();
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("FILTER")) {

                    Toast.makeText(getContext(), "Filter is not configured", Toast.LENGTH_SHORT).show();
                }

                if (requestType.equalsIgnoreCase("CATALOGDATA")) {

                    nodata_Label.setText(getResources().getString(R.string.no_data));
                }
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {
                if (requestType.equalsIgnoreCase("MLADP")) {

                    if (response != null) {
                        try {
                            db.injectCMIDataInto(response, myLearningModel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
            }
        };
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_fragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setEnabled(true);

        catalogAdapter = new CatalogAdapter(getActivity(), BIND_ABOVE_CLIENT, catalogModelsList, true);
        myLearninglistView.setAdapter(catalogAdapter);
        myLearninglistView.setOnItemClickListener(this);
        myLearninglistView.setEmptyView(rootView.findViewById(R.id.nodata_label));


        final View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loadmore, null, false);
        myLearninglistView.addFooterView(footerView);
        progressBar = (ProgressBar) footerView.findViewById(R.id.loadMoreProgressBar);

        myLearninglistView.setOnScrollListener(new EndlessScrollListener() {


            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolled
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                if (TABBALUE.equalsIgnoreCase("calendar")) {
                    progressBar.setVisibility(View.GONE);
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (totalItemCount <= totalRecordsCount && totalItemCount != 0) {

                    Log.d(TAG, "onLoadMore size: catalogModelsList" + catalogModelsList.size());

                    Log.d(TAG, "onLoadMore size: totalRecordsCount" + totalRecordsCount);

                    if (userScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                        userScrolled = false;

                        if (!isSearching && !TABBALUE.equalsIgnoreCase("calendar")) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (isNetworkConnectionAvailable(getContext(), -1)) {
                                refreshCatalog(true);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);

                        }

                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                }

            }


            @Override
            public void onLoadMore(int page, int totalItemsCount) {
            }
        });

        upBtn = (RadioButton) rootView.findViewById(R.id.upcomingbtn);
        upBtn.setChecked(true);

        calenderBtn = (RadioButton) rootView.findViewById(R.id.calanderbtn);
        pastBtn = (RadioButton) rootView.findViewById(R.id.pastbtn);

        upBtn.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        calenderBtn.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        pastBtn.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));

        upBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        calenderBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        pastBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        upBtn.setTypeface(null, Typeface.BOLD);
        segmentedSwitch.setOnCheckedChangeListener(this);
        segmentedSwitch.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        catalogModelsList = new ArrayList<MyLearningModel>();
        contextMenuModelList = new ArrayList<MyLearningModel>();
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshCatalog(false);
        } else {
            injectFromDbtoModel(true);
        }

        initilizeView();
        initilizeCalander();

        return rootView;
    }

    public void injectFromDbtoModel(boolean sortToUpcoming) {
        catalogModelsList = db.fetchEventCatalogModel(sideMenusModel.getComponentId(), TABBALUE, ddlSortList, ddlSortType);
        if (catalogModelsList != null) {
            catalogAdapter.refreshList(catalogModelsList);
        } else {
            catalogModelsList = new ArrayList<MyLearningModel>();
            catalogAdapter.refreshList(catalogModelsList);
            nodata_Label.setText(getResources().getString(R.string.no_data));
        }
        contextMenuModelList = new ArrayList<>();
        contextMenuModelList.addAll(catalogModelsList);

        if (catalogModelsList.size() == pageSize) {
            pageIndex = 2;
        } else {
            pageIndex = catalogModelsList.size() / pageSize;
            pageIndex = pageIndex + 1;
        }

        if (TABBALUE.equalsIgnoreCase("calendar") && catalogModelsList.size() > 0) {
            compactCalendarView.removeAllEvents();
            addEvents(catalogModelsList);
            String todayD = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
            onDayClick(getCurrentDateTimeInDate(todayD));
            progressBar.setVisibility(View.GONE);

            if (isCalendarTabScrolled) {
                onDayClick(compactCalendarView.getFirstDayOfCurrentMonth());
            }

        }
        dismissSvProgress();
    }

    public void initilizeCalander() {
        compactCalendarView.setListener(this);
        YearTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        isCalendarTabScrolled = false;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mylearning_menu, menu);
        item_search = menu.findItem(R.id.mylearning_search);
        MenuItem item_filter = menu.findItem(R.id.mylearning_filter);
        MenuItem itemInfo = menu.findItem(R.id.mylearning_info_help);

        itemInfo.setVisible(false);
        item_filter.setVisible(false);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);

//            searchView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
//            setCursorColor(txtSearch,Color.parseColor(uiSettingsModel.getAppLoginTextolor())); uncomment for cursor
            txtSearch.setHint("Search..");
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    catalogAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mylearning_search:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.toolbar, 1, true, true);
                else
                    toolbar.setVisibility(View.VISIBLE);
                item_search.expandActionView();
                break;
            case R.id.mylearning_info_help:
                Log.d(TAG, "onOptionsItemSelected :mylearning_info_help ");
                appcontroller.setAlreadyViewd(false);
                preferencesManager.setStringValue("false", StaticValues.KEY_HIDE_ANNOTATION);
                catalogAdapter.notifyDataSetChanged();
                break;
            case R.id.mylearning_filter:
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            pageIndex = 1;
            refreshCatalog(true);
            MenuItemCompat.collapseActionView(item_search);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.btntxt_download:
                if (isNetworkConnectionAvailable(context, -1)) {
                } else {
                    showToast(context, "No Internet");
                }
                break;
            case R.id.btn_contextmenu:
                View v = myLearninglistView.getChildAt(position - myLearninglistView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, catalogModelsList.get(position), uiSettingsModel, appUserModel);
                break;
            default:

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");
        EVENT_FRAGMENT_OPENED_FIRSTTIME = 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = getActivity().findViewById(viewID);
        int width = myView.getWidth();
        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);
        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);
        anim.setDuration((long) 400);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });
        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();

    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final MyLearningModel myLearningDetalData, final UiSettingsModel uiSettingsModel, final AppUserModel userModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.event_contextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene
        Menu menu = popup.getMenu();
        menu.getItem(0).setVisible(false);//view
        menu.getItem(1).setVisible(false);//enroll
        menu.getItem(2).setVisible(false);//buy
        menu.getItem(3).setVisible(false);//detail
        menu.getItem(4).setVisible(false);//cancel enrollment


//        boolean subscribedContent = databaseH.isSubscribedContent(myLearningDetalData);

        if (myLearningDetalData.getAddedToMylearning() == 1) {

            if (!myLearningDetalData.getRelatedContentCount().equalsIgnoreCase("0")) {
                menu.getItem(0).setVisible(true);
            }

            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);

            if (!returnEventCompleted(myLearningDetalData.getEventstartTime())) {
                menu.getItem(4).setVisible(true);
            }

            if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {

                File myFile = new File(myLearningDetalData.getOfflinepath());

                if (myFile.exists()) {

                    menu.getItem(4).setVisible(true);

                } else {

                    menu.getItem(4).setVisible(false);
                }
            }

        } else {
            if (myLearningDetalData.getViewType().equalsIgnoreCase("1")) {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(true);

                if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {

                    File myFile = new File(myLearningDetalData.getOfflinepath());

                    if (myFile.exists()) {

                        menu.getItem(4).setVisible(true);

                    } else {

                        menu.getItem(4).setVisible(false);
                    }
                }
            } else if (myLearningDetalData.getViewType().equalsIgnoreCase("2")) {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                menu.getItem(3).setVisible(true);
                if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {

                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
                        menu.getItem(4).setVisible(false);
                    }
                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {

                        menu.getItem(4).setVisible(true);
                    }
                }
                if (!uiSettingsModel.isAllowExpiredEventsSubscription() && returnEventCompleted(myLearningDetalData.getEventendUtcTime())){
                    menu.getItem(1).setVisible(false);
                }

//                if (myLearningDetalData.isCompletedEvent()){
//                    menu.getItem(1).setVisible(false);
//                    menu.getItem(4).setVisible(false);
//                }

            } else if (myLearningDetalData.getViewType().equalsIgnoreCase("3")) {
                menu.getItem(0).setVisible(false);
                menu.getItem(2).setVisible(true);
                menu.getItem(3).setVisible(true);
                menu.getItem(1).setVisible(false);
            }

        }

        // expired event functionality
        if (returnEventCompleted(myLearningDetalData.getEventendUtcTime()) && uiSettingsModel.isAllowExpiredEventsSubscription()) {

            if (myLearningDetalData.getAddedToMylearning() == 1) {

                if (!myLearningDetalData.getRelatedContentCount().equalsIgnoreCase("0")) {
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setVisible(false);//enroll

                }
            } else {
                if (myLearningDetalData.getViewType().equalsIgnoreCase("2") && uiSettingsModel.isAllowExpiredEventsSubscription()) {
                    menu.getItem(1).setVisible(true);//enroll

                }
            }

//                menu.getItem(0).setVisible(true);//view

            menu.getItem(2).setVisible(false);//buy
            menu.getItem(3).setVisible(true);//detail
            menu.getItem(4).setVisible(false);//cancel enrollment
        }


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.ctx_detail) {
                    Intent intentDetail = new Intent(v.getContext(), MyLearningDetail_Activity.class);
                    intentDetail.putExtra("IFROMCATALOG", true);
                    intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
//                    v.getContext().startActivity(intentDetail);
//                    v.getContext().startActivity(intentDetail);
//                   context.startActivityForResult(iWeb, COURSE_CLOSE_CODE);
                    ((Activity) v.getContext()).startActivityForResult(intentDetail, DETAIL_CATALOG_CODE);

                }


                if (item.getItemId() == R.id.ctx_view) {
//                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext());
                    GlobalMethods.relatedContentView(myLearningDetalData, v.getContext());
                }

                if (item.getTitle().toString().equalsIgnoreCase("Download")) {

                }

                if (item.getTitle().toString().equalsIgnoreCase(v.getResources().getString(R.string.btn_txt_cancel_enrolment))) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage(v.getResources().getString(R.string.canceleventmessage)).setTitle(v.getResources().getString(R.string.eventalert))
                            .setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.dismiss();
                            cancelEnrollment(myLearningDetalData);

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
                if (item.getTitle().toString().equalsIgnoreCase("Enroll")) {
                    if (isNetworkConnectionAvailable(context, -1)) {

                        if (uiSettingsModel.isAllowExpiredEventsSubscription() && returnEventCompleted(myLearningDetalData.getEventendUtcTime())){

                            try {
                                addExpiryEvets(myLearningDetalData, position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            addToMyLearningCheckUser(myLearningDetalData, position, false);
                        }
                    }

                    Log.d(TAG, "onMenuItemClick:  Enroll here");


                }
                if (item.getTitle().toString().equalsIgnoreCase("Buy")) {
//                    addToMyLearningCheckUser(myLearningDetalData, position, true);
//
//                    Toast.makeText(context, "Buy here", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        popup.show();//showing popup menu

    }

    public void addToMyLearningCheckUser(MyLearningModel myLearningDetalData, int position, boolean isInapp) {

        if (isNetworkConnectionAvailable(context, -1)) {

            if (myLearningDetalData.getUserID().equalsIgnoreCase("-1")) {

                checkUserLogin(myLearningDetalData, position, isInapp);
            } else {

//                if (isInapp) {
//                    inAppActivityCall(myLearningDetalData);
//
//                } else {
                addToMyLearning(myLearningDetalData, position);
//
//                }

            }
        }
    }

    public void addToMyLearning(final MyLearningModel myLearningDetalData, final int position) {

        if (isNetworkConnectionAvailable(context, -1)) {
            boolean isSubscribed = db.isSubscribedContent(myLearningDetalData);
            if (isSubscribed) {
                Toast toast = Toast.makeText(
                        context,
                        context.getString(R.string.cat_add_already),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                String requestURL = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileAddtoMyCatalog?"
                        + "UserID=" + myLearningDetalData.getUserID() + "&SiteURL=" + myLearningDetalData.getSiteURL()
                        + "&ContentID=" + myLearningDetalData.getContentID() + "&SiteID=" + myLearningDetalData.getSiteID();

                requestURL = requestURL.replaceAll(" ", "%20");
                Log.d(TAG, "inside catalog login : " + requestURL);

                StringRequest strReq = new StringRequest(Request.Method.GET,
                        requestURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "add to mylearning data " + response.toString());
                        if (response.equalsIgnoreCase("true")) {
//                            contextMenuModelList.get(position).setAddedToMylearning(1);
                            getMobileGetMobileContentMetaData(myLearningDetalData, position, true);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.event_add_success))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            dialog.dismiss();
                                            // add event to android calander
//                                            addEventToAndroidDevice(myLearningDetalData);
                                            db.updateEventAddedToMyLearningInEventCatalog(myLearningDetalData, 1);
                                            injectFromDbtoModel(true);

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            Toast toast = Toast.makeText(
                                    context, "Unable to process request",
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        final Map<String, String> headers = new HashMap<>();
                        String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                        headers.put("Authorization", "Basic " + base64EncodedCredentials);
                        return headers;
                    }
                };
                VolleySingleton.getInstance(context).addToRequestQueue(strReq);
            }

        }
    }

    public void checkUserLogin(final MyLearningModel learningModel, final int position, final boolean isInapp) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                + learningModel.getUserName() + "&Password=" + learningModel.getPassword() + "&MobileSiteURL="
                + appUserModel.getSiteURL() + "&DownloadContent=&SiteID=" + appUserModel.getSiteIDValue();

        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "inside catalog login : " + urlStr);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        svProgressHUD.dismiss();
                        if (jsonObj.has("faileduserlogin")) {

                            JSONArray userloginAry = null;
                            try {
                                userloginAry = jsonObj
                                        .getJSONArray("faileduserlogin");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (userloginAry.length() > 0) {


                                String response = null;
                                try {
                                    response = userloginAry
                                            .getJSONObject(0)
                                            .get("userstatus")
                                            .toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (response.contains("Login Failed")) {
                                    Toast.makeText(context,
                                            "Authentication Failed. Contact site admin",
                                            Toast.LENGTH_LONG)
                                            .show();

                                }
                                if (response.contains("Pending Registration")) {

                                    Toast.makeText(context, "Please be patient while awaiting approval. You will receive an email once your profile is approved.",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        } else if (jsonObj.has("successfulluserlogin")) {

                            try {
                                JSONArray loginResponseAry = jsonObj.getJSONArray("successfulluserlogin");
                                if (loginResponseAry.length() != 0) {
                                    JSONObject jsonobj = loginResponseAry.getJSONObject(0);

                                    String userIdresponse = loginResponseAry
                                            .getJSONObject(0)
                                            .get("userid").toString();
                                    if (userIdresponse.length() != 0) {

//                                        if (isInapp) {
//
//                                            inAppActivityCall(learningModel);
//
//                                        } else {
//                                            addToMyLearning(learningModel, position);
//                                        }

                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
                        svProgressHUD.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void getMobileGetMobileContentMetaData(final MyLearningModel learningModel, final int position, final boolean isExpired) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/MobileGetMobileContentMetaData?SiteURL="
                + learningModel.getSiteURL() + "&ContentID=" + learningModel.getContentID() + "&userid="
                + appUserModel.getUserIDValue() + "&DelivoryMode=1&IsDownload=0";

        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "getMobileGetMobileContentMetaData : " + urlStr);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {

                        Log.d(TAG, "getMobileGetMobileContentMetaData response : " + jsonObj);
                        if (jsonObj.length() != 0) {
                            boolean isInserted = false;
                            try {
                                isInserted = db.saveNewlySubscribedContentMetadata(jsonObj);
                                db.updateEventStatus(learningModel, jsonObj, isExpired);

                                if (isInserted) {
                                    contextMenuModelList.get(position).setAddedToMylearning(1);
                                    MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;
                                    injectFromDbtoModel(isExpired);
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage(context.getString(R.string.cat_add_success))
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //do things
                                                    dialog.dismiss();

                                                }
                                            });

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
                        svProgressHUD.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//        Toast.makeText(context, "You Have Purchased Something ", Toast.LENGTH_SHORT).show();
        Log.v("chip", "Owned Managed Product: " + details.purchaseInfo.purchaseData);

        sendInAppDetails(details);

        preferencesManager.setStringValue("", "contentid");

    }

    @Override
    public void onPurchaseHistoryRestored() {
//        Toast.makeText(context, "onPurchaseHistoryRestored", Toast.LENGTH_SHORT).show();
        for (String sku : billingProcessor.listOwnedProducts())
            Log.v("chip", "Owned Managed Product: " + sku);
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(context, "onBillingError", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {
//        Toast.makeText(context, "onBillingInitialized", Toast.LENGTH_SHORT).show();
    }

    public boolean sendInAppDetails(@Nullable TransactionDetails details) {

        String contentId = preferencesManager.getStringValue("contentid");
        MyLearningModel learningModel = new MyLearningModel();

        int position = 0;

        for (int i = 0; i < catalogModelsList.size(); i++) {
            System.out.println(catalogModelsList.get(i));

            if (contentId.equalsIgnoreCase(catalogModelsList.get(i).getContentID())) {
                learningModel = catalogModelsList.get(i);
                position = i;
                break;
            }
        }
        boolean status = false;
        String orderId = "";
        String productId = "";
        String purchaseToken = "";
        String purchaseTime = "";

        try {
            assert details != null;
            orderId = details.purchaseInfo.purchaseData.orderId;
            productId = details.purchaseInfo.purchaseData.productId;
            purchaseToken = details.purchaseInfo.purchaseData.purchaseToken;

            String dateString = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
            purchaseTime = dateString.replace(" ", "%20");

        } catch (Exception jx) {
            Log.d("sendInAppDetails", jx.getMessage());

        }
        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileSaveInAppPurchaseDetails?_userId="
                + learningModel.getUserID() + "&_siteURL=" + appUserModel.getSiteURL() + "&_contentId="
                + learningModel.getContentID() + "&_transactionId=" + orderId + "&_receipt="
                + purchaseToken + "&_productId=" + productId
                + "&_purchaseDate=" + purchaseTime + "&_devicetype=Android";//"&_objectTypeId="+learningModel.getObjecttypeId();

        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "inappwebcall : " + urlStr);


        try {
            final MyLearningModel finalLearningModel = learningModel;
            final int finalPosition = position;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlStr,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String _response) {

                            Log.d("logr  _response =", _response);

                            if (_response.contains("success")) {
                                addToMyLearning(finalLearningModel, finalPosition);
                            } else {
                                Toast.makeText(context, "Purchase failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // Error handling
//                    Log.d("logr  error =", error.getMessage());

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    String authHeaders = appUserModel.getAuthHeaders();
                    String base64EncodedCredentials = Base64.encodeToString(authHeaders.getBytes(), Base64.NO_WRAP);

                    headers.put("Authorization", "Basic " + base64EncodedCredentials);
                    return headers;
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        volleyError = error;
                        Log.d("logr  error =", "Status code " + volleyError.networkResponse.statusCode);

                    }
                    return volleyError;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

        } catch (Exception e) {

        }

        return status;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAIL_CATALOG_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("REFRESH", false);
                if (refresh) {
                    injectFromDbtoModel(true);
                }
            }
        }

        if (requestCode == IAP_LAUNCH_FLOW_CODE && resultCode == 80) {
            if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {

                super.onActivityResult(requestCode, resultCode, data);
//            if (data != null) {
//
//                MyLearningModel learningModel = (MyLearningModel) data.getSerializableExtra("learningdata");
////            billingProcessor.handleActivityResult(8099, 80, intent);
            }
        }
        if (requestCode == COURSE_CLOSE_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");

                File myFile = new File(myLearningModel.getOfflinepath());

                if (isNetworkConnectionAvailable(getContext(), -1)) {
//                    cmiSynchTask = new CmiSynchTask(context);
//                    cmiSynchTask.execute();
                }

                if (!myFile.exists()) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        getStatusFromServer(myLearningModel);

                    }
                } else {


                }

//                injectFromDbtoModel(false);
            }

        }

    }

    public void getStatusFromServer(final MyLearningModel myLearningModel) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String paramsString = "";
        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {

            paramsString = "userId="
                    + myLearningModel.getUserID()
                    + "&scoId="
                    + myLearningModel.getScoId()
                    + "&TrackObjectTypeID="
                    + myLearningModel.getObjecttypeId()
                    + "&TrackContentID="
                    + myLearningModel.getContentID()
                    + "&TrackScoID=" + myLearningModel.getScoId()
                    + "&SiteID=" + myLearningModel.getSiteID()
                    + "&OrgUnitID=" + myLearningModel.getSiteID()
                    + "&isonexist=onexit";

        } else {
            paramsString = "userId="
                    + myLearningModel.getUserID()
                    + "&scoId="
                    + myLearningModel.getScoId();

        }

        vollyService.getJsonObjResponseVolley("UPDATESTATUS", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentStatus?" + paramsString, appUserModel.getAuthHeaders());

        resultListner = new ResultListner() {
            @Override
            public void statusUpdateFromServer(boolean serverUpdated, JSONObject result) {
                int i = -1;

                Log.d(TAG, "statusUpdateFromServer JSONObject :" + result);

                JSONArray jsonArray = null;
                try {
                    if (result.has("contentstatus")) {
                        jsonArray = result.getJSONArray("contentstatus");

                    }
                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String status = jsonObject.get("status").toString();
                        if (status.contains("failed to get statusObject reference not set to an instance of an object.")) {
                            status = "In Progress";
                        }
                        String progress = "0";
                        if (jsonObject.has("progress")) {
                            progress = jsonObject.get("progress").toString();
                        }
                        i = db.updateContentStatus(myLearningModel, status, progress);
                        if (i == 1) {

                            injectFromDbtoModel(false);
//                            Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();

                        } else {

//                            Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    @Override
    public void onDetach() {

        super.onDetach();
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int isChecked) {

        switch (isChecked) {
            case R.id.upcomingbtn:
                compactCalendarView.setVisibility(View.GONE);
                YearTitle.setVisibility(View.GONE);
                upBtn.setTypeface(null, Typeface.BOLD);
                calenderBtn.setTypeface(null, Typeface.NORMAL);
                pastBtn.setTypeface(null, Typeface.NORMAL);
//                sortByDate("before");
                TABBALUE = "upcoming";
                injectFromDbtoModel(false);
                nodata_Label.setText("");
                if (StaticValues.UPCOMINGCALLED == 0) {
                    pageIndex = 1;
                    refreshCatalog(false);
                } else {
                    injectFromDbtoModel(false);
                }
                break;
            case R.id.calanderbtn:
//                isCalendarTabScrolled=false;
                upBtn.setTypeface(null, Typeface.NORMAL);
                calenderBtn.setTypeface(null, Typeface.BOLD);
                pastBtn.setTypeface(null, Typeface.NORMAL);
                compactCalendarView.setVisibility(View.VISIBLE);
                YearTitle.setVisibility(View.VISIBLE);
                StaticValues.UPCOMINGCALLED = 0;
                StaticValues.PASTCALLED = 0;
//                String todayD = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
//                onDayClick(getCurrentDateTimeInDate(todayD));
                TABBALUE = "calendar";
                nodata_Label.setText("");
                getStartDateandEndDate(true);
//                if (StaticValues.CALENDARCALLED == 0) {
                refreshCatalog(false);
//                } else {
//                    injectFromDbtoModel(false);
//                }
                break;
            case R.id.pastbtn:
                upBtn.setTypeface(null, Typeface.NORMAL);
                calenderBtn.setTypeface(null, Typeface.NORMAL);
                pastBtn.setTypeface(null, Typeface.BOLD);
                compactCalendarView.setVisibility(View.GONE);
                YearTitle.setVisibility(View.GONE);
                TABBALUE = "past";
                injectFromDbtoModel(false);
                nodata_Label.setText("");
                if (StaticValues.PASTCALLED == 0) {
                    pageIndex = 1;
                    refreshCatalog(false);
                } else {
                    injectFromDbtoModel(false);
                }
                break;
            default:
                // Nothing to do
        }
    }

    public void dismissSvProgress() {

        new CountDownTimer(400, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                svProgressHUD.dismissImmediately();
            }
        }.start();

    }


    @TargetApi(Build.VERSION_CODES.N)
    public void sortByDate(String typeTime) {
        List<MyLearningModel> myLearningModelList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (typeTime.equalsIgnoreCase("before")) {

            for (int i = 0; i < catalogModelsList.size(); i++) {
                Date strDate = null;
                String checkDate = catalogModelsList.get(i).getEventstartTime();
                try {
                    strDate = sdf.parse(checkDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (new Date().before(strDate)) {
                    catalogModelsList.get(i).setCompletedEvent(false);
                    myLearningModelList.add(catalogModelsList.get(i));
//                 Toast.makeText(context, typeTime + " if  event " + strDate, Toast.LENGTH_SHORT).show();
                }
            }
            catalogAdapter.refreshList(myLearningModelList);
            contextMenuModelList.clear();
            contextMenuModelList.addAll(myLearningModelList);
        } else {

            for (int i = 0; i < catalogModelsList.size(); i++) {
                Date strDate = null;
                String checkDate = catalogModelsList.get(i).getEventstartTime();

                try {
                    strDate = sdf.parse(checkDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (new Date().after(strDate)) {
                    catalogModelsList.get(i).setCompletedEvent(true);
                    myLearningModelList.add(catalogModelsList.get(i));

                }
            }

            catalogAdapter.refreshList(myLearningModelList);
            contextMenuModelList.clear();
            contextMenuModelList.addAll(myLearningModelList);
        }

        if (myLearningModelList.size() == 0) {
            nodata_Label.setText(getResources().getString(R.string.no_data));
        }

        addEvents(myLearningModelList);

    }

    @Override
    public void onDayClick(Date dateClicked) {
        List<MyLearningModel> calanderEventList = new ArrayList<>();
//        Toast.makeText(context, " " + dateFormatForDisplaying.format(dateClicked), Toast.LENGTH_SHORT).show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < catalogModelsList.size(); i++) {
            Date strDate = null;
            String checkDateStr = catalogModelsList.get(i).getEventstartTime();

            try {
                strDate = sdf.parse(checkDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date clickedDate = GetZeroTimeDate(dateClicked);
            Date checkDate = GetZeroTimeDate(strDate);

            if (!clickedDate.after(checkDate) && !clickedDate.before(checkDate)) {

                calanderEventList.add(catalogModelsList.get(i));
                catalogAdapter.refreshList(calanderEventList);
                contextMenuModelList.clear();
                contextMenuModelList.addAll(calanderEventList);
                nodata_Label.setText("");
            } else {
                nodata_Label.setText(getResources().getString(R.string.no_data));
                catalogAdapter.refreshList(calanderEventList);
                contextMenuModelList.clear();
                contextMenuModelList.addAll(calanderEventList);
            }
        }
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {

        getStartDateandEndDate(false);
//        onDayClick(compactCalendarView.getFirstDayOfCurrentMonth());
        isCalendarTabScrolled = true;
    }


    public void getStartDateandEndDate(boolean tabClicked) {

        YearTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        String start = (dateFormatToSendserver.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        Log.d(TAG, "onMonthScroll: startDate" + start);

        String getyearAndMonthInt = (getyearAndMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        Log.d(TAG, "onMonthScroll: getyearAndMonthInt" + getyearAndMonthInt);
        String[] dateAry = getyearAndMonthInt.split("-");

        if (dateAry.length > 0) {

            int yearInt = Integer.parseInt(dateAry[1]);
            int monthInt = Integer.parseInt(dateAry[0]);

            String endDate = getLastDateOfMonth(yearInt, monthInt - 1);
            Log.d(TAG, "onMonthScroll: endDate" + endDate);
            startDateStr = start.replace(" ", "%20");
            endDateStr = endDate.replace(" ", "%20");
            if (!tabClicked) {
                refreshCatalog(false);
            }
        }

    }

    private void addEvents(List<MyLearningModel> myLearningModelList) {

        for (int i = 0; i < myLearningModelList.size(); i++) {

            Date date = ConvertToDate(myLearningModelList.get(i).getEventstartTime());
            currentCalender.setTime(date);
//            currentCalender.add(Calendar.DATE, i);
//            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();
            List<Event> events = getEvents(timeInMillis, i);
            compactCalendarView.addEvents(events);

        }
    }

    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if (day > 2 && day <= 4) {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)));
        } else {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + new Date(timeInMillis)));
        }
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public void addEventToAndroidDevice(MyLearningModel eventModel) {

        if (!eventModel.getRelatedContentCount().equalsIgnoreCase("0")) {
            GlobalMethods.relatedContentView(eventModel, context);
        }

        try {
            String eventUriString = "content://com.android.calendar/events";
            ContentValues eventValues = new ContentValues();
            eventValues.put("calendar_id", 1); // id, We need to choose from
            // our mobile for primary its 1
            eventValues.put("title", eventModel.getCourseName());
            eventValues.put("description", eventModel.getShortDes());
            eventValues.put("eventLocation", eventModel.getLocationName());


            long startMillis = 0;
            long endMillis = 0;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = null, endDate = null;
            try {
                startDate = simpleDateFormat.parse(eventModel.getEventstartTime());
                startMillis = startDate.getTime();
                endDate = simpleDateFormat.parse(eventModel.getEventendTime());
                endMillis = endDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long endDates = startMillis + 1000 * 10 * 10; // For next 10min
            eventValues.put("dtstart", startMillis);
            eventValues.put("dtend", endDates);

            // values.put("allDay", 1); //If it is bithday alarm or such
            // kind (which should remind me for whole day) 0 for false, 1
            // for true
            eventValues.put("eventStatus", eventModel.getStatus()); // This information is
            // sufficient for most
            // entries tentative (0),
            // confirmed (1) or canceled
            // (2):
//            eventValues.put("eventTimezone", "UTC/GMT +5:30");
            eventValues.put("eventTimezone", "UTC/GMT +5:30");


            eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

            Uri eventUri = context.getApplicationContext()
                    .getContentResolver()
                    .insert(Uri.parse(eventUriString), eventValues);
//           String eventID = Long.parseLong(eventUri.getLastPathSegment());
        } catch (Exception ex) {
            Log.e(TAG, "addEventToAndroidDevice: " + ex.getMessage());
        }
    }

    public void cancelEnrollment(final MyLearningModel eventModel) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/CancelEnrolledEvent?EventContentId="
                + eventModel.getContentID() + "&UserID=" + eventModel.getUserID() + "&SiteID=" + appUserModel.getSiteIDValue();

        Log.d(TAG, "main login : " + urlStr);

        urlStr = urlStr.replaceAll(" ", "%20");

        StringRequest jsonObjectRequest = new StringRequest(urlStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        svProgressHUD.dismiss();
                        Log.d("Response: ", " " + response);

                        if (response.contains("true")) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.event_cancelled))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            dialog.dismiss();
                                            // remove event from android calander

                                            db.ejectEventsFromDownloadData(eventModel);
                                            db.updateEventAddedToMyLearningInEventCatalog(eventModel, 0);
                                            injectFromDbtoModel(true);
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
                        svProgressHUD.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void addExpiryEvets(MyLearningModel catalogModel, int position) throws JSONException {

        JSONObject parameters = new JSONObject();

        //mandatory
        parameters.put("SelectedContent", catalogModel.getContentID());
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("SiteID", catalogModel.getSiteID());
        parameters.put("OrgUnitID", catalogModel.getSiteID());
        parameters.put("Locale", "en-us");

        String parameterString = parameters.toString();
        boolean isSubscribed = db.isSubscribedContent(catalogModel);
        if (isSubscribed) {
            Toast toast = Toast.makeText(
                    context,
                    context.getString(R.string.cat_add_already),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            sendExpiryEventData(parameterString, position, catalogModel);
        }
    }

    public void sendExpiryEventData(final String postData, final int position, final MyLearningModel catalogModel) {
        String apiURL = "";

        apiURL = appUserModel.getWebAPIUrl() + "/Catalog/AddExpiredContentToMyLearning";

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.d("CMP", "onResponse: " + s);

                if (s != null && s.length() > 0) {
                    try {

                        if (s.contains("true")) {
// ------------------------- old code here

                            getMobileGetMobileContentMetaData(catalogModel, position, false);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.event_add_success))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            dialog.dismiss();
                                            // add event to android calander
//                                            addEventToAndroidDevice(catalogModel);
                                            db.updateEventAddedToMyLearningInEventCatalog(catalogModel, 1);
                                            injectFromDbtoModel(false);

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            Toast toast = Toast.makeText(
                                    context, "Unable to process request",
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

//--------------------------- old code end here

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();

            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return postData.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public int countOfTotalRecords(JSONObject jsonObject) throws JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("table");
        int totalRecs = 0;

        if (jsonTableAry.length() > 0) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(0);

            totalRecs = jsonMyLearningColumnObj.getInt("totalrecordscount");
        }

        return totalRecs;
    }

}