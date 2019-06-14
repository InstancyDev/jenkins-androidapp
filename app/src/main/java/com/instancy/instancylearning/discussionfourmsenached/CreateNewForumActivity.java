package com.instancy.instancylearning.discussionfourmsenached;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.askexpertenached.AskQuestionActivity;
import com.instancy.instancylearning.askexpertenached.BasicAuthInterceptor;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Service;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.myskills.AddSkillModel;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.getAttachedFileTypeDrawable;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.getFileExtension;
import static com.instancy.instancylearning.utils.Utilities.getFileExtensionWithPlaceHolderImage;
import static com.instancy.instancylearning.utils.Utilities.getFileNameFromPath;
import static com.instancy.instancylearning.utils.Utilities.getMimeTypeFromUri;
import static com.instancy.instancylearning.utils.Utilities.getPath;
import static com.instancy.instancylearning.utils.Utilities.isBigFileThanExpected;
import static com.instancy.instancylearning.utils.Utilities.isFilevalidFileFound;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.toRequestBody;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class CreateNewForumActivity extends AppCompatActivity {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = CreateNewForumActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    DiscussionFourmsDbTables db;

    DiscussionForumModelDg discussionForumModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;

    UiSettingsModel uiSettingsModel;
    DiscussionModeratorModel discussionModeratorModel;

    String finalfileName = "", finalPath = "";
    Uri contentURIFinal;

    private int GALLERY = 1;

    @Nullable
    @BindView(R.id.txt_title)
    TextView labelTitle;

    @Nullable
    @BindView(R.id.txt_description)
    TextView labelDescritpion;


    @Nullable
    @BindView(R.id.txtcancel)
    TextView txtCancel;

    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;

    @Nullable
    @BindView(R.id.txtSettings)
    TextView txtSettings;

    @Nullable
    @BindView(R.id.edit_title)
    EditText editTitle;

    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

    @Nullable
    @BindView(R.id.txtModerator)
    TextView txtModerator;


    @Nullable
    @BindView(R.id.txtCategoriesCount)
    TextView txtCategoriesCount;

    @Nullable
    @BindView(R.id.editModerator)
    EditText editModerator;

    @Nullable
    @BindView(R.id.attachedimg)
    ImageView attachedImg;

    @Nullable
    @BindView(R.id.btnSelect)
    Button btnSelect;

    @Nullable
    @BindView(R.id.btnUpload)
    Button btnUpload;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    @Nullable
    @BindView(R.id.switchattachfiles)
    Switch switchAttachFiles;

    @Nullable
    @BindView(R.id.swtchnewtopic)
    Switch switchNewTopics;

    @Nullable
    @BindView(R.id.switchemailnotifications)
    Switch switchEmail;

    @Nullable
    @BindView(R.id.switchLikeorComment)
    Switch switchLikeorComment;

    @Nullable
    @BindView(R.id.switchShare)
    Switch switchShare;

    @Nullable
    @BindView(R.id.switchPintopic)
    Switch switchPintopic;

    @Nullable
    @BindView(R.id.txtPrivacy)
    TextView txtPrivacy;

    @Nullable
    @BindView(R.id.switchPrivacy)
    Switch switchPrivacy;

    Service service;

    List<ContentValues> breadcrumbItemsList = null;

    CustomFlowLayout tagsCategories;

    RelativeLayout lltagslayout;

    @BindView(R.id.txtCategoriesName)
    TextView txtCategoriesName;

    @BindView(R.id.txtCategories)
    TextView txtCategories;

    @BindView(R.id.txtCategoriesClear)
    TextView txtCategoriesClear;

    @BindView(R.id.tagsRelative)
    RelativeLayout tagsRelative;

    @BindView(R.id.txtCategoriesIcon)
    TextView txtCategoriesIcon;

    @BindView(R.id.lytCategories)
    RelativeLayout lytCategories;

    List<ContentValues> selectedCategories = new ArrayList<ContentValues>();

    List<String> selectedModeraArray = null;

//    List<String> selectedModerators = new ArrayList<>();

    boolean allowNotification = true, allowNewTopic = true, allowAttachFile = true, isUpdateForum = false, allowShare = true, allowLikeTopic = true, allowPin = true, allowPrivate = true;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, CreateNewForumActivity.this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewforumactivity);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();
        lltagslayout = (RelativeLayout) findViewById(R.id.lltagslayout);

        ButterKnife.bind(this);

        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        db = new DiscussionFourmsDbTables(this);

        txtSave.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        txtCancel.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));

        tagsCategories = (CustomFlowLayout) findViewById(R.id.cflBreadcrumb);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        discussionForumModel = new DiscussionForumModelDg();
        initilizeHeaderView();
        if (getIntent().getBooleanExtra("isfromedit", false)) {

            discussionForumModel = (DiscussionForumModelDg) getIntent().getSerializableExtra("forumModel");
            isUpdateForum = true;
            editTitle.setText(discussionForumModel.name);
            editDescription.setText(discussionForumModel.description);
            txtSave.setText(getLocalizationValue(JsonLocalekeys.details_button_updatebutton));
            updateUiForEditQuestion();
            if (discussionForumModel.categoriesIDArray != null && discussionForumModel.categoriesIDArray.size() > 0) {
                breadcrumbItemsList = generateSelectedContentValues(discussionForumModel.categoriesIDArray);
                if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {
                    updateBreadCrumbForEdit();
                    generateBreadcrumb(breadcrumbItemsList);
                }
            } else {
                clearCategory();
            }
        } else {
            btnUpload.setTag(0);
            isUpdateForum = false;
            clearCategory();
            txtSave.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newforumsavebutton));
        }

        List<DiscussionCategoriesModel> discussionCategoriesModelList = db.fetchDiscussionCategories(appUserModel.getSiteIDValue());

        if (discussionCategoriesModelList == null || discussionCategoriesModelList.size() == 0) {
            tagsRelative.setVisibility(View.GONE);
            txtCategories.setVisibility(View.GONE);
        }

        editTitle.setHint(getLocalizationValue(JsonLocalekeys.discussionforum_textfield_newtopictitletextfieldplaceholder));
        editDescription.setHint(getLocalizationValue(JsonLocalekeys.discussionforum_textview_newtopicdescriptionplaceholder));
        txtCancel.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newfourmcancelbutton));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.discussionforum_header_addforumtitlelabel) + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        if (!isUpdateForum) {
            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_upload), null, null, null);
        }

        btnUpload.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnUpload.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnUpload.setPadding(10, 2, 2, 2);

        btnSelect.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnSelect.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnSelect.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_selectbutton));

        // Multipart

        BasicAuthInterceptor interceptor = new BasicAuthInterceptor(appUserModel.getAuthHeaders());
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Change base URL to your upload server URL.
        service = new Retrofit.Builder().baseUrl(appUserModel.getWebAPIUrl() + "/MobileLMS/CreateEditForum/").client(client).build().create(Service.class);

    }

    public List<ContentValues> generateSelectedContentValues(List<String> editCategoryIds) {

        List<DiscussionCategoriesModel> discussionCategoriesModelList;
        discussionCategoriesModelList = db.fetchDiscussionCategories(appUserModel.getSiteIDValue());

        List<ContentValues> selectedCIds = new ArrayList<ContentValues>();

        for (int i = 0; i < editCategoryIds.size(); i++) {

            for (int j = 0; j < discussionCategoriesModelList.size(); j++) {

                if (editCategoryIds.get(i).equalsIgnoreCase("" + discussionCategoriesModelList.get(j).categoryID)) {
                    ContentValues cvBreadcrumbItem = new ContentValues();
                    cvBreadcrumbItem.put("categoryid", discussionCategoriesModelList.get(j).categoryID);
                    cvBreadcrumbItem.put("categoryname", discussionCategoriesModelList.get(j).fullName);

                    selectedCIds.add(cvBreadcrumbItem);
                }

            }

        }

        return selectedCIds;
    }


    public void updateUiForEditQuestion() {

        if (discussionForumModel.forumThumbnailPath.length() > 0) {

            String imgUrl = appUserModel.getSiteURL() + discussionForumModel.forumThumbnailPath;
            Glide.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachedImg);
            attachedImg.setVisibility(View.VISIBLE);
            changeBtnValue(true);

        } else {
            changeBtnValue(false);
            attachedImg.setVisibility(View.GONE);

        }

        if (isValidString(discussionForumModel.moderatorName)) {

            editModerator.setText(discussionForumModel.moderatorName);

        }

        if (discussionForumModel.isPrivate) {
            switchPrivacy.setChecked(true);
            allowPrivate = true;
        } else {
            switchPrivacy.setChecked(false);
            allowPrivate = false;
        }

        if (discussionForumModel.allowShare) {
            switchShare.setChecked(true);
            allowShare = true;
        } else {
            switchShare.setChecked(false);
            allowShare = false;
        }

        if (discussionForumModel.createNewTopic) {
            switchNewTopics.setChecked(true);
            allowNewTopic = true;
        } else {
            switchNewTopics.setChecked(false);
            allowNewTopic = false;
        }

        if (discussionForumModel.attachFile) {
            switchAttachFiles.setChecked(true);
            allowAttachFile = true;
        } else {
            switchAttachFiles.setChecked(false);
            allowAttachFile = false;
        }

        if (discussionForumModel.sendEmail) {
            switchEmail.setChecked(true);
            allowNotification = true;
        } else {
            switchEmail.setChecked(false);
            allowNotification = false;
        }

        if (discussionForumModel.likePosts) {
            switchLikeorComment.setChecked(true);
            allowLikeTopic = true;
        } else {
            switchLikeorComment.setChecked(false);
            allowLikeTopic = false;
        }

        if (discussionForumModel.active) {
            switchPintopic.setChecked(true);
            allowPin = true;
        } else {
            switchPintopic.setChecked(false);
            allowPin = false;
        }

    }


    public void changeBtnValue(boolean isAttachmentFound) {

        if (isAttachmentFound) {
            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_remove), null, null, null);
            btnUpload.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newforumremoveimagebutton));
            attachedImg.setVisibility(View.VISIBLE);
            btnUpload.setTag(1);
        } else {

            btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_upload), null, null, null);
            btnUpload.setTag(0);
            btnUpload.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newtopicselectfileuploadbutton));
            attachedImg.setVisibility(View.GONE);
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

    public void initilizeHeaderView() {

        labelTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        labelDescritpion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtModerator.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtSettings.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCategoriesCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCategoriesClear.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        txtCategoriesIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        txtCategoriesIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCategoriesName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCategories.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtPrivacy.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(txtCategoriesIcon, iconFont);

        labelDescritpion.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_newforumDescriptionlabel));
        txtModerator.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_moderatorlabel));
        txtSettings.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_newforumSettingslabel));
        txtCategoriesName.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_categorytitle));
        txtCategories.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_categorytitle));
        txtPrivacy.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_privacyoptionlabel));
        txtCategoriesIcon.setText(context.getResources().getString(R.string.fa_icon_sort_down));

        SpannableString styledTitle = new SpannableString("*" + getLocalizationValue(JsonLocalekeys.discussionforum_label_newtopictitlelabel) + ":");
        styledTitle.setSpan(new SuperscriptSpan(), 0, 1, 0);
        styledTitle.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
        styledTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledTitle.setSpan(new ForegroundColorSpan(Color.parseColor(uiSettingsModel.getAppTextColor())), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelTitle.setText(styledTitle);


        switchAttachFiles.setText(getLocalizationValue(JsonLocalekeys.discussionforum_tablesection_newforumattachfile));
        switchEmail.setText(getLocalizationValue(JsonLocalekeys.discussionforum_tablesection_newforumsendemail));
        switchNewTopics.setText(getLocalizationValue(JsonLocalekeys.discussionforum_tablesection_newforumcreatenewtopic));
        switchLikeorComment.setText(getLocalizationValue(JsonLocalekeys.discussionforum_tablesection_newforumliketopicorcomment));
        switchPintopic.setText(getLocalizationValue(JsonLocalekeys.discussionforum_tablesection_newforumpinthetopic));
        switchShare.setText(getLocalizationValue(JsonLocalekeys.discussionforum_tablesection_newforumsharewithconnectionsorpeople));
        switchPrivacy.setText(getLocalizationValue(JsonLocalekeys.discussionforum_tablesection_privateforum));

        switchAttachFiles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    allowAttachFile = true;
                } else {
                    allowAttachFile = false;
                }
            }
        });
        switchEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    allowNotification = true;
                } else {
                    allowNotification = false;
                }
            }
        });

        switchNewTopics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    allowNewTopic = true;
                } else {
                    allowNewTopic = false;
                }
            }
        });

        switchLikeorComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    allowLikeTopic = true;
                } else {
                    allowLikeTopic = false;
                }
            }
        });

        switchPintopic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    allowPin = true;
                } else {
                    allowPin = false;
                }
            }
        });

        switchShare.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    allowShare = true;
                } else {
                    allowShare = false;
                }
            }
        });

        switchPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    allowPrivate = true;
                } else {
                    allowPrivate = false;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switchNewTopics.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchEmail.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchAttachFiles.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchEmail.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchAttachFiles.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchNewTopics.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));

            switchNewTopics.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            switchAttachFiles.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            switchEmail.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            switchShare.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            switchPintopic.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            switchPrivacy.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            switchLikeorComment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            switchShare.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchShare.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));

            switchPintopic.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchPintopic.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));

            switchPrivacy.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchPrivacy.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));

            switchLikeorComment.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            switchLikeorComment.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));

        }

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


    public void validateNewForumCreation() throws JSONException {

        String descriptionStr = editDescription.getText().toString().trim();
        String titleStr = editTitle.getText().toString().trim();

        String categoryIds = generateCategoryIds();

        if (isUpdateForum && contentURIFinal == null) {
            finalfileName = discussionForumModel.forumThumbnailPath;
        }
        if (isUpdateForum && (Integer) btnUpload.getTag() == 0) {
            finalfileName = "";
        }

        String dateString = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
        int forumID = -1;
        String moderatorId = "0";
        if (isUpdateForum) {
            forumID = discussionForumModel.forumID;
            if (selectedModeraArray != null && selectedModeraArray.size() > 0) {
                moderatorId = getSelectedModeratorsIds(selectedModeraArray);
            } else {
                moderatorId = discussionForumModel.moderatorID;
            }
        } else {
//            if (discussionModeratorModel != null) {
            if (selectedModeraArray != null && selectedModeraArray.size() > 0) {
                moderatorId = getSelectedModeratorsIds(selectedModeraArray);
//                }
                // moderatorId = "" + discussionModeratorModel.userID;
            } else {
                moderatorId = "0";
            }
        }

        if (titleStr.length() < 1) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_enterforum), Toast.LENGTH_SHORT).show();
        } else if (!allowAttachFile && !allowShare && !allowLikeTopic && !allowNewTopic && !allowNotification && !allowPin && !allowPrivate) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_selectonesetting), Toast.LENGTH_SHORT).show();
        } else if (moderatorId.equalsIgnoreCase("0")) {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_select_moderator), Toast.LENGTH_SHORT).show();
        } else {

            Map<String, RequestBody> parameters = new HashMap<String, RequestBody>();
            parameters.put("CreatedUserID", toRequestBody(appUserModel.getUserIDValue()));
            parameters.put("SiteID", toRequestBody(appUserModel.getSiteIDValue()));
            parameters.put("locale", toRequestBody(preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))));
            parameters.put("ForumID", toRequestBody("" + forumID));
            parameters.put("Name", toRequestBody(titleStr));
            parameters.put("CategoryIDs", toRequestBody(categoryIds));
            parameters.put("Description", toRequestBody(descriptionStr));
            parameters.put("SendEmail", toRequestBody("" + allowNotification));
            parameters.put("CreateNewTopic", toRequestBody("" + allowNewTopic));
            parameters.put("AttachFile", toRequestBody("" + allowAttachFile));
            parameters.put("ParentForumID", toRequestBody("0"));
            parameters.put("IsPrivate", toRequestBody("" + allowPrivate));
            parameters.put("RequiresSubscription", toRequestBody("false"));
            parameters.put("LikePosts", toRequestBody("" + allowLikeTopic));
            parameters.put("Moderation", toRequestBody("false"));
            parameters.put("CreatedDate", toRequestBody(dateString));
            parameters.put("UpdatedDate", toRequestBody(dateString));
            parameters.put("UpdatedUserID", toRequestBody(appUserModel.getUserIDValue()));
            parameters.put("AllowShare", toRequestBody("" + allowShare));
            parameters.put("ModeratorID", toRequestBody(moderatorId));
            parameters.put("ForumThumbnailName", toRequestBody("" + finalfileName));

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {

                uploadFileThroughMultiPart(parameters, contentURIFinal);

            } else {

                Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void uploadFileThroughMultiPart(Map<String, RequestBody> parameters, Uri fileUri) {
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
            body = MultipartBody.Part.createFormData("ForumThumbnailName", file.getName(), requestFile);

        }

        // finally, execute the request
//        Call<ResponseBody> call = service.upload(description, body);
        Call<ResponseBody> call = service.uploadFileWithPartMap(parameters, body);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.v("Upload", "success");
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_newforumhasbeensuccessfullyposted), Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
                closeForum(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong), Toast.LENGTH_SHORT).show();
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
        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("ISSELECTED", false);
                if (refresh) {
//                    discussionModeratorModel = (DiscussionModeratorModel) data.getSerializableExtra("moderatorModel");

                    String selectedModerators = data.getStringExtra("selectedModerators");

                    selectedModeraArray = (List<String>) data.getSerializableExtra("selectedModeratorsIds");

                    // selectedModeraArray = getArrayListFromString(selectedModerators);

//                    List<DiscussionModeratorModel> discussionModeratorModelist = (List<DiscussionModeratorModel>) data.getSerializableExtra("discussionModeratorModelList");

                    editModerator.setText(selectedModerators);
//                    editModerator.setText(discussionModeratorModel.userName);
                }
            }
        }

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
                        attachedImg.setImageBitmap(bitmap);
                    } else {
                        attachedImg.setImageDrawable(typeIcon);
                    }
                    contentURIFinal = contentURI;
                    finalPath = getPath(context, contentURI);
                    contentURIFinal = contentURI;
                    finalfileName = fileName;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateNewForumActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_labelfailed), Toast.LENGTH_SHORT).show();

                }
            }
        }


//        if (requestCode == GALLERY) {
//            if (data != null) {
//                Uri contentURI = data.getData();
//                try {
//                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    final String fileName = getFileNameFromPath(contentURI, this);
//                    final String mimeType = getMimeTypeFromUri(contentURI);
//                    if (!isValidString(mimeType)) {
////                        Toast.makeText(context, "Invalid file type", Toast.LENGTH_SHORT).show();
////                        return;
//                    }
//                    Log.d(TAG, "onActivityResult: fileName=" + fileName + " mimeType =" + mimeType);
//                    final String fileExtension = getFileExtension(contentURI);
//                    Drawable typeIcon = getAttachedFileTypeDrawable(fileExtension, this, uiSettingsModel.getAppButtonBgColor());
//
////                    if (bitmap != null) {
////                        attachedImg.setImageBitmap(bitmap);
////                    } else {
////                        attachedImg.setImageDrawable(typeIcon);
////                    }
//
//                    btnUpload.setTag(1);
//                    finalPath = getPath(context, contentURI);
//                    contentURIFinal = contentURI;
//                    if (fileName.length() > 0) {
//                        changeBtnValue(true);
//                    } else {
//                        changeBtnValue(false);
//                    }
//                    attachedImg.setImageBitmap(bitmap);
//
//                    finalfileName = fileName;
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(CreateNewForumActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_labelfailed), Toast.LENGTH_SHORT).show();
//
//                }
//            }
//
//        }

        if (requestCode == FILTER_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                boolean refresh = data.getBooleanExtra("FILTER", false);
                if (refresh) {

                    selectedCategories = (List<ContentValues>) data.getExtras().getSerializable("selectedCategories");
                    Log.d(TAG, "selectedCategories: " + selectedCategories.size());
                    if (selectedCategories != null && selectedCategories.size() > 0) {

                        breadcrumbItemsList = new ArrayList<ContentValues>();
//                        breadcrumbItemsList = generateTagsList(skillModelListL);

                        if (selectedCategories != null && selectedCategories.size() > 0) {
                            generateBreadcrumb(selectedCategories);
                            breadcrumbItemsList = selectedCategories;
                            txtCategoriesName.setVisibility(View.GONE);
                            lytCategories.setVisibility(View.GONE);
                            txtCategoriesClear.setVisibility(View.VISIBLE);
                            tagsCategories.setVisibility(View.VISIBLE);
                        }

                    }

                }

            }
        }

    }

    public String getSelectedModeratorsIds(List<String> selectedModeraArray) {
        String generatedStr = "";

        if (selectedModeraArray != null) {
            for (int i = 0; i < selectedModeraArray.size(); i++) {

                if (generatedStr.length() > 0) {
                    generatedStr = generatedStr.concat("," + selectedModeraArray.get(i));
                } else {
                    generatedStr = "" + selectedModeraArray.get(i);
                }
            }
        }
        return generatedStr;
    }


    public List<String> getArrayListFromString(String moderatorsSelected) {

        List<String> questionCategoriesArray = new ArrayList<>();

        if (moderatorsSelected == null)
            return questionCategoriesArray;

        if (moderatorsSelected.length() <= 0)
            return questionCategoriesArray;

        questionCategoriesArray = Arrays.asList(moderatorsSelected.split(","));

        return questionCategoriesArray;

    }


    public void updateBreadCrumbForEdit() {
        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {
            txtCategoriesName.setVisibility(View.INVISIBLE);
            lytCategories.setVisibility(View.INVISIBLE);
            tagsRelative.setVisibility(View.VISIBLE);
            lltagslayout.setVisibility(View.VISIBLE);
            txtCategoriesClear.setVisibility(View.VISIBLE);
            tagsCategories.setVisibility(View.VISIBLE);

        }

    }


    public void clearCategory() {

        txtCategoriesCount.setText("");
        breadcrumbItemsList = new ArrayList<>();
        generateBreadcrumb(breadcrumbItemsList);
        txtCategoriesName.setVisibility(View.VISIBLE);
        lytCategories.setVisibility(View.VISIBLE);
        txtCategoriesClear.setVisibility(View.INVISIBLE);
        tagsCategories.setVisibility(View.INVISIBLE);
    }

    public List<ContentValues> generateTagsList(List<AddSkillModel> skillModelList) {
        List<ContentValues> tagsList = new ArrayList<>();

        int breadSkillLimit = 4;
        if (skillModelList.size() < 4)
            breadSkillLimit = skillModelList.size();
        else
            breadSkillLimit = 4;

        for (int i = 0; i < breadSkillLimit; i++) {
            ContentValues cvBreadcrumbItem = new ContentValues();
            cvBreadcrumbItem.put("categoryid", skillModelList.get(i).prefCategoryid);
            cvBreadcrumbItem.put("categoryname", skillModelList.get(i).preferrencetitle);
            cvBreadcrumbItem.put("preferenceId", skillModelList.get(i).preferenceId);
            tagsList.add(cvBreadcrumbItem);
        }

        return tagsList;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.txtsave, R.id.txtcancel, R.id.btnSelect, R.id.btnUpload, R.id.lltagslayout, R.id.txtCategoriesName, R.id.txtCategoriesClear})
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
            case R.id.btnSelect:
                chooseModerator();
                break;
            case R.id.btnUpload:
                if ((Integer) btnUpload.getTag() == 0) {
                    choosePhotoFromGallary();
                } else {
                    changeBtnValue(false);
                }
                break;
            case R.id.txtCategoriesName:
            case R.id.lltagslayout:
                chooseCategory();
                break;
            case R.id.txtCategoriesClear:
                clearCategory();
                break;
        }
    }

    public void chooseCategory() {
        Intent intentDetail = new Intent(context, DiscussionforumCategories.class);
        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {
            intentDetail.putExtra("breadcrumbItemsList", (Serializable) breadcrumbItemsList);
            intentDetail.putExtra("FILTER", true);
        } else {
            intentDetail.putExtra("FILTER", false);
        }

        startActivityForResult(intentDetail, FILTER_CLOSE_CODE);
    }

    public void chooseModerator() {
        Intent intentDetail = new Intent(context, DiscussionModeratorListActivity.class);
        if (selectedModeraArray != null && selectedModeraArray.size() > 0) {
            intentDetail.putExtra("moderatorIDArray", (Serializable) selectedModeraArray);
        } else {
            intentDetail.putExtra("moderatorIDArray", (Serializable) discussionForumModel.moderatorIDArray);
        }

        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(galleryIntent, GALLERY);
        } catch (ActivityNotFoundException notFound) {
            notFound.printStackTrace();
        }
    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWFORUM", refresh);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void generateBreadcrumb(List<ContentValues> dicBreadcrumbItems) {
        boolean isFirstCategory = true;
        ContentValues cvBreadcrumbItem = null;
        // int lastCategory = 10;
        tagsCategories.removeAllViews();
        int breadcrumbCount = dicBreadcrumbItems.size();
        View.OnClickListener onBreadcrumbItemCLick = null;
        onBreadcrumbItemCLick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                String categoryId = tv.getTag(R.id.CATALOG_CATEGORY_ID_TAG)
                        .toString();
                int categoryLevel = Integer.valueOf(tv.getTag(
                        R.id.CATALOG_CATEGORY_LEVEL_TAG).toString());

                String categoryName = tv.getText().toString();

                removeItemFromBreadcrumbListByLevel(categoryLevel);
                generateBreadcrumb(breadcrumbItemsList);

            }
        };

        for (int i = 0; i < breadcrumbCount; i++) {
            if (i == 0) {
                isFirstCategory = true;
            } else {
                isFirstCategory = false;
            }

            TextView textView = new TextView(context);
            TextView arrowView = new TextView(context);

            Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(arrowView, iconFont);

            arrowView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppTextColor() + "'><medium><b>"
                    + context.getResources().getString(R.string.fa_icon_angle_right) + "</b></big> </font>"));

            arrowView.setTextSize(12);

            arrowView.setGravity(Gravity.CENTER | Gravity.LEFT);
            arrowView.setVisibility(View.GONE);
            // String text = coountries[i];
            cvBreadcrumbItem = dicBreadcrumbItems.get(i);
            String categoryId = cvBreadcrumbItem.getAsString("categoryid");
            String categoryName = cvBreadcrumbItem.getAsString("categoryname");

//            textView.setText(Html.fromHtml("<font color='" + context.getResources().getColor(R.color.colorInGreen) + "'><big><b>"
//                    + categoryName + "</b></small>  </font>"));

            textView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppTextColor() + "'><small>"
                    + categoryName + "<b> X </b>" + "</small>  </font>"));

            textView.setGravity(Gravity.CENTER | Gravity.CENTER);
            textView.setTag(R.id.CATALOG_CATEGORY_ID_TAG, categoryId);
            textView.setTag(R.id.CATALOG_CATEGORY_LEVEL_TAG, i);
            // textView.setBackgroundColor(R.color.alert_no_button);
//            textView.setBackgroundColor(context.getResources().getColor(R.color.colorDarkGrey));
            textView.setBackground(context.getResources().getDrawable(R.drawable.cornersround));
            textView.setOnClickListener(onBreadcrumbItemCLick);
            textView.setClickable(true);
            if (!isFirstCategory) {
                tagsCategories.addView(arrowView, new CustomFlowLayout.LayoutParams(
                        CustomFlowLayout.LayoutParams.WRAP_CONTENT, 50));
            }
            tagsCategories.addView(textView, new CustomFlowLayout.LayoutParams(
                    CustomFlowLayout.LayoutParams.WRAP_CONTENT, CustomFlowLayout.LayoutParams.WRAP_CONTENT));
            if (breadcrumbCount > 1) {
                txtCategoriesCount.setVisibility(View.VISIBLE);
                txtCategoriesCount.setText("+" + breadcrumbCount);
            } else {
                txtCategoriesCount.setVisibility(View.GONE);
            }

        }

    }

    public void removeItemFromBreadcrumbListByLevel(int level) {

        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {

            breadcrumbItemsList.remove(level);
        }

        if (breadcrumbItemsList != null && breadcrumbItemsList.size() == 0) {

            clearCategory();


        }

    }

    public String generateCategoryIds() {
        String selectedSkills = "";

        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {

            for (int s = 0; s < breadcrumbItemsList.size(); s++) {
                if (selectedSkills.length() > 0) {
                    selectedSkills = selectedSkills.concat("," + breadcrumbItemsList.get(s).getAsString("categoryid"));
                } else {
                    selectedSkills = breadcrumbItemsList.get(s).getAsString("categoryid");
                }

                Log.d(TAG, "generateSkills: " + selectedSkills);

            }

        } else {
            selectedSkills = "";
        }

        return selectedSkills;
    }

}

