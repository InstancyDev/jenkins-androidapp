package com.instancy.instancylearning.askexpertenached;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
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
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.JsonObject;
import com.instancy.instancylearning.R;


import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;

import com.instancy.instancylearning.helper.VollyService;

import com.instancy.instancylearning.interfaces.Service;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;


import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;

import static com.instancy.instancylearning.utils.Utilities.getFileNameFromPath;

import static com.instancy.instancylearning.utils.Utilities.getRealPathFromURI;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.toRequestBody;

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
    View header;
    AskExpertDbTables db;

    Service service;

    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    UiSettingsModel uiSettingsModel;

    @Nullable
    @BindView(R.id.txt_question)
    TextView labelTitle;

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
    @BindView(R.id.edit_question)
    EditText editQuestion;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    @Nullable
    @BindView(R.id.edit_attachment)
    EditText editAttachment;

    @Nullable
    @BindView(R.id.btnUpload)
    Button btnUpload;

    @Nullable
    @BindView(R.id.attachedimg)
    ImageView attachmentThumb;

    @Nullable
    @BindView(R.id.askexpertsskillslistview)
    ListView skillsListview;

    boolean isFromExperts = false;
    boolean isEdit = false;
    String expertID = "";

    private int GALLERY = 1;

    String topicID = "", finalfileName = "", finalPath = "";
    Uri contentURIFinal;
    List<AskExpertSkillsModelDg> askExpertSkillsModelList = null;
    AskExpertQuestionModelDg askExpertQuestionModelDg = null;
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key, this);

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askquestionnewactivity_en);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        isFromExperts = getIntent().getBooleanExtra("EXPERTS", false);
        isEdit = getIntent().getBooleanExtra("EDIT", false);

        if (isEdit) {
            askExpertQuestionModelDg = (AskExpertQuestionModelDg) getIntent().getSerializableExtra("askExpertQuestionModel");
        }

        db = new AskExpertDbTables(this);
        svProgressHUD = new SVProgressHUD(context);

        ButterKnife.bind(this);

        arryStr = new String[2];

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        header = (View) getLayoutInflater().inflate(R.layout.skillheader, null);
        txtSave.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        txtCancel.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));

        btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_upload), null, null, null);
        btnUpload.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnUpload.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnUpload.setPadding(10, 2, 2, 2);
        btnUpload.setTag(0);
        attachmentThumb.setVisibility(View.GONE);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);


        if (!isFromExperts) {
            askExpertSkillsModelList = db.fetchAskExpertSkillsList();
            if (isEdit) {
                updateEditSkils();
            }
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
        skillsListview.addHeaderView(header, null, false);

        // Multipart

        BasicAuthInterceptor interceptor = new BasicAuthInterceptor(appUserModel.getAuthHeaders());
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Change base URL to your upload server URL.
        service = new Retrofit.Builder().baseUrl(appUserModel.getWebAPIUrl() + "/MobileLMS/InsertNewUserQuestion/").client(client).build().create(Service.class);

        if (isEdit) {
            updateUiForEditQuestion();
        }

    }


    public void getSkillsCalatalogFrom() {
        svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        String parmStringUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/GetUserQuestionSkills?SiteID=" + appUserModel.getSiteIDValue() + "&Type=selected&ExpertID=" + expertID;
        vollyService.getJsonObjResponseVolley("ASKQSCAT", parmStringUrl, appUserModel.getAuthHeaders());

    }

    public void updateUiForEditQuestion() {
        editQuestion.setText(askExpertQuestionModelDg.userQuestion);
        editDescription.setText(askExpertQuestionModelDg.userQuestionDescription);

        if (askExpertQuestionModelDg.userQuestionImagePath.length() > 0) {

            String imgUrl = appUserModel.getSiteURL() + askExpertQuestionModelDg.userQuestionImagePath;
            Picasso.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachmentThumb);
            attachmentThumb.setVisibility(View.VISIBLE);
            editAttachment.setText("");
            changeBtnValue(true);

        } else {

            attachmentThumb.setVisibility(View.GONE);

        }

    }

    public void initilizeHeaderView() {

        labelTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        TextView txtDescription = (TextView) header.findViewById(R.id.txt_relaventskills);

        SpannableString styledTitle
                = new SpannableString("*"+getLocalizationValue(JsonLocalekeys.asktheexpert_label_questionlabel));//Question
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
        txtDescription.setText(styledDescription);

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
        String categoryIds = "";

        category = generateSkills();
        categoryIds = generateSkillsIds();


        AskExpertQuestionModelDg questionModelDg = new AskExpertQuestionModelDg();

        String descriptionStr = editDescription.getText().toString().trim();
        String questionStr = editQuestion.getText().toString().trim();

        boolean isAttachmentRmoved = false;
        int editQuestionId = -1;

        if (isEdit && (Integer) btnUpload.getTag() == 0) {
            isAttachmentRmoved = true;

//            finalfileName="";
        }

        if (isEdit && contentURIFinal == null) {
            finalfileName = askExpertQuestionModelDg.userQuestionImage;
            isAttachmentRmoved = false;
        }

        if (isEdit) {
            editQuestionId = askExpertQuestionModelDg.questionID;
            questionModelDg.questionID = editQuestionId;
            questionModelDg.categoriesIDs = categoryIds;
        }

        if (questionStr.length() < 1) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.asktheexpert_label_questionentertitle), Toast.LENGTH_SHORT).show();
        } else if (category.length() == 0) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.asktheexpert_label_questionskillstitle), Toast.LENGTH_SHORT).show();
        } else {
            questionModelDg.userQuestion = questionStr;
            questionModelDg.questionCategories = category;
            Map<String, RequestBody> parameters = new HashMap<String, RequestBody>();
            parameters.put("UserID", toRequestBody(appUserModel.getUserIDValue()));
            parameters.put("SiteID", toRequestBody(appUserModel.getSiteIDValue()));
            parameters.put("UserEmail", toRequestBody(appUserModel.getUserLoginId()));
            parameters.put("UserName", toRequestBody(appUserModel.getUserName()));
            parameters.put("QuestionTypeID", toRequestBody("1"));
            parameters.put("UserQuestion", toRequestBody(questionStr));
            parameters.put("UserQuestionDesc", toRequestBody(descriptionStr));
            parameters.put("UseruploadedImageName", toRequestBody(finalfileName));
            parameters.put("skills", toRequestBody(category));
            parameters.put("SeletedSkillIds", toRequestBody(categoryIds));
            parameters.put("EditQueID", toRequestBody("" + editQuestionId));
            parameters.put("IsRemoveEditimage", toRequestBody("" + isAttachmentRmoved));

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {

                uploadFileThroughMultiPart(parameters, contentURIFinal, questionModelDg);

            } else {

                Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void uploadFileThroughMultiPart(Map<String, RequestBody> parameters, Uri fileUri, final AskExpertQuestionModelDg questionModelDg) {
        // create upload service client

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        MultipartBody.Part body = null;
        if (finalfileName.length() > 0 && fileUri != null) {
            File file = new File(finalPath);

            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(getContentResolver().getType(fileUri)),
                            file
                    );

            // MultipartBody.Part is used to send also the actual file name
            body = MultipartBody.Part.createFormData("Image", file.getName(), requestFile);

        }

        // finally, execute the request
//        Call<ResponseBody> call = service.upload(description, body);
        Call<ResponseBody> call = service.uploadFileWithPartMap(parameters, body);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess)
                        +" \n"+getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_questionpostedsuccessfully), Toast.LENGTH_SHORT).show();
                try {

                    String responseRecieved = response.body().string();
                    Log.v("Upload", "success responseRecieved" + responseRecieved);
                    // Add Your Logic
                    //  {"Table":[{"Column1":51.0}]}

                    if (responseRecieved != null && responseRecieved.length() > 0) {

                        JSONObject jsonObject = new JSONObject(responseRecieved);

                        if (jsonObject != null && jsonObject.length() > 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Table");
                            if (jsonArray != null && jsonArray.length() > 0) {

                                JSONObject columnObj = jsonArray.getJSONObject(0);
                                Log.d(TAG, "onResponse: QuestionId " + columnObj.getInt("Column1"));

                                questionModelDg.questionID = columnObj.getInt("Column1");
                                try {
                                    callSendExpertMails(questionModelDg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                svProgressHUD.dismiss();
                closeForum(true);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_questionpostfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
            }
        });
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

        if (data != null) {
            Uri contentURI = data.getData();
            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                final String fileName = getFileNameFromPath(contentURI, this);
//                final String mimeType = getMimeTypeFromUri(contentURI);
                Log.d(TAG, "onActivityResult: " + fileName);
//                bitmapAttachment = bitmap;
                editAttachment.setText(fileName);
                attachmentThumb.setImageBitmap(bitmap);
                btnUpload.setTag(1);
//                uploadFile(contentURI);
                finalPath = getRealPathFromURI(context, contentURI);
                contentURIFinal = contentURI;
                if (fileName.length() > 0) {
                    changeBtnValue(true);
                } else {
                    changeBtnValue(false);
                }
                finalfileName = fileName;

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(AskQuestionActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_labelfailed), Toast.LENGTH_SHORT).show();

            }
        }

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
            case R.id.btnUpload:
                if ((Integer) btnUpload.getTag() == 0) {
                    choosePhotoFromGallary();
                } else {
                    changeBtnValue(false);
                }
        }

    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWQS", refresh);
        setResult(RESULT_OK, intent);
        finish();
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


    public String generateSkillsIds() {
        String selectedSkills = "";

        if (askExpertSkillsModelList != null) {

            for (int s = 0; s < askExpertSkillsModelList.size(); s++) {
                if (askExpertSkillsModelList.get(s).isChecked) {
                    if (selectedSkills.length() > 0) {
                        selectedSkills = selectedSkills.concat("," + askExpertSkillsModelList.get(s).preferrenceID);
                    } else {
                        selectedSkills = askExpertSkillsModelList.get(s).preferrenceID;
                    }
                }

                Log.d(TAG, "generateSkills: " + selectedSkills);

            }


        } else {
            selectedSkills = "";
        }

        return selectedSkills;
    }


    public void updateEditSkils() {

        if (askExpertQuestionModelDg.questionCategoriesArray != null && askExpertQuestionModelDg.questionCategoriesArray.size() > 0) {

            for (int k = 0; k < askExpertQuestionModelDg.questionCategoriesArray.size(); k++) {

                if (askExpertSkillsModelList != null && askExpertSkillsModelList.size() > 0) {

                    for (int s = 0; s < askExpertSkillsModelList.size(); s++) {

                        if (askExpertQuestionModelDg.questionCategoriesArray.get(k).equalsIgnoreCase(askExpertSkillsModelList.get(s).preferrenceTitle)) {

                            askExpertSkillsModelList.get(s).isChecked = true;
                        }

                    }

                }

            }
            Log.d(TAG, "updateEditSkils: " + askExpertSkillsModelList.size());
        }
    }


    public List<AskExpertSkillsModelDg> generateAskExpertsSkills(JSONObject jsonObject) throws JSONException {

        List<AskExpertSkillsModelDg> askExpertSkillsModelList1 = new ArrayList<>();

        JSONArray jsonTableAry = jsonObject.getJSONArray("askskills");
        // for deleting records in table for respective table


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            AskExpertSkillsModelDg askExpertSkillsModel = new AskExpertSkillsModelDg();
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


    @SuppressLint("ResourceAsColor")
    public Drawable getDrawableFromString(Context context, int resourceID) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setTextColor(R.color.colorWhite);
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    public void changeBtnValue(boolean isAttachmentFound) {

        if (isAttachmentFound) {
            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_remove), null, null, null);
            btnUpload.setText(getLocalizationValue(JsonLocalekeys.myconnections_alertbutton_removebutton));
            attachmentThumb.setVisibility(View.VISIBLE);
            btnUpload.setTag(1);
        } else {
            editAttachment.setText("");
            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_upload), null, null, null);
            btnUpload.setTag(0);
            btnUpload.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_labelupload));
            attachmentThumb.setVisibility(View.GONE);
            finalfileName = "";
        }
        btnUpload.setPadding(10, 2, 2, 2);

    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }


    public void callSendExpertMails(AskExpertQuestionModelDg questionModelDg) throws JSONException {

        if (isNetworkConnectionAvailable(this, -1)) {


            String parmStringUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/SendExpertMails?intQuestionID=" + questionModelDg.questionID + "&UserId=" + appUserModel.getUserIDValue() + "&localeid="+preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))+"&MailSubject=&userQuestion=" + questionModelDg.userQuestion + "&intSiteID=" + appUserModel.getSiteIDValue() + "&Questionskills=" + questionModelDg.questionCategories;

            parmStringUrl = parmStringUrl.replaceAll(" ", "%20");

            vollyService.getJsonObjResponseVolley("SendExpertMails", parmStringUrl, appUserModel.getAuthHeaders());

        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }

}