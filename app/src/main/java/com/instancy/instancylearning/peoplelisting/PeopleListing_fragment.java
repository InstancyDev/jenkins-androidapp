package com.instancy.instancylearning.peoplelisting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AllUserInfoModel;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningDetail_Activity;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CATALOG_CODE;
import static com.instancy.instancylearning.utils.StaticValues.EVENT_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.Utilities.generateHashMap;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
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
    String filterContentType = "", consolidationType = "all", sortBy = "";
    ResultListner resultListner = null;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    boolean isFromCatogories = false;

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

    String TABBALUE="Experts";


    public PeopleListing_fragment() {


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

        String isViewed = preferencesManager.getStringValue(StaticValues.KEY_HIDE_ANNOTATION);
        if (isViewed.equalsIgnoreCase("true")) {
            appcontroller.setAlreadyViewd(true);
        } else {

            appcontroller.setAlreadyViewd(false);
        }
        vollyService = new VollyService(resultCallback, context);

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

    }

    public void refreshPeopleListing(Boolean isRefreshed) {
        if (!isRefreshed) {

            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        String paramsString = "ComponentID=78&ComponentInstanceID=3473&UserID=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&Locale=en-us&FilterType="+TABBALUE;

        vollyService.getJsonObjResponseVolley("PEOPLELISTING", appUserModel.getWebAPIUrl() + "/MobileLMS/GetPeopleListData?" + paramsString, appUserModel.getAuthHeaders());
    }

    public void peopleListingTabsEitherFromDatabaseOrAPI() {

        String paramsString = "ComponentID=78&ComponentInstanceID=3394&SiteID=" + appUserModel.getSiteIDValue() + "&Locale=en-us";

        vollyService.getJsonObjResponseVolley("PEOPLELISTINGTABS", appUserModel.getWebAPIUrl() + "/MobileLMS/GetpeopleListingTab?" + paramsString, appUserModel.getAuthHeaders());
    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("PEOPLELISTING")) {
                    if (response != null) {
                        try {
                            db.injectPeopleListingListIntoSqLite(response,TABBALUE);
                            injectFromDbtoModel();
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
                            db.injectPeopleListingListIntoSqLite(response,TABBALUE);
//                            injectFromDbtoModel();
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
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("FILTER")) {

                    Toast.makeText(getContext(), "Filter is not configured", Toast.LENGTH_SHORT).show();
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

        peopleListingAdapter = new PeopleListingAdapter(getActivity(), BIND_ABOVE_CLIENT, peopleListingModelList);
        peopleListView.setAdapter(peopleListingAdapter);
        peopleListView.setOnItemClickListener(this);
        peopleListView.setEmptyView(rootView.findViewById(R.id.nodata_label));

        pendingBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        allPBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        expertsBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        myConBtn.setTextColor(getResources().getColor(R.color.colorWhite));

        expertsBtn.setTypeface(null, Typeface.BOLD);

        segmentedSwitch.setOnCheckedChangeListener(this);

        segmentedSwitch.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        pendingBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        expertsBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        allPBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        myConBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        peopleListingModelList = new ArrayList<PeopleListingModel>();
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshPeopleListing(false);
//            peopleListingTabsEitherFromDatabaseOrAPI();
        } else {
            injectFromDbtoModel();
        }

        initilizeView();

        return rootView; // 31732298673 //
    }


    public void injectFromDbtoModel() {
        peopleListingModelList = db.fetchPeopleListModelList(TABBALUE);
        if (peopleListingModelList != null) {
            peopleListingAdapter.refreshList(peopleListingModelList);
        } else {
            peopleListingModelList = new ArrayList<PeopleListingModel>();
            peopleListingAdapter.refreshList(peopleListingModelList);
        }
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
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint("Search..");
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));

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
                peopleListingAdapter.notifyDataSetChanged();
                break;
            case R.id.mylearning_filter:
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshPeopleListing(true);
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
                View v = peopleListView.getChildAt(position - peopleListView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
//                catalogContextMenuMethod(position, view, txtBtnDownload, allUserInfoModelList.get(position), uiSettingsModel, appUserModel);
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

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final MyLearningModel myLearningDetalData, UiSettingsModel uiSettingsModel, final AppUserModel userModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.event_contextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(false);//view
        menu.getItem(1).setVisible(false);//add
        menu.getItem(2).setVisible(false);//buy
        menu.getItem(3).setVisible(false);//detail
        menu.getItem(4).setVisible(false);//cancel enrollment

//        boolean subscribedContent = databaseH.isSubscribedContent(myLearningDetalData);

        if (myLearningDetalData.getAddedToMylearning() == 1) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(true);


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
            } else if (myLearningDetalData.getViewType().equalsIgnoreCase("3")) {
                menu.getItem(0).setVisible(false);
                menu.getItem(2).setVisible(true);
                menu.getItem(3).setVisible(true);
                menu.getItem(1).setVisible(false);
            }


        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("Details")) {
                    Intent intentDetail = new Intent(v.getContext(), MyLearningDetail_Activity.class);
                    intentDetail.putExtra("IFROMCATALOG", true);
                    intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
                    ((Activity) v.getContext()).startActivityForResult(intentDetail, DETAIL_CATALOG_CODE);

                }
                if (item.getTitle().toString().equalsIgnoreCase("Related Content")) {
//                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext());
                    GlobalMethods.relatedContentView(myLearningDetalData, v.getContext());
                }

                if (item.getTitle().toString().equalsIgnoreCase("Download")) {
//                    Toast.makeText(v.getContext(), "You Clicked : " + item.getTitle() + " on position " + position, Toast.LENGTH_SHORT).show();
                }

                if (item.getTitle().toString().equalsIgnoreCase("Cancel Enrollment")) {
//                    deleteDownloadedFile(v, myLearningDetalData, downloadInterface);

                }
                if (item.getTitle().toString().equalsIgnoreCase("Enroll")) {

//                    addToMyLearning(myLearningDetalData);

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAIL_CATALOG_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("REFRESH", false);
                if (refresh) {
                    injectFromDbtoModel();
                }
            }
        }
        if (requestCode == COURSE_CLOSE_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");

                File myFile = new File(myLearningModel.getOfflinepath());

                if (isNetworkConnectionAvailable(getContext(), -1)) {

                }

                if (!myFile.exists()) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        getStatusFromServer(myLearningModel);

                    }
                } else {


                }

                injectFromDbtoModel();
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


    @Override
    public void onDetach() {

        super.onDetach();
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int isChecked) {

        switch (isChecked) {
            case R.id.expertsbtn:
                expertsBtn.setTypeface(null, Typeface.BOLD);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
                sortByDate("before");
                break;
            case R.id.allPeoplebtn:
                allPBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
//                catalogAdapter.refreshList(catalogModelsList);
                break;
            case R.id.myconnectionbtn:
                myConBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
                sortByDate("after");
                break;
            case R.id.pendingbtn:
                pendingBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                sortByDate("after");
                break;
            default:
                // Nothing to do
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void sortByDate(String typeTime) {
//        List<MyLearningModel> myLearningModelList = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        if (typeTime.equalsIgnoreCase("before")) {
//
//            for (int i = 0; i < catalogModelsList.size(); i++) {
//                Date strDate = null;
//                String checkDate = catalogModelsList.get(i).getEventstartTime();
//
//                try {
//                    strDate = sdf.parse(checkDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                if (new Date().before(strDate)) {
//                    myLearningModelList.add(catalogModelsList.get(i));
//                    catalogAdapter.refreshList(myLearningModelList);
////                 Toast.makeText(context, typeTime + " if  event " + strDate, Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//
//        } else {
//
//            for (int i = 0; i < catalogModelsList.size(); i++) {
//                Date strDate = null;
//                String checkDate = catalogModelsList.get(i).getEventstartTime();
//
//                try {
//                    strDate = sdf.parse(checkDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                if (new Date().after(strDate)) {
//                    myLearningModelList.add(catalogModelsList.get(i));
//                    catalogAdapter.refreshList(myLearningModelList);
////                 Toast.makeText(context, typeTime + " if  event " + strDate, Toast.LENGTH_SHORT).show();
//                } else {
//
//                    catalogAdapter.refreshList(myLearningModelList);
//                }
//
//
//            }
//
//
//        }

    }
}