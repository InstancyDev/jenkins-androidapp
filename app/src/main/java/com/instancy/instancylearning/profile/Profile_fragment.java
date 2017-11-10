package com.instancy.instancylearning.profile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.ProfileDetailsModel;
import com.instancy.instancylearning.models.ProfileGroupModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CATALOG_CODE;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 5/19/2017.
 */

public class Profile_fragment extends Fragment {

    @BindView(R.id.profile_thumbs)
    ImageView profileImage;

    ImageView profileRound;

    @BindView(R.id.profileexpandablelist)
    ExpandableListView profileExpandableList;

    String TAG = MyLearningFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    Context context;
    ContentValues cvEditFields = null;

    private List<SideMenusModel> sideMenusModelList = new ArrayList<>();
    SideMenusModel sideMenusModel;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    ProfileDynamicAdapter profileDynamicAdapter;
    HashMap<Integer, List<SideMenusModel>> hmSubMenuList = new HashMap<Integer, List<SideMenusModel>>();

    public Profile_fragment() {


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new DatabaseHandler(context);

        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        vollyService = new VollyService(resultCallback, context);

        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERNAME));
        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));
        sideMenusModel = new SideMenusModel();

        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");

        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        boolean isProfileExists = getALlProfilesDetailsFromDB();


    }


    public boolean getALlProfilesDetailsFromDB() {

        boolean isProfileExists = false;
        List<ProfileGroupModel> profileGroupModelList = new ArrayList<>();

        List<ProfileConfigsModel> profileConfigsModelList = new ArrayList<>();

        ProfileDetailsModel profileDetailsModel = new ProfileDetailsModel();

        profileDetailsModel = db.fetchProfileDetails(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        if (!profileDetailsModel.isProfilexist) {

            return false;
        }

        profileGroupModelList = db.fetchProfileGroupNames(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        profileConfigsModelList = db.fetchUserConfigs(appUserModel.getUserIDValue(), appUserModel.getSiteIDValue());


        HashMap<String, ArrayList<ProfileConfigsModel>> hmGroupWiseConfigs = new HashMap<String, ArrayList<ProfileConfigsModel>>();

        for (ProfileGroupModel grp : profileGroupModelList) {

            String groupID = grp.groupId;

            if (groupID==profileConfigsModelList.get(1).groupid){


            }

        }
        isProfileExists = true;
        HashMapGenerate();
        return isProfileExists;

    }


    public void HashMapGenerate() {
        HashMap<String, ArrayList<ProfileConfigsModel>> hmGroupWiseConfigs = new HashMap<String, ArrayList<ProfileConfigsModel>>();
//
//        ContentValues cvFields = new ContentValues();
//        cvFields = db.getProfileFieldsDictionary(appUserModel.getUserIDValue(), appUserModel.getSiteIDValue());
//        if (cvFields != null) {
//            cvEditFields = new ContentValues();
//            cvEditFields.putAll(cvFields);
//        }
//
//
//        ArrayList<ProfileConfigsModel> filteredGroups = new ArrayList<ProfileConfigsModel>();
//
//        filteredGroups = db.getProfileConfigsArray(appUserModel.getSiteIDValue(), "1");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_activity, container, false);
        ButterKnife.bind(this, rootView);
        View header = (View) getLayoutInflater(savedInstanceState).inflate(R.layout.profile_header_layout_, null);

        profileRound = (ImageView) header.findViewById(R.id.profile_round);

        profileDynamicAdapter = new ProfileDynamicAdapter(rootView.getContext(), hmSubMenuList, sideMenusModelList);

        profileExpandableList.setAdapter(profileDynamicAdapter);
        profileExpandableList.addHeaderView(header);

        initilizeView();

        if (isNetworkConnectionAvailable(getContext(), -1)) {

        } else {

        }

        return rootView;
    }


    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);

        String profileIma = appUserModel.getSiteURL() + "//Content/SiteFiles/" + appUserModel.getSiteIDValue() + "/ProfileImages/" + appUserModel.getProfileImage();
//        Glide.with(this).load(profileIma)
//                .thumbnail(0.5f).placeholder(R.drawable.user_placeholder)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(profileRound);

        Picasso.with(getContext()).load(profileIma).placeholder(R.drawable.user_placeholder).into(profileImage);
        Picasso.with(getContext()).load(profileIma).placeholder(R.drawable.user_placeholder).into(profileRound);
        profileImage.setImageAlpha(25);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String link = bundle.getString("url");

        }
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAIL_CATALOG_CODE && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("REFRESH", false);
            }
        }
    }
}
