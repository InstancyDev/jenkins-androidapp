package com.instancy.instancylearning.globalsearch;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.askexpertenached.AskExpertQuestionModelDg;
import com.instancy.instancylearning.askexpertenached.AskExpertsAnswersActivity;
import com.instancy.instancylearning.askexpertenached.AskExpertsCommentsActivity;
import com.instancy.instancylearning.asynchtask.GlobalSearchResultSynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionCommentsActivity;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionForumModelDg;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionTopicActivity;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionTopicModelDg;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;

import com.instancy.instancylearning.interfaces.GlobalSearchResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.GLobalSearchSelectedModel;

import com.instancy.instancylearning.models.GlobalSearchResultModel;
import com.instancy.instancylearning.models.GlobalSearchResultModelNew;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningDetail_Activity;
import com.instancy.instancylearning.peoplelisting.PeopleListingProfile;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.instancy.instancylearning.globalpackage.GlobalMethods.convertGlobalModelToMylearningModel;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.convertGlobalModelToSideMenuModel;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.launchCourseForGlobalSearch;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.relatedContentView;
import static com.instancy.instancylearning.models.GlobalSearchResultModelNew.fetchCategoriesData;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.fromHtmlToString;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.returnEventCompleted;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class GlobalSearchResultsActivity extends AppCompatActivity implements View.OnClickListener, GlobalSearchResultListner {

    private static final String TAG = GlobalSearchResultsActivity.class.getSimpleName();
    ExpandableListView chxListview;
    GlobalSearchResultsAdapter searchAdapter;
    AppUserModel appUserModel;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    AppController appcontroller;

    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;

    HashMap<String, List<GlobalSearchResultModelNew>> expandableListDetail;
    List<String> expandableListTitle;

    RelativeLayout globalHeaderLayout;
    LinearLayout bottomBtnLayout;
    TextView nodataLabel;
    CheckBox chxSelectedCategory;
    TextView bottomLine;
    String queryString = "";
    List<GLobalSearchSelectedModel> gLobalSearchSelectedModelList = null;
    GlobalSearchResultSynchTask globalSearchResultSynchTask;

    TextView globalSearchResult;

    RelativeLayout globalsearchresultLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.globalsearchactivity);
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(this);
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);
        globalHeaderLayout = (RelativeLayout) findViewById(R.id.globalsearchheader);
        bottomBtnLayout = (LinearLayout) findViewById(R.id.filter_btn_layout);
        chxSelectedCategory = (CheckBox) globalHeaderLayout.findViewById(R.id.chxBox);
        bottomLine = (TextView) globalHeaderLayout.findViewById(R.id.bottomLine);
        chxSelectedCategory.setVisibility(View.GONE);
        bottomBtnLayout.setVisibility(View.GONE);
        bottomLine.setVisibility(View.GONE);
        nodataLabel = (TextView) findViewById(R.id.nodata_label);

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();

        gLobalSearchSelectedModelList = (List<GLobalSearchSelectedModel>) getIntent().getSerializableExtra("globalsearchlist");

        queryString = getIntent().getStringExtra("queryString");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + queryString + "</font>"));


        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        chxListview = (ExpandableListView) findViewById(R.id.chxlistview);


        globalsearchresultLayout = (RelativeLayout) findViewById(R.id.globalsearchresults);
        globalsearchresultLayout.setVisibility(View.VISIBLE);

        globalSearchResult = (TextView) findViewById(R.id.txt_relaventskills);

        globalSearchResult.setText("SearchResult");

        // Construct our adapter, using our own layout and myTeams

        expandableListTitle = new ArrayList<>();
        expandableListDetail = new HashMap<>();

        updateListView(true);

        chxListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;

            }
        });

        chxListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                final GlobalSearchResultModelNew expandedListText = (GlobalSearchResultModelNew) searchAdapter.getChild(groupPosition, childPosition);
//              Toast.makeText(GlobalSearchResultsActivity.this, "groupPosition: " + groupPosition + "childPosition: " + childPosition, Toast.LENGTH_SHORT).show();
                selectedFragment(expandedListText, false, false);
                return false;
            }
        });

        if (isNetworkConnectionAvailable(this, -1)) {
            refreshCatagories(false);
        } else {
            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }
    }

    public void updateListView(boolean isListClicked) {
        if (isListClicked) {
            searchAdapter = new GlobalSearchResultsAdapter(this, expandableListTitle, expandableListDetail, chxListview);
            chxListview.setAdapter(searchAdapter);

        } else {
            searchAdapter.refreshList(expandableListTitle, expandableListDetail);

        }

        if (expandableListDetail != null && expandableListDetail.size() > 0) {
            for (int i = 0; i < expandableListDetail.size(); i++)
                chxListview.expandGroup(i);
        }

    }

    public void selectedFragment(GlobalSearchResultModelNew globalSearchResultModel, boolean nextLevel, boolean nextLevel2) {

//        SideMenusModel sideMenusModel = db.getSideMenuModelForGlobalSearch(expandedListText.siteid, expandedListText.menuID);

        SideMenusModel sideMenusModel = convertGlobalModelToSideMenuModel(globalSearchResultModel);

        if (sideMenusModel.isDataFound() && !sideMenusModel.contextMenuId.equalsIgnoreCase("0")) {
            Intent intentDetail = new Intent(GlobalSearchResultsActivity.this, GlobalCatalogActivity.class);
            intentDetail.putExtra("SIDEMENUMODEL", sideMenusModel);
            intentDetail.putExtra("query", queryString);
            intentDetail.putExtra("ISFROMGLOBAL", true);
            intentDetail.putExtra("nextLevel", nextLevel);
            intentDetail.putExtra("nextLevel2", nextLevel2);

            intentDetail.putExtra("CONTENTID", globalSearchResultModel.contentid);
            intentDetail.putExtra("FOLDERID", globalSearchResultModel.folderid);

            if (globalSearchResultModel.objecttypeid == 653) {
                intentDetail.putExtra("FOLDERID", globalSearchResultModel.folderid);
                intentDetail.putExtra("ANSWERID", globalSearchResultModel.startpage);

            } else if (globalSearchResultModel.objecttypeid == 654) {
                intentDetail.putExtra("CONTENTID", globalSearchResultModel.startpage);
                intentDetail.putExtra("ANSWERID", globalSearchResultModel.contentid);
            }
            startActivity(intentDetail);

        } else {

            MyLearningModel myLearningDetalData = convertGlobalModelToMylearningModel(globalSearchResultModel, appUserModel);

            checkUserLogin(myLearningDetalData, false, true, globalSearchResultModel);

        }

//        SideMenusModel sideMenusModel = createSideMenuModel(expandedListText);

    }


    public void selectedFragmentForSubsite(SideMenusModel sideMenusModel) {

        Intent intentDetail = new Intent(GlobalSearchResultsActivity.this, GlobalCatalogActivity.class);
        intentDetail.putExtra("SIDEMENUMODEL", sideMenusModel);
        intentDetail.putExtra("query", queryString);
        intentDetail.putExtra("ISFROMGLOBAL", true);
        startActivity(intentDetail);

    }

//    public SideMenusModel createSideMenuModel(GlobalSearchResultModel expandedListText) {
//        SideMenusModel sideMenusModel = new SideMenusModel();
//
//        sideMenusModel.setMenuId(expandedListText.menuID);
//        sideMenusModel.setComponentId("" + expandedListText.componentID);
//        sideMenusModel.setRepositoryId("" + expandedListText.componentInstanceID);
//        sideMenusModel.setDisplayName(expandedListText.componentName);
//        sideMenusModel.setSiteID(expandedListText.siteID);
//        sideMenusModel.setContextMenuId("" + expandedListText.contextMenuId);
//        sideMenusModel.setParameterStrings("");
//
//
//        return sideMenusModel;
//    }

    public void refreshCatagories(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        if (gLobalSearchSelectedModelList != null && gLobalSearchSelectedModelList.size() > 0) {

            globalSearchResultSynchTask = new GlobalSearchResultSynchTask(gLobalSearchSelectedModelList, appUserModel, queryString);
            globalSearchResultSynchTask.globalSearchResultListner = this;

            globalSearchResultSynchTask.execute();

            //            for (int i = 0; i < gLobalSearchSelectedModelList.size(); i++) {
//                String paramsString = urlStr + "pageIndex=1&pageSize=10&searchStr=" + queryString +
//                        "&source=0&type=0&fType=&fValue=&sortBy=PublishedDate&sortType=desc&keywords=&ComponentID=225&ComponentInsID=4021&UserID=" + appUserModel.getUserIDValue() +
//                        "&SiteID=" + appUserModel.getSiteIDValue() +
//                        "&OrgUnitID=" + appUserModel.getSiteIDValue() +
//                        "&Locale=en-us&AuthorID=-1&groupBy=PublishedDate" +
//                        "&objComponentList=" + gLobalSearchSelectedModelList.get(i).componentID + "&intComponentSiteID=" + gLobalSearchSelectedModelList.get(i).siteID;
//
//
//                final int finalI = i;
//                vollyService.getSearchResults(new DataCallback() {
//                    @Override
//                    public void onSuccess(JSONObject result) {
//                        Log.d(TAG, finalI + "onSuccess: " + result);
//                        if (gLobalSearchSelectedModelList.size() == finalI) {
//                            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
//                            Log.d(TAG, "onSuccess: loop is completed ");
//                        }
//                    }
//                }, paramsString);
//
//            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.globalsearchmenu, menu);
        MenuItem item_search = menu.findItem(R.id.globalsearch);

        item_search.setVisible(false);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();

            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint("Search..");
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getAppHeaderTextColor()));
            txtSearch.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(GlobalSearchResultsActivity.this, "Queried " + query, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {


                    return true;
                }
            });

        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Log.d("DEBUG", "onOptionsItemSelected: ");
                finish();
                return true;
            case R.id.globalsearch:
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
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");


        if (requestCode == DETAIL_CLOSE_CODE && resultCode == RESULT_OK) {
            boolean refresh = data.getBooleanExtra("REFRESH", false);
            if (refresh) {
                refreshCatagories(true);
            }
        }

    }

    @Override
    public void loopCompleted(List<GlobalSearchResultModelNew> globalSearchResultModelList, String completed) {
        svProgressHUD.dismiss();
        Log.d(TAG, "loopCompleted: " + globalSearchResultModelList.size());
        expandableListDetail = fetchCategoriesData(globalSearchResultModelList);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        searchAdapter.refreshList(expandableListTitle, expandableListDetail);
        if (expandableListDetail != null && expandableListDetail.size() > 0) {
            for (int i = 0; i < expandableListDetail.size(); i++)
                chxListview.expandGroup(i);
        }

        if (expandableListDetail.size() == 0) {
            nodataLabel.setText(getResources().getString(R.string.no_data));
        }
    }

    public void globalSearchContextMenu(final View v, final int position, ImageButton btnselected, final GlobalSearchResultModelNew globalSearchResultModel) {
        db = new DatabaseHandler(v.getContext());
//        SideMenusModel sideMenusModel = db.getSideMenuModelForGlobalSearch(globalSearchResultModel.siteid, globalSearchResultModel.contextMenuId);

        final SideMenusModel sideMenusModel = convertGlobalModelToSideMenuModel(globalSearchResultModel);

        String typeMore = "0";

        if (sideMenusModel.isDataFound() && !sideMenusModel.contextMenuId.equalsIgnoreCase("0")) {

            typeMore = sideMenusModel.getContextMenuId();

        } else {
//            Toast.makeText(v.getContext(), " Menu Not configured ", Toast.LENGTH_SHORT).show();
//            return;
            typeMore = "" + globalSearchResultModel.contextMenuId;
        }

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.global_contextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        // Catalog
        menu.getItem(0).setVisible(false);//view
        menu.getItem(1).setVisible(false);//buy
        menu.getItem(2).setVisible(false);//join

        // event catalog
        menu.getItem(3).setVisible(false);// related content
        menu.getItem(4).setVisible(false);// add
        menu.getItem(5).setVisible(false);//Enroll
        menu.getItem(6).setVisible(false);//Detail
        menu.getItem(12).setVisible(false);//Cancel

        // My Connection
        menu.getItem(7).setVisible(false); // add to my connection
        menu.getItem(8).setVisible(false); // view profile

        //Discussion forum
        menu.getItem(9).setVisible(false);// go to topic
        menu.getItem(10).setVisible(false);// go to forum

        //AskExpert
        menu.getItem(11).setVisible(false);// go to question
        menu.getItem(13).setVisible(false);// go to response

        switch (typeMore) {
            case "1":
                // mylearning
                menu.getItem(0).setVisible(true);//view
                menu.getItem(2).setVisible(false);//join
                menu.getItem(6).setVisible(true);//Detail
                break;
            case "2":
                //catalog
                menu.getItem(6).setVisible(true);//Detail
                if (globalSearchResultModel.isaddedtomylearning == 0) {
                    if (globalSearchResultModel.viewtype == 1 || globalSearchResultModel.viewtype == 2) {
                        menu.getItem(4).setVisible(true);// add
                    } else {
                        if (globalSearchResultModel.viewtype == 3) {
                            menu.getItem(1).setVisible(true);//buy
                        }
                    }
                } else {
                    menu.getItem(0).setVisible(true);//view
                }
                break;
            case "8":
                // events
                if (globalSearchResultModel.relatedconentcount > 0)
                    menu.getItem(3).setVisible(true);// related content
                if (globalSearchResultModel.isaddedtomylearning == 0) {
                    menu.getItem(3).setVisible(false);
                    if (returnEventCompleted(globalSearchResultModel.eventenddatetime) && uiSettingsModel.isAllowExpiredEventsSubscription()) {
                        menu.getItem(5).setVisible(true);//Enroll

                    } else {
                        menu.getItem(5).setVisible(false);
                    }
                    if (globalSearchResultModel.viewtype == 1 || globalSearchResultModel.viewtype == 2) {
                        if (!returnEventCompleted(globalSearchResultModel.eventstartdatetime)) {
                            menu.getItem(5).setVisible(true);//Enroll
                            menu.getItem(3).setVisible(false);
                        } else {
                            if (uiSettingsModel.isAllowExpiredEventsSubscription()) {
                                menu.getItem(5).setVisible(true);//Enroll
                                menu.getItem(3).setVisible(false);

                            }
                        }
                    } else {
                        if (!returnEventCompleted(globalSearchResultModel.eventstartdatetime)) {
                            menu.getItem(12).setVisible(true);
                            if (globalSearchResultModel.viewtype == 3) {
                                menu.getItem(1).setVisible(true);//buy
                            }
                        }

                    }
                }
                    menu.getItem(6).setVisible(true);//Detail
                break;
            case "4":
                //Discussion forum
                if (globalSearchResultModel.objecttypeid == 652) {
                    menu.getItem(9).setVisible(true);// go to forum
                }
                if (globalSearchResultModel.objecttypeid == 17) {
                    menu.getItem(10).setVisible(true);// go to topic
                }
                break;
            case "5":
                // askexpert

                if (globalSearchResultModel.objecttypeid == 653) {
                    menu.getItem(11).setVisible(true);// go to question
                }
                if (globalSearchResultModel.objecttypeid == 654) {
                    menu.getItem(13).setVisible(true);// go to response
                }

                break;
            case "10":
                // people listing
//                menu.getItem(7).setVisible(true); // add to my connection
                menu.getItem(8).setVisible(true); // view profile
                break;
        }

        popup.show();//showing popup menu
        final MyLearningModel myLearningDetalData = convertGlobalModelToMylearningModel(globalSearchResultModel, appUserModel);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ctx_add:
                        addToMyLearningCheckUser(myLearningDetalData, false, globalSearchResultModel);
                        break;
                    case R.id.ctx_view:
                        launchCourseForGlobalSearch(myLearningDetalData, GlobalSearchResultsActivity.this);
                        break;
                    case R.id.ctx_join:
                        Toast.makeText(v.getContext(), "Clicked here", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.ctx_buy:
                        Toast.makeText(GlobalSearchResultsActivity.this, "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.ctx_relatedcontent:
                        relatedContentView(myLearningDetalData, GlobalSearchResultsActivity.this);
                        break;
                    case R.id.ctx_enroll:
                        enrollEventCall(myLearningDetalData, globalSearchResultModel);
                        break;
                    case R.id.ctx_cancel:
                        cancelEnrollment(myLearningDetalData);
                        break;
                    case R.id.ctx_detail:
                        gotoDetailView(globalSearchResultModel);
                        break;
                    case R.id.ctx_addtomy:
                        Toast.makeText(v.getContext(), "Clicked here", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.ctx_profile:
                        gotoProfile(globalSearchResultModel);
                        break;
                    case R.id.ctx_disc:
//                        gotoDiscForum(globalSearchResultModel);
                        getForumList(sideMenusModel, globalSearchResultModel, false);
                        break;
                    case R.id.ctx_topic:
//                        gotoDiscTopic(globalSearchResultModel);
                        getForumList(sideMenusModel, globalSearchResultModel, true);
                        break;
                    case R.id.ctx_answer:
//                        gotoQuestionDetail(globalSearchResultModel, true);
                        getUserQuestionDetails(globalSearchResultModel, sideMenusModel, false);
//                        selectedFragment(globalSearchResultModel, true, false);
                        break;
                    case R.id.ctx_comments:
//                        gotoAnswerDetail(globalSearchResultModel, true);
//                        selectedFragment(globalSearchResultModel, true, true);
                        getUserQuestionDetails(globalSearchResultModel, sideMenusModel, true);
                        break;
                }
                return true;

            }
        });

    }

    public void gotoProfile(GlobalSearchResultModelNew resultModel) {
        //  http://stmciapi.instancysoft.com/api//MobileLMS/GetForumComments?SiteID=374&ForumID=155&TopicID=45d3e3a3-da4b-4d80-bc40-6d5b7ecf7928

        PeopleListingModel peopleListingModel = convertGlobalToPeopleListing(resultModel);

        Intent intentDetail = new Intent(GlobalSearchResultsActivity.this, PeopleListingProfile.class);
        intentDetail.putExtra("peopleListingModel", peopleListingModel);
        intentDetail.putExtra("ISGLOBALSEARCH", true);
        startActivity(intentDetail);

    }

    public void gotoAnswerDetail(GlobalSearchResultModelNew resultModel, boolean nextLevel) {

        AskExpertQuestionModelDg askExpertQuestionModel = convertGlobalToQuestionModel(resultModel);

        Intent intentDetail = new Intent(GlobalSearchResultsActivity.this, AskExpertsCommentsActivity.class);
        intentDetail.putExtra("AskExpertQuestionModelDg", askExpertQuestionModel);
        intentDetail.putExtra("ISGLOBALSEARCH", true);
//        intentDetail.putExtra("nextLevel", nextLevel);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);


    }

    public void getQuestionDetails(GlobalSearchResultModelNew globalSearchResultModelNew, SideMenusModel sideMenusModel) {

        //      public AskTheExpertDTO GetUserQuestionDetails(int intUserId, int intSiteID, int intQuestionID, int ComponentInsID, int ComponentID)

        String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetUserQuestionDetails?intUserId=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getSiteIDValue() + "&intQuestionID=" + globalSearchResultModelNew.folderid + "&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&ComponentID=" + sideMenusModel.getComponentId();

        vollyService.getStringResponseVolley("GetUserQuestionDetails", parmStringUrl, appUserModel.getAuthHeaders());

    }


    public void gotoQuestionDetail(AskExpertQuestionModelDg askExpertQuestionModel, boolean nextLevel, SideMenusModel sideMenusModel) {


        Intent intentDetail = new Intent(GlobalSearchResultsActivity.this, AskExpertsAnswersActivity.class);
        intentDetail.putExtra("AskExpertQuestionModelDg", askExpertQuestionModel);
        intentDetail.putExtra("ISGLOBALSEARCH", true);
        intentDetail.putExtra("sidemenumodel", sideMenusModel);

        intentDetail.putExtra("nextLevel", nextLevel);
        startActivity(intentDetail);

    }


    public void gotoDiscTopic(GlobalSearchResultModelNew resultModel) {
        //  http://stmciapi.instancysoft.com/api//MobileLMS/GetForumComments?SiteID=374&ForumID=155&TopicID=45d3e3a3-da4b-4d80-bc40-6d5b7ecf7928

        DiscussionTopicModelDg topicModel = convertGlobalToDiscussionTopicModel(resultModel);

        Intent intentDetail = new Intent(GlobalSearchResultsActivity.this, DiscussionCommentsActivity.class);
        intentDetail.putExtra("topicModel", topicModel);
        intentDetail.putExtra("ISGLOBALSEARCH", true);
        startActivity(intentDetail);
        //
    }

    public void gotoDiscForum(boolean nextLevel, DiscussionForumModelDg discussionForumModel, String topicID) {
// http://stmciapi.instancysoft.com/api//MobileLMS/GetForumTopics?ForumID=155


        Intent intentDetail = new Intent(GlobalSearchResultsActivity.this, DiscussionTopicActivity.class);
        intentDetail.putExtra("forumModel", discussionForumModel);
        intentDetail.putExtra("NOTIFICATION", false);
        intentDetail.putExtra("ISGLOBALSEARCH", true);
        intentDetail.putExtra("nextLevel", nextLevel);
        intentDetail.putExtra("TOPICID", topicID);

        startActivity(intentDetail);
    }

    public PeopleListingModel convertGlobalToPeopleListing(GlobalSearchResultModelNew resultModel) {
        PeopleListingModel peopleListingModel = new PeopleListingModel();

        if (resultModel == null) {

            return null;
        }


        peopleListingModel.connectionUserID = Integer.parseInt(resultModel.folderid);
        // ObjectID

        peopleListingModel.userDisplayname = resultModel.name;
//
//        peopleListingModel.userID = jsonMyLearningColumnObj.get("ObjectID").toString();
//
//
//        peopleListingModel.jobTitle = jsonMyLearningColumnObj.get("JobTitle").toString();
//
//
//        peopleListingModel.mainOfficeAddress = jsonMyLearningColumnObj.getString("MainOfficeAddress");
//
//
//        peopleListingModel.memberProfileImage = jsonMyLearningColumnObj.getString("MemberProfileImage");
//
//        peopleListingModel.connectionState = jsonMyLearningColumnObj.getString("connectionstate");
//
//
//        peopleListingModel.connectionStateAccept = jsonMyLearningColumnObj.get("connectionstateAccept").toString();


        peopleListingModel.addToMyConnectionAction = false;

        peopleListingModel.userID = resultModel.folderid;
        peopleListingModel.tabID = "All";
        peopleListingModel.siteID = appUserModel.getSiteIDValue();
        peopleListingModel.mainSiteUserID = appUserModel.getUserIDValue();
        peopleListingModel.siteURL = appUserModel.getSiteURL();


        return peopleListingModel;
    }


    public AskExpertQuestionModelDg convertGlobalToQuestionModel(GlobalSearchResultModelNew resultModel) {
        AskExpertQuestionModelDg askExpertQuestionModel = new AskExpertQuestionModelDg();

        if (resultModel == null) {

            return null;
        }

        askExpertQuestionModel.siteID = resultModel.siteid;

        askExpertQuestionModel.userID = Integer.parseInt(resultModel.userID);

        askExpertQuestionModel.userName = resultModel.authordisplayname;

        askExpertQuestionModel.userQuestion = resultModel.name;

        askExpertQuestionModel.questionID = Integer.parseInt(resultModel.folderid);

        askExpertQuestionModel.postedDate = resultModel.publisheddate;

        askExpertQuestionModel.createdDate = resultModel.createddate;


        askExpertQuestionModel.userQuestionDescription = resultModel.longdescription;

        askExpertQuestionModel.questionCategories = resultModel.longdescription;


        return askExpertQuestionModel;
    }

    public DiscussionForumModelDg convertGlobalToDiscussionForum(GlobalSearchResultModelNew resultModel) {
        DiscussionForumModelDg discussionForumModel = new DiscussionForumModelDg();

        if (resultModel == null) {

            return null;
        }

        discussionForumModel.siteID = resultModel.siteid;

        discussionForumModel.name = resultModel.name;

        discussionForumModel.createdDate = resultModel.createddate;

        discussionForumModel.author = resultModel.authordisplayname;

        discussionForumModel.noOfTopics = 0;

        discussionForumModel.totalPosts = 0;

        discussionForumModel.existing = 0;

        discussionForumModel.description = resultModel.longdescription;

        discussionForumModel.isPrivate = false;

        discussionForumModel.active = false;

        discussionForumModel.createdUserID = resultModel.createduserid;

        discussionForumModel.parentForumID = 0;

        discussionForumModel.displayOrder = 0;

        discussionForumModel.requiresSubscription = false;

        discussionForumModel.createNewTopic = false;

        discussionForumModel.attachFile = false;

        discussionForumModel.likePosts = false;

        discussionForumModel.sendEmail = false;

        discussionForumModel.moderation = false;

        discussionForumModel.dfProfileImage = "";

        discussionForumModel.forumID = Integer.parseInt(resultModel.folderid);

        discussionForumModel.name = resultModel.name;

        return discussionForumModel;
    }


    public DiscussionTopicModelDg convertGlobalToDiscussionTopicModel(GlobalSearchResultModelNew resultModel) {
        DiscussionTopicModelDg discussionTopicModel = new DiscussionTopicModelDg();

        if (resultModel == null) {

            return null;
        }

        discussionTopicModel.siteId = resultModel.siteid;

        discussionTopicModel.name = resultModel.name;

        discussionTopicModel.createdDate = resultModel.createddate;

        discussionTopicModel.createdUserID = resultModel.createduserid;

        discussionTopicModel.uploadedImageName = "";

        discussionTopicModel.forumId = Integer.parseInt(resultModel.folderid);

        discussionTopicModel.contentID = resultModel.contentid;

        discussionTopicModel.longDescription = resultModel.longdescription;

        discussionTopicModel.latestReplyBy = resultModel.authordisplayname;

        return discussionTopicModel;
    }


    public void cancelEnrollment(final MyLearningModel myLearningDetalData) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchResultsActivity.this);
        builder.setMessage(GlobalSearchResultsActivity.this.getResources().getString(R.string.canceleventmessage)).setTitle(GlobalSearchResultsActivity.this.getResources().getString(R.string.eventalert))
                .setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
                dialog.dismiss();
                cancelEnrollmentMethod(myLearningDetalData);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    public void cancelEnrollmentMethod(final MyLearningModel eventModel) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/CancelEnrolledEvent?EventContentId="
                + eventModel.getContentID() + "&UserID=" + eventModel.getUserID() + "&SiteID=" + appUserModel.getSiteIDValue();

        Log.d(TAG, "main login : " + urlStr);

        urlStr = urlStr.replaceAll(" ", "%20");

        StringRequest jsonObjectRequest = new StringRequest(urlStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        svProgressHUD.dismiss();
                        Log.d("Response: ", " " + response);

                        if (response.contains("true")) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchResultsActivity.this);
                            builder.setMessage(GlobalSearchResultsActivity.this.getString(R.string.event_cancelled))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            dialog.dismiss();
                                            // remove event from android calander

                                            db.ejectEventsFromDownloadData(eventModel);
                                            db.updateEventAddedToMyLearningInEventCatalog(eventModel, 0);
//                                            injectFromDbtoModel(true);
                                            refreshCatagories(true);
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
                        svProgressHUD.dismiss();
//                        refreshCatagories(true);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

//        VolleySingleton.getInstance(GlobalSearchResultsActivity.this).addToRequestQueue(jsonObjectRequest);

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(jsonObjectRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    public void enrollEventCall(MyLearningModel learningModel, GlobalSearchResultModelNew globalSearchResultModelNew) {

        if (uiSettingsModel.isAllowExpiredEventsSubscription() && returnEventCompleted(learningModel.getEventstartUtcTime())) {

//                            addExpiredEventToMyLearning(myLearningDetalData, position);
            try {
                addExpiryEvets(learningModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            addToMyLearningCheckUser(learningModel, false, globalSearchResultModelNew);
        }

    }

    public void addToMyLearningCheckUser(MyLearningModel myLearningDetalData, boolean isInApp, GlobalSearchResultModelNew globalSearchResultModelNew) {

        if (isNetworkConnectionAvailable(this, -1)) {

            if (myLearningDetalData.getUserID().equalsIgnoreCase("-1")) {

                checkUserLogin(myLearningDetalData, false, false, globalSearchResultModelNew);

            } else {

                if (isInApp) {
//                    inAppActivityCall(myLearningDetalData);
                } else {
                    addToMyLearning(myLearningDetalData, false, false);
                }

            }
        }
    }

    public void checkUserLogin(final MyLearningModel learningModel, final boolean isInApp, final boolean seeAll, final GlobalSearchResultModelNew globalSearchResultModelNew) {

        final String userName = preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID);

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                + userName + "&Password=" + learningModel.getPassword() + "&MobileSiteURL="
                + appUserModel.getSiteURL() + "&DownloadContent=&SiteID=" + learningModel.getSiteID();


        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "inside catalog login : " + urlStr);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        svProgressHUD.dismiss();
                        if (jsonObj.has("faileduserlogin")) {

                            JSONArray userloginAry = null;
                            try {
                                userloginAry = jsonObj
                                        .getJSONArray("faileduserlogin");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (userloginAry.length() > 0) {

                                String response = null;
                                try {
                                    response = userloginAry
                                            .getJSONObject(0)
                                            .get("userstatus")
                                            .toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (response.contains("Login Failed")) {
                                    Toast.makeText(GlobalSearchResultsActivity.this,
                                            "Authentication Failed. Contact site admin",
                                            Toast.LENGTH_LONG)
                                            .show();

                                }
                                if (response.contains("Pending Registration")) {

                                    Toast.makeText(GlobalSearchResultsActivity.this, "Please be patient while awaiting approval. You will receive an email once your profile is approved.",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        } else if (jsonObj.has("successfulluserlogin")) {

                            try {
                                JSONArray loginResponseAry = jsonObj.getJSONArray("successfulluserlogin");
                                if (loginResponseAry.length() != 0) {
                                    JSONObject jsonobj = loginResponseAry.getJSONObject(0);

                                    String userIdresponse = loginResponseAry
                                            .getJSONObject(0)
                                            .get("userid").toString();

                                    if (userIdresponse.length() != 0) {

                                        if (!seeAll) {

                                            if (!isInApp) {
                                                learningModel.setUserID(userIdresponse);
                                                addToMyLearning(learningModel, true, true);
                                            } else {
//                                            inAppActivityCall(learningModel);
                                            }
                                        } else {

                                            SideMenusModel sideModel = convertGlobalModelToSideMenuModel(globalSearchResultModelNew);

                                            selectedFragmentForSubsite(sideModel);

                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
//                        svProgressHUD.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void addExpiryEvets(MyLearningModel catalogModel) throws JSONException {

        JSONObject parameters = new JSONObject();

        //mandatory
        parameters.put("SelectedContent", catalogModel.getContentID());
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("SiteID", catalogModel.getSiteID());
        parameters.put("OrgUnitID", catalogModel.getSiteID());
        parameters.put("Locale", "en-us");

        String parameterString = parameters.toString();
        boolean isSubscribed = db.isSubscribedContent(catalogModel);
        if (isSubscribed) {
            Toast toast = Toast.makeText(
                    GlobalSearchResultsActivity.this,
                    getString(R.string.cat_add_already),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            sendExpiryEventData(parameterString, catalogModel);
        }
    }

    public void sendExpiryEventData(final String postData, final MyLearningModel catalogModel) {
        String apiURL = "";

        apiURL = appUserModel.getWebAPIUrl() + "/Catalog/AddExpiredContentToMyLearning";

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.d("CMP", "onResponse: " + s);

                if (s != null && s.length() > 0) {
                    try {

                        if (s.contains("true")) {
// ------------------------- old code here
                            refreshCatagories(true);
                            getMobileGetMobileContentMetaData(catalogModel, false);

//                            final AlertDialog.Builder builder = new AlertDialog.Builder(MyLearningDetail_Activity.this);
//                            builder.setMessage(getString(R.string.event_add_success))
//                                    .setCancelable(false)
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            //do things
//                                            dialog.dismiss();
//                                            // add event to android calander
////                                            addEventToAndroidDevice(catalogModel);
//                                            db.updateEventAddedToMyLearningInEventCatalog(catalogModel, 1);
//
//                                        }
//                                    });
//                            AlertDialog alert = builder.create();
//                            alert.show();

                        } else {
                            Toast toast = Toast.makeText(
                                    GlobalSearchResultsActivity.this, "Unable to process request",
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

//--------------------------- old code end here

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(GlobalSearchResultsActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();

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
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void gotoDetailView(GlobalSearchResultModelNew globalSearchResultModelNew) {
        MyLearningModel myLearningDetalData = convertGlobalModelToMylearningModel(globalSearchResultModelNew, appUserModel);

        Intent intentDetail = new Intent(GlobalSearchResultsActivity.this, MyLearningDetail_Activity.class);
        intentDetail.putExtra("IFROMCATALOG", false);
        intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
        intentDetail.putExtra("IFROMCATALOG", true);
        startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);

    }

    public void getMobileGetMobileContentMetaData(final MyLearningModel learningModel, final boolean isJoinedCommunity) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/MobileGetMobileContentMetaData?SiteURL="
                + learningModel.getSiteURL() + "&ContentID=" + learningModel.getContentID() + "&userid="
                + appUserModel.getUserIDValue() + "&DelivoryMode=1";

        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "getMobileGetMobileContentMetaData : " + urlStr);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {

                        Log.d(TAG, "getMobileGetMobileContentMetaData response : " + jsonObj);
                        if (jsonObj.length() != 0) {
                            boolean isInserted = false;

                            String succesMessage = "Content Added to My Learning";
                            if (isJoinedCommunity) {
                                succesMessage = "This content item has been added to My Learning page. You have successfully joined the Learning Community: " + learningModel.getSiteName();
                            }

                            if (learningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                                succesMessage = getResources().getString(R.string.event_add_success);
//                                            db.updateEventAddedToMyLearningInEventCatalog(myLearningModel, 1);

                            }
                            final AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchResultsActivity.this);
                            builder.setMessage(succesMessage)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
//                                            refreshCatagories(true);
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
//                        svProgressHUD.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void addToMyLearning(final MyLearningModel myLearningDetalData, final boolean isAutoAdd, final boolean isJoinedCommunity) {

        if (isNetworkConnectionAvailable(GlobalSearchResultsActivity.this, -1)) {
//            boolean isSubscribed = db.isSubscribedContent(myLearningDetalData);
            if (false) {
                Toast toast = Toast.makeText(
                        GlobalSearchResultsActivity.this,
                        GlobalSearchResultsActivity.this.getString(R.string.cat_add_already),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                String requestURL = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileAddtoMyCatalog?"
                        + "UserID=" + myLearningDetalData.getUserID() + "&SiteURL=" + myLearningDetalData.getSiteURL()
                        + "&ContentID=" + myLearningDetalData.getContentID() + "&SiteID=" + myLearningDetalData.getSiteID();

                requestURL = requestURL.replaceAll(" ", "%20");
                Log.d(TAG, "inside catalog login : " + requestURL);

                StringRequest strReq = new StringRequest(Request.Method.GET,
                        requestURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "add to mylearning data " + response.toString());
                        if (response.equalsIgnoreCase("true")) {
//                            catalogModelsList.get(position).setAddedToMylearning(1);

//                            db.updateContenToCatalog(catalogModelsList.get(position));
//                            catalogAdapter.notifyDataSetChanged();
//                            getMobileGetMobileContentMetaData(myLearningDetalData, position);
                            if (!isAutoAdd) {
                                String succesMessage = "Content Added to My Learning";
                                if (isJoinedCommunity) {
                                    succesMessage = "This content item has been added to My Learning page. You have successfully joined the Learning Community: " + myLearningDetalData.getSiteName();
                                }
                                final AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchResultsActivity.this);
                                builder.setMessage(succesMessage)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            refreshCatagories(true);
                        } else {
                            Toast toast = Toast.makeText(GlobalSearchResultsActivity.
                                            this, "Unable to process request",
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast toast = Toast.makeText(GlobalSearchResultsActivity.
                                        this, "Unable to process request",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
//                        refreshCatagories(true);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        final Map<String, String> headers = new HashMap<>();
                        String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                        headers.put("Authorization", "Basic " + base64EncodedCredentials);
                        return headers;
                    }
                };
                ;
                VolleySingleton.getInstance(this).addToRequestQueue(strReq);

            }
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
                if (requestType.equalsIgnoreCase("GetUserQuestionDetails")) {
                    Toast.makeText(GlobalSearchResultsActivity.this, "Unable to fetch Question Details", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);

                if (requestType.equalsIgnoreCase("GetUserQuestionDetails")) {
                    if (response != null) {
                        try {
                            AskExpertQuestionModelDg askExpertQuestionModelDg = getQuestionModel(response);
                            if (askExpertQuestionModelDg != null) {
//                                gotoQuestionDetail(askExpertQuestionModelDg, false);
                            }

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

    public AskExpertQuestionModelDg getQuestionModel(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        AskExpertQuestionModelDg askExpertsQuestionModel = new AskExpertQuestionModelDg();
        JSONArray jsonTableAry = jsonObject.getJSONArray("QuestionList");

        // for deleting records in table for respective table


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            askExpertsQuestionModel.questionID = jsonMyLearningColumnObj.optInt("QuestionID");
            askExpertsQuestionModel.userID = jsonMyLearningColumnObj.optInt("UserID");
            askExpertsQuestionModel.userName = jsonMyLearningColumnObj.optString("UserName");

            askExpertsQuestionModel.userQuestion = fromHtmlToString(jsonMyLearningColumnObj.get("UserQuestion").toString());

            askExpertsQuestionModel.postedDate = jsonMyLearningColumnObj.optString("PostedDate");
            askExpertsQuestionModel.createdDate = jsonMyLearningColumnObj.optString("CreatedDate");
            askExpertsQuestionModel.totalAnswers = jsonMyLearningColumnObj.optInt("Answers");
            askExpertsQuestionModel.questionCategories = jsonMyLearningColumnObj.optString("QuestionCategories");

            askExpertsQuestionModel.userQuestionDescription = fromHtmlToString(jsonMyLearningColumnObj.get("UserQuestionDescription").toString());

            askExpertsQuestionModel.userQuestionImage = jsonMyLearningColumnObj.optString("UserQuestionImage");
            askExpertsQuestionModel.lastActivatedDate = jsonMyLearningColumnObj.optString("LastActivatedDate");
            askExpertsQuestionModel.totalViews = jsonMyLearningColumnObj.optInt("Views");
            askExpertsQuestionModel.objectID = jsonMyLearningColumnObj.optString("ObjectID");
            askExpertsQuestionModel.userImage = jsonMyLearningColumnObj.optString("UserImage");
            askExpertsQuestionModel.actionsLink = jsonMyLearningColumnObj.optString("ActionsLink");
            askExpertsQuestionModel.userQuestionImagePath = jsonMyLearningColumnObj.optString("UserQuestionImagePath");
            askExpertsQuestionModel.answerBtnWithLink = jsonMyLearningColumnObj.optString("AnswerBtnWithLink");

        }
        return askExpertsQuestionModel;
    }

    public void getUserQuestionDetails(GlobalSearchResultModelNew globalSearchResultModelNew, final SideMenusModel sideMenusModel, final boolean newxtLevel) {
        //      public AskTheExpertDTO GetUserQuestionDetails(int intUserId, int intSiteID, int intQuestionID, int ComponentInsID, int ComponentID)

        String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetUserQuestionDetails?intUserId=" + appUserModel.getUserIDValue() + "&intSiteID=" + appUserModel.getSiteIDValue() + "&intQuestionID=" + globalSearchResultModelNew.folderid + "&ComponentInsID=" + sideMenusModel.getRepositoryId() + "&ComponentID=" + sideMenusModel.getComponentId();


        Log.d(TAG, "main login : " + parmStringUrl);

        parmStringUrl = parmStringUrl.replaceAll(" ", "%20");

        StringRequest stringRequest = new StringRequest(parmStringUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        svProgressHUD.dismiss();
                        Log.d("Response: ", " " + response);

                        if (response != null) {
                            try {
                                AskExpertQuestionModelDg askExpertQuestionModelDg = getQuestionModel(response);
                                if (askExpertQuestionModelDg != null) {
                                    gotoQuestionDetail(askExpertQuestionModelDg, newxtLevel, sideMenusModel);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {


                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
                        Toast.makeText(GlobalSearchResultsActivity.this, "Unable to fetch Question Details", Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        VolleySingleton.getInstance(GlobalSearchResultsActivity.this).addToRequestQueue(stringRequest);
    }


    public void getForumList(SideMenusModel sideMenusModel, GlobalSearchResultModelNew globalSearchResultModelNew, boolean nextLevel) {

        //    http://digimedicaapi.instancysoft.com/api/DiscussionForums/GetCourseDiscussionContentID?strContentID=&intForumID=7&intUserID=1&intSiteID=374&strLocale=en-us

        if (nextLevel) {
            queryString = "";
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/GetForumList";

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("intUserID", appUserModel.getUserIDValue());
            parameters.put("intSiteID", appUserModel.getSiteIDValue());
            parameters.put("strLocale", "en-us");
            parameters.put("strSearchText", queryString);
            parameters.put("intCompID", sideMenusModel.getComponentId());
            parameters.put("intCompInsID", sideMenusModel.getRepositoryId());
            parameters.put("intShowPrivateForums", "0");
            parameters.put("strSortCondition", "CreatedDate%20Desc");
            parameters.put("sortby", "");
            parameters.put("sorttype", "");
            parameters.put("pageIndex", "1");
            parameters.put("pageSize", 100);
            parameters.put("RecordsCount", 0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        getStringResponseFromPostMethod(parameterString, urlStr, globalSearchResultModelNew, nextLevel);
    }


    public void getStringResponseFromPostMethod(final String postData, String apiURL, final GlobalSearchResultModelNew globalSearchResultModelNew, final boolean nextLevel) {

        byte[] encrpt = new byte[0];
        try {
            encrpt = postData.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final byte[] finalEncrpt = encrpt;
        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String _response) {

                Log.d(TAG, "onResponse: " + _response);


//                gotoDiscForum(globalSearchResultModelNew, nextLevel);
                try {

                    int forumId = Integer.parseInt(globalSearchResultModelNew.folderid);

                    DiscussionForumModelDg forumModelDg = getForumModel(_response, forumId);

                    if (forumModelDg != null) {
                        gotoDiscForum(nextLevel, forumModelDg, globalSearchResultModelNew.contentid);
                    } else {
                        Toast.makeText(GlobalSearchResultsActivity.this, "Unable to fetch forum details", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return finalEncrpt;
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

        VolleySingleton.getInstance(this).addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public DiscussionForumModelDg getForumModel(String responseStr, int forumId) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("forumList");

        // for deleting records in table for respective table

        DiscussionForumModelDg discussionForumModel = null;

        for (int i = 0; i < jsonTableAry.length(); i++) {

            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            if (forumId == jsonMyLearningColumnObj.optInt("ForumID")) {
                discussionForumModel = new DiscussionForumModelDg();
                discussionForumModel.forumID = jsonMyLearningColumnObj.optInt("ForumID");
                discussionForumModel.name = jsonMyLearningColumnObj.optString("Name");

                Spanned result = fromHtml(jsonMyLearningColumnObj.optString("Description"));
                discussionForumModel.description = result.toString();

                discussionForumModel.parentForumID = jsonMyLearningColumnObj.optInt("ParentForumID");
                discussionForumModel.displayOrder = jsonMyLearningColumnObj.optInt("DisplayOrder");

                String formattedDate = formatDate(jsonMyLearningColumnObj.optString("CreatedDate").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                Log.d(TAG, "injectEventCatalog: " + formattedDate);
                discussionForumModel.createdDate = formattedDate;

                discussionForumModel.siteID = jsonMyLearningColumnObj.optInt("SiteID");
                discussionForumModel.createdUserID = jsonMyLearningColumnObj.optInt("CreatedUserID");

                discussionForumModel.active = jsonMyLearningColumnObj.optBoolean("Active");
                discussionForumModel.requiresSubscription = jsonMyLearningColumnObj.optBoolean("RequiresSubscription");
                discussionForumModel.createNewTopic = jsonMyLearningColumnObj.optBoolean("CreateNewTopic");
                discussionForumModel.attachFile = jsonMyLearningColumnObj.optBoolean("AttachFile");
                discussionForumModel.likePosts = jsonMyLearningColumnObj.optBoolean("LikePosts");
                discussionForumModel.sendEmail = jsonMyLearningColumnObj.optBoolean("SendEmail");
                discussionForumModel.moderation = jsonMyLearningColumnObj.optBoolean("Moderation");
                discussionForumModel.isPrivate = jsonMyLearningColumnObj.optBoolean("IsPrivate");

                String authorName = jsonMyLearningColumnObj.optString("Author");
                if (isValidString(authorName)) {
                    discussionForumModel.author = authorName;
                }

                discussionForumModel.noOfTopics = jsonMyLearningColumnObj.optInt("NoOfTopics");
                discussionForumModel.totalPosts = jsonMyLearningColumnObj.optInt("TotalPosts");
                discussionForumModel.existing = jsonMyLearningColumnObj.optInt("Existing");
                discussionForumModel.totalLikes = jsonMyLearningColumnObj.optInt("TotalLikes");
                discussionForumModel.dfProfileImage = jsonMyLearningColumnObj.optString("DFProfileImage");
                discussionForumModel.dfUpdateTime = jsonMyLearningColumnObj.optString("DFUpdateTime");
                discussionForumModel.dfChangeUpdateTime = jsonMyLearningColumnObj.optString("DFChangeUpdateTime");
                discussionForumModel.forumThumbnailPath = jsonMyLearningColumnObj.optString("ForumThumbnailPath");
                discussionForumModel.descriptionWithLimit = jsonMyLearningColumnObj.optString("DescriptionWithLimit");
                discussionForumModel.moderatorID = jsonMyLearningColumnObj.optInt("ModeratorID");
                discussionForumModel.updatedAuthor = jsonMyLearningColumnObj.optString("UpdatedAuthor");

                String updatedDate = formatDate(jsonMyLearningColumnObj.optString("UpdatedDate").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");


                discussionForumModel.updatedDate = updatedDate;

                discussionForumModel.moderatorName = jsonMyLearningColumnObj.optString("ModeratorName");

                String moderatorName = jsonMyLearningColumnObj.optString("ModeratorName");
                if (isValidString(moderatorName)) {
                    discussionForumModel.moderatorName = moderatorName;
                }

                discussionForumModel.allowShare = jsonMyLearningColumnObj.optBoolean("AllowShare");
                discussionForumModel.descriptionWithoutLimit = jsonMyLearningColumnObj.optString("DescriptionWithoutLimit");


            }


        }
        return discussionForumModel;
    }

}
