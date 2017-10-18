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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.interfaces.LRSJavaScriptInterface;
import com.instancy.instancylearning.interfaces.hideProgressListner;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;

/**
 * Created by Upendranath on 6/29/2017 Working on InstancyLearning.
 * https://github.com/delight-im/Android-AdvancedWebView
 */

public class AdvancedWebCourseLaunch extends AppCompatActivity {

    private WebView adWebView;
    MyLearningModel myLearningModel;
    String TAG = AdvancedWebCourseLaunch.class.getSimpleName();
    String prevStatus = "";
    DatabaseHandler databaseHandler;
    boolean isOffline = false;
    public SVProgressHUD svProgressHUD;
    hideProgressListner hideProgressListner = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advancedweb_courselaunch);
        adWebView = (WebView) findViewById(R.id.normalwebview);
        clearWebViewAbsolutely(adWebView);
        svProgressHUD = new SVProgressHUD(this);
//        adWebView.setListener(this, this);
        databaseHandler = new DatabaseHandler(this);
        Bundle bundle = getIntent().getExtras();
        String courseUrl;
        String courseName = "";

        if (bundle != null) {
            courseUrl = bundle.getString("COURSE_URL");
//            courseUrl = "http://stagingmontessori.instancysoft.com//remote/ajaxlaunchpage.aspx?path=/content/sitefiles/27fb2122-0928-4571-80c6-0e10844013ca/en-us/index_lms.html&coursename=m6_s1_intro_to_arithmetic&contentid=27fb2122-0928-4571-80c6-0e10844013ca&objecttypeid=26&cantrack=yes&scoid=514&eventkey=&eventtype=&trackinguserid=1";
//            try {
//                courseUrl = URLEncoder.encode(courseUrl, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
//            showSvProgressAlert(svProgressHUD);
            myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
            Log.d(TAG, "onCreate:AdvancedWebCourseLaunch " + courseUrl);
            clearWebViewAbsolutely(adWebView);
            courseName = myLearningModel.getCourseName();


            if (courseUrl.startsWith("file:///")) {
                isOffline = true;

            } else {
                isOffline = false;
            }
            if (savedInstanceState != null) {
                adWebView.restoreState(savedInstanceState);
            } else {
                adWebView.loadUrl(courseUrl);
            }
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
//        closeSvProgress();
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
        adWebView.setBackgroundColor(getResources().getColor(R.color.colorFaceBookSilver));
        webSettings.setMediaPlaybackRequiresUserGesture(false);


        adWebView.getSettings().setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        // Add Class in js

        LRSJavaScriptInterface lrsInterface = new LRSJavaScriptInterface(this, myLearningModel, this, hideProgressListner, isOffline);
        lrsInterface.hideProgressListner = new hideProgressListner() {
            @Override
            public void statusUpdateFromServer() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        svProgressHUD.dismiss();
                    }
                });

            }
        };

//        adWebView.addJavascriptInterface(new LRSJavaScriptInterface(this, myLearningModel, this, hideProgressListner, this), "MobileJSInterface");
        adWebView.addJavascriptInterface(lrsInterface, "MobileJSInterface");

        adWebView.setWebViewClient(new WebViewClient() {

                                       @Override
                                       public void onLoadResource(WebView view, String url) {
                                           if (myLearningModel.getObjecttypeId().equalsIgnoreCase("26")) {
                                               if (url.toLowerCase().contains(
                                                       ".html?ioscourseclose=true")) {
                                                   adWebView.stopLoading();
                                                   finish();

                                               } else if (url.toLowerCase().contains(
                                                       ".html?lrsstatements=true")) {

                                               } else if (url.toLowerCase().contains(".html")) {
//                                                   CMIModel cmiDetails = new CMIModel();
//                                                   cmiDetails.set_datecompleted("");
//                                                   cmiDetails.set_siteId(myLearningModel.getSiteID());
//                                                   cmiDetails.set_userId(Integer.parseInt(myLearningModel.getUserID()));
//                                                   cmiDetails.set_scoId(Integer.parseInt(myLearningModel.getScoId()));
//                                                   cmiDetails.set_location(url);
//                                                   cmiDetails.set_startdate(myLearningModel.getCreatedDate());
//                                                   cmiDetails.set_isupdate("false");
//                                                   cmiDetails.set_status(getString(R.string.metadata_status_progress));
//                                                   cmiDetails.set_datecompleted("");
//                                                   cmiDetails.set_seqNum("0");
//                                                   cmiDetails.set_objecttypeid(myLearningModel.getObjecttypeId());
//                                                   cmiDetails.set_sitrurl(myLearningModel.getSiteURL());
//                                                   int seqNo = databaseHandler.insertCMI(cmiDetails, true);


                                               }

                                           }
                                       }

                                       @Override
                                       public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                           Log.d(TAG, "shouldOverrideUrlLoading: from normal web " + url);
                                           url = url.toLowerCase();

//                                           if (url.contains("iosobjectclose=true")) {
//                                               databaseHandler.saveCourseClose(url, myLearningModel);
//                                               return true;
//                                           }

                                           if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
                                               if (url.contains("iosobjectclose=true")) {
                                                   databaseHandler.saveCourseClose(url, myLearningModel);
                                                   return true;
                                               }
                                               if (url.contains("blank.html?ioscourseclose=true&cid")) {
                                                   databaseHandler.saveCourseClose(url, myLearningModel);
                                                   if (url.contains("lstatus=completed")) {
                                                       myLearningModel.setStatus("Completed");
                                                   }
                                                   Intent intent = getIntent();
                                                   intent.putExtra("myLearningDetalData", myLearningModel);
                                                   setResult(RESULT_OK, intent);
                                                   view.stopLoading();
                                                   finish();
                                                   return true;
                                               } else if (url.contains("blank.html?ioscourseclose=true")) {
                                                   databaseHandler.saveCourseClose(url, myLearningModel);

                                                   Intent intent = getIntent();
                                                   intent.putExtra("myLearningDetalData", myLearningModel);
                                                   setResult(RESULT_OK, intent);
                                                   view.stopLoading();
                                                   finish();
                                                   return true;

                                               }


                                           } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {
//                                               view.stopLoading();
//                                               finish();
                                           }

                                           if (url.contains("logoff")) {
                                               Intent intent = getIntent();
                                               intent.putExtra("myLearningDetalData", myLearningModel);
                                               setResult(RESULT_OK, intent);
                                               view.stopLoading();
                                               finish();

                                           }

//                                           if (isOffline) {   uncomment if you find any issue
////                                               svProgressHUD.dismiss();
//                                               return true;
//
//                                           } else {
//                                           return false;
//                                           }
                                           return false;
                                       }

//                                       @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                                       @Override
//                                       public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                                           String urlReq = request.getUrl().toString();
//                                           Log.d(TAG, "shouldOverrideUrlLoading: newmwthod " + urlReq);
//                                           urlReq = urlReq.toLowerCase();
//                                           if (urlReq.contains("ioscourseclose") || urlReq.contains("/logoff") || urlReq.contains("home.html")) {
//                                               Intent intent = getIntent();
//                                               intent.putExtra("myLearningDetalData", myLearningModel);
//                                               setResult(RESULT_OK, intent);
//                                               view.stopLoading();
//                                               finish();
//                                               return false;
//                                           } else if (urlReq.contains("iosobjectclose=true")) {
//
//                                               databaseHandler.saveCourseClose(urlReq, myLearningModel);
//                                               return true;
//                                           }
//                                           return false;
//                                       }

                                       @Override
                                       public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                           super.onPageStarted(view, url, favicon);
                                           Log.d(TAG, "onPageStarted: from normal web " + url);
                                       }

                                       @Override
                                       public void onPageFinished(WebView view, String url) {
                                           super.onPageFinished(view, url);
                                           Log.d(TAG, "onPageFinished: from normal web " + url);
                                           if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10") || myLearningModel.getObjecttypeId().equalsIgnoreCase("26") || !isOffline) {
                                               svProgressHUD.dismiss();
                                           } else {

                                               svProgressHUD.dismiss();
                                           }

                                       }

                                       @Override
                                       public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                           super.onReceivedError(view, errorCode, description, failingUrl);
                                           if (isOffline) {
                                               svProgressHUD.dismiss();
                                               Log.d(TAG, "onReceivedError: from normal web " + failingUrl);
                                           }
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

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
////        adWebView.pauseTimers();
//        adWebView.loadUrl("");
//        adWebView.stopLoading();

    }

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


    public static void clearWebViewAbsolutely(WebView webView) {
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearSslPreferences();
        webView.clearDisappearingChildren();
        webView.clearFocus();
        webView.clearFormData();
        webView.clearMatches();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        adWebView.saveState(outState);

    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

//        if (savedInstanceState != null) {
//            adWebView.restoreState(savedInstanceState);
//        }


    }

}
