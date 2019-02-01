package com.instancy.instancylearning.discussionfourms;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.view.MenuItem;
import android.view.View;
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
import com.instancy.instancylearning.chatmessanger.ChatActivity;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;

import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;

import com.instancy.instancylearning.models.DiscussionForumModel;

import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import static com.instancy.instancylearning.globalpackage.GlobalMethods.encodeImage;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.getFileNameFromPath;
import static com.instancy.instancylearning.utils.Utilities.getMimeTypeFromUri;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://androidcocktail.blogspot.in/2014/03/android-spannablestring-example.html
 */

public class CreateNewTopicActivity extends AppCompatActivity {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = CreateNewTopicActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    private int GALLERY = 1;
    DatabaseHandler db;

    DiscussionTopicModel discussionTopicModel;
    DiscussionForumModel discussionForumModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;

    String topicID = "", finalfileName = "", finalEncodedImageStr = "";

    @Nullable
    @BindView(R.id.txt_title)
    TextView labelTitle;

    @Nullable
    @BindView(R.id.txt_description)
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
    @BindView(R.id.edit_title)
    EditText editTitle;

    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

    @Nullable
    @BindView(R.id.edit_attachment)
    EditText editAttachment;

    @Nullable
    @BindView(R.id.txtAttachment)
    TextView txtAttachment;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    Bitmap bitmapAttachment = null;
    String endocedImageStr = "";

    boolean isUpdateForum = false;
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,CreateNewTopicActivity.this);

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewtopicfragment);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DatabaseHandler(this);
        ButterKnife.bind(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        txtSave.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        txtCancel.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);

        if (getIntent().getBooleanExtra("isfromedit", false)) {

            discussionTopicModel = (DiscussionTopicModel) getIntent().getSerializableExtra("topicModel");
            isUpdateForum = true;
            editTitle.setText(discussionTopicModel.name);
            editDescription.setText(discussionTopicModel.longdescription);
            editAttachment.setText(discussionTopicModel.attachment);
            txtSave.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_update));
        } else {

            isUpdateForum = false;
        }

        discussionForumModel = (DiscussionForumModel) getIntent().getSerializableExtra("forummodel");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "       "+getLocalizationValue(JsonLocalekeys.discussionforum_header_addtopictitlelabel) + "</font>"));

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

        labelTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        labelDescritpion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAttachment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        SpannableString styledTitle
                = new SpannableString("*"+getLocalizationValue(JsonLocalekeys.discussionforum_label_newforumtitlelabel));
        styledTitle.setSpan(new SuperscriptSpan(), 0, 1, 0);
        styledTitle.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
        styledTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledTitle.setSpan(new ForegroundColorSpan(Color.parseColor(uiSettingsModel.getAppTextColor())), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelTitle.setText(styledTitle);

//        SpannableString styledDescription
//                = new SpannableString("*Description");
//        styledDescription.setSpan(new SuperscriptSpan(), 0, 1, 0);
//        styledDescription.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
//        styledDescription.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        styledDescription.setSpan(new ForegroundColorSpan(Color.BLACK), 1, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        labelDescritpion.setText(styledDescription);

        assert bottomLayout != null;
        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

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
                final String mimeType = getMimeTypeFromUri(contentURI);
                Log.d(TAG, "onActivityResult: " + fileName);
                bitmapAttachment = bitmap;
                editAttachment.setText(fileName);
                new CountDownTimer(1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        endocedImageStr = convertToBase64(bitmapAttachment);
                        try {
                            encodeAttachment(fileName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(CreateNewTopicActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_labelfailed), Toast.LENGTH_SHORT).show();

            }
        }

    }

    private String convertToBase64(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);

        return encodedImage;
    }

    public void encodeAttachment(String fileName) throws JSONException {

        if (bitmapAttachment != null) {
            endocedImageStr = convertToBase64(bitmapAttachment);
        }

        if (endocedImageStr.length() < 10) {
            Toast.makeText(CreateNewTopicActivity.this, getLocalizationValue(JsonLocalekeys.commoncomponent_label_invalid_attachment), Toast.LENGTH_SHORT).show();
        } else {

            Log.d(TAG, "validateNewForumCreation: " + endocedImageStr);

            if (isNetworkConnectionAvailable(this, -1)) {

                String replaceDataString = endocedImageStr.replace("\"", "\\\"");
                String addQuotes = ('"' + replaceDataString + '"');

                finalEncodedImageStr = addQuotes;
                finalfileName = fileName;

            } else {
                Toast.makeText(CreateNewTopicActivity.this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendTopicAttachmentDataToServer(final String postData, String topicID, final String fileName) {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/UploadForumAttachment?fileName=" + fileName + "&TopicID=" + topicID + "&ReplyID=&isTopic=true&isEdit=false";

        StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                closeForum(true);
                if (!s.contains("failed")) {


                } else {

                    Toast.makeText(CreateNewTopicActivity.this, getLocalizationValue(JsonLocalekeys.commoncomponent_label_invalid_attachment), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(CreateNewTopicActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
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

        RequestQueue rQueue = Volley.newRequestQueue(CreateNewTopicActivity.this);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.txtsave, R.id.txtcancel, R.id.txtbrowse})
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
            case R.id.txtbrowse:
                choosePhotoFromGallary();
                break;
        }

    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    public void validateNewForumCreation() throws JSONException {

        String titleStr = editTitle.getText().toString().trim();
        String descriptionStr = editDescription.getText().toString().trim();

        if (titleStr.length() < 1) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.discussionforum_textfield_newtopictitletextfieldplaceholder), Toast.LENGTH_SHORT).show();
        } else {

            JSONObject parameters = new JSONObject();
            if (isUpdateForum) {

                parameters.put("strContentID", discussionTopicModel.topicid);
                parameters.put("strTitle", titleStr);
                parameters.put("strDescription", descriptionStr);
                parameters.put("UserID", appUserModel.getUserIDValue());
                parameters.put("SiteID", appUserModel.getSiteIDValue());
                parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
                parameters.put("ForumID", discussionForumModel.forumid);
                parameters.put("ForumName", discussionForumModel.name);
                parameters.put("strAttachFile", finalfileName);


            } else {

                parameters.put("UserID", appUserModel.getUserIDValue());
                parameters.put("Title", titleStr);
                parameters.put("Description", descriptionStr);
                parameters.put("ForumID", discussionForumModel.forumid);
                parameters.put("OrgID", appUserModel.getSiteIDValue());
                parameters.put("InvolvedUsers", "");
                parameters.put("SiteID", appUserModel.getSiteIDValue());
                parameters.put("LocaleID", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
                parameters.put("ForumName", discussionForumModel.name);
                parameters.put("strAttachFile", finalfileName);

            }

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {

                String replaceDataString = parameterString.replace("\"", "\\\"");
                String addQuotes = ('"' + replaceDataString + '"');

                sendNewForumDataToServer(addQuotes, isUpdateForum);
            } else {
                Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sendNewForumDataToServer(final String postData, final boolean isUpdateForum) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = "";
        if (isUpdateForum) {

            urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/EditForumTopic";
        } else {

            urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/CreateForumTopic?ForumID=" + discussionForumModel.forumid;
        }

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    s = s.replaceAll("#\\$#", "=");
                    s = s.replaceAll("^\"|\"$", "");

                    String[] strSplitvalues = s.split("=");

                    if (strSplitvalues.length > 1) {

                        topicID = strSplitvalues[1];
                    }

                    if (topicID.length() > 1 && finalEncodedImageStr.length() > 10) {
                        sendTopicAttachmentDataToServer(finalEncodedImageStr, topicID, finalfileName);
                    } else if (isUpdateForum && finalEncodedImageStr.length() > 10) {
                        sendTopicAttachmentDataToServer(finalEncodedImageStr, discussionTopicModel.topicid, finalfileName);

                    } else {
                        closeForum(true);
                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess)+" \n"+getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_newtopichasbeensuccessfullyadded), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_label_topiccannotpostedcontactadmin), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
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
}

