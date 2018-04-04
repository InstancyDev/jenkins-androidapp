package com.instancy.instancylearning.profile;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DegreeTypeModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UserEducationModel;
import com.instancy.instancylearning.models.UserExperienceModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;
import static com.instancy.instancylearning.utils.Utilities.getFromYearToYear;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://androidcocktail.blogspot.in/2014/03/android-spannablestring-example.html
 */

public class Experience_activity extends AppCompatActivity {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = Experience_activity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    DatabaseHandler db;

    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;

    UiSettingsModel uiSettingsModel;

    @Nullable
    @BindView(R.id.txtcancel)
    TextView txtCancel;

    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;

    @Nullable
    @BindView(R.id.edit_title)
    EditText edit_title;

    @Nullable
    @BindView(R.id.edit_company)
    EditText edit_company;

    @Nullable
    @BindView(R.id.edit_FrmYear)
    EditText edit_FrmYear;

    @Nullable
    @BindView(R.id.edit_ToYear)
    EditText edit_ToYear;

    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

    boolean isNewRecord = false;

    @Nullable
    @BindView(R.id.chx_crnthere)
    CheckBox checkCrntHere;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;


    String degreeTypeStr = "", degreeTypeIDStr = "";

    int fromYInt = 0, toYearInt = 0;

    UserExperienceModel userExperienceModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experience_activity);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

        db = new DatabaseHandler(this);
        ButterKnife.bind(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        vollyService = new VollyService(resultCallback, context);

        userExperienceModel = new UserExperienceModel();
        Bundle bundle = getIntent().getExtras();
        if (getIntent().getBooleanExtra("isfromGroup", false)) {

            isNewRecord = true;

        } else {

            userExperienceModel = (UserExperienceModel) bundle.getSerializable("userExperienceModel");
            isNewRecord = false;

        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "     Experience" + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
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
                        try {
                            deleteEducationDetails();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profiledelete, menu);
        MenuItem itemDelete = menu.findItem(R.id.deleteItem);

        Drawable filterDrawable = getDrawableFromStringHOmeMethod(R.string.fa_icon_trash, context, uiSettingsModel.getAppHeaderTextColor());
        itemDelete.setIcon(filterDrawable);
        itemDelete.setTitle("Delete");

        if (isNewRecord) {
            itemDelete.setVisible(false);
        } else {
            itemDelete.setVisible(true);
        }

        return true;
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

    @OnClick({R.id.txtsave, R.id.txtcancel})
    public void actionsBottomBtns(View view) {
        switch (view.getId()) {
            case R.id.txtsave:
                try {
                    validateNewForumCreation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txtcancel:
                finish();
            case R.id.edit_ToYear:
                Toast.makeText(context, "toyearclicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void validateNewForumCreation() throws JSONException {

        String schoolStr = edit_title.getText().toString().trim();
        String countryStr = edit_company.getText().toString().trim();
        String degreeStr = edit_FrmYear.getText().toString().trim();
        String toYear = edit_ToYear.getText().toString().trim();
        String descriptionStr = editDescription.getText().toString().trim();

        boolean isLEssthan = isGreater(fromYInt, toYearInt);

        if (schoolStr.length() < 4) {
            Toast.makeText(this, "Enter school", Toast.LENGTH_SHORT).show();
        } else if (countryStr.length() < 3) {
            Toast.makeText(this, "Enter country", Toast.LENGTH_SHORT).show();
        } else if (degreeStr.length() < 3) {
            Toast.makeText(this, "Enter degree", Toast.LENGTH_SHORT).show();
        } else if (isLEssthan) {
            Toast.makeText(this, "Your To year can't be earlier or same than your From year ", Toast.LENGTH_SHORT).show();
        } else {

            String totalYs = totalYearStr(fromYInt, toYearInt);
            JSONObject parameters = new JSONObject();

            parameters.put("oldtitle", degreeTypeIDStr);
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("school", schoolStr);
            parameters.put("Country", countryStr);
            parameters.put("title", degreeTypeIDStr);
            parameters.put("degree", degreeStr);
            parameters.put("fromyear", fromYInt);
            parameters.put("toyear", toYearInt);
            parameters.put("discription", descriptionStr);
            parameters.put("showfromdate", totalYs);
            parameters.put("titleEducation", degreeTypeStr);

            if (isNewRecord) {
                parameters.put("DisplayNo", "");
            } else {
                parameters.put("DisplayNo", userExperienceModel.displayNo);
            }

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {
                sendNewOrUpdatedEducationDetailsDataToServer(parameterString);
            } else {
                Toast.makeText(context, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendNewOrUpdatedEducationDetailsDataToServer(final String postData) {
        String apiURL = "";
        if (isNewRecord) {
            apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/AddEducationdata";
        } else {
            apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/UpadteEducationdata";
        }

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("true")) {

                    if (isNewRecord) {
                        Toast.makeText(context, "Success! \n.You have successfully added the education", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Success! \n.You have successfully updated the education", Toast.LENGTH_SHORT).show();
                    }
                    closeForum(true);
                } else {

                    Toast.makeText(context, "Education cannot be posted to server. Contact site admin.", Toast.LENGTH_SHORT).show();
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("REFRESH", refresh);
        setResult(RESULT_OK, intent);
        finish();
    }


    public String totalYearStr(int FromInt, int toInt) {

        String totalDucation = "";

        int totalYears = toInt - FromInt;

        totalDucation = " " + FromInt + "-" + toInt + " " + totalYears + " yrs";


        return totalDucation;
    }

    public boolean isGreater(int FromInt, int toInt) {

        boolean isG = false;

        if (FromInt > toInt) {
            isG = true;
        } else {
            isG = false;
        }

        return isG;
    }

    public void updateUiValues(UserExperienceModel userExperienceModel) {

        editDescription.setText(userExperienceModel.description);

    }

    public void deleteEducationDetails() throws JSONException {

        JSONObject parameters = new JSONObject();

        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("DisplayNo", userExperienceModel.displayNo);

        final String postdata = parameters.toString();

        String apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteEducationdata";

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("true")) {

                    Toast.makeText(context, "Success! \n.You have successfully deleted the education", Toast.LENGTH_SHORT).show();
                    closeForum(true);
                } else {

                    Toast.makeText(context, "Education cannot be posted to server. Contact site admin.", Toast.LENGTH_SHORT).show();
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
                return postdata.getBytes();
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

}

