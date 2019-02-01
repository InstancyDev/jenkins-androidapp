package com.instancy.instancylearning.askexpertenached;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
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
import android.support.v7.widget.CardView;
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
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UpvotersModel;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKRESPONSES;
import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKRESPONSES_DIGI;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

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
    AskExpertDbTables db;
    List<AskExpertAnswerModelDg> askExpertAnswerModelList = null;
    ResultListner resultListner = null;
    AskExpertQuestionModelDg askExpertQuestionModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    AskExpertAnswerAdapter askExpertAnswerAdapter;

    BottomSheetDialog bottomSheetDialog;

    boolean refreshAnswers = false;

    UpvotersAdapter upvotersAdapter;

    List<AskExpertUpVoters> upvotersModelList;

    @Nullable
    @BindView(R.id.askexpertslistview)
    ListView askExpertsListView;

    View header;

    View footor;

    @Nullable
    @BindView(R.id.nodata_label)
    TextView noDataLabel;

    @Nullable
    @BindView(R.id.topicheader)
    RelativeLayout topicheader;

    @Nullable
    @BindView(R.id.fab_comment_button)
    FloatingActionButton floatingActionButton;

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    SideMenusModel sideMenusModel;

    TextView txtNoAnswers;

    boolean nextLevel = false;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, AskExpertsAnswersActivity.this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askexpertsanswersactivity);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new AskExpertDbTables(this);
        ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        initVolleyCallback();
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        topicheader.setVisibility(View.GONE);


        header = (View) getLayoutInflater().inflate(R.layout.askexpertcell_en, null);
        footor = (View) getLayoutInflater().inflate(R.layout.no_data_layout, null);

        vollyService = new VollyService(resultCallback, context);

        nextLevel = getIntent().getBooleanExtra("nextLevel", false);


        askExpertQuestionModel = (AskExpertQuestionModelDg) getIntent().getSerializableExtra("AskExpertQuestionModelDg");

        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sidemenumodel");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.answers) + "</font>"));

        askExpertAnswerAdapter = new AskExpertAnswerAdapter(this, BIND_ABOVE_CLIENT, askExpertAnswerModelList);
        askExpertsListView.setAdapter(askExpertAnswerAdapter);
        askExpertsListView.setOnItemClickListener(this);
        askExpertsListView.addFooterView(footor);
        askExpertAnswerModelList = new ArrayList<AskExpertAnswerModelDg>();
        askExpertsListView.addHeaderView(header, null, false);

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        initilizeHeaderView(header);

        noDataLabel.setVisibility(View.INVISIBLE);
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomment, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        floatingActionButton.setImageDrawable(d);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCommentAndAnswerActivity(false, -1);

            }
        });
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));


        if (isValidString(askExpertQuestionModel.answerBtnWithLink)) {
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }

//        if (db.isPrivilegeExistsFor(StaticValues.REPLYTOQUESTION)) {
//            floatingActionButton.setVisibility(View.VISIBLE);
//        } else {
//            floatingActionButton.setVisibility(View.INVISIBLE);
//        }

        if (isNetworkConnectionAvailable(this, -1)) {
            refreshForAnswers(false);
        } else {
            injectFromDbtoModel();
        }

    }

    public void askCommentFromAnswerActivity(AskExpertAnswerModelDg askExpertAnswerModelDg) {

        Intent intentDetail = new Intent(context, AskExpertsAskAnsCmtActivity.class);
        intentDetail.putExtra("AskExpertAnswerModelDg", askExpertAnswerModelDg);
        intentDetail.putExtra("ISASKANSWER", false);
        intentDetail.putExtra("EDIT", false);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

    }

    public void openCommentAndAnswerActivity(boolean isEdit, int position) {

        Intent intentDetail = new Intent(context, AskExpertsAskAnsCmtActivity.class);
        if (position != -1) {
            AskExpertAnswerModelDg answerModelDg = askExpertAnswerModelList.get(position);
            intentDetail.putExtra("EDIT", isEdit);
            intentDetail.putExtra("AskExpertAnswerModelDg", answerModelDg);
        }
        intentDetail.putExtra("AskExpertQuestionModelDg", askExpertQuestionModel);
        intentDetail.putExtra("ISASKANSWER", true);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
    }

    public void refreshForAnswers(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }

//        String parmStringUrl = "http://angular6api.instancysoft.com/api/MobileLMS/GetAsktheExpertData?intSiteID=" + appUserModel.getSiteIDValue() + "&UserID=" + appUserModel.getUserIDValue() + "&astrLocale="+ preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))+"&aintComponentID=" + sideMenusModel.getComponentId() + "&aintCompInsID=" + sideMenusModel.getRepositoryId() + "&aintSelectedGroupValue=0";

        String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetUserQuestionsResponses?UserID=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getSiteIDValue() + "&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&ComponentID=" + sideMenusModel.getComponentId() + "&intQuestionID=" + askExpertQuestionModel.questionID;

        vollyService.getStringResponseVolley("GetUserQuestionsResponses", parmStringUrl, appUserModel.getAuthHeaders());

    }


    public void openCommentsActivity(boolean isFrom, AskExpertAnswerModelDg askExpertAnswerModel) {

        Intent intentDetail = new Intent(context, AskExpertsCommentsActivity.class);
        intentDetail.putExtra("AskExpertQuestionModelDg", askExpertQuestionModel);
        intentDetail.putExtra("askExpertAnswerModel", askExpertAnswerModel);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

    }

    public void initilizeHeaderView(View view) {

        ImageButton btnContextMenu = (ImageButton) view.findViewById(R.id.btn_contextmenu);
        TextView txtAllActivites = (TextView) view.findViewById(R.id.txt_all_activites);
        txtNoAnswers = (TextView) view.findViewById(R.id.txtno_answers);
        TextView txtQuestion = (TextView) view.findViewById(R.id.txt_question);
        TextView txtDescription = (TextView) view.findViewById(R.id.txt_description);
        TextView txtNoViews = (TextView) view.findViewById(R.id.txtno_views);
        CardView card_view = (CardView) view.findViewById(R.id.card_view);

        ImageView imagethumb = (ImageView) view.findViewById(R.id.imagethumb);

        CustomFlowLayout tagsSkills = (CustomFlowLayout) view.findViewById(R.id.cflBreadcrumb);

        btnContextMenu.setVisibility(View.INVISIBLE);
        txtQuestion.setText(askExpertQuestionModel.userQuestion);
        txtAllActivites.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_askedbylabel, AskExpertsAnswersActivity.this) + " " + askExpertQuestionModel.userName + " ");

        txtAllActivites.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_askedbylabel, AskExpertsAnswersActivity.this) + " " + askExpertQuestionModel.userName + "   |   " + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_askedonlabel, AskExpertsAnswersActivity.this) + " " + askExpertQuestionModel.postedDate + "   |   " + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_lastactivelabel, AskExpertsAnswersActivity.this) + " " + askExpertQuestionModel.postedDate);

        txtNoAnswers.setText(askExpertQuestionModel.totalAnswers + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_answerslabel, AskExpertsAnswersActivity.this));
        txtNoViews.setText(askExpertQuestionModel.totalViews + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_viewslabel, AskExpertsAnswersActivity.this));
        txtDescription.setText(askExpertQuestionModel.userQuestionDescription);

        txtQuestion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtNoAnswers.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAllActivites.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDescription.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtNoViews.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        if (askExpertQuestionModel.userQuestionImage.length() > 0) {

            String imgUrl = appUserModel.getSiteURL() + askExpertQuestionModel.userQuestionImagePath;
            Picasso.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(imagethumb);

            imagethumb.setVisibility(View.VISIBLE);

        } else {

            imagethumb.setVisibility(View.GONE);

        }

        List<ContentValues> breadcrumbItemsList = null;

        breadcrumbItemsList = new ArrayList<ContentValues>();


        if (askExpertQuestionModel.questionCategoriesArray != null && askExpertQuestionModel.questionCategoriesArray.size() > 0) {
            breadcrumbItemsList = generateTagsList(askExpertQuestionModel.questionCategoriesArray);
        } else {
            askExpertQuestionModel.questionCategoriesArray = getArrayListFromString(askExpertQuestionModel.questionCategories);
            breadcrumbItemsList = generateTagsList(askExpertQuestionModel.questionCategoriesArray);
        }

        generateBreadcrumb(breadcrumbItemsList, tagsSkills, this);

    }

    public List<String> getArrayListFromString(String questionCategoriesString) {

        List<String> questionCategoriesArray = new ArrayList<>();

        if (questionCategoriesString.length() <= 0)
            return questionCategoriesArray;

        questionCategoriesArray = Arrays.asList(questionCategoriesString.split(","));

        return questionCategoriesArray;

    }


    public void injectFromDbtoModel() {
        askExpertAnswerModelList = db.fetchAnswersForQuestion("" + askExpertQuestionModel.questionID, askExpertQuestionModel.totalViews);
        if (askExpertAnswerModelList != null) {
            askExpertAnswerAdapter.refreshList(askExpertAnswerModelList);
            noDataLabel.setVisibility(View.INVISIBLE);
            footor.setVisibility(View.GONE);
        } else {
            askExpertAnswerModelList = new ArrayList<AskExpertAnswerModelDg>();
            askExpertAnswerAdapter.refreshList(askExpertAnswerModelList);
            noDataLabel.setVisibility(View.INVISIBLE);
            footor.setVisibility(View.VISIBLE);
        }

        txtNoAnswers.setText(askExpertAnswerModelList.size() + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_answerslabel, AskExpertsAnswersActivity.this));

        if (nextLevel) {
            if (askExpertAnswerModelList != null && askExpertAnswerModelList.size() > 0) {
                openCommentsActivity(false, askExpertAnswerModelList.get(0));
            }

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

    @Override
    public void onRefresh() {
        if (isNetworkConnectionAvailable(context, -1)) {
            refreshForAnswers(true);
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
                boolean refresh = data.getBooleanExtra("NEWCMNT", false);
                if (refresh) {
                    refreshAnswers = true;
                    refreshForAnswers(true);
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
//                PopupMenu popup = new PopupMenu(this, v);
                break;
            case R.id.btn_contextmenu:
                View v = askExpertsListView.getChildAt(position - askExpertsListView.getFirstVisiblePosition());
                catalogContextMenuMethod(position, view, askExpertAnswerModelList.get(position));
                break;
            case R.id.txtTotalUpvoters:
                showUpvoters(askExpertAnswerModelList.get(position).responseID);
                break;
            case R.id.txtComment:
                askCommentFromAnswerActivity(askExpertAnswerModelList.get(position));
                break;
            case R.id.txtComments:
                openCommentsActivity(false, askExpertAnswerModelList.get(position));
                break;
            case R.id.txtUpvote:
                try {
                    callCommentLikeApi(askExpertAnswerModelList.get(position), true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txtDownnvote:
                try {
                    callCommentLikeApi(askExpertAnswerModelList.get(position), false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:

        }
    }

    public void callCommentLikeApi(final AskExpertAnswerModelDg answerModel, boolean isUpOrDown) throws JSONException {
        int isLiked = 0;
        if (isUpOrDown) {
            isLiked = 1;
        } else {
            isLiked = 0;
        }

        JSONObject parameters = new JSONObject();

        parameters.put("intUserID", appUserModel.getUserIDValue());
        parameters.put("strObjectID", "" + answerModel.responseID);
        parameters.put("intTypeID", 3);
        parameters.put("blnIsLiked", isLiked);

        String parameterString = parameters.toString();
        Log.d(TAG, "validateNewForumCreation: " + parameterString);

        if (isNetworkConnectionAvailable(this, -1)) {

            sendNewLikeDataToServer(parameterString);

        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }


    }

    public void sendNewLikeDataToServer(final String postData) {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/InsertContentLikes";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
                refreshForAnswers(true);

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
            public byte[] getBody() {
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

    public void catalogContextMenuMethod(final int position, final View v, final AskExpertAnswerModelDg askExpertAnswerModel) {

        PopupMenu popup = new PopupMenu(this, v);

        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.askexpertmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);//delete
        menu.getItem(1).setVisible(true);// ctx_edit
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_deleteoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_editoption));
        ;

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ctx_delete:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setCancelable(false).setTitle(getLocalizationValue(JsonLocalekeys.profile_alerttitle_stringconfirmation)).setMessage("Are you sure you want to permanently delete the Answer :")
                                .setPositiveButton(JsonLocalekeys.asktheexpert_alertbutton_deletebutton, new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialogBox, int id) {
                                        // ToDo get user input here
                                        deleteAnswerFromServer(askExpertAnswerModel);
                                    }
                                }).setNegativeButton(JsonLocalekeys.asktheexpert_alertbutton_cancelbutton,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                        AlertDialog alertDialogAndroid = alertDialog.create();
                        alertDialogAndroid.show();
                        break;
                    case R.id.ctx_edit:
                        openCommentAndAnswerActivity(true, position);
                }
                return true;

            }
        });
        popup.show();//showing popup menu


    }

    public void deleteAnswerFromServer(final AskExpertAnswerModelDg answerModel) {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteUserResponses?ResponseID=" + answerModel.responseID + "&UserResponseImage=" + answerModel.userResponseImage;

        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("2")) {
                    Toast.makeText(AskExpertsAnswersActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess_message), Toast.LENGTH_SHORT).show();
                    refreshAnswers = true;
//                    db.deleteAnswerFromLocalDB(answerModel);
                    refreshForAnswers(true);
                } else {
                    Toast.makeText(AskExpertsAnswersActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_authenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
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


    public List<ContentValues> generateTagsList(List<String> skillSets) {
        List<ContentValues> tagsList = new ArrayList<>();

        for (int i = 0; i < skillSets.size(); i++) {
            ContentValues cvBreadcrumbItem = new ContentValues();
            cvBreadcrumbItem.put("categoryid", i);
            cvBreadcrumbItem.put("categoryname", skillSets.get(i));
            tagsList.add(cvBreadcrumbItem);
        }

        return tagsList;
    }


    public void generateBreadcrumb(List<ContentValues> dicBreadcrumbItems, CustomFlowLayout category_breadcrumb, final Context context) {
        boolean isFirstCategory = true;
        ContentValues cvBreadcrumbItem = null;
        // int lastCategory = 10;
        category_breadcrumb.removeAllViews();
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
                    + categoryName + "</small>  </font>"));

            textView.setGravity(Gravity.CENTER | Gravity.CENTER);
            textView.setTag(R.id.CATALOG_CATEGORY_ID_TAG, categoryId);
            textView.setTag(R.id.CATALOG_CATEGORY_LEVEL_TAG, i);
            // textView.setBackgroundColor(R.color.alert_no_button);
//            textView.setBackgroundColor(context.getResources().getColor(R.color.colorDarkGrey));
            textView.setBackground(context.getResources().getDrawable(R.drawable.cornersround));
            textView.setOnClickListener(onBreadcrumbItemCLick);
            textView.setClickable(true);
            if (!isFirstCategory) {
                category_breadcrumb.addView(arrowView, new CustomFlowLayout.LayoutParams(
                        CustomFlowLayout.LayoutParams.WRAP_CONTENT, 50));
            }
            category_breadcrumb.addView(textView, new CustomFlowLayout.LayoutParams(
                    CustomFlowLayout.LayoutParams.WRAP_CONTENT, CustomFlowLayout.LayoutParams.WRAP_CONTENT));

        }

    }

    private void showUpvoters(int responseID) {

        upvotersModelList = db.fetchUpvotersist(responseID);

        if (upvotersModelList != null && upvotersModelList.size() > 0) {
            View view = getLayoutInflater().inflate(R.layout.askexpertsupvotersbottomsheet, null);
            ListView lv_languages = (ListView) view.findViewById(R.id.lv_languages);
            upvotersAdapter = new UpvotersAdapter(this, upvotersModelList);
            lv_languages.setAdapter(upvotersAdapter);
            TextView txtCountVoted = view.findViewById(R.id.txtCountVoted);
            txtCountVoted.setText(upvotersModelList.size() + getLocalizationValue(JsonLocalekeys.asktheexpert_label_upvotetitlelabel));
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        } else {
            Toast.makeText(context, "0 voted", Toast.LENGTH_SHORT).show();
        }


    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");

                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);

                if (requestType.equalsIgnoreCase("GetUserQuestionsResponses")) {
                    if (response != null) {
                        try {
                            db.injectAsktheExpertsAnswers(response);
                            db.injectUpVoters(response);
                            injectFromDbtoModel();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {


                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();

            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {

                svProgressHUD.dismiss();
            }
        };
    }


}

