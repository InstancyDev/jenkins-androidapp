package com.instancy.instancylearning.mycompetency;

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
import android.widget.ExpandableListView;
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
import com.instancy.instancylearning.discussionfourms.AddNewCommentActivity;
import com.instancy.instancylearning.discussionfourms.DiscussionCommentsAdapter;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionCommentsModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
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

public class CompetencyCatSkillActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = CompetencyCatSkillActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    DatabaseHandler db;
    String jobRoleName = "", jobTag = "";

    int jobRoleID = 0;

    private static int lastClicked = 0;
    private static int lastClickedItems = -1;

    List<CompetencyCategoryModel> competencyCategoryModelList = null;
    ExpandableListView skillSetListView;

    protected List<SkillModel> skillModelList = new ArrayList<>();

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    CompetencyCatSkillAdapter competencyCatSkillAdapter;

    boolean refreshAnyThing = false;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    SideMenusModel sideMenusModel = null;

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.competencycatskillactivity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

        db = new DatabaseHandler(this);
        ButterKnife.bind(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());

        swipeRefreshLayout.setOnRefreshListener(this);
        svProgressHUD = new SVProgressHUD(context);
        lastClicked = 0;
        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("SIDEMENUMODEL");
        jobRoleID = getIntent().getIntExtra("JOBROLEID", 0);
        jobRoleName = getIntent().getStringExtra("JOBROLENAME");
        jobTag = getIntent().getStringExtra("JOBTAG");

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "" + jobRoleName + "</font>"));
        competencyCategoryModelList = new ArrayList<CompetencyCategoryModel>();

        skillSetListView = (ExpandableListView) findViewById(R.id.mycompetencylist);
        competencyCatSkillAdapter = new CompetencyCatSkillAdapter(this, BIND_ABOVE_CLIENT, competencyCategoryModelList, jobTag, appUserModel, sideMenusModel);
        skillSetListView.setAdapter(competencyCatSkillAdapter);
        skillSetListView.setOnItemClickListener(this);
        skillSetListView.setEmptyView(findViewById(R.id.nodata_label));
        skillSetListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                Toast.makeText(CompetencyCatSkillActivity.this, "Here groupPosition " + competencyCategoryModelList.get(groupPosition).prefCategoryTitle, Toast.LENGTH_SHORT).show();
                if (lastClicked != groupPosition) {
                    skillModelList = new ArrayList<>();
                    competencyCatSkillAdapter.refreshList(competencyCategoryModelList, skillModelList);

                    refreshChildslist(competencyCategoryModelList.get(groupPosition));
                }

                lastClicked = groupPosition;
                return false;
            }


        });

        skillSetListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastClickedItems != -1 && groupPosition != lastClickedItems) {
                    skillSetListView.collapseGroup(lastClickedItems);
                }
                lastClickedItems = groupPosition;
            }
        });

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        if (isNetworkConnectionAvailable(this, -1)) {
            refreshCatgory(false);
        } else {
            injectFromDbtoModel();
        }

    }

    public void refreshCatgory(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/CompetencyManagement/GetUserPrefCatData?ComponentID=" + sideMenusModel.getComponentId() + "&ComponentInstanceID=" + sideMenusModel.getRepositoryId() + "&UserID=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&Locale=en-us&JobRoleID=" + jobRoleID;

        vollyService.getJsonObjResponseVolley("COMPSKILLS", urlStr, appUserModel.getAuthHeaders());

    }

    public void refreshChildslist(CompetencyCategoryModel competencyCategoryModel) {
        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        String urlStr = appUserModel.getWebAPIUrl() + "/CompetencyManagement/GetUserSkills?ComponentID=" + sideMenusModel.getComponentId() + "&ComponentInstanceID=" + sideMenusModel.getRepositoryId() + "&UserID=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&PrefCatid=" + competencyCategoryModel.prefCategoryID + "&JobRoleID=" + jobRoleID;

        vollyService.getJsonObjResponseVolley("COMPCHILD", urlStr, appUserModel.getAuthHeaders());

    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("COMPSKILLS")) {

                    if (requestType.equalsIgnoreCase("COMPSKILLS")) {
                        if (response != null && response.has("PrefCategoryList")) {
                            try {
                                db.injectPrefCategoryList(response, "" + jobRoleID);
                                injectFromDbtoModel();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            nodata_Label.setText(getResources().getString(R.string.no_data));
                        }
                    }
                }

                if (requestType.equalsIgnoreCase("COMPCHILD")) {

                    if (requestType.equalsIgnoreCase("COMPCHILD")) {
                        if (response != null && response.has("SkillsList")) {

                            try {
                                generateSkillCells(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
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
                nodata_Label.setText(getResources().getString(R.string.no_data));
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
        competencyCategoryModelList = db.fetchCompetencyCategoryModel();
        if (competencyCategoryModelList != null) {
            competencyCatSkillAdapter.refreshList(competencyCategoryModelList, skillModelList);
        } else {
            competencyCategoryModelList = new ArrayList<CompetencyCategoryModel>();
            competencyCatSkillAdapter.refreshList(competencyCategoryModelList, skillModelList);
            nodata_Label.setText(getResources().getString(R.string.no_data));
        }
    }

    public void generateSkillCells(JSONObject response) throws JSONException {

        JSONArray jsonArraySkillsList = response.getJSONArray("SkillsList");

        skillModelList = new ArrayList<>();

        for (int i = 0; i < jsonArraySkillsList.length(); i++) {

            JSONObject skillsSetObj = jsonArraySkillsList.getJSONObject(i);
            SkillModel skillModel = new SkillModel();

            if (skillsSetObj.has("SkillName")) {

                skillModel.skillName = skillsSetObj.optString("SkillName");
                skillModel.skillDescription = skillsSetObj.optString("Description");
                skillModel.jobRoleID = skillsSetObj.optString("JobRoleID");
                skillModel.gapScore = skillsSetObj.optDouble("Gap");
                skillModel.managerScore = skillsSetObj.optDouble("ManagersEvaluation");
                skillModel.contentAuthorScore = skillsSetObj.optDouble("ContentEval");
                skillModel.userScore = skillsSetObj.optDouble("UserEvaluation");
                skillModel.valueName = skillsSetObj.optString("UserEvaluation");
                skillModel.skillID = skillsSetObj.optString("SkillID");
                skillModel.prefCategoryID = skillsSetObj.optString("JobRoleID");
                skillModel.weightedAverage = skillsSetObj.optDouble("WeightedAverage");
                skillModel.requiredProficiency = skillsSetObj.optDouble("RequiredProficiency");
                skillModel.requiredProfArys = skillsSetObj.optJSONArray("RequiredProfValues");
            }
            skillModelList.add(skillModel);
        }
        averageTheValues();
        competencyCatSkillAdapter.refreshList(competencyCategoryModelList, skillModelList);
    }

    public void averageTheValues() {
        DecimalFormat df2 = new DecimalFormat(".#");
        for (int i = 0; i < skillModelList.size(); i++) {

            //            DecimalFormat df = new DecimalFormat("#.#");
//            df.format(averageScore);
            double averageValue = (skillModelList.get(i).userScore + skillModelList.get(i).managerScore + skillModelList.get(i).contentAuthorScore) / 3.0;
            skillModelList.get(i).weightedAverage = Double.parseDouble(df2.format(averageValue));
        }
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
            refreshCatgory(true);
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        switch (view.getId()) {
            case R.id.btn_contextmenu:
                break;
            case R.id.btn_attachment:
//                Toast.makeText(context, "attachment" + discussionCommentsModelList.get(i).attachment, Toast.LENGTH_SHORT).show();
            default:
        }
    }
}
