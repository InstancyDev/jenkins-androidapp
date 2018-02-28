package com.instancy.instancylearning.mainactivities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidEmail;


public class ForgotPasswordActivity extends AppCompatActivity {

    PreferencesManager preferencesManager;
    String TAG = NativeSettings.class.getSimpleName();

    @BindView(R.id.edit_email_reset)
    EditText editResetMail;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.txtBody)
    TextView txtBody;

    @BindView(R.id.txt_user)
    TextView imgUser;


    @BindView(R.id.llForgot)
    LinearLayout llForgot;


    AppUserModel appUserModel;
    private SVProgressHUD svProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>Forgot Password</font>"));
        initilizeView(uiSettingsModel);

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

    }

    public void initilizeView(UiSettingsModel uiSettingsModel) {

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.txt_user), iconFont);
        btnSubmit.setBackgroundResource(R.drawable.round_corners);
        GradientDrawable drawable = (GradientDrawable) btnSubmit.getBackground();
        drawable.setColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        btnSubmit.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        imgUser.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        llForgot.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppLoginBGColor()));
        txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtBody.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

    }


    @OnClick({R.id.btn_submit})
    public void socialLoginBtns(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                submitPasswordReset();
                break;

        }
    }

    public void submitPasswordReset() {
        if (isValidEmail(editResetMail.getText().toString().trim())) {

            if (isNetworkConnectionAvailable(this, -1)) {
                resetPasswordVollyCall(editResetMail.getText().toString().trim());
            } else {

                Toast.makeText(this, getResources().getString(R.string.alert_text_check_connection), Toast.LENGTH_SHORT).show();
            }

        } else {

            Toast.makeText(this, getResources().getString(R.string.alert_headtext_email_notvalid), Toast.LENGTH_SHORT).show();


        }


    }

    public void resetPasswordVollyCall(String registeredEmail) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/UserStatusForPasswordReset?Login=" + registeredEmail + "&SiteURL=" + appUserModel.getSiteURL();

        LogUtils.d("here reset url  " + urlStr);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        svProgressHUD.dismiss();

                        Log.d("Response: ", " " + response.has("userstatus"));
//                        {"userstatus":[{"userid":1,"siteid":374,"active":true,"userstatus":1}

                        try {
                            JSONArray forgotResponseAry = response.getJSONArray("userstatus");

                            if (forgotResponseAry.length() != 0) {

                                JSONObject jsonobj = forgotResponseAry.getJSONObject(0);
                                Log.d(TAG, "onResponse: " + jsonobj.get("active"));

                                jsonobj.get("userid").toString();
                                jsonobj.get("siteid").toString();
                                jsonobj.get("active").toString();
                                jsonobj.get("userstatus").toString();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error: ", error.getMessage());
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


    @Override
    public void onBackPressed() {


        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Log.d("DEBUG", "onOptionsItemSelected: ");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
