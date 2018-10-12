package com.instancy.instancylearning.askexpertenached;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourms.DiscussionCommentsActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UpvotersModel;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKRESPONSES;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

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


    AskExpertQuestionModel askExpertQuestionModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;

    boolean refreshAnswers = false;


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
    @BindView(R.id.txtcancel)
    TextView txtCancel;

    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;


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

        btnUpload.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_thumbs_o_up), null, null, null);
        btnUpload.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnUpload.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnUpload.setPadding(10, 2, 2, 2);

        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        vollyService = new VollyService(resultCallback, context);
        askExpertQuestionModel = (AskExpertQuestionModel) getIntent().getSerializableExtra("AskExpertQuestionModel");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "Answers" + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
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
                    validateNewForumCreation(editDescription.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txtcancel:
                finish();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public void attachFragment(DiscussionTopicModel forumModel) {
        Intent intentDetail = new Intent(context, DiscussionCommentsActivity.class);
        intentDetail.putExtra("topicModel", forumModel);
        startActivity(intentDetail);
    }

    public void validateNewForumCreation(String messageStr) throws JSONException {

        String dateString = getCurrentDateTime("dd/MM/yyyy");

        String dateStringSec = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

        if (messageStr.length() < 5) {
            Toast.makeText(AskExpertsAskAnsCmtActivity.this, "Answer cannot be empty", Toast.LENGTH_SHORT).show();
        } else {

            JSONObject parameters = new JSONObject();

            parameters.put("QuestionID", askExpertQuestionModel.questionID);
            parameters.put("ResponseText", messageStr);
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("Locale", "en-us");
            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {

                String replaceDataString = parameterString.replace("\"", "\\\"");
                String addQuotes = ('"' + replaceDataString + '"');
                AskExpertAnswerModel askExpertAnswerModel = new AskExpertAnswerModel();
                askExpertAnswerModel.respondeddate = dateString;
                askExpertAnswerModel.siteID = appUserModel.getSiteIDValue();
                askExpertAnswerModel.userID = appUserModel.getUserIDValue();
                askExpertAnswerModel.responsedate = dateStringSec;
                askExpertAnswerModel.questionID = askExpertQuestionModel.questionID;
                askExpertAnswerModel.response = messageStr;
                askExpertAnswerModel.responseid = appUserModel.getUserIDValue();
                askExpertAnswerModel.respondedusername = appUserModel.getUserName();
                sendNewForumDataToServer(addQuotes, askExpertAnswerModel);
            } else {
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendNewForumDataToServer(final String postData, final AskExpertAnswerModel askExpertAnswerModel) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/PostAskQuestionAnswer";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    String replaceString = s.replace("#$#", "=");
                    String[] strSplitvalues = replaceString.split("=");
                    refreshAnswers = true;
                    String replyID = "";
                    if (strSplitvalues.length > 1) {
                        replyID = strSplitvalues[1].replace("\"", "");
                        Log.d(TAG, "onResponse: " + replyID);
                        askExpertAnswerModel.responseid = replyID;
                        insertSingleAsktheExpertAnswerDataIntoSqLite(askExpertAnswerModel, 1);
                    }

                    Toast.makeText(AskExpertsAskAnsCmtActivity.this, "Success! \nYour Answer has been successfully posted ", Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(AskExpertsAskAnsCmtActivity.this, "New Answer cannot be posted . Contact site admin.", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AskExpertsAskAnsCmtActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
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

    public void insertSingleAsktheExpertAnswerDataIntoSqLite(AskExpertAnswerModel askExpertAnswerModel, int answerCount) {

        String insertStr = "INSERT INTO ASKRESPONSES (questionid, responseid, response, respondeduserid, respondedusername, respondeddate, responsedate, siteid ) VALUES (" +
                "" + askExpertAnswerModel.questionID +
                "," + askExpertAnswerModel.responseid +
                ",'" + askExpertAnswerModel.response +
                "','" + askExpertAnswerModel.respondeduserid +
                "','" + askExpertAnswerModel.respondedusername +
                "','" + askExpertAnswerModel.respondeddate +
                "','" + askExpertAnswerModel.responsedate +
                "'," + askExpertAnswerModel.siteID +
                ")";

        try {
            db.executeQuery(insertStr);


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWQS", refreshAnswers);
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

}

