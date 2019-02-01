package com.instancy.instancylearning.discussionfourmsenached;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
import com.instancy.instancylearning.discussionfourms.AddNewCommentActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.mcoy_jiang.videomanager.ui.McoyVideoView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_TOPICCOMMENTS;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class DiscussionRepliesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = DiscussionRepliesActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    DiscussionFourmsDbTables db;
    List<DiscussionReplyModelDg> discussionReplyModelList = null;
    ResultListner resultListner = null;

    ListView discussionFourmlistView;
    DiscussionCommentsModelDg discussionCommentsModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    DiscussionRepliesAdapter repliesAdapter;

    boolean refreshAnyThing = false;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    boolean isFromGlobalSearch = false;


    @Nullable
    @BindView(R.id.fab_comment_button)
    FloatingActionButton floatingActionButton;

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    View commentHeaderView;

    View header;

    View footor;

    TextView txtRepliesCount;
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,DiscussionRepliesActivity.this);

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussiontopics_fragment);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DiscussionFourmsDbTables(this);
        ButterKnife.bind(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        swipeRefreshLayout.setOnRefreshListener(this);
        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        discussionCommentsModel = (DiscussionCommentsModelDg) getIntent().getSerializableExtra("commentsModel");
        isFromGlobalSearch = getIntent().getBooleanExtra("ISGLOBALSEARCH", false);
        if (isFromGlobalSearch) {

            swipeRefreshLayout.setEnabled(false);
        }

        //Remove this code after

        commentHeaderView = (RelativeLayout) findViewById(R.id.topicheader);
        commentHeaderView.setVisibility(View.GONE);

        /// uptohear

        header = (View) getLayoutInflater().inflate(R.layout.discussioncommentcell_en, null);
        footor = (View) getLayoutInflater().inflate(R.layout.no_data_layout, null);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.discussionforum_label_replys) + "</font>"));
        discussionReplyModelList = new ArrayList<DiscussionReplyModelDg>();
        discussionFourmlistView = (ListView) findViewById(R.id.discussionfourmlist);
        repliesAdapter = new DiscussionRepliesAdapter(this, BIND_ABOVE_CLIENT, discussionReplyModelList);
        discussionFourmlistView.setAdapter(repliesAdapter);
        discussionFourmlistView.setOnItemClickListener(this);

        discussionFourmlistView.addFooterView(footor);
        discussionFourmlistView.addHeaderView(header, null, false);

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        if (isNetworkConnectionAvailable(this, -1)) {
            refreshMyLearning(false);
        } else {
            injectFromDbtoModel();
        }

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomment, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        floatingActionButton.setImageDrawable(d);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentDetail = new Intent(context, AddReplyActivity.class);
                intentDetail.putExtra("commentModel", discussionCommentsModel);
                intentDetail.putExtra("isfromedit", false);
                startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

            }
        });

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        initilizeHeaderView(header);
    }

    public void initilizeHeaderView(View view) {


        TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        TextView txtMessage = (TextView) view.findViewById(R.id.txtmessage);
        ImageView imagethumb = (ImageView) view.findViewById(R.id.imagethumb);
        ImageView attachedImg = (ImageView) view.findViewById(R.id.attachedimg);
        txtRepliesCount = (TextView) view.findViewById(R.id.txtRepliesCount);
        TextView txtLike = (TextView) view.findViewById(R.id.txtLike);
        TextView txtReply = (TextView) view.findViewById(R.id.txtReply);
        TextView txtLikesCount = (TextView) view.findViewById(R.id.txtLikesCount);
        TextView txtDaysAgo = (TextView) view.findViewById(R.id.txtDaysAgo);

        FrameLayout videoLayout = (FrameLayout) view.findViewById(R.id.videoLayout);

        McoyVideoView videoView = (McoyVideoView) view.findViewById(R.id.videoView);


        ImageButton btnContextMenu = (ImageButton) view.findViewById(R.id.btn_contextmenu);

        btnContextMenu.setVisibility(View.INVISIBLE);

        txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtMessage.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        txtReply.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtLikesCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtRepliesCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDaysAgo.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        txtName.setText(discussionCommentsModel.commentedBy);
        txtDaysAgo.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_commentslabel)+" " + discussionCommentsModel.commentedFromDays);
        txtMessage.setText(discussionCommentsModel.message);
        txtMessage.setMaxLines(200);

        txtLikesCount.setText(discussionCommentsModel.commentLikes + ""+getLocalizationValue(JsonLocalekeys.discussionforum_label_likeslabel));
        txtRepliesCount.setText(discussionCommentsModel.commentRepliesCount + " "+getLocalizationValue(JsonLocalekeys.discussionforum_label_replys));


//        assert txtLike != null;
//        txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_thumbs_o_up), null, null, null);
//

        if (discussionCommentsModel.likeState) {
            txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

            assert txtLike != null;
            txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppButtonBgColor()), null, null, null);
        } else {
            txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);

        }

        assert txtReply != null;
        txtReply.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(this, R.string.fa_icon_reply), null, null, null);


        String imgUrl = appUserModel.getSiteURL() + discussionCommentsModel.commentUserProfile;
        Picasso.with(this).load(imgUrl).placeholder(R.drawable.user_placeholder).into(imagethumb);

        if (discussionCommentsModel.commentFileUploadPath.length() == 0) {
            attachedImg.setVisibility(View.GONE);
        } else {

            attachedImg.setVisibility(View.VISIBLE);
            String attachimgUrl = appUserModel.getSiteURL() + discussionCommentsModel.commentFileUploadPath;
            Picasso.with(this).load(attachimgUrl).into(attachedImg);


            if (attachimgUrl.contains(".mp4")) {
                attachedImg.setVisibility(View.GONE);
                videoLayout.setVisibility(View.VISIBLE);
                assert videoView != null;
                videoView.setVideoUrl(attachimgUrl);
            }


        }


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


    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/GetReplies";

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("TopicID", discussionCommentsModel.topicID);
            parameters.put("TopicName", "");
            parameters.put("LocaleID", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("ForumID", discussionCommentsModel.forumID);
            parameters.put("Message", discussionCommentsModel.message);
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("InvolvedUserIDList", "CreatedDate%20Desc");
            parameters.put("strAttachFile", discussionCommentsModel.commentFileUploadPath);
            parameters.put("strReplyID", "-1");
            parameters.put("strCommentID", discussionCommentsModel.commentID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "GetReplies", urlStr);


    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                svProgressHUD.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                svProgressHUD.dismiss();
                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                if (requestType.equalsIgnoreCase("GetReplies")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            db.injectDiscussionFourmReplies(response);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

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

    public void injectFromDbtoModel() {
        discussionReplyModelList = db.fetchDiscussionReplies(appUserModel.getSiteIDValue(), discussionCommentsModel);
        if (discussionReplyModelList != null) {
            repliesAdapter.refreshList(discussionReplyModelList);
            footor.setVisibility(View.GONE);
        } else {
            discussionReplyModelList = new ArrayList<DiscussionReplyModelDg>();
            repliesAdapter.refreshList(discussionReplyModelList);
            footor.setVisibility(View.VISIBLE);
        }
        txtRepliesCount.setText(discussionReplyModelList.size() + getLocalizationValue(JsonLocalekeys.discussionforum_label_reply));
    }

    @Override
    public void onBackPressed() {
        closeForum(refreshAnyThing);
    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWFORUM", refreshAnyThing);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tracklistmenu, menu);
        MenuItem itemInfo = menu.findItem(R.id.tracklist_help);
        Drawable myIcon = getResources().getDrawable(R.drawable.help);
        itemInfo.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
        itemInfo.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                closeForum(true);
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
            refreshMyLearning(true);
            swipeRefreshLayout.setRefreshing(true);
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
                    refreshMyLearning(true);
                    refreshAnyThing = true;
                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intentSocial = new Intent(this, SocialWebLoginsActivity.class);

        switch (view.getId()) {
            case R.id.btn_contextmenu:
//                View v = discussionFourmlistView.getChildAt(i - discussionFourmlistView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) view.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(i, view, txtBtnDownload, discussionReplyModelList.get(i));
                break;
            case R.id.txtLike:
                try {
                    callCommentLikeApi(discussionReplyModelList.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
        }
    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final DiscussionReplyModelDg discussionReplyModelDg) {

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.commentdelete, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_deleterepliesoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_editrepliesoption));
        menu.getItem(0).setVisible(true);//delete
        menu.getItem(1).setVisible(true);//edit

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ctx_edit:
                        Intent intentDetail = new Intent(context, AddReplyActivity.class);
                        intentDetail.putExtra("isfromedit", true);
                        intentDetail.putExtra("replymodel", discussionReplyModelList.get(position));
                        intentDetail.putExtra("commentModel", discussionCommentsModel);
                        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
                        break;
                    case R.id.ctx_delete:
                        try {
                            deleteCommentFromServerBuildObj(discussionReplyModelDg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();//showing popup menu

    }

    public void deleteCommentFromServerBuildObj(DiscussionReplyModelDg commentsModel) throws JSONException {

        JSONObject parameters = new JSONObject();

        parameters.put("SiteID", appUserModel.getSiteIDValue());
        parameters.put("TopicID", commentsModel.topicID);
        parameters.put("ReplyID", commentsModel.replyID);
        parameters.put("ForumID", commentsModel.forumID);
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("TopicName", appUserModel.getSiteIDValue());
        parameters.put("NoofReplies", "0");
        parameters.put("LocaleID", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
        parameters.put("LastPostedDate", commentsModel.postedDate);
        parameters.put("CreatedUserID", commentsModel.postedBy);
        parameters.put("AttachementPath", commentsModel.picture);

        String parameterString = parameters.toString();
        Log.d(TAG, "validateNewForumCreation: " + parameterString);

        if (isNetworkConnectionAvailable(this, -1)) {
            deleteCommentFromServer(parameterString, commentsModel);
        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }


    }

    public void deleteCommentFromServer(final String postData, final DiscussionReplyModelDg replyModelDg) {


        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteForumReply";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess)+" \n"+getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_replyhasbeensuccessfullyposted), Toast.LENGTH_SHORT).show();
                    refreshAnyThing = true;
                    refreshMyLearning(true);
                } else {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_replyauthenticationfailedcontactsiteadmin), Toast.LENGTH_SHORT).show();
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

    public void callCommentLikeApi(final DiscussionReplyModelDg replyModel) throws JSONException {
        int isLiked = 0;
        if (replyModel.likeState) {
            isLiked = 0;
        } else {
            isLiked = 1;
        }

        JSONObject parameters = new JSONObject();

        parameters.put("intUserID", appUserModel.getUserIDValue());
        parameters.put("strObjectID", "" + replyModel.replyID);
        parameters.put("intTypeID", 5);
        parameters.put("blnIsLiked", isLiked);
        parameters.put("intSiteID", appUserModel.getSiteIDValue());
        parameters.put("strLocale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));

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
                refreshMyLearning(true);

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(DiscussionRepliesActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
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


}
