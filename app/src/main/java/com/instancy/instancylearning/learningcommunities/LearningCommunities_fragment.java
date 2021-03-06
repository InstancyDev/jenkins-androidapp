package com.instancy.instancylearning.learningcommunities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.blankj.utilcode.util.LogUtils;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.GetSiteConfigsAsycTask;
import com.instancy.instancylearning.asynchtask.GetSubSiteConfigsAsycTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourms.CreateNewForumActivity;
import com.instancy.instancylearning.discussionfourms.DiscussionFourmAdapter;
import com.instancy.instancylearning.discussionfourms.DiscussionTopicActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.interfaces.SiteConfigInterface;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.Login_activity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CommunitiesModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.CATALOG_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class LearningCommunities_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    String TAG = LearningCommunities_fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    LearningCommunitiesAdapter learningCommunitiesAdapter;
    List<CommunitiesModel> communitiesModelList = null;
    PreferencesManager preferencesManager;
    GetSubSiteConfigsAsycTask getSubSiteConfigsAsycTask;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    ResultListner resultListner = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.mylearninglistview)
    ListView discussionFourmlistView;

    @BindView(R.id.nodata_label)
    TextView nodataLable;

    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    public LearningCommunities_fragment() {


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
        getSubSiteConfigsAsycTask = new GetSubSiteConfigsAsycTask(context);
        vollyService = new VollyService(resultCallback, context);

        sideMenusModel = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
        }
    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        if (sideMenusModel.getRepositoryId().length() < 1) {
            sideMenusModel.setRepositoryId("4026");
        }

        if (sideMenusModel.getComponentId().length() < 1) {
            sideMenusModel.setComponentId("189");
        }

        String apiURL = appUserModel.getWebAPIUrl() + "/Mobilelms/GetPortalListing?siteid=" + appUserModel.getSiteIDValue() + "&userid=" + appUserModel.getUserIDValue() + "&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "&compid=" + sideMenusModel.getComponentId() + "&CompInsID=" + sideMenusModel.getRepositoryId();

        vollyService.getJsonObjResponseVolley("COMMSLIST", apiURL, appUserModel.getAuthHeaders());

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("COMMSLIST")) {
                    if (response != null) {
                        try {
                            db.injectCommunitiesListing(response);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        nodataLable.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
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
                nodataLable.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mylearning, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);

        learningCommunitiesAdapter = new LearningCommunitiesAdapter(getActivity(), BIND_ABOVE_CLIENT, communitiesModelList);
        discussionFourmlistView.setAdapter(learningCommunitiesAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
        discussionFourmlistView.setEmptyView(rootView.findViewById(R.id.nodata_label));

        communitiesModelList = new ArrayList<CommunitiesModel>();
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshCatalog(false);
        } else {
            injectFromDbtoModel();
        }

        initilizeView();

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconforum, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(context, customNav));

        return rootView;
    }


    public void injectFromDbtoModel() {
        communitiesModelList = db.fetchCommunitiesList(appUserModel);
        if (communitiesModelList != null) {
            learningCommunitiesAdapter.refreshList(communitiesModelList);
        } else {
            communitiesModelList = new ArrayList<CommunitiesModel>();
            learningCommunitiesAdapter.refreshList(communitiesModelList);
        }

        if (communitiesModelList.size() > 5) {
            if (item_search != null) {
                item_search.setVisible(true);
            }

        } else {
            if (item_search != null) {
                item_search.setVisible(false);

            }
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
            item_search.setTitle(getLocalizationValue(JsonLocalekeys.search_label));
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint(getLocalizationValue(JsonLocalekeys.commoncomponent_label_searchlabel));
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    learningCommunitiesAdapter.filter(newText.toLowerCase(Locale.getDefault()));

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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshCatalog(true);
            MenuItemCompat.collapseActionView(item_search);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.card_view:
            case R.id.imagethumb:
            case R.id.txt_comminity_name:
                attachFragment(communitiesModelList.get(position));
                break;
            case R.id.btn_contextmenu:
                View v = discussionFourmlistView.getChildAt(position - discussionFourmlistView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, communitiesModelList.get(position));
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

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, CommunitiesModel communitiesModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.communitiesmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.learningcommunity_actionsheet_gotocommunityoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.learningcommunity_actionsheet_joincommunityoption));
        if (communitiesModel.actiongoto == 1) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);

        }


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.ctx_gotocommunity) {

//                    Toast.makeText(context, "this is got to " + communitiesModelList.get(position).siteurl, Toast.LENGTH_SHORT).show();
//                    loginVollyWebCall(communitiesModelList.get(position));

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        try {
                            checkUserLoginPost(communitiesModelList.get(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                        Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }


                }
                if (item.getItemId() == R.id.ctx_joincommunity) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        try {
                            checkUserLoginPost(communitiesModelList.get(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                        Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        popup.show();//showing popup menu

    }

    public void loginVollyWebCall(final CommunitiesModel communitiesModel) {

        if (isNetworkConnectionAvailable(context, -1)) {

//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
            final String userName = preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID);
            final String passWord = preferencesManager.getStringValue(StaticValues.KEY_USERPASSWORD);

            String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                    + userName + "&Password=" + passWord + "&MobileSiteURL="
                    + communitiesModel.siteurl + "&DownloadContent=&SiteID=" + communitiesModel.siteid;

            Log.d(TAG, "subsite login : " + urlStr);

            urlStr = urlStr.replaceAll(" ", "%20");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {


                            if (response.has("faileduserlogin")) {

                                JSONArray userloginAry = null;
                                try {
                                    userloginAry = response
                                            .getJSONArray("faileduserlogin");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (userloginAry.length() > 0) {

                                    String resultForLogin = null;
                                    try {
                                        resultForLogin = userloginAry.getJSONObject(0)
                                                .get("userstatus").toString();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (resultForLogin.contains("Login Failed")) {

                                        Toast.makeText(context,
                                                getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_authenticationfailedcontactsiteadmin),
                                                Toast.LENGTH_LONG).show();

                                    }

                                    if (resultForLogin.contains("Pending Registration")) {

                                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_pleasebepatientawaitingapproval),
                                                Toast.LENGTH_LONG).show();

                                        refreshCatalog(false);
                                    }

                                }

                                svProgressHUD.dismiss();
                            } else if (response.has("successfulluserlogin")) {

                                try {
                                    JSONArray loginResponseAry = response.getJSONArray("successfulluserlogin");
                                    if (loginResponseAry.length() != 0) {

                                        JSONObject jsonobj = loginResponseAry.getJSONObject(0);
                                        JSONObject jsonObject = new JSONObject();
                                        String userId = jsonobj.get("userid").toString();
//                                    profileWebCall(userId);
                                        jsonObject.put("userid", jsonobj.get("userid").toString());
                                        jsonObject.put("orgunitid", jsonobj.get("orgunitid"));
                                        jsonObject.put("userstatus", jsonobj.get("userstatus"));
                                        jsonObject.put("displayname", jsonobj.get("username"));
                                        jsonObject.put("siteid", jsonobj.get("siteid"));
                                        jsonObject.put("username", userName);
                                        jsonObject.put("password", passWord);
                                        jsonObject.put("siteurl", communitiesModel.siteurl);

//                                        db.insertUserCredentialsForOfflineLogin(jsonObject);

                                        Log.d(TAG, "onResponse userid: " + jsonobj.get("userid"));
                                        preferencesManager.setStringValue(userName, StaticValues.SUB_KEY_USERLOGINID);
                                        preferencesManager.setStringValue(passWord, StaticValues.SUB_KEY_USERPASSWORD);
                                        preferencesManager.setStringValue(jsonobj.get("userid").toString(), StaticValues.SUB_KEY_USERID);
                                        preferencesManager.setStringValue(jsonobj.get("username").toString(), StaticValues.SUB_KEY_USERNAME);
                                        preferencesManager.setStringValue(jsonobj.get("userstatus").toString(), StaticValues.SUB_KEY_USERSTATUS);
                                        preferencesManager.setStringValue(jsonobj.get("image").toString(), StaticValues.SUB_KEY_USERPROFILEIMAGE);

                                        MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;

//                                    Intent intentSideMenu = new Intent(context, SideMenu.class);
//                                    intentSideMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intentSideMenu);

                                        subsiteApiCalls(jsonObject.getString("siteurl"), jsonObject.getString("siteid"));
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
//                        Log.e("Error: ", error.getMessage()); 18 wed, 19 thurs, 20 friday, 21 saturday, 22 sunday, 23 monday, 24 tuesday , 25 wednesday, 26, 27, 28, 29;
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
            int MY_SOCKET_TIMEOUT_MS = 100000;
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);


        } else {
            Toast.makeText(getContext(), "  " + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet) + "  ", Toast.LENGTH_SHORT).show();
        }
    }


    public void checkUserLoginPost(final CommunitiesModel communitiesModel) throws JSONException {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/PostLoginDetails";

        final String userName = preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID);
        final String passWord = preferencesManager.getStringValue(StaticValues.KEY_USERPASSWORD);

        JSONObject parameters = new JSONObject();
        parameters.put("UserName", userName);
        parameters.put("Password", passWord);
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

                        String resultForLogin = null;
                        try {
                            resultForLogin = userloginAry.getJSONObject(0)
                                    .get("userstatus").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (resultForLogin.contains("Login Failed")) {

                            Toast.makeText(context,
                                    getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_authenticationfailedcontactsiteadmin),
                                    Toast.LENGTH_LONG).show();

                        }

                        if (resultForLogin.contains("Pending Registration")) {

                            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_pleasebepatientawaitingapproval),
                                    Toast.LENGTH_LONG).show();

                            refreshCatalog(false);
                        }

                    }

                    svProgressHUD.dismiss();
                } else if (jsonObj.has("successfulluserlogin")) {

                    try {
                        JSONArray loginResponseAry = jsonObj.getJSONArray("successfulluserlogin");
                        if (loginResponseAry.length() != 0) {

                            JSONObject jsonobj = loginResponseAry.getJSONObject(0);
                            JSONObject jsonObject = new JSONObject();
//                            String userId = jsonobj.get("userid").toString();
//                                    profileWebCall(userId);
                            jsonObject.put("userid", jsonobj.get("userid").toString());
                            jsonObject.put("orgunitid", jsonobj.get("orgunitid"));
                            jsonObject.put("userstatus", jsonobj.get("userstatus"));
                            jsonObject.put("displayname", jsonobj.get("username"));
                            jsonObject.put("siteid", jsonobj.get("siteid"));
                            jsonObject.put("username", userName);
                            jsonObject.put("password", passWord);
                            jsonObject.put("siteurl", communitiesModel.siteurl);

//                                        db.insertUserCredentialsForOfflineLogin(jsonObject);

                            Log.d(TAG, "onResponse userid: " + jsonobj.get("userid"));
                            preferencesManager.setStringValue(userName, StaticValues.SUB_KEY_USERLOGINID);
                            preferencesManager.setStringValue(passWord, StaticValues.SUB_KEY_USERPASSWORD);
                            preferencesManager.setStringValue(jsonobj.get("userid").toString(), StaticValues.SUB_KEY_USERID);
                            preferencesManager.setStringValue(jsonobj.get("username").toString(), StaticValues.SUB_KEY_USERNAME);
                            preferencesManager.setStringValue(jsonobj.get("userstatus").toString(), StaticValues.SUB_KEY_USERSTATUS);
                            preferencesManager.setStringValue(jsonobj.get("image").toString(), StaticValues.SUB_KEY_USERPROFILEIMAGE);

                            MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;

//                                    Intent intentSideMenu = new Intent(context, SideMenu.class);
//                                    intentSideMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intentSideMenu);

                            subsiteApiCalls(jsonObject.getString("siteurl"), jsonObject.getString("siteid"));
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void attachFragment(CommunitiesModel communitiesModel) {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            if (communitiesModel.actiongoto == 1) {
                try {
                    checkUserLoginPost(communitiesModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    public void subsiteApiCalls(String subSiteUrl, String subSiteID) {

        if (isNetworkConnectionAvailable(context, -1)) {

            if (getSubSiteConfigsAsycTask.getStatus() == AsyncTask.Status.PENDING) {
                // My getSiteConfigsAsycTask is currently doing work in doInBackground()
                getSubSiteConfigsAsycTask.siteConfigInterface = new SiteConfigInterface() {
                    @Override
                    public void preExecuteIn() {
                        Log.d(TAG, "preExecuteIn: ");
                    }

                    @Override
                    public void progressUpdateIn(int status) {
                        Log.d(TAG, "progressUpdateIn: " + status);
                    }

                    @Override
                    public void postExecuteIn(String results) {
                        Log.d(TAG, "postExecuteIn: " + results);
                        if (results.equalsIgnoreCase("true")) {
                            preferencesManager.setStringValue(results, StaticValues.SUB_SITE_ENTERED);
                            try {
                                CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;
                                MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;
//                                ((SideMenu) getActivity()).drawer.openDrawer(Gravity.LEFT);
                                clickHomeButton();
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        }
                        svProgressHUD.dismiss();
                    }
                };
                getSubSiteConfigsAsycTask.execute(subSiteUrl, subSiteID);

            } else {

                LogUtils.d("already running subsitelogin ");
            }
        }

    }

    public void clickHomeButton() {

        new CountDownTimer(500, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                ((SideMenu) getActivity()).homeControllClicked(false, 0, "", true, "");
            }
        }.start();

    }

}