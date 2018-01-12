package com.instancy.instancylearning.discussionfourms;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://androidcocktail.blogspot.in/2014/03/android-spannablestring-example.html
 */

public class CreateNewForumActivity extends AppCompatActivity {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = CreateNewForumActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    DatabaseHandler db;
    ResultListner resultListner = null;


    DiscussionForumModel discussionForumModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;


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
    @BindView(R.id.switchattachfiles)
    Switch switchAttachFiles;

    @Nullable
    @BindView(R.id.swtchnewtopic)
    Switch switchNewTopics;

    @Nullable
    @BindView(R.id.switchemailnotifications)
    Switch switchEmail;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    boolean allowNotification = true, allowNewTopic = true, allowAttachFile = true, isUpdateForum = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createnewforumfragment);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DatabaseHandler(this);
        ButterKnife.bind(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);


        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        discussionForumModel = new DiscussionForumModel();
        if (getIntent().getBooleanExtra("isfromedit", false)) {

            discussionForumModel = (DiscussionForumModel) getIntent().getSerializableExtra("forumModel");
            isUpdateForum = true;
            editTitle.setText(discussionForumModel.name);
            editDescription.setText(discussionForumModel.descriptionValue);
        } else {

            isUpdateForum = false;
        }


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "New Forum" + "</font>"));


        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        initilizeHeaderView();

    }

    public void initilizeHeaderView() {


        labelTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        labelDescritpion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        SpannableString styledTitle
                = new SpannableString("*Tittle");
        styledTitle.setSpan(new SuperscriptSpan(), 0, 1, 0);
        styledTitle.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
        styledTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledTitle.setSpan(new ForegroundColorSpan(Color.BLACK), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelTitle.setText(styledTitle);

        SpannableString styledDescription
                = new SpannableString("*Description");
        styledDescription.setSpan(new SuperscriptSpan(), 0, 1, 0);
        styledDescription.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
        styledDescription.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledDescription.setSpan(new ForegroundColorSpan(Color.BLACK), 1, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        labelDescritpion.setText(styledDescription);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switchNewTopics.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
            switchEmail.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
            switchAttachFiles.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
            switchEmail.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
            switchAttachFiles.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
            switchNewTopics.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        }


        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
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

        String titleStr = editTitle.getText().toString().trim();
        String descriptionStr = editDescription.getText().toString().trim();
        String dateString = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

        if (titleStr.length() < 4) {
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
        } else if (descriptionStr.length() < 10) {
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show();
        } else {
//            Map<String, String> parameters = new HashMap<String, String>();
            JSONObject parameters = new JSONObject();
            if (isUpdateForum) {
                parameters.put("ParentForumID", "0");
                parameters.put("CreateNewTopic", "" + allowNewTopic);
                parameters.put("SiteID", appUserModel.getSiteIDValue());
                parameters.put("LikePosts", "true");
                parameters.put("SendEmail", "" + allowNotification);
                parameters.put("ForumID", "" + discussionForumModel.forumid);
                parameters.put("IsPrivate", "false");
                parameters.put("Moderation", "true");
                parameters.put("CreatedDate", "" + discussionForumModel.createddate);
                parameters.put("Description", descriptionStr);
                parameters.put("CreatedUserID", appUserModel.getUserIDValue());
                parameters.put("RequiresSubscription", "true");
                parameters.put("Name", titleStr);
                parameters.put("AttachFile", "" + allowAttachFile);
            } else {

                parameters.put("ParentForumID", "0");
                parameters.put("CreateNewTopic", "" + allowNewTopic);
                parameters.put("SiteID", appUserModel.getSiteIDValue());
                parameters.put("LikePosts", "true");
                parameters.put("SendEmail", "" + allowNotification);
                parameters.put("ForumID", "-1");
                parameters.put("IsPrivate", "false");
                parameters.put("Moderation", "true");
                parameters.put("CreatedDate", dateString);
                parameters.put("Description", descriptionStr);
                parameters.put("CreatedUserID", appUserModel.getUserIDValue());
                parameters.put("RequiresSubscription", "true");
                parameters.put("Name", titleStr);
                parameters.put("AttachFile", "" + allowAttachFile);

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

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/CreateEditForum";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, "Success! \nYour new forum has been successfully posted to server.", Toast.LENGTH_SHORT).show();
                    closeForum(true);
                } else {

                    Toast.makeText(context, "New forum cannot be posted to server. Contact site admin.", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("NEWFORUM", refresh);
        setResult(RESULT_OK, intent);
        finish();
    }
}

