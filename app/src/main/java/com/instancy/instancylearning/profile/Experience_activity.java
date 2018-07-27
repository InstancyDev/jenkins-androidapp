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
import android.view.WindowManager;
import android.widget.CheckBox;

import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UserExperienceModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.rackspira.kristiawan.rackmonthpicker.RackMonthPicker;
import com.rackspira.kristiawan.rackmonthpicker.listener.DateMonthDialogListener;
import com.rackspira.kristiawan.rackmonthpicker.listener.OnCancelMonthDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;

import static com.instancy.instancylearning.utils.Utilities.getIntFromMonth;
import static com.instancy.instancylearning.utils.Utilities.getMonthFromint;
import static com.instancy.instancylearning.utils.Utilities.getMonthName;
import static com.instancy.instancylearning.utils.Utilities.hideSoftKeyboard;
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
    @BindView(R.id.edit_location)
    EditText editLocation;

    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

    boolean isNewRecord = false;


    @Nullable
    @BindView(R.id.toYearTextview)
    TextView txtToYear;

    @Nullable
    @BindView(R.id.toyearlayout)
    RelativeLayout txtToYearLayout;


    @Nullable
    @BindView(R.id.fromYearTextview)
    TextView txtFromYear;


    @Nullable
    @BindView(R.id.chx_crnthere)
    CheckBox checkCrntHere;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    String joinedStr = "", resignedStr = "";

    int tillDate = 0;

    int fromYInt = 0, toYearInt = 0;

    int fromMonthInt = 0, toMonthInt = 0;

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
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "     Experience" + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
//            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        if (!isNewRecord) {
            updateUiValues(userExperienceModel);
        }

        assert bottomLayout != null;
        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

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
                builder.setMessage(getResources().getString(R.string.removeexpmessage)).setTitle(getResources().getString(R.string.removeconnectionalert))
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
                break;
            case R.id.fromYearTextview:
                hideSoftKeyboard(Experience_activity.this);
                showOnlyYearAndMonth(1);
                break;

            case R.id.toYearTextview:
                hideSoftKeyboard(Experience_activity.this);
                showOnlyYearAndMonth(2);
                break;
            case R.id.chx_crnthere:
                if (checkCrntHere.isChecked()) {
                    txtToYearLayout.setVisibility(View.GONE);
                    txtToYear.setVisibility(View.GONE);
                    tillDate = 1;
                } else {
                    txtToYearLayout.setVisibility(View.VISIBLE);
                    txtToYear.setVisibility(View.VISIBLE);
                    tillDate = 0;
                }
                break;

        }
    }


    public void validateNewForumCreation() throws JSONException {

        String jobRoleStr = edit_title.getText().toString().trim();
        String companyStr = edit_company.getText().toString().trim();
        String locationStr = editLocation.getText().toString().trim();
        String descriptionStr = editDescription.getText().toString().trim();

        boolean isLEssthaY = isGreaterY(fromYInt, toYearInt);

        if (!isNewRecord) {
            isLEssthaY = false;
        }

//        boolean isLEssthaM = isGreaterM(fromMonthInt, toMonthInt);

        if (jobRoleStr.length() < 2) {
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
        } else if (companyStr.length() < 2) {
            Toast.makeText(this, "Enter country", Toast.LENGTH_SHORT).show();
        } else if (locationStr.length() < 2) {
            Toast.makeText(this, "Enter location", Toast.LENGTH_SHORT).show();
        } else if (fromYInt == 0) {
            Toast.makeText(this, "Select from date", Toast.LENGTH_SHORT).show();
        } else if (toYearInt == 0) {
            Toast.makeText(this, "Select to date", Toast.LENGTH_SHORT).show();
        } else if (isLEssthaY) {
            Toast.makeText(this, "Your To year can't be earlier or same than your From year ", Toast.LENGTH_SHORT).show();
        } else {

            String totalYs = totalYearStr(tillDate, fromYInt, toYearInt, fromMonthInt, toMonthInt);

            if (tillDate == 1) {
                resignedStr = getMonthName();
            }
//            if (resignedStr.length() == 0) {
//                resignedStr = userExperienceModel.toDate;
//
//            }
//            if (joinedStr.length() == 0) {
//                joinedStr = userExperienceModel.fromDate;
//
//            }
            JSONObject parameters = new JSONObject();
            parameters.put("oldtitle", jobRoleStr);
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("title", jobRoleStr);
            parameters.put("location", locationStr);
            parameters.put("Company", companyStr);
            parameters.put("discription", descriptionStr);
            parameters.put("fromdate", totalYs); // difference duration
            parameters.put("todate", resignedStr); // todate
            parameters.put("showftoate", resignedStr); //  toDate
            parameters.put("showfromdate", joinedStr); // fromDate
            parameters.put("Tilldate", tillDate); // checkbox tillDate

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
            apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/AddExperiencedata";
        } else {
            apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/UpadteExperiencedata";
        }

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("true")) {

                    if (isNewRecord) {
                        Toast.makeText(context, "Success! \nYou have successfully added the experience", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Success! \nYou have successfully updated the experience", Toast.LENGTH_SHORT).show();
                    }
                    closeForum(true);
                } else {

                    Toast.makeText(context, "Experience cannot be posted to server. Contact site admin.", Toast.LENGTH_SHORT).show();
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


    public String totalYearStr(int tillD, int FromInt, int toInt, int fromMint, int toMint) {

        String totalDucation = "";

        int totalYears = toInt - FromInt;

        int totalMonths = toMint - fromMint;

        if (tillD == 1) {

            totalDucation = " " + FromInt + " -Present";

        } else {

            totalDucation = " " + FromInt + " - " + toInt + " " + totalYears + " yrs " + totalMonths + " months";

        }
        return totalDucation;
    }

    public boolean isGreaterY(int FromInt, int toInt) {

        boolean isG = false;

        if (FromInt > toInt) {
            isG = true;
        } else {
            isG = false;
        }

        return isG;
    }

    public boolean isGreaterM(int fromMnt, int toMnt) {

        boolean isG = false;

        if (fromMnt > toMnt) {
            isG = true;
        } else {
            isG = false;
        }

        return isG;
    }


    public void updateUiValues(UserExperienceModel userExperienceModel) {

        editDescription.setText(userExperienceModel.description);
        edit_company.setText(userExperienceModel.companyName);
        edit_title.setText(userExperienceModel.title);
        editDescription.setText(userExperienceModel.description);
        editLocation.setText(userExperienceModel.location);
        txtToYear.setText(userExperienceModel.toDate);
        txtFromYear.setText(userExperienceModel.fromDate);

//        holder.edit_field.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
//        holder.edit_field.setHintTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (userExperienceModel.tillDate) {
            tillDate = 1;
            checkCrntHere.setChecked(true);
            txtToYearLayout.setVisibility(View.GONE);
            txtToYear.setVisibility(View.GONE);

            String[] fromSplit = userExperienceModel.fromDate.split(" ");

            if (fromSplit.length > 1) { // 0 month 1 year
                fromYInt = Integer.parseInt(fromSplit[1]);
                fromMonthInt = getIntFromMonth(fromSplit[0]);
            }
            joinedStr = userExperienceModel.fromDate;
        } else {
            tillDate = 0;
            checkCrntHere.setChecked(false);

            String[] fromSplit = userExperienceModel.fromDate.split(" ");

            String[] toSplit = userExperienceModel.toDate.split(" ");

            if (fromSplit.length > 1) { // 0 month 1 year
                fromYInt = Integer.parseInt(fromSplit[1]);
                fromMonthInt = getIntFromMonth(fromSplit[0]);
            }

            if (toSplit.length > 1) { // 0 month 1 year
                toYearInt = Integer.parseInt(toSplit[1]);
                toMonthInt = getIntFromMonth(toSplit[0]);
            }

            joinedStr = userExperienceModel.fromDate;
            resignedStr = userExperienceModel.toDate;
        }


    }

    public void deleteEducationDetails() throws JSONException {
        JSONObject parameters = new JSONObject();
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("DisplayNo", userExperienceModel.displayNo);

        final String postdata = parameters.toString();

        String apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteExperiencedata";

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("true")) {

                    Toast.makeText(context, "Success! \nYou have successfully deleted the education", Toast.LENGTH_SHORT).show();
                    closeForum(true);
                } else {

                    Toast.makeText(context, "Experience cannot be posted to server. Contact site admin.", Toast.LENGTH_SHORT).show();
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


    public void showOnlyYearAndMonth(final int fromToInt) {

        final RackMonthPicker rackMonthPicker = new RackMonthPicker(this)
                .setLocale(Locale.ENGLISH)
                .setSelectedMonth(4)
                .setColorTheme(R.color.colorPrimary)
                .setPositiveButton(new DateMonthDialogListener() {
                    @Override
                    public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                        System.out.println(month);
                        System.out.println(startDate);
                        System.out.println(endDate);
                        System.out.println(year);
                        System.out.println(monthLabel);
                        if (fromToInt == 1) {
                            Log.d(TAG, "from onDateMonth: " + month);
                            fromYInt = year;
                            fromMonthInt = month;
                            txtFromYear.setText(userExperienceModel.fromDate);

                            String monthStr = getMonthFromint(month);
                            joinedStr = monthStr + " " + year;
                            txtFromYear.setText(monthStr + " " + year);

                        } else {
                            Log.d(TAG, "to onDateMonth: " + month);
                            toYearInt = year;
                            toMonthInt = month;

                            String monthStr = getMonthFromint(month);

                            txtToYear.setText(monthStr + " " + year);

                            resignedStr = monthStr + " " + year;
                        }

                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener() {
                    @Override
                    public void onCancel(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });

        rackMonthPicker.show();

    }


}

