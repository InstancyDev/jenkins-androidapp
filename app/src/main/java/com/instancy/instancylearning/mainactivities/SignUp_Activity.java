package com.instancy.instancylearning.mainactivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import static com.instancy.instancylearning.utils.StaticValues.BUNDLE_PASSWORD;
import static com.instancy.instancylearning.utils.StaticValues.BUNDLE_USERNAME;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


public class SignUp_Activity extends AppCompatActivity {

    PreferencesManager preferencesManager;
    String TAG = NativeSettings.class.getSimpleName();
    WebView webView;
    private SVProgressHUD svProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        webView = (WebView) findViewById(R.id.webview);
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>Sign up</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }


        final String appDefaultUrl = preferencesManager.getStringValue(StaticValues.KEY_SITEURL);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                if (url.toLowerCase().contains("home")) {
                    view.loadUrl(appDefaultUrl + "nativemobile/Sign-Up/nativesignup/true");
                } else if (url.toLowerCase().contains("autosignupnativeapp.aspx")) {
                    if (url.contains("?")) {
                        Toast.makeText(SignUp_Activity.this, "Sign up success..!!", Toast.LENGTH_SHORT).show();
                        String query = url.toLowerCase().substring(url.lastIndexOf("?") + 1);
                        String userCredentials[] = query.split("&");
                        String userName = userCredentials[0].substring(userCredentials[0].lastIndexOf("=") + 1);
                        String password = userCredentials[1].substring(userCredentials[1].lastIndexOf("=") + 1);
                        Intent loginIntent = new Intent(SignUp_Activity.this, Login_activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(BUNDLE_USERNAME, userName);
                        bundle.putString(BUNDLE_PASSWORD, password);
                        loginIntent.putExtras(bundle);
                        startActivity(loginIntent);
                    }

                } else if (url.toLowerCase().contains("success/false")) {

                    Toast.makeText(SignUp_Activity.this, "Sign up failed..!!", Toast.LENGTH_SHORT).show();
                }

                if (url.contains("&registrationcomplete=true")) {
                    Intent i4 = new Intent(SignUp_Activity.this, Login_activity.class);
                    startActivity(i4);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                svProgressHUD.dismiss();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                svProgressHUD.dismiss();
                if (url.toLowerCase().contains("sign%20in") || url.toLowerCase().contains("sign in")) {
                    Intent signinIntent = new Intent(SignUp_Activity.this, Login_activity.class);
                    startActivity(signinIntent);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Log.i("In setWebViewClient", "error code: " + errorCode + "\ndescription: " + description);
                super.onReceivedError(view, errorCode, description, failingUrl);
                svProgressHUD.dismiss();
            }
        });

//		webview.loadUrl(editTextPreference + "logoff");
        String url = appDefaultUrl + "nativemobile/Sign-Up/nativesignup/true";
        Log.d("In setWebViewClient", "url: " + url);
        if (isNetworkConnectionAvailable(this, -1)) {

            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            webView.loadUrl(url);

        } else {

            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();

        }

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
