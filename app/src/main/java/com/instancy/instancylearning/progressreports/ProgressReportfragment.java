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
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.CardView;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;

import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ProgressReportInterface;
import com.instancy.instancylearning.interfaces.ReportSummeryResponseListner;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;

import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.ProgressChartsModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import mg.yra.lib.trackingring.DataEntry;
import mg.yra.lib.trackingring.DataSet;
import mg.yra.lib.trackingring.TrackingRingView;

import static android.app.Activity.RESULT_OK;
import static com.github.mikephil.charting.utils.ColorTemplate.rgb;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class ProgressReportfragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, ProgressReportInterface {

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

    TextView titleStatus, titleContent, titleScore, titleContentTypes, txtMiddle;

    PieChart mChart, contentChart;

    TrackingRingView ringcharts;

    CardView contentTypeCard;

    ReportSummeryResponseListner reportSummeryResponseListner = null;

    public static final int[] MATERIAL_COLORS_CHARTS = {
            rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db"), rgb("#00ffff"), rgb("#2196F3"), rgb("#E91E63"), rgb("#009688"), rgb("#4CAF50"), rgb("#FFEB3B"), rgb("#607D8B"), rgb("#FF9800"), rgb("#FF5722"), rgb("#795548"), rgb("#3F51B5"), rgb("#F44336"), rgb("#CDDC39"), rgb("#E91E63")
    };

    public ProgressReportfragment() {


    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, getActivity());
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
            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }

        String parmStringUrl = appUserModel.getWebAPIUrl() + "MobileLMS/GetConsolidateRPT?aintSiteID=" + appUserModel.getSiteIDValue() + "&aintUserID=" + appUserModel.getUserIDValue() + "&astrLocale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "&aintComponentID=" + sideMenusModel.getComponentId() + "&aintCompInsID=" + sideMenusModel.getRepositoryId() + "&aintSelectedGroupValue=0";

        vollyService.getStringResponseVolley("PRGLIST", parmStringUrl, appUserModel.getAuthHeaders());

//        W x H x D: 189 cm x 90 cm x 221 cm (6 ft 2 in x 2 ft 11 in x 7 ft 3 in) :
//       Length: 75 inch, Width: 72 inch, Thickness: 5 inch (6 ft 3 in x 6 ft x 5 in)

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
        dataSet.setColors(MATERIAL_COLORS_CHARTS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        mChart.animateXY(1400, 1400);
        data.setValueFormatter(new MyValueFormatter());
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
        l1.setWordWrapEnabled(true);


        // entry label styling
        contentChart.setEntryLabelColor(Color.BLACK);
        contentChart.setEntryLabelTextSize(12f);
        ArrayList<PieEntry> xvalues = new ArrayList<PieEntry>();
        if (contentTypesModelList != null && contentTypesModelList.size() > 0) {
            for (int k = 0; k < contentTypesModelList.size(); k++) {
                xvalues.add(new PieEntry(contentTypesModelList.get(k).contentCount, contentTypesModelList.get(k).contentType));
            }
        }

        if (contentTypesModelList != null && contentTypesModelList.size() > 13) {
            contentChart.setMinimumHeight(1000);
        }

        PieDataSet dataSets = new PieDataSet(xvalues, "");

        PieData datas = new PieData(dataSets);
        // In Percentage term
        datas.setValueFormatter(new PercentFormatter());

        contentChart.setDrawHoleEnabled(false);
        contentChart.setTransparentCircleRadius(25f);
        contentChart.setHoleRadius(25f);
//        contentChart.getLegend().setEnabled(false);


        dataSets.setColors(MATERIAL_COLORS_CHARTS);

        datas.setValueTextSize(13f);

        datas.setValueTextColor(Color.DKGRAY);


        contentChart.animateXY(1400, 1400);

        datas.setValueFormatter(new MyValueFormatter());
        datas.setValueTextSize(11f);
        datas.setValueTextColor(Color.BLACK);
        contentChart.setDrawCenterText(true);
        contentChart.setData(datas);
        contentChart.highlightValues(null);
        contentChart.invalidate();

        if ((getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.app_medmentor)))) {
            contentTypeCard.setVisibility(View.GONE);
        }

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

        progressExpandList.setGroupIndicator(null);

        progressExpandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
//                Toast.makeText(context, "groupPosition: " + groupPosition + "childPosition: " + childPosition, Toast.LENGTH_SHORT).show();

                final ProgressReportChildModel progressReportModel = progressReportModelList.get(groupPosition).progressReportChildModelList.get(childPosition);

                if (view.getId() == R.id.txt_contextmenu) {
//                    progresReportContextMenuMethod(view, progressReportModel);
                } else {
                    ProgressReportModel reportModel = convertChildtoGroupModel(progressReportModel);
                    openReportsActivity(reportModel);

                }


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

                    openReportsActivity(progressReportModel);

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
        txtMiddle = (TextView) header.findViewById(R.id.txtMiddle);

        contentTypeCard = (CardView) header.findViewById(R.id.contentTypeCard);

        /// Localazation
        titleContentTypes = (TextView) header.findViewById(R.id.titleContentTypes);
        titleContentTypes.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_contentstypeslabel));

        titleContent = (TextView) header.findViewById(R.id.titleContent);
        titleContent.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_contentslabel));

        titleScore = (TextView) header.findViewById(R.id.titleScore);
        titleScore.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_averagescorelabel));

        titleStatus = (TextView) header.findViewById(R.id.titleStatus);
        titleStatus.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_statuslabel));


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

        MenuItem itemArchive = menu.findItem(R.id.ctx_archive);
        MenuItem itemWaitlist = menu.findItem(R.id.mylearning_addwaitlist);

        itemInfo.setVisible(false);
        item_filter.setVisible(false);
        item_search.setVisible(false);
        itemArchive.setVisible(false);
        itemWaitlist.setVisible(false);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setTitle(getLocalizationValue(JsonLocalekeys.search_label));
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint(getLocalizationValue(JsonLocalekeys.commoncomponent_label_searchlabel));
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
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
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

        int overAllScore = 0;
        int scoreMax = 0;

        if (jsonTable != null && jsonTable.length() > 0) {
            overAllScore = jsonTable.getJSONObject(0).getInt("overallscore");
        }

        if (jsonTableAr != null && jsonTableAr.length() > 0) {
            scoreMax = jsonTableAr.getJSONObject(0).getInt("ScoreMax");
        }

        // ringcharts function
        final List<DataEntry> entrie = new ArrayList<>();
        entrie.add(new DataEntry(0, "", context.getResources().getColor(R.color.colorWhite), context.getResources().getColor(R.color.colorWhite)));
        entrie.add(new DataEntry(overAllScore, "" + overAllScore, context.getResources().getColor(R.color.colorInGreen), Color.LTGRAY));
        final DataSet datasets = new DataSet(entrie);
        ringcharts.setDataSet(datasets);

        txtMiddle.setText("" + overAllScore + "%");
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

                if (!isValidString(progressReportModel.overScore)) {
                    progressReportModel.overScore = "";
                }

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
            //certificateAction

            progressReportModel.certificateAction = jsonMyLearningColumnObj.optString("CertificateAction");

            progressReportModel.credits=jsonMyLearningColumnObj.optString("Credit");

            progressReportModel.siteID = appUserModel.getUserIDValue();
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

                if (!isValidString(progressReportModel.overScore)) {
                    progressReportModel.overScore = "";
                }

                if (progressReportModel.objectTypeID.equalsIgnoreCase("9")) {
                    progressReportModel.overScore = " NA";
                }

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

            // ObjectID
            if (jsonMyLearningColumnObj.has("seqid")) {

                progressReportModel.seqId = jsonMyLearningColumnObj.optInt("seqid");
//                Log.d(TAG, "getChildDataFor: "+jsonMyLearningColumnObj.optInt("seqid"));
            }

            if (jsonMyLearningColumnObj.has("ParentID")) {

                progressReportModel.eventID = jsonMyLearningColumnObj.optString("ParentID");
                progressReportModel.trackID = jsonMyLearningColumnObj.optString("ParentID");

            }

            progressReportModel.certificateAction = jsonMyLearningColumnObj.optString("CertificateAction");

            progressReportModel.credits=jsonMyLearningColumnObj.optString("Credit");

            progressReportModel.siteID = appUserModel.getSiteIDValue();

            progressReportModelList.add(progressReportModel);
        }

        return progressReportModelList;
    }


    public void openReportsActivity(ProgressReportModel progressReportModel) {

        Intent intentReports = new Intent(context, ProgressReportsActivity.class);
        intentReports.putExtra("progressReportModel", progressReportModel);
        startActivity(intentReports);

    }

    public ProgressReportModel convertChildtoGroupModel(ProgressReportChildModel progressReportChildModel) {

        ProgressReportModel progressReportModel = new ProgressReportModel();

        if (progressReportChildModel != null) {

            progressReportModel.siteID = progressReportChildModel.siteID;
            progressReportModel.jobrolID = progressReportChildModel.jobrolID;
            progressReportModel.userid = progressReportChildModel.userid;
            progressReportModel.skillname = progressReportChildModel.skillname;
            progressReportModel.gradedColor = progressReportChildModel.gradedColor;
            progressReportModel.contenttitle = progressReportChildModel.contenttitle;
            progressReportModel.targetDate = progressReportChildModel.targetDate;
            progressReportModel.objectTypeID = progressReportChildModel.objectTypeID;
            progressReportModel.childData = progressReportChildModel.childData;
            progressReportModel.overScore = progressReportChildModel.overScore;
            progressReportModel.objectID = progressReportChildModel.objectID;
            progressReportModel.cartID = progressReportChildModel.cartID;
            progressReportModel.datecompleted = progressReportChildModel.datecompleted;
            progressReportModel.categoryname = progressReportChildModel.categoryname;
            progressReportModel.orgname = progressReportChildModel.orgname;
            progressReportModel.datestarted = progressReportChildModel.datestarted;
            progressReportModel.contenttype = progressReportChildModel.contenttype;
            progressReportModel.status = progressReportChildModel.status;
            progressReportModel.assignedOn = progressReportChildModel.assignedOn;
            progressReportModel.jobrolename = progressReportChildModel.jobrolename;
            progressReportModel.SCOID = progressReportChildModel.SCOID;
            progressReportModel.categoryID = progressReportChildModel.categoryID;
            progressReportModel.seqId = progressReportChildModel.seqId;
            progressReportModel.trackID = progressReportChildModel.trackID;
            progressReportModel.eventID = progressReportChildModel.eventID;


        }
        return progressReportModel;
    }


    @Override
    public void viewCertificateLink(boolean fromChild, ProgressReportModel progressReportModel, ProgressReportChildModel progressReportChildModel) {




    }
}