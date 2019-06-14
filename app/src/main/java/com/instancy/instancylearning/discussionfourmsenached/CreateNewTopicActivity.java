package com.instancy.instancylearning.discussionfourmsenached;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.askexpertenached.BasicAuthInterceptor;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
import static com.instancy.instancylearning.utils.Utilities.getAttachedFileTypeDrawable;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.getFileExtension;
import static com.instancy.instancylearning.utils.Utilities.getFileExtensionWithPlaceHolderImage;
import static com.instancy.instancylearning.utils.Utilities.getFileNameFromPath;
import static com.instancy.instancylearning.utils.Utilities.getMimeTypeFromUri;
import static com.instancy.instancylearning.utils.Utilities.getPath;
import static com.instancy.instancylearning.utils.Utilities.getRealPathFromURI;
import static com.instancy.instancylearning.utils.Utilities.gettheContentTypeNotImg;
import static com.instancy.instancylearning.utils.Utilities.isBigFileThanExpected;
import static com.instancy.instancylearning.utils.Utilities.isFilevalidFileFound;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.toRequestBody;

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

    DiscussionTopicModelDg discussionTopicModel;
    DiscussionForumModelDg discussionForumModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;


    Service service;
    Uri contentURIFinal;
    String topicID = "", finalfileName = "", finalEncodedImageStr = "", finalPath = "";

    @Nullable
    @BindView(R.id.txt_title)
    TextView labelTitle;

    @Nullable
    @BindView(R.id.txt_description)
    TextView labelDescritpion;

    @Nullable
    @BindView(R.id.txtbrowse)
    Button btnUpload;

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


    @Nullable
    @BindView(R.id.attachedimg)
    ImageView attachmentThumb;

    boolean isUpdateForum = false;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, CreateNewTopicActivity.this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewtopicactivity);
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
        discussionForumModel = (DiscussionForumModelDg) getIntent().getSerializableExtra("forummodel");
        initilizeHeaderView();
        if (getIntent().getBooleanExtra("isfromedit", false)) {
            discussionTopicModel = (DiscussionTopicModelDg) getIntent().getSerializableExtra("topicModel");
            isUpdateForum = true;
            editTitle.setText(discussionTopicModel.name);
            editDescription.setText(discussionTopicModel.longDescription);
            editAttachment.setText(discussionTopicModel.uploadFileName);
            txtSave.setText(getLocalizationValue(JsonLocalekeys.details_button_updatebutton));
            updateUiForEditQuestion();
        } else {
            attachmentThumb.setVisibility(View.GONE);
            changeBtnValue(false);
            txtSave.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newforumsavebutton));
        }
        txtCancel.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newfourmcancelbutton));
        editTitle.setHint(getLocalizationValue(JsonLocalekeys.discussionforum_textfield_newtopictitletextfieldplaceholder));
        editDescription.setHint(getLocalizationValue(JsonLocalekeys.discussionforum_textview_newtopicdescriptionplaceholder));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "       " + getLocalizationValue(JsonLocalekeys.discussionforum_header_addtopictitlelabel) + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }


        // Multipart

        BasicAuthInterceptor interceptor = new BasicAuthInterceptor(appUserModel.getAuthHeaders());
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Change base URL to your upload server URL.
        service = new Retrofit.Builder().baseUrl(appUserModel.getWebAPIUrl() + "/MobileLMS/UploadForumAttachment/").client(client).build().create(Service.class);
        attacmentisNoAllowed();
    }


    public void attacmentisNoAllowed() {

        if (discussionForumModel != null && !discussionForumModel.attachFile) {
            btnUpload.setVisibility(View.GONE);
            attachmentThumb.setVisibility(View.GONE);
            txtAttachment.setVisibility(View.GONE);
        }
    }

    public void initilizeHeaderView() {

        labelTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        labelDescritpion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAttachment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        SpannableString styledTitle
                = new SpannableString("*" + getLocalizationValue(JsonLocalekeys.discussionforum_label_newtopictitlelabel) + ":");
        styledTitle.setSpan(new SuperscriptSpan(), 0, 1, 0);
        styledTitle.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
        styledTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledTitle.setSpan(new ForegroundColorSpan(Color.parseColor(uiSettingsModel.getAppTextColor())), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelTitle.setText(styledTitle);

        txtAttachment.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_newcommentattachmentlabel));
        labelDescritpion.setText(getLocalizationValue(JsonLocalekeys.details_label_descriptionlabel));

//        SpannableString styledDescription
//                = new SpannableString("*Description");
//        styledDescription.setSpan(new SuperscriptSpan(), 0, 1, 0);
//        styledDescription.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
//        styledDescription.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        styledDescription.setSpan(new ForegroundColorSpan(Color.BLACK), 1, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        labelDescritpion.setText(styledDescription);

        assert bottomLayout != null;
        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_upload), null, null, null);
        btnUpload.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnUpload.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnUpload.setPadding(10, 2, 2, 2);
        btnUpload.setTag(0);
        btnUpload.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newtopicselectfileuploadbutton));
    }

    public void updateUiForEditQuestion() {

        if (discussionTopicModel.uploadFileName.length() > 0) {

            changeBtnValue(true);
        } else {
            changeBtnValue(false);

        }
    }


    public void changeBtnValue(boolean isAttachmentFound) {

        if (isAttachmentFound) {
            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_remove), null, null, null);
            btnUpload.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newforumremoveimagebutton));
            attachmentThumb.setVisibility(View.VISIBLE);
            btnUpload.setTag(1);
            if (isUpdateForum) {
                final String fileExtesnion = getFileExtensionWithPlaceHolderImage(discussionTopicModel.uploadFileName);
                String imgUrl = appUserModel.getSiteURL() + discussionTopicModel.uploadFileName;
                int resourceId = 0;

                resourceId = gettheContentTypeNotImg(fileExtesnion);
                if (resourceId == 0)
                    Glide.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachmentThumb);
                else
                    attachmentThumb.setImageDrawable(getDrawableFromStringWithColor(this, resourceId, uiSettingsModel.getAppButtonBgColor()));

            }

        } else {
            editAttachment.setText("");
            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_upload), null, null, null);
            btnUpload.setTag(0);
            btnUpload.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newtopicselectfileuploadbutton));

            attachmentThumb.setVisibility(View.GONE);
            finalfileName = "";
        }
        btnUpload.setPadding(10, 2, 2, 2);

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
        if (requestCode == GALLERY) {

            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    final String fileName = getFileNameFromPath(contentURI, this);

                    final String filePathHere = getPath(context, contentURI);


                    if (!isValidString(filePathHere) || filePathHere.toLowerCase().contains(".zip") || filePathHere.toLowerCase().contains(".rar")) {
                        Toast.makeText(context, "Invalid file type", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    File file = new File(filePathHere);

                    final String fileExtesnion = getFileExtensionWithPlaceHolderImage(filePathHere);
                    if (!isFilevalidFileFound(uiSettingsModel.getDiscussionForumFileTypes(), fileExtesnion)) {
                        Toast.makeText(context, "Invalid file type", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int requiredSize = Integer.parseInt(uiSettingsModel.getUserUploadFileSize()) / 1048576;

                    if (isBigFileThanExpected(uiSettingsModel.getUserUploadFileSize(), file)) {
                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.maximum_allowed_file_size) + " " + requiredSize, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d(TAG, "onActivityResult: " + fileName);
                    Drawable typeIcon = getAttachedFileTypeDrawable(fileExtesnion, this, uiSettingsModel.getAppButtonBgColor());

                    if (fileName.length() > 0) {
                        changeBtnValue(true);
                    } else {
                        changeBtnValue(false);
                    }

                    if (bitmap != null) {
                        attachmentThumb.setImageBitmap(bitmap);
                    } else {
                        attachmentThumb.setImageDrawable(typeIcon);
                    }
                    editAttachment.setText(fileName);
                    contentURIFinal = contentURI;
                    finalPath = getPath(context, contentURI);
                    contentURIFinal = contentURI;
                    finalfileName = fileName;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateNewTopicActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_labelfailed), Toast.LENGTH_SHORT).show();

                }
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
                if ((Integer) btnUpload.getTag() == 0) {
                    choosePhotoFromGallary();
                } else {
                    changeBtnValue(false);
                }
                break;
        }

    }

    public void choosePhotoFromGallary() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(intent, GALLERY);
        } catch (ActivityNotFoundException notFound) {
            notFound.printStackTrace();
        }
    }

    public void validateNewForumCreation() throws JSONException {

        String titleStr = editTitle.getText().toString().trim();
        String descriptionStr = editDescription.getText().toString().trim();


        if (isUpdateForum && contentURIFinal == null) {
            finalfileName = discussionTopicModel.uploadedImageName;
        }
        if (isUpdateForum && (Integer) btnUpload.getTag() == 0) {
            finalfileName = "";
        }


        if (titleStr.length() < 1) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.discussionforum_textfield_newforumtitletextfieldplaceholder), Toast.LENGTH_SHORT).show();
        } else {

            JSONObject parameters = new JSONObject();
            if (isUpdateForum) {

                parameters.put("strContentID", discussionTopicModel.contentID);
                parameters.put("Title", titleStr);
                parameters.put("Description", descriptionStr);
                parameters.put("UserID", appUserModel.getUserIDValue());
                parameters.put("SiteID", appUserModel.getSiteIDValue());
                parameters.put("OrgID", appUserModel.getSiteIDValue());
                parameters.put("LocaleID", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
                parameters.put("InvolvedUsers", "");
                parameters.put("ForumID", discussionForumModel.forumID);
                parameters.put("ForumName", discussionForumModel.name);
                parameters.put("strAttachFile", finalfileName);
                parameters.put("IsPin", false);

            } else {

                parameters.put("UserID", appUserModel.getUserIDValue());
                parameters.put("Title", titleStr);
                parameters.put("Description", descriptionStr);
                parameters.put("ForumID", discussionForumModel.forumID);
                parameters.put("OrgID", appUserModel.getSiteIDValue());
                parameters.put("InvolvedUsers", "");
                parameters.put("SiteID", appUserModel.getSiteIDValue());
                parameters.put("LocaleID", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
                parameters.put("ForumName", discussionForumModel.name);
                parameters.put("strAttachFile", finalfileName);
                parameters.put("strContentID", "");
                parameters.put("IsPin", false);

            }

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {

                sendNewForumDataToServer(parameterString, isUpdateForum);
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

            urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/CreateForumTopic";
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

                    topicID = "";
                    if (strSplitvalues.length > 1) {
                        topicID = strSplitvalues[1];
                    }

                    if (topicID.length() == 0 && isUpdateForum) {
                        topicID = discussionTopicModel.contentID;
                    }

                    String attachmentImg = editAttachment.getText().toString();

                    if (attachmentImg.length() > 7) {
                        Map<String, RequestBody> parameters = new HashMap<String, RequestBody>();
                        parameters.put("intUserID", toRequestBody(appUserModel.getUserIDValue()));
                        parameters.put("intSiteID", toRequestBody(appUserModel.getSiteIDValue()));
                        parameters.put("strLocale", toRequestBody(preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))));
                        parameters.put("TopicID", toRequestBody("" + topicID));
                        parameters.put("ReplyID", toRequestBody(""));
                        parameters.put("isTopic", toRequestBody("" + true));
                        Log.d(TAG, "onResponse: topicID " + topicID);
                        uploadFileThroughMultiPart(parameters, contentURIFinal);

                    }


                    if (topicID.length() > 1 && finalEncodedImageStr.length() > 10) {

                        //  sendTopicAttachmentDataToServer(finalEncodedImageStr, topicID, finalfileName);


                    } else if (isUpdateForum && finalEncodedImageStr.length() > 10) {

                        // sendTopicAttachmentDataToServer(finalEncodedImageStr, discussionTopicModel.contentID, finalfileName);

                    } else {
                        closeForum(true);
                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_newtopichasbeensuccessfullyadded), Toast.LENGTH_SHORT).show();
                    }

                } else if (s.contains("exist")) {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_topic_exist), Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong), Toast.LENGTH_SHORT).show();
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

    private void uploadFileThroughMultiPart(Map<String, RequestBody> parameters, Uri fileUri) {

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
                Log.v("Upload", "success");

                //  Toast.makeText(CreateNewTopicActivity.this, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
                closeForum(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(CreateNewTopicActivity.this, getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_unabletopostcomment), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
            }
        });
    }

}

