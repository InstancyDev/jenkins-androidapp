package com.instancy.instancylearning.chatmessanger;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;


import android.widget.TextView;
import android.widget.Toast;


import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;

import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.ProfileDetailsModel;
import com.instancy.instancylearning.models.ProfileGroupModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UserEducationModel;
import com.instancy.instancylearning.models.UserExperienceModel;
import com.instancy.instancylearning.peoplelisting.PeopleProfileExpandAdapter;
import com.instancy.instancylearning.utils.PreferencesManager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 5/19/2017.
 * https://github.com/timigod/android-chat-ui
 * https://github.com/stfalcon-studio/ChatKit
 */

public class ChatFragment extends AppCompatActivity {


    String TAG = ChatFragment.class.getSimpleName();

    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    PreferencesManager preferencesManager;

    PeopleListingModel peopleListingModel;
    TextView userName, userLocation;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    PeopleProfileExpandAdapter profileDynamicAdapter;
    HashMap<String, List<ProfileConfigsModel>> hmGroupWiseConfigs = new HashMap<String, List<ProfileConfigsModel>>();

    List<ProfileGroupModel> profileGroupModelList = new ArrayList<>();

    List<UserEducationModel> educationModelArrayList = new ArrayList<>();

    List<UserExperienceModel> experienceModelArrayList = new ArrayList<>();

    ProfileDetailsModel profileDetailsModel = new ProfileDetailsModel();


    List<ProfileConfigsModel> profileConfigsModelList = new ArrayList<>();

    public ChatFragment() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_activity);

        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(this);

        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        vollyService = new VollyService(resultCallback, this);

        peopleListingModel = new PeopleListingModel();

        peopleListingModel = (PeopleListingModel) getIntent().getSerializableExtra("peopleListingModel");


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "Chat" + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace(); //
        }


        if (isNetworkConnectionAvailable(this, -1)) {


        } else {

            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

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

