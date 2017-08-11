package com.instancy.instancylearning.mainactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
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
import com.blankj.utilcode.util.LogUtils;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.models.AppUserModel;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.copyFile;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 5/11/2017.
 */

public class Login_activity extends Activity implements PopupMenu.OnMenuItemClickListener {

    @Bind(R.id.id_settings_txt)
    TextView settingTxt;

    @Bind(R.id.txt_pass)
    TextView imgPassword;

    @Bind(R.id.txtalert)
    TextView alertText;

    @Bind(R.id.txt_user)
    TextView imgUser;

    @Bind(R.id.id_loginbtn)
    Button btnLogin;

    @Bind(R.id.btnewuser)
    Button btnSignup;

    @Bind(R.id.btnforgot)
    Button btnForgot;

    @Bind(R.id.id_useredit)
    EditText editUserName;

    @Bind(R.id.id_passwordedit)
    EditText editPassword;

    @Bind(R.id.btntxt_facebook)
    TextView btnFacebook;

    @Bind(R.id.btntxt_twitter)
    TextView btnTwitter;

    @Bind(R.id.btntxt_google)
    TextView btnGoogle;

    @Bind(R.id.btntxt_linkedin)
    TextView btnLinkedin;

    @Bind(R.id.txt_orsocialmedia)
    TextView txtOrSocialmedia;

    @Bind(R.id.rlSocialLogin)
    LinearLayout linearLayoutOr;

    @Bind(R.id.lineview1)
    View lineView1;

    @Bind(R.id.lineview2)
    View lineView2;

    AppUserModel appUserModel;
    UiSettingsModel uiSettingsModel;
    PreferencesManager preferencesManager;
    private SVProgressHUD svProgressHUD;
    private String TAG = Login_activity.class.getSimpleName();
    String autoSignInUserName = "";
    String autoSignInPassword = "";
    boolean isAutoSignIn = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        appUserModel = AppUserModel.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();
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
//        btnLogin.setTextColor(Color.parseColor(uiSettingsModel.getHeaderTextColor()));

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


        editUserName.setText("admin@Instancy.com");
        editPassword.setText("abc");

        LogUtils.d("DBG", "KEY_WEBAPIURL " + appUserModel.getWebAPIUrl());
        LogUtils.d("KEY_SITEURL :" + appUserModel.getSiteURL());
        LogUtils.d("Authorization: " + appUserModel.getAuthHeaders());
        getMyCatalogData();
    }

    @OnClick({R.id.btntxt_facebook, R.id.btntxt_twitter, R.id.btntxt_linkedin, R.id.btntxt_google, R.id.id_settings_txt, R.id.id_loginbtn, R.id.btnewuser, R.id.btnforgot})
    public void socialLoginBtns(View view) {
        switch (view.getId()) {
            case R.id.id_loginbtn:
                loginMethod();
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
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, "Linkedin");
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

    public void loginMethod() {

        String userName = editUserName.getText().toString().trim();
        String passWord = editPassword.getText().toString().trim();

        if (userName.length() < 1) {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
        } else if (passWord.length() < 1) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        } else {

            if (isNetworkConnectionAvailable(this, -1)) {

                svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
                loginVollyWebCall(userName, passWord);
            } else {

                SweetAlert.sweetAlertNoNet(Login_activity.this, getResources().getString(R.string.alert_headtext_no_internet), getResources().getString(R.string.alert_text_check_connection));

            }

        }
    }

    public void loginVollyWebCall(final String userName, final String password) {


        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                + userName + "&Password=" + password + "&MobileSiteURL="
                + appUserModel.getSiteURL() + "&DownloadContent=&SiteID=" + appUserModel.getSiteIDValue();

        LogUtils.d("here  " + urlStr);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        svProgressHUD.dismiss();

                        Log.d("Response: ", " " + response.has("faileduserlogin"));

                        if (response.has("faileduserlogin")) {

                            SweetAlert.sweetErrorAlert(Login_activity.this, "Oops...", getResources().getString(R.string.login_failed_contact_admin));
                            alertText.setVisibility(View.VISIBLE);


                        } else if (response.has("successfulluserlogin")) {
                            alertText.setVisibility(View.GONE);
                            try {
                                JSONArray loginResponseAry = response.getJSONArray("successfulluserlogin");

                                if (loginResponseAry.length() != 0) {

                                    JSONObject jsonobj = loginResponseAry.getJSONObject(0);
                                    Log.d(TAG, "onResponse userid: " + jsonobj.get("userid"));
                                    preferencesManager.setStringValue(userName, StaticValues.KEY_USERLOGINID);
                                    preferencesManager.setStringValue(password, StaticValues.KEY_USERPASSWORD);
                                    preferencesManager.setStringValue(jsonobj.get("userid").toString(), StaticValues.KEY_USERID);
                                    preferencesManager.setStringValue(jsonobj.get("username").toString(), StaticValues.KEY_USERNAME);
                                    preferencesManager.setStringValue(jsonobj.get("userstatus").toString(), StaticValues.KEY_USERSTATUS);
                                    preferencesManager.setStringValue(jsonobj.get("image").toString(), StaticValues.KEY_USERPROFILEIMAGE);

                                    Intent intentSideMenu = new Intent(Login_activity.this, SideMenu.class);
                                    startActivity(intentSideMenu);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

//                            SweetAlert.sweetAlertSuccess(Login_activity.this, "Great...", "Success Login ");
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

        btnSignup.setVisibility(View.INVISIBLE);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAutoSignIn) {
            editUserName.setText(autoSignInUserName);
            editPassword.setText(autoSignInPassword);
            isAutoSignIn = false;
            loginMethod();
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
//                Intent intentSettings = new Intent(this, NativeSettings.class);
//                intentSettings.putExtra(StaticValues.KEY_ISLOGIN, false);
//                startActivity(intentSettings);
                return true;
        }
        return false;
    }


}

