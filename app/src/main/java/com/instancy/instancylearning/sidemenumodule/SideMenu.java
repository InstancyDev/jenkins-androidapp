package com.instancy.instancylearning.sidemenumodule;

import android.app.Activity;
import android.content.ContentValues;
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
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.adapters.MenuDrawerDynamicAdapter;
import com.instancy.instancylearning.catalog.CatalogCategories_Fragment;
import com.instancy.instancylearning.catalog.Catalog_fragment;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourms.DiscussionFourm_fragment;
import com.instancy.instancylearning.events.Event_fragment;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.home.HomeCategories_Fragment;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.learningcommunities.LearningCommunities_fragment;
import com.instancy.instancylearning.mainactivities.Login_activity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.ProfileDetailsModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
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
import static com.instancy.instancylearning.utils.StaticValues.IS_MENUS_FIRST_TIME;
import static com.instancy.instancylearning.utils.StaticValues.MAIN_MENU_POSITION;
import static com.instancy.instancylearning.utils.StaticValues.SUB_MENU_POSITION;

public class SideMenu extends AppCompatActivity {

    public String TAG = SideMenu.class.getSimpleName();

    private List<SideMenusModel> sideMenusModel;
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
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    UiSettingsModel uiSettingsModel;

    public Toolbar toolbar;

    SideMenusModel homeModel;
    int homeIndex = 0;

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

        View customNav = LayoutInflater.from(this).inflate(R.layout.homebutton, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        toolbar.setLogo(d);
        toolbar.setContentInsetStartWithNavigation(0);
        View logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
                homeControllClicked();
            }
        });

        ImageView imgBottom = (ImageView) findViewById(R.id.bottom_logo);
//        imgBottom.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.crop_life))) {
            logoView.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayShowCustomEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor(uiSettingsModel.getHeaderTextColor()));
        toggle.syncState();

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.drawerheaderview);
        rl.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));


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
        txtUsername.setText(name);

        txtAddress.setText(strAry[1]);

        sideMenusModel = db.getNativeMainMenusData();

        hmSubMenuList = new HashMap<Integer, List<SideMenusModel>>();


        int i = 0;
        for (SideMenusModel menu : sideMenusModel) {
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


        if (sideMenusModel != null) {
            menuDynamicAdapter = new MenuDrawerDynamicAdapter(
                    getApplicationContext(), hmSubMenuList, sideMenusModel);

            SideMenusModel model = new SideMenusModel();
            if (savedInstanceState == null) {
                String indexed = "0";
                for (int j = 0; j < sideMenusModel.size(); j++) {

                    if (sideMenusModel.get(j).getDisplayOrder() == 1) {
                        indexed = sideMenusModel.get(j).getContextMenuId();
                        model = sideMenusModel.get(j);
                    }
                }

                // on first time to display view for first navigation item based on the number
                selectItem(Integer.parseInt(indexed), model); // 2 is your fragment's number for "CollectionFragment"
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
                    int logoutPos = sideMenusModel.size() - 1;

//                    String filterCondition = sideMenusModel.get(groupPosition).getConditions();
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

                            if (sideMenusModel != null && hmSubMenuList != null) {
                                if (hmSubMenuList.containsKey(sideMenusModel.get(groupPosition).getMenuId())) {
                                    return false;
                                } else {

                                    try {
                                        selectItem(Integer.parseInt(sideMenusModel.get(groupPosition).getContextMenuId()), sideMenusModel.get(groupPosition));

                                    } catch (NumberFormatException numEx) {
                                        numEx.printStackTrace();
                                        selectItem(1, sideMenusModel.get(groupPosition));
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
    }

    public void homeControllClicked() {

        selectItem(homeIndex, homeModel);
        navDrawerExpandableView.expandGroup(0);
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

    public void selectItem(int menuid, SideMenusModel sideMenusModel) {

        Fragment fragment = null;

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
            default:
                Log.d(TAG, "selectItem: default contextmenu");
                fragment = new Catalog_fragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
            Bundle bundle = new Bundle();

            // send model from her to fragment
//            bundle.putString(CONTEXT_TITLE, sideMenusModel.get(position).getDisplayName());
            bundle.putSerializable("sidemenumodel", sideMenusModel);
            bundle.putBoolean("ISFROMCATEGORIES", false);
            fragment.setArguments(bundle);

            navDrawerExpandableView.setItemChecked(sideMenusModel.getDisplayOrder(), true);
            navDrawerExpandableView.setSelection(sideMenusModel.getDisplayOrder());

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
            List<SideMenusModel> mList = hmSubMenuList.get(sideMenusModel.get(MAIN_MENU_POSITION).getMenuId());
            SideMenusModel m = mList.get(childPosition);

            if (m.getIsOfflineMenu().equals("true")) {
                navDrawerExpandableView.setSelectedGroup(groupPosition);
                navDrawerExpandableView.setSelectedChild(groupPosition, childPosition, true);
                selectItem(Integer.parseInt(m.getContextMenuId()), m);
            } else {

            }

        }
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

}
