package com.instancy.instancylearning.sidemenumodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.adapters.MenuDrawerDynamicAdapter;
import com.instancy.instancylearning.catalog.CatalogCategories_Fragment;
import com.instancy.instancylearning.catalog.Catalog_fragment;
import com.instancy.instancylearning.chatmessanger.SendMessage_fragment;
import com.instancy.instancylearning.chatmessanger.SignalAService;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourms.DiscussionFourm_fragment;
import com.instancy.instancylearning.events.Event_fragment;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.home.HomeCategories_Fragment;
import com.instancy.instancylearning.learningcommunities.LearningCommunities_fragment;
import com.instancy.instancylearning.mainactivities.Login_activity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.ProfileDetailsModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
import com.instancy.instancylearning.notifications.Notifications_fragment;
import com.instancy.instancylearning.peoplelisting.PeopleListing_fragment;
import com.instancy.instancylearning.profile.Profile_fragment;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.webpage.Webpage_fragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.getToolbarLogoIcon;
import static com.instancy.instancylearning.utils.StaticValues.CATALOG_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.IS_MENUS_FIRST_TIME;
import static com.instancy.instancylearning.utils.StaticValues.MAIN_MENU_POSITION;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.PROFILE_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.SIDEMENUOPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.SUB_MENU_POSITION;
import static com.instancy.instancylearning.utils.StaticValues.BACKTOMAINSITE;
import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;

public class SideMenu extends AppCompatActivity implements View.OnClickListener, DrawerLayout.DrawerListener {

    public String TAG = SideMenu.class.getSimpleName();

    private List<SideMenusModel> sideMenumodelList;
    DatabaseHandler db;

    @BindView(R.id.expanded_menu_drawer)
    ExpandableListView navDrawerExpandableView;

    @BindView(R.id.profile_thumbs)
    ImageView profileImage;

    @BindView(R.id.id_username)
    TextView txtUsername;

    @BindView(R.id.id_user_address)
    TextView txtAddress;

    @BindView(R.id.txtbtn_settings)
    TextView txtBtnSettings;

    @BindView(R.id.txtbtn_back)
    TextView textBtnBack;

    @BindView(R.id.back_layout)
    RelativeLayout backLayout;

    @BindView(R.id.sendmessage_layout)
    RelativeLayout sendMessageLayout;

    @BindView(R.id.notification_layout)
    RelativeLayout notificationLayout;


    @BindView(R.id.subsitelayout)
    LinearLayout subsiteLayout;


    @BindView(R.id.back_font)
    TextView fontBack;

    @BindView(R.id.settings_font)
    TextView fontSettings;

    @BindView(R.id.notification_font)
    TextView fontNotification;

    @BindView(R.id.txtbtn_notification)
    TextView txtBtnNotification;

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
    public Toolbar toolbar;

    SideMenusModel homeModel;
    int homeIndex = 0;

    RelativeLayout drawerHeaderView;
    public View logoView;

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


        View customNav = LayoutInflater.from(this).inflate(R.layout.homebutton, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        toolbar.setLogo(d);
        toolbar.setContentInsetStartWithNavigation(0);
        logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
                homeControllClicked(false, 0);


            }
        });

        backLayout.setOnClickListener(this);
        sendMessageLayout.setOnClickListener(this);
        notificationLayout.setOnClickListener(this);

        ImageView imgBottom = (ImageView) findViewById(R.id.bottom_logo);
//       imgBottom.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.crop_life))) {
            logoView.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayShowCustomEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(this);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor(uiSettingsModel.getHeaderTextColor()));
        toggle.syncState();

        drawerHeaderView = (RelativeLayout) findViewById(R.id.drawerheaderview);
        drawerHeaderView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));


        ProfileDetailsModel profileDetailsModel = new ProfileDetailsModel();

        profileDetailsModel = db.fetchProfileDetails(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        String[] strAry = new String[2];

        strAry = extractProfileNameAndLocation(profileDetailsModel);

        String profileIma = appUserModel.getSiteURL() + "//Content/SiteFiles/" + appUserModel.getSiteIDValue() + "/ProfileImages/" + appUserModel.getProfileImage();

        Picasso.with(this).load(profileIma).placeholder(R.drawable.user_placeholder).into(profileImage);
        String name = strAry[0];

        if (name.contains("Anonymous") || name.contains("null")) {

            name = preferencesManager.getStringValue(StaticValues.KEY_USERNAME);
        }
        txtUsername.setText(upperCaseWords(name));

        txtAddress.setText(upperCaseWords(strAry[1]));

        sideMenumodelList = db.getNativeMainMenusData();

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
                sendMessageLayout.setVisibility(View.VISIBLE);
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

                // on first time to display view for first navigation item based on the number
                selectItem(Integer.parseInt(indexed), model,false); // 2 is your fragment's number for "CollectionFragment"
                homeModel = model;
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
                    if (logoutPos == groupPosition) {
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
                                        selectItem(Integer.parseInt(sideMenumodelList.get(groupPosition).getContextMenuId()), sideMenumodelList.get(groupPosition),false);

                                    } catch (NumberFormatException numEx) {
                                        numEx.printStackTrace();
                                        selectItem(1, sideMenumodelList.get(groupPosition),false);
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
                    subMenuItemClickListener(parent, groupPosition, childPosition);
                    return true;
                }

            });


            navDrawerExpandableView.expandGroup(lastClicked);
        }

        signalAService = SignalAService.newInstance(this);
        signalAService.startSignalA();
    }

    public void homeControllClicked(boolean isFromNotification, int menuId) {

        if (!isFromNotification)
            selectItem(homeIndex, homeModel,false);
        else {
            selectItem(menuId, getMenuModelForNotification(menuId),true);
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

    private Bitmap createBitmapFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void selectItem(int menuid, SideMenusModel sideMenusModel,boolean isFromNotification) {

        Fragment fragment = null; //

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
                fragment = new Event_fragment();
                break;
            case 4:
                fragment = new DiscussionFourm_fragment();
                break;
            case 9:
                fragment = new LearningCommunities_fragment();
                break;
            case 10:
                fragment = new PeopleListing_fragment();
                break;
            case 99:
                fragment = new SendMessage_fragment();
                break;
            case 100:
                fragment = new Notifications_fragment();
                break;
            default:
                Log.d(TAG, "selectItem: default contextmenu");
                fragment = new com.instancy.instancylearning.menufragments.Catalog_fragment();
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
                fragment.setArguments(bundle);
                navDrawerExpandableView.setItemChecked(sideMenusModel.getDisplayOrder(), true);
                navDrawerExpandableView.setSelection(sideMenusModel.getDisplayOrder());
            }

            drawer.closeDrawer(Gravity.LEFT);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

//            if (getFragmentManager().getBackStackEntryCount() > 0) {
//                getFragmentManager().popBackStack();
//                return;
//            }
//            if (!doubleBackToExitPressedOnce) {
//                this.doubleBackToExitPressedOnce = true;
//                Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
//
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        doubleBackToExitPressedOnce = false;
//                    }
//                }, 2000);
//            } else {
            super.onBackPressed();
//                return;
//            }
        }
    }


    private void subMenuItemClickListener(ExpandableListView parent,
                                          int groupPosition, int childPosition) {

        if (!(MAIN_MENU_POSITION == groupPosition && SUB_MENU_POSITION == childPosition)) {
            MAIN_MENU_POSITION = groupPosition;
            SUB_MENU_POSITION = childPosition;
            List<SideMenusModel> mList = hmSubMenuList.get(sideMenumodelList.get(MAIN_MENU_POSITION).getMenuId());
            SideMenusModel m = mList.get(childPosition);

            if (m.getIsOfflineMenu().equals("true")) {
                navDrawerExpandableView.setSelectedGroup(groupPosition);
                navDrawerExpandableView.setSelectedChild(groupPosition, childPosition, true);
                selectItem(Integer.parseInt(m.getContextMenuId()), m,false);
            } else {

            }

        }
    }


    public void backToMainSite() {
        BACKTOMAINSITE = 2;
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
        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID));
        appUserModel.setPassword(preferencesManager.getStringValue(StaticValues.KEY_USERPASSWORD));
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        drawerHeaderView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        sideMenumodelList = new ArrayList<SideMenusModel>();
        sideMenumodelList = db.getNativeMainMenusData();

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

        homeControllClicked(false, 0);
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


        if (!detailsModel.displayname.equalsIgnoreCase("")) {
            name = detailsModel.displayname;
        } else if (!detailsModel.firstname.equalsIgnoreCase("")) {
            name = detailsModel.firstname + " " + detailsModel.lastname;
        } else {
            name = "Anonymous";
        }

        if (!detailsModel.addresscity.equalsIgnoreCase("") && !detailsModel.addresscity.contains("na")) {
            if (!detailsModel.addressstate.equalsIgnoreCase("") && !detailsModel.addressstate.contains("na")) {
                location = detailsModel.addresscity + ", " + detailsModel.addressstate;
            } else {
                location = detailsModel.addresscity;
            }
        } else if (!detailsModel.addressstate.equalsIgnoreCase("") && !detailsModel.addressstate.contains("na")) {
            location = detailsModel.addressstate;
        } else if (!detailsModel.addresscountry.equalsIgnoreCase("") && !detailsModel.addresscountry.contains("na")) {
            location = detailsModel.addresscountry;
        } else {
            location = "";
        }

        strAry[0] = name;
        strAry[1] = location;

        return strAry;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_layout:
                backToMainSite();
                break;
            case R.id.sendmessage_layout:
                selectItem(99, sideMenumodelList.get(0),false);
                break;
            case R.id.notification_layout:
                selectItem(100, sideMenumodelList.get(0),false);
                break;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: sidemenu");
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
//        Log.d(TAG, "onDrawerSlide: sidemenu");
//        if (SIDEMENUOPENED_FIRSTTIME == 0) {
//            enteredSubsiteMethods();
//        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        Log.d(TAG, "onDrawerOpened: sidemenu");
        if (SIDEMENUOPENED_FIRSTTIME == 0) {
            enteredSubsiteMethods();
        }
    }

    public void enteredSubsiteMethods() {
        String isSubSiteEntered = preferencesManager.getStringValue(StaticValues.SUB_SITE_ENTERED);
        if (isSubSiteEntered.equalsIgnoreCase("true")) {
            backLayout.setVisibility(View.VISIBLE);
            subsiteLayout.setVisibility(View.VISIBLE);
            SIDEMENUOPENED_FIRSTTIME = 1;
            BACKTOMAINSITE = 1;
            TextView mainSiteName = (TextView) backLayout.findViewById(R.id.txtbtn_back);
            TextView subsiteName = (TextView) subsiteLayout.findViewById(R.id.subsitename);
            LinearLayout subsiteLa = (LinearLayout) subsiteLayout.findViewById(R.id.subsitelays);
            subsiteName.setText(appUserModel.getSiteName());
            mainSiteName.setText(appUserModel.getMainSiteName());
            subsiteLa.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
            subsiteLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
            subsiteLa.setAlpha(.9f);
//            subsiteLayout.setAlpha(.7f);

            drawerHeaderView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

            sideMenumodelList = new ArrayList<SideMenusModel>();
            sideMenumodelList = db.getNativeMainMenusData();

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
}
