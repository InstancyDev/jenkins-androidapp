package com.instancy.instancylearning.peoplelisting;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;

/**
 * Created by Upendranath on 5/19/2017.
 * https://github.com/timigod/android-chat-ui
 * https://github.com/stfalcon-studio/ChatKit
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
    PreferencesManager preferencesManager;
    ContentValues cvEditFields = null;

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
                peopleListingModel.userDisplayname + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace(); //
        }

        View header = (View) getLayoutInflater().inflate(R.layout.profile_header_layout_, null);
        swipeRefreshLayout.setOnRefreshListener(this);

        profileRound = (ImageView) header.findViewById(R.id.profile_round);

        userName = header.findViewById(R.id.profilename);
        userLocation = header.findViewById(R.id.userlocation);

        userName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        userLocation.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        profileDynamicAdapter = new PeopleProfileExpandAdapter(this, experienceModelArrayList, educationModelArrayList, profileGroupModelList, hmGroupWiseConfigs);

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

        userName.setText(peopleListingModel.userDisplayname);
        userLocation.setText(peopleListingModel.mainOfficeAddress);

        initilizeView();

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

        String imgUrl = peopleListingModel.siteURL + peopleListingModel.memberProfileImage;

        Picasso.with(this).load(imgUrl).placeholder(R.drawable.defaulttechguy).into(profileImage);
        Picasso.with(this).load(imgUrl).placeholder(R.drawable.defaulttechguy).into(profileRound);
        profileImage.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        profileImage.setImageAlpha(25);

    }


    @Override
    public void onRefresh() {

        if (isNetworkConnectionAvailable(this, -1)) {
            swipeRefreshLayout.setRefreshing(true);
            profileWebCall(peopleListingModel.userID, true);

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
                        try {
//                            swipeRefreshLayout.setRefreshing(false);

                            hmGroupWiseConfigs = new HashMap<String, List<ProfileConfigsModel>>();

                            profileGroupModelList = new ArrayList<>();

                            educationModelArrayList = new ArrayList<>();

                            experienceModelArrayList = new ArrayList<>();

                            profileDetailsModel = new ProfileDetailsModel();

                            profileConfigsModelList = new ArrayList<>();

                            InjectAllProfileDetails(response, peopleListingModel.userID);
                            getOnlinePprofileDetails();


                            profileDynamicAdapter.refreshList(experienceModelArrayList, educationModelArrayList, profileGroupModelList, hmGroupWiseConfigs);
                            if (profileGroupModelList != null && profileGroupModelList.size() > 0) {
                                for (int i = 0; i < profileGroupModelList.size(); i++)
                                    profileExpandableList.expandGroup(i);
                            }
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                }
                swipeRefreshLayout.setRefreshing(false);
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

    public void InjectAllProfileDetails(JSONObject jsonObject, String userID) throws JSONException {
        if (jsonObject != null || jsonObject.length() != 0) {

            Log.d(TAG, "InjectAllProfileDetails: " + jsonObject);

            JSONArray jsonProfileAry = null;

            JSONArray jsonGroupsAry = null;

            JSONArray jsonEducationAry = null;

            JSONArray jsonExperienceAry = null;

            if (jsonObject.has("userprofiledetails")) {
                jsonProfileAry = jsonObject.getJSONArray("userprofiledetails");
            } else {

                jsonProfileAry = jsonObject.getJSONArray("table");
            }

            if (jsonObject.has("userprofilegroups")) {

                jsonGroupsAry = jsonObject.getJSONArray("userprofilegroups");
            } else {
                jsonGroupsAry = jsonObject.getJSONArray("table2");
            }

            if (jsonObject.has("usereducationdata")) {

                jsonEducationAry = jsonObject.getJSONArray("usereducationdata");

                educationModelArrayList = new ArrayList<>();

                // for all education data hide for cle
                if (jsonEducationAry.length() > 0) {

                    for (int i = 0; i < jsonEducationAry.length(); i++) {

                        UserEducationModel userEducationModel = new UserEducationModel();

                        JSONObject experiencObj = jsonEducationAry.getJSONObject(i);

                        if (experiencObj.has("userid")) {

                            userEducationModel.userid = experiencObj.get("userid").toString();
                        }
                        if (experiencObj.has("school")) {
                            userEducationModel.school = experiencObj.get("school").toString();
                        }
                        if (experiencObj.has("country")) {
                            userEducationModel.country = experiencObj.get("country").toString();
                        }
                        if (experiencObj.has("degree")) {
                            userEducationModel.degree = experiencObj.get("degree").toString();
                        }
                        if (experiencObj.has("fromyear")) {
                            userEducationModel.fromyear = experiencObj.get("fromyear").toString();
                        }
                        if (experiencObj.has("totalperiod")) {
                            userEducationModel.totalperiod = experiencObj.get("totalperiod").toString();
                        }
                        if (experiencObj.has("toyear")) {
                            userEducationModel.toyear = experiencObj.get("toyear").toString();
                        }
                        if (experiencObj.has("titleeducation")) {
                            userEducationModel.titleeducation = experiencObj.get("titleeducation").toString();
                        }

                        if (experiencObj.has("titleid")) {
                            userEducationModel.titleid = experiencObj.get("titleid").toString();
                        }

                        if (experiencObj.has("description")) {
                            userEducationModel.description = experiencObj.get("description").toString();
                        }

                        if (experiencObj.has("displayno")) {
                            userEducationModel.displayno = experiencObj.get("displayno").toString();
                        }

                        Log.d(TAG, "InjectAllProfileDetails: at index " + userEducationModel);
                        educationModelArrayList.add(userEducationModel);

                    }

                }


            } else {
                //no else
            }

            if (jsonObject.has("userexperiencedata")) {

                jsonExperienceAry = jsonObject.getJSONArray("userexperiencedata");

                experienceModelArrayList = new ArrayList<>();
                // for experience data
                if (jsonExperienceAry.length() > 0) {
                    for (int i = 0; i < jsonExperienceAry.length(); i++) {

                        UserExperienceModel userExperienceModel = new UserExperienceModel();

                        JSONObject experiencObj = jsonExperienceAry.getJSONObject(i);

                        if (experiencObj.has("userid")) {

                            userExperienceModel.userID = experiencObj.get("userid").toString();
                        }
                        if (experiencObj.has("title")) {
                            userExperienceModel.title = experiencObj.get("title").toString();
                        }
                        if (experiencObj.has("location")) {
                            userExperienceModel.location = experiencObj.get("location").toString();
                        }
                        if (experiencObj.has("description")) {
                            userExperienceModel.description = experiencObj.get("description").toString();
                        }
                        if (experiencObj.has("difference")) {
                            userExperienceModel.difference = experiencObj.get("difference").toString();
                        }
                        if (experiencObj.has("displayno")) {
                            userExperienceModel.displayNo = experiencObj.get("displayno").toString();
                        }
                        if (experiencObj.has("fromdate")) {
                            userExperienceModel.fromDate = experiencObj.get("fromdate").toString();
                        }
                        if (experiencObj.has("todate")) {
                            userExperienceModel.toDate = experiencObj.get("todate").toString();
                        }

                        if (experiencObj.has("companyname")) {
                            userExperienceModel.companyName = experiencObj.get("companyname").toString();
                        }


                        experienceModelArrayList.add(userExperienceModel);
                    }


                }


            } else {
                //no else for old apis
            }

            // for all groups and child data
            if (jsonGroupsAry.length() > 0) {

                for (int i = 0; i < jsonGroupsAry.length(); i++) {

                    JSONObject profileGroupObj = jsonGroupsAry.getJSONObject(i);

                    JSONObject jsonObjectProfileConfigs = jsonGroupsAry.getJSONObject(i);

                    String groupid = "";
                    if (profileGroupObj.has("groupid")) {

                        groupid = profileGroupObj.get("groupid").toString();
                    }

                    if (groupid.equalsIgnoreCase("6"))
                        continue;

                    if (jsonObjectProfileConfigs.has("datafilelist")) {

                        JSONArray jsonProfileConfigArray = jsonObjectProfileConfigs.getJSONArray("datafilelist");

                        if (jsonProfileConfigArray.length() > 0) {

                            injectProfileConfigs(jsonProfileConfigArray, userID, groupid);

                        } else {
                            continue;
                        }

                    }

                    String groupname = "";
                    String objecttypeid = "";
                    String showinprofile = "";
                    String localeid = "";

                    if (profileGroupObj.has("groupname")) {

                        groupname = profileGroupObj.get("groupname").toString();
                    }

                    if (groupname.contains("None"))
                        continue;

                    if (profileGroupObj.has("groupid")) {

                        groupid = profileGroupObj.get("groupid").toString();
                    }


                    if (profileGroupObj.has("groupid")) {

                        groupid = profileGroupObj.get("groupid").toString();
                    }

                    if (profileGroupObj.has("objecttypeid")) {

                        objecttypeid = profileGroupObj.get("objecttypeid").toString();
                    }

                    if (profileGroupObj.has("showinprofile")) {
                        showinprofile = profileGroupObj.get("showinprofile").toString();

                    }

                    if (profileGroupObj.has("localeid")) {
                        localeid = profileGroupObj.get("localeid").toString();

                    }

                    try {
                        ProfileGroupModel profileGroupModel = new ProfileGroupModel();
                        profileGroupModel.groupId = groupid;
                        profileGroupModel.groupname = groupname;
                        profileGroupModel.objecttypeid = objecttypeid;
                        profileGroupModel.showinprofile = showinprofile;
                        profileGroupModel.userid = userID;
                        profileGroupModel.siteid = appUserModel.getSiteIDValue();

                        profileGroupModelList.add(profileGroupModel);

                    } catch (SQLiteException exception) {

                        exception.printStackTrace();
                    }
                }

            }

            // for all profile data
            if (jsonProfileAry.length() > 0) {

                for (int i = 0; i < jsonProfileAry.length(); i++) {
                    ProfileDetailsModel profileModel = new ProfileDetailsModel();

                    JSONObject profileObj = jsonProfileAry.getJSONObject(i);


                    if (profileObj.has("firstname")) {

                        profileModel.firstname = profileObj.get("firstname").toString();
                    }
                    if (profileObj.has("lastname")) {
                        profileModel.lastname = profileObj.get("lastname").toString();
                    }
                    if (profileObj.has("accounttype")) {
                        profileModel.accounttype = profileObj.get("accounttype").toString();
                    }
                    if (profileObj.has("orgunitid")) {
                        profileModel.orgunitid = profileObj.get("orgunitid").toString();
                    }
                    if (profileObj.has("siteid")) {
                        profileModel.siteid = profileObj.get("siteid").toString();
                    }
                    if (profileObj.has("approvalstatus")) {
                        profileModel.approvalstatus = profileObj.get("approvalstatus").toString();
                    }
                    if (profileObj.has("displayname")) {
                        profileModel.displayname = profileObj.get("displayname").toString();
                    }
                    if (profileObj.has("organization")) {
                        profileModel.organization = profileObj.get("organization").toString();
                    }
                    if (profileObj.has("email")) {
                        profileModel.email = profileObj.get("email").toString();
                    }
                    if (profileObj.has("usersite")) {
                        profileModel.usersite = profileObj.get("usersite").toString();
                    }
                    if (profileObj.has("supervisoremployeeid")) {
                        profileModel.supervisoremployeeid = profileObj.get("supervisoremployeeid").toString();
                    }
                    if (profileObj.has("addressline1")) {
                        profileModel.addressline1 = profileObj.get("addressline1").toString();
                    }
                    if (profileObj.has("addresscity")) {
                        profileModel.addresscity = profileObj.get("addresscity").toString();
                    }
                    if (profileObj.has("addressstate")) {
                        profileModel.addressstate = profileObj.get("addressstate").toString();
                    }
                    if (profileObj.has("addresszip")) {
                        profileModel.addresszip = profileObj.get("addresszip").toString();
                    }
                    if (profileObj.has("addresscountry")) {
                        profileModel.addresscountry = profileObj.get("addresscountry").toString();
                    }
                    if (profileObj.has("phone")) {
                        profileModel.phone = profileObj.get("phone").toString();
                    }
                    if (profileObj.has("mobilephone")) {
                        profileModel.mobilephone = profileObj.get("mobilephone").toString();
                    }
                    if (profileObj.has("imaddress")) {
                        profileModel.imaddress = profileObj.get("imaddress").toString();
                    }
                    if (profileObj.has("dateofbirth")) {

                        String formattedDate = formatDate(profileObj.get("dateofbirth").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MMM-dd");

                        profileModel.dateofbirth = formattedDate;

                        // format the date
                    }
                    if (profileObj.has("gender")) {

                        String genderStr = profileObj.get("gender").toString();

                        if (genderStr.toLowerCase().equalsIgnoreCase("true")) {
                            profileModel.gender = "Male";
                        } else {
                            profileModel.gender = "Female";
                        }
                    }
                    if (profileObj.has("nvarchar6")) {
                        profileModel.nvarchar6 = profileObj.get("nvarchar6").toString();
                    }
                    if (profileObj.has("paymentmode")) {
                        profileModel.paymentmode = profileObj.get("paymentmode").toString();
                    }
                    if (profileObj.has("nvarchar7")) {
                        profileModel.nvarchar7 = profileObj.get("nvarchar7").toString();
                    }
                    if (profileObj.has("nvarchar8")) {
                        profileModel.nvarchar8 = profileObj.get("nvarchar8").toString();
                    }
                    if (profileObj.has("nvarchar9")) {
                        profileModel.nvarchar9 = profileObj.get("nvarchar9").toString();
                    }
                    if (profileObj.has("securepaypalid")) {
                        profileModel.securepaypalid = profileObj.get("securepaypalid").toString();
                    }
                    if (profileObj.has("nvarchar10")) {
                        profileModel.nvarchar10 = profileObj.get("nvarchar10").toString();
                    }
                    if (profileObj.has("picture")) {
                        profileModel.picture = profileObj.get("picture").toString();
                    }
                    if (profileObj.has("highschool")) {
                        profileModel.highschool = profileObj.get("highschool").toString();
                    }
                    if (profileObj.has("college")) {
                        profileModel.college = profileObj.get("college").toString();
                    }
                    if (profileObj.has("jobtitle")) {
                        profileModel.jobtitle = profileObj.get("jobtitle").toString();
                    }
                    if (profileObj.has("businessfunction")) {
                        profileModel.businessfunction = profileObj.get("businessfunction").toString();
                    }
                    if (profileObj.has("primaryjobfunction")) {
                        profileModel.primaryjobfunction = profileObj.get("primaryjobfunction").toString();
                    }
                    if (profileObj.has("payeeaccountno")) {
                        profileModel.payeeaccountno = profileObj.get("payeeaccountno").toString();
                    }

                    if (profileObj.has("payeename")) {
                        profileModel.payeename = profileObj.get("payeename").toString();
                    }
                    if (profileObj.has("paypalaccountname")) {
                        profileModel.paypalaccountname = profileObj.get("paypalaccountname").toString();
                    }
                    if (profileObj.has("paypalemail")) {
                        profileModel.paypalemail = profileObj.get("paypalemail").toString();
                    }

                    if (profileObj.has("shipaddline1")) {
                        profileModel.shipaddline1 = profileObj.get("shipaddline1").toString();
                    }
                    if (profileObj.has("shipaddcity")) {
                        profileModel.shipaddcity = profileObj.get("shipaddcity").toString();
                    }

                    if (profileObj.has("shipaddstate")) {
                        profileModel.shipaddstate = profileObj.get("shipaddstate").toString();
                    }
                    if (profileObj.has("shipaddzip")) {
                        profileModel.shipaddzip = profileObj.get("shipaddzip").toString();
                    }

                    if (profileObj.has("shipaddcountry")) {
                        profileModel.shipaddcountry = profileObj.get("shipaddcountry").toString();
                    }
                    if (profileObj.has("shipaddphone")) {
                        profileModel.shipaddphone = profileObj.get("shipaddphone").toString();
                    }

                    if (profileObj.has("objectid")) {
                        profileModel.userid = profileObj.get("objectid").toString();
                    }

//                    profileModel.userid = appUserModel.getUserIDValue();

                    Log.d(TAG, "InjectAllProfileDetails: at index " + profileModel);

                    profileDetailsModel = profileModel;
                }

            }


        }
    }

    public void injectProfileConfigs(JSONArray configAry, String userId, String groupId) throws JSONException {

        String[] profileAry = {"objectid", "accounttype", "orgunitid", "siteid", "approvalstatus", "firstname", "lastname", "displayname", "organization", "email", "usersite", "supervisoremployeeid", "addressline1", "addresscity", "addressstate", "addresszip", "addresscountry", "phone", "mobilephone", "imaddress", "dateofbirth", "gender", "nvarchar6", "paymentmode", "nvarchar7", "nvarchar8", "nvarchar9", "securepaypalid", "nvarchar10", "picture", "highschool", "college", "highestdegree", "jobtitle", "businessfunction", "primaryjobfunction", "payeeaccountno", "payeename", "paypalaccountname", "paypalemail", "shipaddline1", "shipaddcity", "shipaddstate", "shipaddzip", "shipaddcountry", "shipaddphone"};


        for (int i = 0; i < configAry.length(); i++) {

            JSONObject profileObj = configAry.getJSONObject(i);

            if (profileObj.has("datafieldname")) {

                String dataFieldName = profileObj.get("datafieldname").toString();

                for (String fieldName : profileAry) {

                    if (dataFieldName.toLowerCase().equalsIgnoreCase(fieldName)) {
                        Log.d(TAG, "injectProfileConfigs: dataFieldName " + dataFieldName);

                        String aliasname = "";
                        String attributedisplaytext = "";
                        String groupid = "";
                        String displayOrder = "";
                        String attributeconfigid = "";
                        String isrequired = "";
                        String iseditable = "";
                        String enduservisibility = "";
                        String uicontroltypeid = "";
                        String name = "";
                        String datafieldname = "";

                        if (profileObj.has("datafieldname")) {

                            datafieldname = profileObj.get("datafieldname").toString();
                        }

                        if (profileObj.has("aliasname")) {

                            aliasname = profileObj.get("aliasname").toString();
                        }
                        if (profileObj.has("attributedisplaytext")) {

                            attributedisplaytext = profileObj.get("attributedisplaytext").toString();
                        }

                        if (profileObj.has("groupid")) {

                            groupid = profileObj.get("groupid").toString();
                        }

                        if (profileObj.has("displayorder")) {

                            displayOrder = profileObj.get("displayorder").toString();
                        }

                        if (profileObj.has("attributeconfigid")) {
                            attributeconfigid = profileObj.get("attributeconfigid").toString();

                        }

                        if (profileObj.has("isrequired")) {

                            isrequired = profileObj.get("isrequired").toString();
                        }

                        if (profileObj.has("iseditable")) {
                            iseditable = profileObj.get("iseditable").toString();

                        }

                        if (profileObj.has("enduservisibility")) {
                            enduservisibility = profileObj.get("enduservisibility").toString();

                        }

                        if (profileObj.has("uicontroltypeid")) {
                            uicontroltypeid = profileObj.get("uicontroltypeid").toString();

                        }

                        if (profileObj.has("name")) {
                            name = profileObj.get("name").toString();

                        }

                        try {
                            ProfileConfigsModel profileConfigsModel = new ProfileConfigsModel();
                            profileConfigsModel.aliasname = aliasname;
                            profileConfigsModel.attributedisplaytext = attributedisplaytext;
                            profileConfigsModel.groupid = groupid;
                            profileConfigsModel.displayorder = displayOrder;
                            profileConfigsModel.attributeconfigid = attributeconfigid;
                            profileConfigsModel.isrequired = isrequired;
                            profileConfigsModel.iseditable = iseditable;
                            profileConfigsModel.enduservisibility = enduservisibility;
                            profileConfigsModel.uicontroltypeid = uicontroltypeid;
                            profileConfigsModel.names = name;
                            profileConfigsModel.datafieldname = datafieldname;

                            profileConfigsModelList.add(profileConfigsModel);

                        } catch (SQLiteException exception) {

                            exception.printStackTrace();
                        }

                    }

                }

            }
        }
    }

    public void getOnlinePprofileDetails() {


        List<ProfileConfigsModel> profileConfigsModelLists = null;

        for (ProfileGroupModel grp : profileGroupModelList) {

            String groupID = grp.groupId;

            profileConfigsModelLists = new ArrayList<>();

            for (int i = 0; i < profileConfigsModelList.size(); i++) {

                if (groupID.equalsIgnoreCase(profileConfigsModelList.get(i).groupid)) {
                    ProfileConfigsModel profileConfigsModel = new ProfileConfigsModel();
                    profileConfigsModel = profileConfigsModelList.get(i);
                    profileConfigsModelLists.add(profileConfigsModel);
                }

                String keyName = "";

                for (int j = 0; j < profileConfigsModelLists.size(); j++) {

                    keyName = profileConfigsModelLists.get(j).datafieldname.toLowerCase().toLowerCase();

                    if (keyName.equalsIgnoreCase("picture")) {
                        profileConfigsModelLists.remove(j);
                        continue;
                    }

                    String valueName = "";
                    try {
                        valueName = switchCaseMethod(keyName);

                    } catch (NullPointerException ex) {

                        ex.printStackTrace();
                    }

                    if (valueName.contains("null")) {
                        valueName = "";
//                    continue;
                    }

                    profileConfigsModelLists.get(j).valueName = valueName;

                    hmGroupWiseConfigs.put(grp.groupname, profileConfigsModelLists);

                }
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
    }


    public String switchCaseMethod(String key) {

        String returnValue = "";

        switch (key) {
            case "nvarchar6":
                returnValue = profileDetailsModel.nvarchar6;
                break;
            case "firstname":
                returnValue = profileDetailsModel.firstname;
                break;
            case "lastname":
                returnValue = profileDetailsModel.lastname;
                break;
            case "displayname":
                returnValue = profileDetailsModel.displayname;
                break;

            case "organization":
                returnValue = profileDetailsModel.organization;
                break;

            case "email":
                returnValue = profileDetailsModel.email;
                break;

            case "usersite":
                returnValue = profileDetailsModel.usersite;
                break;

            case "addressline1":
                returnValue = profileDetailsModel.addressline1;
                break;

            case "addresscity":
                returnValue = profileDetailsModel.addresscity;
                break;

            case "addressstate":
                returnValue = profileDetailsModel.addressstate;
                break;

            case "addresszip":
                returnValue = profileDetailsModel.addresszip;
                break;

            case "addresscountry":
                returnValue = profileDetailsModel.addresscountry;
                break;

            case "phone":
                returnValue = profileDetailsModel.phone;
                break;

            case "mobilephone":
                returnValue = profileDetailsModel.mobilephone;
                break;

            case "dateofbirth":
                returnValue = profileDetailsModel.dateofbirth;
                break;

            case "highschool":
                returnValue = profileDetailsModel.highschool;
                break;

            case "college":
                returnValue = profileDetailsModel.college;
                break;

            case "highestdegree":
                returnValue = profileDetailsModel.highestdegree;
                break;

            case "jobtitle":
                returnValue = profileDetailsModel.jobtitle;
                break;

            case "businessfunction":
                returnValue = profileDetailsModel.businessfunction;
                break;

            case "payeename":
                returnValue = profileDetailsModel.payeename;
                break;


        }


        return returnValue;
    }

}

