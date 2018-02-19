package com.instancy.instancylearning.mainactivities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by Upendranath on 4/11/2017.
 */

public class SocialWebLoginsActivity extends AppCompatActivity {

    AdvancedWebView webView;
    String url = "";
    PreferencesManager preferencesManager;
    SVProgressHUD svProgressHUD;
    AppUserModel appUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socialweblogins);
        webView = (AdvancedWebView) findViewById(R.id.webloginactivity);
        svProgressHUD = new SVProgressHUD(this);
        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();

        Bundle bundle = getIntent().getExtras();
        String actionBaritle = "Login";
        boolean isFromAttachment = false;
        if (bundle != null) {
            isFromAttachment = bundle.getBoolean("ATTACHMENT", false);
            if (isFromAttachment) {
                url = bundle.getString(StaticValues.KEY_SOCIALLOGIN);
            } else {
                url = appUserModel.getSiteURL().concat(bundle.getString(StaticValues.KEY_SOCIALLOGIN));
            }

            actionBaritle = bundle.getString(StaticValues.KEY_ACTIONBARTITLE);

        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + actionBaritle + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        webView.setScrollbarFadingEnabled(false);
//        webView.getSettings().setSupportZoom(true);
//        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
//        webView.getSettings()
//                .setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setDomStorageEnabled(true);

        WebSettings webSettings = this.webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.getUseWideViewPort();
        webSettings.setDatabaseEnabled(true);
        webSettings.setSaveFormData(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSupportZoom(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setBackgroundColor(getResources().getColor(R.color.colorFaceBookSilver));
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        webView.loadUrl(url);

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {

//				ShowAlert.alertOK(SocialWebLoginsActivity.this, R.string.alert,
//						message, R.string.alert_btntext_OK, false);

                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {

//				ShowAlert.alertOK(SocialWebLoginsActivity.this, R.string.alert,
//						message, R.string.alert_btntext_OK, false);
                return true;
            }

            ;

            @Override
            public void onProgressChanged(WebView view, int progress) {

            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);


                Log.d("SCL", "Social Login " + url);
                if (url.toLowerCase().contains("autosocialloginnativeapp.aspx")) {
                    if (url.contains("?")) {

                        Toast.makeText(SocialWebLoginsActivity.this, "Login success..!!", Toast.LENGTH_SHORT).show();

                        String query = url.toLowerCase().substring(
                                url.lastIndexOf("?") + 1);
                        String userCredentials[] = query.split("&");

                        String pasData = userCredentials[1].toString();

                        String tempPassToken[] = pasData.split("=");

                        String pasDataF = tempPassToken[1].toString();

                        String pasFInalToken[] = pasDataF.split("#");

                        String userName = userCredentials[0]
                                .substring(userCredentials[0].lastIndexOf("=") + 1);

                        String password = pasFInalToken[0].toString();

                        Log.d("SCL", "Social Login pasData " + password);

                        // + 1);

                        // Log.d("SCL", "Social query " + query);
                        // Log.d("SCL","Social Login userName "+ userName);
                        // Log.d("SCL","Social Login password "+ password);

                        Intent loginIntent = new
                                Intent(SocialWebLoginsActivity.this, Login_activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(StaticValues.BUNDLE_USERNAME, userName);
                        bundle.putString(StaticValues.BUNDLE_PASSWORD, password);
                        loginIntent.putExtras(bundle);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);

                    }

                } else if (url.toLowerCase().contains("success/false")) {


                }

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                svProgressHUD.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Log.i("In setWebViewClient", "error code: " + errorCode
                        + "\ndescription: " + description);
                super.onReceivedError(view, errorCode, description, failingUrl);
                svProgressHUD.dismiss();

            }
        });
        // SharedPreferences prefs = PreferenceManager
        // .getDefaultSharedPreferences(getBaseContext());
        // editTextPreference =
        // prefs.getString("url",getString(R.string.app_default_url));
        // webView.loadUrl(editTextPreference + "logoff");
        // webview.loadUrl(editTextPreference + "nativemobile/Sign%20Up");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
