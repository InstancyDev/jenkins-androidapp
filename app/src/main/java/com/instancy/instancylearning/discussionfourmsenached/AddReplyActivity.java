package com.instancy.instancylearning.discussionfourmsenached;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.Menu;
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
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class AddReplyActivity extends AppCompatActivity {

    Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = AddReplyActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    DatabaseHandler db;

    DiscussionCommentsModelDg discussionCommentsModel;

    DiscussionReplyModelDg discussionReplyModel;

    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;

    String finalfileName = "";

    @Nullable
    @BindView(R.id.txtcancel)
    TextView txtCancel;

    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;

    @Nullable
    @BindView(R.id.lbReplay)
    TextView lbReplay;

    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    boolean isUpdateForum = false;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, AddReplyActivity.this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussionreplyactivity);
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
        lbReplay.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCancel.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));

        txtSave.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newforumsavebutton));
        txtCancel.setText(getLocalizationValue(JsonLocalekeys.discussionforum_button_newfourmcancelbutton));
        lbReplay.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_replylabel));

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);

        discussionCommentsModel = (DiscussionCommentsModelDg) getIntent().getSerializableExtra("commentModel");

        if (getIntent().getBooleanExtra("isfromedit", false)) {

            discussionReplyModel = (DiscussionReplyModelDg) getIntent().getSerializableExtra("replymodel");
            isUpdateForum = true;
            editDescription.setText(discussionReplyModel.message);
            txtSave.setText(getLocalizationValue(JsonLocalekeys.details_button_updatebutton));
        } else {

            isUpdateForum = false;
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.discussionforum_label_replylabel) + "  " + discussionCommentsModel.message + "</font>"));

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
//
//        SpannableString styledDescription
//                = new SpannableString("*"+getLocalizationValue(JsonLocalekeys.discussionforum_label_replylabel));
//        styledDescription.setSpan(new SuperscriptSpan(), 0, 1, 0);
//        styledDescription.setSpan(new RelativeSizeSpan(0.9f), 0, 1, 0);
//        styledDescription.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        styledDescription.setSpan(new ForegroundColorSpan(Color.parseColor(uiSettingsModel.getAppTextColor())), 1, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        editDescription.setHint(getLocalizationValue(JsonLocalekeys.discussionforum_label_newtopicdescriptionlabel));
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
                finish();

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

        String descriptionStr = editDescription.getText().toString().trim();

        int replyId = -1;

        if (isUpdateForum) {
            replyId = discussionReplyModel.replyID;
        }

        if (descriptionStr.length() < 1) {
            Toast.makeText(AddReplyActivity.this, getLocalizationValue(JsonLocalekeys.discussionforum_label_newtopicdescriptionlabel), Toast.LENGTH_SHORT).show();
        } else {

            JSONObject parameters = new JSONObject();

            parameters.put("TopicID", discussionCommentsModel.topicID);
            parameters.put("TopicName", "");
            parameters.put("ForumID", discussionCommentsModel.forumID);
            parameters.put("Message", descriptionStr);
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("InvolvedUserIDList", "");
            parameters.put("LocaleID", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("strAttachFile", finalfileName);
            parameters.put("strReplyID", replyId);
            parameters.put("strCommentID", discussionCommentsModel.commentID);

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {

                sendNewForumDataToServer(parameterString);
            } else {
                Toast.makeText(AddReplyActivity.this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sendNewForumDataToServer(final String postData) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        final String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/PostReply";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + responseStr);

                if (responseStr != null && responseStr.length() > 0) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(responseStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONArray jsonTableAry = null;
                    try {
                        jsonTableAry = jsonObject.getJSONArray("Table");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonTableAry != null && jsonTableAry.length() > 0) {

                        closeForum(true);
                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(AddReplyActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AddReplyActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
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
        request.setRetryPolicy(new

                DefaultRetryPolicy(
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

