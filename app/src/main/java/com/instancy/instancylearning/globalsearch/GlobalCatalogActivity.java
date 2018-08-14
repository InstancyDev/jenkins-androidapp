package com.instancy.instancylearning.globalsearch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.askexpert.AskExpertFragment;
import com.instancy.instancylearning.catalog.CatalogCategories_Fragment;
import com.instancy.instancylearning.catalog.Catalog_fragment;
import com.instancy.instancylearning.chatmessanger.SendMessage_fragment;
import com.instancy.instancylearning.discussionfourms.DiscussionFourm_fragment;
import com.instancy.instancylearning.events.Event_fragment_new;
import com.instancy.instancylearning.gameficitation.LeaderboardFragment;
import com.instancy.instancylearning.gameficitation.MyAchivementsFragment;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.home.HomeCategories_Fragment;
import com.instancy.instancylearning.learningcommunities.LearningCommunities_fragment;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mycompetency.MyCompetencyFragment;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
import com.instancy.instancylearning.notifications.Notifications_fragment;
import com.instancy.instancylearning.peoplelisting.PeopleListing_fragment;
import com.instancy.instancylearning.profile.Profile_fragment;
import com.instancy.instancylearning.progressreports.ProgressReportfragment;
import com.instancy.instancylearning.settings.SettingsInnerFragment;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.webpage.Webpage_fragment;


/**
 * Created by Upendranath on 5/19/2017.
 * https://github.com/timigod/android-chat-ui
 * https://blog.sendbird.com/android-chat-tutorial-building-a-messaging-ui
 */

public class GlobalCatalogActivity extends AppCompatActivity {

    String TAG = GlobalCatalogActivity.class.getSimpleName();

    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    SideMenusModel sideMenusModel;

    String skillID,query;

    private static final int CONTENT_VIEW_ID = 10101010;

    public GlobalCatalogActivity() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("SIDEMENUMODEL");

        skillID = getIntent().getStringExtra("SKILLID");
        String titleName = getIntent().getStringExtra("TITLENAME");
        query = getIntent().getStringExtra("query");

        FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        if (savedInstanceState == null) {

            attachFragmentToActivity(sideMenusModel, titleName, "");
        }

        appUserModel = AppUserModel.getInstance();

        uiSettingsModel = UiSettingsModel.getInstance();

        preferencesManager = PreferencesManager.getInstance();


        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.button_chatbox_send), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.button_attachment), iconFont);

        vollyService = new VollyService(resultCallback, this);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));

        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "" + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace(); //
        }
    }

    public void attachFragmentToActivity(SideMenusModel sideMenusModel, String titleName, String topicID) {

        Fragment fragment = null; //

        switch (sideMenusModel.contextMenuId) {
            case "1":
                fragment = new MyLearningFragment();
                break;
            case "2":
//                if (sideMenusModel.getLandingPageType().equalsIgnoreCase("1")) {
//                    fragment = new CatalogCategories_Fragment();
//                } else {
                    fragment = new Catalog_fragment();
//                }
                break;
            case "3":
                fragment = new Profile_fragment();
                break;
            case "7":
                fragment = new Webpage_fragment();
                break;
            case "6":
                fragment = new HomeCategories_Fragment();
                break;
            case "8":
                fragment = new Event_fragment_new();
                break;
            case "4":
                fragment = new DiscussionFourm_fragment();
                break;
            case "5":
                fragment = new AskExpertFragment();
                break;
            case "9":
                fragment = new LearningCommunities_fragment();
                break;
            case "10":
                fragment = new PeopleListing_fragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FrameLayout frame = new FrameLayout(this);
            frame.setId(CONTENT_VIEW_ID);
            setContentView(frame, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            Bundle bundle = new Bundle();
            bundle.putSerializable("sidemenumodel", sideMenusModel);
            bundle.putBoolean("ISFROMCATEGORIES", false);
            bundle.putBoolean("ISFROMNOTIFICATIONS", false);
            bundle.putSerializable("SKILLID", skillID);
            bundle.putSerializable("TITLENAME", titleName);
            bundle.putSerializable("ISFROMGLOBAL", true);
            bundle.putString("TOPICID", topicID);
            bundle.putString("CONTENTID", "");
            bundle.putString("query", query);

            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(CONTENT_VIEW_ID, fragment).commit();

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Chat fragment");
//        chatService.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}

