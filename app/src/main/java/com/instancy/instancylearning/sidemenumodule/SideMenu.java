package com.instancy.instancylearning.sidemenumodule;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.adapters.MenuDrawerDynamicAdapter;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.mainactivities.Login_activity;
import com.instancy.instancylearning.menufragments.Catalog_fragment;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
import com.instancy.instancylearning.profile.Profile_fragment;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.webpage.Webpage_fragment;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    // protected List<Menus> menus = null;
    protected List<SideMenusModel> mainMenuList = null;
    protected List<SideMenusModel> subMenuList = null;
    // int mainMenuPosition = -1;
    // int subMenuPosition = -1;
    HashMap<Integer, List<SideMenusModel>> hmSubMenuList = null;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    UiSettingsModel uiSettingsModel;

    public Toolbar toolbar;

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
//        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
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


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor(uiSettingsModel.getHeaderTextColor()));
        toggle.syncState();

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.drawerheaderview);
        rl.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        ImageView imgBottom = (ImageView) findViewById(R.id.bottom_logo);
//        imgBottom.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        // navdrawer Top layout initilization

        String profileIma = appUserModel.getSiteURL() + "//Content/SiteFiles/" + appUserModel.getSiteIDValue() + "/ProfileImages/" + appUserModel.getProfileImage();

        Picasso.with(this).load(profileIma).placeholder(R.drawable.user_placeholder).into(profileImage);
        txtUsername.setText(appUserModel.getUserName());

        txtAddress.setText(appUserModel.getDisplayName().isEmpty() ? getResources().getString(R.string.app_name) : appUserModel.getDisplayName());

        sideMenusModel = db.getNativeMainMenusData();

        if (sideMenusModel != null) {
            menuDynamicAdapter = new MenuDrawerDynamicAdapter(
                    getApplicationContext(), hmSubMenuList, sideMenusModel);

            if (savedInstanceState == null) {
                // on first time to display view for first navigation item based on the number
                selectItem(1, 0); // 2 is your fragment's number for "CollectionFragment"
                lastClicked = 0;
            }
            navDrawerExpandableView.setAdapter(menuDynamicAdapter);
            navDrawerExpandableView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                    Toast.makeText(SideMenu.this, "Here groupPosition " + groupPosition, Toast.LENGTH_SHORT).show();
                    int logoutPos = sideMenusModel.size() - 1;

                    String filterCondition = sideMenusModel.get(groupPosition).getConditions();
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
                            try {
                                selectItem(Integer.parseInt(sideMenusModel.get(groupPosition).getContextMenuId()), groupPosition);

                            } catch (NumberFormatException numEx) {
                                numEx.printStackTrace();
                                selectItem(1, groupPosition);
                            }
                        }
                    }
                    drawer.closeDrawer(Gravity.LEFT);
                    lastClicked = groupPosition;
                    return false;
                }
            });
        }
    }


    private void selectItem(int menuid, int position) {

        Fragment fragment = null;

        switch (menuid) {
            case 1:
                fragment = new MyLearningFragment();
                break;
            case 2:
                fragment = new com.instancy.instancylearning.catalog.Catalog_fragment();
                break;
            case 3:
                fragment = new Profile_fragment();
                break;
            case 7:
                fragment = new Webpage_fragment();
                break;
            default:
                fragment = new Catalog_fragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
            Bundle bundle = new Bundle();

            // send model from her to fragment
//            bundle.putString(CONTEXT_TITLE, sideMenusModel.get(position).getDisplayName());
            bundle.putSerializable("sidemenumodel", sideMenusModel.get(position));
            fragment.setArguments(bundle);

            navDrawerExpandableView.setItemChecked(position, true);
            navDrawerExpandableView.setSelection(position);

            drawer.closeDrawer(Gravity.LEFT);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.action_search);
//
//        if (menuItem != null) {
//            tintMenuIcon(SideMenu.this, menuItem, R.color.colorWhite);
//        }
//
//
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_search) {
////            Intent intentSettings = new Intent(this, Settings_activity.class);
////            intentSettings.putExtra(StaticValues.KEY_ISLOGIN, true);
////            startActivity(intentSettings);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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


}
