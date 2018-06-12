package com.instancy.instancylearning.profile;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
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
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.util.Base64.encodeToString;
import static com.instancy.instancylearning.utils.StaticValues.EDUCATION_ACT;

import static com.instancy.instancylearning.utils.StaticValues.ISPROFILENAMEORIMAGEUPDATED;
import static com.instancy.instancylearning.utils.StaticValues.PROFILE_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.Utilities.getFileNameFromPath;
import static com.instancy.instancylearning.utils.Utilities.getMimeTypeFromUri;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;

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

    List<UserEducationModel> educationModelArrayList = new ArrayList<>();

    List<UserExperienceModel> experienceModelArrayList = new ArrayList<>();

    private int GALLERY = 1, CAMERA = 2;

    String finalfileName = "";


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

        profileDetailsModel = db.fetchProfileDetails(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        if (!profileDetailsModel.isProfilexist) {

            return false;
        }

        String profileIma = appUserModel.getSiteURL() + "/Content/SiteFiles/374/ProfileImages/" + profileDetailsModel.profileimagepath;

        Picasso.with(getContext()).load(profileIma).placeholder(R.drawable.defaultavatar).into(profileImage);
        Picasso.with(getContext()).load(profileIma).placeholder(R.drawable.defaultavatar).into(profileRound);

        educationModelArrayList = db.fetchUserEducationModel(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());
        experienceModelArrayList = db.fetchUserExperienceModel(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        String[] strAry = new String[2];

        strAry = extractProfileNameAndLocation(profileDetailsModel);

        userName.setText(strAry[0]);
        userLocation.setText(strAry[1]);
        ISPROFILENAMEORIMAGEUPDATED = 1;
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

        if (educationModelArrayList.size() > 0 || uiSettingsModel.getAddProfileAdditionalTab().contains("education")) {

            ProfileGroupModel profileGroupModel = new ProfileGroupModel();
            profileGroupModel.groupId = "123";
            profileGroupModel.groupname = "Education";

            hmGroupWiseConfigs.put("Education", profileConfigsModelList);

            profileGroupModelList.add(profileGroupModel);
        }

        if (experienceModelArrayList.size() > 0 || uiSettingsModel.getAddProfileAdditionalTab().contains("experience")) {

            ProfileGroupModel profileGroupModel = new ProfileGroupModel();
            profileGroupModel.groupId = "124";
            profileGroupModel.groupname = "Experience";

            hmGroupWiseConfigs.put("Experience", profileConfigsModelList);

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

        TextView uploadIconFont = (TextView) header.findViewById(R.id.uploadPhotoFont);

        Typeface iconFon = FontManager.getTypeface(context, FontManager.FONTAWESOME);

        uploadIconFont.setVisibility(View.GONE);

        uploadIconFont.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        FontManager.markAsIconContainer(header.findViewById(R.id.uploadPhotoFont), iconFon);

        userName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        userLocation.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        boolean isProfileExists = getALlProfilesDetailsFromDB();

        profileDynamicAdapter = new ProfileExpandAdapter(rootView.getContext(), experienceModelArrayList, educationModelArrayList, profileGroupModelList, hmGroupWiseConfigs);

        profileExpandableList.setAdapter(profileDynamicAdapter);
        profileExpandableList.addHeaderView(header);
        profileExpandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

//                UnComment
//                if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("123")) {
//                    editSelectedGroup("EDU", groupPosition);
//                } else if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("124")) {
//                    editSelectedGroup("EXP", groupPosition);
//                } else if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("1")) {
//                    editSelectedGroup("PER", groupPosition);
//                } else if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("2")) {
//                    editSelectedGroup("CNT", groupPosition);
//                } else if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("6")) {
//                    editSelectedGroup("BCK", groupPosition);
//                }

                return true;
            }
        });

        if (profileGroupModelList != null && profileGroupModelList.size() > 0) {
            for (int i = 0; i < profileGroupModelList.size(); i++)
                profileExpandableList.expandGroup(i);
        }

        profileExpandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

//                if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("123")) {
//                    educationClicked(groupPosition, childPosition, "EDU");
//                } else if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("124")) {
//                    educationClicked(groupPosition, childPosition, "EXP");
//                } else if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("1")) {
////                    editSelectedGroup("PER", groupPosition);
//                } else if (profileGroupModelList.get(groupPosition).groupId.equalsIgnoreCase("2")) {
////                    editSelectedGroup("CNT", groupPosition);
//                }

                return true;
            }

        });


        if (isNetworkConnectionAvailable(getContext(), -1) && PROFILE_FRAGMENT_OPENED_FIRSTTIME == 0) {

            profileWebCall(appUserModel.getUserIDValue(), false);

        } else {

            boolean checkPforile = getALlProfilesDetailsFromDB();
            if (!checkPforile) {
                Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }
        }

        initilizeView();


        return rootView;
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
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);

//        String profileIma = appUserModel.getSiteURL() + "//Content/SiteFiles/" + appUserModel.getSiteIDValue() + "/ProfileImages/" + appUserModel.getProfileImage();

//        String profileIma = appUserModel.getSiteURL() + "/Content/SiteFiles/374/ProfileImages/" + profileDetailsModel.profileimagepath;
//
//        Picasso.with(getContext()).load(profileIma).placeholder(R.drawable.defaultavatar).into(profileImage);
//        Picasso.with(getContext()).load(profileIma).placeholder(R.drawable.defaultavatar).into(profileRound);

        profileImage.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));

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

        if (!(detailsModel.displayname.equalsIgnoreCase("") || detailsModel.displayname.equalsIgnoreCase("null"))) {
            name = detailsModel.displayname;
        } else if (!detailsModel.firstname.equalsIgnoreCase("")) {
            name = detailsModel.firstname + " " + detailsModel.lastname;
        } else {
            name = "Anonymous";
        }

        if (!detailsModel.addresscity.equalsIgnoreCase("") && !detailsModel.addresscity.contains("null")) {
            if (!detailsModel.addressstate.equalsIgnoreCase("") && !detailsModel.addressstate.contains("null")) {
                location = detailsModel.addresscity + "," + detailsModel.addressstate;
            } else {
                location = detailsModel.addresscity;
            }
        } else if (!detailsModel.addressstate.equalsIgnoreCase("") && !detailsModel.addressstate.contains("null")) {
            location = detailsModel.addressstate;
        } else if (!detailsModel.addresscountry.equalsIgnoreCase("") && !detailsModel.addresscountry.contains("null")) {
            location = detailsModel.addresscountry;
        } else {
            location = "";
        }

        strAry[0] = upperCaseWords(name);
        strAry[1] = upperCaseWords(location);

        return strAry;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            Uri contentURI = data.getData();
            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contentURI);
                final String fileName = getFileNameFromPath(contentURI, context);
                final String mimeType = getMimeTypeFromUri(contentURI);
                Log.d(TAG, "onActivityResult: " + fileName);
                profileImage.setImageBitmap(bitmap);
                profileRound.setImageBitmap(bitmap);
                new CountDownTimer(1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        String endocedImageStr = convertToBase64(bitmap);
                        try {
                            encodeAttachment(fileName, endocedImageStr);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();

            }


        } else if (requestCode == CAMERA) {
//            Uri contentURI = data.getData();

            final Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profileRound.setImageBitmap(thumbnail);
            profileImage.setImageBitmap(thumbnail);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            final String fileName = "IMG_" + timeStamp + "_.jpg";

//            saveImage(thumbnail);
//            final String fileName = getFileNameFromPath(contentURI, context);
//            final String mimeType = getMimeTypeFromUri(contentURI);
//            Log.d(TAG, "onActivityResult: " + fileName);


            new CountDownTimer(1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    String endocedImageStr = convertToBase64(thumbnail);
                    try {
                        encodeAttachment(fileName, endocedImageStr);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }

        if (requestCode == EDUCATION_ACT && resultCode == RESULT_OK) {
            if (data != null) {
                boolean refresh = data.getBooleanExtra("REFRESH", false);
                if (refresh) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {

                        profileWebCall(appUserModel.getUserIDValue(), true);
                    } else {

                        Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

                    }


                }
            }
        }

    }

    private String convertToBase64(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] byteArrayImage = baos.toByteArray();

        return encodeToString(byteArrayImage, Base64.NO_WRAP);
    }


    public void encodeAttachment(String fileName, String imageFileStrr) throws JSONException {


        if (imageFileStrr.length() < 10) {
            Toast.makeText(context, "Invalid attached file", Toast.LENGTH_SHORT).show();
        } else {

            Log.d(TAG, "validateNewForumCreation: " + imageFileStrr);

            if (isNetworkConnectionAvailable(context, -1)) {

                String replaceDataString = imageFileStrr.replace("\"", "\\\"");
                String addQuotes = ('"' + replaceDataString + '"');

                sendImageTOServer(addQuotes, fileName);
            } else {
                Toast.makeText(context, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }
        }


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
                                profileDynamicAdapter.refreshList(experienceModelArrayList, educationModelArrayList, profileGroupModelList, hmGroupWiseConfigs);

                                if (profileGroupModelList != null && profileGroupModelList.size() > 0) {
                                    for (int i = 0; i < profileGroupModelList.size(); i++)
                                        profileExpandableList.expandGroup(i);
                                }

                                boolean isPresent = db.checkChoiceTxtPresent();

                                if (!isPresent) {
                                 }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                }

                if (requestType.equalsIgnoreCase("DROPDOWNLIST")) {
                    if (response != null) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("table5");

                            Log.d(TAG, "Volley JSON post" + jsonArray.length());

                            db.injectProfielFieldOptions(jsonArray);

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
//                showPictureDialog();
                break;
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    public void sendImageTOServer(final String postData, String fileName) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        //sending image to server

        String apiString = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileSyncProfileImage?fileName=" + fileName + "&siteURL=" + appUserModel.getSiteURL() + "&UserID=" + appUserModel.getUserIDValue();

        Log.d(TAG, "sendImageTOServer: " + apiString);

        final StringRequest request = new StringRequest(Request.Method.POST, apiString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("true")) {
                    Toast.makeText(context, "    Profile Picture Successfully Updated   ", Toast.LENGTH_SHORT).show();
//                    profileWebCall(appUserModel.getUserIDValue(), true);

                    ISPROFILENAMEORIMAGEUPDATED = 1;
                } else {
                    Toast.makeText(context, "   Profile Picture failed to Update    ", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return postData.getBytes();
            }


            ;

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

        PROFILE_FRAGMENT_OPENED_FIRSTTIME = 2;

    }


    public void editSelectedGroup(String typeString, int groupPosition) {

        if (typeString.equalsIgnoreCase("EXP")) {
            Intent intentDetail = new Intent(context, Experience_activity.class);
            intentDetail.putExtra("isfromGroup", true);
            startActivityForResult(intentDetail, EDUCATION_ACT);

        } else if (typeString.equalsIgnoreCase("EDU")) {
            Intent intentDetail = new Intent(context, Education_activity.class);
            intentDetail.putExtra("isfromGroup", true);
            startActivityForResult(intentDetail, EDUCATION_ACT);
        } else if (typeString.equalsIgnoreCase("PER")) {

//            Log.d(TAG, "onGroupClick: personal" + hmGroupWiseConfigs.get(profileGroupModelList.get(groupPosition).groupname).get(groupPosition).valueName);

            Log.d(TAG, "onGroupClick: groupid" + hmGroupWiseConfigs.get(profileGroupModelList.get(groupPosition).groupname).get(groupPosition));

            List<ProfileConfigsModel> profileConfigsModelArrayList = hmGroupWiseConfigs.get(profileGroupModelList.get(groupPosition).groupname);

            Intent intentDetail = new Intent(context, Personalinfo_activity.class);
            intentDetail.putExtra("fromWhichGroup", profileGroupModelList.get(groupPosition).groupId);
            intentDetail.putExtra("profileConfigsModelArrayList", (Serializable) profileConfigsModelArrayList);
            intentDetail.putExtra("GroupName", profileGroupModelList.get(groupPosition).groupname);
            startActivityForResult(intentDetail, EDUCATION_ACT);

        } else if (typeString.equalsIgnoreCase("CNT")) {

            List<ProfileConfigsModel> profileConfigsModelArrayList = hmGroupWiseConfigs.get(profileGroupModelList.get(groupPosition).groupname);

//            Log.d(TAG, "editSelectedGroup: " + profileConfigsModelArrayList.size());

            if (profileConfigsModelArrayList != null && profileConfigsModelArrayList.size() > 0) {

                Intent intentDetail = new Intent(context, Personalinfo_activity.class);
                intentDetail.putExtra("fromWhichGroup", profileGroupModelList.get(groupPosition).groupId);
                intentDetail.putExtra("GroupName", profileGroupModelList.get(groupPosition).groupname);
                intentDetail.putExtra("profileConfigsModelArrayList", (Serializable) profileConfigsModelArrayList);
                startActivityForResult(intentDetail, EDUCATION_ACT);

            }

        } else if (typeString.equalsIgnoreCase("BCK")) {

            List<ProfileConfigsModel> profileConfigsModelArrayList = hmGroupWiseConfigs.get(profileGroupModelList.get(groupPosition).groupname);

            Log.d(TAG, "editSelectedGroup: " + profileConfigsModelArrayList.size());

            Intent intentDetail = new Intent(context, Personalinfo_activity.class);
            intentDetail.putExtra("fromWhichGroup", profileGroupModelList.get(groupPosition).groupId);
            intentDetail.putExtra("GroupName", profileGroupModelList.get(groupPosition).groupname);
            intentDetail.putExtra("profileConfigsModelArrayList", (Serializable) profileConfigsModelArrayList);
            startActivityForResult(intentDetail, EDUCATION_ACT);

        }

    }

    private void educationClicked(int groupPosition, int childPosition, String typeSelected) {

        if (typeSelected.equalsIgnoreCase("EDU")) {

            UserEducationModel userEducationModel = educationModelArrayList.get(childPosition);

            Intent intentDetail = new Intent(context, Education_activity.class);
            intentDetail.putExtra("isfromGroup", false);
            intentDetail.putExtra("userEducationModel", userEducationModel);
            startActivityForResult(intentDetail, EDUCATION_ACT);

        } else if (typeSelected.equalsIgnoreCase("EXP")) {

            UserExperienceModel userExperienceModel = experienceModelArrayList.get(childPosition);

            Intent intentDetail = new Intent(context, Experience_activity.class);
            intentDetail.putExtra("isfromGroup", false);
            intentDetail.putExtra("userExperienceModel", userExperienceModel);
            startActivityForResult(intentDetail, EDUCATION_ACT);

        }

    }

    private void countriesWebApiCall(String userId) {

//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetUserDetails?UserID=" + userId + "&siteURL=" + appUserModel.getSiteURL() + "&siteid=" + appUserModel.getSiteIDValue();

        urlStr = urlStr.replaceAll(" ", "%20");

        Log.d(TAG, "profileWebCall: " + urlStr);

        vollyService.getJsonObjResponseVolley("DROPDOWNLIST", urlStr, appUserModel.getAuthHeaders());

    }


}


