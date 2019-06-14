package com.instancy.instancylearning.discussionfourmsenached;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;

import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.AppUserModel;

import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class DiscussionTopicActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = DiscussionTopicActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    View topicheaderView;
    DiscussionFourmsDbTables db;
    List<DiscussionTopicModelDg> discussionTopicModels = null;

    ListView discussionFourmlistView;
    DiscussionForumModelDg discussionForumModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    DiscussionTopicAdapter fourmAdapter;

    boolean refreshAnyThing = false;

    BottomSheetDialog bottomSheetDialog;

    List<LikesModel> likesModelList;

    View header;

    View footor;

    TextView txtLikes, txtTopics;

    @Nullable
    @BindView(R.id.fab_comment_button)
    FloatingActionButton floatingActionButton;

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;


    boolean isFromNotification = false;
    boolean isFromGlobalSearch = false;

    boolean nextLevel = false;

    boolean isAbleCreateTopic = false, isAbleToDeleteTopic = false, isAbleToEditTopic = false;


    String topicID = "";

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, DiscussionTopicActivity.this);

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
        swipeRefreshLayout.setOnRefreshListener(this);

        //Remove this code after

        topicheaderView = (RelativeLayout) findViewById(R.id.topicheader);
        topicheaderView.setVisibility(View.GONE);

        header = (View) getLayoutInflater().inflate(R.layout.discussionfourmcell_en, null);
        footor = (View) getLayoutInflater().inflate(R.layout.no_data_layout, null);
        TextView nodataFottor = footor.findViewById(R.id.nodata_label);
        nodataFottor.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel));

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        discussionForumModel = (DiscussionForumModelDg) getIntent().getSerializableExtra("forumModel");

        isFromNotification = getIntent().getBooleanExtra("NOTIFICATION", false);

        isFromGlobalSearch = getIntent().getBooleanExtra("ISGLOBALSEARCH", false);

        if (isFromNotification) {

            topicID = getIntent().getStringExtra("TOPICID");
        }

        if (isFromGlobalSearch) {
            swipeRefreshLayout.setEnabled(false);
            nextLevel = getIntent().getBooleanExtra("nextLevel", false);
            topicID = getIntent().getStringExtra("TOPICID");
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.discussionforum_label_topicslabel) + "</font>"));
        discussionFourmlistView = (ListView) findViewById(R.id.discussionfourmlist);
        discussionTopicModels = new ArrayList<DiscussionTopicModelDg>();
        fourmAdapter = new DiscussionTopicAdapter(this, BIND_ABOVE_CLIENT, discussionTopicModels, discussionForumModel.likePosts);
        discussionFourmlistView.setAdapter(fourmAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
        discussionFourmlistView.addFooterView(footor);
        discussionFourmlistView.addHeaderView(header, null, false);

        initilizeHeaderView(header);

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
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomments, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));


        isAbleCreateTopic = db.isPrivilegeExistsFor(StaticValues.CREATETOPIC);

        isAbleToDeleteTopic = db.isPrivilegeExistsFor(StaticValues.DELETETOPIC);

        isAbleToEditTopic = db.isPrivilegeExistsFor(StaticValues.EDITTOPIC);

        floatingActionButton.setImageDrawable(d);

        if (!isAbleCreateTopic) {
            floatingActionButton.setVisibility(View.GONE);
        }

        if (!discussionForumModel.createNewTopic) {
            floatingActionButton.setVisibility(View.GONE);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDetail = new Intent(context, CreateNewTopicActivity.class);
                intentDetail.putExtra("isfromedit", false);
                intentDetail.putExtra("topicModel", "");
                intentDetail.putExtra("forummodel", discussionForumModel);

                startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
            }
        });

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));


    }

    public void initilizeHeaderView(View view) {

        TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        txtTopics = (TextView) view.findViewById(R.id.txtTopics);
        txtLikes = (TextView) view.findViewById(R.id.txtLikes);
        TextView txtAuthor = (TextView) view.findViewById(R.id.txt_author);
        final TextView txtShortDesc = (TextView) view.findViewById(R.id.txtShortDesc);
        ImageView attachedImg = (ImageView) view.findViewById(R.id.attachedimg);
        ImageButton btnContextMenu = (ImageButton) view.findViewById(R.id.btn_contextmenu);

        btnContextMenu.setVisibility(View.INVISIBLE);

        txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtTopics.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtLikes.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtShortDesc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        txtName.setText(discussionForumModel.name);
        txtShortDesc.setText(discussionForumModel.description);
//        holder.txtAuthor.setText("Moderator: " + discussionForumModel.moderatorName + " | Created by: " + discussionForumModel.author + " on " + discussionForumModel.createdDate + " | Last Updated by : " + discussionForumModel.updatedAuthor + " on " + discussionForumModel.updatedDate);

        txtTopics.setText(discussionForumModel.noOfTopics + " " + getLocalizationValue(JsonLocalekeys.discussionforum_label_topicslabel));
        txtLikes.setText(discussionForumModel.totalLikes + " " + getLocalizationValue(JsonLocalekeys.discussionforum_button_likesbutton));

        String totalActivityStr = getLocalizationValue(JsonLocalekeys.discussionforum_label_moderatorlabel) + " ";

        if (isValidString(discussionForumModel.moderatorName)) {

            totalActivityStr = totalActivityStr + discussionForumModel.moderatorName;
        }

        if (isValidString(discussionForumModel.author)) {

            totalActivityStr = totalActivityStr + " | " + getLocalizationValue(JsonLocalekeys.discussionforum_label_createdbylabel) + discussionForumModel.author + " " + getLocalizationValue(JsonLocalekeys.discussionforum_label_createdonlabel) + " " + discussionForumModel.createdDate;
        }

        if (isValidString(discussionForumModel.updatedAuthor)) {

            totalActivityStr = totalActivityStr + " | " + getLocalizationValue(JsonLocalekeys.discussionforum_label_lastupdatedbylabel) + "  " + discussionForumModel.updatedAuthor + " " + getLocalizationValue(JsonLocalekeys.discussionforum_label_createdonlabel) + " " + discussionForumModel.updatedDate;
        }

        txtAuthor.setText(totalActivityStr);

        if (discussionForumModel.description.isEmpty()) {
            txtShortDesc.setVisibility(View.GONE);
        } else {
            txtShortDesc.setVisibility(View.VISIBLE);
        }

        String imgUrl = appUserModel.getSiteURL() + discussionForumModel.forumThumbnailPath;

        if (isValidString(discussionForumModel.forumThumbnailPath)) {
            attachedImg.setVisibility(View.VISIBLE);

//            if (imgUrl.startsWith("http:"))
//                imgUrl = imgUrl.replace("http:", "https:");

            Glide.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(attachedImg);
        } else {

            attachedImg.setVisibility(View.GONE);
        }

        // Sort View functionality
        LinearLayout sortLayout = (LinearLayout) view.findViewById(R.id.sortviewForTopics);

        sortLayout.setVisibility(View.VISIBLE);

        final TextView txtOld = (TextView) view.findViewById(R.id.txtOld);
        final TextView txtNew = (TextView) view.findViewById(R.id.txtNew);
        txtOld.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtNew.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtNew.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_newestontoplabel));
        txtOld.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_oldestontoplabel));

        txtOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fourmAdapter.applySortBy(true, "1");
                txtOld.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                txtNew.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            }
        });

        txtNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fourmAdapter.applySortBy(false, "1");
                txtNew.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                txtOld.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            }
        });

    }

    public void refreshMyLearning(Boolean isRefreshed) {

        vollyService.getJsonObjResponseVolley("GetForumTopics", appUserModel.getWebAPIUrl() + "/MobileLMS/GetForumTopics?ForumID=" + discussionForumModel.forumID + "&intUserID=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getSiteIDValue() + "&strLocale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)), appUserModel.getAuthHeaders());
        swipeRefreshLayout.setRefreshing(false);

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("GetForumTopics")) {
                    if (response != null) {
                        try {
                            db.injectDiscussionTopics(response, discussionForumModel);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

//                        nodata_Label.setText( );
                    }
                }
                svProgressHUD.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                svProgressHUD.dismiss();
                nodata_Label.setText("");
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
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
        discussionTopicModels = db.fetchDiscussionTopicList(appUserModel.getSiteIDValue(), discussionForumModel.forumID);
        if (discussionTopicModels != null) {
            fourmAdapter.refreshList(discussionTopicModels);
            footor.setVisibility(View.GONE);
        } else {
            discussionTopicModels = new ArrayList<DiscussionTopicModelDg>();
            fourmAdapter.refreshList(discussionTopicModels);
//            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel));
            footor.setVisibility(View.VISIBLE);
        }

        updateCommentsCount();

        triggerActionForFirstItem();
    }


    public void updateCommentsCount() {

        int totalCount = 0;
        for (int i = 0; i < discussionTopicModels.size(); i++) {
            int noofreplies = 0;
            try {
                noofreplies = discussionTopicModels.get(i).noOfReplies;
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                noofreplies = 0;
            }
            totalCount = totalCount + noofreplies;

        }

        txtTopics.setText(discussionTopicModels.size() + " " + getLocalizationValue(JsonLocalekeys.discussionforum_label_topicslabel));

//        txtCommentsCount.setText(totalCount + " Comment(s)");
//        txtTopicsCount.setText(discussionTopicModels.size() + " Topic(s)");
    }

    @Override
    public void onBackPressed() {

        closeForum(refreshAnyThing);
    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWFORUM", refresh);
        setResult(RESULT_OK, intent);
        finish();
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
                closeForum(refreshAnyThing);
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
    public void onRefresh() {
        if (isNetworkConnectionAvailable(context, -1)) {
            refreshMyLearning(true);

            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }

    public void triggerActionForFirstItem() {

        if (isFromNotification || nextLevel) {
            int selectedPostion = getPositionForNotification(topicID);
//            discussionFourmlistView.setSelection(selectedPostion);

            if (discussionTopicModels != null) {

                try {
                    attachFragment(discussionTopicModels.get(selectedPostion));
                    isFromNotification = false;
                } catch (IndexOutOfBoundsException ex) {
//                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public int getPositionForNotification(String contentID) {
        int position = 0;


        for (int k = 0; k < discussionTopicModels.size(); k++) {
            if (discussionTopicModels.get(k).contentID.equalsIgnoreCase(contentID)) {
                position = k;
                break;
            }

        }

        return position;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intentSocial = new Intent(this, SocialWebLoginsActivity.class);
        switch (view.getId()) {
            case R.id.card_view:
                attachFragment(discussionTopicModels.get(position));
                break;
            case R.id.btn_contextmenu:
                View v = discussionFourmlistView.getChildAt(position - discussionFourmlistView.getFirstVisiblePosition());
                assert v != null;
                ImageButton txtBtnDownload = null;
                try {
                    txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                catalogContextMenuMethod(position, view, txtBtnDownload, discussionTopicModels.get(position));
                break;
            case R.id.btn_attachment:
//                Toast.makeText(context, "attachment" + discussionCommentsModelList.get(i).attachment, Toast.LENGTH_SHORT).show();
                String imageUrl = appUserModel.getSiteURL() + "/content/sitefiles/" + discussionTopicModels.get(position).uploadFileName;
                intentSocial.putExtra("ATTACHMENT", true);
                intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, imageUrl);
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, discussionTopicModels.get(position).name);
                startActivity(intentSocial);
                break;
            case R.id.txtLikesCount:
                getForumLevelLikeList(discussionTopicModels.get(position));
                break;
            case R.id.txtLikes:
                try {
                    if (isNetworkConnectionAvailable(this, -1)) {

                        callCommentLikeApi(discussionTopicModels.get(position));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txtComment:
                gotoCommentActivity(discussionTopicModels.get(position));
                break;
            default:
                break;
        }

    }

    public void gotoCommentActivity(DiscussionTopicModelDg discussionTopicModel) {

        Intent intentDetail = new Intent(context, AddNewCommentActivity.class);
        intentDetail.putExtra("topicModel", discussionTopicModel);
        intentDetail.putExtra("forumModel", discussionForumModel);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

    }

    public void getForumLevelLikeList(final DiscussionTopicModelDg forumModel) {
        if (isNetworkConnectionAvailable(context, -1)) {

            String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetTopicCommentLevelLikeList?strObjectID=" + forumModel.contentID + "&intUserID=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getUserIDValue() + "&strLocale=preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))&intTypeID=1";

            vollyService.getStringResponseVolley("GetTopicCommentLevelLikeList", parmStringUrl, appUserModel.getAuthHeaders());

        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }


    public void attachFragment(DiscussionTopicModelDg topicModelDg) {
        Intent intentDetail = new Intent(context, DiscussionCommentsActivity.class);
        intentDetail.putExtra("topicModel", topicModelDg);
        intentDetail.putExtra("forumModel", discussionForumModel);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final DiscussionTopicModelDg discussionTopicModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.discussonforum, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();
        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_edittopicoption));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.discussionforum_actionsheet_deletetopicoption));
        if (isAbleToEditTopic) {
            menu.getItem(0).setVisible(true);//view
        } else {
            menu.getItem(0).setVisible(false);//view

        }

        if (isAbleToDeleteTopic) {

            menu.getItem(1).setVisible(true);//view
        } else {
            menu.getItem(1).setVisible(false);//view

        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ctx_edit:
                        Intent intentDetail = new Intent(context, CreateNewTopicActivity.class);
                        intentDetail.putExtra("isfromedit", true);
                        intentDetail.putExtra("topicModel", discussionTopicModel);
                        intentDetail.putExtra("forummodel", discussionForumModel);
                        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
                        break;
                    case R.id.ctx_delete:
                        try {
                            deleteTopicFromServerBuildObj(discussionTopicModel);
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
            txtCountVoted.setText(likesModelList.size() + " " + getLocalizationValue(JsonLocalekeys.discussionforum_button_likesbutton));
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        } else {
            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_nolikesfound), Toast.LENGTH_SHORT).show();
        }


    }

    public void callCommentLikeApi(final DiscussionTopicModelDg topicModelDg) throws JSONException {
        int isLiked = 0;
        if (topicModelDg.likeState) {
            isLiked = 0;
            discussionForumModel.totalLikes = discussionForumModel.totalLikes - 1;
        } else {
            isLiked = 1;
            discussionForumModel.totalLikes = discussionForumModel.totalLikes + 1;
        }

        txtLikes.setText(discussionForumModel.totalLikes + " " + getLocalizationValue(JsonLocalekeys.discussionforum_button_likesbutton));

        JSONObject parameters = new JSONObject();

        parameters.put("intUserID", appUserModel.getUserIDValue());
        parameters.put("strObjectID", "" + topicModelDg.contentID);
        parameters.put("intTypeID", 1);
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
                refreshAnyThing = true;
            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(DiscussionTopicActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
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
/*                headers.put("Content-Type", "application/json");
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

    public void deleteTopicFromServerBuildObj(DiscussionTopicModelDg topicModelDg) throws JSONException {

        JSONObject parameters = new JSONObject();


        parameters.put("SiteID", appUserModel.getSiteIDValue());
        parameters.put("TopicID", topicModelDg.contentID);
        parameters.put("ForumID", discussionForumModel.forumID);
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("ForumName", discussionForumModel.name);
        parameters.put("LocaleID", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));

        String parameterString = parameters.toString();
        Log.d(TAG, "validateNewForumCreation: " + parameterString);

        if (isNetworkConnectionAvailable(this, -1)) {
            deleteTopicFromServer(parameterString);
        } else {
            Toast.makeText(context, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }


    }

    public void deleteTopicFromServer(final String postData) {


        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteForumTopic";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, "Success! \n" + getLocalizationValue(JsonLocalekeys.discussionforum_alertsubtitle_topichasbeensuccessfullydeleted), Toast.LENGTH_SHORT).show();
                    refreshAnyThing = true;
                    refreshMyLearning(true);
//                    deleteCommentFromLocalDB();
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
}

