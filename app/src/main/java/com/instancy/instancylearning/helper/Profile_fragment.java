package com.instancy.instancylearning.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 5/19/2017.
 */

public class Profile_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    @BindView(R.id.profile_thumbs)
    ImageView profileImage;

    ImageView profileRound;

    @BindView(R.id.profileexpandablelist)
    ExpandableListView profileExpandableList;

    @BindView(R.id.swipeprofile)
    SwipeRefreshLayout swipeRefreshLayout;

    String TAG = MyLearningFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    Context context;
    ContentValues cvEditFields = null;

    SideMenusModel sideMenusModel;
    TextView userName, userLocation;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    ProfileExpandAdapter profileDynamicAdapter;
    HashMap<String, List<ProfileConfigsModel>> hmGroupWiseConfigs = new HashMap<String, List<ProfileConfigsModel>>();
    List<ProfileGroupModel> profileGroupModelList = new ArrayList<>();

    public Profile_fragment() {


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


    }

    public boolean getALlProfilesDetailsFromDB() {

        boolean isProfileExists = false;

        List<ProfileConfigsModel> profileConfigsModelList = new ArrayList<>();

        ProfileDetailsModel profileDetailsModel = new ProfileDetailsModel();

        List<UserEducationModel> educationModelArrayList = new ArrayList<>();

        List<UserExperienceModel> experienceModelArrayList = new ArrayList<>();

        profileDetailsModel = db.fetchProfileDetails(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        if (!profileDetailsModel.isProfilexist) {

            return false;
        }

        educationModelArrayList = db.fetchUserEducationModel(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        experienceModelArrayList = db.fetchUserExperienceModel(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

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

                if (keyName.contains("picture")) {
                    profileConfigsModelList.remove(i);
                    continue;

                }

                Log.d(TAG, "names here: " + cvFields.get(keyName));
                String valueName = cvFields.get(keyName).toString();

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


        isProfileExists = true;
        return isProfileExists;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_activity, container, false);
        ButterKnife.bind(this, rootView);

        View header = (View) getLayoutInflater(savedInstanceState).inflate(R.layout.profile_header_layout_, null);
        swipeRefreshLayout.setOnRefreshListener(this);
        profileRound = (ImageView) header.findViewById(R.id.profile_round);

        userName = header.findViewById(R.id.profilename);
        userLocation = header.findViewById(R.id.userlocation);
        boolean isProfileExists = getALlProfilesDetailsFromDB();
//        profileDynamicAdapter = new ProfileExpandAdapter(rootView.getContext(), profileGroupModelList, hmGroupWiseConfigs);

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


        initilizeView();


        return rootView;
    }


    private void profileWebCall(String userId, boolean isRefreshed) {

        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetUserDetails?UserID=" + userId + "&siteURL=" + appUserModel.getSiteURL() + "&siteid=" + appUserModel.getSiteIDValue();

        urlStr = urlStr.replaceAll(" ", "%20");

        Log.d(TAG, "profileWebCall: " + urlStr);

        vollyService.getJsonObjResponseVolley("PROFILEDATA", urlStr, appUserModel.getAuthHeaders());

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

        Picasso.with(getContext()).load(profileIma).placeholder(R.drawable.user_placeholder).into(profileImage);
        Picasso.with(getContext()).load(profileIma).placeholder(R.drawable.user_placeholder).into(profileRound);
        profileImage.setImageAlpha(25);
        profileRound.setOnClickListener(this);

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


        strAry[0] = name;
        strAry[1] = location;

        return strAry;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onRefresh() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            swipeRefreshLayout.setRefreshing(true);
            profileWebCall(appUserModel.getUserIDValue(), true);

        } else {

            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    void initVolleyCallback() {

        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                if (requestType.equalsIgnoreCase("PROFILEDATA")) {
                    if (response != null) {

                        try {
                            swipeRefreshLayout.setRefreshing(false);
                            db.InjectAllProfileDetails(response, appUserModel.getUserIDValue());
                            boolean isProfileExists = getALlProfilesDetailsFromDB();
                            if (isProfileExists) {
                                profileDynamicAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.profile_round:
                Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                break;

        }

    }
}
