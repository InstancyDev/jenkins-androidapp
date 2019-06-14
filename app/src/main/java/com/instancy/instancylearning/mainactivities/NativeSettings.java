package com.instancy.instancylearning.mainactivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.blankj.utilcode.util.LogUtils;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.adapters.NativeSettingsAdapter;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.interfaces.StringResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.NativeSetttingsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Native;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.webkit.URLUtil.isValidUrl;
import static com.instancy.instancylearning.utils.Utilities.formatURL;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 5/29/2017.
 */

public class NativeSettings extends AppCompatActivity {

    NativeSettingsAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    PreferencesManager preferencesManager;
    Boolean isLogin;
    private SVProgressHUD svProgressHUD;
    final Context context = this;
    String TAG = NativeSettings.class.getSimpleName();

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, NativeSettings.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        svProgressHUD = new SVProgressHUD(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            isLogin = bundle.getBoolean(StaticValues.KEY_ISLOGIN);
        }
        PreferencesManager.initializeInstance(context);
        preferencesManager = PreferencesManager.getInstance();
//        preferencesManager.setStringValue(getResources().getString(R.string.app_default_url), StaticValues.KEY_SITEURL);

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + getLocalizationValue(JsonLocalekeys.sidemenu_button_settingsbutton) + "</font>"));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }


        View someView = findViewById(R.id.settingslayout);
        someView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        // Add a few teams to display.
        expandableListDetail = NativeSetttingsModel.getData(isLogin, NativeSettings.this);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListView = (ExpandableListView) findViewById(R.id.settings_list);
        // Construct our adapter, using our own layout and myTeams
        expandableListAdapter = new NativeSettingsAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        if (isLogin) {
            expandableListView.expandGroup(0);
            expandableListView.expandGroup(1);
        } else {
            expandableListView.expandGroup(0);
        }
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childPosition == 0 && !isLogin) {

//                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText(getLocalizationValue(JsonLocalekeys.profile_alerttitle_stringareyousure))
//                            .setContentText(getLocalizationValue(JsonLocalekeys.sidemenu_button_settingsbutton))
//                            .setConfirmText(getLocalizationValue(JsonLocalekeys.siteurlsetting_alertbutton_resetbutton))
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//                                    if (isNetworkConnectionAvailable(context, -1)) {
//                                        preferencesManager.setStringValue(getString(R.string.app_default_url), StaticValues.KEY_SITEURL);
//                                        Intent intentBranding = new Intent(NativeSettings.this, Splash_activity.class);
//                                        intentBranding.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        startActivity(intentBranding);
//                                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.default_url_set) + preferencesManager.getStringValue(StaticValues.KEY_SITEURL), Toast.LENGTH_LONG).show();
//                                    } else {
//                                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
//                                    }
//
//
//                                    sDialog.dismissWithAnimation();
//
//                                }
//                            }).setCancelText("Cancel").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//
//                            sweetAlertDialog.dismissWithAnimation();
//
//                        }
//                    })
//                            .show();

                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NativeSettings.this);
                    builder.setMessage(getLocalizationValue(JsonLocalekeys.sidemenu_button_settingsbutton)).setTitle(getLocalizationValue(JsonLocalekeys.profile_alerttitle_stringareyousure))
                            .setCancelable(false)
                            .setPositiveButton(getLocalizationValue(JsonLocalekeys.siteurlsetting_alertbutton_resetbutton), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things

                                    if (isNetworkConnectionAvailable(context, -1)) {
                                        preferencesManager.setStringValue(getString(R.string.app_default_url), StaticValues.KEY_SITEURL);
                                        Intent intentBranding = new Intent(NativeSettings.this, Splash_activity.class);
                                        intentBranding.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intentBranding);
                                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.default_url_set) + "  " + preferencesManager.getStringValue(StaticValues.KEY_SITEURL), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                                    }

                                    dialog.dismiss();
                                }
                            }).setNegativeButton(getLocalizationValue(JsonLocalekeys.siteurlsetting_alertbutton_cancelbutton), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();

                } else {

                    resetUrlEditDialog();
                }

                return true;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                return true;
            }
        });

    }

    public void resetUrlEditDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.reseturldialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.reseturledit);
        userInput.setText(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setTitle(getLocalizationValue(JsonLocalekeys.siteurlsetting_label_siteurlheadinglabel))
                .setPositiveButton(getLocalizationValue(JsonLocalekeys.siteurlsetting_alertbutton_submitbutton),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                String newUrl = userInput.getText().toString().trim();
                                newUrl = formatURL(newUrl);

                                if (isValidUrl(newUrl)) {
                                    if (isNetworkConnectionAvailable(context, -1)) {
//                                        resetUrlWebCall(newUrl);
                                        resetUrlWebCallForDigi(newUrl);
                                    } else {
                                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.siteurlsetting_alertsubtitle_siteurlnotvalidorcheckinternetconnection), Toast.LENGTH_SHORT).show();

                                }

                            }


                        })
                .setNegativeButton(getLocalizationValue(JsonLocalekeys.siteurlsetting_alertbutton_cancelbutton),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void resetUrlWebCall(final String newUrl) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        VolleySingleton.stringRequests(newUrl + "/PublicModules/SiteAPIDetails.aspx", new StringResultListner<String>() {
            @Override
            public void getResult(String result) {
                if (!result.isEmpty()) {
                    //do what you need with the result...

                    String webApiUrl = getSiteAPIDetails(result);

                    LogUtils.d(TAG, "Result web api " + webApiUrl);

                    if (isValidUrl(webApiUrl)) {
                        if (isNetworkConnectionAvailable(context, -1)) {
                            preferencesManager.setStringValue(newUrl, StaticValues.KEY_SITEURL);
                            Intent intentBranding = new Intent(NativeSettings.this, Splash_activity.class);
                            intentBranding.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentBranding);

                        } else {
                            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.siteurlsetting_alertsubtitle_siteurlnotvalidorcheckinternetconnection) + webApiUrl, Toast.LENGTH_SHORT).show();

                    }

                    svProgressHUD.dismiss();
                }
            }

            @Override
            public void getError(String error) {
                LogUtils.e(TAG, "Result error " + error);
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
            }
        });
    }


    public void resetUrlWebCallForDigi(final String newUrl) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        //     String requestURL = getResources().getString(R.string.app_default_auth_url)+"Authentication/GetAPIAuth?AppURL=" + newUrl;

        String requestURL = getResources().getString(R.string.app_default_auth_url) + "Authentication/GetAPIAuth?AppURL=" + newUrl;

        VolleySingleton.stringRequests(requestURL, new StringResultListner<String>() {
            @Override
            public void getResult(String result) {
                if (!result.isEmpty()) {
                    //do what you need with the result...

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String webApiUrl = jsonObject.optString("WebAPIUrl");

                    LogUtils.d(TAG, "Result web api " + webApiUrl);

                    if (isValidUrl(webApiUrl)) {
                        if (isNetworkConnectionAvailable(context, -1)) {
                            preferencesManager.setStringValue(newUrl, StaticValues.KEY_SITEURL);


                            Intent intentBranding = new Intent(NativeSettings.this, Splash_activity.class);
                            intentBranding.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentBranding);

                        } else {
                            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.siteurlsetting_alertsubtitle_siteurlnotvalidorcheckinternetconnection) + webApiUrl, Toast.LENGTH_SHORT).show();

                    }

                    svProgressHUD.dismiss();
                }
            }

            @Override
            public void getError(String error) {
                LogUtils.e(TAG, "Result error " + error);
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
            }
        });
    }


    public String getSiteAPIDetails(String result) {

        String strAPIURL = "";

        if (result.indexOf("<!DOCTYPE html>") > -1) {
            strAPIURL = result.split("<!DOCTYPE html>")[0]
                    .toString().trim();

            if (!Utilities.isValidString(strAPIURL)) {
                strAPIURL = "";
            } else {

                Log.d("webapiurl", strAPIURL);
                preferencesManager.setStringValue(strAPIURL, StaticValues.KEY_WEBAPIURL);

            }
        }

        return strAPIURL;
    }

    @Override
    public void onBackPressed() {
        if (isLogin) {


        }

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
