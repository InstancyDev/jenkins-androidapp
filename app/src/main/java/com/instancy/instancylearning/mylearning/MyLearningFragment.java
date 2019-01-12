package com.instancy.instancylearning.mylearning;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.advancedfilters_mylearning.AllFilterModel;
import com.instancy.instancylearning.advancedfilters_mylearning.AllFiltersActivity;
import com.instancy.instancylearning.advancedfilters_mylearning.ApplyFilterModel;
import com.instancy.instancylearning.advancedfilters_mylearning.ContentFilterByModel;
import com.instancy.instancylearning.normalfilters.AdvancedFilterActivity;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.filter.Filter_activity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.globalsearch.GlobalSearchActivity;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.UnZip;

import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.EventInterface;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.EndlessScrollListener;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.utils.StaticValues.BACKTOMAINSITE;
import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE_ADV;
import static com.instancy.instancylearning.utils.StaticValues.GLOBAL_SEARCH;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.REVIEW_REFRESH;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.showToast;

/**
 * Created by Upendranath on 5/19/2017.
 * http://www.cs.dartmouth.edu/~campbell/cs65/lecture08/lecture08.html
 * https://github.com/majidgolshadi/Android-Download-Manager-Pro
 */

public class MyLearningFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    String TAG = MyLearningFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.mylearninglistview)
    ListView myLearninglistView;
    MyLearningAdapter myLearningAdapter;
    List<MyLearningModel> myLearningModelsList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search, itemArchive;
    SideMenusModel sideMenusModel = null;
    String filterContentType = "", consolidationType = "all", sortBy = "", ddlSortList = "", ddlSortType = "", searchText = "", contentFilterType = "";
    ResultListner resultListner = null;
    WebAPIClient webAPIClient;
    CmiSynchTask cmiSynchTask;
    UiSettingsModel uiSettingsModel;
    AppController appcontroller;
    boolean firstTimeVisible = true;

    boolean isReportEnabled = true;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    boolean isFromNotification = false;

    String contentIDFromNotification = "";

    EventInterface eventInterface = null;

    int pageIndex = 1, totalRecordsCount = 0, pageSize = 10;

    boolean isDigimedica = true;

    boolean isSearching = false;
    boolean userScrolled = false;
    boolean isArchived = false;
    int isArchi = 0;
    ProgressBar progressBar;
    HashMap<String, String> responMap = null;
    List<ContentFilterByModel> contentFilterByModelList = new ArrayList<>();
    ApplyFilterModel applyFilterModel = new ApplyFilterModel();

    public MyLearningFragment() {

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
        String isViewed = preferencesManager.getStringValue(StaticValues.KEY_HIDE_ANNOTATION);

        if (isViewed.equalsIgnoreCase("true")) {
            appcontroller.setAlreadyViewd(true);
        } else {
            appcontroller.setAlreadyViewd(false);
        }
        vollyService = new VollyService(resultCallback, context);

        sideMenusModel = null;
        webAPIClient = new WebAPIClient(context);

        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
            isFromNotification = bundle.getBoolean("ISFROMNOTIFICATIONS");

            if (isFromNotification) {

                contentIDFromNotification = bundle.getString("CONTENTID");
            }

            responMap = generateConditionsHashmap(sideMenusModel.getConditions());
        }
        if (responMap != null && responMap.containsKey("showconsolidatedlearning")) {
            String consolidate = responMap.get("showconsolidatedlearning");
            if (consolidate.equalsIgnoreCase("true")) {
                consolidationType = "consolidate";
            } else {
                consolidationType = "all";
            }
        } else {
            // No such key
            consolidationType = "all";
        }
        if (responMap != null && responMap.containsKey("sortby")) {
            sortBy = responMap.get("sortby");
        } else {
            // No such key
            sortBy = "";
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


        if (responMap != null && responMap.containsKey("FilterContentType")) {
            filterContentType = responMap.get("FilterContentType");
        } else {
            // No such key
            filterContentType = "";
        }

        if (responMap != null && responMap.containsKey("ContentFilterBy")) {
            contentFilterType = responMap.get("ContentFilterBy");
        } else {
            // No such key
            contentFilterType = "";
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        setHasOptionsMenu(true);
    }

    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }
        String paramsString = "FilterCondition=" + filterContentType +
                "&SortCondition=" + sortBy +
                "&RecordCount=0&OrgUnitID="
                + appUserModel.getSiteIDValue()
                + "&UserID="
                + appUserModel.getUserIDValue()
                + "&Type=" + consolidationType
                + "&FilterID=-1&ComponentID=3&Locale=en-us&SearchText=" + searchText + "&SiteID="
                + appUserModel.getSiteIDValue()
                + "&PreferenceID=-1&CategoryCompID=19&DateOfMyLastAccess=&SingleBranchExpand=false&GoogleValues=&DeliveryMode=1&GroupJoin=0" + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize + "&CompInsID=" + sideMenusModel.getRepositoryId() + "&sortType=" + ddlSortType + "&sortby=" + ddlSortList + "&IsArchieve=" + isArchi;

        vollyService.getJsonObjResponseVolley("MYLEARNINGDATA", appUserModel.getWebAPIUrl() + "MobileLMS/MobileMyCatalogObjectsNew?" + paramsString, appUserModel.getAuthHeaders());
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("MYLEARNINGDATA")) {
                    if (response != null) {
                        try {
                            db.injectMyLearningData(response, pageIndex);
                            totalRecordsCount = countOfTotalRecords(response);
                            injectFromDbtoModel();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        nodata_Label.setText(getResources().getString(R.string.no_data));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                if (requestType.equalsIgnoreCase("UPDATESTATUS")) {

                    if (response != null) {

                        if (resultListner != null)
                            resultListner.statusUpdateFromServer(true, response);

                    } else {

                    }
                }
                if (requestType.equalsIgnoreCase("FILTER")) {

                    if (response != null) {
                        try {
                            db.insertFilterIntoDB(response, appUserModel, 1);
                            Intent intent = new Intent(context, AdvancedFilterActivity.class);
                            intent.putExtra("isFrom", 1);
                            startActivityForResult(intent, FILTER_CLOSE_CODE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        Toast.makeText(getContext(), "Filter is not configured", Toast.LENGTH_SHORT).show();

                    }

                }
                svProgressHUD.dismiss();
                swipeRefreshLayout.setRefreshing(false);
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

                if (requestType.equalsIgnoreCase("MYLEARNINGDATA")) {

                    nodata_Label.setText(getResources().getString(R.string.no_data));
                }

            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);

                if (requestType.equalsIgnoreCase("MYLEARNINGDATA")) {
                    if (response != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            db.injectMyLearningData(jsonObj, pageIndex);
                            totalRecordsCount = countOfTotalRecords(jsonObj);
                            if (totalRecordsCount == 0) {
                                myLearningModelsList = new ArrayList<MyLearningModel>();
                                myLearningAdapter.refreshList(myLearningModelsList);
                                nodata_Label.setText(getResources().getString(R.string.no_data));
                            } else {
                                injectFromDbtoModel();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        nodata_Label.setText(getResources().getString(R.string.no_data));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

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

                svProgressHUD.dismiss();
            }
        };
    }

    public void injectFromDbtoModel() {
//          myLearningModelsList.clear();
        myLearningModelsList = db.fetchMylearningModel();
        if (myLearningModelsList != null) {
//            Log.d(TAG, "dataLoaded: " + myLearningModelsList.size());
            myLearningAdapter.refreshList(myLearningModelsList);
//            myLearninglistView.setVisibility(View.VISIBLE);
        } else {
            myLearningModelsList = new ArrayList<MyLearningModel>();
            myLearningAdapter.refreshList(myLearningModelsList);
//            myLearninglistView.setVisibility(View.GONE);
            nodata_Label.setText(getResources().getString(R.string.no_data));
        }

        if (myLearningModelsList.size() == pageSize) {
            pageIndex = 2;
        } else {
            pageIndex = myLearningModelsList.size() / pageSize;
            pageIndex = pageIndex + 1;
        }
//        Comment this for globalsearch
//        if (myLearningModelsList.size() > 5) {
//            if (item_search != null) {
//                item_search.setVisible(true);
//            }
//
//        } else {
//
//            if (item_search != null) {
//                item_search.setVisible(false);
//            }
//
//        }

        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                triggerActionForFirstItem();
//                progressBar.setVisibility(View.GONE);
            }
        }.start();

        svProgressHUD.dismiss();
    }

    public void triggerActionForFirstItem() {

        if (uiSettingsModel.getAutoLaunchMyLearningFirst().equalsIgnoreCase("true")) {

            if (myLearningModelsList.size() > 0) {

                if (!myLearningModelsList.get(0).getStatusActual().toLowerCase().contains("completed") && firstTimeVisible) {

                    if (myLearninglistView != null) {
                        myLearninglistView.performItemClick(getView(), 0, R.id.title_text);
                    }
                    firstTimeVisible = false;
                }
            }

        } else {
            if (isFromNotification) {
                int selectedPostion = getPositionForNotification(contentIDFromNotification);
//                myLearninglistView.setSelection(selectedPostion);

                if (myLearningModelsList != null) {

                    try {
                        Intent intentDetail = new Intent(getContext(), MyLearningDetail_Activity.class);
                        intentDetail.putExtra("IFROMCATALOG", false);
                        intentDetail.putExtra("myLearningDetalData", myLearningModelsList.get(selectedPostion));
                        startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);
                        isFromNotification = false;
                    } catch (IndexOutOfBoundsException ex) {
//                        Toast.makeText(context, "No Content Avaliable", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, "No Content Avaliable", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public int getPositionForNotification(String contentID) {
        int position = 0;

        for (int k = 0; k < myLearningModelsList.size(); k++) {
            if (myLearningModelsList.get(k).getContentID().equalsIgnoreCase(contentID)) {
                position = k;
                break;
            }

        }

        return position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mylearning, container, false);
        Log.d(TAG, "onCreateView: ");
        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        myLearningModelsList = new ArrayList<MyLearningModel>();
        isReportEnabled = db.isPrivilegeExistsFor(StaticValues.REPORTPREVILAGEID);
        initilizeInterface();
        myLearningAdapter = new MyLearningAdapter(getActivity(), BIND_ABOVE_CLIENT, myLearningModelsList, eventInterface, isReportEnabled);
        myLearninglistView.setAdapter(myLearningAdapter);
        myLearninglistView.setOnItemClickListener(this);
        myLearninglistView.setEmptyView(rootView.findViewById(R.id.nodata_label));
//        myLearninglistView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });

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

                if (totalItemCount < totalRecordsCount && totalItemCount != 0) {

                    Log.d(TAG, "onLoadMore size: catalogModelsList" + myLearningModelsList.size());

                    Log.d(TAG, "onLoadMore size: totalRecordsCount" + totalRecordsCount);


                    if (userScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                        userScrolled = false;

                        if (!isSearching) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (isNetworkConnectionAvailable(getContext(), -1)) {
                                if (isDigimedica) {
                                    getMobileMyCatalogObjectsNew(true);
                                } else {
                                    refreshMyLearning(true);
                                }
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
                appcontroller.setAlreadyViewd(true);
                preferencesManager.setStringValue("true", StaticValues.KEY_HIDE_ANNOTATION);
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
//                Log.d(TAG, "onLoadMore: called totalItemsCount" + totalItemsCount);
//                if (totalItemsCount < totalRecordsCount && totalItemsCount != 0) {
//
//                    Log.d(TAG, "onLoadMore size: catalogModelsList" + myLearningModelsList.size());
//
//                    Log.d(TAG, "onLoadMore size: totalRecordsCount" + totalRecordsCount);
//
//                    if (!isSearching) {
//                        progressBar.setVisibility(View.VISIBLE);
//                        refreshMyLearning(true);
//                    } else {
//                        progressBar.setVisibility(View.GONE);
//
//                    }
//
//                } else {
//                    progressBar.setVisibility(View.GONE);
//                }
//
            }
        });

        toolbar = ((SideMenu) getActivity()).toolbar;
//        setSearchtollbar();

        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        toolbar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));
//        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + contextTitle + "</font>"));
        if (BACKTOMAINSITE == 2) {
            MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;
            BACKTOMAINSITE = 0;
        }

        if (isNetworkConnectionAvailable(getContext(), -1) && MYLEARNING_FRAGMENT_OPENED_FIRSTTIME == 0) {

//            refreshMyLearning(false);
            if (isDigimedica) {
                getMobileMyCatalogObjectsNew(false);
            } else {
                refreshMyLearning(false);
            }
        } else {
//            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            injectFromDbtoModel();

        }

        return rootView;
    }

    public void initilizeInterface() {
        eventInterface = new EventInterface() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void cancelEnrollment(MyLearningModel learningModel, boolean isCancel) {
                if (isCancel) {
                    cancelEnrollmentMethod(learningModel);
                } else {
                    GlobalMethods.addEventToDeviceCalendar(learningModel, context);
                }
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String link = bundle.getString("url");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

        MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 2;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mylearning_menu, menu);
        item_search = menu.findItem(R.id.mylearning_search);
        MenuItem item_filter = menu.findItem(R.id.mylearning_filter);
        MenuItem itemInfo = menu.findItem(R.id.mylearning_info_help);
        itemArchive = menu.findItem(R.id.ctx_archive);
        itemArchive.setVisible(true);

        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.crop_life))) {
            itemInfo.setVisible(true);
        } else {
            itemInfo.setVisible(false);
        }

        item_filter.setVisible(true);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);

//            searchView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
//            setCursorColor(txtSearch,Color.parseColor(uiSettingsModel.getAppLoginTextolor()));
            txtSearch.setHint(getResources().getString(R.string.mylearning_search_hint));
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

                    myLearningAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }
            });
        }

        if (itemArchive != null) {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_archive, context, uiSettingsModel.getAppHeaderTextColor());
            itemArchive.setIcon(filterDrawable);
            itemArchive.setTitle("Archive");
        }

        if (item_filter != null) {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_filter, context, uiSettingsModel.getAppHeaderTextColor());
            item_filter.setIcon(filterDrawable);
            item_filter.setTitle("Filter");
        }

        if (itemInfo != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.help);
            itemInfo.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
        }
        item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                isSearching = true;
                if (uiSettingsModel.isGlobasearch()) {
                    gotoGlobalSearch();
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                isSearching = false;
                return true;
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

    public void setSearchtollbar() {
        if (toolbar != null) {
            toolbar.inflateMenu(R.menu.mylearning_menu);
            search_menu = toolbar.getMenu();

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(R.id.toolbar, 1, true, false);
                    else
                        toolbar.setVisibility(View.GONE);
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mylearning_search:
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                    circleReveal(R.id.toolbar, 1, true, true);
//                else
//                    toolbar.setVisibility(View.VISIBLE);
//                item_search.expandActionView();
//                gotoGlobalSearch();
                break;
            case R.id.mylearning_info_help:
                appcontroller.setAlreadyViewd(false);
                preferencesManager.setStringValue("false", StaticValues.KEY_HIDE_ANNOTATION);
                myLearningAdapter.notifyDataSetChanged();
                break;
            case R.id.mylearning_filter:
//                filterApiCall();
                advancedFilters();
                break;
            case R.id.ctx_archive:
                if (isNetworkConnectionAvailable(getContext(), -1)) {
                    isArchivedCall();
                } else {
                    showToast(context, "No Internet");
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void gotoGlobalSearch() {

        Intent intent = new Intent(context, GlobalSearchActivity.class);
        intent.putExtra("sideMenusModel", sideMenusModel);
        startActivityForResult(intent, GLOBAL_SEARCH);

    }

    public void isArchivedCall() {
        if (isArchived) {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_archive, context, uiSettingsModel.getAppHeaderTextColor());
            itemArchive.setIcon(filterDrawable);
            isArchived = false;
            isArchi = 0;
            pageIndex = 1;
        } else {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_leanpub, context, uiSettingsModel.getAppHeaderTextColor());
            itemArchive.setIcon(filterDrawable);
            isArchived = true;
            isArchi = 1;
            pageIndex = 1;
        }

        if (isDigimedica) {
            getMobileMyCatalogObjectsNew(true);
        } else {
            refreshMyLearning(true);
        }
    }

    public HashMap<String, String> generateHashMap(String[] conditionsArray) {

        HashMap<String, String> map = new HashMap<String, String>();

        if (conditionsArray.length != 0) {

            for (int i = 0; i < conditionsArray.length; i++) {
                String[] filterArray = conditionsArray[i].split("=");
                System.out.println(" forvalue " + filterArray);
                if (filterArray.length > 1) {
                    map.put(filterArray[0], filterArray[1]);
                }
            }
            for (String s : map.keySet()) {
                System.out.println(s + " forvalue " + map.get(s));
            }
        } else {

        }
        return map;
    }

    @Override
    public void onRefresh() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            pageIndex = 1;
            searchText = "";
            if (isDigimedica) {
                getMobileMyCatalogObjectsNew(true);
            } else {
                refreshMyLearning(true);
            }
            MenuItemCompat.collapseActionView(item_search);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
//            showCustomAlert(context);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (view == null) {
            firstTimeVisible = true;
            return;
        }
        switch (view.getId()) {
            case R.id.btntxt_download:
                if (isNetworkConnectionAvailable(context, -1)) {
                    downloadTheCourse(myLearningModelsList.get(position), view, position);
                } else {
                    showToast(context, "No Internet");
                }
                break;
            case R.id.imagethumb:
            case R.id.txt_title_name:
                GlobalMethods.launchCourseViewFromGlobalClass(myLearningModelsList.get(position), getContext());
                break;
            default:
                GlobalMethods.launchCourseViewFromGlobalClass(myLearningModelsList.get(position), getContext());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow,
                             final boolean isShow) {
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

    @Override
    public void onResume() {
        super.onResume();
//        triggerActionForFirstItem();
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

    public void downloadTheCourse(final MyLearningModel learningModel, final View view, final int position) {

        boolean isZipFile = false;

        final String[] downloadSourcePath = {null};

        TextView txtBtnDownload = (TextView) view.findViewById(R.id.btntxt_download);

        txtBtnDownload.setEnabled(false);

        switch (learningModel.getObjecttypeId()) {
            case "52":
                downloadSourcePath[0] = learningModel.getSiteURL() + "/content/sitefiles/"
                        + learningModel.getSiteID() + "/usercertificates/" + learningModel.getSiteID() + "/"
                        + learningModel.getContentID() + ".pdf";
                isZipFile = false;
                break;
            case "11":
            case "14":
                if (learningModel.getObjecttypeId().equalsIgnoreCase("11") && learningModel.getJwvideokey().length() > 0 & learningModel.getCloudmediaplayerkey().length() > 0) {
                    //JW Standalone video content in offline mode.

                    downloadSourcePath[0] = "https://content.jwplatform.com/videos/" + learningModel.getJwvideokey() + ".mp4";

                } else {

                    downloadSourcePath[0] = learningModel.getSiteURL() + "content/publishfiles/"
                            + learningModel.getFolderPath() + "/" + learningModel.getStartPage();
                }

                isZipFile = false;
                break;
            case "8":
            case "9":
            case "10":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/publishfiles/"
                        + learningModel.getFolderPath() + "/" + learningModel.getContentID() + ".zip";
                isZipFile = true;
                break;
            default:
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/publishfiles/"
                        + learningModel.getFolderPath() + "/" + learningModel.getContentID()
                        + ".zip";
                isZipFile = true;
                break;
        }

        final boolean finalisZipFile = isZipFile;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int statusCode = 0;
                //code to do the HTTP request
                if (finalisZipFile) {

                    statusCode = webAPIClient.checkFileFoundOrNot(downloadSourcePath[0], appUserModel.getAuthHeaders());

                    if (statusCode != 200) {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "/content/downloadfiles/"
                                + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], view, learningModel, position);

                    } else {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/publishfiles/"
                                + learningModel.getFolderPath() + "/" + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], view, learningModel, position);

                    }
                } else {

                    downloadThin(downloadSourcePath[0], view, learningModel, position);
                }
//                int statusCode = vollyService.checkResponseCode(downloadSourcePath[0]);

            }
        });
        thread.start();

    }

    public void downloadThin(String downloadStruri, View view, final MyLearningModel learningModel, final int position) {

        downloadStruri = downloadStruri.replace(" ", "%20");
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        Uri downloadUri = Uri.parse(downloadStruri);
        String extensionStr = "";
        switch (learningModel.getObjecttypeId()) {
            case "52":
            case "11":
            case "14":

                String[] startPage = null;
                if (learningModel.getStartPage().contains("/")) {
                    startPage = learningModel.getStartPage().split("/");
                    extensionStr = startPage[1];
                } else {
                    extensionStr = learningModel.getStartPage();
                }
                break;
            case "8":
            case "9":
            case "10":
                extensionStr = learningModel.getContentID() + ".zip";
                break;
            default:
                extensionStr = learningModel.getContentID() + ".zip";
                break;
        }

        String localizationFolder = "";
        String[] startPage = null;
        if (learningModel.getStartPage().contains("/")) {
            startPage = learningModel.getStartPage().split("/");
            localizationFolder = "/" + startPage[0];
        } else {
            localizationFolder = "";
        }
        String downloadDestFolderPath = "";
        if (extensionStr.contains(".zip")) {

            downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                    + "/.Mydownloads/Contentdownloads" + "/" + learningModel.getContentID();

        } else {
            downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                    + "/.Mydownloads/Contentdownloads" + "/" + learningModel.getContentID() + localizationFolder;
        }

        boolean success = (new File(downloadDestFolderPath)).mkdirs();
        final String finalDownloadedFilePath = downloadDestFolderPath + "/" + extensionStr;
        final Uri destinationUri = Uri.parse(finalDownloadedFilePath);
        final String finalDownloadDestFolderPath = downloadDestFolderPath;
        Log.d(TAG, "downloadThin: " + downloadUri);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new com.thin.downloadmanager.DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Log.d(TAG, "onDownloadComplete: ");

                        if (finalDownloadedFilePath.contains(".zip")) {
                            String zipFile = finalDownloadedFilePath;
                            String unzipLocation = finalDownloadDestFolderPath;
                            UnZip d = new UnZip(zipFile,
                                    unzipLocation);
//                            File zipfile = new File(zipFile);
//                            zipfile.delete();
//
//                            UnzipUtility unzipper = new UnzipUtility();
//                            try {
//                                unzipper.unzip(zipFile, unzipLocation);
//                            } catch (Exception ex) {
//                                // some errors occurred
//                                ex.printStackTrace();
//                            }
//

//                            ZipArchive zipArchive = new ZipArchive();
//                            zipArchive.unzip(zipFile,unzipLocation,"");

                        }
                        myLearningAdapter.notifyDataSetChanged();

                        if (learningModel.getObjecttypeId().equalsIgnoreCase("10")) {
                            if (!learningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
                                callMobileGetContentTrackedData(learningModel);
                                callMobileGetMobileContentMetaData(learningModel);
                            } else {
                                callMobileGetMobileContentMetaData(learningModel);

                            }

                        } else {
                            if (!learningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
                                callMobileGetContentTrackedData(learningModel);

                            }

                        }

                        // write jw content method download

                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Log.d(TAG, "onDownloadFailed: " + +errorCode);
                        Toast.makeText(context, "    Currently we are unable to download the content.   Please try again later.     ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
//                        Log.d(TAG, "onProgress: " + progress);
                        View v = myLearninglistView.getChildAt(position - myLearninglistView.getFirstVisiblePosition());
                        if (v != null) {
                            updateStatus(position, progress);
                        }
                    }

                });
        int downloadId = downloadManager.add(downloadRequest);
    }

    public void callMobileGetContentTrackedData(MyLearningModel learningModel) {
        String paramsString = "_studid=" + learningModel.getUserID() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + learningModel.getSiteURL() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        vollyService.getJsonObjResponseVolley("MLADP", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?" + paramsString, appUserModel.getAuthHeaders(), learningModel);

    }

    public void callMobileGetMobileContentMetaData(final MyLearningModel learningModel) {

        String paramsString = "SiteURL=" + learningModel.getSiteURL()
                + "&ContentID=" + learningModel.getContentID()
                + "&userid=" + learningModel.getUserID()
                + "&DelivoryMode=1&IsDownload=1";


        String metaDataUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetMobileContentMetaData?" + paramsString;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, metaDataUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    try {
                        db.insertTrackObjects(response, learningModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjReq);

    }

    private void updateStatus(int index, int Status) {
        // Update ProgressBar
        // Update Text to ColStatus
        View v = myLearninglistView.getChildAt(index - myLearninglistView.getFirstVisiblePosition());
        TextView txtBtnDownload = (TextView) v.findViewById(R.id.btntxt_download);
        CircleProgressBar circleProgressBar = (CircleProgressBar) v.findViewById(R.id.circle_progress);

        circleProgressBar.setVisibility(View.VISIBLE);
        txtBtnDownload.setVisibility(View.GONE);
        circleProgressBar.setProgress(Status);
        // Enabled Button View
        if (Status >= 100) {
            if (isAdded()) {
                txtBtnDownload.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
                txtBtnDownload.setVisibility(View.VISIBLE);
                circleProgressBar.setVisibility(View.GONE);
                txtBtnDownload.setEnabled(false);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COURSE_CLOSE_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");

                File myFile = new File(myLearningModel.getOfflinepath());

                if (!myFile.exists()) {

                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {

                        if (isNetworkConnectionAvailable(getContext(), -1)) {
                            getStatusFromServer(myLearningModel);

                        }
                    } else {
                        if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
                            int i = -1;
                            i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
                            if (i == 1) {
//                                Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();
                                injectFromDbtoModel();
                            } else {

//                                Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
                        int i = -1;
                        i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");

                        if (i == 1) {
//                            Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();
//                            myLearningAdapter.notifyDataSetChanged();
//                            injectFromDbtoModel();
                        } else {

//                            Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }
                    }
                    MenuItemCompat.collapseActionView(item_search);
                    injectFromDbtoModel();
                }
                if (isNetworkConnectionAvailable(getContext(), -1)) {
                    cmiSynchTask = new CmiSynchTask(context);
                    cmiSynchTask.execute();
                }
            }
        }

        if (requestCode == DETAIL_CLOSE_CODE && resultCode == RESULT_OK) {
//            Toast.makeText(context, "Detail Status updated!", Toast.LENGTH_SHORT).show();
            if (data.getStringExtra("refresh").equalsIgnoreCase("refresh")) {
                injectFromDbtoModel();
            } else {
                boolean refresh = data.getBooleanExtra("NEWREVIEW", false);
                if (refresh) {

                    if (isDigimedica) {
                        getMobileMyCatalogObjectsNew(true);
                    } else {
                        refreshMyLearning(true);
                    }
                }

            }
        }

        if (requestCode == FILTER_CLOSE_CODE && resultCode == RESULT_OK) {

            boolean resetFilter = data.getBooleanExtra("FILTER", false);

            if (resetFilter) {

                injectFromDbtoModel();

            } else {
                String sortName = data.getStringExtra("coursetype");
                boolean filterAscend = data.getBooleanExtra("sortby", false);
                String configId = data.getStringExtra("configid");
                myLearningAdapter.applySortBy(filterAscend, configId);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(data.getStringExtra("jsonInnerValues"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonObject.length() > 0) {
                    myLearningAdapter.applyGroupBy(jsonObject);
                }

                if (jsonObject.length() > 0) {
                    myLearningAdapter.filterByObjTypeId(jsonObject);
                }

            }

            if (myLearningModelsList.size() <= 0) {
                nodata_Label.setText(getResources().getString(R.string.no_data));
            }


        }

        if (requestCode == REVIEW_REFRESH && resultCode == RESULT_OK) {
            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");
                boolean refresh = data.getBooleanExtra("NEWREVIEW", false);
                if (refresh) {

                    if (isDigimedica) {
                        getMobileMyCatalogObjectsNew(true);
                    } else {
                        refreshMyLearning(true);
                    }

                }

            }
        }

        if (requestCode == GLOBAL_SEARCH && resultCode == RESULT_OK) {
            if (data != null) {
                searchText = data.getStringExtra("queryString");
                if (searchText.length() > 0) {

                    if (isDigimedica) {
                        getMobileMyCatalogObjectsNew(true);
                    } else {
                        refreshMyLearning(true);
                    }

                }
            }
        }

        if (requestCode == FILTER_CLOSE_CODE_ADV && resultCode == RESULT_OK) {
            if (data != null) {
                boolean isApplied = data.getBooleanExtra("APPLY", false);
                if (isApplied) {

                    contentFilterByModelList = (List<ContentFilterByModel>) data.getExtras().getSerializable("contentFilterByModelList");

                    applyFilterModel = (ApplyFilterModel) data.getExtras().getSerializable("applyFilterModel");
                    Log.d(TAG, "onActivityResult: applyFilterModel : " + applyFilterModel.categories);
                    pageIndex = 1;
                    if (isDigimedica) {
                        getMobileMyCatalogObjectsNew(true);
                    } else {
                        refreshMyLearning(true);
                    }

                }
            }
        }


    }

    public void getStatusFromServer(final MyLearningModel myLearningModel) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String paramsString = "";
        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {

            paramsString = "userID="
                    + myLearningModel.getUserID()
                    + "&scoid="
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
            paramsString = "userID="
                    + myLearningModel.getUserID()
                    + "&scoid="
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
                        String status = "";
                        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.app_esperanza))) {

                            status = jsonObject.optString("Name").trim();
                        } else {

                            status = jsonObject.optString("status").trim();
                        }// esperanza call


                        String progress = "";
                        if (jsonObject.has("progress")) {
                            progress = jsonObject.get("progress").toString();
                        }
                        i = db.updateContentStatus(myLearningModel, status, progress);
                        if (i == 1) {

                            injectFromDbtoModel();
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

    public void filterApiCall() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
            String urlStr = "UserID=1&ComponentID=3&SiteID=374&ShowAllItems=true&FilterContentType=&FilterMediaType=&SprateEvents=&EventType=&IsCompetencypath=false&LocaleID=en-us&CompInsID=3134";
            vollyService.getJsonObjResponseVolley("FILTER", appUserModel.getWebAPIUrl() + "/Mobilelms/GetMyLearningFilters?" + urlStr, appUserModel.getAuthHeaders());

        } else {

            JSONObject jsonObject = db.fetchFilterObject(appUserModel, 1);
            if (jsonObject != null) {

                Intent intent = new Intent(context, Filter_activity.class);
                intent.putExtra("isFrom", 1);
                startActivityForResult(intent, FILTER_CLOSE_CODE);

            } else {
                Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void cancelEnrollmentMethod(final MyLearningModel eventModel) {

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
                                            injectFromDbtoModel();
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
//                        svProgressHUD.dismiss();
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

    public int countOfTotalRecords(JSONObject jsonObject) throws JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("table");
        int totalRecs = 0;

        if (jsonTableAry.length() > 0) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(0);

            totalRecs = jsonMyLearningColumnObj.getInt("totalrecordscount");
        }

        return totalRecs;
    }

    public void advancedFilters() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            if (contentFilterByModelList.size() == 0) {
                contentFilterByModelList = generateContentFilters();
            }

            List<AllFilterModel> allFilterModelList = getAllFilterModelList();

            if (contentFilterByModelList != null && contentFilterByModelList.size() > 0) {
                Intent intent = new Intent(context, AllFiltersActivity.class);
                intent.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intent.putExtra("isFrom", 0);
                intent.putExtra("contentFilterByModelList", (Serializable) contentFilterByModelList);
                intent.putExtra("allFilterModelList", (Serializable) allFilterModelList);
                startActivityForResult(intent, FILTER_CLOSE_CODE_ADV);
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }

    }


    public List<AllFilterModel> getAllFilterModelList() {

        List<AllFilterModel> allFilterModelList = new ArrayList<>();

        AllFilterModel advFilterModel = new AllFilterModel();
        advFilterModel.categoryName = "Filter by";
        advFilterModel.categoryID = 1;
        allFilterModelList.add(advFilterModel);

        if (responMap != null && responMap.containsKey("EnableGroupby")) {
            String enableGroupby = responMap.get("EnableGroupby");
            if (enableGroupby != null && enableGroupby.equalsIgnoreCase("true")) {
                AllFilterModel groupFilterModel = new AllFilterModel();
                groupFilterModel.categoryName = "Group By";
                groupFilterModel.categoryID = 2;
                groupFilterModel.isGroup = true;
                if (responMap != null && responMap.containsKey("ddlGroupby")) {
                    String ddlGroupby = responMap.get("ddlGroupby");
                    Log.d(TAG, "getAllFilterModelList: " + ddlGroupby);
                    groupFilterModel.groupArrayList = getArrayListFromString(ddlGroupby);
                    if (groupFilterModel.groupArrayList != null && groupFilterModel.groupArrayList.size() > 0) {
                        allFilterModelList.add(groupFilterModel);
                    }
                }
            }
        }

//        if (responMap != null && responMap.containsKey("EnableFilterSort")) {
//            String enableSortby = responMap.get("EnableFilterSort");
//            if (enableSortby != null && enableSortby.equalsIgnoreCase("true")) {
//                AllFilterModel sortFilterModel = new AllFilterModel();
//                sortFilterModel.categoryName = "Sort By";
//                sortFilterModel.categoryID = 3;
//                allFilterModelList.add(sortFilterModel);
//            }
//        }

        AllFilterModel sortFilterModel = new AllFilterModel();
        sortFilterModel.categoryName = "Sort By";
        sortFilterModel.categoryID = 3;
        allFilterModelList.add(sortFilterModel);

        return allFilterModelList;
    }

    public List<ContentFilterByModel> generateContentFilters() {
        List<ContentFilterByModel> contentFilterByModelList = new ArrayList<>();
        if (contentFilterType != null && contentFilterType.length() > 0) {

            List<String> filterCategoriesArray = getArrayListFromString(contentFilterType);

            if (filterCategoriesArray != null && filterCategoriesArray.size() > 0) {
                for (int i = 0; i < filterCategoriesArray.size(); i++) {
                    ContentFilterByModel contentFilterByModel = new ContentFilterByModel();

                    switch (filterCategoriesArray.get(i)) {
                        case "categories":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "cat";
                            contentFilterByModel.categoryDisplayName = "Category";
                            contentFilterByModel.goInside = true;
                            break;
                        case "skills":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "skills";
                            contentFilterByModel.categoryDisplayName = "By Skills";
                            contentFilterByModel.goInside = true;
                            break;
                        case "objecttypeid":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "bytype";
                            contentFilterByModel.categoryDisplayName = "Content Types";
                            contentFilterByModel.goInside = false;
                            break;
                        case "jobroles":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "jobroles";
                            contentFilterByModel.categoryDisplayName = "Job Roles";
                            contentFilterByModel.goInside = false;
                            break;
                        case "solutions":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "tag";
                            contentFilterByModel.categoryDisplayName = "Solutions";
                            contentFilterByModel.goInside = false;
                            break;
                        case "rating":
                            if (responMap != null && responMap.containsKey("ShowrRatings")) {
                                String showrRatings = responMap.get("ShowrRatings");
                                if (showrRatings.contains("true") && contentFilterByModelList.size() > 0) {
                                    contentFilterByModel.categoryName = "Show Ratings";
                                    contentFilterByModel.categoryIcon = "";
                                    contentFilterByModel.categoryID = "rate";
                                    contentFilterByModel.categoryDisplayName = "Rating";
                                    contentFilterByModel.goInside = false;
                                }
                            }
                            break;
                        case "eventduration":
                            if (responMap != null && responMap.containsKey("SprateEvents")) {
                                String showrRatings = responMap.get("SprateEvents");
                                if (showrRatings.contains("true") && contentFilterByModelList.size() > 0) {

                                    contentFilterByModel.categoryName = "SprateEvents";
                                    contentFilterByModel.categoryIcon = "";
                                    contentFilterByModel.categoryID = "duration";
                                    contentFilterByModel.categoryDisplayName = "Duration";
                                    contentFilterByModel.goInside = false;
                                }
                            }
                            break;
                        case "ecommerceprice":
                            if (responMap != null && responMap.containsKey("EnableEcommerce")) {
                                String showrRatings = responMap.get("EnableEcommerce");
                                if (showrRatings.contains("true") && contentFilterByModelList.size() > 0) {
                                    contentFilterByModel.categoryName = "EnableEcommerce";
                                    contentFilterByModel.categoryIcon = "";
                                    contentFilterByModel.categoryID = "priceRange";
                                    contentFilterByModel.categoryDisplayName = "PriceRange";
                                    contentFilterByModel.goInside = false;
                                }
                            }
                            break;
                        case "instructor":
                            contentFilterByModel.categoryName = "Instructor";
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "inst";
                            contentFilterByModel.categoryDisplayName = "Instructor";
                            contentFilterByModel.goInside = false;
                            break;
                        case "certificate":
                            break;
                        case "eventdates":
                            contentFilterByModel.categoryName = "Event dates";
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "eventdates";
                            contentFilterByModel.categoryDisplayName = "eventdates";
                            contentFilterByModel.goInside = false;
                            break;

                    }
                    if (contentFilterByModel.categoryID.length() != 0) {
                        contentFilterByModelList.add(contentFilterByModel);
                    }
                }
//                ContentFilterByModel contentFilterByModel = new ContentFilterByModel();
//                contentFilterByModel.categoryName = "EnableEcommerce";
//                contentFilterByModel.categoryIcon = "";
//                contentFilterByModel.categoryID = "priceRange";
//                contentFilterByModel.categoryDisplayName = "PriceRange";
//                contentFilterByModelList.add(contentFilterByModel);
//                contentFilterByModel.goInside = false;

            }
        }
        return contentFilterByModelList;

    }

    public List<String> getArrayListFromString(String questionCategoriesString) {

        List<String> questionCategoriesArray = new ArrayList<>();

        if (questionCategoriesString.length() <= 0)
            return questionCategoriesArray;

        questionCategoriesArray = Arrays.asList(questionCategoriesString.split(","));

        return questionCategoriesArray;

    }


    public void getMobileMyCatalogObjectsNew(Boolean isRefreshed) {

        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/MobileMyCatalogObjectsData";

        JSONObject parameters = new JSONObject();

        try {

            parameters.put("pageIndex", pageIndex);
            parameters.put("pageSize", pageSize);
            parameters.put("SearchText", searchText);
            parameters.put("source", 0);
            parameters.put("type", 0);
            parameters.put("sortBy", ddlSortList);
            parameters.put("ComponentID", sideMenusModel.getComponentId());
            parameters.put("ComponentInsID", sideMenusModel.getRepositoryId());
            parameters.put("HideComplete", "false");
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("OrgUnitID", appUserModel.getSiteIDValue());
            parameters.put("Locale", "en-us");
            parameters.put("groupBy", applyFilterModel.groupBy);
            parameters.put("categories", applyFilterModel.categories);
            parameters.put("objecttypes", applyFilterModel.objectTypes);
            parameters.put("skillcats", applyFilterModel.skillCats);
            parameters.put("skills", applyFilterModel.skills);
            parameters.put("jobroles", applyFilterModel.jobRoles);
            parameters.put("solutions", applyFilterModel.solutions);
            parameters.put("ratings", applyFilterModel.ratings);
            parameters.put("keywords", "");
            parameters.put("pricerange", applyFilterModel.priceRange);
            parameters.put("duration", applyFilterModel.duration);
            parameters.put("instructors", applyFilterModel.instructors);
            parameters.put("IsArchived", isArchi);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "MYLEARNINGDATA", urlStr);
    }
}
