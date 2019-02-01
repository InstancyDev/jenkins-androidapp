package com.instancy.instancylearning.discussionfourmsenached;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
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
import android.text.Spanned;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.instancy.instancylearning.askexpertenached.AskExpertsAnswersActivity;
import com.instancy.instancylearning.discussionfourmsenached.CreateNewForumActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalsearch.GlobalSearchActivity;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.StaticValues.GLOBAL_SEARCH;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class DiscussionFourm_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, View.OnClickListener {

    String TAG = DiscussionFourm_fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DiscussionFourmsDbTables db;
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.discussionfourmlist)
    ListView discussionFourmlistView;
    DiscussionFourmAdapter discussionFourmAdapter;
    List<DiscussionForumModelDg> discussionForumModelList = null;
    List<DiscussionForumModelDg> originalForumList = null;
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

    @Nullable
    @BindView(R.id.fab_fourm_button)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.txtCategoriesName)
    TextView txtCategoriesName;

    @BindView(R.id.txtCategoriesClear)
    TextView txtCategoriesClear;

    @BindView(R.id.lltagslayout)
    LinearLayout lltagslayout;

    @BindView(R.id.txtCategoriesIcon)
    TextView txtCategoriesIcon;

    @BindView(R.id.lytCategories)
    RelativeLayout lytCategories;

    @BindView(R.id.tagsRelative)
    RelativeLayout tagsRelative;

    List<ContentValues> breadcrumbItemsList = null;

    @BindView(R.id.cflBreadcrumb)
    CustomFlowLayout tagsCategories;

    @Nullable
    @BindView(R.id.txtCategoriesCount)
    TextView txtCategoriesCount;

    boolean isFromNotification = false, isFromGlobalSearch = false, nextLevel = false;

    String contentIDFromNotification = "", queryString = "";
    String topicID = "";

    boolean isPrivilageForCreateForum = false, isPrivateForum = false, isAbleToDelete = false, isAbleToEdit = false;


    BottomSheetDialog bottomSheetDialog;

    List<LikesModel> likesModelList;

    public DiscussionFourm_fragment() {


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
        db = new DiscussionFourmsDbTables(context);
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
            isFromGlobalSearch = bundle.getBoolean("ISFROMGLOBAL", false);
            if (isFromNotification) {

                contentIDFromNotification = bundle.getString("TOPICID");
                topicID = bundle.getString("CONTENTID");
            }

            if (isFromGlobalSearch) {
                queryString = bundle.getString("query");

                nextLevel = bundle.getBoolean("nextLevel", false);
            }

        }

        isPrivilageForCreateForum = db.isPrivilegeExistsFor(StaticValues.CREATEFORUM);

        isAbleToDelete = db.isPrivilegeExistsFor(StaticValues.EDITFORUM);

        isAbleToEdit = db.isPrivilegeExistsFor(StaticValues.DELETEFORUM);

        isPrivateForum = db.isPrivilegeExistsFor(StaticValues.PRIVATEPREVILAGEISALLOWED);

    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }


        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/GetForumList";

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("intUserID", appUserModel.getUserIDValue());
            parameters.put("intSiteID", appUserModel.getSiteIDValue());
            parameters.put("strLocale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("strSearchText", queryString);
            parameters.put("intCompID", sideMenusModel.getComponentId());
            parameters.put("intCompInsID", sideMenusModel.getRepositoryId());
            parameters.put("intShowPrivateForums", "0");
            parameters.put("strSortCondition", "CreatedDate%20Desc");
            parameters.put("sortby", "");
            parameters.put("sorttype", "");
            parameters.put("pageIndex", "1");
            parameters.put("pageSize", 100);
            parameters.put("RecordsCount", 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "GetForumList", urlStr);
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
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                if (requestType.equalsIgnoreCase("GetForumList")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {

                            if (isFromGlobalSearch) {
                                discussionForumModelList = createDiscussionFourmList(response);
                                discussionFourmAdapter.refreshList(discussionForumModelList);
                            } else {
                                db.injectDiscussionFourmDataIntoTable(response);
                                refreshCatagories();
                                injectFromDbtoModel();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }

                if (requestType.equalsIgnoreCase("GetForumLevelLikeList")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            likesModelList = generateLikesList(jsonArray);
                            showLikesList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }

                if (requestType.equalsIgnoreCase("DeleteForum")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        refreshCatalog(true);
                    } else {

                    }
                }

                if (requestType.equalsIgnoreCase("GetCategories")) {
                    if (response != null) {
                        try {
                            db.injectDiscussionCategoriesResponse(response);

                            Log.d(TAG, "notifySuccess: ");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

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


    public void refreshCatagories() {


        String urlStr = appUserModel.getWebAPIUrl() + "DiscussionForums/GetCategories";

        JSONObject parameters = new JSONObject();


        try {
            parameters.put("intUserID", appUserModel.getUserIDValue());
            parameters.put("intSiteID", appUserModel.getSiteIDValue());
            parameters.put("strLocale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("intCompID", sideMenusModel.getComponentId());
            parameters.put("intCompInsID", sideMenusModel.getRepositoryId());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "GetCategories", urlStr);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.discussionfourm_fragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        discussionForumModelList = new ArrayList<DiscussionForumModelDg>();
        originalForumList = new ArrayList<DiscussionForumModelDg>();
        discussionFourmAdapter = new DiscussionFourmAdapter(getActivity(), BIND_ABOVE_CLIENT, discussionForumModelList, isAbleToDelete, isAbleToEdit);
        discussionFourmlistView.setAdapter(discussionFourmAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
//        discussionFourmlistView.setEmptyView(rootView.findViewById(R.id.nodata_label));

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
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        if (isPrivilageForCreateForum) {
            floatingActionButton.setVisibility(View.VISIBLE);
        }

        if (isFromGlobalSearch) {
            swipeRefreshLayout.setEnabled(false);
        }

        floatingActionButton.setImageDrawable(d);

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDetail = new Intent(context, CreateNewForumActivity.class);
                intentDetail.putExtra("isfromedit", false);
                intentDetail.putExtra("forumModel", "");
                startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
            }
        });

        txtCategoriesName.setVisibility(View.VISIBLE);
        lytCategories.setVisibility(View.VISIBLE);
        txtCategoriesIcon.setOnClickListener(this);
        txtCategoriesName.setOnClickListener(this);

        tagsRelative.setVisibility(View.VISIBLE);
        tagsRelative.setOnClickListener(this);

        lltagslayout.setVisibility(View.VISIBLE);
        lltagslayout.setOnClickListener(this);

        txtCategoriesClear.setVisibility(View.GONE);
        txtCategoriesClear.setOnClickListener(this);

        tagsCategories.setVisibility(View.GONE);

        txtCategoriesClear.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        txtCategoriesIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCategoriesName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        FontManager.markAsIconContainer(txtCategoriesIcon, iconFont);

        txtCategoriesIcon.setText( context.getResources().getString(R.string.fa_icon_sort_down));

        return rootView;
    }


    public List<DiscussionForumModelDg> createDiscussionFourmList(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        List<DiscussionForumModelDg> discussionForumModelList1 = new ArrayList<>();

        JSONArray jsonTableAry = jsonObject.getJSONArray("forumList");

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            DiscussionForumModelDg discussionForumModel = new DiscussionForumModelDg();

            discussionForumModel.forumID = jsonMyLearningColumnObj.optInt("ForumID");
            discussionForumModel.name = jsonMyLearningColumnObj.optString("Name");
            discussionForumModel.description = jsonMyLearningColumnObj.optString("Description");
            discussionForumModel.parentForumID = jsonMyLearningColumnObj.optInt("ParentForumID");
            discussionForumModel.displayOrder = jsonMyLearningColumnObj.optInt("DisplayOrder");
            discussionForumModel.createdDate = jsonMyLearningColumnObj.optString("CreatedDate");
            discussionForumModel.siteID = jsonMyLearningColumnObj.optInt("SiteID");
            discussionForumModel.createdUserID = jsonMyLearningColumnObj.optInt("CreatedUserID");
            discussionForumModel.createdDate = jsonMyLearningColumnObj.optString("CreatedDate");
            discussionForumModel.active = jsonMyLearningColumnObj.optBoolean("Active");
            discussionForumModel.requiresSubscription = jsonMyLearningColumnObj.optBoolean("RequiresSubscription");
            discussionForumModel.createNewTopic = jsonMyLearningColumnObj.optBoolean("CreateNewTopic");
            discussionForumModel.attachFile = jsonMyLearningColumnObj.optBoolean("AttachFile");
            discussionForumModel.likePosts = jsonMyLearningColumnObj.optBoolean("LikePosts");
            discussionForumModel.sendEmail = jsonMyLearningColumnObj.optBoolean("SendEmail");
            discussionForumModel.moderation = jsonMyLearningColumnObj.optBoolean("Moderation");
            discussionForumModel.isPrivate = jsonMyLearningColumnObj.optBoolean("IsPrivate");
            discussionForumModel.author = jsonMyLearningColumnObj.optString("Author");
            discussionForumModel.noOfTopics = jsonMyLearningColumnObj.optInt("NoOfTopics");
            discussionForumModel.totalPosts = jsonMyLearningColumnObj.optInt("TotalPosts");
            discussionForumModel.existing = jsonMyLearningColumnObj.optInt("Existing");
            discussionForumModel.totalLikes = jsonMyLearningColumnObj.optInt("TotalLikes");
            discussionForumModel.dfProfileImage = jsonMyLearningColumnObj.optString("DFProfileImage");
            discussionForumModel.dfUpdateTime = jsonMyLearningColumnObj.optString("DFUpdateTime");
            discussionForumModel.dfChangeUpdateTime = jsonMyLearningColumnObj.optString("DFChangeUpdateTime");
            discussionForumModel.forumThumbnailPath = jsonMyLearningColumnObj.optString("ForumThumbnailPath");
            discussionForumModel.descriptionWithLimit = jsonMyLearningColumnObj.optString("DescriptionWithLimit");
            discussionForumModel.moderatorID = jsonMyLearningColumnObj.optInt("ModeratorID");
            discussionForumModel.updatedAuthor = jsonMyLearningColumnObj.optString("UpdatedAuthor");
            discussionForumModel.updatedDate = jsonMyLearningColumnObj.optString("UpdatedDate");
            discussionForumModel.moderatorName = jsonMyLearningColumnObj.optString("ModeratorName");
            discussionForumModel.allowShare = jsonMyLearningColumnObj.optBoolean("AllowShare");
            discussionForumModel.descriptionWithoutLimit = jsonMyLearningColumnObj.optString("DescriptionWithoutLimit");

            discussionForumModelList1.add(discussionForumModel);
        }
        return discussionForumModelList1;
    }


    public void injectFromDbtoModel() {

        discussionForumModelList = db.fetchDiscussionForums(appUserModel.getSiteIDValue(), isPrivateForum);
        if (discussionForumModelList != null) {
            discussionFourmAdapter.refreshList(discussionForumModelList);
            nodata_Label.setText("");
            originalForumList = discussionForumModelList;
        } else {
            discussionForumModelList = new ArrayList<DiscussionForumModelDg>();
            discussionFourmAdapter.refreshList(discussionForumModelList);
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
            originalForumList = discussionForumModelList;
        }


        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {
            generateBreadcrumb(breadcrumbItemsList);
            txtCategoriesName.setVisibility(View.GONE);
            lytCategories.setVisibility(View.GONE);
            txtCategoriesClear.setVisibility(View.VISIBLE);
            tagsCategories.setVisibility(View.VISIBLE);

            discussionForumModelList = getDiscussionForumListCategories(breadcrumbItemsList);
            discussionFourmAdapter.refreshList(discussionForumModelList);
        }

//        if (discussionForumModelList.size() > 5 && !isFromGlobalSearch) {
//            if (item_search != null)
//                item_search.setVisible(true);
//        } else {
//            if (item_search != null)
//                item_search.setVisible(false);
//        }
        triggerActionForFirstItem();
    }

    public void triggerActionForFirstItem() {

        if (isFromNotification) {
            int selectedPostion = getPositionForNotification(contentIDFromNotification);
//            discussionFourmlistView.setSelection(selectedPostion);

            if (discussionForumModelList != null) {

                try {
                    attachFragment(discussionForumModelList.get(selectedPostion), isFromNotification);
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

        for (int k = 0; k < discussionForumModelList.size(); k++) {
            if (discussionForumModelList.get(k).forumID == contentIntID) {
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
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    discussionFourmAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });


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
            queryString = "";
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
                attachFragment(discussionForumModelList.get(position), isFromNotification);
                break;
            case R.id.btn_contextmenu:
                View v = discussionFourmlistView.getChildAt(position - discussionFourmlistView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, discussionForumModelList.get(position));
                break;
            case R.id.txtLikes:
                getForumLevelLikeList(discussionForumModelList.get(position));
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

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, DiscussionForumModelDg discussionForumModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.discussonforum, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_editforumoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_deleteforumoption));
        if (isAbleToEdit) {
            menu.getItem(0).setVisible(true);//view
        } else {
            menu.getItem(0).setVisible(false);//view

        }

        if (isAbleToDelete) {
            menu.getItem(1).setVisible(true);//view
        } else {
            menu.getItem(1).setVisible(false);//view


        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ctx_edit:
                        Intent intentDetail = new Intent(context, CreateNewForumActivity.class);
                        intentDetail.putExtra("isfromedit", true);
                        intentDetail.putExtra("forumModel", discussionForumModelList.get(position));
                        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
                        break;
                    case R.id.ctx_delete:
                        deleteForumFromServer(discussionForumModelList.get(position));
                        break;
                }
                return true;
            }
        });
        popup.show();//showing popup menu

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("NEWFORUM", false);
                if (refresh) {
                    refreshCatalog(true);
                }
            }
        }

        if (requestCode == GLOBAL_SEARCH && resultCode == RESULT_OK) {
            if (data != null) {
                queryString = data.getStringExtra("queryString");
                if (queryString.length() > 0) {

                    refreshCatalog(true);

                }

            }
        }

        if (requestCode == FILTER_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                boolean refresh = data.getBooleanExtra("FILTER", false);
                if (refresh) {
                    List<ContentValues> selectedCategories = new ArrayList<ContentValues>();
                    selectedCategories = (List<ContentValues>) data.getExtras().getSerializable("selectedCategories");
                    Log.d(TAG, "selectedCategories: " + selectedCategories.size());

                    if (selectedCategories.size() > 0) {
                        generateBreadcrumb(selectedCategories);
                        breadcrumbItemsList = selectedCategories;
                        txtCategoriesName.setVisibility(View.GONE);
                        lytCategories.setVisibility(View.GONE);
                        txtCategoriesClear.setVisibility(View.VISIBLE);
                        tagsCategories.setVisibility(View.VISIBLE);
                    }

                    discussionForumModelList = getDiscussionForumListCategories(selectedCategories);
                    discussionFourmAdapter.refreshList(discussionForumModelList);
                }

            }
        }

    }

    public List<DiscussionForumModelDg> getDiscussionForumListCategories(List<ContentValues> categoryIds) {
        List<DiscussionForumModelDg> discussionForumModelDgs = new ArrayList<>();

        if (categoryIds != null && categoryIds.size() > 0) {

            for (int i = 0; i < categoryIds.size(); i++) {

                for (int j = 0; j < originalForumList.size(); j++) {

                    if (originalForumList.get(j).categoriesIDArray != null && originalForumList.get(j).categoriesIDArray.size() > 0) {
                        for (int k = 0; k < originalForumList.get(j).categoriesIDArray.size(); k++) {

                            if (categoryIds.get(i).get("categoryid").toString().equalsIgnoreCase(originalForumList.get(j).categoriesIDArray.get(k))) {

                                discussionForumModelDgs.add(originalForumList.get(j));

                            }

                        }

                    }
                }

            }

        }

        Set<DiscussionForumModelDg> hs = new HashSet<>();
        hs.addAll(discussionForumModelDgs);
        discussionForumModelDgs.clear();
        discussionForumModelDgs.addAll(hs);

        if (discussionForumModelDgs.size() == 0) {
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
        }

        return discussionForumModelDgs;
    }

    public void attachFragment(DiscussionForumModelDg forumModel, boolean isFromNotification) {
        Intent intentDetail = new Intent(context, DiscussionTopicActivity.class);
        intentDetail.putExtra("forumModel", forumModel);
        intentDetail.putExtra("NOTIFICATION", isFromNotification);
        intentDetail.putExtra("TOPICID", topicID);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    public void getForumLevelLikeList(final DiscussionForumModelDg forumModel) {
        if (isNetworkConnectionAvailable(context, -1)) {

            String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetForumLevelLikeList?strObjectID=" + forumModel.forumID + "&intUserID=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getUserIDValue() + "&strLocale=preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))";

            vollyService.getStringResponseVolley("GetForumLevelLikeList", parmStringUrl, appUserModel.getAuthHeaders());

        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }


    public void deleteForumFromServer(final DiscussionForumModelDg forumModel) {
        if (isNetworkConnectionAvailable(context, -1)) {

            String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/DeleteForum?ForumID=" + forumModel.forumID + "&UserID=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getUserIDValue() + "&LocaleID=preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))";

            vollyService.getStringResponseVolley("DeleteForum", parmStringUrl, appUserModel.getAuthHeaders());

        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }


    public List<LikesModel> generateLikesList(JSONArray jsonArray) {
        List<LikesModel> likesModelList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonAnswerColumnObj = null;
            try {
                jsonAnswerColumnObj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LikesModel likesModel = new LikesModel();

            likesModel.userID = jsonAnswerColumnObj.optInt("UserID");
            likesModel.userAddress = jsonAnswerColumnObj.optString("UserAddress");
            likesModel.picture = jsonAnswerColumnObj.optString("UserThumb");
            likesModel.userName = jsonAnswerColumnObj.optString("UserName");
            likesModel.jobTitle = jsonAnswerColumnObj.optString("UserDesg");

            likesModelList.add(likesModel);
        }

        return likesModelList;
    }

    private void showLikesList() {

        if (likesModelList != null && likesModelList.size() > 0) {
            View view = getLayoutInflater().inflate(R.layout.askexpertsupvotersbottomsheet, null);

            ListView lv_languages = (ListView) view.findViewById(R.id.lv_languages);
            LikesAdapter upvotersAdapter = new LikesAdapter(getActivity(), likesModelList);
            lv_languages.setAdapter(upvotersAdapter);
            TextView txtCountVoted = view.findViewById(R.id.txtCountVoted);
            txtCountVoted.setText(likesModelList.size() + " " + getLocalizationValue(JsonLocalekeys.discussionforum_label_likeslabel));
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        } else {
            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_label_nolikeslabel), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.txtCategoriesIcon:
            case R.id.txtCategoriesName:
            case R.id.lltagslayout:
                chooseCategory();
                break;
            case R.id.txtCategoriesClear:
                clearCategory();
                break;

        }
    }

    public void clearCategory() {


        txtCategoriesCount.setText("");
        breadcrumbItemsList = new ArrayList<>();
        generateBreadcrumb(breadcrumbItemsList);
        txtCategoriesName.setVisibility(View.VISIBLE);
        lytCategories.setVisibility(View.VISIBLE);
        txtCategoriesClear.setVisibility(View.GONE);
        tagsCategories.setVisibility(View.GONE);
        injectFromDbtoModel();
    }

    public void chooseCategory() {
        Intent intentDetail = new Intent(context, DiscussionforumCategories.class);
        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {
            intentDetail.putExtra("breadcrumbItemsList", (Serializable) breadcrumbItemsList);
            intentDetail.putExtra("FILTER", true);
        } else {
            intentDetail.putExtra("FILTER", false);
        }

        startActivityForResult(intentDetail, FILTER_CLOSE_CODE);
    }


    public void generateBreadcrumb(List<ContentValues> dicBreadcrumbItems) {
        boolean isFirstCategory = true;
        ContentValues cvBreadcrumbItem = null;
        // int lastCategory = 10;
        tagsCategories.removeAllViews();
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
                    + categoryName + "<b> X </b>" + "</small>  </font>"));

            textView.setGravity(Gravity.CENTER | Gravity.CENTER);
            textView.setTag(R.id.CATALOG_CATEGORY_ID_TAG, categoryId);
            textView.setTag(R.id.CATALOG_CATEGORY_LEVEL_TAG, i);
            // textView.setBackgroundColor(R.color.alert_no_button);
//            textView.setBackgroundColor(context.getResources().getColor(R.color.colorDarkGrey));
            textView.setBackground(context.getResources().getDrawable(R.drawable.cornersround));
            textView.setOnClickListener(onBreadcrumbItemCLick);
            textView.setClickable(true);
            if (!isFirstCategory) {
                tagsCategories.addView(arrowView, new CustomFlowLayout.LayoutParams(
                        CustomFlowLayout.LayoutParams.WRAP_CONTENT, 50));
            }
            tagsCategories.addView(textView, new CustomFlowLayout.LayoutParams(
                    CustomFlowLayout.LayoutParams.WRAP_CONTENT, CustomFlowLayout.LayoutParams.WRAP_CONTENT));
            if (breadcrumbCount > 1) {
                txtCategoriesCount.setVisibility(View.VISIBLE);
                txtCategoriesCount.setText("+" + breadcrumbCount);
            } else {
                txtCategoriesCount.setVisibility(View.GONE);
            }

        }

    }

    public void removeItemFromBreadcrumbListByLevel(int level) {

        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {

            breadcrumbItemsList.remove(level);

            discussionForumModelList = getDiscussionForumListCategories(breadcrumbItemsList);
            discussionFourmAdapter.refreshList(discussionForumModelList);
        }

        if (breadcrumbItemsList != null && breadcrumbItemsList.size() == 0) {

            clearCategory();


        }


    }

}