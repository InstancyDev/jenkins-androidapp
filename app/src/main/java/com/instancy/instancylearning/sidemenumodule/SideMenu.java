package com.instancy.instancylearning.sidemenumodule;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.adapters.MenuDrawerDynamicAdapter;
import com.instancy.instancylearning.askexpert.AskExpertFragment;
import com.instancy.instancylearning.catalog.CatalogCategories_Fragment;
import com.instancy.instancylearning.catalog.Catalog_fragment;
import com.instancy.instancylearning.chatmessanger.SendMessage_fragment;
import com.instancy.instancylearning.chatmessanger.SignalAService;
import com.instancy.instancylearning.chatmessanger.SignalAServiceMicrosoft;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionFourm_fragment;
import com.instancy.instancylearning.events.Event_fragment_new;
import com.instancy.instancylearning.gameficitation.LeaderboardFragment;
import com.instancy.instancylearning.gameficitation.MyAchivementsFragment;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.NetworkChangeReceiver;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.home.HomeCategories_Fragment;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.learningcommunities.LearningCommunities_fragment;
import com.instancy.instancylearning.mainactivities.Login_activity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.ProfileDetailsModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mycompetency.MyCompetencyFragment;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
import com.instancy.instancylearning.myskills.MySkillFragment;
import com.instancy.instancylearning.notifications.Notifications_fragment;
import com.instancy.instancylearning.peoplelisting.PeopleListing_fragment;
import com.instancy.instancylearning.profile.Profile_fragment;
import com.instancy.instancylearning.progressreports.ProgressReportfragment;
import com.instancy.instancylearning.settings.SettingsInnerFragment;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.webpage.Webpage_fragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.getToolbarLogoIcon;
import static com.instancy.instancylearning.utils.StaticValues.BACKTOMAINSITE;
import static com.instancy.instancylearning.utils.StaticValues.CATALOG_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.ISPROFILENAMEORIMAGEUPDATED;
import static com.instancy.instancylearning.utils.StaticValues.IS_MENUS_FIRST_TIME;
import static com.instancy.instancylearning.utils.StaticValues.MAIN_MENU_POSITION;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.NOTIFICATIONVIWED;
import static com.instancy.instancylearning.utils.StaticValues.PROFILE_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.SIDEMENUOPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.SUB_MENU_POSITION;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;

public class SideMenu extends AppCompatActivity implements View.OnClickListener, DrawerLayout.DrawerListener {

    public String TAG = SideMenu.class.getSimpleName();

    private List<SideMenusModel> sideMenumodelList;
    DatabaseHandler db;

    @BindView(R.id.expanded_menu_drawer)
    ExpandableListView navDrawerExpandableView;

    @BindView(R.id.profile_thumbs)
    ImageView profileImage;


    @BindView(R.id.bottom_logo)
    ImageView bottomLogo;


    @BindView(R.id.id_username)
    TextView txtUsername;

    @BindView(R.id.id_user_address)
    TextView txtAddress;

    @BindView(R.id.txtbtn_settings)
    TextView txtBtnSettings;

    @BindView(R.id.txtbtn_sendmessage)
    TextView txtBtnsendMessage;

    @BindView(R.id.txtbtn_back)
    TextView textBtnBack;

    @BindView(R.id.back_layout)
    RelativeLayout backLayout;

    @BindView(R.id.settings_layout)
    RelativeLayout settingsLayout;

    @BindView(R.id.sendmessage_layout)
    RelativeLayout sendMessageLayout;

    @BindView(R.id.notification_layout)
    RelativeLayout notificationLayout;

    @BindView(R.id.subsitelayout)
    LinearLayout subsiteLayout;

    @BindView(R.id.back_font)
    TextView fontBack;

    @BindView(R.id.sendmessage_font)
    TextView sendMessagFont;

    @BindView(R.id.settings_font)
    TextView fontSettings;

    @BindView(R.id.notification_font)
    TextView fontNotification;

    @BindView(R.id.txtbtn_notification)
    TextView txtBtnNotification;

    @BindView(R.id.subsitename)
    TextView subsiteName;

    private BroadcastReceiver mNetworkReceiver;

    VollyService vollyService;
    IResult resultCallback = null;

    private static int lastClicked = 0;

    AppUserModel appUserModel;
    MenuDrawerDynamicAdapter menuDynamicAdapter;
    PreferencesManager preferencesManager;
    //    protected List<SideMenusModel> mainMenuList = null;
    protected List<SideMenusModel> subMenuList = null;

    HashMap<Integer, List<SideMenusModel>> hmSubMenuList = null;
    public DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    UiSettingsModel uiSettingsModel;
    SignalAService signalAService;
    Communicator communicator;
    SignalAServiceMicrosoft signalAServiceMicrosoft;
    public Toolbar toolbar;

    SideMenusModel homeModel, tempHomeModel;
    int homeIndex = 0;

    boolean isFromPushNotification = false;

    RelativeLayout drawerHeaderView;
    public View logoView;

    //    private SignalRService mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();
        db = new DatabaseHandler(this);
        appUserModel = AppUserModel.getInstance();

        initVolleyCallback();

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
        vollyService = new VollyService(resultCallback, this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            isFromPushNotification = bundle.getBoolean("PUSH");

        }

        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERNAME));
        appUserModel.setProfileImage(preferencesManager.getStringValue(StaticValues.KEY_USERPROFILEIMAGE));
        appUserModel.setUserLoginId(preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID));
        ButterKnife.bind(this);
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        uiSettingsModel = UiSettingsModel.getInstance();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getAppHeaderTextColor() + "'>My Learning</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.back_font), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.settings_font), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.notification_font), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.sendmessage_font), iconFont);

        Drawable bd = getDrawableFromStringHOmeMethod(R.string.fa_icon_home, this, uiSettingsModel.getAppHeaderTextColor());
        toolbar.setLogo(bd);
        toolbar.setContentInsetStartWithNavigation(0);
        logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
                homeControllClicked(false, 0, "", false, "");


            }
        });

        if (isNetworkConnectionAvailable(this, -1)) {
            refreshNotification();
            NOTIFICATIONVIWED = 0;
        } else {

        }

        backLayout.setOnClickListener(this);
        sendMessageLayout.setOnClickListener(this);
        notificationLayout.setOnClickListener(this);
        notificationLayout.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.VISIBLE);
        settingsLayout.setOnClickListener(this);
        updateBottomButtonColor();


        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.crop_life))) {
            logoView.setVisibility(View.GONE);
        } else if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.cvcta)) || getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.healthhelp)) || getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.enterprisedemo))) {
            notificationLayout.setVisibility(View.VISIBLE);
            sendMessageLayout.setVisibility(View.VISIBLE);
//            signalAService = SignalAService.newInstance(this);
//            signalAService.startSignalA();
        }
        Log.d(TAG, "onCreate: appname " + getResources().getString(R.string.app_name));

        getSupportActionBar().setDisplayShowCustomEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        navDrawerExpandableView.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(this);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor(uiSettingsModel.getHeaderTextColor()));
        toggle.syncState();

        drawerHeaderView = (RelativeLayout) findViewById(R.id.drawerheaderview);
        drawerHeaderView.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuHeaderBGColor()));

        updateDisplayNameAndImage();

        sideMenumodelList = db.getNativeMainMenusData(uiSettingsModel.isEnableAzureSSOForLearner(), this);

        hmSubMenuList = new HashMap<Integer, List<SideMenusModel>>();

        int i = 0;
        for (SideMenusModel menu : sideMenumodelList) {
            int parentMenuId = menu.getMenuId();
            subMenuList = db.getNativeSubMenusData(parentMenuId);
            if (subMenuList != null && subMenuList.size() > 0) {
                hmSubMenuList.put(parentMenuId, subMenuList);

            }

            if (menu.getContextMenuId().equals("1")) {
                if (IS_MENUS_FIRST_TIME) {
                    IS_MENUS_FIRST_TIME = false;
                }
            }

            if (menu.getContextMenuId().equals("10")) {

                if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.mciswitchinstitute))) {
                    sendMessageLayout.setVisibility(View.GONE);
                } else {
                    sendMessageLayout.setVisibility(View.VISIBLE);

                }


//                startSignalService();

//                signalAServiceMicrosoft = SignalAServiceMicrosoft.newInstance(this);
//                signalAServiceMicrosoft.startSignalA();

//                Intent intent = new Intent();
//                intent.setClass(this, SignalRService.class);
//                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
            i++;
        }

        if (sideMenumodelList != null) {
            menuDynamicAdapter = new MenuDrawerDynamicAdapter(
                    getApplicationContext(), hmSubMenuList, sideMenumodelList);

            SideMenusModel model = new SideMenusModel();
            if (savedInstanceState == null) {
                String indexed = "0";
                for (int j = 0; j < sideMenumodelList.size(); j++) {

                    if (sideMenumodelList.get(j).getDisplayOrder() == 1) {
                        indexed = sideMenumodelList.get(j).getContextMenuId();
                        model = sideMenumodelList.get(j);

                    } else {

                        indexed = sideMenumodelList.get(0).getContextMenuId();
                        model = sideMenumodelList.get(0);

                    }

                }

                // on first time to display view for first time navigation item based on the number
                homeModel = model;
                tempHomeModel = model;
                homeIndex = Integer.parseInt(indexed);
                lastClicked = 0;

            }
            navDrawerExpandableView.setAdapter(menuDynamicAdapter);
            navDrawerExpandableView.setOnGroupExpandListener((new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    int len = menuDynamicAdapter.getGroupCount();

                    for (int i = 0; i < len; i++) {
                        if (i != groupPosition) {
                            navDrawerExpandableView.collapseGroup(i);
                        }
                    }
                }

            }));

            navDrawerExpandableView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                    Toast.makeText(SideMenu.this, "Here groupPosition " + groupPosition, Toast.LENGTH_SHORT).show();

                    int logoutPos = sideMenumodelList.size() - 1;


//                    String filterCondition = sideMenumodelList.get(groupPosition).getConditions();
//                    if (logoutPos == groupPosition && isSubSiteEntered.equalsIgnoreCase("false")) {
                    if (logoutPos == groupPosition && sideMenumodelList.get(groupPosition).menuId == 5555) {

                        Intent intent = new Intent(SideMenu.this, Login_activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        preferencesManager.setStringValue("", StaticValues.KEY_USERLOGINID);
                        preferencesManager.setStringValue("", StaticValues.KEY_USERPASSWORD);
                        startActivity(intent);
                        finish();
                        preferencesManager.setStringValue("", StaticValues.KEY_USERID);
                    } else {
                        if (lastClicked != groupPosition) {

                            if (sideMenumodelList != null && hmSubMenuList != null) {
                                if (hmSubMenuList.containsKey(sideMenumodelList.get(groupPosition).getMenuId())) {
                                    return false;
                                } else {

                                    try {
                                        selectItem(Integer.parseInt(sideMenumodelList.get(groupPosition).getContextMenuId()), sideMenumodelList.get(groupPosition), isFromPushNotification, "", "");

                                    } catch (NumberFormatException numEx) {
                                        numEx.printStackTrace();
                                        selectItem(1, sideMenumodelList.get(groupPosition), isFromPushNotification, "", "");
                                    }
                                }
                            }
                        }
                    }
                    drawer.closeDrawer(Gravity.LEFT);
                    lastClicked = groupPosition;

                    return false;
                }


            });


            navDrawerExpandableView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    subMenuItemClickListener(groupPosition, childPosition);
                    return true;
                }

            });

            navDrawerExpandableView.expandGroup(lastClicked);
        }

        if (bundle != null) {
            JSONObject pushObj = new JSONObject();
            isFromPushNotification = bundle.getBoolean("PUSH");

            if (isFromPushNotification) {
                try {

                    pushObj = new JSONObject(bundle.getString(StaticValues.FCM_OBJECT));
                    triggerPushNotification(pushObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }


        if (!isFromPushNotification) {
            selectItem(homeIndex, homeModel, false, "", ""); // 2 is your fragment's number for "CollectionFragment"
        }


    }

    public void startSignalService() {

        communicator = new Communicator() {
            @Override
            public void messageRecieved(JSONArray messageReceived) {

            }

            @Override
            public void userOnline(int typeUpdate,JSONArray objReceived) {
                Log.d(TAG, "messageRecieved: " + objReceived);
            }
        };

        signalAService = SignalAService.newInstance(this);
        signalAService.communicator = communicator;
        signalAService.startSignalA();
    }

    public void updateDisplayNameAndImage() {

        ProfileDetailsModel profileDetailsModel = new ProfileDetailsModel();

        profileDetailsModel = db.fetchProfileDetails(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        String[] strAry = new String[2];

        strAry = extractProfileNameAndLocation(profileDetailsModel);

//        String profileIma = appUserModel.getSiteURL() + "//Content/SiteFiles/" + appUserModel.getSiteIDValue() + "/ProfileImages/" + appUserModel.getProfileImage();

//        String profileIma = appUserModel.getSiteURL() + "//Content/SiteFiles/374/ProfileImages/" + appUserModel.getProfileImage();

        String profileIma = appUserModel.getSiteURL() + "/Content/SiteFiles/374/ProfileImages/" + profileDetailsModel.profileimagepath;

        Picasso.with(this).load(profileIma).placeholder(R.drawable.defaultavatar).into(profileImage);
        String name = strAry[0];

//        if (name.contains("Anonymous") || name.contains("null")) {
//
//            name = preferencesManager.getStringValue(StaticValues.KEY_USERNAME);
//        }

        txtUsername.setText(upperCaseWords(name));
        txtUsername.setTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));

        txtAddress.setText(upperCaseWords(strAry[1]));
        txtAddress.setTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));


        if (isValidString(uiSettingsModel.getNativeAppLoginLogo())) {
//            Picasso.with(this).load(uiSettingsModel.getNativeAppLoginLogo()).placeholder(R.drawable.younextyoubanner).into(bottomLogo);
//            bottomLogo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppLoginBGColor())));
        }

    }


    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void homeControllClicked(boolean isFromNotification, int menuId, String contentID, boolean isFromCommunityList, String fourmID) {

        String isSubSiteEntered = preferencesManager.getStringValue(StaticValues.SUB_SITE_ENTERED);
        if (isSubSiteEntered.equalsIgnoreCase("true")) {
            isFromCommunityList = true;
        } else {
            isFromCommunityList = false;
        }

        if (isFromCommunityList) {

            SideMenusModel model = getMenuModelForSubsites();

            int menuID = 0;
            try {
                homeModel = model;
                menuID = Integer.parseInt(model.getContextMenuId());
            } catch (NumberFormatException excepetion) {
                excepetion.printStackTrace();
                menuID = 1;

            }

            selectItem(menuID, model, false, contentID, fourmID);

        } else if (!isFromNotification) {
            selectItem(homeIndex, homeModel, false, contentID, fourmID);
        } else {
            selectItem(menuId, getMenuModelForNotification(menuId), true, contentID, fourmID);

        }

        navDrawerExpandableView.expandGroup(0);
        lastClicked = 0;
    }

    public SideMenusModel getMenuModelForNotification(int menuId) {
        SideMenusModel sideMenusModel = new SideMenusModel();

        for (int i = 0; i < sideMenumodelList.size(); i++) {

            if (sideMenumodelList.get(i).getContextMenuId().equalsIgnoreCase("" + menuId)) {
                sideMenusModel = sideMenumodelList.get(i);
            }

        }

        return sideMenusModel;
    }

    public void selectItem(int menuid, SideMenusModel sideMenusModel, boolean isFromNotification, String contentID, String topicID) {

        Fragment fragment = null; //

        SideMenusModel catalogSideMenuModel = sideMenusModel;

        switch (menuid) {
            case 1:
                fragment = new MyLearningFragment();
                break;
            case 2:
                if (sideMenusModel.getLandingPageType().equalsIgnoreCase("1")) {
                    fragment = new CatalogCategories_Fragment();
                } else {
                    fragment = new Catalog_fragment();
                }
                break;
            case 3:
                fragment = new Profile_fragment();
                break;
            case 7:
                fragment = new Webpage_fragment();
                break;
            case 6:
                fragment = new HomeCategories_Fragment();
                break;
            case 8:
//                fragment = new Event_fragment();
                fragment = new Event_fragment_new();
                break;
            case 4:
//                fragment = new DiscussionFourm_fragment();
                fragment = new com.instancy.instancylearning.discussionfourmsenached.DiscussionFourm_fragment();
                break;
            case 5:
                fragment = new com.instancy.instancylearning.askexpertenached.AskExpertFragment();
//                fragment = new AskExpertFragment();
                break;
            case 9:
                fragment = new LearningCommunities_fragment();
                break;
            case 10:
                fragment = new PeopleListing_fragment();
//                fragment = new MyAchivementsFragment();
                break;
            case 11:
                if (uiSettingsModel.isEnableSkillstobeMappedwithJobRoles()) {
                    fragment = new MyCompetencyFragment();
                } else {
                    fragment = new MySkillFragment();
                }
                break;
            case 12:
                fragment = new LeaderboardFragment();
                break;
            case 13:
                fragment = new MyAchivementsFragment();
                break;
            case 14:
                fragment = new ProgressReportfragment();
                break;
            case 99:
                fragment = new SendMessage_fragment();
                break;
            case 100:
                fragment = new Notifications_fragment();
                break;
            case 101:
                fragment = new SettingsInnerFragment();
                break;
            default:
                subMenuItemClickListener(0, 0);
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
            Bundle bundle = new Bundle();
            if (menuid != 99) {
                bundle.putSerializable("sidemenumodel", sideMenusModel);

                bundle.putBoolean("ISFROMCATEGORIES", false);
                bundle.putBoolean("ISFROMNOTIFICATIONS", isFromNotification);

                bundle.putString("TOPICID", topicID);
                bundle.putString("CONTENTID", contentID);
                fragment.setArguments(bundle);

                navDrawerExpandableView.setItemChecked(sideMenusModel.getDisplayOrder(), true);
                navDrawerExpandableView.setSelection(sideMenusModel.getDisplayOrder());

            }
            if (menuid == 99 || menuid == 100 || menuid == 101) {

                lastClicked = -1;
                navDrawerExpandableView.setItemChecked(lastClicked, false);
                menuDynamicAdapter.notifyDataSetChanged();
            }

            drawer.closeDrawer(Gravity.LEFT);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    public void updateBottomButtonColor() {

        backLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
        settingsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
        sendMessageLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
        notificationLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
        txtBtnNotification.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
        txtBtnSettings.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
        txtBtnsendMessage.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
        fontSettings.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
        fontBack.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
        fontNotification.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
        textBtnBack.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
        sendMessagFont.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));

        notificationLayout.setVisibility(View.VISIBLE);
        if (isValidString(uiSettingsModel.getNativeAppLoginLogo())) {
//            Picasso.with(this).load(uiSettingsModel.getNativeAppLoginLogo()).into(bottomLogo);
//            bottomLogo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppLoginBGColor())));
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                return;
            }

//            if (!doubleBackToExitPressedOnce) {
//                this.doubleBackToExitPressedOnce = true;
//                Toast.makeText(this, "      Please click BACK again to exit.      ", Toast.LENGTH_SHORT).show();

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.exitapplication))
                    .setCancelable(false).setNegativeButton(getResources().getString(R.string.exitapplication_negative), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            }).setPositiveButton(getResources().getString(R.string.exitapplication_positive), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    dialog.dismiss();
                    finish();

                }
            });
            AlertDialog alert = builder.create();
////                TextView textView = (TextView) alert.findViewById(android.R.id.title);
////                textView.setTextSize(20);
            alert.show();
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        doubleBackToExitPressedOnce = false;
//                    }
//                }, 2000);
//            } else {
//                super.onBackPressed();
//                return;
//            }
        }
    }


    private void subMenuItemClickListener(int groupPosition, int childPosition) {

        if (!(MAIN_MENU_POSITION == groupPosition && SUB_MENU_POSITION == childPosition)) {
            MAIN_MENU_POSITION = groupPosition;
            SUB_MENU_POSITION = childPosition;
            List<SideMenusModel> mList = hmSubMenuList.get(sideMenumodelList.get(MAIN_MENU_POSITION).getMenuId());
            SideMenusModel m = mList.get(childPosition);

            if (m.getIsOfflineMenu().equals("true")) {
                navDrawerExpandableView.setSelectedGroup(groupPosition);
                navDrawerExpandableView.setSelectedChild(groupPosition, childPosition, true);
                selectItem(Integer.parseInt(m.getContextMenuId()), m, false, "", "");
            } else {

            }
        }
    }

    public SideMenusModel getMenuModelForSubsites() {
        SideMenusModel sideMenusModel = new SideMenusModel();

        sideMenumodelList = new ArrayList<SideMenusModel>();

        sideMenumodelList = db.getNativeMainMenusData(uiSettingsModel.isEnableAzureSSOForLearner(), this);

        hmSubMenuList = new HashMap<Integer, List<SideMenusModel>>();

        int i = 0;
        for (SideMenusModel menu : sideMenumodelList) {
            int parentMenuId = menu.getMenuId();
            subMenuList = db.getNativeSubMenusData(parentMenuId);
            if (subMenuList != null && subMenuList.size() > 0) {
                hmSubMenuList.put(parentMenuId, subMenuList);
            }
            if (i == 0) {
                sideMenusModel = menu;
            }
            i++;
        }
        return sideMenusModel;
    }

    public void backToMainSite() {
        BACKTOMAINSITE = 2;
        NOTIFICATIONVIWED = 1;
        homeModel = tempHomeModel;
        SIDEMENUOPENED_FIRSTTIME = 0;
        CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;
        MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;
        PROFILE_FRAGMENT_OPENED_FIRSTTIME = 0;
        preferencesManager.setStringValue("false", StaticValues.SUB_SITE_ENTERED);
        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));
        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERNAME));
        appUserModel.setPassword(preferencesManager.getStringValue(StaticValues.KEY_USERPASSWORD));
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        drawerHeaderView.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuHeaderBGColor()));

        sideMenumodelList = new ArrayList<SideMenusModel>();
        sideMenumodelList = db.getNativeMainMenusData(uiSettingsModel.isEnableAzureSSOForLearner(), this);

        hmSubMenuList = new HashMap<Integer, List<SideMenusModel>>();

        int i = 0;
        for (SideMenusModel menu : sideMenumodelList) {
            int parentMenuId = menu.getMenuId();
            subMenuList = db.getNativeSubMenusData(parentMenuId);
            if (subMenuList != null && subMenuList.size() > 0) {
                hmSubMenuList.put(parentMenuId, subMenuList);
            }

            if (menu.getContextMenuId().equals("1")) {
                if (IS_MENUS_FIRST_TIME) {
                    IS_MENUS_FIRST_TIME = false;
                }
            }
            i++;
        }

        menuDynamicAdapter.refreshList(sideMenumodelList, hmSubMenuList);

        backLayout.setVisibility(View.GONE);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor(uiSettingsModel.getHeaderTextColor()));
        Drawable bd = getDrawableFromStringHOmeMethod(R.string.fa_icon_home, this, uiSettingsModel.getAppHeaderTextColor());
        toolbar.setLogo(bd);
        homeControllClicked(false, 0, "", false, "");

        updateBottomButtonColor();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    if (data != null) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }
    }

    public String[] extractProfileNameAndLocation(ProfileDetailsModel detailsModel) {

        String[] strAry = new String[2];

        String name = "";
        String location = "";

        if (!(detailsModel.displayname.equalsIgnoreCase("") || detailsModel.displayname.equalsIgnoreCase("null"))) {
            name = detailsModel.displayname;
        } else if (!detailsModel.firstname.equalsIgnoreCase("")) {
            name = detailsModel.firstname + " " + detailsModel.lastname;
        } else {
            name = "Anonymous";
        }

        if (!detailsModel.addresscity.equalsIgnoreCase("") && !detailsModel.addresscity.contains("null")) {
            if (!detailsModel.addressstate.equalsIgnoreCase("") && !detailsModel.addressstate.contains("null")) {
                location = detailsModel.addresscity + "," + detailsModel.addressstate;
            } else {
                location = detailsModel.addresscity;
            }
        } else if (!detailsModel.addressstate.equalsIgnoreCase("") && !detailsModel.addressstate.contains("null")) {
            location = detailsModel.addressstate;
        } else if (!detailsModel.addresscountry.equalsIgnoreCase("") && !detailsModel.addresscountry.contains("null")) {
            location = detailsModel.addresscountry;
        } else {
            location = "";
        }

        strAry[0] = upperCaseWords(name);
        strAry[1] = upperCaseWords(location);

        return strAry;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                backToMainSite();
                break;
            case R.id.sendmessage_layout:
                selectItem(99, sideMenumodelList.get(0), false, "", "");
                break;
            case R.id.notification_layout:
                selectItem(100, sideMenumodelList.get(0), false, "", "");
                break;
            case R.id.settings_layout:
                selectItem(101, sideMenumodelList.get(0), false, "", "");
                break;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: sidemenu");
    }


    @Override
    protected void onResume() {
        super.onResume();

//        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), null, Color.parseColor(uiSettingsModel.getMenuHeaderBGColor()));
//        (SideMenu.this).setTaskDescription(taskDescription);

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        Log.d(TAG, "onDrawerOpened: sidemenu");
        if (SIDEMENUOPENED_FIRSTTIME == 0) {
            enteredSubsiteMethods();
            NOTIFICATIONVIWED = 1;
        }

        if (isNetworkConnectionAvailable(this, -1) && NOTIFICATIONVIWED == 1) {
            refreshNotification();
            NOTIFICATIONVIWED = 0;
        } else {

        }

        if (isNetworkConnectionAvailable(this, -1) && ISPROFILENAMEORIMAGEUPDATED == 1) {
            updateDisplayNameAndImage();
            ISPROFILENAMEORIMAGEUPDATED = 0;
        } else {

        }
    }

    public void enteredSubsiteMethods() {
        String isSubSiteEntered = preferencesManager.getStringValue(StaticValues.SUB_SITE_ENTERED);
        if (isSubSiteEntered.equalsIgnoreCase("true")) {
            backLayout.setVisibility(View.VISIBLE);
            subsiteLayout.setVisibility(View.VISIBLE);
            SIDEMENUOPENED_FIRSTTIME = 1;
            BACKTOMAINSITE = 1;
            lastClicked = -1;
            TextView mainSiteName = (TextView) backLayout.findViewById(R.id.txtbtn_back);
            TextView subsiteName = (TextView) subsiteLayout.findViewById(R.id.subsitename);
            mainSiteName.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
            LinearLayout subsiteLa = (LinearLayout) subsiteLayout.findViewById(R.id.subsitelays);
            subsiteName.setText(appUserModel.getSiteName());
            mainSiteName.setText(appUserModel.getMainSiteName());
            subsiteLa.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuHeaderBGColor()));
            subsiteLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuHeaderBGColor()));
            subsiteLa.setAlpha(.7f);
//            subsiteLayout.setAlpha(.7f);

            drawerHeaderView.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuHeaderBGColor()));

            sideMenumodelList = new ArrayList<SideMenusModel>();
            sideMenumodelList = db.getNativeMainMenusData(uiSettingsModel.isEnableAzureSSOForLearner(), this);

            hmSubMenuList = new HashMap<Integer, List<SideMenusModel>>();

            int i = 0;
            for (SideMenusModel menu : sideMenumodelList) {
                int parentMenuId = menu.getMenuId();
                subMenuList = db.getNativeSubMenusData(parentMenuId);
                if (subMenuList != null && subMenuList.size() > 0) {
                    hmSubMenuList.put(parentMenuId, subMenuList);
                }

                if (menu.getContextMenuId().equals("1")) {
                    if (IS_MENUS_FIRST_TIME) {
                        IS_MENUS_FIRST_TIME = false;
                    }
                }
                i++;
            }
            menuDynamicAdapter.refreshList(sideMenumodelList, hmSubMenuList);
            toggle.getDrawerArrowDrawable().setColor(Color.parseColor(uiSettingsModel.getHeaderTextColor()));
            Drawable bd = getDrawableFromStringHOmeMethod(R.string.fa_icon_home, this, uiSettingsModel.getAppHeaderTextColor());
            toolbar.setLogo(bd);
            updateBottomButtonColor();

        } else {
            backLayout.setVisibility(View.GONE);
            subsiteLayout.setVisibility(View.GONE);
            SIDEMENUOPENED_FIRSTTIME = 0;
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        Log.d(TAG, "onDrawerClosed: sidemenu");
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    public boolean respectiveMenuExistsOrNot(String contextMenuId) {
        boolean exists = false;

        for (int i = 0; i < sideMenumodelList.size(); i++) {

            if (contextMenuId.equalsIgnoreCase(sideMenumodelList.get(i).contextMenuId)) {
                exists = true;
            }

        }
        return exists;
    }

    public void refreshNotification() {

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
                        try {

                            JSONArray jsonTableAry = response.getJSONArray("notificationsdata");

                            if (jsonTableAry.length() > 0) {
                                txtBtnNotification.setText(getResources().getString(R.string.sidemenu_button_notificationbutton) + "(" + jsonTableAry.length() + ")");
                            } else {
                                txtBtnNotification.setText(getResources().getString(R.string.sidemenu_button_notificationbutton));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");

            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);


            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {


            }
        };
    }

    public void triggerPushNotification(JSONObject jsonObject) {

        int contextMenuID = jsonObject.optInt("contextmenuid", 1);
        String contentID = jsonObject.optString("contentid");
        String fourmID = jsonObject.optString("fourmid");

        switch (contextMenuID) {
            case 1:// mylearning
                homeControllClicked(true, contextMenuID, contentID, false, fourmID);
                MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;
                break;
            case 2:// catalog
                homeControllClicked(true, contextMenuID, contentID, false, fourmID);
                CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;
                break;
            case 3:
                homeControllClicked(true, contextMenuID, contentID, false, fourmID);
                break;
            case 4://discussion boards
                homeControllClicked(true, contextMenuID, contentID, false, fourmID);
                break;
            case 5:// ask experts
                homeControllClicked(true, contextMenuID, contentID, false, fourmID);
                break;
            default:
                homeControllClicked(true, 1, contentID, false, fourmID);
                break;
        }

    }


    public SideMenusModel getSideMenuModel() {

        SideMenusModel sideMenusModel = new SideMenusModel();

        if (sideMenumodelList.size() > 0) {

            for (int i = 0; i < sideMenumodelList.size(); i++) {


                if (sideMenumodelList.get(i).getComponentId().equalsIgnoreCase("1")) {

                    sideMenusModel = sideMenumodelList.get(i);

                }

            }

        }

        return sideMenusModel;
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
//    private final ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
//            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    };
}
