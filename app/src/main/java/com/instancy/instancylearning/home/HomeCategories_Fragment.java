package com.instancy.instancylearning.home;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.interfaces.RecyclerViewClickListener;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CatalogCategoryButtonModel;

import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.generateHashMap;

/**
 * Created by Upendranath on 11/27/2017.
 */

public class HomeCategories_Fragment extends Fragment implements Communicator {

    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    String TAG = HomeCategories_Fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    private RecyclerViewClickListener clicklistener;

    @BindView(R.id.catalog_recycler)
    RecyclerView recyclerView;

    PreferencesManager preferencesManager;
    String filterContentType = "", consolidationType = "all", sortBy = "";
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    ResultListner resultListner = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    RecyclerView catalogRecycler;

    HomeButtonAdapter mAdapter;

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    protected List<SideMenusModel> homeMenuList = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new DatabaseHandler(context);

        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        String isViewed = preferencesManager.getStringValue(StaticValues.KEY_HIDE_ANNOTATION);
        if (isViewed.equalsIgnoreCase("true")) {
            appcontroller.setAlreadyViewd(true);
        } else {

            appcontroller.setAlreadyViewd(false);
        }


        vollyService = new VollyService(resultCallback, context);
        List<CatalogCategoryButtonModel> categoryButtonModelList;
        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERNAME));
        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));
        sideMenusModel = null;
        HashMap<String, String> responMap = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
            responMap = generateConditionsHashmap(sideMenusModel.getConditions());
        }
        if (responMap != null && responMap.containsKey("Type")) {
            String consolidate = responMap.get("Type");
            if (consolidate.equalsIgnoreCase("consolidate")) {
                consolidationType = "consolidate";
            } else {

                // keep all
                consolidationType = "all";
            }
        } else {
            // No such key // keep all
            consolidationType = "all";
        }
        if (responMap != null && responMap.containsKey("sortby")) {
            sortBy = responMap.get("sortby");
        } else {
            // No such key
            sortBy = "";
        }
        if (responMap != null && responMap.containsKey("FilterContentType")) {
            filterContentType = responMap.get("FilterContentType");
        } else {
            // No such key
            filterContentType = "";
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        if (container != null) {
            rootView = inflater.inflate(R.layout.cataloggrid_fragment, container, false);
            ButterKnife.bind(this, rootView);
            createRecyclerView(rootView);
            swipeRefreshLayout.setEnabled(false);
            initilizeView();

        }

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

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }

    public void createRecyclerView(View rootView) {


        LinearLayout llCatalogGridCatageory = (LinearLayout) rootView.findViewById(R.id.llCatalogGridCatageory);

        llCatalogGridCatageory.setVisibility(View.INVISIBLE);

        catalogRecycler = (RecyclerView) rootView.findViewById(R.id.catalog_recycler);

        catalogRecycler.setHasFixedSize(true);

        homeMenuList = new ArrayList<>();
        String requiredMenue = extractMenuesMethod(sideMenusModel.getParameterStrings());

        homeMenuList = db.getHomeInnerMenusData(requiredMenue);

        if (requiredMenue.length() != 0) {

            mAdapter = new HomeButtonAdapter(homeMenuList, clicklistener);
        }
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            catalogRecycler.setLayoutManager(new GridLayoutManager(context, 3));
        } else {
            catalogRecycler.setLayoutManager(new GridLayoutManager(context, 5));
        }

        catalogRecycler.setAdapter(mAdapter);
        catalogRecycler.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppBGColor())));
        ;
        catalogRecycler.setVisibility(View.VISIBLE);

        catalogRecycler.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG, "onClick: " + position);

                List<SideMenusModel> subMenuList = null;

                int parentMenuId = homeMenuList.get(position).getMenuId();
                subMenuList = db.getNativeSubMenusData(parentMenuId);

                if (subMenuList != null && subMenuList.size() > 0) {

                    mAdapter.reloadAllContent(subMenuList);

                } else {
                    String indexed = homeMenuList.get(position).getContextMenuId();
                    ((SideMenu) getActivity()).selectItem(Integer.parseInt(indexed), homeMenuList.get(position));
                }

            }
        }));
    }

    public HashMap<String, String> generateConditionsHashmap(String conditions) {

        HashMap<String, String> responMap = null;
        if (conditions != null && !conditions.equals("")) {
            if (conditions.contains("#@#")) {
                String[] conditionsArray = conditions.split("#@#");
                int conditionCount = conditionsArray.length;
                if (conditionCount > 0) {
                    responMap = generateHashMap(conditionsArray);

                }
            }
        }
        return responMap;
    }

    @Override
    public void breadCrumbStatus(List<ContentValues> dicBreadcrumbItems) {

        Log.d(TAG, "breadCrumbStatus: in HomeFragment " + dicBreadcrumbItems);

    }

    // for recycler view listners

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private RecyclerViewClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final RecyclerViewClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));

                Log.d(TAG, "onInterceptTouchEvent: ");
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public String extractMenuesMethod(String selectedMenus) {

        String menuesRequired = "";
        String[] arrParam = null;
        if (selectedMenus.contains("=")) {
            arrParam = selectedMenus.split("=");
            if (arrParam.length > 1) {

            }
        }
        if (arrParam != null) {

            String homeIntro = arrParam[1];

            if (homeIntro.contains("&")) {

                String[] homeString = homeIntro.split("&");
                homeIntro = homeString[0];
            }

            menuesRequired = homeIntro.replace("\"", "");
        }

        return menuesRequired;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
