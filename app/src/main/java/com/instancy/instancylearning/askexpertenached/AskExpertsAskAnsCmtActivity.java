package com.instancy.instancylearning.askexpertenached;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Service;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
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
import static com.instancy.instancylearning.utils.Utilities.getAttachedFileTypeDrawable;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.getFileExtension;
import static com.instancy.instancylearning.utils.Utilities.getFileExtensionWithPlaceHolderImage;
import static com.instancy.instancylearning.utils.Utilities.getFileNameFromPath;
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
 */

public class AskExpertsAskAnsCmtActivity extends AppCompatActivity {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = AskExpertsAskAnsCmtActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    DatabaseHandler db;
    private int GALLERY = 1;

    AskExpertQuestionModelDg askExpertQuestionModel;
    AskExpertAnswerModelDg askExpertAnswerModelDg;
    AskExpertCommentModel askExpertCommentModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;

    boolean refreshAnswers = false;

    @Nullable
    @BindView(R.id.txtTitle)
    TextView labelTitle;

    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

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
    @BindView(R.id.txtcancel)
    TextView txtCancel;

    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    String titleStr = "";

    String finalfileName = "", finalPath = "";

    Uri contentURIFinal;

    Service service;

    boolean isAskAnswer = false;

    boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askexpertans_cmtactivity);
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

        btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_upload), null, null, null);
        btnUpload.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnUpload.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnUpload.setPadding(10, 2, 2, 2);
        btnUpload.setTag(0);
        attachmentThumb.setVisibility(View.GONE);

        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        txtSave.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_alertbutton_submitbutton));
        txtCancel.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_alertbutton_cancelbutton));

        vollyService = new VollyService(resultCallback, context);
        askExpertQuestionModel = (AskExpertQuestionModelDg) getIntent().getSerializableExtra("AskExpertQuestionModelDg");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            isAskAnswer = bundle.getBoolean("ISASKANSWER", false);
            isEdit = bundle.getBoolean("EDIT", false);
        }

        BasicAuthInterceptor interceptor = new BasicAuthInterceptor(appUserModel.getAuthHeaders());
//      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if (isAskAnswer) {
            titleStr = getLocalizationValue(JsonLocalekeys.asktheexpert_answerheader);

            service = new Retrofit.Builder().baseUrl(appUserModel.getWebAPIUrl() + "/MobileLMS/InsertEditQuestionResponse/").client(client).build().create(Service.class);
            if (isEdit) {
                askExpertAnswerModelDg = (AskExpertAnswerModelDg) getIntent().getSerializableExtra("AskExpertAnswerModelDg");
                updateUiForEditQuestion();
            } else {
                askExpertAnswerModelDg = new AskExpertAnswerModelDg();
            }
            editDescription.setHint(getLocalizationValue(JsonLocalekeys.asktheexpert_textview_entertheanswerplaceholder));
            labelTitle.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_label_answerlabel));
        } else {
            titleStr = getLocalizationValue(JsonLocalekeys.asktheexpert_commentheader);
            askExpertAnswerModelDg = (AskExpertAnswerModelDg) getIntent().getSerializableExtra("AskExpertAnswerModelDg");
            service = new Retrofit.Builder().baseUrl(appUserModel.getWebAPIUrl() + "/MobileLMS/InsertEditResponseComments/").client(client).build().create(Service.class);
            if (isEdit) {
                askExpertCommentModel = (AskExpertCommentModel) getIntent().getSerializableExtra("AskExpertCommentModel");
                updateUiForEditComment();
            } else {
                askExpertCommentModel = new AskExpertCommentModel();
            }
            editDescription.setHint(getLocalizationValue(JsonLocalekeys.asktheexpert_textview_enterthecommentplaceholder));
            labelTitle.setText(titleStr);
        }
        // Change base URL to your upload server URL.


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                titleStr + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
    }

    public void updateUiForEditQuestion() {
        editDescription.setText(askExpertAnswerModelDg.response);

        if (isValidString(askExpertAnswerModelDg.userResponseImagePath)) {

//            String imgUrl = appUserModel.getSiteURL() + askExpertAnswerModelDg.userResponseImagePath;
//            Glide.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachmentThumb);
//            attachmentThumb.setVisibility(View.VISIBLE);
//            editAttachment.setText("");
//            changeBtnValue(true);
            changeBtnValue(true, true);
        } else {

            attachmentThumb.setVisibility(View.GONE);
            changeBtnValue(false, true);
        }

    }

    public void updateUiForEditComment() {
        editDescription.setText(askExpertCommentModel.commentDescription);

        if (isValidString(askExpertCommentModel.commentImage)) {

//            String imgUrl = appUserModel.getSiteURL() + askExpertCommentModel.usercCmntImagePath;
//            Glide.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachmentThumb);
//            attachmentThumb.setVisibility(View.VISIBLE);
//            editAttachment.setText("");
            // changeBtnValue(true);
            changeBtnValue(true, false);
        } else {
            changeBtnValue(false, false);
//            attachmentThumb.setVisibility(View.GONE);

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


    @Override
    public void onBackPressed() {
        closeForum(refreshAnswers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tracklistmenu, menu);
        MenuItem itemInfo = menu.findItem(R.id.tracklist_help);
        Drawable myIcon = getResources().getDrawable(R.drawable.help);
        itemInfo.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
        itemInfo.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                closeForum(refreshAnswers);
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

    @OnClick({R.id.txtsave, R.id.txtcancel})
    public void actionsBottomBtns(View view) {

        switch (view.getId()) {
            case R.id.txtsave:
                try {
                    if (isAskAnswer) {
                        validateAnswerCreation(editDescription.getText().toString());
                    } else {
                        validateCommentCreation(editDescription.getText().toString());
                    }
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
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void changeBtnValue(boolean isAttachmentFound, boolean isAskAnswer) {

        if (isAttachmentFound) {
            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_remove), null, null, null);
            btnUpload.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newforumremoveimagebutton));
            attachmentThumb.setVisibility(View.VISIBLE);
            btnUpload.setTag(1);
            if (isEdit) {
                String imgUrl = "";
                String fileExtesnion = "";
                if (isAskAnswer) {
                    imgUrl = appUserModel.getSiteURL() + askExpertAnswerModelDg.userResponseImagePath;
                    fileExtesnion = getFileExtensionWithPlaceHolderImage(askExpertAnswerModelDg.userResponseImagePath);
                } else {
                    imgUrl = appUserModel.getSiteURL() + askExpertCommentModel.usercCmntImagePath;
                    fileExtesnion = getFileExtensionWithPlaceHolderImage(askExpertCommentModel.usercCmntImagePath);
                }

                int resourceId = 0;

                resourceId = gettheContentTypeNotImg(fileExtesnion);
                if (resourceId == 0)
                    Glide.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachmentThumb);
                else
                    attachmentThumb.setImageDrawable(getDrawableFromStringWithColor(this, resourceId, uiSettingsModel.getAppButtonBgColor()));
            }

        } else {
            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_upload), null, null, null);
            btnUpload.setTag(0);
            btnUpload.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newtopicselectfileuploadbutton));
            attachmentThumb.setVisibility(View.GONE);
            finalfileName = "";
        }
        btnUpload.setPadding(10, 2, 2, 2);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        super.onActivityResult(requestCode, resultCode, data);

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

                // final String fileExtension = getFileExtension(contentURI);

                Drawable typeIcon = getAttachedFileTypeDrawable(fileExtesnion, this, uiSettingsModel.getAppButtonBgColor());

                if (fileName.length() > 0) {
                    changeBtnValue(true, isAskAnswer);
                } else {
                    changeBtnValue(false, isAskAnswer);
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
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_labelfailed), Toast.LENGTH_SHORT).show();

            }
        }


//        if (data != null) {
//            Uri contentURI = data.getData();
//            try {
//                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                final String fileName = getFileNameFromPath(contentURI, this);
//
//                final String filePathHere = getPath(context, contentURI);
//
//                if (!isValidString(filePathHere)) {
//                    Toast.makeText(context, "Invalid file type", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                final String fileExtension = getFileExtension(contentURI);
//                Drawable typeIcon = getAttachedFileTypeDrawable(fileExtension, this, uiSettingsModel.getAppButtonBgColor());
//
//                if (fileName.length() > 0) {
//                    changeBtnValue(true, isAskAnswer);
//                } else {
//                    changeBtnValue(false, isAskAnswer);
//                }
//
//                if (bitmap != null) {
//                    attachmentThumb.setImageBitmap(bitmap);
//                } else {
//                    attachmentThumb.setImageDrawable(typeIcon);
//                }
//
//                Log.d(TAG, "onActivityResult: " + fileName);
//                editAttachment.setText(fileName);
////                attachmentThumb.setImageBitmap(bitmap);
//                btnUpload.setTag(1);
////                uploadFile(contentURI);
//                finalPath = getPath(context, contentURI);
//
//                contentURIFinal = contentURI;
//
//                finalfileName = fileName;
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(AskExpertsAskAnsCmtActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_labelfailed), Toast.LENGTH_SHORT).show();
//
//            }
//        }

    }

    public void validateAnswerCreation(String messageStr) {

        boolean isAttachmentRmoved = false;
        int responseID = -1;

        if (isEdit && (Integer) btnUpload.getTag() == 0) {
            finalfileName = askExpertAnswerModelDg.userResponseImage;
            if (!messageStr.equalsIgnoreCase(askExpertAnswerModelDg.response)) {
                if (isEdit && contentURIFinal == null && (Integer) btnUpload.getTag() == 0) {
                    isAttachmentRmoved = true;
                } else {
                    isAttachmentRmoved = false;
                }
            } else {
                isAttachmentRmoved = true;
            }
        }

        if (isEdit && contentURIFinal == null) {
            finalfileName = askExpertAnswerModelDg.userResponseImage;
        }

        if (isEdit) {
            responseID = askExpertAnswerModelDg.responseID;
        }

        if (messageStr.length() < 2) {
            Toast.makeText(AskExpertsAskAnsCmtActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_answercannotbeempty), Toast.LENGTH_SHORT).show();
        } else {

            Map<String, RequestBody> parameters = new HashMap<String, RequestBody>();

            parameters.put("UserID", toRequestBody(appUserModel.getUserIDValue()));
            parameters.put("SiteID", toRequestBody(appUserModel.getSiteIDValue()));
            parameters.put("UserEmail", toRequestBody(appUserModel.getUserLoginId()));
            parameters.put("UserName", toRequestBody(appUserModel.getUserName()));
            parameters.put("Response", toRequestBody(messageStr));
            parameters.put("UserResponseImageName", toRequestBody(finalfileName));
            parameters.put("ResponseID", toRequestBody("" + responseID));
            parameters.put("QuestionID", toRequestBody("" + askExpertQuestionModel.questionID));
            parameters.put("IsRemoveEditimage", toRequestBody("" + isAttachmentRmoved));

            if (isNetworkConnectionAvailable(this, -1)) {

                respondToQuestion(parameters, contentURIFinal, askExpertAnswerModelDg);

            } else {
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void validateCommentCreation(String messageStr) throws JSONException {

        boolean isAttachmentRmoved = false;
        int commentId = -1;

        if (isEdit && (Integer) btnUpload.getTag() == 0) {
            isAttachmentRmoved = true;
            finalfileName = askExpertCommentModel.commentImage;
            if (!messageStr.equalsIgnoreCase(askExpertAnswerModelDg.response)) {
                if (isEdit && contentURIFinal == null && (Integer) btnUpload.getTag() == 0) {
                    isAttachmentRmoved = true;
                } else {
                    isAttachmentRmoved = false;
                }
            } else {
                isAttachmentRmoved = true;
            }
        }
        if (isEdit && contentURIFinal == null) {
            finalfileName = askExpertCommentModel.commentImage;
            //isAttachmentRmoved = false;
        }

//        if (isEdit && contentURIFinal == null) {
//            finalfileName = askExpertCommentModel.commentImage;
//        }
//        if (isEdit && (Integer) btnUpload.getTag() == 0) {
//            isAttachmentRmoved = true;
//            if (!messageStr.equalsIgnoreCase(askExpertAnswerModelDg.response)) {
//                isAttachmentRmoved = false;
//            }
//        }

        if (isEdit) {
            commentId = askExpertCommentModel.commentID;
        }
        if (messageStr.length() < 2) {
            Toast.makeText(AskExpertsAskAnsCmtActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_commentcannotbeempty), Toast.LENGTH_SHORT).show();
        } else {

            Map<String, RequestBody> parameters = new HashMap<String, RequestBody>();

            parameters.put("UserID", toRequestBody(appUserModel.getUserIDValue()));
            parameters.put("SiteID", toRequestBody(appUserModel.getSiteIDValue()));
            parameters.put("ResponseID", toRequestBody("" + askExpertAnswerModelDg.responseID));
            parameters.put("QuestionID", toRequestBody("" + askExpertAnswerModelDg.questionID));
            parameters.put("CommentID", toRequestBody("" + commentId));
            parameters.put("Comment", toRequestBody(messageStr));
            parameters.put("UserCommentImage", toRequestBody(finalfileName));
            parameters.put("IsRemoveCommentImage", toRequestBody("" + isAttachmentRmoved));

            if (isNetworkConnectionAvailable(this, -1)) {

                commentTheAnswer(parameters, contentURIFinal);

            } else {
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWCMNT", refreshAnswers);
        setResult(RESULT_OK, intent);
        finish();
    }

    public List<ContentValues> generateTagsList(int position) {
        List<ContentValues> tagsList = new ArrayList<>();

        for (int i = 1; i < position; i++) {
            ContentValues cvBreadcrumbItem = new ContentValues();
            cvBreadcrumbItem.put("categoryid", i);
            cvBreadcrumbItem.put("categoryname", "Skill " + i);
            tagsList.add(cvBreadcrumbItem);

        }

        return tagsList;
    }


    @SuppressLint("ResourceAsColor")
    public Drawable getDrawableFromString(Context context, int resourceID) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    private void respondToQuestion(Map<String, RequestBody> parameters, Uri fileUri, final AskExpertAnswerModelDg askExpertAnswerModel) {
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
                Log.v("Upload", "success");
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess) + "\n" + getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_answersuccessfullyposted), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
                refreshAnswers = true;
                closeForum(refreshAnswers);
                String responseRecieved = null;
                try {
                    responseRecieved = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("Upload", "success responseRecieved" + responseRecieved);
                // Add Your Logic

//                "Table": [{
//                           "QuestionID": 23,
//                            "UserID": 1,
//                            "UserName": "Peter Fail",
//                            "UserQuestion": "asasasdsd",
//                            "UserEmail": "admin@instancy.com",
//                            "CreatedDate": "Dec 05, 2018",
//                            "RespondedUserID": 1,
//                            "ResponseID": 9,
//                            "ResponseDate": "2018-12-05T19:07:02.01",
//                            "Response": "respnse&nbsp;",
//                            "RespondedUserName": "",
//                            "RespondedDate": "05/12/2018",
//                            "UserResponseImage": "",
//                            "NotifyMessage": ""
//                } ]

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseRecieved);
                    if (jsonObject != null && jsonObject.length() > 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Table");
                        if (jsonArray != null && jsonArray.length() > 0) {

                            JSONObject columnObj = jsonArray.getJSONObject(0);

                            //       {"QuestionID":30,"UserID":373,"UserName":"View User","UserQuestion":"QA","UserEmail":"view@mail.com","CreatedDate":"Mar 04, 2019","RespondedUserID":1,"ResponseID":1,"ResponseDate":"2019-03-15T12:38:00.227","Response":"answer","RespondedUserName":"?????? ??????????? ?????? ????????????????? ???????????","RespondedDate":"15\/03\/2019","UserResponseImage":""}

                            askExpertAnswerModel.questionID = columnObj.optInt("QuestionID");
                            askExpertAnswerModel.userID = columnObj.optInt("UserID");
                            askExpertAnswerModel.respondedUserName = columnObj.optString("UserName");
                            askExpertAnswerModel.questionForMail = columnObj.optString("UserQuestion");
                            askExpertAnswerModel.userMail = columnObj.optString("UserEmail");
                            askExpertAnswerModel.createdDateMail = columnObj.optString("CreatedDate");
                            askExpertAnswerModel.respondedUserId = columnObj.optInt("RespondedUserID");
                            askExpertAnswerModel.responseID = columnObj.optInt("ResponseID");
                            askExpertAnswerModel.respondeDate = columnObj.optString("ResponseDate");
                            askExpertAnswerModel.response = columnObj.optString("Response");
                            askExpertAnswerModel.respondeDate = columnObj.optString("RespondedDate");
                            askExpertAnswerModel.userResponseImage = columnObj.optString("UserResponseImage");
//                            askExpertAnswerModelDg. = columnObj.optInt("NotifyMessage");

                            try {
                                callSendExpertMailsForAnswer(askExpertAnswerModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_newanswer_authenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
            }
        });
    }

    private void commentTheAnswer(Map<String, RequestBody> parameters, Uri fileUri) {
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
                Log.v("Upload", "success");
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_commentsuccessfullyposted), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
                refreshAnswers = true;
                closeForum(refreshAnswers);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_newcomment_authenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
            }
        });
    }

    public void callSendExpertMailsForAnswer(AskExpertAnswerModelDg answerModelDg) throws JSONException {

        if (isNetworkConnectionAvailable(this, -1)) {

            String parmStringUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/sendMail?" +
                    "userName=" + appUserModel.getUserName() +
                    "&userId=" + appUserModel.getUserIDValue() +
                    "&response=" + answerModelDg.response +
                    "&siteId=" + appUserModel.getSiteIDValue() +
                    "&localeId=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "" +
                    "&toEmail=" + answerModelDg.userMail +
                    "&userquestion=" + askExpertQuestionModel.userQuestion +
                    "&fromEmail=" + appUserModel.getUserLoginId();

            parmStringUrl = parmStringUrl.replaceAll(" ", "%20");

            vollyService.getStringResponseVolley("sendMail", parmStringUrl, appUserModel.getAuthHeaders());

        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, AskExpertsAskAnsCmtActivity.this);

    }

}

