package com.instancy.instancylearning.catalog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
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
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.dinuscxj.progressbar.CircleProgressBar;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.instancy.instancylearning.advancedfilters_mylearning.AllFilterModel;
import com.instancy.instancylearning.advancedfilters_mylearning.AllFiltersActivity;

import com.instancy.instancylearning.advancedfilters_mylearning.ApplyFilterModel;
import com.instancy.instancylearning.advancedfilters_mylearning.ContentFilterByModel;
import com.instancy.instancylearning.events.PrerequisiteContentActivity;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mylearning.MyLearningDetailActivity1;
import com.instancy.instancylearning.normalfilters.AdvancedFilterActivity;
import com.instancy.instancylearning.globalsearch.GlobalSearchActivity;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.mainactivities.SignUp_Activity;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.MembershipModel;
import com.instancy.instancylearning.models.PeopleListingModel;

import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
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
import com.instancy.instancylearning.mylearning.MyLearningDetailActivity;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
import com.instancy.instancylearning.utils.EndlessScrollListener;
import com.instancy.instancylearning.utils.JsonLocalekeys;
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
import static com.instancy.instancylearning.utils.StaticValues.CATALOG_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CATALOG_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE_ADV;
import static com.instancy.instancylearning.utils.StaticValues.GLOBAL_SEARCH;
import static com.instancy.instancylearning.utils.StaticValues.IAP_LAUNCH_FLOW_CODE;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.PREREQ_CLOSE;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.generateHashMap;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringMethod;
import static com.instancy.instancylearning.utils.Utilities.isMemberyExpry;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.returnEventCompleted;
import static com.instancy.instancylearning.utils.Utilities.showToast;


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
    MenuItem item_search, itemWishList;

    boolean isWishlisted = false;
    int isWsh = 0;

    private DatePickerDialog datePickerDialog;

    String dueDate = "";

    SideMenusModel sideMenusModel = null;
    String filterContentType = "", consolidationType = "all", sortBy = "", allowAddContentType = "", ddlSortList = "", ddlSortType = "", contentFilterType = "";
    ResultListner resultListner = null;
    CmiSynchTask cmiSynchTask;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    BillingProcessor billingProcessor;

    boolean isFromCatogories = false;
    boolean isFromPeopleListing = false;
    boolean isFromMyCompetency = false;
    boolean isReportEnabled = true;
    boolean isFromNotification = false;
    boolean isFromGlobalSearch = false;
    boolean isIconEnabled = false;

    WebAPIClient webAPIClient;

    int pageIndex = 1, totalRecordsCount = 0, pageSize = 10;
    boolean isSearching = false;
    boolean userScrolled = false;

    ProgressBar progressBar;

    MembershipModel membershipModel = null;

    CustomFlowLayout category_breadcrumb = null;

    List<ContentValues> breadcrumbItemsList = new ArrayList<ContentValues>();


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

    String contentIDFromNotification = "";

    public static int REFRESH = 0;

    PeopleListingModel peopleListingModel;

    String skillID = "";
    String skillName = "";

    String queryString = "";

    List<ContentFilterByModel> contentFilterByModelList = new ArrayList<>();

    ApplyFilterModel applyFilterModel = new ApplyFilterModel();

    boolean isDigimedica = true;

    ActionBar actionBar = null;

    public Catalog_fragment() {


    }

    HashMap<String, String> responMap = null;

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
        cmiSynchTask = new CmiSynchTask(context);
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        membershipModel = new MembershipModel();
        isReportEnabled = db.isPrivilegeExistsFor(StaticValues.REPORTPREVILAGEID);
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
//        HashMap<String, String> responMap = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");

            isFromGlobalSearch = bundle.getBoolean("ISFROMGLOBAL", false);

            isFromNotification = bundle.getBoolean("ISFROMNOTIFICATIONS");

            isFromPeopleListing = bundle.getBoolean("ISFROMPEOPELLISTING", false);

            isFromMyCompetency = bundle.getBoolean("ISFROMMYCOMPETENCY", false);

            if (isFromNotification) {

                contentIDFromNotification = bundle.getString("CONTENTID");
            }
            if (isFromPeopleListing) {
                peopleListingModel = (PeopleListingModel) bundle.getSerializable("peopleListingModel");
            }
            if (isFromMyCompetency) {
                skillID = bundle.getString("SKILLID");
                skillName = bundle.getString("TITLENAME");
            }

            if (isFromGlobalSearch) {
                queryString = bundle.getString("query");
            }

            responMap = generateConditionsHashmap(sideMenusModel.getConditions());

            isFromCatogories = bundle.getBoolean("ISFROMCATEGORIES");
            catalogModelsList = new ArrayList<>();
            if (isFromCatogories) {

                catalogModelsList = (List<MyLearningModel>) bundle.getSerializable("cataloglist");

                breadcrumbItemsList = (List<ContentValues>) bundle.getSerializable("breadicrumblist");
                // applyFilterModel.categories = bundle.getString("categoryid");

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
            if (isFromPeopleListing) {
                sortBy = "";
            }
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

        if (responMap != null && responMap.containsKey("AllowAddContentType")) {
            allowAddContentType = responMap.get("AllowAddContentType");

        }

        if (responMap != null && responMap.containsKey("ContentFilterBy")) {
            contentFilterType = responMap.get("ContentFilterBy");
        } else {
            // No such key
            contentFilterType = "";
        }

        if (responMap != null && responMap.containsKey("ContentProperties")) {

            String contentProperties = responMap.get("ContentProperties");
            if (contentProperties.toLowerCase().contains("thumbnailiconpath"))
                isIconEnabled = true;
            else
                isIconEnabled = false;
        }
    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }
        sortBy = "c.publisheddate%20desc,c.name";

        String paramsString = "FilterCondition=" + filterContentType + "&SortCondition=" + sortBy + "&RecordCount=200&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=" + consolidationType + "&ComponentID=" + sideMenusModel.getComponentId() + "&CartID=&Locale=en-us&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=" + queryString + "&DateOfMyLastAccess=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize + "&ddlSortType=" + ddlSortType + "&ddlSortList=" + ddlSortList + "&iswishlistcontent=" + isWsh;

        vollyService.getJsonObjResponseVolley("CATALOGDATA", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileCatalogObjectsNew?" + paramsString, appUserModel.getAuthHeaders());

    }

    public void refreshPeopleListingCatalog(String authorID) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));

        String paramsString = "FilterCondition=" + filterContentType + "&SortCondition=" + sortBy + "&RecordCount=150&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=All" + "&ComponentID=" + sideMenusModel.getComponentId() + "&CartID=&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=&DateOfMyLastAccess=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&AuthorID=" + authorID;

        vollyService.getJsonObjResponseVolley("PEOPLELISTINGCATALOG", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileCatalogObjectsNew?" + paramsString, appUserModel.getAuthHeaders());

    }

    public void refreshMyCompetencyCatalog(boolean isLoadMore) {

        if (!isLoadMore) {
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }
        String paramsString = "";

        if (isFromMyCompetency) {
            paramsString = "FilterCondition=" + filterContentType + "&SortCondition=" + sortBy + "&RecordCount=150&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=All" + "&ComponentID=1" + "&CartID=&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=&DateOfMyLastAccess=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&AuthorID=0&fType=Skills&fValue=" + skillID;

        } else {

            //  FilterCondition=8,9,10,11,14,20,21,26,27,28,36,50,52,70,646,102,689,693&SortCondition=c.PublishedDate%20desc,c.name&RecordCount=200&OrgUnitID=374&userid=13608&Type=All&ComponentID=1&CartID=&Locale="+ preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))+"&SiteID=374&CategoryCompID=19&SearchText=Learning&DateOfMyLastAccess=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1&ComponentInsID=3131&AuthorID=0

            paramsString = "FilterCondition=" + filterContentType + "&SortCondition=" + sortBy + "&RecordCount=150&OrgUnitID=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Type=All" + "&ComponentID=1" + "&CartID=&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "&SiteID=" + appUserModel.getSiteIDValue() + "&CategoryCompID=19&SearchText=" + queryString + "&DateOfMyLastAccess=&GoogleValues=&IsAdvanceSearch=false&ContentID=&Createduserid=-1&SearchPartial=1&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&AuthorID=0" + "&pageIndex=" + pageIndex + "&pageSize=" + pageSize;

        }

        vollyService.getJsonObjResponseVolley("MYCMPTCY", appUserModel.getWebAPIUrl() + "MobileLMS/MobileCatalogObjectsNew?" + paramsString, appUserModel.getAuthHeaders());

    }

    public void getMobileCatalogObjectsData(Boolean isRefreshed) {

        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/MobileCatalogObjectsData";

        JSONObject parameters = new JSONObject();

        try {

            parameters.put("pageIndex", pageIndex);
            parameters.put("pageSize", pageSize);
            parameters.put("SearchText", queryString);
            parameters.put("ContentID", "");
            parameters.put("sortBy", applyFilterModel.sortBy.length() > 0 ? applyFilterModel.sortBy : ddlSortList);
            parameters.put("ComponentID", sideMenusModel.getComponentId());
            parameters.put("ComponentInsID", sideMenusModel.getRepositoryId());
            parameters.put("AdditionalParams", "");
            parameters.put("SelectedTab", "");
            parameters.put("AddtionalFilter", "");
            parameters.put("LocationFilter", "");
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("OrgUnitID", appUserModel.getSiteIDValue());
            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("groupBy", applyFilterModel.groupBy);
            parameters.put("categories", applyFilterModel.categories);
            parameters.put("objecttypes", applyFilterModel.objectTypes);
            parameters.put("skillcats", applyFilterModel.skillCats);
            parameters.put("skills", applyFilterModel.skills);
            parameters.put("jobroles", applyFilterModel.jobRoles);
            parameters.put("solutions", applyFilterModel.solutions);
            parameters.put("keywords", "");
            parameters.put("ratings", applyFilterModel.ratings);
            parameters.put("pricerange", applyFilterModel.priceRange);
            parameters.put("eventdate", "");
            parameters.put("certification", "");
            parameters.put("duration", applyFilterModel.duration);
            parameters.put("instructors", applyFilterModel.instructors);
            parameters.put("iswishlistcontent", isWsh);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "CATALOGDATA", urlStr);
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
                            db.injectCatalogData(response, false, pageIndex);
                            totalRecordsCount = countOfTotalRecords(response);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }


                if (requestType.equalsIgnoreCase("PEOPLELISTINGCATALOG")) {
                    if (response != null) {
                        try {

                            catalogModelsList = generateCatalogForPeopleListing(response);
                            catalogAdapter.refreshList(catalogModelsList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                if (requestType.equalsIgnoreCase("MYCMPTCY")) {
                    if (response != null) {
                        try {

                            if (isFromGlobalSearch) {
                                catalogModelsList.addAll(generateCatalogForPeopleListing(response));
                                totalRecordsCount = countOfTotalRecords(response);
                                if (totalRecordsCount == 0) {
                                    nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                                }
                            } else {
                                catalogModelsList = generateCatalogForPeopleListing(response);
                            }

                            if (catalogModelsList.size() > 0) {
                                catalogAdapter.refreshList(catalogModelsList);
                            } else {
                                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
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
                            db.insertFilterIntoDB(response, appUserModel, 0);
                            Intent intent = new Intent(context, AdvancedFilterActivity.class);
                            intent.putExtra("isFrom", 0);
                            startActivityForResult(intent, FILTER_CLOSE_CODE);
                            REFRESH = 0;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.catalog_label_filtersnotconfigured), Toast.LENGTH_SHORT).show();

                    }
                }
                if (requestType.equalsIgnoreCase("WISHLIST")) {
                    if (response != null) {
                        if (isDigimedica) {
                            pageIndex = 1;
                            getMobileCatalogObjectsData(true);
                        } else {
                            refreshCatalog(true);
                        }
                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_itemremovedtowishlistsuccesfully), Toast.LENGTH_SHORT).show();
                    } else {

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

                    Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.catalog_label_filtersnotconfigured), Toast.LENGTH_SHORT).show();
                }

                if (requestType.equalsIgnoreCase("CATALOGDATA")) {

                    nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                }
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);

                if (requestType.equalsIgnoreCase("CATALOGDATA")) {
                    if (response != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            db.injectCatalogData(jsonObj, false, pageIndex);
                            totalRecordsCount = countOfTotalRecords(jsonObj);
                            injectFromDbtoModel();

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

    public int countOfTotalRecords(JSONObject jsonObject) throws JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("table");
        int totalRecs = 0;

        if (jsonTableAry.length() > 0) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(0);

            totalRecs = jsonMyLearningColumnObj.getInt("totalrecordscount");
        }

        return totalRecs;
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
        membershipModel = db.fetchMembership(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        catalogAdapter = new CatalogAdapter(getActivity(), BIND_ABOVE_CLIENT, catalogModelsList, false, membershipModel, isIconEnabled);
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

                if (totalItemCount < totalRecordsCount && totalItemCount != 0) {

                    Log.d(TAG, "onLoadMore size: catalogModelsList" + catalogModelsList.size());

                    Log.d(TAG, "onLoadMore size: totalRecordsCount" + totalRecordsCount);


                    if (userScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                        userScrolled = false;

                        if (!isSearching) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (isNetworkConnectionAvailable(getContext(), -1)) {
                                if (isFromGlobalSearch) {
                                    refreshMyCompetencyCatalog(true);
                                } else {
                                    if (isDigimedica) {
                                        getMobileCatalogObjectsData(true);
                                    } else {
                                        refreshCatalog(true);
                                    }
                                }

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
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
//                Log.d(TAG, "onLoadMore: called page " + page);
                Log.d(TAG, "onLoadMore: called totalItemsCount" + totalItemsCount);
            }
        });

        if (BACKTOMAINSITE == 2) {
            CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;
            BACKTOMAINSITE = 0;
        }

        if (isFromMyCompetency || isFromGlobalSearch) {
            swipeRefreshLayout.setEnabled(false);
            refreshMyCompetencyCatalog(false);
            CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;

        } else if (!isFromPeopleListing) {
            if (!isFromCatogories) {
                catalogModelsList = new ArrayList<MyLearningModel>();
//                if (isNetworkConnectionAvailable(getContext(), -1) && CATALOG_FRAGMENT_OPENED_FIRSTTIME == 0) {
                if (isNetworkConnectionAvailable(getContext(), -1)) {
                    if (isDigimedica) {
                        getMobileCatalogObjectsData(false);
                    } else {
                        refreshCatalog(false);
                    }
                } else {
                    injectFromDbtoModel();
                }
            } else {
                swipeRefreshLayout.setEnabled(false);

                catalogAdapter.refreshList(catalogModelsList);
            }

        } else {
            swipeRefreshLayout.setEnabled(false);
            refreshPeopleListingCatalog(peopleListingModel.userID);
            CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;
        }


        initilizeView();

        if (allowAddContentType.length() > 0) {
            fabActionMenusInitilization();
        }
        if (isFromGlobalSearch) {
            uploadFloatMenu.setVisibility(View.GONE);
        }
        onBackPressed(rootView);

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


        fabAudio.setLabelText(getLocalizationValue(JsonLocalekeys.catalog_fabitem_uploadfileaudio));
        fabImage.setLabelText(getLocalizationValue(JsonLocalekeys.catalog_fabitem_uploadfileimage));
        fabVideo.setLabelText(getLocalizationValue(JsonLocalekeys.catalog_fabitem_uploadfilevideo));
        fabDocument.setLabelText(getLocalizationValue(JsonLocalekeys.catalog_fabitem_uploadfiledocument));
        fabWebsiteURL.setLabelText(getLocalizationValue(JsonLocalekeys.catalog_fabitem_uploadfilewebsiteurl));

    }

    public void injectFromDbtoModel() {
        catalogModelsList = db.fetchCatalogModel(sideMenusModel.getComponentId(), ddlSortList, ddlSortType, applyFilterModel.filterApplied);
        if (catalogModelsList != null) {
            catalogAdapter.refreshList(catalogModelsList);
        } else {
            catalogModelsList = new ArrayList<MyLearningModel>();
            catalogAdapter.refreshList(catalogModelsList);
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
        }

        if (catalogModelsList.size() == pageSize) {
            pageIndex = 2;
        } else {
            pageIndex = catalogModelsList.size() / pageSize;
            pageIndex = pageIndex + 1;
        }

//        if (catalogModelsList.size() > 5) {
//            if (item_search != null) {
//                item_search.setVisible(true);
//            }
//        } else {
//            if (item_search != null) {
//                item_search.setVisible(false);
//            }

//        }

        triggerActionForFirstItem();


//        if (uiSettingsModel.isGlobasearch() && queryString.length() > 0) {
//
//            catalogAdapter.filter(queryString);
//
//        }

    }

    public void triggerActionForFirstItem() {

        if (isFromNotification) {
            int selectedPostion = getPositionForNotification(contentIDFromNotification);
            myLearninglistView.setSelection(selectedPostion);
            Intent intentDetail = new Intent(getContext(), MyLearningDetailActivity1.class);
            intentDetail.putExtra("IFROMCATALOG", true);
            intentDetail.putExtra("sideMenusModel", sideMenusModel);
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
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        String titleName = sideMenusModel.getDisplayName();
        if (isFromMyCompetency) {
            titleName = skillName;
        }
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + titleName + "</font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void updateActionbarTitle(boolean forMylearning) {
        String titleName = "";
        if (forMylearning) {
            titleName = sideMenusModel.getDisplayName();
        } else {
            titleName = getLocalizationValue(JsonLocalekeys.catalog_header_wishlisttitlelabel);
        }
        if (isFromMyCompetency) {
            titleName = skillName;
        }
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + titleName + "</font>"));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mylearning_menu, menu);
        item_search = menu.findItem(R.id.mylearning_search);
        MenuItem item_filter = menu.findItem(R.id.mylearning_filter);
        MenuItem itemInfo = menu.findItem(R.id.mylearning_info_help);

        itemWishList = menu.findItem(R.id.ctx_archive);
        itemWishList.setVisible(true);

        itemInfo.setVisible(false);

        if (responMap != null && responMap.containsKey("ShowIndexes")) {
            String showIndexes = responMap.get("ShowIndexes");
            if (showIndexes.equalsIgnoreCase("top")) {
                item_filter.setVisible(true);
            } else {
                item_filter.setVisible(false);
            }
        } else {
            // No such key
            item_filter.setVisible(false);
        }

        if (isFromMyCompetency || isFromGlobalSearch) {
            item_search.setVisible(false);
            item_filter.setVisible(false);
            itemWishList.setVisible(false);
        }

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
//          tintMenuIcon(getActivity(), item_search, R.color.colorWhite);.
            item_search.setTitle(getLocalizationValue(JsonLocalekeys.search_label));
            final SearchView searchView = (SearchView) item_search.getActionView();
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint(getLocalizationValue(JsonLocalekeys.commoncomponent_label_searchlabel));
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            txtSearch.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            searchView.setBackgroundColor(Color.RED);

//            searchView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));

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

        if (item_filter != null) {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_filter, context, uiSettingsModel.getAppHeaderTextColor());
            item_filter.setIcon(filterDrawable);
            item_filter.setTitle(getLocalizationValue(JsonLocalekeys.catalog_header_filtertitlelabel));

        }

        if (itemInfo != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.help);
            itemInfo.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
        }

        if (itemWishList != null) {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_heart, context, uiSettingsModel.getAppHeaderTextColor());
            itemWishList.setIcon(filterDrawable);
            itemWishList.setTitle(getLocalizationValue(JsonLocalekeys.catalog_header_wishlisttitlelabel));
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
                break;
            case R.id.mylearning_info_help:
                Log.d(TAG, "onOptionsItemSelected :mylearning_info_help ");
                appcontroller.setAlreadyViewd(false);
                preferencesManager.setStringValue("false", StaticValues.KEY_HIDE_ANNOTATION);
                catalogAdapter.notifyDataSetChanged();
                break;
            case R.id.mylearning_filter:
//              filterApiCall();
                advancedFilters();
                break;
            case R.id.ctx_archive:
                if (isNetworkConnectionAvailable(getContext(), -1)) {
                    isWishListCall();
                } else {
                    showToast(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet));
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public void isWishListCall() {

        if (isWishlisted) {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_heart, context, uiSettingsModel.getAppHeaderTextColor());
            itemWishList.setIcon(filterDrawable);
            itemWishList.setTitle(getLocalizationValue(JsonLocalekeys.catalog_header_wishlisttitlelabel));
            isWishlisted = false;
            isWsh = 0;
            pageIndex = 1;
            updateActionbarTitle(true);
        } else {
            Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_book, context, uiSettingsModel.getAppHeaderTextColor());
            itemWishList.setIcon(filterDrawable);
            itemWishList.setTitle(sideMenusModel.getDisplayName());
            isWishlisted = true;
            isWsh = 1;
            pageIndex = 1;
            updateActionbarTitle(false);
        }

        if (isDigimedica) {
            getMobileCatalogObjectsData(true);
        } else {
            refreshCatalog(true);
        }
    }


    @Override
    public void onRefresh() {
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            pageIndex = 1;
            queryString = "";
            if (isDigimedica) {
                getMobileCatalogObjectsData(true);
            } else {
                refreshCatalog(true);
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
                    downloadTheCourse(catalogModelsList.get(position), view, position);
                } else {
                    showToast(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet));
                }
                break;
            case R.id.btn_contextmenu:
                View v = myLearninglistView.getChildAt(position - myLearninglistView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, catalogModelsList.get(position), uiSettingsModel, appUserModel);
                break;
            case R.id.imagethumb:
            case R.id.card_view:
                if (!catalogModelsList.get(position).getObjecttypeId().equalsIgnoreCase("70")) {
                    openDetailsPage(catalogModelsList.get(position));
                }
                break;
            case R.id.rat_adapt_ratingbar:
                break;
            case R.id.fabbtnthumb:
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


    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final MyLearningModel myLearningDetalData, final UiSettingsModel uiSettingsModel, final AppUserModel userModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.catalog_contextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(false);//view  ctx_view
        menu.getItem(1).setVisible(false);//add   ctx_add
        menu.getItem(2).setVisible(false);//buy    ctx_buy
        menu.getItem(3).setVisible(false);//detail ctx_detail
        menu.getItem(4).setVisible(false);//delete  ctx_delete
        menu.getItem(5).setVisible(false);//download ctx_download
        menu.getItem(6).setVisible(false);//addwishlist  ctx_addtowishlist
        menu.getItem(7).setVisible(false);//rmwishlist  ctx_removefromwishlist

        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_viewoption));//view  ctx_view
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_addtomylearningoption));//add   ctx_add
        menu.getItem(2).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_buyoption));//buy    ctx_buy
        menu.getItem(3).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_detailsoption));//detail ctx_detail
        menu.getItem(4).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_deleteoption));//delete  ctx_delete
        menu.getItem(5).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_downloadoption));//download ctx_download
        menu.getItem(6).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_wishlistoption));//addwishlist  ctx_addtowishlist
        menu.getItem(7).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_removefromwishlistoption));//rmwishlist  ctx_removefromwishlist

//        boolean subscribedContent = databaseH.isSubscribedContent(myLearningDetalData);

        if (myLearningDetalData.getAddedToMylearning() == 1) {

            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);

            if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70")) {
                Integer relatedCount = Integer.parseInt(myLearningDetalData.getRelatedContentCount());
                if (relatedCount > 0 && myLearningDetalData.getIsListView().equalsIgnoreCase("true")) {
                    menu.getItem(0).setVisible(true);
                } else {
                    menu.getItem(0).setVisible(false);
                }
            }
            if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {

                File myFile = new File(myLearningDetalData.getOfflinepath());

                if (myFile.exists()) {

                    menu.getItem(4).setVisible(false);
                    menu.getItem(5).setVisible(false);//download

                } else {

                    menu.getItem(4).setVisible(false);

                }
            }

        } else {
            if (myLearningDetalData.getViewType().equalsIgnoreCase("1") || myLearningDetalData.getViewType().equalsIgnoreCase("2")) {

//                if (myLearningDetalData.getAddedToMylearning() == 0) {
//                    menu.getItem(0).setVisible(true);
//                } else {
//                    menu.getItem(0).setVisible(false);
//                }
                menu.getItem(1).setVisible(true);
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(true);

                if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {

                    File myFile = new File(myLearningDetalData.getOfflinepath());

                    if (myFile.exists()) {

                        menu.getItem(4).setVisible(true);
                        menu.getItem(5).setVisible(false);

                    } else {

                        menu.getItem(4).setVisible(false);
                    }

                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
                        menu.getItem(4).setVisible(false);
                    }
                    if (isValidString(myLearningDetalData.getEventstartUtcTime()) && !returnEventCompleted(myLearningDetalData.getEventstartUtcTime())) {
                        if (isValidString(myLearningDetalData.getActionWaitlist()) && myLearningDetalData.getActionWaitlist().equalsIgnoreCase("true")) {
                            menu.getItem(1).setVisible(true);
                            menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.events_actionsheet_waitlistoption));
                        } else {
                            menu.getItem(1).setVisible(true);
                        }
                    } else if (myLearningDetalData.getEventScheduleType() == 1) {
                        if (uiSettingsModel.isEnableMultipleInstancesforEvent()) {

                        }
                    }

                }
            }
//            else if (myLearningDetalData.getViewType().equalsIgnoreCase("2")) { commented as per events comparision
//                menu.getItem(0).setVisible(false);
//                menu.getItem(1).setVisible(true);
//                menu.getItem(3).setVisible(true);
//                if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {
//
//                    File myFile = new File(myLearningDetalData.getOfflinepath());
//
//                    if (myFile.exists()) {
//
//                        menu.getItem(4).setVisible(true);
//                        menu.getItem(5).setVisible(false);
//                    } else {
//
//                        menu.getItem(4).setVisible(false);
//                    }
//
//                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
//                        menu.getItem(4).setVisible(false);
//                    }
////                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {
////
////                        menu.getItem(4).setVisible(true);
////                    }
//                }
//            }
            else if (myLearningDetalData.getViewType().equalsIgnoreCase("3")) {
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
        if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
            menu.getItem(5).setVisible(false);
        }
        if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {
            File myFile = new File(myLearningDetalData.getOfflinepath());
            if (myFile.exists()) {
                menu.getItem(5).setVisible(false);
            } else {
                if (myLearningDetalData.getAddedToMylearning() != 2) {
                    menu.getItem(5).setVisible(true);
                }

            }
        }
        if (myLearningDetalData.getAddedToMylearning() == 0 || myLearningDetalData.getAddedToMylearning() == 2) {
            if (myLearningDetalData.isArchived()) {
                menu.getItem(7).setVisible(true);//removeWishListed
            } else {
                menu.getItem(6).setVisible(true);//isWishListed
            }
        }
        if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("10") || myLearningDetalData.getIsListView().equalsIgnoreCase("true") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("28") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("688") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("102") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
            menu.getItem(5).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ctx_view:
                        if (myLearningDetalData.getAddedToMylearning() == 0 && myLearningDetalData.getViewType().equalsIgnoreCase("1")) {
                            GlobalMethods.launchCoursePreviewViewFromGlobalClass(myLearningDetalData, v.getContext(), isIconEnabled);
                        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70") && myLearningDetalData.getIsListView().equalsIgnoreCase("true")) {
                            GlobalMethods.relatedContentView(myLearningDetalData, v.getContext(), isIconEnabled);
                        } else {

                            if (isValidString(myLearningDetalData.getViewprerequisitecontentstatus())) {
                                String alertMessage = getLocalizationValue(JsonLocalekeys.prerequistesalerttitle6_alerttitle6);
                                alertMessage = alertMessage + " " + myLearningDetalData.getViewprerequisitecontentstatus();
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(alertMessage).setTitle(getLocalizationValue(JsonLocalekeys.details_alerttitle_stringalert))
                                        .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.events_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext(), isIconEnabled);

                            }
                        }
                        break;
                    case R.id.ctx_add:
                        if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70") && !returnEventCompleted(myLearningDetalData.getEventstartTime()) && uiSettingsModel.isAllowExpiredEventsSubscription()) {
                            try {
                                addExpiryEvets(myLearningDetalData, position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70") && uiSettingsModel.isEnableMultipleInstancesforEvent() && myLearningDetalData.getEventScheduleType() == 1) {

                            Log.d(TAG, "sheduled here: sheduled called" + myLearningDetalData.getCourseName());

                            Intent intentDetail = new Intent(context, MyLearningDetailActivity1.class);
                            intentDetail.putExtra("IFROMCATALOG", false);
                            intentDetail.putExtra("ISICONENABLED", isIconEnabled);
                            intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
                            intentDetail.putExtra("typeFrom", "tab");
                            intentDetail.putExtra("sideMenusModel", sideMenusModel);
                            startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);

                        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70") && isValidString(myLearningDetalData.getActionWaitlist()) && myLearningDetalData.getActionWaitlist().equalsIgnoreCase("true")) {
                            int avaliableSeats = 0;
                            try {
                                avaliableSeats = Integer.parseInt(myLearningDetalData.getAviliableSeats());
                            } catch (NumberFormatException nf) {
                                avaliableSeats = 0;
                                nf.printStackTrace();
                            }
                            if (avaliableSeats == 0) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(getLocalizationValue(JsonLocalekeys.eventdetailsenrollement_alertsubtitle_eventenrollmentlimit))
                                        .setTitle(getLocalizationValue(JsonLocalekeys.eventenrolltitle_alerttitle_enrollalerttitle))
                                        .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.mylearning_closebuttonaction_closebuttonalerttitle), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton(getLocalizationValue(JsonLocalekeys.myskill_alerttitle_stringconfirm), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            addToWaitList(myLearningDetalData);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        //do things
                                        dialog.dismiss();


                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }

                        } else {
                            if (uiSettingsModel.getNoOfDaysForCourseTargetDate() > 0) {

                                if (myLearningDetalData.getAddedToMylearning() == 2) {
                                    Intent intent = new Intent(context, PrerequisiteContentActivity.class);
                                    intent.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                                    intent.putExtra("myLearningDetalData", (Serializable) myLearningDetalData);
                                    startActivityForResult(intent, PREREQ_CLOSE);
                                } else {
                                    selectTheDueDate(myLearningDetalData, position, uiSettingsModel.getNoOfDaysForCourseTargetDate());

                                }
                            } else {
                                addToMyLearningCheckUser(myLearningDetalData, position, false);
                            }
                        }
                        break;
                    case R.id.ctx_buy:
                        if (uiSettingsModel.isEnableIndidvidualPurchaseConfig() && uiSettingsModel.isEnableMemberShipConfig() && myLearningDetalData.getGoogleProductID() == null) {

                            gotoMemberShipView(myLearningDetalData);

                        } else {
                            addToMyLearningCheckUser(myLearningDetalData, position, true);
                        }
                        break;
                    case R.id.ctx_detail:
                        Intent intentDetail = new Intent(getContext(), MyLearningDetailActivity1.class);

                        if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70") && !returnEventCompleted(myLearningDetalData.getEventstartTime()) && uiSettingsModel.isAllowExpiredEventsSubscription()) {
                            intentDetail.putExtra("IFROMCATALOG", false);
                            intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
                            intentDetail.putExtra("sideMenusModel", sideMenusModel);
                            ((Activity) getContext()).startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);
                        } else {
                            intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
                            intentDetail.putExtra("sideMenusModel", sideMenusModel);
                            intentDetail.putExtra("IFROMCATALOG", true);
                            ((Activity) getContext()).startActivityForResult(intentDetail, DETAIL_CATALOG_CODE);
                        }
                        break;
                    case R.id.ctx_delete:

                        break;
                    case R.id.ctx_download:
                        if (isNetworkConnectionAvailable(context, -1)) {
                            downloadTheCourse(catalogModelsList.get(position), v, position);
                        } else {
                            showToast(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet));
                        }
                        break;
                    case R.id.ctx_addtowishlist:
                        if (isNetworkConnectionAvailable(context, -1)) {
                            try {
                                addToWishListApiCall(catalogModelsList.get(position));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet));
                        }
                        break;
                    case R.id.ctx_removefromwishlist:
                        if (isNetworkConnectionAvailable(context, -1)) {
                            removeFromWishList(catalogModelsList.get(position).getContentID());
                        } else {
                            showToast(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet));
                        }
                        break;

                }

                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void gotoMemberShipView(MyLearningModel learningModel) {

        Intent intentSignup = new Intent(context, SignUp_Activity.class);
        intentSignup.putExtra(StaticValues.KEY_SOCIALLOGIN, appUserModel.getSiteURL() + "Join/nativeapp/true/membership/true/userid/" + learningModel.getUserID() + "/siteid/" + learningModel.getSiteID());
        intentSignup.putExtra(StaticValues.KEY_ACTIONBARTITLE, "Membership");
        startActivity(intentSignup);
//        http://mayur.instancysoft.com/Sign%20Up/profiletype/selfregistration/nativeapp/true
//        http://mayur.instancysoft.com/Join/nativeapp/true/membership/true/userid/2/siteid/374
    }

    public void addToMyLearningCheckUser(MyLearningModel myLearningDetalData, int position, boolean isInapp) {

        if (isNetworkConnectionAvailable(context, -1)) {

            if (myLearningDetalData.getAddedToMylearning() == 2 && !myLearningDetalData.isFromPrereq) {

                Intent intent = new Intent(context, PrerequisiteContentActivity.class);
                intent.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intent.putExtra("myLearningDetalData", (Serializable) myLearningDetalData);
                startActivityForResult(intent, PREREQ_CLOSE);
            } else {

                if (myLearningDetalData.getUserID().equalsIgnoreCase("-1")) {

                    //  checkUserLogin(myLearningDetalData, position, isInapp); commented for  get Method

                    try {
                        checkUserLoginPost(myLearningDetalData, position, isInapp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    if (isInapp) {
                        inAppActivityCall(myLearningDetalData);

                    } else {
                        addToMyLearning(myLearningDetalData, position, false, false);

                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectTheDueDate(final MyLearningModel myLearningDetalData,
                                 final int position, int dueDateTarget) {

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.DATE, dueDateTarget);
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Log.d(TAG, "onDateSet: " + dateFormatter.format(newDate.getTime()));
                dueDate = "";
                dueDate = dateFormatter.format(newDate.getTime());
                Log.d(TAG, "onDateSet: dueDate " + dueDate);
                addToMyLearningCheckUser(myLearningDetalData, position, false);

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Save", datePickerDialog);
        datePickerDialog.show();

    }

    public void addToMyLearning(final MyLearningModel myLearningDetalData, final int position,
                                final boolean isAutoAdd, final boolean isJoinedCommunity) {

        if (isNetworkConnectionAvailable(context, -1)) {

            if (myLearningDetalData.getAddedToMylearning() == 2) {

                Intent intent = new Intent(context, PrerequisiteContentActivity.class);
                intent.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intent.putExtra("myLearningDetalData", (Serializable) myLearningDetalData);
                startActivityForResult(intent, PREREQ_CLOSE);

            } else {
                boolean isSubscribed = db.isSubscribedContent(myLearningDetalData);
                if (isSubscribed) {
                    Toast toast = Toast.makeText(
                            context,
                            getLocalizationValue(JsonLocalekeys.catalog_label_alreadyinmylearning),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    String requestURL = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileAddtoMyCatalog?"
                            + "UserID=" + myLearningDetalData.getUserID() + "&SiteURL=" + myLearningDetalData.getSiteURL()
                            + "&ContentID=" + myLearningDetalData.getContentID() + "&SiteID=" + myLearningDetalData.getSiteID() + "&targetDate=" + dueDate;
                    requestURL = requestURL.replaceAll(" ", "%20");
                    Log.d(TAG, "inside catalog login : " + requestURL);
                    dueDate = "";
                    StringRequest strReq = new StringRequest(Request.Method.GET,
                            requestURL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "add to mylearning data " + response.toString());
                            if (response.equalsIgnoreCase("true")) {
                                catalogModelsList.get(position).setAddedToMylearning(1);
//                            catalogModelsList.get(position).setArchived(false);
                                db.updateContenToCatalog(catalogModelsList.get(position));
                                catalogAdapter.notifyDataSetChanged();
                                getMobileGetMobileContentMetaData(myLearningDetalData, position);
                                if (!isAutoAdd) {
                                    String succesMessage = getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_thiscontentitemhasbeenaddedto) + " " + getLocalizationValue(JsonLocalekeys.mylearning_header_mylearningtitlelabel);
                                    if (isJoinedCommunity) {
                                        succesMessage = getLocalizationValue(JsonLocalekeys.catalog_label_alreadyinmylearning) + getLocalizationValue(JsonLocalekeys.events_alertsubtitle_youhavesuccessfullyjoinedcommunity) + myLearningDetalData.getSiteName();
                                    }
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage(succesMessage)
                                            .setCancelable(false)
                                            .setPositiveButton(getLocalizationValue(JsonLocalekeys.myconnections_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //do things
                                                    dialog.dismiss();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                                Log.d(TAG, "onResponse: isWishlisted " + isWishlisted);
                                Log.d(TAG, "onResponse: isWsh " + isWsh);

                                if (isWishlisted && isWsh == 1) {
                                    getMobileCatalogObjectsData(true);
                                }

                            } else {
                                Toast toast = Toast.makeText(
                                        context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_unabletoprocess),
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
    }

    public void gotoGlobalSearch() {

        Intent intent = new Intent(context, GlobalSearchActivity.class);
        intent.putExtra("sideMenusModel", sideMenusModel);
        startActivityForResult(intent, GLOBAL_SEARCH);

    }


    public void checkUserLogin(final MyLearningModel learningModel, final int position, final boolean isInapp) {


        final String userName = preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID);

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                + userName + "&Password=" + learningModel.getPassword() + "&MobileSiteURL="
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
                                            getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_authenticationfailedcontactsiteadmin),
                                            Toast.LENGTH_LONG)
                                            .show();

                                }
                                if (response.contains("Pending Registration")) {
                                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_pleasebepatientawaitingapproval),
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


    public void checkUserLoginPost(final MyLearningModel learningModel, final int position, final boolean isInapp) throws JSONException {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/PostLoginDetails";

        JSONObject parameters = new JSONObject();
        parameters.put("UserName", learningModel.getUserName());
        parameters.put("Password", learningModel.getPassword());
        parameters.put("MobileSiteURL", appUserModel.getSiteURL());
        parameters.put("DownloadContent", "");
        parameters.put("SiteID", appUserModel.getSiteIDValue());
        parameters.put("isFromSignUp", false);

        final String postData = parameters.toString();

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
                JSONObject jsonObj = null;
                try {
                    jsonObj = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                                    getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_authenticationfailedcontactsiteadmin),
                                    Toast.LENGTH_LONG)
                                    .show();

                        }
                        if (response.contains("Pending Registration")) {
                            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_pleasebepatientawaitingapproval),
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return postData.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public void getMobileGetMobileContentMetaData(final MyLearningModel learningModel,
                                                  final int position) {

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
                                    builder.setMessage(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_addtomylearningoption))
                                            .setCancelable(false)
                                            .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
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

            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_inappserviceunavailable), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_inappidnotinserver), Toast.LENGTH_SHORT).show();
        }

//        billingProcessor.purchase(MyLearningDetailActivity.this, "com.foundationcourseforpersonal.managedproduct");
//        String productid = null;
//        productid = learningModel.getGoogleProductID();

//        SkuDetails sku = billingProcessor.getPurchaseListingDetails(testId);

//        Toast.makeText(this, sku != null ? sku.toString() : "Failed to load SKU details", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails
            details) {
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
                                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_purchasefailed), Toast.LENGTH_SHORT).show();
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

            boolean refresh = data.getBooleanExtra("REFRESH", false);
            if (refresh) {
                if (!isFromPeopleListing) {
                    injectFromDbtoModel();
                    MenuItemCompat.collapseActionView(item_search);

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

                if (!isFromPeopleListing) {
                    injectFromDbtoModel();
                    MenuItemCompat.collapseActionView(item_search);
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
                catalogAdapter.applySortBy(filterAscend, configId);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(data.getStringExtra("jsonInnerValues"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonObject.length() > 0) {
                    catalogAdapter.applyGroupBy(jsonObject);
                }

                if (jsonObject.length() > 0) {
                    catalogAdapter.filterByObjTypeId(jsonObject);
                }

            }

            if (catalogModelsList.size() <= 0) {
                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
            }

        }

        if (requestCode == DETAIL_CLOSE_CODE && resultCode == 0) {
//            Toast.makeText(context, "Detail Status updated!", Toast.LENGTH_SHORT).show();
            if (data == null) {
                pageIndex = 1;
                if (isDigimedica) {
                    getMobileCatalogObjectsData(true);
                } else {
                    refreshCatalog(true);
                }
            }
        }

        if (requestCode == GLOBAL_SEARCH && resultCode == RESULT_OK) {
            if (data != null) {
                queryString = data.getStringExtra("queryString");
                if (queryString.length() > 0) {
                    pageIndex = 1;
                    if (isDigimedica) {
                        getMobileCatalogObjectsData(true);
                    } else {
                        refreshCatalog(true);
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
                        getMobileCatalogObjectsData(true);
                    } else {
                        refreshCatalog(true);
                    }

                }
            }
        }

        if (requestCode == PREREQ_CLOSE && resultCode == RESULT_OK && data != null) {

            boolean refresh = data.getBooleanExtra("REFRESH", false);

            if (refresh) {

                ((SideMenu) getActivity()).homeControllClicked(true, 1, "", false, "");

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
                        String status = "";


                        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.app_esperanza))) {

                            status = jsonObject.optString("Name").trim();
                        } else {

                            status = jsonObject.optString("status").trim();
                        }// esperanza call


                        if (status.contains("failed to get statusObject reference not set to an instance of an object.")) {
                            status = "In Progress";
                        }
                        String progress = "0";
                        if (jsonObject.has("progress")) {
                            progress = jsonObject.get("progress").toString();
                        }

                        String localeStatus = "";
                        if (jsonObject.has("ContentStatus")) {
                            localeStatus = jsonObject.get("ContentStatus").toString();
                        }

                        i = db.updateContentStatus(myLearningModel, status, progress, localeStatus);
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
                groupFilterModel.isGroup = true;
                groupFilterModel.categorySelectedData = applyFilterModel.groupBy;
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
//                sortFilterModel.categoryName = getLocalizationValue(JsonLocalekeys.filter_lbl_sortbytitlelabel);
//                sortFilterModel.categoryID = 3;
//                sortFilterModel.categorySelectedData = applyFilterModel.sortBy;
//                sortFilterModel.categorySelectedDataDisplay = applyFilterModel.sortByDisplay;
//                allFilterModelList.add(sortFilterModel);
//            }
//        }

        if (responMap != null && responMap.containsKey("ContentFilterBy")) {
            String enableSortby = responMap.get("ContentFilterBy");
            if (enableSortby != null && enableSortby.toLowerCase().contains("sortitemsby")) {
                AllFilterModel sortFilterModel = new AllFilterModel();
                sortFilterModel.categoryName = getLocalizationValue(JsonLocalekeys.filter_lbl_sortbytitlelabel);
                sortFilterModel.categoryID = 3;
                sortFilterModel.categorySelectedData = applyFilterModel.sortBy;
                sortFilterModel.categorySelectedDataDisplay = applyFilterModel.sortByDisplay;
                allFilterModelList.add(sortFilterModel);
            }
        }


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
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_caegoriestitlelabel);
                            if (isValidString(applyFilterModel.categories)) {
                                contentFilterByModel.selectedSkillIdsArry.add(applyFilterModel.categories);
                            }
                            contentFilterByModel.goInside = true;
                            break;
                        case "skills":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "skills";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_byskills);
                            contentFilterByModel.goInside = true;
                            break;
                        case "objecttypeid":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "bytype";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_contenttype);
                            contentFilterByModel.goInside = false;
                            break;
                        case "jobroles":
                        case "job":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "jobroles";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_jobroles_header);
                            contentFilterByModel.goInside = false;
                            break;
                        case "solutions":
                            contentFilterByModel.categoryName = filterCategoriesArray.get(i);
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "tag";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_tag);
                            contentFilterByModel.goInside = false;
                            break;
                        case "rating":
                            if (responMap != null && responMap.containsKey("ShowrRatings")) {
                                String showrRatings = responMap.get("ShowrRatings");
                                if (showrRatings.contains("true") && contentFilterByModelList.size() > 0) {
                                    contentFilterByModel.categoryName = "Show Ratings";
                                    contentFilterByModel.categoryIcon = "";
                                    contentFilterByModel.categoryID = "rate";
                                    contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_rating);
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
                                    contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_duration);
                                    contentFilterByModel.goInside = false;
                                }
                            }
                            break;
                        case "ecommerceprice":
                            if (uiSettingsModel.isEnableEcommerce() && responMap != null && responMap.containsKey("EnableEcommerce")) {
                                String showrRatings = responMap.get("EnableEcommerce");
                                if (showrRatings.contains("true") && contentFilterByModelList.size() > 0) {
                                    contentFilterByModel.categoryName = "EnableEcommerce";
                                    contentFilterByModel.categoryIcon = "";
                                    contentFilterByModel.categoryID = "priceRange";
                                    contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_pricerange);
                                    contentFilterByModel.goInside = false;
                                }
                            }
                            break;
                        case "instructor":
                            contentFilterByModel.categoryName = "Instructor";
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "inst";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_instructor);
                            contentFilterByModel.goInside = false;
                            break;
                        case "certificate":
                            break;
                        case "eventdates":
                            contentFilterByModel.categoryName = "Event dates";
                            contentFilterByModel.categoryIcon = "";
                            contentFilterByModel.categoryID = "eventdates";
                            contentFilterByModel.categoryDisplayName = getLocalizationValue(JsonLocalekeys.filter_lbl_eventdatedate);
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


    public List<String> getArrayListFromString(String questionCategoriesString) {

        List<String> questionCategoriesArray = new ArrayList<>();

        if (questionCategoriesString.length() <= 0)
            return questionCategoriesArray;

        questionCategoriesArray = Arrays.asList(questionCategoriesString.split(","));

        return questionCategoriesArray;

    }

    public void advancedFilters() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            if (contentFilterByModelList.size() == 0) {
//                applyFilterModel.categories = "";
                contentFilterByModelList = generateContentFilters();
            }

            List<AllFilterModel> allFilterModelList = getAllFilterModelList();

            if (contentFilterByModelList != null && contentFilterByModelList.size() > 0) {
                Intent intent = new Intent(context, AllFiltersActivity.class);
                intent.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intent.putExtra("isFrom", 0);
                intent.putExtra("contentFilterByModelList", (Serializable) contentFilterByModelList);
                intent.putExtra("allFilterModelList", (Serializable) allFilterModelList);
                intent.putExtra("contentFilterType", contentFilterType);
                intent.putExtra("responMap", (Serializable) responMap);
                startActivityForResult(intent, FILTER_CLOSE_CODE_ADV);
            }
        } else {
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();

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

                    Intent intent = new Intent(context, Catalog_fragment.class);
                    intent.putExtra("breadicrumblist", (Serializable) breadcrumbItemsList);
                    intent.putExtra("iscatalogclicked", true);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
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

            arrowView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppButtonBgColor() + "'><medium><b>"
                    + getResources().getString(R.string.fa_icon_angle_right) + "</b></big> </font>"));

//            arrowView.setText(getResources().getString(R.string.fa_icon_forward));
//            arrowView.setTextColor(getResources().getColor(R.color.colorPrimary)));
            arrowView.setTextSize(20);


            arrowView.setGravity(Gravity.CENTER | Gravity.LEFT);
            // String text = coountries[i];
            cvBreadcrumbItem = dicBreadcrumbItems.get(i);
            String categoryId = cvBreadcrumbItem.getAsString("categoryid");
            String categoryName = cvBreadcrumbItem.getAsString("categoryname");

            textView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppButtonBgColor() + "'><big><b><u>"
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
        List<ContentValues> tempBreadCrumb = new ArrayList<>();

        if (categoryLevel == 0) {
            tempBreadCrumb.add(breadcrumbItemsList.get(categoryLevel));
        } else if (breadcrumbItemsList != null)
            for (int k = 0; breadcrumbItemsList.size() > k; k++) {
                if (k < categoryLevel + 1) {
                    tempBreadCrumb.add(breadcrumbItemsList.get(k));
                }
            }
        breadcrumbItemsList = new ArrayList<>();
        breadcrumbItemsList.addAll(tempBreadCrumb);
//        breadcrumbItemsList = breadcrumbItemsList.subList(0, categoryLevel + 1);
        nodata_Label.setVisibility(View.GONE);
    }


//    public void removeItemFromBreadcrumbList(int categoryLevel) {
//        List<ContentValues> tempBreadCrumb = new ArrayList<>();
//
//        if (categoryLevel == 0) {
//            tempBreadCrumb.add(breadcrumbItemsList.get(categoryLevel));
//        }
// else if (breadcrumbItemsList != null) {
//            for (int k = 0; breadcrumbItemsList.size() > k; k++) {
//                if (k != categoryLevel + 1) {
//                    tempBreadCrumb.add(breadcrumbItemsList.get(k));
//                }
//            }
//        }
//
//        breadcrumbItemsList = new ArrayList<>();
//        breadcrumbItemsList.addAll(tempBreadCrumb);
////        breadcrumbItemsList = breadcrumbItemsList.subList(0, categoryLevel + 1);
//    }

    public void downloadTheCourse(final MyLearningModel learningModel, final View view,
                                  final int position) {

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
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/downloadfiles/"
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

    public void downloadThin(String downloadStruri, View view,
                             final MyLearningModel learningModel, final int position) {

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
                            File zipfile = new File(zipFile);
                            zipfile.delete();
                        }
                        catalogAdapter.notifyDataSetChanged();

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
                + "&DelivoryMode=1&IsDownload=1&localeId=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name));

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
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
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

    //    // Main action: create notification

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
                if (isDigimedica) {
                    getMobileCatalogObjectsData(true);
                } else {
                    refreshCatalog(true);
                }
            } else {
                injectFromDbtoModel();
            }
        }
        REFRESH = 0;
    }

    public void openDetailsPage(MyLearningModel learningModel) {

        if (learningModel.getViewType().equalsIgnoreCase("1") || learningModel.getViewType().equalsIgnoreCase("3") || learningModel.getViewType().equalsIgnoreCase("2")) {
            Intent intentDetail = new Intent(getContext(), MyLearningDetailActivity1.class);
            intentDetail.putExtra("IFROMCATALOG", true);
            intentDetail.putExtra("myLearningDetalData", learningModel);
            intentDetail.putExtra("sideMenusModel", sideMenusModel);
            ((Activity) getContext()).startActivityForResult(intentDetail, DETAIL_CATALOG_CODE);

        }
    }


    public List<MyLearningModel> generateCatalogForPeopleListing(JSONObject jsonObject) throws
            JSONException {


        List<MyLearningModel> myLearningModelList = new ArrayList<>();

        JSONArray jsonTableAry = jsonObject.getJSONArray("table2");
        // for deleting records in table for respective table

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            MyLearningModel myLearningModel = new MyLearningModel();


            //sitename
            if (jsonMyLearningColumnObj.has("sitename")) {

                myLearningModel.setSiteName(jsonMyLearningColumnObj.get("sitename").toString());
            }
            // siteurl
            if (jsonMyLearningColumnObj.has("siteurl")) {

                myLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());

            }
            // siteid
            if (jsonMyLearningColumnObj.has("orgunitid")) {

                myLearningModel.setSiteID(jsonMyLearningColumnObj.get("orgunitid").toString());

            }
            // userid
            if (jsonMyLearningColumnObj.has("userid")) {

                myLearningModel.setUserID(jsonMyLearningColumnObj.get("userid").toString());

            }
            // coursename


            if (jsonMyLearningColumnObj.has("name")) {

                myLearningModel.setCourseName(jsonMyLearningColumnObj.get("name").toString());

            }


            String authorName = "";
            if (jsonMyLearningColumnObj.has("contentauthordisplayname")) {
                authorName = jsonMyLearningColumnObj.getString("contentauthordisplayname");

            }

            if (isValidString(authorName)) {
                myLearningModel.setAuthor(authorName);
            } else {
                // author
                if (jsonMyLearningColumnObj.has("author")) {

                    myLearningModel.setAuthor(jsonMyLearningColumnObj.get("author").toString());

                }
            }


            // contentID
            if (jsonMyLearningColumnObj.has("contentid")) {

                myLearningModel.setContentID(jsonMyLearningColumnObj.get("contentid").toString());

            }
            // createddate
            if (jsonMyLearningColumnObj.has("createddate")) {

                myLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

            }

            // durationEndDate
            if (jsonMyLearningColumnObj.has("durationenddate")) {

                myLearningModel.setDurationEndDate(jsonMyLearningColumnObj.get("durationenddate").toString());

            }
            // objectID
            if (jsonMyLearningColumnObj.has("objectid")) {

                myLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());

            }
            // thumbnailimagepath
            if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {

                String imageurl = jsonMyLearningColumnObj.getString("thumbnailimagepath");


                if (isValidString(imageurl)) {

                    myLearningModel.setThumbnailImagePath(imageurl);
                    String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + myLearningModel.getContentID() + "/" + imageurl;
                    myLearningModel.setImageData(imagePathSet);


                } else {
                    if (jsonMyLearningColumnObj.has("contenttypethumbnail")) {
                        String imageurlContentType = jsonMyLearningColumnObj.getString("contenttypethumbnail");
                        if (isValidString(imageurlContentType)) {
                            String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + imageurlContentType;
                            myLearningModel.setImageData(imagePathSet);

                        }
                    }


                }
//                // imagedata
//                if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {
//
//
//                } else {
//
//                }
                // relatedcontentcount
                if (jsonMyLearningColumnObj.has("relatedconentcount")) {

                    myLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedconentcount").toString());

                }
                // isDownloaded
                if (jsonMyLearningColumnObj.has("isdownloaded")) {

                    myLearningModel.setIsDownloaded(jsonMyLearningColumnObj.get("isdownloaded").toString());

                }
                // courseattempts
                if (jsonMyLearningColumnObj.has("courseattempts")) {

                    myLearningModel.setCourseAttempts(jsonMyLearningColumnObj.get("courseattempts").toString());

                }
                // objecttypeid
                if (jsonMyLearningColumnObj.has("objecttypeid")) {

                    myLearningModel.setObjecttypeId(jsonMyLearningColumnObj.get("objecttypeid").toString());

                }
                // scoid
                if (jsonMyLearningColumnObj.has("scoid")) {

                    myLearningModel.setScoId(jsonMyLearningColumnObj.get("scoid").toString());

                }
                // startpage
                if (jsonMyLearningColumnObj.has("startpage")) {

                    myLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

                }
                // status
                if (jsonMyLearningColumnObj.has("corelessonstatus")) {

                    myLearningModel.setStatusActual(jsonMyLearningColumnObj.get("corelessonstatus").toString());

                }

                // shortdes
                if (jsonMyLearningColumnObj.has("shortdescription")) {


                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("shortdescription").toString());

                    myLearningModel.setShortDes(result.toString());

                }

                // longdes
                if (jsonMyLearningColumnObj.has("longdescription")) {

                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("longdescription").toString());

//                    myLearningModel.setShortDes(result.toString());
                    myLearningModel.setLongDes(result.toString());

                }
                // typeofevent
                if (jsonMyLearningColumnObj.has("typeofevent")) {

                    int typeoFEvent = Integer.parseInt(jsonMyLearningColumnObj.get("typeofevent").toString());

                    myLearningModel.setTypeofevent(typeoFEvent);

                }

                // medianame
                if (jsonMyLearningColumnObj.has("medianame")) {
                    String medianame = "";

                    if (!myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                        if (jsonMyLearningColumnObj.getString("medianame").equalsIgnoreCase("test")) {
                            medianame = "Assessment(Test)";

                        } else {
                            medianame = jsonMyLearningColumnObj.get("medianame").toString();
                        }
                    } else {
                        if (myLearningModel.getTypeofevent() == 2) {
                            medianame = "Event (Online)";


                        } else if (myLearningModel.getTypeofevent() == 1) {
                            medianame = "Event (Face to Face)";

                        }
                    }

                    myLearningModel.setMediaName(medianame);

                }       // ratingid
                if (jsonMyLearningColumnObj.has("ratingid")) {

                    myLearningModel.setRatingId(jsonMyLearningColumnObj.get("ratingid").toString());

                }
                // publishedDate
                if (jsonMyLearningColumnObj.has("publisheddate")) {

                    myLearningModel.setPublishedDate(jsonMyLearningColumnObj.get("publisheddate").toString());

                }
                // eventstarttime
                if (jsonMyLearningColumnObj.has("eventstartdatetime")) {

                    myLearningModel.setEventstartTime(jsonMyLearningColumnObj.get("eventstartdatetime").toString());

                }
                // eventendtime
                if (jsonMyLearningColumnObj.has("eventenddatetime")) {

                    myLearningModel.setEventendTime(jsonMyLearningColumnObj.get("eventenddatetime").toString());

                }

                // mediatypeid
                if (jsonMyLearningColumnObj.has("mediatypeid")) {

                    myLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());

                }
                // dateassigned
                if (jsonMyLearningColumnObj.has("dateassigned")) {

                    myLearningModel.setDateAssigned(jsonMyLearningColumnObj.get("dateassigned").toString());

                }
                // keywords
                if (jsonMyLearningColumnObj.has("seokeywords")) {

                    myLearningModel.setKeywords(jsonMyLearningColumnObj.get("seokeywords").toString());

                }
                // eventcontentid
                if (jsonMyLearningColumnObj.has("eventcontentid")) {

                    myLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());

                }
                // eventAddedToCalender
                myLearningModel.setEventAddedToCalender(false);


                // isExpiry
                myLearningModel.setIsExpiry("false");

                // locationname
                if (jsonMyLearningColumnObj.has("locationname")) {

                    myLearningModel.setLocationName(jsonMyLearningColumnObj.get("locationname").toString());

                }
                // timezone
                if (jsonMyLearningColumnObj.has("timezone")) {

                    myLearningModel.setTimeZone(jsonMyLearningColumnObj.get("timezone").toString());

                }
                // participanturl
                if (jsonMyLearningColumnObj.has("participanturl")) {

                    myLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

                }
                // display
                myLearningModel.setDisplayName(appUserModel.getDisplayName());
                // userName
                myLearningModel.setUserName(appUserModel.getUserName());
                // password
                myLearningModel.setPassword(appUserModel.getPassword());

                // isListView
                if (jsonMyLearningColumnObj.has("bit5")) {

                    myLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

                }

                // joinurl
                if (jsonMyLearningColumnObj.has("joinurl")) {

                    myLearningModel.setJoinurl(jsonMyLearningColumnObj.get("joinurl").toString());

                }

                // offlinepath
                if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
                    String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
                    String startPage = jsonMyLearningColumnObj.get("startpage").toString();
                    String contentid = jsonMyLearningColumnObj.get("contentid").toString();
                    String downloadDestFolderPath = context.getExternalFilesDir(null)
                            + "/.Mydownloads/Contentdownloads" + "/" + contentid;

                    String finalDownloadedFilePath = downloadDestFolderPath + "/" + startPage;

                    myLearningModel.setOfflinepath(finalDownloadedFilePath);
                }
//

                // wresult
                if (jsonMyLearningColumnObj.has("wresult")) {

                    myLearningModel.setWresult(jsonMyLearningColumnObj.get("wresult").toString());

                }
                // wmessage
                if (jsonMyLearningColumnObj.has("wmessage")) {

                    myLearningModel.setWmessage(jsonMyLearningColumnObj.get("wmessage").toString());

                }

                // presenter
                if (jsonMyLearningColumnObj.has("presenter")) {

                    myLearningModel.setPresenter(jsonMyLearningColumnObj.get("presenter").toString());

                }

                //sitename
                if (jsonMyLearningColumnObj.has("saleprice")) {

                    myLearningModel.setPrice(jsonMyLearningColumnObj.get("saleprice").toString());

                }

                //googleproductid
                if (jsonMyLearningColumnObj.has("googleproductid")) {

                    myLearningModel.setGoogleProductID(jsonMyLearningColumnObj.get("googleproductid").toString());

                }

                //componentid
                if (jsonMyLearningColumnObj.has("componentid")) {

                    myLearningModel.setComponentId(jsonMyLearningColumnObj.get("componentid").toString());

                }

                //currency
                if (jsonMyLearningColumnObj.has("currency")) {

                    myLearningModel.setCurrency(jsonMyLearningColumnObj.get("currency").toString());

                }

                //viewtype
                if (jsonMyLearningColumnObj.has("viewtype")) {

                    myLearningModel.setViewType(jsonMyLearningColumnObj.get("viewtype").toString());

                }
                //isaddedtomylearning
                if (jsonMyLearningColumnObj.has("isaddedtomylearning")) {

                    myLearningModel.setAddedToMylearning(Integer.parseInt(jsonMyLearningColumnObj.get("isaddedtomylearning").toString()));

                }


                //membershipname
                if (jsonMyLearningColumnObj.has("membershipname")) {

                    myLearningModel.setMembershipname(jsonMyLearningColumnObj.get("membershipname").toString());

                }
                //membershiplevel
                if (jsonMyLearningColumnObj.has("membershiplevel")) {

                    String memberShip = jsonMyLearningColumnObj.getString("membershiplevel");
                    int memberInt = 1;
                    if (isValidString(memberShip)) {
                        memberInt = Integer.parseInt(memberShip);
                    } else {
                        memberInt = 1;
                    }
                    myLearningModel.setMemberShipLevel(memberInt);

                }

                //folderpath
                if (jsonMyLearningColumnObj.has("folderpath")) {

                    myLearningModel.setFolderPath(jsonMyLearningColumnObj.get("folderpath").toString());

                }

//                //folderpath
//                if (jsonMyLearningColumnObj.has("folderpath")) {
//
//                    myLearningModel.setContentTypeImagePath(jsonMyLearningColumnObj.get("folderpath").toString());
//
//                }
                myLearningModel.setContentTypeImagePath(jsonMyLearningColumnObj.optString("iconpath", ""));

                //jwvideokey
                if (jsonMyLearningColumnObj.has("jwvideokey")) {

                    String jwKey = jsonMyLearningColumnObj.getString("jwvideokey");

                    if (isValidString(jwKey)) {
                        myLearningModel.setJwvideokey(jwKey);
                    } else {
                        myLearningModel.setJwvideokey("");
                    }

                }

                //cloudmediaplayerkey
                if (jsonMyLearningColumnObj.has("cloudmediaplayerkey")) {

                    myLearningModel.setCloudmediaplayerkey(jsonMyLearningColumnObj.optString("cloudmediaplayerkey"));

                    String jwKey = jsonMyLearningColumnObj.getString("cloudmediaplayerkey");

                    if (isValidString(jwKey)) {
                        myLearningModel.setCloudmediaplayerkey(jwKey);
                    } else {
                        myLearningModel.setCloudmediaplayerkey("");
                    }
                }

                myLearningModelList.add(myLearningModel);
            }

        }

        if (myLearningModelList.size() == pageSize) {
            pageIndex = 2;
        } else {
            pageIndex = myLearningModelList.size() / pageSize;
            pageIndex = pageIndex + 1;
        }


        return myLearningModelList;
    }

    public void addExpiryEvets(MyLearningModel catalogModel, int position) throws JSONException {

        JSONObject parameters = new JSONObject();

        //mandatory
        parameters.put("SelectedContent", catalogModel.getContentID());
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("SiteID", catalogModel.getSiteID());
        parameters.put("OrgUnitID", catalogModel.getSiteID());
        parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));

        String parameterString = parameters.toString();
        boolean isSubscribed = db.isSubscribedContent(catalogModel);
        if (isSubscribed) {
            Toast toast = Toast.makeText(
                    context,
                    getLocalizationValue(getLocalizationValue(JsonLocalekeys.catalog_label_alreadyinmylearning)),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            sendExpiryEventData(parameterString, position, catalogModel);
        }
    }

    public void sendExpiryEventData(final String postData, final int position,
                                    final MyLearningModel catalogModel) {
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

                            getMobileGetMobileContentMetaData(catalogModel, position);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(getLocalizationValue(JsonLocalekeys.events_alertsubtitle_thiseventitemhasbeenaddedto) + "  "
                                    + getLocalizationValue(JsonLocalekeys.mylearning_header_mylearningtitlelabel))
                                    .setCancelable(false)
                                    .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            dialog.dismiss();
                                            // add event to android calander
                                            MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;
                                            db.updateEventAddedToMyLearningInEventCatalog(catalogModel, 1);
                                            injectFromDbtoModel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            Toast toast = Toast.makeText(
                                    context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_unabletoprocess),
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
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();

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
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

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

    public void addToWishListApiCall(MyLearningModel learningModel) throws
            JSONException {
        String addedDate = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
        JSONObject parameters = new JSONObject();
        parameters.put("ContentID", learningModel.getContentID());
        parameters.put("AddedDate", addedDate);
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("ComponentID", sideMenusModel.getComponentId());
        parameters.put("ComponentInstanceID", sideMenusModel.getRepositoryId());


        final String parameterString = parameters.toString();

        String urlString = appUserModel.getWebAPIUrl() + "/Catalog/AddToWishList";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
                if (s != null && s.length() > 0) {
                    if (isDigimedica) {
                        pageIndex = 1;
                        getMobileCatalogObjectsData(true);
                    } else {
                        refreshCatalog(true);
                    }

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_itemaddedtowishlistsuccesfully), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return parameterString.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public void removeFromWishList(String contentID) {

        //    http://angular6api.instancysoft.com/api/WishList/DeleteItemFromWishList?ContentID=d2e3b4de-94f5-42db-8806-85400cc7e3f8&instUserID=1

        String paramsString = "ContentID=" + contentID + "&instUserID=" + appUserModel.getUserIDValue();

        vollyService.getJsonObjResponseVolley("WISHLIST", appUserModel.getWebAPIUrl() + "/WishList/DeleteItemFromWishList?" + paramsString, appUserModel.getAuthHeaders());

    }

    public void addToWaitList(MyLearningModel catalogModel) throws JSONException {

        JSONObject parameters = new JSONObject();
        //mandatory
        parameters.put("WLContentID", catalogModel.getContentID());
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("siteid", catalogModel.getSiteID());
        parameters.put("locale", "" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));

        String parameterString = parameters.toString();
        boolean isSubscribed = db.isSubscribedContent(catalogModel);
        if (isSubscribed) {
            Toast toast = Toast.makeText(
                    context,
                    getLocalizationValue(JsonLocalekeys.catalog_label_alreadyinmylearning),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            sendWaitlistEventData(parameterString, catalogModel);
        }
    }

    public void sendWaitlistEventData(final String postData, final MyLearningModel catalogModel) {
        String apiURL = "";

        apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/EnrollWaitListEvent";

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.d("CMP", "onResponse: " + s);

                if (s != null && s.length() > 0) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        if (jsonObj.has("IsSuccess")) {

                            int waitlistEnrolls = catalogModel.getWaitlistenrolls() + 1;

                            catalogModel.setWaitlistenrolls(waitlistEnrolls);

                            Log.d(TAG, "onResponse: " + catalogModel.getWaitlistenrolls());
                            db.updateEventAddedToMyLearningInEventCatalog(catalogModel, 1);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(jsonObj.optString("Message"))
                                    .setCancelable(false)
                                    .setPositiveButton(getLocalizationValue(JsonLocalekeys.events_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            dialog.dismiss();

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            Toast toast = Toast.makeText(
                                    context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_unabletoprocess),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();

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
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

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

    public void onBackPressed(View rootView) {
        //handle back press event
        //Back pressed Logic for fragment
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Log.d(TAG, "onKey: clicked in back" + isFromCatogories);
                        if (isFromCatogories) return true;
                        else return false;
                    }
                }
                return false;
            }
        });

    }

}