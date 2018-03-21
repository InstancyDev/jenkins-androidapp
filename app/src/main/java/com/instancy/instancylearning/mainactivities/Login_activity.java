package com.instancy.instancylearning.mainactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.blankj.utilcode.util.ToastUtils;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.utils.SweetAlert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.StaticValues.CATALOG_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.Utilities.copyFile;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 5/11/2017.
 */

public class Login_activity extends Activity implements PopupMenu.OnMenuItemClickListener {

    @BindView(R.id.id_settings_txt)
    TextView settingTxt;

    @BindView(R.id.txt_pass)
    TextView imgPassword;

    @BindView(R.id.txtalert)
    TextView alertText;

    @BindView(R.id.txt_user)
    TextView imgUser;

    @BindView(R.id.id_loginbtn)
    Button btnLogin;

    @BindView(R.id.btnewuser)
    Button btnSignup;

    @BindView(R.id.btnforgot)
    Button btnForgot;

    @BindView(R.id.id_useredit)
    EditText editUserName;

    @BindView(R.id.id_passwordedit)
    EditText editPassword;

    @BindView(R.id.btntxt_facebook)
    TextView btnFacebook;

    @BindView(R.id.btntxt_twitter)
    TextView btnTwitter;

    @BindView(R.id.btntxt_google)
    TextView btnGoogle;

    @BindView(R.id.btntxt_linkedin)
    TextView btnLinkedin;

    @BindView(R.id.txt_orsocialmedia)
    TextView txtOrSocialmedia;

    @BindView(R.id.rlSocialLogin)
    LinearLayout linearLayoutOr;

    @BindView(R.id.lineview1)
    View lineView1;

    @BindView(R.id.lineview2)
    View lineView2;

    AppUserModel appUserModel;
    UiSettingsModel uiSettingsModel;
    PreferencesManager preferencesManager;
    private SVProgressHUD svProgressHUD;
    private String TAG = Login_activity.class.getSimpleName();
    String autoSignInUserName = "";
    String autoSignInPassword = "";
    boolean isAutoSignIn = false;
    DatabaseHandler db;
    AppController appController;
    VollyService vollyService;
    IResult resultCallback = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        db = new DatabaseHandler(this);
        appUserModel = AppUserModel.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        initVolleyCallback();

        vollyService = new VollyService(resultCallback, getApplicationContext());

        appController = AppController.getInstance();
        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();
        VolleySingleton.getInstance(this);
        ButterKnife.bind(this);
        initilizeView();
        hideOrShowBtns();
        svProgressHUD = new SVProgressHUD(this);

        Bundle bundleAutoSignIn = this.getIntent().getExtras();
        if (bundleAutoSignIn != null) {
            autoSignInUserName = bundleAutoSignIn.getString(StaticValues.BUNDLE_USERNAME, "");
            autoSignInPassword = bundleAutoSignIn.getString(StaticValues.BUNDLE_PASSWORD, "");
            if (isValidString(autoSignInUserName)
                    && isValidString(autoSignInPassword)) {
                isAutoSignIn = true;
            }
        }
    }

    public void initilizeView() {
//        btnLogin.setBackgroundResource(R.drawable.round_corners);
//        GradientDrawable drawable = (GradientDrawable) btnLogin.getBackground();
//        drawable.setColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));



        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());

        btnLogin.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        btnLogin.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));

        imgPassword.setTextColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));
        imgUser.setTextColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));

        editPassword.setTextColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));
        editUserName.setTextColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));

        editPassword.setHintTextColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));
        editUserName.setHintTextColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));

        btnLogin.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnLogin.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        btnSignup.setTextColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));
        btnForgot.setTextColor(Color.parseColor(uiSettingsModel.getAppLoginTextolor()));


//       uncomment for backgroundcolor purpose

        View someView = findViewById(R.id.login_layout);
        someView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppLoginBGColor()));

        if ((getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.cle_academy))) || (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.crop_life)))|| (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.ppdlife)))) {
//            btnSignup.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

            settingTxt.setVisibility(View.INVISIBLE);
        }
        else {

        settingTxt.setVisibility(View.VISIBLE);
        }


        Drawable drawablePass = editPassword.getBackground(); // get current EditText drawable
        drawablePass.setColorFilter(Color.parseColor(uiSettingsModel.getAppButtonBgColor()), PorterDuff.Mode.SRC_ATOP); // change the drawable color

        Drawable drawableUser = editUserName.getBackground(); // get current EditText drawable
        drawableUser.setColorFilter(Color.parseColor(uiSettingsModel.getAppButtonBgColor()), PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT > 16) {
            editPassword.setBackground(drawablePass); // set the new drawable to EditText
            editUserName.setBackground(drawableUser); // set the new drawable to EditText

        } else {
            editPassword.setBackgroundDrawable(drawablePass); // use setBackgroundDrawable because setBackground r
            editUserName.setBackgroundDrawable(drawableUser); // use setBackgroundDrawable because setBackground requ
        }

        appUserModel = AppUserModel.getInstance();
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.id_settings_txt), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.btntxt_facebook), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.btntxt_twitter), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.btntxt_google), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.btntxt_linkedin), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.txt_pass), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.txt_user), iconFont);

        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));

        editUserName.setText(preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID));
        editPassword.setText(preferencesManager.getStringValue(StaticValues.KEY_USERPASSWORD));

//        editUserName.setText("admin@Instancy.com");
//        editPassword.setText("abc");

        getMyCatalogData();
    }

    @OnClick({R.id.btntxt_facebook, R.id.btntxt_twitter, R.id.btntxt_linkedin, R.id.btntxt_google, R.id.id_settings_txt, R.id.id_loginbtn, R.id.btnewuser, R.id.btnforgot})
    public void socialLoginBtns(View view) {
        switch (view.getId()) {
            case R.id.id_loginbtn:
                try {
                    loginMethod();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btntxt_google:
                if (isNetworkConnectionAvailable(this, -1)) {
                    methodCallByTag(1);
                } else {

                    Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btntxt_twitter:
                if (isNetworkConnectionAvailable(this, -1)) {
                    methodCallByTag(2);
                } else {

                    Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btntxt_facebook:
                if (isNetworkConnectionAvailable(this, -1)) {
                    methodCallByTag(3);
                } else {

                    Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btntxt_linkedin:
                if (isNetworkConnectionAvailable(this, -1)) {
                    methodCallByTag(4);
                } else {

                    Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.id_settings_txt:
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.settings_menu);
                popupMenu.show();
                break;
            case R.id.btnewuser:
                methodCallByTag(5);
                break;
            case R.id.btnforgot:
                methodCallByTag(6);
//                initiatePopupWindow();
                break;
        }

    }

    public void methodCallByTag(int tag) {

        Intent intentSocial = new Intent(this, SocialWebLoginsActivity.class);
        intentSocial.putExtra("ATTACHMENT", true);

        switch (tag) {

            case 1:
                intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, ApiConstants.googleUrl);
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, "Google");
                startActivity(intentSocial);
                break;
            case 2:
                intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, ApiConstants.twitterUrl);
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, "Twitter");
                startActivity(intentSocial);
                break;
            case 3:
                intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, ApiConstants.facebookUrl);
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, "Facebook");
                startActivity(intentSocial);
                break;
            case 4:
                intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, ApiConstants.linkedInUrl);
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, "Linkedin");//9963014569
                startActivity(intentSocial);
                break;
            case 5:
                Intent intentSignup = new Intent(this, SignUp_Activity.class);
                startActivity(intentSignup);
                break;
            case 6:
                Intent intentForgot = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intentForgot);
                break;
            default:
                break;
        }

    }

    public void loginMethod() throws JSONException {

        String userName = editUserName.getText().toString().trim();
        String passWord = editPassword.getText().toString().trim();


        if (userName.length() < 1) {
            Toast.makeText(this, "  Enter Username  ", Toast.LENGTH_SHORT).show();
        } else if (passWord.length() < 1) {
            Toast.makeText(this, "  Enter Password  ", Toast.LENGTH_SHORT).show();
        } else {

            if (isNetworkConnectionAvailable(this, -1)) {
                svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
                loginVollyWebCall(userName, passWord);
            } else {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", userName);
                    jsonObject.put("password", passWord);
                    jsonObject.put("siteid", "374");
                    jsonObject.put("siteurl", preferencesManager.getStringValue(StaticValues.KEY_SITEURL));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject jsonReturn = null;
                try {
                    jsonReturn = db.checkOfflineUserCredintials(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonReturn.length() != 0) {

                    appController.setAlreadyViewd(true);
                    preferencesManager.setStringValue("true", StaticValues.KEY_HIDE_ANNOTATION);
                    preferencesManager.setStringValue(userName, StaticValues.KEY_USERLOGINID);
                    preferencesManager.setStringValue(passWord, StaticValues.KEY_USERPASSWORD);
                    preferencesManager.setStringValue(jsonReturn.get("userid").toString(), StaticValues.KEY_USERID);
                    preferencesManager.setStringValue(jsonReturn.get("displayname").toString(), StaticValues.KEY_USERNAME);
                    preferencesManager.setStringValue(jsonReturn.get("userstatus").toString(), StaticValues.KEY_USERSTATUS);
                    MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;
                    CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;
                    Intent intentSideMenu = new Intent(Login_activity.this, SideMenu.class);
                    intentSideMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentSideMenu);

                } else {
                    appController.setAlreadyViewd(false);
                    preferencesManager.setStringValue("false", StaticValues.KEY_HIDE_ANNOTATION);
                    SweetAlert.sweetAlertNoNet(Login_activity.this, getResources().getString(R.string.alert_headtext_no_internet), getResources().getString(R.string.alert_text_check_connection));
                }
            }

        }
    }

    public void loginVollyWebCall(final String userName, final String password) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                + userName + "&Password=" + password + "&MobileSiteURL="
                + appUserModel.getSiteURL() + "&DownloadContent=&SiteID=" + appUserModel.getSiteIDValue();

        Log.d(TAG, "main login : " + urlStr);

        urlStr = urlStr.replaceAll(" ", "%20");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        svProgressHUD.dismiss();
                        Log.d("Response: ", " " + response.has("faileduserlogin"));
                        if (response.has("faileduserlogin")) {
//                            SweetAlert.sweetErrorAlert(Login_activity.this, "Oops...", getResources().getString(R.string.login_failed_contact_admin));
                            alertText.setVisibility(View.VISIBLE);

                        } else if (response.has("successfulluserlogin")) {
                            alertText.setVisibility(View.GONE);
                            try {
                                JSONArray loginResponseAry = response.getJSONArray("successfulluserlogin");
                                if (loginResponseAry.length() != 0) {


                                    JSONObject jsonobj = loginResponseAry.getJSONObject(0);
                                    JSONObject jsonObject = new JSONObject();
                                    String userId = jsonobj.get("userid").toString();
                                    profileWebCall(userId);
                                    jsonObject.put("userid", jsonobj.get("userid").toString());
                                    jsonObject.put("orgunitid", jsonobj.get("orgunitid"));
                                    jsonObject.put("userstatus", jsonobj.get("userstatus"));
                                    jsonObject.put("displayname", jsonobj.get("username"));
                                    jsonObject.put("siteid", jsonobj.get("siteid"));
                                    jsonObject.put("username", userName);
                                    jsonObject.put("password", password);
                                    jsonObject.put("siteurl", appUserModel.getSiteURL());

                                    db.insertUserCredentialsForOfflineLogin(jsonObject);

                                    Log.d(TAG, "onResponse userid: " + jsonobj.get("userid"));
                                    preferencesManager.setStringValue(userName, StaticValues.KEY_USERLOGINID);
                                    preferencesManager.setStringValue(password, StaticValues.KEY_USERPASSWORD);
                                    preferencesManager.setStringValue(jsonobj.get("userid").toString(), StaticValues.KEY_USERID);
                                    preferencesManager.setStringValue(jsonobj.get("username").toString(), StaticValues.KEY_USERNAME);
                                    preferencesManager.setStringValue(jsonobj.get("userstatus").toString(), StaticValues.KEY_USERSTATUS);
                                    preferencesManager.setStringValue(jsonobj.get("image").toString(), StaticValues.KEY_USERPROFILEIMAGE);


                                    appUserModel.setUserIDValue(userId);
                                    MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;

                                    Intent intentSideMenu = new Intent(Login_activity.this, SideMenu.class);
                                    intentSideMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intentSideMenu);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
//                        svProgressHUD.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void profileWebCall(String userId) {

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetUserDetailsv1?UserID=" + userId + "&siteURL=" + appUserModel.getSiteURL() + "&siteid=" + appUserModel.getSiteIDValue();

        urlStr = urlStr.replaceAll(" ", "%20");

        Log.d(TAG, "profileWebCall: " + urlStr);

        vollyService.getJsonObjResponseVolley("PROFILEDATA", urlStr, appUserModel.getAuthHeaders());

    }

    void getMyCatalogData() {
        File tepfile = new File(getExternalFilesDir(null)
                + "/Mydownloads/Content");
        if (!tepfile.exists()) {
            boolean success = (new File(getExternalFilesDir(null)
                    + "/Mydownloads")).mkdirs();
            copyAssets();
            String zipFile = getExternalFilesDir(null)
                    + "/Mydownloads/output.zip";
            String unzipLocation = getExternalFilesDir(null)
                    + "/Mydownloads/";
            UnZip d = new UnZip(zipFile, unzipLocation);
        }
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open("Content.zip");
            out = new FileOutputStream(getExternalFilesDir(null)
                    + "/Mydownloads/" + "output.zip");
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("copyAssets", e.getMessage());
        }
    }

    public void hideOrShowBtns() {

        if ((getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.development)))) {

            settingTxt.setVisibility(View.VISIBLE);
        }

        if (uiSettingsModel.getIsFaceBook().equalsIgnoreCase("false")) {
            btnFacebook.setVisibility(View.GONE);
        } else {
            btnFacebook.setVisibility(View.VISIBLE);
        }
        if (uiSettingsModel.getIsLinkedIn().equalsIgnoreCase("false")) {
            btnLinkedin.setVisibility(View.GONE);
        } else {
            btnLinkedin.setVisibility(View.VISIBLE);
        }
        if (uiSettingsModel.getIsTwitter().equalsIgnoreCase("false")) {
            btnTwitter.setVisibility(View.GONE);
        } else {
            btnTwitter.setVisibility(View.VISIBLE);
        }
        if (uiSettingsModel.getIsGoogle().equalsIgnoreCase("false")) {
            btnGoogle.setVisibility(View.GONE);
        } else {
            btnGoogle.setVisibility(View.VISIBLE);
        }
        if (uiSettingsModel.getIsGoogle().equalsIgnoreCase("false") || uiSettingsModel.getIsFaceBook().equalsIgnoreCase("false") || uiSettingsModel.getIsLinkedIn().equalsIgnoreCase("false") || uiSettingsModel.getIsTwitter().equalsIgnoreCase("false")) {
            txtOrSocialmedia.setVisibility(View.GONE);
            linearLayoutOr.setVisibility(View.GONE);
            lineView1.setVisibility(View.GONE);
            lineView2.setVisibility(View.GONE);
        } else {
            txtOrSocialmedia.setVisibility(View.VISIBLE);
            linearLayoutOr.setVisibility(View.VISIBLE);
            lineView2.setVisibility(View.VISIBLE);
            lineView1.setVisibility(View.VISIBLE);
        }

        if (uiSettingsModel.getSelfRegistrationAllowed().equalsIgnoreCase("true")) {

            btnSignup.setVisibility(View.VISIBLE);

        } else {
            btnSignup.setVisibility(View.INVISIBLE);

        }

        btnForgot.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAutoSignIn) {
            editUserName.setText(autoSignInUserName);
            editPassword.setText(autoSignInPassword);
            isAutoSignIn = false;
            try {
                loginMethod();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.id_settings:

                return true;
        }
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_settings:
                Intent intentSettings = new Intent(this, NativeSettings.class);
                intentSettings.putExtra(StaticValues.KEY_ISLOGIN, false);
                startActivity(intentSettings);
                return true;
        }
        return false;
    }

    void initVolleyCallback() {

        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                if (requestType.equalsIgnoreCase("PROFILEDATA")) {
                    if (response != null) {

                        try {

                            db.InjectAllProfileDetails(response, appUserModel.getUserIDValue());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }

//                    Intent intentSideMenu = new Intent(Login_activity.this, SideMenu.class);
//                    intentSideMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intentSideMenu);
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

}

