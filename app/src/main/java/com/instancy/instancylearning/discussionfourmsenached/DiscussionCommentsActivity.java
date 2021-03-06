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
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourmsenached.AddNewCommentActivity;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionCommentsAdapter;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionCommentsModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
import static com.instancy.instancylearning.utils.Utilities.getFileExtensionWithPlaceHolderImage;
import static com.instancy.instancylearning.utils.Utilities.gettheContentTypeNotImg;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class DiscussionCommentsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = DiscussionCommentsActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    DiscussionFourmsDbTables db;
    List<DiscussionCommentsModelDg> discussionCommentsModelList = null;
    ResultListner resultListner = null;

    ListView discussionFourmlistView;
    DiscussionTopicModelDg discussionTopicModel;

    DiscussionForumModelDg discussionForumModel;

    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    DiscussionCommentsAdapter commentsAdapter;

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

    BottomSheetDialog bottomSheetDialog;

    List<LikesModel> likesModelList;

    View commentHeaderView;

    View header;

    TextView txtCommentCount, txtLikesCount;

    View footor;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, DiscussionCommentsActivity.this);

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
        discussionTopicModel = (DiscussionTopicModelDg) getIntent().getSerializableExtra("topicModel");
        discussionForumModel = (DiscussionForumModelDg) getIntent().getSerializableExtra("forumModel");
        isFromGlobalSearch = getIntent().getBooleanExtra("ISGLOBALSEARCH", false);
        if (isFromGlobalSearch) {

            swipeRefreshLayout.setEnabled(false);
        }

        //Remove this code after

        commentHeaderView = (RelativeLayout) findViewById(R.id.topicheader);
        commentHeaderView.setVisibility(View.GONE);

        /// uptohear

        header = (View) getLayoutInflater().inflate(R.layout.discussiontopiccell_en, null);
        footor = (View) getLayoutInflater().inflate(R.layout.no_data_layout, null);

        TextView nodataFottor = footor.findViewById(R.id.nodata_label);
        nodataFottor.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.discussionforum_label_commentslabel) + "</font>"));
        discussionCommentsModelList = new ArrayList<DiscussionCommentsModelDg>();
        discussionFourmlistView = (ListView) findViewById(R.id.discussionfourmlist);
        commentsAdapter = new DiscussionCommentsAdapter(this, BIND_ABOVE_CLIENT, discussionCommentsModelList, discussionForumModel.likePosts);
        discussionFourmlistView.setAdapter(commentsAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
//        discussionFourmlistView.setEmptyView(findViewById(R.id.nodata_label));

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

                Intent intentDetail = new Intent(context, AddNewCommentActivity.class);
                intentDetail.putExtra("topicModel", discussionTopicModel);
                intentDetail.putExtra("forumModel", discussionForumModel);
                startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

            }
        });

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        initilizeHeaderView(header);
    }

    public void initilizeHeaderView(View view) {

        TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        TextView txtShortDesc = (TextView) view.findViewById(R.id.txtShortDesc);
        ImageView imagethumb = (ImageView) view.findViewById(R.id.imagethumb);
        ImageView attachedImg = (ImageView) view.findViewById(R.id.attachedimg);
        TextView txtAuthor = (TextView) view.findViewById(R.id.txt_author);
        txtCommentCount = (TextView) view.findViewById(R.id.txtCommentsCount);
        TextView txtLikes = (TextView) view.findViewById(R.id.txtLikes);
        TextView txtComment = (TextView) view.findViewById(R.id.txtComment);
        TextView txtLikesCount = (TextView) view.findViewById(R.id.txtLikesCount);


        ImageButton btnContextMenu = (ImageButton) view.findViewById(R.id.btn_contextmenu);

        btnContextMenu.setVisibility(View.INVISIBLE);

        txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtShortDesc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtLikes.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtComment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtLikesCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCommentCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        txtComment.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_commentlabel));
        txtLikes.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_likelabel));

        txtName.setText(discussionTopicModel.name);
        txtShortDesc.setText(discussionTopicModel.longDescription);

        txtLikesCount.setText(discussionTopicModel.likes + " " + getLocalizationValue(JsonLocalekeys.discussionforum_button_likesbutton));
        txtCommentCount.setText(discussionTopicModel.noOfReplies + " " + getLocalizationValue(JsonLocalekeys.discussionforum_label_commentslabel));

        String totalActivityStr = getLocalizationValue(JsonLocalekeys.discussionforum_label_createdbylabel) + " ";

        if (isValidString(discussionTopicModel.author)) {

            totalActivityStr = totalActivityStr + discussionTopicModel.author + " " + discussionTopicModel.createdTime;
        }

        if (isValidString(discussionTopicModel.modifiedUserName)) {

            totalActivityStr = totalActivityStr + " |  " + getLocalizationValue(JsonLocalekeys.discussionforum_label_lastupdatedbylabel) + " " + discussionTopicModel.author + " " + discussionTopicModel.updatedTime;
        }

        txtAuthor.setText(totalActivityStr);

        txtAuthor.setText(totalActivityStr);

        if (discussionTopicModel.longDescription.isEmpty()) {
            txtShortDesc.setVisibility(View.GONE);
        } else {
            txtShortDesc.setVisibility(View.VISIBLE);
        }
        btnContextMenu.setVisibility(View.INVISIBLE);

        String imgUrl = appUserModel.getSiteURL() + discussionTopicModel.uploadFileName;

        String topicProfileImg = appUserModel.getSiteURL() + discussionTopicModel.topicUserProfile;
        if (topicProfileImg.startsWith("http:"))
            topicProfileImg = topicProfileImg.replace("http:", "https:");
        if (isValidString(discussionTopicModel.topicUserProfile)) {
            Glide.with(this).load(topicProfileImg).placeholder(R.drawable.user_placeholder).into(imagethumb);
        }

        if (isValidString(discussionTopicModel.uploadFileName)) {

            attachedImg.setVisibility(View.VISIBLE);

            final String fileExtesnion = getFileExtensionWithPlaceHolderImage(discussionTopicModel.uploadFileName);

            int resourceId = 0;

            resourceId = gettheContentTypeNotImg(fileExtesnion);
            if (resourceId == 0)
                Glide.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachedImg);
            else
                attachedImg.setImageDrawable(getDrawableFromStringWithColor(this, resourceId, uiSettingsModel.getAppButtonBgColor()));

        } else {

            attachedImg.setVisibility(View.GONE);
        }

        if (discussionTopicModel.likeState) {
            txtLikes.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
            assert txtLikes != null;
            txtLikes.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppButtonBgColor()), null, null, null);
        } else

        {
            txtLikes.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtLikes.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(this, R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);

        }

        assert txtComment != null;
        txtComment.setCompoundDrawablesWithIntrinsicBounds(

                getDrawableFromString(this, R.string.fa_icon_comment), null, null, null);


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
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }
        vollyService.getStringResponseVolley("GetCommentList", appUserModel.getWebAPIUrl() + "/MobileLMS/GetCommentList?intSiteID=" + appUserModel.getSiteIDValue() + "&ForumID=" + discussionTopicModel.forumId + "&TopicID=" + discussionTopicModel.contentID + "&intUserID=" + appUserModel.getUserIDValue() + "&strLocale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "", appUserModel.getAuthHeaders());

        swipeRefreshLayout.setRefreshing(false);
    }

    public void gotoReplyActivity(DiscussionCommentsModelDg discussionCommentsModel) {

        Intent intentDetail = new Intent(context, AddReplyActivity.class);
        intentDetail.putExtra("commentModel", discussionCommentsModel);
        intentDetail.putExtra("isfromedit", false);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

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
                if (requestType.equalsIgnoreCase("GetCommentList")) {

                    if (response != null) {
                        try {
                            db.injectDiscussionComments(response, discussionTopicModel);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        nodata_Label.setText(getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_noitemstodisplay));
                    }
                }

                if (requestType.equalsIgnoreCase("GetTopicCommentLevelLikeList")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            likesModelList = generateLikesList(jsonArray);
                            showLikesList();
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
        discussionCommentsModelList = db.fetchDiscussionComments(appUserModel.getSiteIDValue(), discussionTopicModel);
        if (discussionCommentsModelList != null) {
            commentsAdapter.refreshList(discussionCommentsModelList);
            footor.setVisibility(View.GONE);
        } else {
            discussionCommentsModelList = new ArrayList<DiscussionCommentsModelDg>();
            commentsAdapter.refreshList(discussionCommentsModelList);
            footor.setVisibility(View.VISIBLE);
        }
        txtCommentCount.setText(discussionCommentsModelList.size() + " " + getLocalizationValue(JsonLocalekeys.discussionforum_label_commentslabel));
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

    public void attachFragment(DiscussionCommentsModelDg commentsModel) {
        Intent intentDetail = new Intent(context, DiscussionRepliesActivity.class);
        intentDetail.putExtra("commentsModel", commentsModel);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
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
                    refreshMyLearning(false);
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

        switch (view.getId()) {
            case R.id.card_view:
                attachFragment(discussionCommentsModelList.get(i));
                break;
            case R.id.btn_contextmenu:
//                View v = discussionFourmlistView.getChildAt(i - discussionFourmlistView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) view.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(i, view, txtBtnDownload, discussionCommentsModelList.get(i));
                break;
            case R.id.txtLikesCount:
                getForumLevelLikeList(discussionCommentsModelList.get(i));
                break;
            case R.id.txtLike:
                try {
                    callCommentLikeApi(discussionCommentsModelList.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txtReply:
                gotoReplyActivity(discussionCommentsModelList.get(i));
                break;
            default:
        }
    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final DiscussionCommentsModelDg discussionCommentsModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.commentdelete, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);//delete
        menu.getItem(1).setVisible(true);//edit
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_deletecommentoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_editcommentoption));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ctx_edit:
                        Intent intentDetail = new Intent(context, AddNewCommentActivity.class);
                        intentDetail.putExtra("isfromedit", true);
                        intentDetail.putExtra("topicModel", discussionTopicModel);
                        intentDetail.putExtra("commentModel", discussionCommentsModelList.get(position));
                        intentDetail.putExtra("forumModel", discussionForumModel);
                        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
                        break;
                    case R.id.ctx_delete:
                        try {
                            deleteCommentFromServerBuildObj(discussionCommentsModel);
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

    public void deleteCommentFromLocalDB(DiscussionCommentsModelDg discussionCommentsModel) {

        try {
            String strDelete = "DELETE FROM " + TBL_TOPICCOMMENTS + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND forumid ='" + discussionTopicModel.forumId + "' AND commentid  ='" + discussionCommentsModel.commentID + "'";
            db.executeQuery(strDelete);
            injectFromDbtoModel();

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

        int commentCount = 0;
        try {
            commentCount = discussionTopicModel.noOfReplies;
        } catch (NumberFormatException ex) {

            ex.printStackTrace();
        }

        if (commentCount > 0) {
            try {

                int count = commentCount;
                db.executeQuery("UPDATE " + TBL_TOPICCOMMENTS + "SET noofreplies = " + count + " WHERE forumid =" + discussionTopicModel.forumId + "' AND topicid = '" + discussionTopicModel.contentID + "' AND siteid = '" + discussionTopicModel.siteId);

            } catch (SQLiteException sqlEx) {

                sqlEx.printStackTrace();
            }

        }

    }

    public void deleteCommentFromServerBuildObj(DiscussionCommentsModelDg commentsModel) throws JSONException {

        JSONObject parameters = new JSONObject();

        parameters.put("SiteID", appUserModel.getSiteIDValue());
        parameters.put("TopicID", commentsModel.topicID);
        parameters.put("ReplyID", commentsModel.replyID);
        parameters.put("ForumID", commentsModel.forumID);
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("TopicName", discussionTopicModel.name);
        parameters.put("NoofReplies", commentsModel.commentRepliesCount);
        parameters.put("LocaleID", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
        parameters.put("LastPostedDate", commentsModel.postedDate);
        parameters.put("CreatedUserID", commentsModel.postedBy);
        parameters.put("AttachementPath", commentsModel.commentFileUploadPath);

        String parameterString = parameters.toString();
        Log.d(TAG, "validateNewForumCreation: " + parameterString);

        if (isNetworkConnectionAvailable(this, -1)) {
            deleteCommentFromServer(parameterString);
        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }


    }

    public void deleteCommentFromServer(final String postData) {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteForumComment";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {
                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess) + " \n" + getLocalizationValue(JsonLocalekeys.discussionforum_alerttsubtitle_commentdeletesuccess_message), Toast.LENGTH_SHORT).show();
                    refreshAnyThing = true;
                    refreshMyLearning(true);
//                    deleteCommentFromLocalDB(discussionTopicModel);
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

    public void getForumLevelLikeList(DiscussionCommentsModelDg commentsModel) {
        if (isNetworkConnectionAvailable(context, -1)) {

            String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetTopicCommentLevelLikeList?strObjectID=" + commentsModel.commentID + "&intUserID=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getUserIDValue() + "&strLocale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "&intTypeID=2";

            vollyService.getStringResponseVolley("GetTopicCommentLevelLikeList", parmStringUrl, appUserModel.getAuthHeaders());

        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }


    public List<LikesModel> generateLikesList(JSONArray jsonArray) {
        List<LikesModel> likesModelList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonAnswerColumnObj = null;
            try {
                jsonAnswerColumnObj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LikesModel likesModel = new LikesModel();

            likesModel.userID = jsonAnswerColumnObj.optInt("UserID");
            likesModel.userAddress = jsonAnswerColumnObj.optString("UserAddress");
            likesModel.picture = jsonAnswerColumnObj.optString("UserThumb");
            likesModel.userName = jsonAnswerColumnObj.optString("UserName");
            likesModel.jobTitle = jsonAnswerColumnObj.optString("UserDesg");

            likesModelList.add(likesModel);
        }

        return likesModelList;
    }

    private void showLikesList() {

        if (likesModelList != null && likesModelList.size() > 0) {
            View view = getLayoutInflater().inflate(R.layout.askexpertsupvotersbottomsheet, null);

            ListView lv_languages = (ListView) view.findViewById(R.id.lv_languages);
            LikesAdapter upvotersAdapter = new LikesAdapter(this, likesModelList);
            lv_languages.setAdapter(upvotersAdapter);
            TextView txtCountVoted = view.findViewById(R.id.txtCountVoted);
            txtCountVoted.setText(likesModelList.size() + " Likes");
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        } else {
            Toast.makeText(context, "No Likes", Toast.LENGTH_SHORT).show();
        }


    }

    public void callCommentLikeApi(final DiscussionCommentsModelDg commentsModelDg) throws JSONException {
        int isLiked = 0;
        if (commentsModelDg.likeState) {
            isLiked = 0;
        } else {
            isLiked = 1;
        }

        JSONObject parameters = new JSONObject();

        parameters.put("intUserID", appUserModel.getUserIDValue());
        parameters.put("strObjectID", "" + commentsModelDg.commentID);
        parameters.put("intTypeID", 2);
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
                Toast.makeText(DiscussionCommentsActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
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
