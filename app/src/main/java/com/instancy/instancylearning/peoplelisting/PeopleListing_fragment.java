package com.instancy.instancylearning.peoplelisting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.advancedfilters_mylearning.AllFilterModel;
import com.instancy.instancylearning.advancedfilters_mylearning.AllFiltersActivity;
import com.instancy.instancylearning.advancedfilters_mylearning.ApplyFilterModel;
import com.instancy.instancylearning.advancedfilters_mylearning.ContentFilterByActivity;
import com.instancy.instancylearning.advancedfilters_mylearning.ContentFilterByModel;
import com.instancy.instancylearning.askexpert.AskQuestionActivity;
import com.instancy.instancylearning.catalog.Catalog_fragment;
import com.instancy.instancylearning.chatmessanger.ChatActivity;
import com.instancy.instancylearning.chatmessanger.SignalAService;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalsearch.GlobalSearchActivity;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.EndlessScrollListener;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.utils.StaticValues.BACK_STACK_ROOT_TAG;
import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE_ADV;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.StaticValues.GLOBAL_SEARCH;
import static com.instancy.instancylearning.utils.StaticValues.REFRESH_PEOPLE;
import static com.instancy.instancylearning.utils.Utilities.generateHashMap;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.showToast;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class PeopleListing_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener {

    String TAG = PeopleListing_fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.peoplelsitview)
    ListView peopleListView;

    PeopleListingAdapter peopleListingAdapter;
    List<PeopleListingModel> peopleListingModelList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    String filterContentType = "", consolidationType = "all", sortBy = "", contentFilterType = "";

    ResultListner resultListner = null;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    SignalAService signalAService;

    @BindView(R.id.segmentedswitch)
    SegmentedGroup segmentedSwitch;

    @BindView(R.id.expertsbtn)
    RadioButton expertsBtn;

    @BindView(R.id.allPeoplebtn)
    RadioButton allPBtn;

    @BindView(R.id.myconnectionbtn)
    RadioButton myConBtn;

    @BindView(R.id.pendingbtn)
    RadioButton pendingBtn;

    //    String TABBALUE = "Experts";
    String TABBALUE = "All";

    String recepientID = "default";

    String userStatus = "";

    Communicator communicator;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;


    // load more
    int pageIndex = 1, totalRecordsCount = 0, pageSize = 10;

    boolean isSearching = false;
    boolean userScrolled = false;

    boolean isaskQuestionEnabled = false;

    ProgressBar progressBar;


    boolean isFromGlobalSearch = false;

    String queryString = "";

    // Filters ADVANCED
    List<ContentFilterByModel> contentFilterByModelList = new ArrayList<>();

    ApplyFilterModel applyFilterModel = new ApplyFilterModel();

    boolean isDigimedica = true;

    HashMap<String, String> responMap = null;

    public PeopleListing_fragment() {


    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, getActivity());
    }

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

        vollyService = new VollyService(resultCallback, context);

        sideMenusModel = null;

        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
            responMap = generateConditionsHashmap(sideMenusModel.getConditions());
//            catalogSideMenuModel = (SideMenusModel) bundle.getSerializable("catalogSideMenuModel");

            isFromGlobalSearch = bundle.getBoolean("ISFROMGLOBAL", false);

            if (isFromGlobalSearch) {
                queryString = bundle.getString("query");
                TABBALUE = "All";
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

//        signalAService = SignalAService.newInstance(context);
//        signalAService.startSignalA();
        isaskQuestionEnabled = db.isPrivilegeExistsFor(StaticValues.ASKEXPERTPREVILAGEID);
    }

    public void refreshPeopleListing(Boolean isRefreshed) {
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            if (!isRefreshed) {

                svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
            }

            String paramsString = "ComponentID=" + sideMenusModel.getComponentId() + "&ComponentInstanceID=" + sideMenusModel.getRepositoryId() + "&UserID=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "&FilterType=" + TABBALUE + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize + "&SearchText=" + queryString;

            vollyService.getJsonObjResponseVolley("PEOPLELISTING", appUserModel.getWebAPIUrl() + "/MobileLMS/GetPeopleListData?" + paramsString, appUserModel.getAuthHeaders());
        } else {


            injectFromDbtoModel();
            REFRESH_PEOPLE = 0;
        }
    }

    public void refreshPeopleListingGlobalListing(Boolean isRefreshed) {
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            if (!isRefreshed) {

                svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
            }

            String paramsString = "ComponentID=" + sideMenusModel.getComponentId() + "&ComponentInstanceID=" + sideMenusModel.getRepositoryId() + "&UserID=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "&FilterType=" + TABBALUE + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize + "&SearchText=" + queryString;

            vollyService.getJsonObjResponseVolley("PEOPLELISTING", appUserModel.getWebAPIUrl() + "/MobileLMS/GetPeopleListData?" + paramsString, appUserModel.getAuthHeaders());
        } else {

//            injectFromDbtoModel();
//            REFRESH_PEOPLE = 0;
        }
    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("PEOPLELISTING")) {
                    if (response != null) {
                        try {
                            totalRecordsCount = countOfTotalRecords(response);
                            if (isFromGlobalSearch) {
                                peopleListingModelList.addAll(generateOnlinePeoplelistingModel(response));
                                peopleListingAdapter.refreshList(peopleListingModelList);
                            } else {
                                db.injectPeopleListingListIntoSqLite(response, TABBALUE, pageIndex);
                                injectFromDbtoModel();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                if (requestType.equalsIgnoreCase("PEOPLELISTINGTABS")) {
                    if (response != null) {
                        try {
                            db.injectPeopleListingListIntoSqLite(response, TABBALUE, pageIndex);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }


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
//                Log.d(TAG, "Volley String post" + response);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("REMOVEACTION")) {
                    if (response != null) {
                        if (isDigimedica) {
                            pageIndex = 1;
                            getPeopleList(true);
                        } else {
                            refreshPeopleListing(true);
                        }
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(response)
                                .setCancelable(false)
                                .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.dismiss();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();


                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }

                if (requestType.equalsIgnoreCase("ACCEPTACTION")) {
                    if (response != null) {

//                        Log.d(TAG, "notifySuccess: in  ACCEPTACTION " + response);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(response)
                                .setCancelable(false)
                                .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.dismiss();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                        if (isDigimedica) {
                            pageIndex = 1;
                            getPeopleList(true);
                        } else {
                            refreshPeopleListing(false);
                        }

                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }

                if (requestType.equalsIgnoreCase("ADDACTION")) {
                    if (response != null) {
//                        Log.d(TAG, "notifySuccess: in  ADDACTION " + response);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(response)
                                .setCancelable(false)
                                .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.dismiss();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                        if (isDigimedica) {
                            pageIndex = 1;
                            getPeopleList(true);

                        } else {
                            refreshPeopleListing(false);
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    svProgressHUD.dismiss();
                }

                swipeRefreshLayout.setRefreshing(false);

                if (requestType.equalsIgnoreCase("PEOPLELISTING")) {
                    if (response != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            totalRecordsCount = countOfTotalRecords(jsonObj);
                            if (isFromGlobalSearch) {
                                peopleListingModelList.addAll(generateOnlinePeoplelistingModel(jsonObj));
                                peopleListingAdapter.refreshList(peopleListingModelList);
                            } else {
                                db.injectPeopleListingListIntoSqLite(jsonObj, TABBALUE, pageIndex);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.peoplelisting_fragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);


        peopleListingAdapter = new PeopleListingAdapter(getActivity(), BIND_ABOVE_CLIENT, peopleListingModelList);
        peopleListView.setAdapter(peopleListingAdapter);
        peopleListView.setOnItemClickListener(this);
        peopleListView.setEmptyView(rootView.findViewById(R.id.nodata_label));


        final View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loadmore, null, false);
        peopleListView.addFooterView(footerView);
        progressBar = (ProgressBar) footerView.findViewById(R.id.loadMoreProgressBar);

        peopleListView.setOnScrollListener(new EndlessScrollListener() {

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

                    Log.d(TAG, "onLoadMore size: peopleListingModelList" + peopleListingModelList.size());

                    Log.d(TAG, "onLoadMore size: totalRecordsCount" + totalRecordsCount);


                    if (userScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                        userScrolled = false;

                        if (!isSearching) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (isNetworkConnectionAvailable(getContext(), -1)) {
                                if (isFromGlobalSearch) {
                                    refreshPeopleListingGlobalListing(true);
                                } else {

                                    if (isDigimedica) {
                                        getPeopleList(true);
                                    } else {
                                        refreshPeopleListing(true);
                                    }
                                }

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "   " + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet) + "   ", Toast.LENGTH_SHORT).show();
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

            }
        });
        expertsBtn.setVisibility(View.GONE);
        pendingBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        allPBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        expertsBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        myConBtn.setTextColor(getResources().getColor(R.color.colorWhite));

        expertsBtn.setTypeface(null, Typeface.BOLD);

        segmentedSwitch.setOnCheckedChangeListener(this);

        if (isFromGlobalSearch) {
            segmentedSwitch.setVisibility(View.GONE);
        }

        segmentedSwitch.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        pendingBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        expertsBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        allPBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        myConBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        updateLocalazation();
        peopleListingModelList = new ArrayList<PeopleListingModel>();

        if (isNetworkConnectionAvailable(getContext(), -1) && REFRESH_PEOPLE == 0) {
            if (isFromGlobalSearch) {
                refreshPeopleListingGlobalListing(false);
            } else {

                if (isDigimedica) {
                    getPeopleList(false);
                } else {
                    refreshPeopleListing(false);
                }
            }

        } else {
            injectFromDbtoModel();
        }

        initilizeView();

        return rootView;
    }

    public void updateLocalazation() {
        pendingBtn.setText(getLocalizationValue(JsonLocalekeys.myconnections_tabbutton_pendingrequestsbutton));
        expertsBtn.setText(getLocalizationValue(JsonLocalekeys.myconnections_tabbutton_expertssbutton));
        allPBtn.setText(getLocalizationValue(JsonLocalekeys.myconnections_tabbutton_allpeoplesbutton));
        myConBtn.setText(getLocalizationValue(JsonLocalekeys.myconnections_tabbutton_myconnectinsbutton));
    }

    public void injectFromDbtoModel() {
        peopleListingModelList = db.fetchPeopleListModelList(TABBALUE);
        if (peopleListingModelList != null) {
            peopleListingAdapter.refreshList(peopleListingModelList);
        } else {
            peopleListingModelList = new ArrayList<PeopleListingModel>();
            peopleListingAdapter.refreshList(peopleListingModelList);
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
        }

        if (peopleListingModelList.size() == pageSize) {
            pageIndex = 2;
        } else {
            pageIndex = peopleListingModelList.size() / pageSize;
            pageIndex = pageIndex + 1;
        }

//        if (peopleListingModelList.size() > 5) {
//            if (item_search != null) {
//                item_search.setVisible(true);
//            }
//        } else {
//            if (item_search != null) {
//                item_search.setVisible(false);
//            }
//        }

//        if (uiSettingsModel.isGlobasearch() && queryString.length() > 0) {
//
//            peopleListingAdapter.filter(queryString);
//
//        }

    }

    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);


        communicator = new Communicator() {
            @Override
            public void messageRecieved(JSONArray messageReceived) {

                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.people_label_receive_chatlist) + messageReceived, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void userOnline(int typeUpdate, JSONArray objReceived) {

            }

        };

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

        if (responMap != null && responMap.containsKey("ShowIndexes")) {
            String showIndexes = responMap.get("ShowIndexes");
            if (showIndexes.equalsIgnoreCase("top")) {
                item_filter.setVisible(true);
            }
        } else {
            // No such key
            item_filter.setVisible(false);
        }

        if (isFromGlobalSearch) {
            item_search.setVisible(false);
            swipeRefreshLayout.setEnabled(false);
        }

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

            item_search.setTitle(getLocalizationValue(JsonLocalekeys.search_label));
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint(getLocalizationValue(JsonLocalekeys.commoncomponent_label_searchlabel));

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

                    peopleListingAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });

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

        if (item_filter != null) {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_filter, context, uiSettingsModel.getAppHeaderTextColor());
            item_filter.setIcon(filterDrawable);
            item_filter.setTitle("Filter");

        }
    }

    public void gotoGlobalSearch() {

        Intent intent = new Intent(context, GlobalSearchActivity.class);
        intent.putExtra("sideMenusModel", sideMenusModel);
        startActivityForResult(intent, GLOBAL_SEARCH);

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
//                item_search.expandActionView();
                break;
            case R.id.mylearning_info_help:
//                Log.d(TAG, "onOptionsItemSelected :mylearning_info_help ");
                appcontroller.setAlreadyViewd(false);
                preferencesManager.setStringValue("false", StaticValues.KEY_HIDE_ANNOTATION);
                peopleListingAdapter.notifyDataSetChanged();
                break;
            case R.id.mylearning_filter:
                advancedFilters();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void advancedFilters() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            if (contentFilterByModelList.size() == 0) {
                contentFilterByModelList = generateContentFilters();
            }

            if (contentFilterByModelList != null && contentFilterByModelList.size() > 0) {

                Intent intent = new Intent(context, ContentFilterByActivity.class);
                intent.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intent.putExtra("allFilterModel", "");
                intent.putExtra("isFrom", 2);
                intent.putExtra("contentFilterByModelList", (Serializable) contentFilterByModelList);
                startActivityForResult(intent, FILTER_CLOSE_CODE_ADV);

            }
        } else {
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }

    }

    public List<ContentFilterByModel> generateContentFilters() {
        List<ContentFilterByModel> contentFilterByModelList = new ArrayList<>();
        if (contentFilterType != null && contentFilterType.length() > 0) {

            List<String> filterCategoriesArray = getArrayListFromString(contentFilterType);

            if (filterCategoriesArray != null && filterCategoriesArray.size() > 0) {
                for (int i = 0; i < filterCategoriesArray.size(); i++) {
                    ContentFilterByModel contentFilterByModel = new ContentFilterByModel();
                    // skills,jobroles,locations,userinfo,company
                    switch (filterCategoriesArray.get(i)) {
                        case "locations":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "locations";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_location);
                            contentFilterByModel.goInside = true;
                            break;
                        case "skills":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "skills";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_byskills);
                            contentFilterByModel.goInside = true;
                            break;
                        case "jobroles":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "jobroles";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_jobroles_header);
                            contentFilterByModel.goInside = false;
                            break;
                        case "company":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "company";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_company);
                            contentFilterByModel.goInside = false;
                            break;
                        case "userinfo":
                            contentFilterByModel.categoryName = "Userinfo";
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "userinfo";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_userinfotitlelabel);
                            contentFilterByModel.goInside = false;
                            break;
                    }
                    if (contentFilterByModel.categoryID.length() != 0) {
                        contentFilterByModelList.add(contentFilterByModel);
                    }
                }
            }
        }
        return contentFilterByModelList;

    }

    public List<AllFilterModel> getAllFilterModelList() {

        List<AllFilterModel> allFilterModelList = new ArrayList<>();

        AllFilterModel advFilterModel = new AllFilterModel();
        advFilterModel.categoryName = getLocalizationValue(JsonLocalekeys.filter_lbl_filterbytitlelabel);
        advFilterModel.categoryID = 1;
        allFilterModelList.add(advFilterModel);

        if (responMap != null && responMap.containsKey("EnableGroupby")) {
            String enableGroupby = responMap.get("EnableGroupby");
            if (enableGroupby != null && enableGroupby.equalsIgnoreCase("true")) {
                AllFilterModel groupFilterModel = new AllFilterModel();
                groupFilterModel.categoryName = getLocalizationValue(JsonLocalekeys.filter_lbl_groupbytitlelabel);
                groupFilterModel.categoryID = 2;
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

        AllFilterModel sortFilterModel = new AllFilterModel();
        sortFilterModel.categoryName = getLocalizationValue(JsonLocalekeys.filter_lbl_sortbytitlelabel);
        sortFilterModel.categoryID = 3;
        allFilterModelList.add(sortFilterModel);

        return allFilterModelList;
    }

    public List<String> getArrayListFromString(String questionCategoriesString) {

        List<String> questionCategoriesArray = new ArrayList<>();

        if (questionCategoriesString.length() <= 0)
            return questionCategoriesArray;

        questionCategoriesArray = Arrays.asList(questionCategoriesString.split(","));

        return questionCategoriesArray;

    }


    @Override
    public void onRefresh() {
        pageIndex = 1;
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            queryString = "";
            if (isDigimedica) {
                getPeopleList(true);
            } else {
                refreshPeopleListing(true);
            }
            MenuItemCompat.collapseActionView(item_search);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
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
                    showToast(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet));
                }
                break;
            case R.id.btn_contextmenu:
                View v = peopleListView.getChildAt(position - peopleListView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                peopelContextMenuMethod(position, view, txtBtnDownload, peopleListingModelList.get(position), uiSettingsModel, appUserModel);
                break;
            default:

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");
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

    public void peopelContextMenuMethod(final int position, final View v, ImageButton btnselected, final PeopleListingModel peopleListingModel, UiSettingsModel uiSettingsModel, final AppUserModel userModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.peoplecontextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_viewprofileoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_viewcontentoption));
        menu.getItem(2).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_acceptconnectionoption));
        menu.getItem(3).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_removeconnectionoption));
        menu.getItem(4).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_ignoreconnectionoption));
        menu.getItem(5).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_sendmessageoption));
        menu.getItem(6).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_addtomyconnectionsoption));
        menu.getItem(7).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_canceloption));
        menu.getItem(8).setTitle(getLocalizationValue(JsonLocalekeys.myconnections_actionsheet_askaquestionoption));
        menu.getItem(0).setVisible(false);//view profile
        menu.getItem(1).setVisible(false);//view content
        menu.getItem(2).setVisible(false);//accept connection
        menu.getItem(3).setVisible(false);//remove connection
        menu.getItem(4).setVisible(false);//ignore enrollment
        menu.getItem(5).setVisible(false);//send message
        menu.getItem(6).setVisible(false);//add to my connection
        menu.getItem(7).setVisible(false);
        menu.getItem(8).setVisible(false);

        if (peopleListingModel.viewProfileAction) {
            menu.getItem(0).setVisible(true);
        }

        if (peopleListingModel.viewContentAction) {
//            menu.getItem(1).setVisible(true); content
        }

        // comment for cvcta
        if (peopleListingModel.sendMessageAction) {
            menu.getItem(5).setVisible(true);
        }

        if (peopleListingModel.addToMyConnectionAction) {
            menu.getItem(6).setVisible(true);
        }

        if (peopleListingModel.acceptAction) {
            menu.getItem(2).setVisible(true);
        }

        if (peopleListingModel.removeFromMyConnectionAction) {
            menu.getItem(3).setVisible(true);
        }

        if (peopleListingModel.ignoreAction) {
            menu.getItem(4).setVisible(true);
        }

        if (peopleListingModel.viewProfileAction) {
            menu.getItem(0).setVisible(true);
        }


        if (isaskQuestionEnabled && TABBALUE.equals("Experts")) {
            menu.getItem(8).setVisible(true);

        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.ctx_play) {
                    Intent intentDetail = new Intent(context, PeopleListingProfile.class);
                    intentDetail.putExtra("peopleListingModel", peopleListingModel);
                    startActivity(intentDetail);

//                    Toast.makeText(context, "View Profile", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.ctx_view) {

                    if (isNetworkConnectionAvailable(context, -1)) {

                        replaceFragment(peopleListingModelList.get(position));

                    } else {

                        showToast(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet));
                    }


                }

                if (item.getItemId() == R.id.ctx_detail) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        acceptConnectionAction(peopleListingModel);
                    } else {
                        Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }

                }

                if (item.getItemId() == R.id.ctx_report) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {

                        try {
                            recepientID = generateUserChatList(peopleListingModel.userID);
                            peopleListingModel.chatConnectionUserId = recepientID;
                            peopleListingModel.chatUserStatus = userStatus;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intentDetail = new Intent(context, ChatActivity.class);
                        intentDetail.putExtra("peopleListingModel", peopleListingModel);
                        startActivity(intentDetail);

                        signalAService = SignalAService.newInstance(context);
                        signalAService.startSignalA();

                    } else {
                        Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }

                }

                if (item.getItemId() == R.id.ctx_join) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(getLocalizationValue(JsonLocalekeys.myconnections_alertsubtitle_areyousurewanttoremoveconnection)).setTitle(getLocalizationValue(JsonLocalekeys.profile_alerttitle_stringconfirmation))
                                .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.myconnections_alertbutton_cancelbutton), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(getLocalizationValue(JsonLocalekeys.myconnections_alertbutton_removebutton), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();
                                removeConnectionAction(peopleListingModel);

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();


                    } else {
                        Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }
                }
                if (item.getItemId() == R.id.ctx_download) {
//                    Toast.makeText(context, "Ignore Connection", Toast.LENGTH_SHORT).show();

                }
                if (item.getItemId() == R.id.ctxaskaquestion) {

                    askAQuestionMethod(peopleListingModel);
                }

                if (item.getItemId() == R.id.ctx_addtocalender) {//

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        addConnectionAction(peopleListingModel);//BUG
                    } else {
                        Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });
        popup.show();//showing popup menu

    }

    public void askAQuestionMethod(PeopleListingModel peopleListingModel) {
        Intent intentDetail = new Intent(context, AskQuestionActivity.class);
        intentDetail.putExtra("EXPERTS", true);
        intentDetail.putExtra("EXPERTID", peopleListingModel.askaQuestion);
        startActivity(intentDetail);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GLOBAL_SEARCH && resultCode == RESULT_OK) {
            if (data != null) {
                queryString = data.getStringExtra("queryString");
                if (queryString.length() > 0) {
                    pageIndex = 1;
                    if (isDigimedica) {
                        getPeopleList(true);
                    } else {
                        refreshPeopleListing(true);
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
                        getPeopleList(true);
                    } else {
                        refreshPeopleListing(true);
                    }

                }
            }
        }


    }

    public void removeConnectionAction(PeopleListingModel peopleListingModel) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String paramsString = "SelectedObjectID="
                + peopleListingModel.userID
                + "&SelectAction=RemoveConnection&UserName="
                + peopleListingModel.userDisplayname + "&UserID=" + peopleListingModel.userID + "&mainSiteUserid=" + peopleListingModel.mainSiteUserID + "&SiteID=" + peopleListingModel.siteID + "&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "";

        String paramsEncodeString = paramsString.replaceAll(" ", "%20");

        vollyService.getStringResponseVolley("REMOVEACTION", appUserModel.getWebAPIUrl() + "/MobileLMS/PeopleListingActions?" + paramsEncodeString, appUserModel.getAuthHeaders());
    }

    public void acceptConnectionAction(PeopleListingModel peopleListingModel) {

        String paramsString = "SelectedObjectID="
                + peopleListingModel.userID
                + "&SelectAction=Accept&UserName="
                + peopleListingModel.userDisplayname + "&UserID=" + peopleListingModel.userID + "&mainSiteUserid=" + peopleListingModel.mainSiteUserID + "&SiteID=" + peopleListingModel.siteID + "&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "";

        String paramsEncodeString = paramsString.replaceAll(" ", "%20");

        vollyService.getStringResponseVolley("ACCEPTACTION", appUserModel.getWebAPIUrl() + "/MobileLMS/PeopleListingActions?" + paramsEncodeString, appUserModel.getAuthHeaders());
    }

    public void addConnectionAction(PeopleListingModel peopleListingModel) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String paramsString = "SelectedObjectID="
                + peopleListingModel.userID
                + "&SelectAction=AddConnection&UserName="
                + peopleListingModel.userDisplayname + "&UserID=" + peopleListingModel.userID + "&mainSiteUserid=" + peopleListingModel.mainSiteUserID + "&SiteID=" + peopleListingModel.siteID + "&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "";

        String paramsEncodeString = paramsString.replaceAll(" ", "%20");

        vollyService.getStringResponseVolley("ADDACTION", appUserModel.getWebAPIUrl() + "/MobileLMS/PeopleListingActions?" + paramsEncodeString, appUserModel.getAuthHeaders());
    }


    private void replaceFragment(PeopleListingModel peopleListingModel) {

        String backStateName = PeopleListing_fragment.class.getName();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        Bundle bundle = new Bundle();

        Catalog_fragment nextFrag = new Catalog_fragment();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

//        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

        REFRESH_PEOPLE = 1;
        bundle.putSerializable("sidemenumodel", sideMenusModel);
        bundle.putSerializable("peopleListingModel", peopleListingModel);

        bundle.putBoolean("ISFROMPEOPELLISTING", true);

        nextFrag.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_body, nextFrag)
                .addToBackStack(backStateName)
                .commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int isChecked) {
        MenuItemCompat.collapseActionView(item_search);
        switch (isChecked) {

            case R.id.expertsbtn:
                expertsBtn.setTypeface(null, Typeface.BOLD);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
                TABBALUE = "Experts";
                pageIndex = 1;
                nodata_Label.setText("");
                if (isDigimedica) {
                    getPeopleList(true);
                } else {
                    refreshPeopleListing(true);
                }
                break;
            case R.id.allPeoplebtn:
                allPBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
                TABBALUE = "All";
                pageIndex = 1;
                nodata_Label.setText("");
                if (isDigimedica) {
                    getPeopleList(true);
                } else {
                    refreshPeopleListing(true);
                }
                break;
            case R.id.myconnectionbtn:
                myConBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
                TABBALUE = "MyConnections";
                pageIndex = 1;
                nodata_Label.setText("");
                if (isDigimedica) {
                    getPeopleList(true);
                } else {
                    refreshPeopleListing(true);
                }
                break;
            case R.id.pendingbtn:
                pendingBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                TABBALUE = "Pending";
                pageIndex = 1;
                nodata_Label.setText("");
                if (isDigimedica) {
                    getPeopleList(true);
                } else {
                    refreshPeopleListing(true);
                }
                break;
            default:
                // Nothing to do
                TABBALUE = "Experts";
        }

    }

    public String generateUserChatList(String userID) throws JSONException {
        String receipent = "default";

        String chatListStr = preferencesManager.getStringValue(StaticValues.CHAT_LIST);
        if (chatListStr.length() > 10) {
//            Log.d(TAG, "log: ConnectionId users List jsonObject  -------------- " + jsonObject);
            JSONArray jsonArray = new JSONArray(chatListStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJsonOnj = jsonArray.getJSONObject(i);
                Log.d(TAG, "generateUserChatList: " + userJsonOnj);
                if (userJsonOnj.getString("ChatuserID").equalsIgnoreCase(userID)) {
                    receipent = userJsonOnj.getString("ConnectionId");
                    userStatus = userJsonOnj.getString("status");

                }
            }
        }
        return receipent;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        signalAService = SignalAService.newInstance(context);
        signalAService.stopSignalA();
    }

    public int countOfTotalRecords(JSONObject jsonObject) throws JSONException {

        int totalRecs = 0;

        if (jsonObject != null && jsonObject.length() > 0) {


            totalRecs = jsonObject.getInt("PeopleCount");
        }

        return totalRecs;
    }

    public List<PeopleListingModel> generateOnlinePeoplelistingModel(JSONObject jsonObject) throws JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("PeopleList");

        List<PeopleListingModel> peopleListingModelList = new ArrayList<>();

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            PeopleListingModel peopleListingModel = new PeopleListingModel();

            //ConnectionUserID
            if (jsonMyLearningColumnObj.has("ConnectionUserID")) {

                peopleListingModel.connectionUserID = jsonMyLearningColumnObj.getInt("ConnectionUserID");
            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("ObjectID")) {

                peopleListingModel.userID = jsonMyLearningColumnObj.get("ObjectID").toString();

            }
//            // JobTitle
//            if (jsonMyLearningColumnObj.has("JobTitle")) {
//
//                Spanned result = fromHtml(jsonMyLearningColumnObj.get("JobTitle").toString());
//
//                peopleListingModel.communitydescription = result.toString();
//
//            }
            // JobTitle
            if (jsonMyLearningColumnObj.has("JobTitle")) {

                peopleListingModel.jobTitle = jsonMyLearningColumnObj.get("JobTitle").toString();

            }
            // MainOfficeAddress
            if (jsonMyLearningColumnObj.has("MainOfficeAddress")) {

                peopleListingModel.mainOfficeAddress = jsonMyLearningColumnObj.getString("MainOfficeAddress");

            }

            // MemberProfileImage
            if (jsonMyLearningColumnObj.has("MemberProfileImage")) {
                peopleListingModel.memberProfileImage = jsonMyLearningColumnObj.getString("MemberProfileImage");

            }

            // UserDisplayname
            if (jsonMyLearningColumnObj.has("UserDisplayname")) {

                peopleListingModel.userDisplayname = jsonMyLearningColumnObj.getString("UserDisplayname");

            }
            // connectionstate
            if (jsonMyLearningColumnObj.has("connectionstate")) {

                peopleListingModel.connectionState = jsonMyLearningColumnObj.getString("connectionstate");

            }
            // connectionstateAccept
            if (jsonMyLearningColumnObj.has("connectionstateAccept")) {

                peopleListingModel.connectionStateAccept = jsonMyLearningColumnObj.get("connectionstateAccept").toString();

            }
            // ViewProfileAction
            if (jsonMyLearningColumnObj.has("ViewProfileAction")) {

                String viewprofileAction = jsonMyLearningColumnObj.getString("ViewProfileAction");

                if (isValidString(viewprofileAction)) {
                    peopleListingModel.viewProfileAction = true;
                } else {
                    peopleListingModel.viewProfileAction = false;
                }
            }

            // AcceptAction
            if (jsonMyLearningColumnObj.has("AcceptAction")) {

                String acceptAction = jsonMyLearningColumnObj.getString("AcceptAction");

                if (isValidString(acceptAction)) {
                    peopleListingModel.acceptAction = true;

                } else {
                    peopleListingModel.acceptAction = false;
                }
            }

            // IgnoreAction
            if (jsonMyLearningColumnObj.has("IgnoreAction")) {

                String ignoreAction = jsonMyLearningColumnObj.getString("IgnoreAction");

                if (isValidString(ignoreAction)) {
                    peopleListingModel.ignoreAction = true;

                } else {
                    peopleListingModel.ignoreAction = false;
                }
            }

            // ViewContentAction
            if (jsonMyLearningColumnObj.has("ViewContentAction")) {

                String viewContentAction = jsonMyLearningColumnObj.getString("ViewContentAction");

                if (isValidString(viewContentAction)) {
                    peopleListingModel.viewContentAction = true;

                } else {
                    peopleListingModel.viewContentAction = false;
                }
            }

            // SendMessageAction
            if (jsonMyLearningColumnObj.has("SendMessageAction")) {

                String sendMessageAction = jsonMyLearningColumnObj.getString("SendMessageAction");

                if (isValidString(sendMessageAction)) {
                    peopleListingModel.sendMessageAction = true;

                } else {
                    peopleListingModel.sendMessageAction = false;
                }
            }
            // AddToMyConnectionAction
            if (jsonMyLearningColumnObj.has("AddToMyConnectionAction")) {

                String addToMyConnectionAction = jsonMyLearningColumnObj.getString("AddToMyConnectionAction");

                if (isValidString(addToMyConnectionAction)) {
                    peopleListingModel.addToMyConnectionAction = true;

                } else {
                    peopleListingModel.addToMyConnectionAction = false;
                }
            }

            // RemoveFromMyConnectionAction
            if (jsonMyLearningColumnObj.has("RemoveFromMyConnectionAction")) {

                String removeFromMyConnectionAction = jsonMyLearningColumnObj.getString("RemoveFromMyConnectionAction");

                if (isValidString(removeFromMyConnectionAction)) {
                    peopleListingModel.removeFromMyConnectionAction = true;

                } else {
                    peopleListingModel.removeFromMyConnectionAction = false;
                }
            }

            // InterestAreas
            if (jsonMyLearningColumnObj.has("InterestAreas")) {

                peopleListingModel.interestAreas = jsonMyLearningColumnObj.getString("InterestAreas");

            }

            // connectionstateAccept
            if (jsonMyLearningColumnObj.has("NotaMember")) {

                peopleListingModel.notaMember = jsonMyLearningColumnObj.getInt("NotaMember");

            }


            peopleListingModel.tabID = "All";
            peopleListingModel.siteID = appUserModel.getSiteIDValue();
            peopleListingModel.mainSiteUserID = appUserModel.getUserIDValue();
            peopleListingModel.siteURL = appUserModel.getSiteURL();


            peopleListingModelList.add(peopleListingModel);
        }


        if (peopleListingModelList.size() == pageSize) {
            pageIndex = 2;
        } else {
            pageIndex = peopleListingModelList.size() / pageSize;
            pageIndex = pageIndex + 1;
        }

        return peopleListingModelList;
    }

    public void getPeopleList(Boolean isRefreshed) {

        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/GetPeopleList";
        JSONObject parameters = new JSONObject();
        try {

            parameters.put("AdditionalParams", "");
            parameters.put("ComponentID", sideMenusModel.getComponentId());
            parameters.put("ComponentInstanceID", sideMenusModel.getRepositoryId());
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("sortBy", "");
            parameters.put("sortType", "");
            parameters.put("pageIndex", pageIndex);
            parameters.put("pageSize", pageSize);
            parameters.put("filterType", TABBALUE);
            parameters.put("TabID", "");
            parameters.put("SearchText", queryString);
            parameters.put("contentid", "");
            parameters.put("location", applyFilterModel.locations);
            parameters.put("company", applyFilterModel.company);
            parameters.put("skilllevels", "");
            parameters.put("firstname", applyFilterModel.firstName);
            parameters.put("lastname", applyFilterModel.lastName);
            parameters.put("skillcats", applyFilterModel.skillCats);
            parameters.put("skills", applyFilterModel.skills);
            parameters.put("jobroles", applyFilterModel.jobRoles);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "PEOPLELISTING", urlStr);
    }

}