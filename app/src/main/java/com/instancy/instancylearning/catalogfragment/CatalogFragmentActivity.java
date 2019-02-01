package com.instancy.instancylearning.catalogfragment;

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
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.catalog.Catalog_fragment;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;

import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mycompetency.SkillModel;
import com.instancy.instancylearning.utils.PreferencesManager;


/**
 * Created by Upendranath on 5/19/2017.
 * https://github.com/timigod/android-chat-ui
 * https://blog.sendbird.com/android-chat-tutorial-building-a-messaging-ui
 */

public class CatalogFragmentActivity extends AppCompatActivity {

    String TAG = CatalogFragmentActivity.class.getSimpleName();

    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    SideMenusModel sideMenusModel;

    String skillID;

    private static final int CONTENT_VIEW_ID = 10101010;

    public CatalogFragmentActivity() {


    }
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,CatalogFragmentActivity.this);

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("SIDEMENUMODEL");
        skillID =getIntent().getStringExtra("SKILLID");
        String titleName=getIntent().getStringExtra("TITLENAME");


        FrameLayout frame = new FrameLayout(this);
        frame.setId(CONTENT_VIEW_ID);
        setContentView(frame, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        if (savedInstanceState == null) {
            Fragment newFragment = new Catalog_fragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("sidemenumodel", sideMenusModel);
            bundle.putSerializable("SKILLID",skillID);
            bundle.putSerializable("TITLENAME",titleName);
            bundle.putBoolean("ISFROMCATEGORIES", false);
            bundle.putBoolean("ISFROMNOTIFICATIONS", false);
            bundle.putBoolean("ISFROMMYCOMPETENCY", true);
            newFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(CONTENT_VIEW_ID, newFragment).commit();
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
                "Catalog" + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace(); //
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first: ");
        super.onActivityResult(requestCode, resultCode, data);

    }
//    https://www.survivingwithandroid.com/2013/05/android-http-downlod-upload-multipart.html
}

