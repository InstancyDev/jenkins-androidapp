package com.instancy.instancylearning.discussionfourmsenached;


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

import android.support.v7.widget.SearchView;
import android.text.Html;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;


import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.android.volley.VolleyError;


import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;


import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;

import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;
import java.util.Locale;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class DiscussionModeratorListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = DiscussionModeratorListActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    ResultListner resultListner = null;

    ListView discussionFourmlistView;

    DiscussionCommentsModelDg discussionCommentsModel;

    PreferencesManager preferencesManager;

    RelativeLayout relativeLayout;

    UiSettingsModel uiSettingsModel;

    DiscussionModeratorAdapter moderatorAdapter;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    View commentHeaderView;

    boolean refreshAnyThing = false;

    @Nullable
    @BindView(R.id.fab_comment_button)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    List<DiscussionModeratorModel> discussionModeratorModelList = null;
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,DiscussionModeratorListActivity.this);

    }    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussiontopics_fragment);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();

        ButterKnife.bind(this);

        swipeRefreshLayout.setEnabled(false);

        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));


        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);


        commentHeaderView = (RelativeLayout) findViewById(R.id.topicheader);
        commentHeaderView.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                getLocalizationValue(JsonLocalekeys.discussionforum_label_moderatorlabel).replace(":","") + "</font>"));
        discussionModeratorModelList = new ArrayList<DiscussionModeratorModel>();
        discussionFourmlistView = (ListView) findViewById(R.id.discussionfourmlist);
        moderatorAdapter = new DiscussionModeratorAdapter(this, BIND_ABOVE_CLIENT, discussionModeratorModelList);
        discussionFourmlistView.setAdapter(moderatorAdapter);
        discussionFourmlistView.setOnItemClickListener(this);

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

        }

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomment, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

    }

    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/GetUserListBasedOnRoles?intSiteID=" + appUserModel.getSiteIDValue() + "&intUserID=" + appUserModel.getUserIDValue() + "&strLocale=preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))";


        vollyService.getStringResponseVolley("GetUserListBasedOnRoles", urlStr, appUserModel.getAuthHeaders());

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                svProgressHUD.dismiss();
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
                if (requestType.equalsIgnoreCase("GetUserListBasedOnRoles")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            discussionModeratorModelList = createDiscussionModeratorList(response);
                            moderatorAdapter.refreshList(discussionModeratorModelList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }

                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {
                svProgressHUD.dismiss();
            }
        };
    }

    @Override
    public void onBackPressed() {

    }

    public void closeForum(int position) {
        Intent intent = getIntent();
        if (position != -1) {
            intent.putExtra("ISSELECTED", true);
            intent.putExtra("moderatorModel", discussionModeratorModelList.get(position));
        } else {
            intent.putExtra("ISSELECTED", false);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.globalsearchmenu, menu);
        MenuItem item_search = menu.findItem(R.id.globalsearch);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
//          tintMenuIcon(getActivity(), item_search, R.color.colorWhite);.
            item_search.setTitle(getLocalizationValue(JsonLocalekeys.search_label));
            final SearchView searchView = (SearchView) item_search.getActionView();
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint(getLocalizationValue(JsonLocalekeys.commoncomponent_label_searchlabel));
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
//            txtSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

//            ImageView searchClose = searchView.findViewById(android.support.v7.appcompat.R.id.home);
//            searchClose.setImageResource(R.drawable.ic_filter_list_black_24dp);

            // Set search view clear icon
            ImageView searchIconClearView = (ImageView) searchView
                    .findViewById(android.support.v7.appcompat.R.id.search_close_btn);

            if (searchIconClearView != null) {

                searchIconClearView.setImageResource(R.drawable.close);

            }
            // Does help!

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);



            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    moderatorAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });


        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                closeForum(-1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

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
                closeForum(i);
                break;
            default:
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


    public List<DiscussionModeratorModel> createDiscussionModeratorList(String responseStr) throws JSONException {

        List<DiscussionModeratorModel> discussionForumModelList1 = new ArrayList<>();

        JSONArray jsonTableAry = new JSONArray(responseStr);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            DiscussionModeratorModel discussionModeratorModel = new DiscussionModeratorModel();

            discussionModeratorModel.userID = jsonMyLearningColumnObj.optInt("UserID");
            discussionModeratorModel.userThumb = jsonMyLearningColumnObj.optString("UserThumb");
            discussionModeratorModel.userName = jsonMyLearningColumnObj.optString("UserName");
            discussionModeratorModel.userAddress = jsonMyLearningColumnObj.optString("UserDesg");
            discussionModeratorModel.userDesg = jsonMyLearningColumnObj.optString("UserAddress");

            discussionForumModelList1.add(discussionModeratorModel);
        }
        return discussionForumModelList1;
    }

}
