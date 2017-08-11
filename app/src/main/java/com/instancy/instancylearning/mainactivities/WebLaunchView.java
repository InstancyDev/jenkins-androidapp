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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;

/**
 * Created by Upendranath on 6/29/2017 Working on InstancyLearning.
 */

public class WebLaunchView extends AppCompatActivity {

    MyLearningModel myLearningModel;
    String TAG = WebLaunchView.class.getSimpleName();
    WebView webView;
    String courseUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = (WebView) findViewById(R.id.webview);

        Bundle bundle = getIntent().getExtras();
        String courseName = "";
        if (bundle != null) {
            courseUrl = bundle.getString("COURSE_URL");
//            try {
//                courseUrl = URLEncoder.encode(courseUrl, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
            Log.d(TAG, "onCreate: in web launch " + courseUrl);
            webView.loadUrl(courseUrl);
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
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         Log.d(TAG, "shouldOverrideUrlLoading: from normal web " + url);

                                         url = url.toLowerCase();
                                         if (url.contains("ioscourseclose") || url.contains("/logoff")) {
                                             Intent intent = getIntent();
                                             intent.putExtra("myLearningDetalData", myLearningModel);
                                             setResult(RESULT_OK, intent);
                                             webView.loadUrl("about:blank");
                                             webView.stopLoading();
                                             finish();
                                         }

                                         return false;

                                     }

                                     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                         String urlReq = request.getUrl().toString();
                                         Log.d(TAG, "shouldOverrideUrlLoading: " + urlReq);
                                         urlReq = urlReq.toLowerCase();
                                         if (urlReq.contains("ioscourseclose") || urlReq.contains("/logoff")) {
                                             Intent intent = getIntent();
                                             intent.putExtra("myLearningDetalData", myLearningModel);
                                             setResult(RESULT_OK, intent);
                                             webView.loadUrl("about:blank");
                                             webView.stopLoading();
                                             finish();
                                         }
                                         return false;

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
                                         view.stopLoading();
                                         view.loadUrl("about:blank");

                                     }

                                 }
        );

        webView.loadUrl(courseUrl);
    }

//    private class MyWebViewClient extends WebViewClient {
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            Log.d(TAG, "shouldOverrideUrlLoading: from normal web " + url);
////            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
//            url = url.toLowerCase();
//            if (url.contains("ioscourseclose") || url.contains("/logoff")) {
//                Intent intent = getIntent();
//                intent.putExtra("myLearningDetalData", myLearningModel);
//                setResult(RESULT_OK, intent);
//                webView.loadUrl("about:blank");
//                webView.stopLoading();
//
//                finish();
////              }
//            }
//
//            return false;
//
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            String urlReq = request.getUrl().toString();
//            Log.d(TAG, "shouldOverrideUrlLoading: " + urlReq);
//            urlReq = urlReq.toLowerCase();
//            if (urlReq.contains("ioscourseclose") || urlReq.contains("/logoff")) {
//                Intent intent = getIntent();
//                intent.putExtra("myLearningDetalData", myLearningModel);
//                setResult(RESULT_OK, intent);
//                webView.loadUrl("about:blank");
//                webView.stopLoading();
//                finish();
////              }
//            }
//            return false;
//
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//        }
//
//        @Override
//        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//            super.onReceivedError(view, request, error);
//            view.stopLoading();
//            view.loadUrl("about:blank");
//            finish();
//        }
//    }

    @Override
    public void onBackPressed() {
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

    @Override
    public void onPause() {
        webView.onPause();
        webView.pauseTimers();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }


    @Override
    protected void onDestroy() {
        webView.destroy();

        webView = null;
        super.onDestroy();
    }
}
