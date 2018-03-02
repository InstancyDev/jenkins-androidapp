package com.instancy.instancylearning.discussionfourms;

import android.app.Activity;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
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
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.getToolbarLogoIcon;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

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

    DatabaseHandler db;
    List<DiscussionTopicModel> discussionTopicModels = null;

    ListView discussionFourmlistView;
    DiscussionForumModel discussionForumModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    DiscussionTopicAdapter fourmAdapter;


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

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

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
        swipeRefreshLayout.setOnRefreshListener(this);


        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        discussionForumModel = (DiscussionForumModel) getIntent().getSerializableExtra("forumModel");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "Topics" + "</font>"));
        discussionFourmlistView = (ListView) findViewById(R.id.discussionfourmlist);
        fourmAdapter = new DiscussionTopicAdapter(this, BIND_ABOVE_CLIENT, discussionTopicModels);
        discussionFourmlistView.setAdapter(fourmAdapter);
        discussionFourmlistView.setOnItemClickListener(this);
        discussionFourmlistView.setEmptyView(findViewById(R.id.nodata_label));

        discussionTopicModels = new ArrayList<DiscussionTopicModel>();

//        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
//        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
//        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
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
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomments, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));

        floatingActionButton.setImageDrawable(d);

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

    public void initilizeHeaderView() {

        btnContextMenu.setVisibility(View.INVISIBLE);
        txtTopicsCount.setVisibility(View.INVISIBLE);

        txtName.setText(discussionForumModel.name);
        txtShortDisc.setText(discussionForumModel.descriptionValue);
        txtAuthor.setText("Moderator:" + discussionForumModel.author + " ");
        txtLastUpdate.setText("Last update: " + discussionForumModel.createddate + " ");

        txtTopicsCount.setText(discussionForumModel.nooftopics + " Topic(s)");
        txtCommentsCount.setText(discussionForumModel.totalposts + " Comment(s)");

        txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtLastUpdate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        txtTopicsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCommentsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        String imgUrl = appUserModel.getSiteURL() + discussionForumModel.imagedata;
        Picasso.with(this).load(imgUrl).placeholder(R.drawable.user_placeholder).into(imgThumb);
    }

    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        vollyService.getJsonObjResponseVolley("GETCALL", appUserModel.getWebAPIUrl() + "/MobileLMS/GetForumTopics?ForumID=" + discussionForumModel.forumid, appUserModel.getAuthHeaders());
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
                        db.injectDiscussionTopicsList(response, discussionForumModel);
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
        discussionTopicModels = db.fetchDiscussionTopicModelList(appUserModel.getSiteIDValue(), discussionForumModel.forumid);
        if (discussionTopicModels != null) {
            fourmAdapter.refreshList(discussionTopicModels);
        } else {
            discussionTopicModels = new ArrayList<DiscussionTopicModel>();
            fourmAdapter.refreshList(discussionTopicModels);
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
            refreshMyLearning(true);

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
        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("NEWFORUM", false);
                if (refresh) {
                    refreshMyLearning(false);
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
                attachFragment(discussionTopicModels.get(position));
                break;
            case R.id.btn_contextmenu:
                View v = discussionFourmlistView.getChildAt(position - discussionFourmlistView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, discussionTopicModels.get(position));
                break;
            default:

        }

    }

    public void attachFragment(DiscussionTopicModel forumModel) {
        Intent intentDetail = new Intent(context, DiscussionCommentsActivity.class);
        intentDetail.putExtra("topicModel", forumModel);
        startActivity(intentDetail);
    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final DiscussionTopicModel discussionTopicModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.discussonforum, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);//view

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("Edit")) {
                    Intent intentDetail = new Intent(context, CreateNewTopicActivity.class);
                    intentDetail.putExtra("isfromedit", true);
                    intentDetail.putExtra("topicModel", discussionTopicModel);
                    intentDetail.putExtra("forummodel", discussionForumModel);
                    startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

                }
                return true;
            }
        });
        popup.show();//showing popup menu

    }

}

