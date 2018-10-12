package com.instancy.instancylearning.progressreports;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.ProgressChartsModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.Reports_Activity;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mg.yra.lib.trackingring.DataEntry;
import mg.yra.lib.trackingring.DataSet;
import mg.yra.lib.trackingring.TrackingRingView;

import static android.app.Activity.RESULT_OK;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class ProgressReportfragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    String TAG = ProgressReportfragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    @BindView(R.id.swipeprogress)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_expandlist)
    ExpandableListView progressExpandList;
    ProgressReportAdapter progressReportAdapter;
    List<ProgressReportModel> progressReportModelList = null;
    List<ProgressChartsModel.ContentStatusModel> contentStatusModelList = null;
    List<ProgressChartsModel.ContentTypesModel> contentTypesModelList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    ResultListner resultListner = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    boolean isFromNotification = false;

    String contentIDFromNotification = "";
    String topicID = "";

    TextView txtContentScore;

    PieChart mChart, contentChart;

    TrackingRingView ringcharts;

    public ProgressReportfragment() {


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new DatabaseHandler(context);
        initVolleyCallback();
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        vollyService = new VollyService(resultCallback, context);

        sideMenusModel = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");

            isFromNotification = bundle.getBoolean("ISFROMNOTIFICATIONS");

            if (isFromNotification) {

                contentIDFromNotification = bundle.getString("TOPICID");
                topicID = bundle.getString("CONTENTID");
            }

        }


    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

//        String parmStringUrl = appUserModel.getWebAPIUrl() + "/ConsolidatedProgressReport/GetConsolidateRPT?aintSiteID=" + appUserModel.getSiteIDValue() + "&aintUserID=" + appUserModel.getUserIDValue() + "&astrLocale=en-us&aintComponentID=" + sideMenusModel.getComponentId() + "&aintCompInsID=" + sideMenusModel.getRepositoryId() + "&aintSelectedGroupValue=0";
//
//        vollyService.getStringResponseVolley("PRGLIST", parmStringUrl, appUserModel.getAuthHeaders());

        String parmStringUrl = "http://angular6api.instancysoft.com/api/MobileLMS/GetConsolidateRPT?aintSiteID=" + appUserModel.getSiteIDValue() + "&aintUserID=" + appUserModel.getUserIDValue() + "&astrLocale=en-us&aintComponentID=" + sideMenusModel.getComponentId() + "&aintCompInsID=" + sideMenusModel.getRepositoryId() + "&aintSelectedGroupValue=0";

        vollyService.getStringResponseVolley("PRGLIST", parmStringUrl, "A459QN8B57:jTV1fyibJgicZtGfZy7EMKOYk67I1GhvgJqgrH57");


//        W x H x D: 189 cm x 90 cm x 221 cm (6 ft 2 in x 2 ft 11 in x 7 ft 3 in) :
//       Length: 75 inch, Width: 72 inch, Thickness: 5 inch (6 ft 3 in x 6 ft x 5 in)

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("PRGLIST")) {
                    if (response != null) {

                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                svProgressHUD.dismiss();
                swipeRefreshLayout.setRefreshing(false);
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
                if (requestType.equalsIgnoreCase("PRGLIST")) {
                    if (response != null) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            progressReportModelList = generateProgressReport(jsonObject);
                            progressReportAdapter.refreshList(progressReportModelList);
                            generateContentAverageScore(jsonObject);
                            contentStatusModelList = generateContentStatusModelList(jsonObject);
                            contentTypesModelList = generateContentTypesModelList(jsonObject);
                            updateChartsData(progressReportModelList.size());
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

    public void updateChartsData(int contentCount) {

        // content count
        txtContentScore.setText("" + contentCount);

        // content status chart
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(false);
        mChart.setTransparentCircleColor(Color.WHITE);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setDrawSliceText(false);
        mChart.setUsePercentValues(false);
        mChart.setCenterTextColor(Color.WHITE);

        // add a selection listener
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        mChart.setEntryLabelColor(Color.BLACK);
        mChart.setEntryLabelTextSize(12f);
        ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();

        if (contentStatusModelList != null && contentStatusModelList.size() > 0) {
            for (int k = 0; k < contentStatusModelList.size(); k++) {
                yvalues.add(new PieEntry(contentStatusModelList.get(k).contentCount, contentStatusModelList.get(k).status));
            }
        }

        PieDataSet dataSet = new PieDataSet(yvalues, "");
        PieData data = new PieData(dataSet);
        // In Percentage term
        data.setValueFormatter(new PercentFormatter());
        // Default value

        mChart.setData(data);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        mChart.animateXY(1400, 1400);

        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart.setDrawCenterText(true);
        mChart.setData(data);
        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();

        // content types chart
        contentChart.getDescription().setEnabled(false);
        contentChart.setExtraOffsets(5, 10, 5, 5);
        contentChart.setDragDecelerationFrictionCoef(0.95f);
        contentChart.setDrawHoleEnabled(false);
        contentChart.setTransparentCircleColor(Color.WHITE);
        // enable rotation of the chart by touch
        contentChart.setRotationEnabled(true);
        contentChart.setHighlightPerTapEnabled(true);
        contentChart.setDrawSliceText(false);
        contentChart.setUsePercentValues(false);
        contentChart.setCenterTextColor(Color.BLACK);

        // add a selection listener
        Legend l1 = contentChart.getLegend();
        l1.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l1.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l1.setOrientation(Legend.LegendOrientation.VERTICAL);
        l1.setDrawInside(false);
        l1.setXEntrySpace(7f);
        l1.setYEntrySpace(0f);
        l1.setYOffset(0f);

        // entry label styling
        contentChart.setEntryLabelColor(Color.BLACK);
        contentChart.setEntryLabelTextSize(12f);
        ArrayList<PieEntry> xvalues = new ArrayList<PieEntry>();
        if (contentTypesModelList != null && contentTypesModelList.size() > 0) {
            for (int k = 0; k < contentTypesModelList.size(); k++) {
                xvalues.add(new PieEntry(contentTypesModelList.get(k).contentCount, contentTypesModelList.get(k).contentType));
            }
        }
        PieDataSet dataSets = new PieDataSet(xvalues, "");

        PieData datas = new PieData(dataSets);
        // In Percentage term
        datas.setValueFormatter(new PercentFormatter());

        contentChart.setDrawHoleEnabled(false);
        contentChart.setTransparentCircleRadius(25f);
        contentChart.setHoleRadius(25f);

        dataSets.setColors(ColorTemplate.VORDIPLOM_COLORS);
        datas.setValueTextSize(13f);
        datas.setValueTextColor(Color.DKGRAY);

        contentChart.animateXY(1400, 1400);

        datas.setValueTextSize(11f);
        datas.setValueTextColor(Color.BLACK);
        contentChart.setDrawCenterText(true);
        contentChart.setData(datas);
        contentChart.highlightValues(null);
        contentChart.invalidate();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.progressreportfragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);

        progressReportAdapter = new ProgressReportAdapter(getActivity(), progressReportModelList, progressExpandList, ProgressReportfragment.this);
        progressExpandList.setAdapter(progressReportAdapter);
        progressExpandList.setOnItemClickListener(this);
        progressExpandList.setEmptyView(rootView.findViewById(R.id.nodata_label));


        progressExpandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
//                Toast.makeText(context, "groupPosition: " + groupPosition + "childPosition: " + childPosition, Toast.LENGTH_SHORT).show();

                final ProgressReportChildModel progressReportModel = progressReportModelList.get(groupPosition).progressReportChildModelList.get(childPosition);

//                if (view.getId() == R.id.txt_contextmenu) {
//                    progresReportContextMenuMethod(view, progressReportModel);
//                } else {
                getMobileGetMobileContentMetaData(appUserModel.getSiteURL(), progressReportModel.objectID);
//                }


                return true;
            }
        });

        progressExpandList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

//                Toast.makeText(context, "groupPosition: " + i, Toast.LENGTH_SHORT).show();

                final ProgressReportModel progressReportModel = progressReportModelList.get(i);

                if (progressReportModel.progressReportChildModelList != null && progressReportModel.progressReportChildModelList.size() > 0) {

                    return false;
                } else {

                    getMobileGetMobileContentMetaData(appUserModel.getSiteURL(), progressReportModel.objectID);

                    return true;
                }
            }
        });

        View header = (View) getLayoutInflater(savedInstanceState).inflate(R.layout.progresscharts, null);
        progressExpandList.addHeaderView(header);
        txtContentScore = (TextView) header.findViewById(R.id.contentcount);
        mChart = (PieChart) header.findViewById(R.id.statuschart);
        contentChart = (PieChart) header.findViewById(R.id.contenttypechart);
        ringcharts = (TrackingRingView) header.findViewById(R.id.ringcharts);
        progressReportModelList = new ArrayList<ProgressReportModel>();
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshCatalog(false);
        } else {

        }

        initilizeView();

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconforum, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(context, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));


        return rootView;
    }


    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mylearning_menu, menu);
        item_search = menu.findItem(R.id.mylearning_search);
        MenuItem item_filter = menu.findItem(R.id.mylearning_filter);
        MenuItem itemInfo = menu.findItem(R.id.mylearning_info_help);

        itemInfo.setVisible(false);
        item_filter.setVisible(false);
        item_search.setVisible(false);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint("Search..");
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {


                    return true;
                }

            });

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mylearning_search:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.toolbar, 1, true, true);
                else
                    toolbar.setVisibility(View.VISIBLE);
                item_search.expandActionView();
                break;
            case R.id.mylearning_info_help:

                break;
            case R.id.mylearning_filter:
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshCatalog(true);
            MenuItemCompat.collapseActionView(item_search);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.card_view:
//                attachFragment(progressReportModelList.get(position));
                break;
            case R.id.btn_contextmenu:
//                View v = discussionFourmlistView.getChildAt(position - discussionFourmlistView.getFirstVisiblePosition());
//                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
//                catalogContextMenuMethod(position, view, txtBtnDownload, discussionForumModelList.get(position));
                break;
            default:

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = getActivity().findViewById(viewID);
        int width = myView.getWidth();
        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);
        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);
        anim.setDuration((long) 400);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });
        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("NEWFORUM", false);
                if (refresh) {
                    refreshCatalog(true);
                }
            }
        }
    }


    @Override
    public void onDetach() {

        super.onDetach();
    }

    public List<ProgressChartsModel.ContentStatusModel> generateContentStatusModelList(JSONObject jsonObject) throws JSONException {
        List<ProgressChartsModel.ContentStatusModel> contentStatusModelList1 = new ArrayList<>();

        JSONArray jsonTableAry = jsonObject.getJSONArray("StatusCountData");


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);


            ProgressChartsModel.ContentStatusModel contentStatusModel = new ProgressChartsModel.ContentStatusModel();

            //ConnectionUserID
            if (jsonMyLearningColumnObj.has("status")) {

                contentStatusModel.status = jsonMyLearningColumnObj.getString("status");
            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("ContentCount")) {

                contentStatusModel.contentCount = jsonMyLearningColumnObj.getInt("ContentCount");

            }

            contentStatusModelList1.add(contentStatusModel);
        }
        return contentStatusModelList1;
    }


    public List<ProgressChartsModel.ContentTypesModel> generateContentTypesModelList(JSONObject jsonObject) throws JSONException {
        List<ProgressChartsModel.ContentTypesModel> contentStatusModelList1 = new ArrayList<>();

        JSONArray jsonTableAry = jsonObject.getJSONArray("ContentCountData");


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            ProgressChartsModel.ContentTypesModel contentStatusModel = new ProgressChartsModel.ContentTypesModel();

            //ConnectionUserID
            if (jsonMyLearningColumnObj.has("ContentType")) {

                contentStatusModel.contentType = jsonMyLearningColumnObj.getString("ContentType");
            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("ContentCount")) {

                contentStatusModel.contentCount = jsonMyLearningColumnObj.getInt("ContentCount");

            }

            contentStatusModelList1.add(contentStatusModel);
        }
        return contentStatusModelList1;
    }

    public void generateContentAverageScore(JSONObject jsonObject) throws JSONException {

        List<ProgressChartsModel.ContentTypesModel> contentStatusModelList1 = new ArrayList<>();

        JSONArray jsonTable = jsonObject.getJSONArray("ScoreCount");
        JSONArray jsonTableAr = jsonObject.getJSONArray("ScoreMaxCount");

        int overAllScore = jsonTable.getJSONObject(0).getInt("overallscore");

        int scoreMax = jsonTableAr.getJSONObject(0).getInt("ScoreMax");


        // ringcharts function
        final List<DataEntry> entrie = new ArrayList<>();
        entrie.add(new DataEntry(scoreMax, "" + scoreMax, context.getResources().getColor(R.color.colorInGreen), Color.LTGRAY));
        entrie.add(new DataEntry(overAllScore, "" + overAllScore, context.getResources().getColor(R.color.colorOutTealGreen), Color.LTGRAY));
        final DataSet datasets = new DataSet(entrie);
        ringcharts.setDataSet(datasets);
    }

    public List<ProgressReportModel> generateProgressReport(JSONObject jsonObject) throws
            JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("ParentData");

        List<ProgressReportModel> progressReportModelList = new ArrayList<>();

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);


            ProgressReportModel progressReportModel = new ProgressReportModel();

            //ConnectionUserID
            if (jsonMyLearningColumnObj.has("userid")) {

                progressReportModel.userid = jsonMyLearningColumnObj.getString("userid");
            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("orgname")) {

                progressReportModel.orgname = jsonMyLearningColumnObj.get("orgname").toString();

            }

            // JobTitle
            if (jsonMyLearningColumnObj.has("contenttitle")) {

                progressReportModel.contenttitle = jsonMyLearningColumnObj.get("contenttitle").toString();

            }
            // MainOfficeAddress
            if (jsonMyLearningColumnObj.has("ObjectTypeID")) {

                progressReportModel.objectTypeID = jsonMyLearningColumnObj.getString("ObjectTypeID");

            }

            // MemberProfileImage
            if (jsonMyLearningColumnObj.has("contenttype")) {
                progressReportModel.contenttype = jsonMyLearningColumnObj.getString("contenttype");

            }

            // UserDisplayname
            if (jsonMyLearningColumnObj.has("AssignedOn")) {

                progressReportModel.assignedOn = jsonMyLearningColumnObj.getString("AssignedOn");

            }
            // connectionstate
            if (jsonMyLearningColumnObj.has("TargetDate")) {

                progressReportModel.targetDate = jsonMyLearningColumnObj.getString("TargetDate");

            }
            // connectionstateAccept
            if (jsonMyLearningColumnObj.has("Status")) {

                progressReportModel.status = jsonMyLearningColumnObj.get("Status").toString();

            }


            // AcceptAction
            if (jsonMyLearningColumnObj.has("datestarted")) {

                progressReportModel.datestarted = jsonMyLearningColumnObj.getString("datestarted");


            }

            // IgnoreAction
            if (jsonMyLearningColumnObj.has("datecompleted")) {

                progressReportModel.datecompleted = jsonMyLearningColumnObj.getString("datecompleted");

            }


            // InterestAreas
            if (jsonMyLearningColumnObj.has("SCOID")) {

                progressReportModel.SCOID = jsonMyLearningColumnObj.getString("SCOID");

            }

            // ObjectID
            if (jsonMyLearningColumnObj.has("ObjectID")) {

                progressReportModel.objectID = jsonMyLearningColumnObj.getString("ObjectID");

            }

            // ObjectID
            if (jsonMyLearningColumnObj.has("overallscore")) {

                progressReportModel.overScore = jsonMyLearningColumnObj.getString("overallscore");

            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("skillname")) {

                progressReportModel.skillname = jsonMyLearningColumnObj.getString("skillname");

            }

            // categoryname
            if (jsonMyLearningColumnObj.has("categoryname")) {
                progressReportModel.categoryname = jsonMyLearningColumnObj.getString("categoryname");
            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("jobrolename")) {

                progressReportModel.jobrolename = jsonMyLearningColumnObj.getString("jobrolename");

            }

            // ObjectID
            if (jsonMyLearningColumnObj.has("categoryID")) {

                progressReportModel.categoryID = jsonMyLearningColumnObj.getString("categoryID");

            }
            // ObjectID here one more loop
            if (jsonMyLearningColumnObj.has("ChildData")) {

                String responseChild = jsonMyLearningColumnObj.get("ChildData").toString();

                if (isValidString(responseChild)) {
                    JSONArray jsonArray = jsonMyLearningColumnObj.getJSONArray("ChildData");

                    if (jsonArray != null && jsonArray.length() > 0) {

                        progressReportModel.progressReportChildModelList = getChildDataFor(jsonArray);
                    }
                }

            }

            progressReportModelList.add(progressReportModel);
        }

        List<ProgressChartsModel.ContentStatusModel> contentStatusModel = new ArrayList<>();

        JSONArray contentStatusAry = jsonObject.getJSONArray("StatusCountData");


        return progressReportModelList;
    }

    public List<ProgressReportChildModel> getChildDataFor(JSONArray jsonTableAry) throws
            JSONException {

        List<ProgressReportChildModel> progressReportModelList = new ArrayList<>();

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            ProgressReportChildModel progressReportModel = new ProgressReportChildModel();

            //ConnectionUserID
            if (jsonMyLearningColumnObj.has("userid")) {

                progressReportModel.userid = jsonMyLearningColumnObj.getString("userid");
            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("orgname")) {

                progressReportModel.orgname = jsonMyLearningColumnObj.get("orgname").toString();

            }

            // JobTitle
            if (jsonMyLearningColumnObj.has("contenttitle")) {

                progressReportModel.contenttitle = jsonMyLearningColumnObj.get("contenttitle").toString();

            }
            // MainOfficeAddress
            if (jsonMyLearningColumnObj.has("ObjectTypeID")) {

                progressReportModel.objectTypeID = jsonMyLearningColumnObj.getString("ObjectTypeID");

            }

            // MemberProfileImage
            if (jsonMyLearningColumnObj.has("contenttype")) {
                progressReportModel.contenttype = jsonMyLearningColumnObj.getString("contenttype");

            }

            // UserDisplayname
            if (jsonMyLearningColumnObj.has("AssignedOn")) {

                progressReportModel.assignedOn = jsonMyLearningColumnObj.getString("AssignedOn");

            }
            // connectionstate
            if (jsonMyLearningColumnObj.has("TargetDate")) {

                progressReportModel.targetDate = jsonMyLearningColumnObj.getString("TargetDate");

            }
            // connectionstateAccept
            if (jsonMyLearningColumnObj.has("Status")) {

                progressReportModel.status = jsonMyLearningColumnObj.get("Status").toString();

            }


            // AcceptAction
            if (jsonMyLearningColumnObj.has("datestarted")) {

                progressReportModel.datestarted = jsonMyLearningColumnObj.getString("datestarted");


            }

            // IgnoreAction
            if (jsonMyLearningColumnObj.has("datecompleted")) {

                progressReportModel.datecompleted = jsonMyLearningColumnObj.getString("datecompleted");

            }


            // InterestAreas
            if (jsonMyLearningColumnObj.has("SCOID")) {

                progressReportModel.SCOID = jsonMyLearningColumnObj.getString("SCOID");

            }

            // ObjectID
            if (jsonMyLearningColumnObj.has("ObjectID")) {

                progressReportModel.objectID = jsonMyLearningColumnObj.getString("ObjectID");

            }

            // ObjectID
            if (jsonMyLearningColumnObj.has("overallscore")) {

                progressReportModel.overScore = jsonMyLearningColumnObj.getString("overallscore");

            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("skillname")) {

                progressReportModel.skillname = jsonMyLearningColumnObj.getString("skillname");

            }

            // categoryname
            if (jsonMyLearningColumnObj.has("categoryname")) {
                progressReportModel.categoryname = jsonMyLearningColumnObj.getString("categoryname");
            }
            // ObjectID
            if (jsonMyLearningColumnObj.has("jobrolename")) {

                progressReportModel.jobrolename = jsonMyLearningColumnObj.getString("jobrolename");

            }

            // ObjectID
            if (jsonMyLearningColumnObj.has("categoryID")) {

                progressReportModel.categoryID = jsonMyLearningColumnObj.getString("categoryID");

            }

            progressReportModelList.add(progressReportModel);
        }


        return progressReportModelList;
    }

    public void getMobileGetMobileContentMetaData(final String siteUrl, final String contentID) {
        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/MobileGetMobileContentMetaData?SiteURL="
                + siteUrl + "&ContentID=" + contentID + "&userid="
                + appUserModel.getUserIDValue() + "&DelivoryMode=1";

        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "getMobileGetMobileContentMetaData : " + urlStr);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {

                        Log.d(TAG, "getMobileGetMobileContentMetaData response : " + jsonObj);
                        if (jsonObj.length() != 0) {

                            try {
                                MyLearningModel learningModel = getMylearningModel(jsonObj);
                                openReportsActivity(learningModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        svProgressHUD.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
                        svProgressHUD.dismiss();
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

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void openReportsActivity(MyLearningModel myLearningModel) {

        if (myLearningModel.getUserID().length() > 0 && !myLearningModel.getUserID().equalsIgnoreCase("-1")) {

            db.injectMyLearningIntoTable(myLearningModel, false, true);

            Intent intentReports = new Intent(context, Reports_Activity.class);
            intentReports.putExtra("myLearningDetalData", myLearningModel);
            intentReports.putExtra("typeFrom", "");
            intentReports.putExtra("FROMPG", true);
            startActivity(intentReports);

        } else {
            Toast.makeText(context, "No record found", Toast.LENGTH_SHORT).show();
        }
    }

    public MyLearningModel getMylearningModel(JSONObject jsonObject) throws JSONException {

        Log.d("saveNewlySubscribed DB", " " + jsonObject);

        JSONArray jsonTableAry = jsonObject.getJSONArray("table");
        // for deleting records in table for respective table

        MyLearningModel myLearningModel = new MyLearningModel();

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            //sitename
            if (jsonMyLearningColumnObj.has("sitename")) {

                myLearningModel.setSiteName(jsonMyLearningColumnObj.get("sitename").toString());
            }
            // siteurl
            if (jsonMyLearningColumnObj.has("siteurl")) {

                myLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());

            }
            // siteid
            if (jsonMyLearningColumnObj.has("siteid")) {

                myLearningModel.setSiteID(jsonMyLearningColumnObj.get("siteid").toString());

            }
            // userid
            if (jsonMyLearningColumnObj.has("userid")) {

                myLearningModel.setUserID(jsonMyLearningColumnObj.get("userid").toString());

            }
            // coursename


            if (jsonMyLearningColumnObj.has("name")) {

                myLearningModel.setCourseName(jsonMyLearningColumnObj.get("name").toString());

            }

            // shortdes
            if (jsonMyLearningColumnObj.has("shortdescription")) {


                Spanned result = fromHtml(jsonMyLearningColumnObj.get("shortdescription").toString());

                myLearningModel.setShortDes(result.toString());

            }

            String authorName = "";
            if (jsonMyLearningColumnObj.has("contentauthordisplayname")) {
                authorName = jsonMyLearningColumnObj.getString("contentauthordisplayname");

            }

            if (authorName.length() != 0) {
                myLearningModel.setAuthor(authorName);
            } else {
                // author
                if (jsonMyLearningColumnObj.has("author")) {

                    myLearningModel.setAuthor(jsonMyLearningColumnObj.get("author").toString());

                }
            }

            // contentID
            if (jsonMyLearningColumnObj.has("contentid")) {

                myLearningModel.setContentID(jsonMyLearningColumnObj.get("contentid").toString());

            }
            // createddate
            if (jsonMyLearningColumnObj.has("createddate")) {

                myLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

            }
            // displayName

            myLearningModel.setDisplayName(appUserModel.getDisplayName());
            // durationEndDate
            if (jsonMyLearningColumnObj.has("durationenddate")) {

                myLearningModel.setDurationEndDate(jsonMyLearningColumnObj.get("durationenddate").toString());

            }
            // objectID
            if (jsonMyLearningColumnObj.has("objectid")) {

                myLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());

            }
            // thumbnailimagepath
            if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {

                String imageurl = jsonMyLearningColumnObj.getString("thumbnailimagepath");


                if (isValidString(imageurl)) {

                    myLearningModel.setThumbnailImagePath(imageurl);
                    String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + myLearningModel.getContentID() + "/" + imageurl;
                    myLearningModel.setImageData(imagePathSet);


                } else {
                    if (jsonMyLearningColumnObj.has("contenttypethumbnail")) {
                        String imageurlContentType = jsonMyLearningColumnObj.getString("contenttypethumbnail");
                        if (isValidString(imageurlContentType)) {
                            String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + imageurlContentType;
                            myLearningModel.setImageData(imagePathSet);

                        }
                    }


                }
                // relatedcontentcount
                if (jsonMyLearningColumnObj.has("relatedconentcount")) {

                    myLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedconentcount").toString());

                }
                // isDownloaded
                if (jsonMyLearningColumnObj.has("isdownloaded")) {

                    myLearningModel.setIsDownloaded(jsonMyLearningColumnObj.get("isdownloaded").toString());

                }
                // courseattempts
                if (jsonMyLearningColumnObj.has("courseattempts")) {

                    myLearningModel.setCourseAttempts(jsonMyLearningColumnObj.get("courseattempts").toString());

                }
                // objecttypeid
                if (jsonMyLearningColumnObj.has("objecttypeid")) {

                    myLearningModel.setObjecttypeId(jsonMyLearningColumnObj.get("objecttypeid").toString());

                }
                // scoid
                if (jsonMyLearningColumnObj.has("scoid")) {

                    myLearningModel.setScoId(jsonMyLearningColumnObj.get("scoid").toString());

                }
                // startpage
                if (jsonMyLearningColumnObj.has("startpage")) {

                    myLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

                }
                // status
                if (jsonMyLearningColumnObj.has("corelessonstatus")) {

                    myLearningModel.setStatusActual(jsonMyLearningColumnObj.get("corelessonstatus").toString());

                }
                // userName
                myLearningModel.setUserName(appUserModel.getUserName());
                // longdes
                if (jsonMyLearningColumnObj.has("longdescription")) {

                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("longdescription").toString());

//                    myLearningModel.setShortDes(result.toString());
                    myLearningModel.setLongDes(result.toString());

                }
                // typeofevent
                if (jsonMyLearningColumnObj.has("viewtype")) {

                    int typeoFEvent = Integer.parseInt(jsonMyLearningColumnObj.get("viewtype").toString());

                    myLearningModel.setTypeofevent(typeoFEvent);

                }

                // medianame
                if (jsonMyLearningColumnObj.has("medianame")) {
                    String medianame = "";

                    if (!myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                        if (jsonMyLearningColumnObj.getString("medianame").equalsIgnoreCase("test")) {
                            medianame = "Assessment(Test)";

                        } else {
                            medianame = jsonMyLearningColumnObj.get("medianame").toString();
                        }
                    } else {
                        if (myLearningModel.getTypeofevent() == 2) {
                            medianame = "Event (Online)";


                        } else if (myLearningModel.getTypeofevent() == 1) {
                            medianame = "Event (Face to Face)";

                        }
                    }

                    myLearningModel.setMediaName(medianame);

                }       // ratingid
                if (jsonMyLearningColumnObj.has("ratingid")) {

                    myLearningModel.setRatingId(jsonMyLearningColumnObj.get("ratingid").toString());

                }
                // publishedDate
                if (jsonMyLearningColumnObj.has("publisheddate")) {

                    String formattedDate = formatDate(jsonMyLearningColumnObj.get("publisheddate").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                    Log.d(TAG, "injectEventCatalog: " + formattedDate);
                    myLearningModel.setPublishedDate(formattedDate);

                }

                if (jsonMyLearningColumnObj.has("eventstartdatedisplay")) {

                    String formattedDate = formatDate(jsonMyLearningColumnObj.get("eventstartdatedisplay").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                    Log.d(TAG, "injectEventCatalog: " + formattedDate);
                    myLearningModel.setEventstartTime(formattedDate);
                }
                // eventenddatedisplay
                if (jsonMyLearningColumnObj.has("eventenddatedisplay")) {

                    String formattedDate = formatDate(jsonMyLearningColumnObj.get("eventenddatedisplay").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

                    Log.d(TAG, "injectEventCatalog: " + formattedDate);
                    myLearningModel.setEventendTime(formattedDate);
                }
                // mediatypeid
                if (jsonMyLearningColumnObj.has("mediatypeid")) {

                    myLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());

                }
                // dateassigned
                if (jsonMyLearningColumnObj.has("dateassigned")) {

                    myLearningModel.setDateAssigned(jsonMyLearningColumnObj.get("dateassigned").toString());

                }
                // keywords
                if (jsonMyLearningColumnObj.has("seokeywords")) {

                    myLearningModel.setKeywords(jsonMyLearningColumnObj.get("seokeywords").toString());

                }
                // eventcontentid
                if (jsonMyLearningColumnObj.has("eventcontentid")) {

                    myLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());

                }
                // eventAddedToCalender
                myLearningModel.setEventAddedToCalender(false);

                // isExpiry
                myLearningModel.setIsExpiry("false");

                // locationname
                if (jsonMyLearningColumnObj.has("eventfulllocation")) {

                    myLearningModel.setLocationName(jsonMyLearningColumnObj.get("eventfulllocation").toString());

                }
                // timezone
                if (jsonMyLearningColumnObj.has("timezone")) {

                    myLearningModel.setTimeZone(jsonMyLearningColumnObj.get("timezone").toString());

                }
                // participanturl
                if (jsonMyLearningColumnObj.has("participanturl")) {

                    myLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

                }
                // password

                myLearningModel.setPassword(appUserModel.getPassword());

                // isListView
                if (jsonMyLearningColumnObj.has("bit5")) {

                    myLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

                }

                // joinurl
                if (jsonMyLearningColumnObj.has("joinurl")) {

                    myLearningModel.setJoinurl(jsonMyLearningColumnObj.get("joinurl").toString());

                }

                // offlinepath
                if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
                    String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
                    String startPage = jsonMyLearningColumnObj.get("startpage").toString();
                    String contentid = jsonMyLearningColumnObj.get("contentid").toString();
                    String downloadDestFolderPath = context.getExternalFilesDir(null)
                            + "/.Mydownloads/Contentdownloads" + "/" + contentid;

                    String finalDownloadedFilePath = downloadDestFolderPath + "/" + startPage;

                    myLearningModel.setOfflinepath(finalDownloadedFilePath);
                }
//
                // wresult
                if (jsonMyLearningColumnObj.has("wresult")) {

                    myLearningModel.setWresult(jsonMyLearningColumnObj.get("wresult").toString());

                }
                // wmessage
                if (jsonMyLearningColumnObj.has("wmessage")) {

                    myLearningModel.setWmessage(jsonMyLearningColumnObj.get("wmessage").toString());

                }

                // presenter
                if (jsonMyLearningColumnObj.has("presentername")) {

                    myLearningModel.setPresenter(jsonMyLearningColumnObj.get("presentername").toString());

                }

                //membershipname
                if (jsonMyLearningColumnObj.has("membershipname")) {

                    myLearningModel.setMembershipname(jsonMyLearningColumnObj.get("membershipname").toString());

                }
                //membershiplevel
                if (jsonMyLearningColumnObj.has("membershiplevel")) {

//                    myLearningModel.setMemberShipLevel(jsonMyLearningColumnObj.getInt("membershiplevel"));

                    String memberShip = jsonMyLearningColumnObj.getString("membershiplevel");
                    int memberInt = 1;
                    if (isValidString(memberShip)) {
                        memberInt = Integer.parseInt(memberShip);
                    } else {
                        memberInt = 1;
                    }
                    myLearningModel.setMemberShipLevel(memberInt);


                }


                //membershiplevel
                if (jsonMyLearningColumnObj.has("folderpath")) {

                    myLearningModel.setFolderPath(jsonMyLearningColumnObj.getString("folderpath"));

                }

                //jwvideokey
                if (jsonMyLearningColumnObj.has("jwvideokey")) {

                    String jwKey = jsonMyLearningColumnObj.getString("jwvideokey");

                    if (isValidString(jwKey)) {
                        myLearningModel.setJwvideokey(jwKey);
                    } else {
                        myLearningModel.setJwvideokey("");
                    }

                }

                //cloudmediaplayerkey
                if (jsonMyLearningColumnObj.has("cloudmediaplayerkey")) {

                    myLearningModel.setCloudmediaplayerkey(jsonMyLearningColumnObj.optString("cloudmediaplayerkey"));

                    String jwKey = jsonMyLearningColumnObj.getString("cloudmediaplayerkey");

                    if (isValidString(jwKey)) {
                        myLearningModel.setCloudmediaplayerkey(jwKey);
                    } else {
                        myLearningModel.setCloudmediaplayerkey("");
                    }
                }


                //sitename
                if (jsonMyLearningColumnObj.has("progress")) {

                    myLearningModel.setProgress(jsonMyLearningColumnObj.get("progress").toString());
                    if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started")) {

                    }
                } else {
                    myLearningModel.setStatusActual("Not Started");
                }

            }

        }

        return myLearningModel;
    }


}