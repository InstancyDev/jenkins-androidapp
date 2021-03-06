package com.instancy.instancylearning.discussionfourms;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
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
import android.text.Spanned;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
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
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;

import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.StaticValues.GLOBAL_SEARCH;
import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.showToast;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class DiscussionFourm_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    String TAG = DiscussionFourm_fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.discussionfourmlist)
    ListView discussionFourmlistView;
    DiscussionFourmAdapter discussionFourmAdapter;
    List<DiscussionForumModel> discussionForumModelList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    ResultListner resultListner = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    @Nullable
    @BindView(R.id.fab_fourm_button)
    FloatingActionButton floatingActionButton;

    boolean isFromNotification = false, isFromGlobalSearch = false;

    String contentIDFromNotification = "", queryString = "";
    String topicID = "";

    boolean isPrivilageForDiscussion = false;

//    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
//    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
//    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    public DiscussionFourm_fragment() {


    }
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,getActivity());

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

            isFromNotification = bundle.getBoolean("ISFROMNOTIFICATIONS");
            isFromGlobalSearch = bundle.getBoolean("ISFROMGLOBAL", false);
            if (isFromNotification) {

                contentIDFromNotification = bundle.getString("TOPICID");
                topicID = bundle.getString("CONTENTID");
            }

            if (isFromGlobalSearch) {
                queryString = bundle.getString("query");

            }

        }

        isPrivilageForDiscussion = db.isPrivilegeExistsFor(434);

    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }

        vollyService.getJsonObjResponseVolley("FOURMSLIST", appUserModel.getWebAPIUrl() + "/MobileLMS/GetForums?SiteID=" + appUserModel.getSiteIDValue() + "&SearchText=" + queryString, appUserModel.getAuthHeaders());
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("FOURMSLIST")) {
                    if (response != null) {
                        try {
                            if (isFromGlobalSearch) {
                                discussionForumModelList = createDiscussionFourmList(response);
                                discussionFourmAdapter.refreshList(discussionForumModelList);
                            } else {
                                db.injectDiscussionFourmList(response);
                                injectFromDbtoModel();
                            }

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
        View rootView = inflater.inflate(R.layout.discussionfourm_fragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);

        discussionFourmAdapter = new DiscussionFourmAdapter(getActivity(), BIND_ABOVE_CLIENT, discussionForumModelList);
        discussionFourmlistView.setAdapter(discussionFourmAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
        discussionFourmlistView.setEmptyView(rootView.findViewById(R.id.nodata_label));

        discussionForumModelList = new ArrayList<DiscussionForumModel>();
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

        if (isPrivilageForDiscussion) {
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

        return rootView;
    }


    public List<DiscussionForumModel> createDiscussionFourmList(JSONObject jsonObject) throws JSONException {

        List<DiscussionForumModel> discussionForumModelList1 = new ArrayList<>();

        JSONArray jsonTableAry = jsonObject.getJSONArray("table");
        // for deleting records in table for respective table

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            DiscussionForumModel discussionForumModel = new DiscussionForumModel();

            //active
            if (jsonMyLearningColumnObj.has("active")) {

                discussionForumModel.active = jsonMyLearningColumnObj.get("active").toString();
            }
            // attachfile
            if (jsonMyLearningColumnObj.has("attachfile")) {

                discussionForumModel.attachfile = jsonMyLearningColumnObj.get("attachfile").toString();

            }
            // author
            if (jsonMyLearningColumnObj.has("author")) {

                discussionForumModel.author = jsonMyLearningColumnObj.get("author").toString();

            }
            // createduserid
            if (jsonMyLearningColumnObj.has("createduserid")) {

                discussionForumModel.createduserid = jsonMyLearningColumnObj.get("createduserid").toString();

            }
            // createnewtopic

            if (jsonMyLearningColumnObj.has("createnewtopic")) {

                discussionForumModel.createnewtopic = jsonMyLearningColumnObj.get("createnewtopic").toString();

            }

            // description
            if (jsonMyLearningColumnObj.has("description")) {

                Spanned result = fromHtml(jsonMyLearningColumnObj.get("description").toString());

                discussionForumModel.descriptionValue = result.toString();

            }

            if (jsonMyLearningColumnObj.has("displayorder")) {
                discussionForumModel.displayorder = jsonMyLearningColumnObj.get("displayorder").toString();

            }

            // existing
            if (jsonMyLearningColumnObj.has("existing")) {

                discussionForumModel.existing = jsonMyLearningColumnObj.get("existing").toString();

            }
            // forumname
            if (jsonMyLearningColumnObj.has("forumname")) {


                Spanned result = fromHtml(jsonMyLearningColumnObj.get("forumname").toString());

                discussionForumModel.forumname = result.toString();

            }

            // isprivate
            if (jsonMyLearningColumnObj.has("isprivate")) {

                discussionForumModel.isprivate = jsonMyLearningColumnObj.get("isprivate").toString();

            }
            // likeposts
            if (jsonMyLearningColumnObj.has("likeposts")) {

                discussionForumModel.likeposts = jsonMyLearningColumnObj.get("likeposts").toString();

            }
            // moderation
            if (jsonMyLearningColumnObj.has("moderation")) {

                discussionForumModel.moderation = jsonMyLearningColumnObj.get("moderation").toString();

            }
            // name
            if (jsonMyLearningColumnObj.has("name")) {

                discussionForumModel.name = jsonMyLearningColumnObj.get("name").toString();
            }
            // nooftopics
            if (jsonMyLearningColumnObj.has("nooftopics")) {

                discussionForumModel.nooftopics = jsonMyLearningColumnObj.get("nooftopics").toString();

            }
            // parentforumid
            if (jsonMyLearningColumnObj.has("parentforumid")) {

                discussionForumModel.parentforumid = jsonMyLearningColumnObj.get("parentforumid").toString();

            }
            // requiressubscription
            if (jsonMyLearningColumnObj.has("requiressubscription")) {

                discussionForumModel.requiressubscription = jsonMyLearningColumnObj.get("requiressubscription").toString();

            }
            // sendemail
            if (jsonMyLearningColumnObj.has("sendemail")) {

                discussionForumModel.sendemail = jsonMyLearningColumnObj.get("sendemail").toString();

            }
            // siteid
            if (jsonMyLearningColumnObj.has("siteid")) {

                discussionForumModel.siteid = jsonMyLearningColumnObj.get("siteid").toString();

            }
            // totalposts
            if (jsonMyLearningColumnObj.has("totalposts")) {

                discussionForumModel.totalposts = jsonMyLearningColumnObj.get("totalposts").toString();

            }

            // forumid
            if (jsonMyLearningColumnObj.has("forumid")) {

                int fourmID = Integer.parseInt(jsonMyLearningColumnObj.get("forumid").toString());

                discussionForumModel.forumid = fourmID;

            }

            // publishedDate
            if (jsonMyLearningColumnObj.has("createddate")) {

                String formattedDate = formatDate(jsonMyLearningColumnObj.get("createddate").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                Log.d(TAG, "injectEventCatalog: " + formattedDate);
                discussionForumModel.createddate = formattedDate;

            }

            discussionForumModelList1.add(discussionForumModel);
        }
        return discussionForumModelList1;
    }


    public void injectFromDbtoModel() {
        discussionForumModelList = db.fetchDiscussionModel(appUserModel.getSiteIDValue());
        if (discussionForumModelList != null) {
            discussionFourmAdapter.refreshList(discussionForumModelList);
        } else {
            discussionForumModelList = new ArrayList<DiscussionForumModel>();
            discussionFourmAdapter.refreshList(discussionForumModelList);
        }

        if (discussionForumModelList.size() > 5) {
            if (item_search != null)
                item_search.setVisible(true);
        } else {
            if (item_search != null)
                item_search.setVisible(false);
        }
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
            if (discussionForumModelList.get(k).forumid == contentIntID) {
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
            queryString="";
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

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, DiscussionForumModel discussionForumModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.discussonforum, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_editforumoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_deleteforumoption));;
        menu.getItem(0).setVisible(true);//view

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId()==R.id.ctx_edit) {
                    Intent intentDetail = new Intent(context, CreateNewForumActivity.class);
                    intentDetail.putExtra("isfromedit", true);
                    intentDetail.putExtra("forumModel", discussionForumModelList.get(position));
                    startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

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

    }

    public void attachFragment(DiscussionForumModel forumModel, boolean isFromNotification) {
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

}