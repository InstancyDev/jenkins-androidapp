package com.instancy.instancylearning.askexpert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.AskExpertSkillsModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
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

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.toStringS;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://androidcocktail.blogspot.in/2014/03/android-spannablestring-example.html
 */

public class AskQuestionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = AskQuestionActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    AskSkillAdapter askExpertAdapter;

    String arryStr[];

    DatabaseHandler db;
    ResultListner resultListner = null;

    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    UiSettingsModel uiSettingsModel;

    @Nullable
    @BindView(R.id.txt_description)
    TextView labelTitle;

    @Nullable
    @BindView(R.id.txt_relaventskills)
    TextView labelDescritpion;

    @Nullable
    @BindView(R.id.txtbrowse)
    TextView txtBrowse;

    @Nullable
    @BindView(R.id.txtcancel)
    TextView txtCancel;

    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;



    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

    @Nullable
    @BindView(R.id.edit_attachment)
    EditText editAttachment;


    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    @Nullable
    @BindView(R.id.askexpertsskillslistview)
    ListView skillsListview;

    boolean allowSkills = true, allowCommunication = true;

    boolean isFromExperts = false;
    String expertID = "";

    List<AskExpertSkillsModel> askExpertSkillsModelList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askquestionnewactivity);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        isFromExperts = getIntent().getBooleanExtra("EXPERTS", false);
        db = new DatabaseHandler(this);
        svProgressHUD = new SVProgressHUD(context);

        ButterKnife.bind(this);

        arryStr = new String[2];

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));



        txtSave.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        txtCancel.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));



        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);


        if (!isFromExperts) {
            askExpertSkillsModelList = db.fetchAskExpertSkillsModelList();
        } else {
            expertID = getIntent().getStringExtra("EXPERTID");
            getSkillsCalatalogFrom();
        }


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.asktheexpert_header_askaquestiontitlelabel) + "</font>"));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        initilizeHeaderView();

        askExpertAdapter = new AskSkillAdapter(this, BIND_ABOVE_CLIENT, askExpertSkillsModelList);
        skillsListview.setAdapter(askExpertAdapter);
        skillsListview.setOnItemClickListener(this);

    }

    public void getSkillsCalatalogFrom() {
        svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        String parmStringUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/GetUserQuestionSkills?SiteID=" + appUserModel.getSiteIDValue() + "&Type=selected&ExpertID=" + expertID;
        vollyService.getJsonObjResponseVolley("ASKQSCAT", parmStringUrl, appUserModel.getAuthHeaders());

    }

    public void initilizeHeaderView() {

        labelTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        labelDescritpion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        SpannableString styledTitle
                = new SpannableString("*"+getLocalizationValue(JsonLocalekeys.asktheexpert_label_questionlabel));
        styledTitle.setSpan(new SuperscriptSpan(), 0, 1, 0);
        styledTitle.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
        styledTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        styledTitle.setSpan(new ForegroundColorSpan(Color.BLACK), 1, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelTitle.setText(styledTitle);

        SpannableString styledDescription
                = new SpannableString("*"+getLocalizationValue(JsonLocalekeys.asktheexpert_label_skillslabel));
        styledDescription.setSpan(new SuperscriptSpan(), 0, 1, 0);
        styledDescription.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
        styledDescription.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        styledDescription.setSpan(new ForegroundColorSpan(Color.BLACK), 1, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelDescritpion.setText(styledDescription);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            switchSkills.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//            switchCommunication.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//            switchCommunication.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//            switchSkills.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//            switchrelatinship.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//            switchrelatinship.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        }

        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                if (requestType.equalsIgnoreCase("ASKQSCAT")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: ASKQSCAT  " + response);
                        try {
                            askExpertSkillsModelList = generateAskExpertsSkills(response);
                            askExpertAdapter.refreshList(askExpertSkillsModelList);

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void validateNewForumCreation() throws JSONException {

        String category = "";

        category = generateSkills();

        String descriptionStr = editDescription.getText().toString().trim();

        if (descriptionStr.length() < 1) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.asktheexpert_label_questionentertitle), Toast.LENGTH_SHORT).show();
        } else if (category.length() == 0) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.asktheexpert_label_questionskillstitle), Toast.LENGTH_SHORT).show();
        } else {
//            Map<String, String> parameters = new HashMap<String, String>();
            JSONObject parameters = new JSONObject();

            parameters.put("aintQuestionTypeID", "1");
            parameters.put("aintUserID", appUserModel.getUserIDValue());
            parameters.put("astrQuestionCategories", "" + category);
            parameters.put("astrUserEmail", appUserModel.getUserLoginId());
            parameters.put("astrUserName", appUserModel.getUserName());
            parameters.put("astrUserQuestion", descriptionStr);

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {

                String replaceDataString = parameterString.replace("\"", "\\\"");
                String addQuotes = ('"' + replaceDataString + '"');

                sendNewForumDataToServer(addQuotes, descriptionStr, category);

            } else {
                Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendNewForumDataToServer(final String postData, final String questionData, final String category) {

        final String dateString = getCurrentDateTime("yyyy/MM/dd");

        final String dateStringSec = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/PostNewAskQuestion?siteUrl=" + appUserModel.getSiteURL();

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {


                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess)
                            +" \n"+getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_questionpostedsuccessfully), Toast.LENGTH_SHORT).show();
                    AskExpertQuestionModel askExpertQuestionModel = new AskExpertQuestionModel();
                    String replaceString = s.replace("##", "=");
                    String[] strSplitvalues = replaceString.split("=");

                    String replyID = "";
                    if (strSplitvalues.length > 1) {
                        replyID = strSplitvalues[1].replace("\"", "");
                        Log.d(TAG, "onResponse: " + replyID);
                        askExpertQuestionModel.questionID = Integer.parseInt(replyID);
                        askExpertQuestionModel.postedDate = dateString;
                        askExpertQuestionModel.username = appUserModel.getUserName();
                        askExpertQuestionModel.userID = appUserModel.getUserIDValue();
                        askExpertQuestionModel.userQuestion = questionData;
                        askExpertQuestionModel.postedDate = dateString;
                        askExpertQuestionModel.createdDate = dateStringSec;
                        askExpertQuestionModel.siteID = appUserModel.getSiteIDValue();
                        askExpertQuestionModel.questionCategories = category;

                        insertSingleAsktheExpertAnswerDataIntoSqLite(askExpertQuestionModel);
                    }

                    closeForum(true);
                } else {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_unabletopostquetion), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
                closeForum(false);
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
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");


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

    public static Drawable setTintDrawable(Drawable drawable, @ColorInt int color) {
        drawable.clearColorFilter();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.invalidateSelf();
        Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapDrawable, color);
        return wrapDrawable;
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

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWQS", refresh);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void insertSingleAsktheExpertAnswerDataIntoSqLite(AskExpertQuestionModel askExpertQuestionModel) {

        String insertStr = "INSERT INTO ASKQUESTIONS (questionid, userid, username, userquestion, posteddate, createddate, answers,questioncategories,siteid ) VALUES (" +
                "" + askExpertQuestionModel.questionID +
                "," + askExpertQuestionModel.userID +
                ",'" + askExpertQuestionModel.username +
                "','" + askExpertQuestionModel.userQuestion +
                "','" + askExpertQuestionModel.postedDate +
                "','" + askExpertQuestionModel.createdDate +
                "'," + 0 +
                ",'" + askExpertQuestionModel.questionCategories +
                "'," + askExpertQuestionModel.siteID +
                ")";
        try {
            db.executeQuery(insertStr);
            closeForum(true);
        } catch (SQLiteException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {
        switch (view.getId()) {
            case R.id.swtchskills:
                if (askExpertSkillsModelList.get(index).isChecked) {
                    askExpertSkillsModelList.get(index).isChecked = false;
                } else {
                    askExpertSkillsModelList.get(index).isChecked = true;
                }
                askExpertAdapter.notifyDataSetChanged();
                break;
        }
    }

    public String generateSkills() {
        String selectedSkills = "";

        if (askExpertSkillsModelList != null) {

            for (int s = 0; s < askExpertSkillsModelList.size(); s++) {
                if (askExpertSkillsModelList.get(s).isChecked) {
                    if (selectedSkills.length() > 0) {
                        selectedSkills = selectedSkills.concat("," + askExpertSkillsModelList.get(s).shortSkillName);
                    } else {
                        selectedSkills = askExpertSkillsModelList.get(s).shortSkillName;
                    }
                }

                Log.d(TAG, "generateSkills: " + selectedSkills);

            }


        } else {
            selectedSkills = "";
        }

        return selectedSkills;
    }

    public List<AskExpertSkillsModel> generateAskExpertsSkills(JSONObject jsonObject) throws JSONException {

        List<AskExpertSkillsModel> askExpertSkillsModelList1 = new ArrayList<>();

        JSONArray jsonTableAry = jsonObject.getJSONArray("askskills");
        // for deleting records in table for respective table


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            AskExpertSkillsModel askExpertSkillsModel = new AskExpertSkillsModel();
            //questionid
            if (jsonMyLearningColumnObj.has("orgunitid")) {

                askExpertSkillsModel.orgUnitID = jsonMyLearningColumnObj.getString("orgunitid");
            }
            // response
            if (jsonMyLearningColumnObj.has("preferrenceid")) {

                askExpertSkillsModel.preferrenceID = jsonMyLearningColumnObj.get("preferrenceid").toString();

            }
            // responseid
            if (jsonMyLearningColumnObj.has("preferrencetitle")) {

                askExpertSkillsModel.preferrenceTitle = jsonMyLearningColumnObj.get("preferrencetitle").toString();

            }
            // respondeduserid
            if (jsonMyLearningColumnObj.has("shortskillname")) {

                askExpertSkillsModel.shortSkillName = jsonMyLearningColumnObj.get("shortskillname").toString();

            }

            askExpertSkillsModel.siteID = appUserModel.getSiteIDValue();

            askExpertSkillsModelList1.add(askExpertSkillsModel);
        }

        return askExpertSkillsModelList1;
    }

    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key, this);

    }

}