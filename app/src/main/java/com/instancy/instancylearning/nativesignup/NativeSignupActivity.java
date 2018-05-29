package com.instancy.instancylearning.nativesignup;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;

import com.instancy.instancylearning.models.SignUpConfigsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://androidcocktail.blogspot.in/2014/03/android-spannablestring-example.html
 */

public class NativeSignupActivity extends AppCompatActivity {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = NativeSignupActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    DatabaseHandler db;

    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;

    UiSettingsModel uiSettingsModel;


    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;

    @Nullable
    @BindView(R.id.personaleditlist)
    ListView personalEditList;

    boolean isNewRecord = false;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    NativeSignupAdapter nativeSignupAdapter;


    List<SignUpConfigsModel> signUpConfigsModelList;


    ArrayList<String> degreeTitleList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nativesignupactivity);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(this);
        ButterKnife.bind(this);
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        svProgressHUD = new SVProgressHUD(context);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);

        signUpConfigsModelList = new ArrayList<>();

        getSignUpDetails();

        nativeSignupAdapter = new NativeSignupAdapter(this, BIND_ABOVE_CLIENT, signUpConfigsModelList);
        personalEditList.setAdapter(nativeSignupAdapter);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getResources().getString(R.string.btn_txt_signup) + "</font>"));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        if (isNetworkConnectionAvailable(this, -1)) {
            int i = returnHide();
            if (i == 1) {
//                countriesWebApiCall(appUserModel.getUserIDValue());

                degreeTitleList = db.fetchCountriesName(appUserModel.getSiteIDValue(), "25");

//                profileEditAdapter.refreshCountries(degreeTitleList);


            }

        } else {

        }
        assert bottomLayout != null;
        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
    }


    public int returnHide() {
        int returnValue = 0;

        if (signUpConfigsModelList.size() > 0) {

            for (int i = 0; i < signUpConfigsModelList.size(); i++) {

                if (signUpConfigsModelList.get(i).uicontroltypeid.equalsIgnoreCase("DropDownList")) {
                    returnValue = 1;
                }
            }
        }
        return returnValue;
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("PROFILEDATA")) {
                    if (response != null) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("table5");

                            Log.d(TAG, "Volley JSON post" + jsonArray.length());

                            db.injectProfielFieldOptions(jsonArray);

                            degreeTitleList = db.fetchCountriesName(appUserModel.getSiteIDValue(), "25");

//                            profileEditAdapter.refreshCountries(degreeTitleList);

                            Log.d(TAG, "notifySuccess: " + degreeTitleList.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {

                    }
                }


                if (requestType.equalsIgnoreCase("SIGNUPDATA")) {
                    if (response != null) {

                        if (response.has("profileconfigdata")) {

                            JSONArray signUpConfigAry = null;
                            try {
                                signUpConfigAry = response.getJSONArray("profileconfigdata");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (signUpConfigAry.length() > 0) {

                                try {
                                    db.injectSignUpConfigDetails(signUpConfigAry);

                                    injectFromDbToModel();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        }


                        boolean isPresent = db.checkChoiceTxtPresent();

                        if (!isPresent) {

                        }

                    } else {

                    }
                }

                svProgressHUD.dismiss();
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
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

    public void injectFromDbToModel() {


        signUpConfigsModelList = db.fetchUserSignConfigs();

        if (signUpConfigsModelList != null && signUpConfigsModelList.size() > 0) {
            nativeSignupAdapter.refreshList(signUpConfigsModelList);
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                closeForum(false);
                return true;
            case R.id.deleteItem:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.removeeducationmessage)).setTitle(getResources().getString(R.string.removeconnectionalert))
                        .setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.dismiss();

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.txtsave})
    public void actionsBottomBtns(View view) {

        switch (view.getId()) {
            case R.id.txtsave:
                try {
                    validateDataFields();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    public void validateDataFields() throws JSONException {

        boolean isValidationCompleted = true;

        Log.d(TAG, "validateDataFields: " + signUpConfigsModelList);


        for (int i = 0; i < signUpConfigsModelList.size(); i++) {

            if (signUpConfigsModelList.get(i).isrequired.contains("true") && signUpConfigsModelList.get(i).valueName.length() == 0) {

                Log.d(TAG, "validateNewForumCreation:  required " + signUpConfigsModelList.get(i).valueName);

                Toast.makeText(context, "Enter " + signUpConfigsModelList.get(i).displaytext, Toast.LENGTH_SHORT).show();
                isValidationCompleted = false;
                break;
            }
        }

        String finalString = "";

//        JSONObject jsonObject = new JSONObject();

        for (int i = 0; i < signUpConfigsModelList.size(); i++) {

//            finalString.put(profileConfigsModelist.get(i).datafieldname, profileConfigsModelist.get(i).valueName);

            String keyString = signUpConfigsModelList.get(i).datafieldname.toLowerCase();

            String valueString = signUpConfigsModelList.get(i).valueName;

//            jsonObject.put(keyString, valueString);

            if (i == signUpConfigsModelList.size() - 1) {

                finalString = finalString + keyString + "='" + valueString + "'";
            } else {
                finalString = finalString + keyString + "='" + valueString + "',";
            }


        }

//        String newString = jsonObject.toString().replace(":", "=");
//
//        String newStringWithoutBraces = newString.replace("{", "");
//
//        String newStringWithout = newStringWithoutBraces.replace("}", "");
//
//        Log.d(TAG, "validateNewForumCreation: " + finalString);
//
//        String replaceDataSt = finalString.replace("\'", "\\\'");


        if (isValidationCompleted) {

            JSONObject parameters = new JSONObject();

            //mandatory
            parameters.put("UserGroupIDs", "");
            parameters.put("RoleIDs", "");
            parameters.put("CMGroupIDs", "");
            parameters.put("Cmd", finalString);

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            String replaceDataString = parameterString.replace("\"", "\\\"");
            String addQuotes = ('"' + replaceDataString + '"');

            if (isNetworkConnectionAvailable(this, -1)) {
                sendNewOrUpdatedEducationDetailsDataToServer(addQuotes);
            } else {
                Toast.makeText(context, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sendNewOrUpdatedEducationDetailsDataToServer(final String postData) {
        String apiURL = "";


        apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileUpdateUserProfile?studId=" + appUserModel.getUserIDValue() + "&SiteURL=" + appUserModel.getSiteURL();
        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return postData.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("REFRESH", refresh);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void countriesWebApiCall(String userId) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetUserDetails?UserID=" + userId + "&siteURL=" + appUserModel.getSiteURL() + "&siteid=" + appUserModel.getSiteIDValue();

        urlStr = urlStr.replaceAll(" ", "%20");

        Log.d(TAG, "profileWebCall: " + urlStr);

        vollyService.getJsonObjResponseVolley("PROFILEDATA", urlStr, appUserModel.getAuthHeaders());

    }

    private void getSignUpDetails() {


        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetSignUpDetails?ComponentID=47&ComponentInstanceID=3104&Locale=en-us&SiteID=" + appUserModel.getSiteIDValue() + "&SiteURL=" + appUserModel.getSiteURL();

        urlStr = urlStr.replaceAll(" ", "%20");

        Log.d(TAG, "getSignUpDetails: " + urlStr);

        vollyService.getJsonObjResponseVolley("SIGNUPDATA", urlStr, appUserModel.getAuthHeaders());

    }

}

