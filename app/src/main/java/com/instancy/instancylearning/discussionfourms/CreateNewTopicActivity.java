package com.instancy.instancylearning.discussionfourms;

import android.content.Context;
import android.content.Intent;
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
import android.support.v7.widget.CardView;
import android.text.Html;
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

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionCommentsModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class CreateNewTopicActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = CreateNewTopicActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    DatabaseHandler db;
    List<DiscussionCommentsModel> discussionCommentsModelList = null;
    SwipeRefreshLayout swipeRefreshLayout;
    ResultListner resultListner = null;

    ListView discussionFourmlistView;
    DiscussionTopicModel discussionTopicModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    DiscussionCommentsAdapter commentsAdapter;


    @Nullable
    @BindView(R.id.txt_name)
    TextView txtName;

    @Nullable
    @BindView(R.id.card_view)
    CardView card_view;

    @Nullable
    @BindView(R.id.txtShortDesc)
    TextView txtShortDisc;

    @Nullable
    @BindView(R.id.imagethumb)
    ImageView imgThumb;

    @Nullable
    @BindView(R.id.txtLastUpdate)
    TextView txtLastUpdate;

    @Nullable
    @BindView(R.id.txt_author)
    TextView txtAuthor;

    @Nullable
    @BindView(R.id.txttopics)
    TextView txtTopicsCount;

    @Nullable
    @BindView(R.id.btn_contextmenu)
    ImageButton btnContextMenu;

    @Nullable
    @BindView(R.id.txtcomments)
    TextView txtCommentsCount;

    @Nullable
    @BindView(R.id.fab_comment_button)
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussiontopics_fragment);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DatabaseHandler(this);
        ButterKnife.bind(this);

        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERNAME));
        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipemylearning);
        swipeRefreshLayout.setOnRefreshListener(this);
        svProgressHUD = new SVProgressHUD(context);


        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        discussionTopicModel = (DiscussionTopicModel) getIntent().getSerializableExtra("topicModel");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "Comments" + "</font>"));
        discussionFourmlistView = (ListView) findViewById(R.id.discussionfourmlist);
        commentsAdapter = new DiscussionCommentsAdapter(this, BIND_ABOVE_CLIENT, discussionCommentsModelList);
        discussionFourmlistView.setAdapter(commentsAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
        discussionFourmlistView.setEmptyView(findViewById(R.id.nodata_label));

        discussionCommentsModelList = new ArrayList<DiscussionCommentsModel>();

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
        if (isNetworkConnectionAvailable(this, -1)) {
            refreshMyLearning(false);
        } else {
            injectFromDbtoModel();
        }
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomment, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));

        floatingActionButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.user_placeholder));
    }

    public void initilizeHeaderView() {

        btnContextMenu.setVisibility(View.INVISIBLE);
        txtTopicsCount.setVisibility(View.INVISIBLE);

        txtName.setText(discussionTopicModel.name);
        txtShortDisc.setText(discussionTopicModel.longdescription);
        txtAuthor.setText(discussionTopicModel.latestreplyby);
        txtLastUpdate.setText(discussionTopicModel.createddate + " ");

        txtCommentsCount.setText(discussionTopicModel.noofreplies + " Comment(s)");

        txtTopicsCount.setVisibility(View.INVISIBLE);


        txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtLastUpdate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        txtTopicsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCommentsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        if (discussionTopicModel.longdescription.isEmpty() || discussionTopicModel.longdescription.contains("null")) {
            txtShortDisc.setVisibility(View.GONE);
        } else {
            txtShortDisc.setVisibility(View.VISIBLE);
        }


    }

    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        vollyService.getJsonObjResponseVolley("GETCALL", appUserModel.getWebAPIUrl() + "/MobileLMS/GetForumComments?SiteID=" + appUserModel.getSiteIDValue() + "&ForumID=" + discussionTopicModel.forumid + "&TopicID=" + discussionTopicModel.topicid, appUserModel.getAuthHeaders());

        swipeRefreshLayout.setRefreshing(false);
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("GETCALL")) {

                    try {
                        db.injectDiscussionCommentsList(response, discussionTopicModel);
                        injectFromDbtoModel();
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public void injectFromDbtoModel() {
        discussionCommentsModelList = db.fetchDiscussionCommentsModelList(appUserModel.getSiteIDValue(), discussionTopicModel);
        if (discussionCommentsModelList != null) {
            commentsAdapter.refreshList(discussionCommentsModelList);
        } else {
            discussionCommentsModelList = new ArrayList<DiscussionCommentsModel>();
            commentsAdapter.refreshList(discussionCommentsModelList);
        }

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
    public void onRefresh() {
        if (isNetworkConnectionAvailable(context, -1)) {
//            refreshCatalog(true);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


    }
}

