package com.instancy.instancylearning.notifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
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
import com.instancy.instancylearning.discussionfourms.CreateNewForumActivity;
import com.instancy.instancylearning.discussionfourms.DiscussionFourmAdapter;
import com.instancy.instancylearning.discussionfourms.DiscussionTopicActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.NotificationModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

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
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class Notifications_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    String TAG = Notifications_fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.notificationlist)
    ListView discussionFourmlistView;

    NotificationAdapter notificationAdapter;

    List<NotificationModel> notificationModelList = null;

    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    ResultListner resultListner = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    public Notifications_fragment() {


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
        }
    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }
        vollyService.getJsonObjResponseVolley("NOTIFICATIODATA", appUserModel.getWebAPIUrl() + "/MobileLMS/GetMobileNotifications?userid=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&Locale=en-us", appUserModel.getAuthHeaders());

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("NOTIFICATIODATA")) {
                    if (response != null) {
//                        try {
//                            db.injectNotifications(response);
//                            injectFromDbtoModel();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
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
        View rootView = inflater.inflate(R.layout.notification_fragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);

        notificationAdapter = new NotificationAdapter(getActivity(), BIND_ABOVE_CLIENT, notificationModelList);
        discussionFourmlistView.setAdapter(notificationAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
        discussionFourmlistView.setEmptyView(rootView.findViewById(R.id.nodata_label));

        notificationModelList = new ArrayList<NotificationModel>();

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
//        discussionForumModelList = db.fetchDiscussionModel(appUserModel.getSiteIDValue());
//        if (discussionForumModelList != null) {
//            discussionFourmAdapter.refreshList(discussionForumModelList);
//        } else {
//            discussionForumModelList = new ArrayList<DiscussionForumModel>();
//            discussionFourmAdapter.refreshList(discussionForumModelList);
//        }

    }

    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + "Notifications" + "</font>"));

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

                    notificationAdapter.filter(newText.toLowerCase(Locale.getDefault()));

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
            case R.id.mylearning_filter:
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
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.card_view:
                break;

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

}