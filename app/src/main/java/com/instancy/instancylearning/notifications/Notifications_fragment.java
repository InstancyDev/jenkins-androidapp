package com.instancy.instancylearning.notifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
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
import android.widget.LinearLayout;
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
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourms.CreateNewForumActivity;
import com.instancy.instancylearning.discussionfourms.DiscussionFourmAdapter;
import com.instancy.instancylearning.discussionfourms.DiscussionTopicActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.NotificationEnumModel;
import com.instancy.instancylearning.models.NotificationModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

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
import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_NOTIFICATIONS;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;

import static com.instancy.instancylearning.utils.StaticValues.NOTIFICATIONVIWED;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class Notifications_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, View.OnClickListener {

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

    @BindView(R.id.noDataLabel)
    TextView noDataLabel;

    @BindView(R.id.txtMarkAllBtn)
    TextView txtMarkAllBtn;

    @BindView(R.id.txtClearAll)
    TextView txtClearAll;

    @BindView(R.id.lytTopBtns)
    LinearLayout lytTopBtns;

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
        }
    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }
        vollyService.getJsonObjResponseVolley("NOTIFICATIODATA", appUserModel.getWebAPIUrl() + "/MobileLMS/GetMobileNotifications?userid=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "", appUserModel.getAuthHeaders());

    }

    public void markAllAsReadApiCall() {

        vollyService.getStringResponseVolley("REMOVENOTIFICATIONCOUNT", appUserModel.getWebAPIUrl() + "/UserNotifications/Removenotificationcount?UserID=" + appUserModel.getUserIDValue(), appUserModel.getAuthHeaders());

    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("NOTIFICATIODATA")) {
                    if (response != null) {
                        try {
                            db.injectNotifications(response);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        noDataLabel.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
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
                if (requestType.equalsIgnoreCase("REMOVENOTIFICATIONCOUNT")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess:REMOVENOTIFICATIONCOUNT " + response);
                        txtMarkAllBtn.setVisibility(View.GONE);
                        refreshCatalog(true);
                    }
                }

                if (requestType.equalsIgnoreCase("CLEARALL")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess:CLEARALL  " + response);
                        txtMarkAllBtn.setVisibility(View.GONE);
                        txtClearAll.setVisibility(View.GONE);
                        refreshCatalog(true);
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
        discussionFourmlistView.setEmptyView(rootView.findViewById(R.id.noDataLabel));
        notificationModelList = new ArrayList<NotificationModel>();

//        discussionFourmlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                // TODO Auto-generated method stub
//
//                Toast.makeText(context, "Item Deleted", Toast.LENGTH_LONG).show();
//
//                return true;
//            }
//
//        });

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
        txtMarkAllBtn.setOnClickListener(this);
        txtClearAll.setOnClickListener(this);
//        txtMarkAllBtn.setBackground(getShapeDrawable());
//        txtMarkAllBtn.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.app_esperanza))) {
            txtMarkAllBtn.setVisibility(View.GONE);
        }
        txtMarkAllBtn.setVisibility(View.GONE);
        return rootView;
    }

    public ShapeDrawable getShapeDrawable() {

        ShapeDrawable sd = new ShapeDrawable();

        // Specify the shape of ShapeDrawable
        sd.setShape(new RectShape());

        // Specify the border color of shape
        sd.getPaint().setColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        // Set the border width
        sd.getPaint().setStrokeWidth(10f);

        // Specify the style is a Stroke
        sd.getPaint().setStyle(Paint.Style.STROKE);

        // Finally, add the drawable background to TextView

        return sd;
    }


    public void injectFromDbtoModel() {
        notificationModelList = db.fetchNotificationModel(appUserModel.getSiteIDValue());
        if (notificationModelList != null) {
            notificationAdapter.refreshList(notificationModelList);
            lytTopBtns.setVisibility(View.VISIBLE);
        } else {
            notificationModelList = new ArrayList<NotificationModel>();
            notificationAdapter.refreshList(notificationModelList);
            noDataLabel.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
            lytTopBtns.setVisibility(View.GONE);
        }

        boolean isCompleted = isALlNotificationsAreSeen();

        if (isCompleted) {
            txtMarkAllBtn.setVisibility(View.GONE);
        } else {
            txtMarkAllBtn.setVisibility(View.VISIBLE);
        }

        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.app_esperanza))) {
            lytTopBtns.setVisibility(View.GONE);
        }

//        if (item_search != null && notificationModelList.size() > 5) {
//            item_search.setVisible(true);
//        } else {
//            item_search.setVisible(false);
//        }

    }


    public boolean isALlNotificationsAreSeen() {

        boolean isCompleted = true;

        if (notificationModelList != null && notificationModelList.size() > 0) {

            for (int j = 0; j < notificationModelList.size(); j++) {

                if (notificationModelList.get(j).markasread.toLowerCase().contains("false")) {
                    isCompleted = false;
                    break;
                }

            }

        }

        return isCompleted;
    }


    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + getLocalizationValue(JsonLocalekeys.sidemenu_button_notificationbutton) + "</font>"));
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
        MenuItem itemWaitlist = menu.findItem(R.id.mylearning_addwaitlist);
        itemInfo.setVisible(false);
        item_filter.setVisible(false);
        itemWaitlist.setVisible(false);

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

                    notificationAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    if (notificationAdapter.getCount() < 1) {
                        noDataLabel.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));

                    } else {
                        noDataLabel.setText("");
                    }

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
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.card_view:
                markAsReadWebCall(notificationModelList.get(position), position);
                break;
            case R.id.txtDelete:
                if (isNetworkConnectionAvailable(getContext(), -1)) {
                    deleteAlert(notificationModelList.get(position));
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

    }

    public void markAsReadWebCall(NotificationModel notificationModel, int position) {

//        requiredFunctionalityForTheSelectedCell(notificationModel);

        markAsReadNotification(notificationModel, position);
        notificationModelList.get(position).markasread = "true";
        notificationAdapter.notifyDataSetChanged();
    }

    //
    public void requiredFunctionalityForTheSelectedCell(NotificationModel notificationModel) {

        NotificationEnumModel notificationEnumModel = new NotificationEnumModel();

        if (notificationModel.notificationid.equalsIgnoreCase(notificationEnumModel.General) && notificationModel.contentid.equalsIgnoreCase("null")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(notificationModel.message).setTitle(notificationModel.notificationtitle)
                    .setCancelable(false)
                    .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.dismiss();

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            refreshCatalog(true);
        } else if (notificationModel.notificationid.equalsIgnoreCase(notificationEnumModel.General) && notificationModel.contentid.length() > 4) {

            boolean myLearningExists = myLearningAction(notificationModel.contentid);
            boolean myCatalogExists = false;
            if (!myLearningExists)
                myCatalogExists = myCatalogAction(notificationModel.contentid);

            if (myLearningExists) {
                ((SideMenu) getActivity()).homeControllClicked(true, 1, notificationModel.contentid, false, "");
            } else if (myCatalogExists) {
                ((SideMenu) getActivity()).homeControllClicked(true, 2, notificationModel.contentid, false, "");
            } else {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(notificationModel.message).setTitle(notificationModel.notificationtitle)
                        .setCancelable(false)
                        .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                refreshCatalog(true);
            }
        } else if (notificationModel.notificationid.equalsIgnoreCase(notificationEnumModel.ForumCommentNotification) && notificationModel.contentid.length() > 4) {

            ((SideMenu) getActivity()).homeControllClicked(true, 4, "", false, "");
        } else if (notificationModel.notificationid.equalsIgnoreCase(notificationEnumModel.NewConnectionRequest) && notificationModel.contentid.length() == 4) {

            ((SideMenu) getActivity()).homeControllClicked(true, 10, "", false, "");

        } else if (notificationModel.notificationid.equalsIgnoreCase(notificationEnumModel.AssignedToMyCatalog) && notificationModel.contentid.length() > 4) {


            boolean myLearningExists = myLearningAction(notificationModel.contentid);
            boolean myCatalogExists = false;
            if (!myLearningExists)
                myCatalogExists = myCatalogAction(notificationModel.contentid);

            if (myLearningExists) {
                ((SideMenu) getActivity()).homeControllClicked(true, 1, notificationModel.contentid, false, "");
            } else if (myCatalogExists) {
                ((SideMenu) getActivity()).homeControllClicked(true, 2, notificationModel.contentid, false, "");
            } else {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(notificationModel.notificationsubject).setTitle(notificationModel.notificationtitle)
                        .setCancelable(false)
                        .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                refreshCatalog(true);
            }
//            ((SideMenu) getActivity()).homeControllClicked(true, 1, notificationModel.contentid, false, "");

        } else {

            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.notification_alert_subtitlerespectivecontentnotavailablealert), Toast.LENGTH_SHORT).show();

        }

    }

    public boolean myLearningAction(String contentID) {
        boolean isMyLearningContetExists = false;
        boolean isMyLearningMenuExit = ((SideMenu) getActivity()).respectiveMenuExistsOrNot("1");
        if (isMyLearningMenuExit) {
            isMyLearningContetExists = db.isContentIDExistsInMyLearning(contentID);
        }
        return isMyLearningContetExists;
    }

    public boolean myCatalogAction(String contentID) {
        boolean isCatalogContentExists = false;
        boolean isCatalogMenuExit = ((SideMenu) getActivity()).respectiveMenuExistsOrNot("2");
        if (isCatalogMenuExit) {
            isCatalogContentExists = db.isContentIDExistsInCatalog(contentID);
        }
        return isCatalogContentExists;
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

    public void deleteAlert(final NotificationModel notificationModel) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false).setTitle(getLocalizationValue(JsonLocalekeys.profile_alerttitle_stringconfirmation)).setMessage(getLocalizationValue(JsonLocalekeys.notifications_label_suredeletenotificationalert))
                .setPositiveButton(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_deletetopicoption), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        deleteAnswerFromServer(notificationModel);
                    }
                }).setNegativeButton(getLocalizationValue(JsonLocalekeys.discussionforum_alertbutton_cancelbutton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialog.create();
        alertDialogAndroid.show();

    }

    public void deleteAnswerFromServer(final NotificationModel notificationModel) {

//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteUserNotification?userNotificationId=" + notificationModel.usernotificationid;

        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
//                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("true")) {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.notifications_label_successnotificationalert) + " \n" + getLocalizationValue(JsonLocalekeys.notifications_label_successfullydeletenotificationalert), Toast.LENGTH_SHORT).show();
                    NOTIFICATIONVIWED = 1;
                    deleteAnswerFromLocalDB(notificationModel);

                } else {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.notifications_label_deletenotificationalertcontactadmin), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
//                svProgressHUD.dismiss();
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

    public void deleteAnswerFromLocalDB(NotificationModel notificationModel) {

        try {
            String strDelete = "DELETE FROM " + TBL_NOTIFICATIONS + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND usernotificationid   ='" + notificationModel.usernotificationid + "'";
            db.executeQuery(strDelete);

            injectFromDbtoModel();

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    public void markAsReadNotification(final NotificationModel notificationModel, final int position) {

//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);7013465807

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/UpdateNotificationMarkAsRead?NotificationId=" + notificationModel.notificationid + "&userID=" + appUserModel.getUserIDValue() + "&userNotificationId=" + notificationModel.usernotificationid;
        Log.d(TAG, "markAsReadNotificationAPI: " + urlString);
        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
//                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("true")) {

//                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.notifications_label_successnotificationalert) + " \n" + getLocalizationValue(JsonLocalekeys.notifications_label_successfullydeletenotificationalert), Toast.LENGTH_SHORT).show();
                    notificationModelList.get(position).markasread = "true";
                    notificationAdapter.notifyDataSetChanged();
                    requiredFunctionalityForTheSelectedCell(notificationModel);

//                    deleteAnswerFromLocalDB(notificationModel);
                } else {
                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.notifications_label_deletenotificationalertcontactadmin), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
//                svProgressHUD.dismiss();
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

    public void clearAllNotificationApiCall() {
        String urlStr = appUserModel.getWebAPIUrl() + "/UserNotifications/clearnotification";
        JSONObject parameters = new JSONObject();

        try {

            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("UserNotificationID", "-1");
            parameters.put("GameID", -1);
            parameters.put("fromAchievement", true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "CLEARALL", urlStr);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.txtMarkAllBtn:
                markAllAsReadApiCall();
                break;
            case R.id.txtClearAll:
                clearAllNotificationApiCall();
                break;

        }
    }

}