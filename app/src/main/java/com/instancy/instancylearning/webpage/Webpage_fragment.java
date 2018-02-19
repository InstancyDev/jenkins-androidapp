package com.instancy.instancylearning.webpage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Upendranath on 5/19/2017.
 */

public class Webpage_fragment extends Fragment {

    String TAG = Webpage_fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;

    @BindView(R.id.normalwebview)
    public WebView webView;

    PreferencesManager preferencesManager;
    Context context;


    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    String filterContentType = "", consolidationType = "all", sortBy = "";

    WebAPIClient webAPIClient;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;


    public Webpage_fragment() {


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);


        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        vollyService = new VollyService(resultCallback, context);

        sideMenusModel = null;
        webAPIClient = new WebAPIClient(context);
        HashMap<String, String> responMap = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");

        }
        if (responMap != null && responMap.containsKey("showconsolidatedlearning")) {
            String consolidate = responMap.get("showconsolidatedlearning");
            if (consolidate.equalsIgnoreCase("true")) {
                consolidationType = "consolidate";
            } else {
                consolidationType = "all";
            }
        } else {
            // No such key
            consolidationType = "all";
        }
        if (responMap != null && responMap.containsKey("sortby")) {
            sortBy = responMap.get("sortby");
        } else {
            // No such key
            sortBy = "";
        }
        if (responMap != null && responMap.containsKey("FilterContentType")) {
            filterContentType = responMap.get("FilterContentType");
        } else {
            // No such key
            filterContentType = "";
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.advancedweb_courselaunch, container, false);
        ButterKnife.bind(this, rootView);

        initilizeView();
        return rootView;
    }


    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);

        WebSettings webSettings = this.webView.getSettings();
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
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSupportZoom(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setBackgroundColor(getResources().getColor(R.color.colorFaceBookSilver));
        webSettings.setMediaPlaybackRequiresUserGesture(false);

//        webView.loadUrl("https://www.flipkart.com/");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
            Log.d(TAG, "onActivityCreated: " + sideMenusModel.getParameterStrings());
        }
        String parmUrl = getUrlFromParmString(sideMenusModel.getParameterStrings());
        Log.d(TAG, "onActivityCreated: parmUrl " + parmUrl);
        webView.loadUrl(parmUrl);
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }

    public String getUrlFromParmString(String parmString) {
        String urlStr = "";

        if (parmString == null)
            return "";
        String splitFirst[] = parmString.split("=");

        if (splitFirst.length > 0) {

            String replaceString = splitFirst[1];

            replaceString = replaceString.replace("#$#", "=");
            String splitSecond[] = replaceString.split("=");
            if (splitSecond.length > 0) {

                urlStr = "" + appUserModel.getSiteURL() + "/content/sitefiles/" + splitSecond[0];
            }
        }
        return urlStr;
    }

    public static void clearWebViewAbsolutely(WebView webView) {
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearSslPreferences();
        webView.clearDisappearingChildren();
        webView.clearFocus();
        webView.clearFormData();
        webView.clearMatches();
        webView.destroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearWebViewAbsolutely(webView);
    }
}
