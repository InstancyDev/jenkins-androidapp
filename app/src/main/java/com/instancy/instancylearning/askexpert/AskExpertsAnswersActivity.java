package com.instancy.instancylearning.askexpert;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.instancy.instancylearning.discussionfourms.CreateNewTopicActivity;
import com.instancy.instancylearning.discussionfourms.DiscussionCommentsActivity;
import com.instancy.instancylearning.discussionfourms.DiscussionTopicAdapter;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKQUESTIONS;
import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKQUESTIONSKILLS;
import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKRESPONSES;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class AskExpertsAnswersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = AskExpertsAnswersActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    DatabaseHandler db;
    List<AskExpertAnswerModel> askExpertAnswerModelList = null;
    ResultListner resultListner = null;
    AskExpertQuestionModel askExpertQuestionModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    AskExpertAnswerAdapter askExpertAnswerAdapter;

    boolean refreshAnswers = false;

    @Nullable
    @BindView(R.id.askexpertslistview)
    ListView askExpertsListView;

    @Nullable
    @BindView(R.id.txt_question)
    TextView txtQuestion;

    @Nullable
    @BindView(R.id.card_view)
    CardView card_view;

    @Nullable
    @BindView(R.id.txt_askedby)
    TextView txtAskedBy;

    @Nullable
    @BindView(R.id.txt_askedon)
    TextView txtAskedOn;

    @Nullable
    @BindView(R.id.txtno_answers)
    TextView txtNoAnswers;

    @Nullable
    @BindView(R.id.btn_contextmenu)
    ImageButton btnContextMenu;


    @Nullable
    @BindView(R.id.fab_comment_button)
    FloatingActionButton floatingActionButton;

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key, AskExpertsAnswersActivity.this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askexpertsanswersactivity);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DatabaseHandler(this);
        ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);


        vollyService = new VollyService(resultCallback, context);
        askExpertQuestionModel = (AskExpertQuestionModel) getIntent().getSerializableExtra("AskExpertQuestionModelDg");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.answers)) + "</font>");

        askExpertAnswerAdapter = new AskExpertAnswerAdapter(this, BIND_ABOVE_CLIENT, askExpertAnswerModelList);
        askExpertsListView.setAdapter(askExpertAnswerAdapter);

        askExpertsListView.setOnItemClickListener(this);
        askExpertsListView.setEmptyView(findViewById(R.id.nodata_label));



        askExpertAnswerModelList = new ArrayList<AskExpertAnswerModel>();

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        initilizeHeaderView();
        injectFromDbtoModel();
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomment, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        floatingActionButton.setImageDrawable(d);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCommentView();

            }
        });
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        if (appUserModel.getUserIDValue().equalsIgnoreCase(askExpertQuestionModel.postedUserId)) {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
    }

    public void openCommentView() {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.askcommentdialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(getLocalizationValue(JsonLocalekeys.siteurlsetting_alertbutton_submitbutton), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogBox, int id) {
                        // ToDo get user input here

                        try {

                            validateNewForumCreation(userInputDialogEditText.getText().toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton(getLocalizationValue(JsonLocalekeys.siteurlsetting_alertbutton_cancelbutton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });

        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialogAndroid.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            }
        });
        alertDialogAndroid.show();


    }

    public void initilizeHeaderView() {

        btnContextMenu.setVisibility(View.INVISIBLE);
        txtQuestion.setText(askExpertQuestionModel.userQuestion);
        txtAskedBy.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_label_askedbylabel) + " " + askExpertQuestionModel.username + " ");
        txtAskedOn.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_label_askedonlabel) + " " + askExpertQuestionModel.postedDate);
        txtNoAnswers.setText(askExpertQuestionModel.answers + getLocalizationValue(JsonLocalekeys.asktheexpert_label_answerslabel));
        txtQuestion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtNoAnswers.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAskedBy.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAskedOn.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

    }


    public void injectFromDbtoModel() {
        askExpertAnswerModelList = db.fetchAskExpertAnswersModelList("" + askExpertQuestionModel.questionID);
        if (askExpertAnswerModelList != null) {
            askExpertAnswerAdapter.refreshList(askExpertAnswerModelList);
        } else {
            askExpertAnswerModelList = new ArrayList<AskExpertAnswerModel>();
            askExpertAnswerAdapter.refreshList(askExpertAnswerModelList);
        }

        txtNoAnswers.setText(askExpertAnswerModelList.size() + getLocalizationValue(JsonLocalekeys.asktheexpert_label_answerslabel));
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

    @Override
    public void onRefresh() {
        if (isNetworkConnectionAvailable(context, -1)) {
//            refreshMyLearning(true);

            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("NEWFORUM", false);
                if (refresh) {
//                    refreshMyLearning(false);
                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.card_view:
//                attachFragment(askExpertAnswerModelList.get(position));
                break;
            case R.id.btn_contextmenu:
                View v = askExpertsListView.getChildAt(position - askExpertsListView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, askExpertAnswerModelList.get(position));
                break;
            default:

        }

    }

    public void attachFragment(DiscussionTopicModel forumModel) {
        Intent intentDetail = new Intent(context, DiscussionCommentsActivity.class);
        intentDetail.putExtra("topicModel", forumModel);
        startActivity(intentDetail);
    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final AskExpertAnswerModel askExpertAnswerModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.askexpertmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);//delete
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_deleteoption));//view
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_editoption));;//enroll
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId()==R.id.ctx_delete) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setCancelable(false).setTitle(getLocalizationValue(JsonLocalekeys.details_alerttitle_stringconfirmation)).setMessage(getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_areyousuretodeletetheanswer))
                            .setPositiveButton(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_deleteoption), new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialogBox, int id) {
                                    // ToDo get user input here
                                    deleteAnswerFromServer(askExpertAnswerModel);
                                }
                            })
                            .setNegativeButton(getLocalizationValue(JsonLocalekeys.asktheexpert_alertbutton_cancelbutton),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });

                    AlertDialog alertDialogAndroid = alertDialog.create();
                    alertDialogAndroid.show();
                }
                return true;
            }
        });
        popup.show();//showing popup menu

    }

    public void deleteAnswerFromServer(final AskExpertAnswerModel answerModel) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteAskResponse?ResponseID=" + answerModel.responseid;

        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
                // if (s.contains("success")) {
                if (s.contains(getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess))) {
                    Toast.makeText(context, " " + getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess_message), Toast.LENGTH_SHORT).show();
                    refreshAnswers = true;
                    deleteAnswerFromLocalDB(answerModel);
                } else {
                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_authenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);

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

    public void deleteAnswerFromLocalDB(AskExpertAnswerModel askExpertAnswerModel) {

        try {
            String strDelete = "DELETE FROM " + TBL_ASKRESPONSES + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND responseid  ='" + askExpertAnswerModel.responseid + "'";
            db.executeQuery(strDelete);

            injectFromDbtoModel();

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    public void validateNewForumCreation(String messageStr) throws JSONException {

        String dateString = getCurrentDateTime("dd/MM/yyyy");

        String dateStringSec = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

        if (messageStr.length() < 5) {
            Toast.makeText(AskExpertsAnswersActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_answercannotbeempty), Toast.LENGTH_SHORT).show();
        } else {

            JSONObject parameters = new JSONObject();

            parameters.put("QuestionID", askExpertQuestionModel.questionID);
            parameters.put("ResponseText", messageStr);
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
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
                Toast.makeText(AskExpertsAnswersActivity.this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
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

                   Toast.makeText(context, " " + getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess)
                            + " \n" + getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_answersuccessfullyposted), Toast.LENGTH_SHORT).show();



                } else {
                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_newanswer_authenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AskExpertsAnswersActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
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
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");


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

            injectFromDbtoModel();

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
}

