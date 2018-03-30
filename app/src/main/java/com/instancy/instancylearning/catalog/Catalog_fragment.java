package com.instancy.instancylearning.catalog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.blankj.utilcode.util.ImageUtils;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.MembershipModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.filter.Filter_activity;
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
import com.instancy.instancylearning.mylearning.MyLearningFragment;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.BACKTOMAINSITE;
import static com.instancy.instancylearning.utils.StaticValues.CATALOG_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CATALOG_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.IAP_LAUNCH_FLOW_CODE;
import static com.instancy.instancylearning.utils.Utilities.ConvertToDate;
import static com.instancy.instancylearning.utils.Utilities.generateHashMap;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringMethod;
import static com.instancy.instancylearning.utils.Utilities.isMemberyExpry;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.showToast;
import static com.instancy.instancylearning.utils.Utilities.tintMenuIcon;

/**
 * Created by Upendranath on 5/19/2017.
 */

public class Catalog_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, BillingProcessor.IBillingHandler, View.OnClickListener {

    String TAG = MyLearningFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;

    CatalogAdapter catalogAdapter;
    List<MyLearningModel> catalogModelsList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    String filterContentType = "", consolidationType = "all", sortBy = "", allowAddContentType = "";
    ResultListner resultListner = null;
    CmiSynchTask cmiSynchTask;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    BillingProcessor billingProcessor;
    boolean isFromCatogories = false;
    WebAPIClient webAPIClient;

    MembershipModel membershipModel = null;

    CustomFlowLayout category_breadcrumb = null;

    List<ContentValues> breadcrumbItemsList = null;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.mylearninglistview)
    ListView myLearninglistView;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    Communicator communicator;

    @BindView(R.id.uploadfloatmenu)
    FloatingActionMenu uploadFloatMenu;

    @BindView(R.id.fabAudio)
    FloatingActionButton fabAudio;

    @BindView(R.id.fabDocument)
    FloatingActionButton fabDocument;

    @BindView(R.id.fabImage)
    FloatingActionButton fabImage;

    @BindView(R.id.fabWebsiteURL)
    FloatingActionButton fabWebsiteURL;

    @BindView(R.id.fabVideo)
    FloatingActionButton fabVideo;

    boolean isFromNotification = false;

    String contentIDFromNotification = "";

    public static int REFRESH = 0;

    public Catalog_fragment() {


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
        membershipModel = new MembershipModel();
//        try {
//            communicator = (Communicator) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement Communicator");
//        }

        String isViewed = preferencesManager.getStringValue(StaticValues.KEY_HIDE_ANNOTATION);
//        synchData = new SynchData(context);
        if (isViewed.equalsIgnoreCase("true")) {
            appcontroller.setAlreadyViewd(true);
        } else {

            appcontroller.setAlreadyViewd(false);
        }
        vollyService = new VollyService(resultCallback, context);

        String apiKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxZKOgrgA0BACsUqzZ49Xqj1SEWSx/VNSQ7e/WkUdbn7Bm2uVDYagESPHd7xD6cIUZz9GDKczG/fkoShHZdMCzWKiq07BzWnxdSaWa4rRMr+uylYAYYvV5I/R3dSIAOCbbcQ1EKUp5D7c2ltUpGZmHStDcOMhyiQgxcxZKTec6YiJ17X64Ci4adb9X/ensgOSduwQwkgyTiHjklCbwyxYSblZ4oD8WE/Ko9003VrD/FRNTAnKd5ahh2TbaISmEkwed/TK4ehosqYP8pZNZkx/bMsZ2tMYJF0lBUl5i9NS+gjVbPX4r013Pjrnz9vFq2HUvt7p26pxpjkBTtkwVgnkXQIDAQAB";

        billingProcessor = new BillingProcessor(context, apiKey, this);

        webAPIClient = new WebAPIClient(context);

        sideMenusModel = null;
        HashMap<String, String> responMap = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");

            isFromNotification = bundle.getBoolean("ISFROMNOTIFICATIONS");

            if (isFromNotification) {

                contentIDFromNotification = bundle.getString("CONTENTID");
            }

            responMap = generateConditionsHashmap(sideMenusModel.getConditions());

            isFromCatogories = bundle.getBoolean("ISFROMCATEGORIES");
            catalogModelsList = new ArrayList<>();
            if (isFromCatogories) {

                catalogModelsList = (List<MyLearningModel>) bundle.getSerializable("cataloglist");
                breadcrumbItemsList = new ArrayList<ContentValues>();
                breadcrumbItemsList = (List<ContentValues>) bundle.getSerializable("breadicrumblist");
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

        if (responMap != null && responMap.containsKey("AllowAddContentType")) {
            allowAddContentType = responMap.get("AllowAddContentType");

        }

    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        String paramsString = "FilterCondition=" + filterContentType + "&SortCondition=" + sortBy + "&RecordCount=0&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=" + consolidationType + "&FilterID=-1&ComponentID=" + sideMenusModel.getComponentId() + "&CartID=&Locale=&CatalogPreferenceID=1&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=&DateOfMyLastAccess=&SingleBranchExpand=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1";

        vollyService.getJsonObjResponseVolley("CATALOGDATA", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileCatalogObjectsNew?" + paramsString, appUserModel.getAuthHeaders());
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
                            db.injectCatalogData(response, false);
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
                            db.insertFilterIntoDB(response, appUserModel);
                            Intent intent = new Intent(context, Filter_activity.class);
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
        View rootView = inflater.inflate(R.layout.fragment_mylearning, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (isFromCatogories) {
            breadCrumbPlusButtonInit(rootView);
        }
        catalogAdapter = new CatalogAdapter(getActivity(), BIND_ABOVE_CLIENT, catalogModelsList, false);
        myLearninglistView.setAdapter(catalogAdapter);
        myLearninglistView.setOnItemClickListener(this);
        myLearninglistView.setEmptyView(rootView.findViewById(R.id.nodata_label));

        if (BACKTOMAINSITE == 2) {
            CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;
            BACKTOMAINSITE = 0;
        }

        if (!isFromCatogories) {
            catalogModelsList = new ArrayList<MyLearningModel>();
            if (isNetworkConnectionAvailable(getContext(), -1) && CATALOG_FRAGMENT_OPENED_FIRSTTIME == 0) {
                refreshCatalog(false);
            } else {
                injectFromDbtoModel();
            }
        } else {
            swipeRefreshLayout.setEnabled(false);

            catalogAdapter.refreshList(catalogModelsList);
        }

        initilizeView();

        if (allowAddContentType.length() > 0) {
            fabActionMenusInitilization();
        }

        return rootView;
    }

    public void fabActionMenusInitilization() {
        uploadFloatMenu.setVisibility(View.VISIBLE);
        uploadFloatMenu.setClosedOnTouchOutside(true);
        uploadFloatMenu.setMenuButtonColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        uploadFloatMenu.getMenuIconView().setColorFilter(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
//        uploadFloatMenu.getMenuIconView().setImageDrawable(getDrawableFromStringHOmeMethod(R.string.fa_icon_cloud_upload, context, uiSettingsModel.getAppHeaderTextColor()));

        fabAudio.setOnClickListener(this);
        fabImage.setOnClickListener(this);
        fabDocument.setOnClickListener(this);
        fabWebsiteURL.setOnClickListener(this);
        fabVideo.setOnClickListener(this);

        fabAudio.setImageDrawable(getDrawableFromStringMethod(R.string.fa_icon_file_audio_o, context, uiSettingsModel.getAppHeaderTextColor()));
        fabImage.setImageDrawable(getDrawableFromStringMethod(R.string.fa_icon_image, context, uiSettingsModel.getAppHeaderTextColor()));
        fabVideo.setImageDrawable(getDrawableFromStringMethod(R.string.fa_icon_file_video_o, context, uiSettingsModel.getAppHeaderTextColor()));
        fabDocument.setImageDrawable(getDrawableFromStringMethod(R.string.fa_icon_file, context, uiSettingsModel.getAppHeaderTextColor()));
        fabWebsiteURL.setImageDrawable(getDrawableFromStringMethod(R.string.fa_icon_link, context, uiSettingsModel.getAppHeaderTextColor()));

        fabAudio.setColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        fabImage.setColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        fabVideo.setColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        fabDocument.setColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        fabWebsiteURL.setColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));


        fabAudio.setLabelText(getResources().getString(R.string.audio));
        fabImage.setLabelText(getResources().getString(R.string.image));
        fabVideo.setLabelText(getResources().getString(R.string.video));
        fabDocument.setLabelText(getResources().getString(R.string.document));
        fabWebsiteURL.setLabelText(getResources().getString(R.string.website_url));
    }

    public void injectFromDbtoModel() {
        catalogModelsList = db.fetchCatalogModel(sideMenusModel.getComponentId());
        if (catalogModelsList != null) {
            catalogAdapter.refreshList(catalogModelsList);
        } else {
            catalogModelsList = new ArrayList<MyLearningModel>();
            catalogAdapter.refreshList(catalogModelsList);
            nodata_Label.setText(getResources().getString(R.string.no_data));
        }

        if (catalogModelsList.size() > 5) {
            if (item_search != null) {
                item_search.setVisible(true);
            }
        } else {
            if (item_search != null) {
                item_search.setVisible(false);
            }

        }

        triggerActionForFirstItem();

        membershipModel = db.fetchMembership(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

    }

    public void triggerActionForFirstItem() {

        if (isFromNotification) {
            int selectedPostion = getPositionForNotification(contentIDFromNotification);
            myLearninglistView.setSelection(selectedPostion);
            Intent intentDetail = new Intent(getContext(), MyLearningDetail_Activity.class);
            intentDetail.putExtra("IFROMCATALOG", true);
            intentDetail.putExtra("myLearningDetalData", catalogModelsList.get(selectedPostion));
            startActivityForResult(intentDetail, DETAIL_CATALOG_CODE);

//            openDetailsPage(catalogModelsList.get(selectedPostion),false);
            isFromNotification = false;
        }

    }

    public int getPositionForNotification(String contentID) {
        int position = 0;

        for (int k = 0; k < catalogModelsList.size(); k++) {
            if (catalogModelsList.get(k).getContentID().equalsIgnoreCase(contentID)) {
                position = k;
                break;
            }

        }

        return position;
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
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);.
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

                    catalogAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });

        }

        if (item_filter != null) {
            item_filter.setIcon(R.drawable.filter_icon);
            tintMenuIcon(getActivity(), item_filter, R.color.colorWhite);
            Drawable myIcon = getResources().getDrawable(R.drawable.filter_icon);
            item_filter.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
            item_filter.setTitle("Filter");

        }

        if (itemInfo != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.help);
            itemInfo.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
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
                filterApiCall();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        if (isNetworkConnectionAvailable(getContext(), -1)) {
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
                    downloadTheCourse(catalogModelsList.get(position), view, position);
                } else {
                    showToast(context, "No Internet");
                }
                break;
            case R.id.btn_contextmenu:
                View v = myLearninglistView.getChildAt(position - myLearninglistView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, catalogModelsList.get(position), uiSettingsModel, appUserModel);
                break;
            case R.id.imagethumb:
            case R.id.card_view:
                openDetailsPage(catalogModelsList.get(position));
                break;
            default:

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

        CATALOG_FRAGMENT_OPENED_FIRSTTIME = 1;
    }

    @Override
    public void onPause() {
        super.onPause();
//        CATALOG_FRAGMENT_OPENED_FIRSTTIME = 1;
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
        popup.getMenuInflater().inflate(R.menu.catalog_contextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(false);//view
        menu.getItem(1).setVisible(false);//add
        menu.getItem(2).setVisible(false);//buy
        menu.getItem(3).setVisible(false);//detail
        menu.getItem(4).setVisible(false);//delete

//        boolean subscribedContent = databaseH.isSubscribedContent(myLearningDetalData);

        if (myLearningDetalData.getAddedToMylearning() == 1) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);

            if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {

                File myFile = new File(myLearningDetalData.getOfflinepath());

                if (myFile.exists()) {

                    menu.getItem(4).setVisible(false);

                } else {

                    menu.getItem(4).setVisible(false);
                }
            }

        } else {
            if (myLearningDetalData.getViewType().equalsIgnoreCase("1")) {

                if (myLearningDetalData.getAddedToMylearning() == 0) {
                    menu.getItem(0).setVisible(true);
                } else {
                    menu.getItem(0).setVisible(false);
                }
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

                    File myFile = new File(myLearningDetalData.getOfflinepath());

                    if (myFile.exists()) {

                        menu.getItem(4).setVisible(true);

                    } else {

                        menu.getItem(4).setVisible(false);
                    }

                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
                        menu.getItem(4).setVisible(false);
                    }
//                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {
//
//                        menu.getItem(4).setVisible(true);
//                    }
                }
            } else if (myLearningDetalData.getViewType().equalsIgnoreCase("3")) {
                boolean isMemberExpired = isMemberyExpry(membershipModel.expirydate);
                if (isMemberExpired) {
                    menu.getItem(0).setVisible(false);
                    menu.getItem(2).setVisible(true);
                    menu.getItem(3).setVisible(true);
                    menu.getItem(1).setVisible(false);
                } else {
                    if (membershipModel.membershiplevel >= myLearningDetalData.getMemberShipLevel()) {
                        menu.getItem(1).setVisible(true); //add

                    } else {
                        menu.getItem(2).setVisible(true);// buy

                    }
                    menu.getItem(0).setVisible(false);
                    menu.getItem(3).setVisible(true);
                }
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("Details")) {

//                    openDetailsPage(myLearningDetalData);

                    Intent intentDetail = new Intent(getContext(), MyLearningDetail_Activity.class);
                    intentDetail.putExtra("IFROMCATALOG", true);
                    intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
                    ((Activity) getContext()).startActivityForResult(intentDetail, DETAIL_CATALOG_CODE);

                }
                if (item.getTitle().toString().equalsIgnoreCase("View")) {


                    if (myLearningDetalData.getAddedToMylearning() == 0 && myLearningDetalData.getViewType().equalsIgnoreCase("1")) {
                        GlobalMethods.launchCoursePreviewViewFromGlobalClass(myLearningDetalData, v.getContext());
                    } else {
                        GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext());
                    }


                }

                if (item.getTitle().toString().equalsIgnoreCase("Download")) {
//                    Toast.makeText(v.getContext(), "You Clicked : " + item.getTitle() + " on position " + position, Toast.LENGTH_SHORT).show();

                }

                if (item.getTitle().toString().equalsIgnoreCase("Delete")) {
//                    deleteDownloadedFile(v, myLearningDetalData, downloadInterface);

                }
                if (item.getTitle().toString().equalsIgnoreCase("Add")) {

//                    addToMyLearning(myLearningDetalData);
                    addToMyLearningCheckUser(myLearningDetalData, position, false);
                }
                if (item.getTitle().toString().equalsIgnoreCase("Buy")) {
                    addToMyLearningCheckUser(myLearningDetalData, position, true);
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

                if (isInapp) {
                    inAppActivityCall(myLearningDetalData);

                } else {
                    addToMyLearning(myLearningDetalData, position, false, false);

                }
            }
        }
    }

    public void addToMyLearning(final MyLearningModel myLearningDetalData, final int position, final boolean isAutoAdd, final boolean isJoinedCommunity) {

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
                            catalogModelsList.get(position).setAddedToMylearning(1);

                            db.updateContenToCatalog(catalogModelsList.get(position));
                            catalogAdapter.notifyDataSetChanged();
                            getMobileGetMobileContentMetaData(myLearningDetalData, position);
                            if (!isAutoAdd) {
                                String succesMessage = "Content Added to My Learning";
                                if (isJoinedCommunity) {
                                    succesMessage = "This content item has been added to My Learning page. You have successfully joined the Learning Community: " + myLearningDetalData.getSiteName();
                                }
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(succesMessage)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

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
                ;
                VolleySingleton.getInstance(context).addToRequestQueue(strReq);

            }
        }
    }

    public void checkUserLogin(final MyLearningModel learningModel, final int position, final boolean isInapp) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                + learningModel.getUserName() + "&Password=" + learningModel.getPassword() + "&MobileSiteURL="
                + learningModel.getSiteURL() + "&DownloadContent=&SiteID=" + learningModel.getSiteID();

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

                                        if (isInapp) {

                                            inAppActivityCall(learningModel);

                                        } else {
                                            learningModel.setUserID(userIdresponse);
                                            addToMyLearning(learningModel, position, false, true);
                                        }

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

    public void getMobileGetMobileContentMetaData(final MyLearningModel learningModel, final int position) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/MobileGetMobileContentMetaData?SiteURL="
                + learningModel.getSiteURL() + "&ContentID=" + learningModel.getContentID() + "&userid="
                + appUserModel.getUserIDValue() + "&DelivoryMode=1";

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
                                if (isInserted) {
                                    catalogModelsList.get(position).setAddedToMylearning(1);
//                                    db.updateContenToCatalog(catalogModelsList.get(position));
                                    catalogAdapter.notifyDataSetChanged();
//                                    Toast toast = Toast.makeText(
//                                            context,
//                                            context.getString(R.string.cat_add_success),
//                                            Toast.LENGTH_SHORT);
//                                    toast.setGravity(Gravity.CENTER, 0, 0);
//                                    toast.show();

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

    public void inAppActivityCall(MyLearningModel learningModel) {
        preferencesManager.setStringValue(learningModel.getContentID(), "contentid");
        if (!BillingProcessor.isIabServiceAvailable(context)) {

            Toast.makeText(context, "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16", Toast.LENGTH_SHORT).show();
        }

        String testId = "android.test.purchased";
        String productId = "com.instancy.managedproduct";
        String originalproductid = learningModel.getGoogleProductID();

        if (originalproductid.length() != 0) {
            Intent intent = new Intent();
            intent.putExtra("learningdata", learningModel);
            billingProcessor.handleActivityResult(IAP_LAUNCH_FLOW_CODE, 80, intent);
            billingProcessor.purchase(getActivity(), originalproductid);
        } else {
            Toast.makeText(context, "Inapp id not configured in server", Toast.LENGTH_SHORT).show();
        }

//        billingProcessor.purchase(MyLearningDetail_Activity.this, "com.foundationcourseforpersonal.managedproduct");
//        String productid = null;
//        productid = learningModel.getGoogleProductID();

//        SkuDetails sku = billingProcessor.getPurchaseListingDetails(testId);

//        Toast.makeText(this, sku != null ? sku.toString() : "Failed to load SKU details", Toast.LENGTH_SHORT).show();


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
                                addToMyLearning(finalLearningModel, finalPosition, false, false);
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
                    injectFromDbtoModel();
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
                    cmiSynchTask = new CmiSynchTask(context);
                    cmiSynchTask.execute();
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

    public void filterApiCall() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
            String urlStr = "UserID=1&ComponentID=3&SiteID=374&ShowAllItems=true&FilterContentType=&FilterMediaType=&SprateEvents=&EventType=&IsCompetencypath=false&LocaleID=en-us&CompInsID=3134";
            vollyService.getJsonObjResponseVolley("FILTER", appUserModel.getWebAPIUrl() + "/Mobilelms/GetMyLearningFilters?" + urlStr, appUserModel.getAuthHeaders());

        } else {

            JSONObject jsonObject = db.fetchFilterObject(appUserModel);
            if (jsonObject != null) {

                Intent intent = new Intent(context, Filter_activity.class);
                startActivityForResult(intent, FILTER_CLOSE_CODE);

            } else {
                Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void breadCrumbPlusButtonInit(View rootView) {

        LinearLayout llCatalogGridCatageory = (LinearLayout) rootView.findViewById(R.id.llCatalogGridCatageory);
        llCatalogGridCatageory.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppBGColor())));
        category_breadcrumb = (CustomFlowLayout) rootView.findViewById(R.id.cflBreadcrumb);

        llCatalogGridCatageory.setVisibility(View.VISIBLE);
        generateBreadcrumb(breadcrumbItemsList);

    }


    @Override
    public void onDetach() {
        communicator = null;
        super.onDetach();
    }

    public void generateBreadcrumb(List<ContentValues> dicBreadcrumbItems) {
        boolean isFirstCategory = true;
        ContentValues cvBreadcrumbItem = null;
        category_breadcrumb.removeAllViews();
        int breadcrumbCount = 0;
        if (null != dicBreadcrumbItems) {
            breadcrumbCount = dicBreadcrumbItems.size();
        }
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
                removeItemFromBreadcrumbList(categoryLevel);
                generateBreadcrumb(breadcrumbItemsList);
                if (getFragmentManager().getBackStackEntryCount() > 0) {
//                    try {
//                        communicator.breadCrumbStatus(breadcrumbItemsList);
//                    } catch (NullPointerException e) {
//                        throw new ClassCastException(context.toString()
//                                + " must implement IFragmentToActivity");
//                    }
                    getFragmentManager().popBackStack();

                }
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

            arrowView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppHeaderColor() + "'><medium><b>"
                    + getResources().getString(R.string.fa_icon_angle_right) + "</b></big> </font>"));

//            arrowView.setText(getResources().getString(R.string.fa_icon_forward));
//            arrowView.setTextColor(getResources().getColor(R.color.colorPrimary)));
            arrowView.setTextSize(20);


            arrowView.setGravity(Gravity.CENTER | Gravity.LEFT);
            // String text = coountries[i];
            cvBreadcrumbItem = dicBreadcrumbItems.get(i);
            String categoryId = cvBreadcrumbItem.getAsString("categoryid");
            String categoryName = cvBreadcrumbItem.getAsString("categoryname");

            textView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppHeaderColor() + "'><big><b><u>"
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

    public void removeItemFromBreadcrumbList(int categoryLevel) {
        breadcrumbItemsList = breadcrumbItemsList.subList(0, categoryLevel + 1);
    }

    public void downloadTheCourse(final MyLearningModel learningModel, final View view, final int position) {

        if (learningModel.getAddedToMylearning() == 0) {
            addToMyLearning(learningModel, position, true, false);
        }
//        else {
//
//        }
        boolean isZipFile = false;

        final String[] downloadSourcePath = {null};


        switch (learningModel.getObjecttypeId()) {
            case "52":
                downloadSourcePath[0] = learningModel.getSiteURL() + "/content/sitefiles/"
                        + learningModel.getSiteID() + "/usercertificates/" + learningModel.getSiteID() + "/"
                        + learningModel.getContentID() + ".pdf";
                isZipFile = false;
                break;
            case "11":
            case "14":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getStartPage();
                isZipFile = false;
                break;
            case "8":
            case "9":
            case "10":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
                isZipFile = true;
                break;
            default:
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID()
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
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/downloadfiles/"
                                + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], view, learningModel, position);

                    } else {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                                + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
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
                    + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID();

        } else {
            downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                    + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID() + localizationFolder;
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
                            File zipfile = new File(zipFile);
                            zipfile.delete();
                        }
                        catalogAdapter.notifyDataSetChanged();

                        if (learningModel.getObjecttypeId().equalsIgnoreCase("10")) {
                            if (!learningModel.getStatus().equalsIgnoreCase("Not Started")) {
                                callMobileGetContentTrackedData(learningModel);
                                callMobileGetMobileContentMetaData(learningModel);
                            } else {
                                callMobileGetMobileContentMetaData(learningModel);

                            }

                        } else {
                            if (!learningModel.getStatus().equalsIgnoreCase("Not Started")) {
                                callMobileGetContentTrackedData(learningModel);

                            }

                        }


                        // write jw content method download


                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Log.d(TAG, "onDownloadFailed: " + +errorCode);
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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fabVideo:
                wikiFileUploadButtonClicked("ContentTypeID=11&MediaTypeID=3", 1);
                break;
            case R.id.fabAudio:
                wikiFileUploadButtonClicked("ContentTypeID=11&MediaTypeID=4", 1);
                break;
            case R.id.fabDocument:
                wikiFileUploadButtonClicked("ContentTypeID=14", 1);
                break;
            case R.id.fabImage:
                wikiFileUploadButtonClicked("ContentTypeID=11&MediaTypeID=1", 1);
                break;
            case R.id.fabWebsiteURL:
                wikiFileUploadButtonClicked("ContentTypeID=28&MediaTypeID=13", 1);
                break;

        }
    }

    public void wikiFileUploadButtonClicked(String contentMediaTypeID, int intsr) {

        if (isNetworkConnectionAvailable(getContext(), -1)) {

            String android_Id = Settings.Secure.getString(getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            String urlString = appUserModel.getSiteURL() + "/PublicModules/UserLoginVerify.aspx?Fileupload=true&ComponentID=" + sideMenusModel.getComponentId() + "&CMSGroupID=3&" + contentMediaTypeID + "&userid=" + appUserModel.getUserIDValue() + "&deviceid=" + android_Id + "&devicetype=ios";

            if (intsr == 1) {
                openChromeTabsInAndroid(urlString);
            } else {

                Intent intentSocial = new Intent(context, SocialWebLoginsActivity.class);
                intentSocial.putExtra("ATTACHMENT", true);
                intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, urlString);
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, "");
                Log.d(TAG, "wikiFileUploadButtonClicked: " + urlString);
                startActivity(intentSocial);
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    public void openChromeTabsInAndroid(String urlStr) {

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setToolbarColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()))
                .setShowTitle(false).enableUrlBarHiding()
                .build();
        REFRESH = 1;
        customTabsIntent.launchUrl(context, Uri.parse(urlStr));
    }

    //    // Main action: create notification 12500+30000+50000+50000+100000+15868=258368 257280

    private static PendingIntent createPendingMainActionNotifyIntent(
            @NonNull final Context context,
            @NonNull final CustomTabsIntent action) {

        Intent actionIntent = new Intent(context, Catalog_fragment.class);
        return PendingIntent.getService(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (REFRESH == 1) {
            if (isNetworkConnectionAvailable(getContext(), -1)) {
                refreshCatalog(false);
            } else {
                injectFromDbtoModel();
            }
        }
        REFRESH = 0;
    }

    public void openDetailsPage(MyLearningModel learningModel) {

        if (learningModel.getViewType().equalsIgnoreCase("1") || learningModel.getViewType().equalsIgnoreCase("3") || learningModel.getViewType().equalsIgnoreCase("2")) {
            Intent intentDetail = new Intent(getContext(), MyLearningDetail_Activity.class);
            intentDetail.putExtra("IFROMCATALOG", true);
            intentDetail.putExtra("myLearningDetalData", learningModel);
            ((Activity) getContext()).startActivityForResult(intentDetail, DETAIL_CATALOG_CODE);

        }
    }

}