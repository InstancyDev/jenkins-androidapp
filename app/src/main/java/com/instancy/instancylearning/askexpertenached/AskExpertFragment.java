package com.instancy.instancylearning.askexpertenached;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalsearch.GlobalSearchActivity;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.interfaces.TagClicked;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.StaticValues.GLOBAL_SEARCH;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


/**
 * Created by Upendranath on 5/19/2017.
 **/

@TargetApi(Build.VERSION_CODES.N)
public class AskExpertFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, View.OnClickListener, TagClicked {

    String TAG = AskExpertFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    AskExpertDbTables db;

    TagClicked tagClicked;

    @BindView(R.id.skillTxt)
    TextView skillTxt;

    @BindView(R.id.sortbyTxt)
    TextView sortbyTxt;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.askexpertslistview)
    ListView askexpertListView;

    AskExpertAdapter askExpertAdapter;
    List<AskExpertQuestionModelDg> askExpertQuestionModelList = null;

    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    ResultListner resultListner = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;


    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    boolean isFromNotification = false, nextLevel = false, nextLevel2 = false;

    String contentIDFromNotification = "", contentIdForDetailScreen = "";

    boolean isFromGlobalSearch = false;

    String queryText = "";

    List<AskExpertCategoriesModelDigi> askExpertCategoriesModelsList = null;

    List<AskExpertSkillsModelDg> askExpertSkillsModelList = null;


    String[] skillsForFilter;
    String[] categoriesID;

    @Nullable
    @BindView(R.id.fab_ask_question)
    android.support.design.widget.FloatingActionButton floatingActionButton;


    public AskExpertFragment() {


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new AskExpertDbTables(context);
        initVolleyCallback();
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        vollyService = new VollyService(resultCallback, context);

        sideMenusModel = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");

            isFromNotification = bundle.getBoolean("ISFROMNOTIFICATIONS");

            if (isFromNotification) {

                contentIDFromNotification = bundle.getString("CONTENTID");
            }

            isFromGlobalSearch = bundle.getBoolean("ISFROMGLOBAL");

            if (isFromGlobalSearch) {

                queryText = bundle.getString("query");

                contentIDFromNotification = bundle.getString("FOLDERID");

                contentIdForDetailScreen = bundle.getString("ANSWERID", "");

                nextLevel = bundle.getBoolean("nextLevel", false);

                nextLevel2 = bundle.getBoolean("nextLevel2", false);

            }

        }
    }

    public void refreshAskQuestions(Boolean isRefreshed) {
        if (!isRefreshed) {

            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }

//        String parmStringUrl = "http://angular6api.instancysoft.com/api/MobileLMS/GetAsktheExpertData?intSiteID=" + appUserModel.getSiteIDValue() + "&UserID=" + appUserModel.getUserIDValue() + "&astrLocale="+ preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))+"&aintComponentID=" + sideMenusModel.getComponentId() + "&aintCompInsID=" + sideMenusModel.getRepositoryId() + "&aintSelectedGroupValue=0";

        String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetAsktheExpertData?UserID=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getSiteIDValue() + "&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&ComponentID=" + sideMenusModel.getComponentId() + "&intSkillID=-1&SortBy=CreatedDate%20Desc&pageIndex=1&pageSize=1000&SearchText=" + queryText;

        vollyService.getStringResponseVolley("GetAsktheExpertData", parmStringUrl, appUserModel.getAuthHeaders());

    }

    public void getUserQuestionSkills() {
// for question asking filters
        String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetUserQuestionSkills?aintSiteID=" + appUserModel.getSiteIDValue() + "&astrType=selected";

        vollyService.getStringResponseVolley("GetUserQuestionSkills", parmStringUrl, appUserModel.getAuthHeaders());

    }


    public void getFilterUserSkills() {
// for filter skills
        String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetFilterUserSkills";

        vollyService.getStringResponseVolley("GetFilterUserSkills", parmStringUrl, appUserModel.getAuthHeaders());
    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);

                if (requestType.equalsIgnoreCase("GetAsktheExpertData")) {
                    if (response != null) {
                        try {
                            db.injectAsktheExpertQuestionDataIntoTable(response);
                            injectFromDbtoModel();
                            getUserQuestionSkills();
                            getFilterUserSkills();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                if (requestType.equalsIgnoreCase("GetFilterUserSkills")) {
                    if (response != null) {
                        try {

                            db.injectAsktheExpertSkillFilters(response);
                            svProgressHUD.dismiss();
                            db.injectAsktheExpertMappingQuestion(response);
                            askExpertCategoriesModelsList = db.fetchAskFilterSkillsList();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (requestType.equalsIgnoreCase("GetUserQuestionSkills")) {
                    if (response != null) {
                        try {

                            db.injectAsktheExpertsSkills(response);
                            askExpertSkillsModelList = db.fetchAskExpertSkillsList();

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void generateSkillsFromJsonObj(JSONObject response) {

        JSONArray jsonAskCategories = response.optJSONArray("askcategories");

        if (jsonAskCategories.length() > 0) {
            skillsForFilter = new String[jsonAskCategories.length()];
            categoriesID = new String[jsonAskCategories.length()];

            for (int i = 0; i < jsonAskCategories.length(); i++) {

                JSONObject object = jsonAskCategories.optJSONObject(i);
                skillsForFilter[i] = object.optString("category");
                categoriesID[i] = object.optString("categoryid");
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.askexpertsfragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        askExpertQuestionModelList = new ArrayList<AskExpertQuestionModelDg>();
        askExpertAdapter = new AskExpertAdapter(getActivity(), BIND_ABOVE_CLIENT, askExpertQuestionModelList, tagClicked);
        askexpertListView.setAdapter(askExpertAdapter);
        askExpertAdapter.tagClicked = this;
        askexpertListView.setOnItemClickListener(this);
        askexpertListView.setEmptyView(rootView.findViewById(R.id.nodata_label));


        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshAskQuestions(false);
        } else {
            injectFromDbtoModel();
        }

        skillTxt.setOnClickListener(this);
        sortbyTxt.setOnClickListener(this);

        if (isFromGlobalSearch) {
            swipeRefreshLayout.setEnabled(false);
        }
        nodata_Label.setText("");
        initilizeView();
        initilizeFloatBtn(context);

        return rootView;
    }

    public void injectFromDbtoModel() {
        askExpertQuestionModelList = db.fetchAskExpertsQuestions("");
        if (askExpertQuestionModelList != null) {
            askExpertAdapter.refreshList(askExpertQuestionModelList);
        } else {
            askExpertQuestionModelList = new ArrayList<AskExpertQuestionModelDg>();
            askExpertAdapter.refreshList(askExpertQuestionModelList);
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
        }

//        List<AskExpertQuestionModelDg> askExpertQuestionModelDgList = db.fetchAskExpertsQuestions("");

//        if (askExpertQuestionModelList.size() > 5) {
//            if (item_search != null && !isFromGlobalSearch)
//                item_search.setVisible(true);
//        } else {
//            if (item_search != null)
//                item_search.setVisible(false);
//        }

        triggerActionForFirstItem();
    }

    public void triggerActionForFirstItem() {

        if (isFromNotification || nextLevel) {
            int selectedPostion = getPositionForNotification(contentIDFromNotification);
            askexpertListView.setSelection(selectedPostion);

            if (askExpertQuestionModelList != null) {

                try {
                    attachFragment(askExpertQuestionModelList.get(selectedPostion));
                    isFromNotification = false;
                } catch (IndexOutOfBoundsException ex) {
//                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel), Toast.LENGTH_SHORT).show();
                }

            } else {

                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public int getPositionForNotification(String contentID) {
        int position = 0;
        int contentIntID = 0;
        try {
            contentIntID = Integer.parseInt(contentID);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        for (int k = 0; k < askExpertQuestionModelList.size(); k++) {
            if (askExpertQuestionModelList.get(k).questionID == contentIntID) {
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
        if (isFromGlobalSearch) {
            item_search.setVisible(false);
        }


        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setTitle(getLocalizationValue(JsonLocalekeys.search_label));
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint(getLocalizationValue(JsonLocalekeys.commoncomponent_label_searchlabel));
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    askExpertAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });

        }

        item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                if (uiSettingsModel.isGlobasearch()) {
                    gotoGlobalSearch();
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                return true;
            }
        });
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
            queryText="";
            refreshAskQuestions(true);
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
                updateViewsCount(position, askExpertQuestionModelList.get(position));
                attachFragment(askExpertQuestionModelList.get(position));
                break;
            case R.id.btn_contextmenu:
//                View v = askexpertListView.getChildAt(position - askexpertListView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) view.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, askExpertQuestionModelList.get(position));
                break;
            case R.id.txtno_answers:
                updateViewsCount(position, askExpertQuestionModelList.get(position));
                attachFragment(askExpertQuestionModelList.get(position));
                break;
            case R.id.txtno_views:
                break;
            default:

        }

    }

    public void updateViewsCount(int position, AskExpertQuestionModelDg questionModelDg) {

        if (questionModelDg.totalViews == 0) {
            askExpertQuestionModelList.get(position).totalViews = 1;
            askExpertAdapter.notifyDataSetChanged();
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

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final AskExpertQuestionModelDg askExpertQuestionModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.askexpertmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene
        Menu menu = popup.getMenu();
        menu.getItem(0).setVisible(true);//delete ctx_edit
        menu.getItem(1).setVisible(true);// ctx_edit
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_deleteoption));//view
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_editoption));;//enroll

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ctx_delete:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setCancelable(false).setTitle(getLocalizationValue(JsonLocalekeys.profile_alerttitle_stringconfirmation)).setMessage(getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_areyousurewanttopermanentlydeletequestion))
                                .setPositiveButton(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_deleteoption), new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialogBox, int id) {
                                        // ToDo get user input here
                                        deleteQuestionFromServer(askExpertQuestionModel);
                                    }
                                }).setNegativeButton(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_canceloption),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                        AlertDialog alertDialogAndroid = alertDialog.create();
                        alertDialogAndroid.show();
                        break;
                    case R.id.ctx_edit:
                        callAskQuestion(position);
                }
                return true;

            }
        });
        popup.show();//showing popup menu

    }

    public void deleteQuestionFromServer(final AskExpertQuestionModelDg questionModel) {

//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);


        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteAskQuestion?QuestionID=" + questionModel.questionID + "&UserUploadimage=" + questionModel.userQuestionImagePath;

        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess)
                            +" \n"+getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_questiondeletesuccess_message), Toast.LENGTH_SHORT).show();
                    refreshAskQuestions(true);
//                    deleteQuestionFromLocalDB(questionModel);
                } else {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_questionauthenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);

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

        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("NEWQS", false);
                if (refresh) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        refreshAskQuestions(true);
                    } else {
                        injectFromDbtoModel();
                    }

                }
            }
        }
        if (requestCode == GLOBAL_SEARCH && resultCode == RESULT_OK) {
            if (data != null) {
                queryText = data.getStringExtra("queryString");
                if (queryText.length() > 0) {

                    refreshAskQuestions(true);

                }

            }
        }

    }

    public void attachFragment(AskExpertQuestionModelDg askExpertQuestionModel) {
        Intent intentDetail = new Intent(context, AskExpertsAnswersActivity.class);
        intentDetail.putExtra("AskExpertQuestionModelDg", askExpertQuestionModel);
        intentDetail.putExtra("sidemenumodel", sideMenusModel);
//        intentDetail.putExtra("nextLevel2", nextLevel2);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
        oneTimeViewApiCall(askExpertQuestionModel);
    }

    public void oneTimeViewApiCall(AskExpertQuestionModelDg askExpertQuestionModel) {

        String parmStringUrl = appUserModel.getWebAPIUrl() + "AsktheExpert/getSetUserQuestionviews?UserID=" + appUserModel.getUserIDValue() + "&intQuestionID=" + askExpertQuestionModel.questionID;

        vollyService.getStringResponseVolley("getSetUserQuestionviews", parmStringUrl, appUserModel.getAuthHeaders());


    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.skillTxt:
                filterPopUp();
                break;
            case R.id.sortbyTxt:
                showSortPopup(view);
                break;
            case R.id.fab_ask_question:
                callAskQuestion(-1);
                break;

        }
    }

    public void callAskQuestion(int position) {
        Intent intentDetail = new Intent(context, AskQuestionActivity.class);
        AskExpertQuestionModelDg askExpertQuestionModel = null;
        if (position != -1) {
            askExpertQuestionModel = askExpertQuestionModelList.get(position);
            intentDetail.putExtra("askExpertQuestionModel", askExpertQuestionModel);
            intentDetail.putExtra("EDIT", true);
        }
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
    }

    private void showSortPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        // Inflate the menu from xml
        popup.inflate(R.menu.sortbymenus);
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ctx_ssort_select:
                        askExpertAdapter.applySortBy(true, "-1");
                        break;
                    case R.id.ctx_sort_activity:
                        askExpertAdapter.applySortBy(false, "0");
                        break;
                    case R.id.ctx_sort_answers:
                        askExpertAdapter.applySortBy(false, "1");
                        break;
                    case R.id.ctx_sort_views:
                        askExpertAdapter.applySortBy(true, "2");
                        break;
                    case R.id.ctx_sort_title:
                        askExpertAdapter.applySortBy(true, "3");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        popup.show();
    }


    public void filterPopUp() {


        if (askExpertCategoriesModelsList != null && askExpertCategoriesModelsList.size() == 0) {
            askExpertCategoriesModelsList = db.fetchAskFilterSkillsList();
        }

        if (askExpertCategoriesModelsList != null && askExpertCategoriesModelsList.size() > 0) {
            skillsForFilter = new String[askExpertCategoriesModelsList.size()];
            categoriesID = new String[askExpertCategoriesModelsList.size()];

            for (int s = 0; s < askExpertCategoriesModelsList.size(); s++) {
                skillsForFilter[s] = askExpertCategoriesModelsList.get(s).category;
                categoriesID[s] = askExpertCategoriesModelsList.get(s).categoryID;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            //set the title for alert dialog
            builder.setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_chooseskillinordertofilter));

            //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog
            builder.setItems(skillsForFilter, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {
                    //setting the button text to the selected itenm from the list

//                    Toast.makeText(context, " Selected" + strSplitvalues[item], Toast.LENGTH_SHORT).show();

                    filteredByCategory(categoriesID[item], skillsForFilter[item]);

                }
            });

            //Creating CANCEL button in alert dialog, to dismiss the dialog box when nothing is selected
            builder.setCancelable(false)
                    .setNegativeButton(getLocalizationValue(JsonLocalekeys.asktheexpert_alertbutton_cancelbutton), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //When clicked on CANCEL button the dalog will be dismissed
                            dialog.dismiss();


                        }
                    }).setPositiveButton(getLocalizationValue(JsonLocalekeys.asktheexpert_alertbutton_allbutton), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

//                    Toast.makeText(context, "   all clicked    ", Toast.LENGTH_SHORT).show();
                    filteredByCategory("", "");
                }
            });

            //Creating alert dialog
            AlertDialog alert = builder.create();
            //Showingalert dialog
            alert.show();
        } else {
            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_label_filtersnotconfigured), Toast.LENGTH_SHORT).show();
        }
    }

    public void filteredByCategory(String selectedCategory, String selectedSkillName) {

        askExpertQuestionModelList = db.fetchAskExpertsQuestions(selectedCategory);

        if (askExpertQuestionModelList != null && askExpertQuestionModelList.size() > 0) {
            askExpertAdapter.refreshList(askExpertQuestionModelList);
        } else {
            askExpertQuestionModelList = new ArrayList<>();
            askExpertAdapter.refreshList(askExpertQuestionModelList);
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
        }
        if (selectedSkillName.length() != 0) {

            if (selectedSkillName.length() > 25) {
                skillTxt.setTextSize(12);
            } else {
                skillTxt.setTextSize(16);
            }

            skillTxt.setText(selectedSkillName);
        } else {
            skillTxt.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_alertbutton_allbutton));
            skillTxt.setTextSize(16);
        }
    }

    public void initilizeFloatBtn(Context context) {

        assert floatingActionButton != null;
        floatingActionButton.setImageDrawable(getDrawableFromStringHOmeMethod(R.string.fa_icon_question, context, uiSettingsModel.getAppHeaderTextColor()));
        floatingActionButton.setOnClickListener(this);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));

    }

    @Override
    public void tagClickedInterface(String categoryName) {

        Toast.makeText(context, "" + categoryName, Toast.LENGTH_SHORT).show();
        String categoryId = getCategoryIdFrom(categoryName);
        filteredByCategory(categoryId, categoryName);

    }

    public String getCategoryIdFrom(String skillName) {
        String categoryID = "";
        if (askExpertCategoriesModelsList != null && askExpertCategoriesModelsList.size() == 0) {
            askExpertCategoriesModelsList = db.fetchAskFilterSkillsList();
        }

        for (int s = 0; s < askExpertCategoriesModelsList.size(); s++) {
            if (skillName.trim().toLowerCase().equalsIgnoreCase(askExpertCategoriesModelsList.get(s).category.trim().toLowerCase())) {
                categoryID = askExpertCategoriesModelsList.get(s).categoryID;
            }
        }

        return categoryID;
    }

    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,getActivity());

    }

}
