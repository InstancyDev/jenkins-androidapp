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

public class Education_activity extends AppCompatActivity {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = Education_activity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    private int GALLERY = 1;
    DatabaseHandler db;
    ResultListner resultListner = null;

    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;

    ArrayList<DegreeTypeModel> degreeTypeModels;
    @Nullable
    @BindView(R.id.txtcancel)
    TextView txtCancel;

    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;

    @Nullable
    @BindView(R.id.edit_school)
    EditText editSchool;

    @Nullable
    @BindView(R.id.edit_country)
    EditText edit_country;

    @Nullable
    @BindView(R.id.edit_edType)
    EditText edit_edType;

    @Nullable
    @BindView(R.id.edit_Degree)
    EditText edit_Degree;

    @Nullable
    @BindView(R.id.edit_FrmYear)
    EditText edit_FrmYear;

    @Nullable
    @BindView(R.id.edit_ToYear)
    EditText edit_ToYear;

    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

    @Nullable
    @BindView(R.id.spinner_ToYear)
    Spinner spinnerToYear;

    ArrayAdapter titleAdapter, fromYearAdapter, toYearAdapter;
    @Nullable
    @BindView(R.id.spinner_FrmYear)
    Spinner spinnerFrmYear;

    @Nullable
    @BindView(R.id.spinner_DgreType)
    Spinner spinnerDgreType;

    boolean isNewRecord = false;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    ArrayList<String> fromYList, toYList, degreeTitleList;

    String degreeTypeStr = "", degreeTypeIDStr = "";

    int fromYInt = 0, toYearInt = 0;

    UserEducationModel userEducationModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.education_activity);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DatabaseHandler(this);
        ButterKnife.bind(this);
        degreeTypeModels = new ArrayList<>();
        fromYList = new ArrayList<>();
        toYList = new ArrayList<>();

        degreeTitleList = new ArrayList<>();

        fromYList.add(0, "");
        fromYList = getFromYearToYear(1957, 2018);

        toYList.add(0, "");
        toYList = getFromYearToYear(1957, 2018);

        degreeTitleList.add(0, "");

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        getDegreeTitles();

        userEducationModel = new UserEducationModel();
        Bundle bundle = getIntent().getExtras();
        if (getIntent().getBooleanExtra("isfromGroup", false)) {

            isNewRecord = true;

        } else {

            userEducationModel = (UserEducationModel) bundle.getSerializable("userEducationModel");
            isNewRecord = false;

        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "     Education" + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        initilizeSpinnersView();
        if (isNetworkConnectionAvailable(this, -1)) {

        } else {

        }

    }

    public void initilizeSpinnersView() {


        fromYearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, fromYList);

        toYearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, toYList);

        // attaching data adapter to spinner
        spinnerFrmYear.setAdapter(fromYearAdapter);
        spinnerToYear.setAdapter(toYearAdapter);

        assert bottomLayout != null;
        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        spinnerToYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                Log.d(TAG, "onItemSelected: " + toYList.get(position));
                toYearInt = Integer.parseInt(toYList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        spinnerFrmYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {

                Log.d(TAG, "onItemSelected: " + fromYList.get(position));

                fromYInt = Integer.parseInt(fromYList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        spinnerDgreType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {

                Log.d(TAG, "onItemSelected: " + degreeTitleList.get(position));
                degreeTypeStr = degreeTitleList.get(position);
                degreeTypeIDStr = returnSelectedId(degreeTypeStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("DGRE")) {
                    if (response != null) {
                        try {
//                            degreeTypeModels = getdegreeTypeModelArrayList(response);
                            updateSpinner(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
                if (requestType.equalsIgnoreCase("ASKQSCAT")) {
                    if (response != null) {

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

    public void updateSpinner(JSONObject responseObj) throws JSONException {

        degreeTitleList = new ArrayList<>();

        if (responseObj.has("educationTitleList")) {
            JSONArray jsonArray = responseObj.getJSONArray("educationTitleList");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                DegreeTypeModel degreeTypeModel = new DegreeTypeModel();

                if (object.has("id")) {
                    degreeTypeModel.id = object.getString("id");
                }

                if (object.has("name")) {
                    degreeTypeModel.name = object.getString("name");
                    degreeTitleList.add(object.getString("name"));
                }
                degreeTypeModels.add(degreeTypeModel);
            }
        }

        titleAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, degreeTitleList);

        spinnerDgreType.setAdapter(titleAdapter);


        if (!isNewRecord) {
            updateUiValues(userEducationModel);
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
                break;
        }

    }

    public void validateNewForumCreation() throws JSONException {

        String schoolStr = editSchool.getText().toString().trim();
        String countryStr = edit_country.getText().toString().trim();
        String degreeStr = edit_Degree.getText().toString().trim();
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
                parameters.put("DisplayNo", userEducationModel.displayno);
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

    public void getDegreeTitles() {
        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));

        String parmStringUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/GeteducationTitleList";

        vollyService.getJsonObjResponseVolley("DGRE", parmStringUrl, appUserModel.getAuthHeaders());

    }

    public String returnSelectedId(String selectedTitle) {

        String id = "1";

        for (int i = 0; i < degreeTypeModels.size(); i++) {

            if (degreeTypeModels.get(i).name.equalsIgnoreCase(selectedTitle)) {

                id = degreeTypeModels.get(i).id;
            }

        }

        return id;
    }

    public ArrayList<DegreeTypeModel> getdegreeTypeModelArrayList(JSONObject responseObj) throws JSONException {

        ArrayList<DegreeTypeModel> degreeTypeModels = new ArrayList<DegreeTypeModel>();
        if (responseObj.has("educationTitleList")) {
            JSONArray jsonArray = responseObj.getJSONArray("educationTitleList");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                DegreeTypeModel degreeTypeModel = new DegreeTypeModel();

                if (object.has("id")) {
                    degreeTypeModel.id = object.getString("id");
                }

                if (object.has("name")) {
                    degreeTypeModel.name = object.getString("name");
                }
                degreeTypeModels.add(degreeTypeModel);
            }
        }


        return degreeTypeModels;
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

    public void updateUiValues(UserEducationModel educationModel) {


        int spinnerPosition = titleAdapter.getPosition(educationModel.titleeducation);

        spinnerDgreType.setSelection(spinnerPosition);

        int toYear = toYearAdapter.getPosition(educationModel.toyear);

        spinnerToYear.setSelection(toYear);

        int fromYear = fromYearAdapter.getPosition(educationModel.fromyear);
        spinnerFrmYear.setSelection(fromYear);
        edit_Degree.setText(userEducationModel.degree);
        editSchool.setText(userEducationModel.school);
        edit_country.setText(userEducationModel.country);
        editDescription.setText(userEducationModel.description);

        try {
            fromYInt = Integer.parseInt(educationModel.fromyear);
            toYearInt = Integer.parseInt(educationModel.toyear);
        } catch (NumberFormatException numberFormatEx) {
            numberFormatEx.printStackTrace();
            toYearInt = 0;
            fromYInt = 0;
        }


        degreeTypeStr = educationModel.titleeducation;
    }


    public void deleteEducationDetails() throws JSONException {

        JSONObject parameters = new JSONObject();

        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("DisplayNo", userEducationModel.displayno);

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

