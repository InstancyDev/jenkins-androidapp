package com.instancy.instancylearning.profile;

import android.content.Context;
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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
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

import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
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


    DiscussionTopicModel discussionTopicModel;
    DiscussionForumModel discussionForumModel;
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
    EditText editTitle;

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

    ArrayAdapter<DegreeTypeModel> myAdapter;
    @Nullable
    @BindView(R.id.spinner_FrmYear)
    Spinner spinnerFrmYear;

    @Nullable
    @BindView(R.id.spinner_DgreType)
    Spinner spinnerDgreType;


    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    boolean isUpdateForum = false;

    ArrayList<String> fromYList, toYList;


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

        fromYList = getFromYearToYear(1957, 2018);

        toYList = getFromYearToYear(1957, 2018);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        getDegreeTitles();


        if (getIntent().getBooleanExtra("isfromedit", false)) {

            discussionTopicModel = (DiscussionTopicModel) getIntent().getSerializableExtra("topicModel");
            isUpdateForum = true;
            editTitle.setText(discussionTopicModel.name);
            editDescription.setText(discussionTopicModel.longdescription);
        } else {

            isUpdateForum = false;
        }

        discussionForumModel = (DiscussionForumModel) getIntent().getSerializableExtra("forummodel");

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
        initilizeHeaderView();
        if (isNetworkConnectionAvailable(this, -1)) {

        } else {

        }


    }

    public void initilizeHeaderView() {

        ArrayAdapter fromYadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, fromYList);
        // attaching data adapter to spinner
        spinnerFrmYear.setAdapter(fromYadapter);
        spinnerToYear.setAdapter(fromYadapter);
        assert bottomLayout != null;
        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        myAdapter = new ArrayAdapter<DegreeTypeModel>(this, android.R.layout.simple_spinner_item, degreeTypeModels);


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
                            degreeTypeModels = getdegreeTypeModelArrayList(response);
                            spinnerDgreType.setAdapter(myAdapter);
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

        String titleStr = editTitle.getText().toString().trim();
        String descriptionStr = editDescription.getText().toString().trim();
        String dateString = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

        if (titleStr.length() < 4) {
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
        } else if (descriptionStr.length() < 10) {
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show();
        } else {

            JSONObject parameters = new JSONObject();
            if (isUpdateForum) {

                parameters.put("strContentID", discussionTopicModel.topicid);
                parameters.put("strTitle", titleStr);
                parameters.put("strDescription", descriptionStr);
                parameters.put("UserID", appUserModel.getUserIDValue());
                parameters.put("SiteID", appUserModel.getSiteIDValue());
                parameters.put("Locale", "en-us");
                parameters.put("ForumID", discussionForumModel.forumid);
                parameters.put("ForumName", discussionForumModel.name);
                parameters.put("strAttachFile", "");

            } else {

                parameters.put("UserID", appUserModel.getUserIDValue());
                parameters.put("Title", titleStr);
                parameters.put("Description", descriptionStr);
                parameters.put("ForumID", discussionForumModel.forumid);
                parameters.put("OrgID", appUserModel.getSiteIDValue());
                parameters.put("InvolvedUsers", "");
                parameters.put("SiteID", appUserModel.getSiteIDValue());
                parameters.put("LocaleID", "en-us");
                parameters.put("ForumName", discussionForumModel.name);
                parameters.put("strAttachFile", "" + discussionForumModel.createddate);

            }

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {

                String replaceDataString = parameterString.replace("\"", "\\\"");
                String addQuotes = ('"' + replaceDataString + '"');

                sendNewForumDataToServer(addQuotes);
            } else {
                Toast.makeText(context, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sendNewForumDataToServer(final String postData) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/CreateForumTopic?ForumID=" + discussionForumModel.forumid;

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, "Success! \nYour new topic has been successfully posted to server.", Toast.LENGTH_SHORT).show();
                    closeForum(true);
                } else {

                    Toast.makeText(context, "New topic cannot be posted to server. Contact site admin.", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("NEWFORUM", refresh);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void getDegreeTitles() {
        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));

        String parmStringUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/GeteducationTitleList";

        vollyService.getJsonObjResponseVolley("DGRE", parmStringUrl, appUserModel.getAuthHeaders());

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
}

