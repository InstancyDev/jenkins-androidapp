package com.instancy.instancylearning.discussionfourms;

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
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionCommentsModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
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
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

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

    DatabaseHandler db;
    List<DiscussionCommentsModel> discussionCommentsModelList = null;
    ResultListner resultListner = null;

    ListView discussionFourmlistView;
    DiscussionTopicModel discussionTopicModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    DiscussionCommentsAdapter commentsAdapter;


    boolean refreshAnyThing = false;

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
    @BindView(R.id.btn_attachment)
    TextView txtTopicAttachment;


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


        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

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
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        floatingActionButton.setImageDrawable(d);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentDetail = new Intent(context, AddNewCommentActivity.class);
                intentDetail.putExtra("forumModel", discussionTopicModel);
                startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

            }
        });

        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));

    }

    public void initilizeHeaderView() {

        btnContextMenu.setVisibility(View.INVISIBLE);
        txtTopicsCount.setVisibility(View.INVISIBLE);

        if (discussionTopicModel.attachment.length() > 10) {


            Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(findViewById(R.id.btn_attachment), iconFont);

            txtTopicAttachment.setVisibility(View.VISIBLE);
            txtTopicAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentSocial = new Intent(DiscussionCommentsActivity.this, SocialWebLoginsActivity.class);
                    String imageUrl = appUserModel.getSiteURL() + "/content/sitefiles/" + discussionTopicModel.attachment;
                    intentSocial.putExtra("ATTACHMENT", true);
                    intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, imageUrl);
                    intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, discussionTopicModel.displayName);
                    startActivity(intentSocial);

                }
            });
        }

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
        txtTopicAttachment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        txtTopicsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCommentsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        if (discussionTopicModel.longdescription.isEmpty() || discussionTopicModel.longdescription.contains("null")) {
            txtShortDisc.setVisibility(View.GONE);
        } else {
            txtShortDisc.setVisibility(View.VISIBLE);
        }

        String imgUrl = appUserModel.getSiteURL() + discussionTopicModel.imagedata;
        Picasso.with(this).load(imgUrl).placeholder(R.drawable.user_placeholder).into(imgThumb);


    }

    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
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
        txtCommentsCount.setText(discussionCommentsModelList.size() + " Comment(s)");
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
                catalogContextMenuMethod(i, view, txtBtnDownload, discussionCommentsModelList.get(i));
                break;
            case R.id.btn_attachment:
//                Toast.makeText(context, "attachment" + discussionCommentsModelList.get(i).attachment, Toast.LENGTH_SHORT).show();
                intentSocial.putExtra("ATTACHMENT", true);
                intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, discussionCommentsModelList.get(i).attachment);
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, discussionTopicModel.displayName);//9963014569
                startActivity(intentSocial);
            default:
        }
    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final DiscussionCommentsModel discussionCommentsModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.commentdelete, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);//view

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("Delete")) {

//                    Toast.makeText(context, "Delete Here " + discussionCommentsModel.displayName, Toast.LENGTH_SHORT).show();

                    try {
                        deleteCommentFromServerBuildObj(discussionCommentsModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        popup.show();//showing popup menu

    }

    public void deleteCommentFromLocalDB(DiscussionCommentsModel discussionCommentsModel) {

        try {
            String strDelete = "DELETE FROM " + TBL_TOPICCOMMENTS + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND forumid ='" + discussionTopicModel.forumid + "' AND commentid  ='" + discussionCommentsModel.commentID + "'";
            db.executeQuery(strDelete);
            injectFromDbtoModel();

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

        int commentCount = 0;
        try {
            commentCount = Integer.parseInt(discussionTopicModel.noofreplies);
        } catch (NumberFormatException ex) {

            ex.printStackTrace();
        }

        if (commentCount > 0) {
            try {

                int count = commentCount;
                db.executeQuery("UPDATE " + TBL_TOPICCOMMENTS + "SET noofreplies = " + count + " WHERE forumid =" + discussionTopicModel.forumid + "' AND topicid = '" + discussionTopicModel.topicid + "' AND siteid = '" + discussionTopicModel.siteid);

            } catch (SQLiteException sqlEx) {

                sqlEx.printStackTrace();
            }

        }

    }

    public void deleteCommentFromServerBuildObj(DiscussionCommentsModel commentsModel) throws JSONException {

        JSONObject parameters = new JSONObject();

        parameters.put("SiteID", appUserModel.getSiteIDValue());
        parameters.put("TopicID", commentsModel.topicID);
        parameters.put("ReplyID", commentsModel.replyID);
        parameters.put("ForumID", commentsModel.forumID);
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("TopicName", appUserModel.getSiteIDValue());
        parameters.put("NoofReplies", "0");
        parameters.put("LocaleID", "en-us");
        parameters.put("LastPostedDate", commentsModel.postedDate);
        parameters.put("CreatedUserID", commentsModel.postedBy);

        String parameterString = parameters.toString();
        Log.d(TAG, "validateNewForumCreation: " + parameterString);

        if (isNetworkConnectionAvailable(this, -1)) {

            String replaceDataString = parameterString.replace("\"", "\\\"");
            String addQuotes = ('"' + replaceDataString + '"');

            deleteCommentFromServer(addQuotes, commentsModel);
        } else {
            Toast.makeText(context, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }


    }

    public void deleteCommentFromServer(final String postData, final DiscussionCommentsModel commentsModel) {

//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteForumComment";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, "Success! \nYour new topic has been successfully posted to server.", Toast.LENGTH_SHORT).show();
                    refreshAnyThing = true;
                    deleteCommentFromLocalDB(commentsModel);
                } else {

                    Toast.makeText(context, "New topic cannot be posted to server. Contact site admin.", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
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
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

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
