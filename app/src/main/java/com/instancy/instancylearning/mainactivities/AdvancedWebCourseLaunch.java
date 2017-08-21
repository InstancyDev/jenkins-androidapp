package com.instancy.instancylearning.mainactivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.interfaces.LRSJavaScriptInterface;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by Upendranath on 6/29/2017 Working on InstancyLearning.
 * https://github.com/delight-im/Android-AdvancedWebView
 */

public class AdvancedWebCourseLaunch extends AppCompatActivity {

    private AdvancedWebView adWebView;
    MyLearningModel myLearningModel;
    String TAG = AdvancedWebCourseLaunch.class.getSimpleName();
    String prevStatus = "";
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advancedweb_courselaunch);
        adWebView = (AdvancedWebView) findViewById(R.id.advanced_coursewbview);
//        adWebView.setListener(this, this);
        databaseHandler = new DatabaseHandler(this);
        Bundle bundle = getIntent().getExtras();
        String courseUrl;
        String courseName = "";
        if (bundle != null) {
            courseUrl = bundle.getString("COURSE_URL");
//            try {
//                courseUrl = URLEncoder.encode(courseUrl, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            adWebView.loadUrl(courseUrl);
            myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
            Log.d(TAG, "onCreate:AdvancedWebCourseLaunch " + courseUrl);

            courseName = myLearningModel.getCourseName();
        }


        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
            UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
            getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + courseName + "</font>"));
            try {
                final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
                upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }


        WebSettings webSettings = this.adWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.getUseWideViewPort();
        webSettings.setDatabaseEnabled(true);
        webSettings.setSaveFormData(true);
        adWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        adWebView.setScrollbarFadingEnabled(false);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSupportZoom(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // Add Class in js

        adWebView.addJavascriptInterface(new LRSJavaScriptInterface(this, myLearningModel), "MobileJSInterface");

        adWebView.setWebViewClient(new WebViewClient() {
                                       @Override
                                       public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                           Log.d(TAG, "shouldOverrideUrlLoading: from normal web " + url);

                                           url = url.toLowerCase();
                                           if (url.contains("ioscourseclose") || url.contains("/logoff") || url.contains("home.html")) {
                                               Intent intent = getIntent();
                                               intent.putExtra("myLearningDetalData", myLearningModel);
                                               setResult(RESULT_OK, intent);
                                               view.stopLoading();
                                               databaseHandler.saveCourseClose(url, myLearningModel);
                                               finish();
                                           }
                                           else if (url.contains("iosobjectclose=true")) {

                                               databaseHandler.saveCourseClose(url, myLearningModel);
                                           }
                                           return true;

                                       }

                                       @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                       @Override
                                       public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                           String urlReq = request.getUrl().toString();
                                           Log.d(TAG, "shouldOverrideUrlLoading: newmwthod " + urlReq);
                                           urlReq = urlReq.toLowerCase();
                                           if (urlReq.contains("ioscourseclose") || urlReq.contains("/logoff") || urlReq.contains("home.html")) {
                                               Intent intent = getIntent();
                                               intent.putExtra("myLearningDetalData", myLearningModel);
                                               setResult(RESULT_OK, intent);
                                               view.stopLoading();
                                               finish();
                                           }
                                           return true;
                                       }

                                       @Override
                                       public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                           super.onPageStarted(view, url, favicon);

                                       }

                                       @Override
                                       public void onPageFinished(WebView view, String url) {
                                           super.onPageFinished(view, url);
                                       }

                                       @Override
                                       public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                                           super.onReceivedError(view, request, error);

                                       }

                                   }
        );


        adWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);

            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);

            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        return;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = getIntent();
                intent.putExtra("myLearningDetalData", myLearningModel);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
