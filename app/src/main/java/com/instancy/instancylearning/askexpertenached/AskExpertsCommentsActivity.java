package com.instancy.instancylearning.askexpertenached;

import android.annotation.SuppressLint;
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
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.askexpert.AskExpertAnswerAdapter;
import com.instancy.instancylearning.askexpert.AskExpertsAnswersActivity;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourms.DiscussionCommentsActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
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

import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKCOMMENTS_DIGI;
import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKRESPONSES;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.getFileExtensionWithPlaceHolderImage;
import static com.instancy.instancylearning.utils.Utilities.gettheContentTypeNotImg;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class AskExpertsCommentsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = AskExpertsCommentsActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    AskExpertDbTables db;
    List<AskExpertCommentModel> askExpertCommentModelList = null;
    ResultListner resultListner = null;
    AskExpertQuestionModelDg askExpertQuestionModel;
    AskExpertAnswerModelDg askExpertAnswerModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    AskExpertCommentAdapter askExpertCommentAdapter;

    boolean refreshAnswers = false;

    @Nullable
    @BindView(R.id.askexpertslistview)
    ListView askExpertsListView;

    @Nullable
    @BindView(R.id.card_view)
    CardView card_view;

    @Nullable
    @BindView(R.id.imagethumb)
    ImageView imgThumb;


    @Nullable
    @BindView(R.id.attachedimg)
    ImageView attachedImg;

    @Nullable
    @BindView(R.id.txt_name)
    TextView txtName;

    @Nullable
    @BindView(R.id.txtDaysAgo)
    TextView txtDaysAgo;

    @Nullable
    @BindView(R.id.txtmessage)
    TextView txtMessage;

    @Nullable
    @BindView(R.id.btn_contextmenu)
    ImageButton btnContextMenu;

    @Nullable
    @BindView(R.id.txtUpvote)
    TextView txtUpvote;

    @Nullable
    @BindView(R.id.txtDownnvote)
    TextView txtDownnvote;

    @Nullable
    @BindView(R.id.txtComment)
    TextView txtComment;

    @Nullable
    @BindView(R.id.txtComments)
    TextView txtComments;

    @Nullable
    @BindView(R.id.txtTotalViews)
    TextView txtTotalViews;

    @Nullable
    @BindView(R.id.txtTotalUpvoters)
    TextView txtTotalUpvoters;

    @Nullable
    @BindView(R.id.fab_comment_button)
    FloatingActionButton floatingActionButton;

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askexpertscommentsactivity);
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


        vollyService = new VollyService(resultCallback, context);

        askExpertQuestionModel = (AskExpertQuestionModelDg) getIntent().getSerializableExtra("AskExpertQuestionModelDg");
        askExpertAnswerModel = (AskExpertAnswerModelDg) getIntent().getSerializableExtra("askExpertAnswerModel");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.asktheexpert_label_commentslabel) + "</font>"));

        askExpertCommentAdapter = new AskExpertCommentAdapter(this, BIND_ABOVE_CLIENT, askExpertCommentModelList);
        askExpertsListView.setAdapter(askExpertCommentAdapter);

        askExpertsListView.setOnItemClickListener(this);
        askExpertsListView.setEmptyView(findViewById(R.id.nodata_label));

        askExpertCommentModelList = new ArrayList<AskExpertCommentModel>();

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        initilizeHeaderView();

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
//        if (appUserModel.getUserIDValue().equalsIgnoreCase("" + askExpertQuestionModel.createduserID)) {
//            floatingActionButton.setVisibility(View.INVISIBLE);
//        }

        if (isNetworkConnectionAvailable(this, -1)) {
            refreshForComments(false);
        } else {
            injectFromDbtoModel();
        }

    }

    public void openCommentAndAnswerActivity(boolean isEdit, int position) {

        Intent intentDetail = new Intent(context, AskExpertsAskAnsCmtActivity.class);

        if (position != -1) {
            AskExpertCommentModel commentModel = askExpertCommentModelList.get(position);
            intentDetail.putExtra("EDIT", isEdit);
            intentDetail.putExtra("AskExpertCommentModel", commentModel);
        }

        intentDetail.putExtra("AskExpertQuestionModelDg", askExpertQuestionModel);
        intentDetail.putExtra("AskExpertAnswerModelDg", askExpertAnswerModel);
        intentDetail.putExtra("ISASKANSWER", false);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
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

                if (requestType.equalsIgnoreCase("GetUserResponseComments")) {
                    if (response != null) {
                        try {
                            db.injectComments(response);

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

    public void openCommentView() {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.askcommentdialog, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(getLocalizationValue(JsonLocalekeys.asktheexpert_alertbutton_submitbutton), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogBox, int id) {
                        // ToDo get user input here

                        try {

                            validateNewForumCreation(userInputDialogEditText.getText().toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton(getLocalizationValue(JsonLocalekeys.asktheexpert_alertbutton_cancelbutton),
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


        txtName.setText(askExpertAnswerModel.respondedUserName);
        txtDaysAgo.setText(" " + getLocalizationValue(JsonLocalekeys.asktheexpert_label_answeredonlabel) + " " + askExpertAnswerModel.daysAgo);
        txtMessage.setText(askExpertAnswerModel.response + " ");
        txtTotalViews.setText(askExpertAnswerModel.upvotesCount + " " + getLocalizationValue(JsonLocalekeys.asktheexpert_label_viewslabel));
        txtComments.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_label_commentslabel) + " " + askExpertAnswerModel.commentCount);

        txtComment.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_label_commentslabel));

        txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDaysAgo.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtMessage.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtUpvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDownnvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtTotalUpvoters.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtComment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtComments.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        assert txtComment != null;
        txtComment.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_comments), null, null, null);


        if (askExpertAnswerModel.isLikedStr.equalsIgnoreCase("null")) {
            txtUpvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtDownnvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtUpvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);
            txtDownnvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_down, uiSettingsModel.getAppTextColor()), null, null, null);
        } else if (askExpertAnswerModel.isLikedStr.equalsIgnoreCase("true")) {
            txtUpvote.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
            txtDownnvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            txtUpvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppButtonBgColor()), null, null, null);
            txtDownnvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_down, uiSettingsModel.getAppTextColor()), null, null, null);

        } else if (askExpertAnswerModel.isLikedStr.equalsIgnoreCase("false")) {
            txtUpvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtDownnvote.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
            txtUpvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);
            txtDownnvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_down, uiSettingsModel.getAppButtonBgColor()), null, null, null);
        }


        btnContextMenu.setVisibility(View.INVISIBLE);

        card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        String imgUrl = appUserModel.getSiteURL() + askExpertAnswerModel.userResponseImagePath;
        if (isValidString(askExpertAnswerModel.userResponseImage)) {

            attachedImg.setVisibility(View.VISIBLE);

            final String fileExtesnion = getFileExtensionWithPlaceHolderImage(askExpertAnswerModel.userResponseImagePath);

            int resourceId = 0;

            resourceId = gettheContentTypeNotImg(fileExtesnion);
            if (resourceId == 0)
                Glide.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachedImg);
            else
                attachedImg.setImageDrawable(getDrawableFromStringWithColor(this, resourceId, uiSettingsModel.getAppButtonBgColor()));

        } else {

            attachedImg.setVisibility(View.GONE);
        }

        String imgUrls = appUserModel.getSiteURL() + askExpertAnswerModel.picture;

        if (imgUrls.startsWith("http:"))
            imgUrls = imgUrls.replace("http:", "https:");

        Glide.with(this).load(imgUrls).placeholder(R.drawable.user_placeholder).into(imgThumb);


    }


    public void injectFromDbtoModel() {
        askExpertCommentModelList = db.fetchCommentsList(askExpertAnswerModel.responseID);
        if (askExpertCommentModelList != null) {
            askExpertCommentAdapter.refreshList(askExpertCommentModelList);
        } else {
            askExpertCommentModelList = new ArrayList<AskExpertCommentModel>();
            askExpertCommentAdapter.refreshList(askExpertCommentModelList);
        }

        txtComments.setText(getLocalizationValue(JsonLocalekeys.asktheexpert_label_commentslabel) + " " + askExpertCommentModelList.size());
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
            refreshForComments(true);
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
                    refreshForComments(true);
                    refreshAnswers = true;
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
                catalogContextMenuMethod(position, view, txtBtnDownload, askExpertCommentModelList.get(position));
                break;
            case R.id.txtLike:
                try {
                    callCommentLikeApi(askExpertCommentModelList.get(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final AskExpertCommentModel askExpertCommentModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.askexpertmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);//delete
        menu.getItem(1).setVisible(true);//edit
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_deleteoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_editoption));
        ;

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ctx_delete:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setCancelable(false).setTitle(getLocalizationValue(JsonLocalekeys.profile_alerttitle_stringconfirmation)).setMessage(getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_areyousuretodeletecomment))
                                .setPositiveButton(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_deleteoption), new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialogBox, int id) {
                                        // ToDo get user input here
                                        deleteCommentFromServer(askExpertCommentModel);
                                    }
                                }).setNegativeButton(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_canceloption),
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
//        askExpertCommentModel
    }

    public void deleteCommentFromServer(final AskExpertCommentModel askExpertCommentModel) {

//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteUserResponseComment?commentId=" + askExpertCommentModel.commentID + "&Commentedimage=" + askExpertCommentModel.commentImage;

        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("1")) {
                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.asktheexpert_alerttsubtitle_commentdeletesuccess_message), Toast.LENGTH_SHORT).show();
                    refreshAnswers = true;
                    db.deleteCommentFromLocalDB(askExpertCommentModel);
                    injectFromDbtoModel();
                } else {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_commentauthenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
                }
                svProgressHUD.dismiss();
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

    public void refreshForComments(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }

        String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetUserResponseComments?UserID=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getSiteIDValue() + "&intQuestionID=" + askExpertQuestionModel.questionID;

        vollyService.getStringResponseVolley("GetUserResponseComments", parmStringUrl, appUserModel.getAuthHeaders());

    }

    public void validateNewForumCreation(String messageStr) throws JSONException {

        String dateString = getCurrentDateTime("dd/MM/yyyy");

        String dateStringSec = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

        if (messageStr.length() < 5) {
            Toast.makeText(AskExpertsCommentsActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_answercannotbeempty), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AskExpertsCommentsActivity.this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(AskExpertsCommentsActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alerttitle_stringsuccess) + "\n" + getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_answersuccessfullyposted), Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(AskExpertsCommentsActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_newanswer_authenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AskExpertsCommentsActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
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
        intent.putExtra("NEWCMNT", refreshAnswers);
        setResult(RESULT_OK, intent);
        finish();
    }

    @SuppressLint("ResourceAsColor")
    public Drawable getDrawableFromString(Context context, int resourceID) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setTextColor(R.color.colorDarkGrey);
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }


    public void callCommentLikeApi(final AskExpertCommentModel commentModel) throws JSONException {
        int isLiked = 0;
        if (commentModel.isLiked) {
            isLiked = 0;
        } else {
            isLiked = 1;
        }

        JSONObject parameters = new JSONObject();

        parameters.put("intUserID", appUserModel.getUserIDValue());
        parameters.put("strObjectID", "" + commentModel.commentID);
        parameters.put("intTypeID", 4);
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
                refreshForComments(true);

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AskExpertsCommentsActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
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
      /*          headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");*/


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

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, AskExpertsCommentsActivity.this);

    }
}



