package com.instancy.instancylearning.peoplelisting;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.ProfileDetailsModel;
import com.instancy.instancylearning.models.ProfileGroupModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UserEducationModel;
import com.instancy.instancylearning.models.UserExperienceModel;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
import com.instancy.instancylearning.profile.ProfileExpandAdapter;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;

/**
 * Created by Upendranath on 5/19/2017.
 */

public class PeopleListingProfile extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.profile_thumbs)
    ImageView profileImage;

    ImageView profileRound;

    @BindView(R.id.profileexpandablelist)
    ExpandableListView profileExpandableList;

    @BindView(R.id.swipeprofile)
    SwipeRefreshLayout swipeRefreshLayout;

    String TAG = PeopleListingProfile.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    ContentValues cvEditFields = null;

    PeopleListingModel peopleListingModel;
    TextView userName, userLocation;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    ProfileExpandAdapter profileDynamicAdapter;
    HashMap<String, List<ProfileConfigsModel>> hmGroupWiseConfigs = new HashMap<String, List<ProfileConfigsModel>>();
    List<ProfileGroupModel> profileGroupModelList = new ArrayList<>();

    List<UserEducationModel> educationModelArrayList = new ArrayList<>();

    List<UserExperienceModel> experienceModelArrayList = new ArrayList<>();


    public PeopleListingProfile() {


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        vollyService = new VollyService(resultCallback, this);

        peopleListingModel = new PeopleListingModel();

        peopleListingModel = (PeopleListingModel) getIntent().getSerializableExtra("peopleListingModel");


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "Profile " + "</font>"));


        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }


        View header = (View) getLayoutInflater().inflate(R.layout.profile_header_layout_, null);
        swipeRefreshLayout.setOnRefreshListener(this);

        profileRound = (ImageView) header.findViewById(R.id.profile_round);

        userName = header.findViewById(R.id.profilename);
        userLocation = header.findViewById(R.id.userlocation);
//        boolean isProfileExists = getALlProfilesDetailsFromDB();
        profileDynamicAdapter = new ProfileExpandAdapter(this, experienceModelArrayList, educationModelArrayList, profileGroupModelList, hmGroupWiseConfigs);

        profileExpandableList.setAdapter(profileDynamicAdapter);
        profileExpandableList.addHeaderView(header);

        profileExpandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Doing nothing
                return true;
            }
        });

        if (profileGroupModelList != null && profileGroupModelList.size() > 0) {
            for (int i = 0; i < profileGroupModelList.size(); i++)
                profileExpandableList.expandGroup(i);
        }

        if (isNetworkConnectionAvailable(this, -1)) {

            profileWebCall(peopleListingModel.userID, false);
        } else {

            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }

        initilizeView();

    }

    public boolean getALlProfilesDetailsFromDB() {

        boolean isProfileExists = false;

        List<ProfileConfigsModel> profileConfigsModelList = new ArrayList<>();

        ProfileDetailsModel profileDetailsModel = new ProfileDetailsModel();

        profileDetailsModel = db.fetchProfileDetails(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        if (!profileDetailsModel.isProfilexist) {

            return false;
        }

//        educationModelArrayList = db.fetchUserEducationModel(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

//        experienceModelArrayList = db.fetchUserExperienceModel(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        String[] strAry = new String[2];

        strAry = extractProfileNameAndLocation(profileDetailsModel);

        userName.setText(strAry[0]);
        userLocation.setText(strAry[1]);

        profileGroupModelList = db.fetchProfileGroupNames(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        for (ProfileGroupModel grp : profileGroupModelList) {

            String groupID = grp.groupId;

            profileConfigsModelList = db.fetchUserConfigs(appUserModel.getUserIDValue(), appUserModel.getSiteIDValue(), groupID);

            ContentValues cvFields = new ContentValues();
            cvFields = db.getProfileFieldsDictionary(appUserModel.getUserIDValue(), appUserModel.getSiteIDValue());
            if (cvFields != null) {
                cvEditFields = new ContentValues();
                cvEditFields.putAll(cvFields);
            }

            for (int i = 0; i < profileConfigsModelList.size(); i++) {

                String keyName = profileConfigsModelList.get(i).datafieldname.toLowerCase().toLowerCase();

                if (keyName.equalsIgnoreCase("picture")) {
                    profileConfigsModelList.remove(i);
                    continue;
                }

                Log.d(TAG, "names here: " + cvFields.get(keyName));
                String valueName = "";
                try {
                    valueName = cvFields.get(keyName).toString();

                } catch (NullPointerException ex) {

                    ex.printStackTrace();
                }

                if (valueName.contains("null")) {
                    valueName = "";
//                    continue;
                }

                profileConfigsModelList.get(i).valueName = valueName;

                hmGroupWiseConfigs.put(grp.groupname, profileConfigsModelList);
            }
        }

        if (educationModelArrayList.size() > 0) {

            ProfileGroupModel profileGroupModel = new ProfileGroupModel();
            profileGroupModel.groupId = "123";
            profileGroupModel.groupname = "Education";

            hmGroupWiseConfigs.put("Education", profileConfigsModelList);

            profileGroupModelList.add(profileGroupModel);
        }


        if (experienceModelArrayList.size() > 0) {

            ProfileGroupModel profileGroupModel = new ProfileGroupModel();
            profileGroupModel.groupId = "124";
            profileGroupModel.groupname = "Experience";

            hmGroupWiseConfigs.put("Experience", profileConfigsModelList);

            profileGroupModelList.add(profileGroupModel);
        }

        isProfileExists = true;
        return isProfileExists;

    }

    private void profileWebCall(String userId, boolean isRefreshed) {

        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetUserDetailsv1?UserID=" + userId + "&siteURL=" + appUserModel.getSiteURL() + "&siteid=" + appUserModel.getSiteIDValue();

        urlStr = urlStr.replaceAll(" ", "%20");

        Log.d(TAG, "profileWebCall: " + urlStr);

        vollyService.getJsonObjResponseVolley("PROFILEDATA", urlStr, appUserModel.getAuthHeaders());

    }


    public void initilizeView() {

        String profileIma = appUserModel.getSiteURL() + "//Content/SiteFiles/" + appUserModel.getSiteIDValue() + "/ProfileImages/" + appUserModel.getProfileImage();

        Picasso.with(this).load(profileIma).placeholder(R.drawable.user_placeholder).into(profileImage);
        Picasso.with(this).load(profileIma).placeholder(R.drawable.user_placeholder).into(profileRound);
        profileImage.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        profileImage.setImageAlpha(25);

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

        if (!detailsModel.addresscity.equalsIgnoreCase("") && !detailsModel.addresscity.contains("na")) {
            if (!detailsModel.addressstate.equalsIgnoreCase("") && !detailsModel.addressstate.contains("na")) {
                location = detailsModel.addresscity + "," + detailsModel.addressstate;
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

        strAry[0] = upperCaseWords(name);
        strAry[1] = upperCaseWords(location);

        return strAry;
    }


    @Override
    public void onRefresh() {

        if (isNetworkConnectionAvailable(this, -1)) {
            swipeRefreshLayout.setRefreshing(true);
            profileWebCall(appUserModel.getUserIDValue(), true);

        } else {

            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    void initVolleyCallback() {

        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                if (requestType.equalsIgnoreCase("PROFILEDATA")) {
                    if (response != null) {

                        Log.d(TAG, "notifySuccess: " + response);
//                        try {
//                            swipeRefreshLayout.setRefreshing(false);
////                            db.InjectAllProfileDetails(response, appUserModel.getUserIDValue());
////                            boolean isProfileExists = getALlProfilesDetailsFromDB();
//                            if (isProfileExists) {
//                                profileDynamicAdapter.refreshList(experienceModelArrayList, educationModelArrayList, profileGroupModelList, hmGroupWiseConfigs);
//
//                                if (profileGroupModelList != null && profileGroupModelList.size() > 0) {
//                                    for (int i = 0; i < profileGroupModelList.size(); i++)
//                                        profileExpandableList.expandGroup(i);
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

                    } else {

                    }
                }

                svProgressHUD.dismiss();
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {


                svProgressHUD.dismiss();
            }
        };
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
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


}

