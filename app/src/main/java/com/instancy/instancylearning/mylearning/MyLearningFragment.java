package com.instancy.instancylearning.mylearning;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.tintMenuIcon;

/**
 * Created by Upendranath on 5/19/2017.
 * http://www.cs.dartmouth.edu/~campbell/cs65/lecture08/lecture08.html
 * https://github.com/majidgolshadi/Android-Download-Manager-Pro
 */

public class MyLearningFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    String TAG = MyLearningFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    @Bind(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.mylearninglistview)
    ListView myLearninglistView;
    MyLearningAdapter myLearningAdapter;
    List<MyLearningModel> myLearningModelsList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    String filterContentType = "", consolidationType = "all", sortBy = "";
    ResultListner resultListner = null;
    WebAPIClient webAPIClient;
    //    SynchData synchData;
    CmiSynchTask cmiSynchTask;

    public MyLearningFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new DatabaseHandler(context);
        initVolleyCallback();
        cmiSynchTask = new CmiSynchTask(context);
//        synchData = new SynchData(context);
        vollyService = new VollyService(resultCallback, context);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERNAME));
        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));
        sideMenusModel = null;
        webAPIClient = new WebAPIClient(context);
        HashMap<String, String> responMap = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");
            responMap = generateConditionsHashmap(sideMenusModel.getConditions());
        }
        if (responMap != null && responMap.containsKey("showconsolidatedlearning")) {
            String consolidate = responMap.get("showconsolidatedlearning");
            if (consolidate.equalsIgnoreCase("true")) {
                consolidationType = "consolidate";
            } else {
                consolidationType = "all";
            }
        } else {
            // No such key
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
        Log.d(TAG, "onCreate: ");

        setHasOptionsMenu(true);

    }


    public void refreshMyLearning(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }
        String paramsString = "FilterCondition=" + filterContentType +
                "&SortCondition=" + sortBy +
                "&RecordCount=0&OrgUnitID="
                + appUserModel.getSiteIDValue()
                + "&UserID="
                + appUserModel.getUserIDValue()
                + "&Type=" + consolidationType
                + "&FilterID=-1&ComponentID=3&Locale=en-us&SearchText=&SiteID="
                + appUserModel.getSiteIDValue()
                + "&PreferenceID=-1&CategoryCompID=19&DateOfMyLastAccess=&SingleBranchExpand=false&GoogleValues=&DeliveryMode=1&GroupJoin=0";
        vollyService.getJsonObjResponseVolley("MYLEARNINGDATA", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileMyCatalogObjectsNew?" + paramsString, appUserModel.getAuthHeaders());
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("MYLEARNINGDATA")) {
                    if (response != null) {
                        try {
                            db.injectMyLearningData(response);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
                if (requestType.equalsIgnoreCase("UPDATESTATUS")) {

                    if (response != null) {

                        if (resultListner != null)
                            resultListner.statusUpdateFromServer(true, response);

                    } else {

                    }
                }
//                if (requestType.equalsIgnoreCase("MLADP")) {
//
//                    if (response != null) {
//                        try {
//                            db.injectCMIDataInto(response,lear);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//
//                    }
//
//                }
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
                if (requestType.equalsIgnoreCase("MLADP")) {

                    if (response != null) {
                        try {
                            db.injectCMIDataInto(response, myLearningModel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }

                }

                svProgressHUD.dismiss();
            }
        };
    }

    public void injectFromDbtoModel() {
//          myLearningModelsList.clear();
        myLearningModelsList = db.fetchMylearningModel();
        if (myLearningModelsList != null) {
//            Log.d(TAG, "dataLoaded: " + myLearningModelsList.size());
            myLearningAdapter.refreshList(myLearningModelsList);
//            myLearninglistView.setVisibility(View.VISIBLE);
        } else {
            myLearningModelsList = new ArrayList<MyLearningModel>();
            myLearningAdapter.refreshList(myLearningModelsList);
//            myLearninglistView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mylearning, container, false);
        Log.d(TAG, "onCreateView: ");
        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);
        myLearningModelsList = new ArrayList<MyLearningModel>();
        myLearningAdapter = new MyLearningAdapter(getActivity(), BIND_ABOVE_CLIENT, myLearningModelsList);
        myLearninglistView.setAdapter(myLearningAdapter);
        myLearninglistView.setOnItemClickListener(this);
        myLearninglistView.setEmptyView(rootView.findViewById(R.id.nodata_label));
        toolbar = ((SideMenu) getActivity()).toolbar;
//        setSearchtollbar();

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshMyLearning(false);
        } else {
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            injectFromDbtoModel();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String link = bundle.getString("url");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mylearning_menu, menu);
        item_search = menu.findItem(R.id.mylearning_search);
        item_search = menu.findItem(R.id.mylearning_search);
        MenuItem item_filter = menu.findItem(R.id.mylearning_filter);
        item_filter.setVisible(false);
        if (item_search != null) {
            item_search.setIcon(R.drawable.ic_search_black_24dp);
            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);

            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint("Search..");
            txtSearch.setHintTextColor(Color.DKGRAY);
            txtSearch.setTextColor(getResources().getColor(R.color.colorGray));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

//                    if (TextUtils.isEmpty(newText)) {
//                        myLearningAdapter.filter("");
//                        myLearninglistView.clearTextFilter();
//                        injectFromDbtoModel();
//                    } else {

                    myLearningAdapter.filter(newText.toLowerCase(Locale.getDefault()));

//                    }
                    return true;
                }
            });
        }

        if (item_filter != null) {
            item_filter.setIcon(R.drawable.ic_filter_list_black_24dp);
            tintMenuIcon(getActivity(), item_filter, R.color.colorWhite);
            item_filter.setTitle("Filter");
        }
    }

    public void setSearchtollbar() {
        if (toolbar != null) {
            toolbar.inflateMenu(R.menu.mylearning_menu);
            search_menu = toolbar.getMenu();

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(R.id.toolbar, 1, true, false);
                    else
                        toolbar.setVisibility(View.GONE);
                }
            });

        } else
            Log.d("toolbar", "setSearchtollbar: NULL");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.mylearning_search) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                circleReveal(R.id.toolbar, 1, true, true);
            else
                toolbar.setVisibility(View.VISIBLE);
            item_search.expandActionView();

//            MenuItemCompat.setOnActionExpandListener(item_search, new MenuItemCompat.OnActionExpandListener() {
//                @Override
//                public boolean onMenuItemActionCollapse(MenuItem item) {
//                    // Do something when collapsed
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        circleReveal(R.id.toolbar, 1, true, false);
//                    } else
//                        toolbar.setVisibility(View.GONE);
//                    return true;
//                }
//
//                @Override
//                public boolean onMenuItemActionExpand(MenuItem item) {
//                    // Do something when expanded
//                    return true;
//                }
//            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public HashMap<String, String> generateHashMap(String[] conditionsArray) {

        HashMap<String, String> map = new HashMap<String, String>();

        if (conditionsArray.length != 0) {

            for (int i = 0; i < conditionsArray.length; i++) {
                String[] filterArray = conditionsArray[i].split("=");
                System.out.println(" forvalue " + filterArray);
                if (filterArray.length > 1) {
                    map.put(filterArray[0], filterArray[1]);
                }
            }
            for (String s : map.keySet()) {
                System.out.println(s + " forvalue " + map.get(s));
            }
        } else {

        }
        return map;
    }

    @Override
    public void onRefresh() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshMyLearning(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.btntxt_download:
                downloadTheCourse(myLearningModelsList.get(position), view, position);
                break;
            case R.id.imagethumb:
                GlobalMethods.launchCourseViewFromGlobalClass(myLearningModelsList.get(position), getContext());
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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
    public void onResume() {
        super.onResume();
//        synchData.SyncData();
        cmiSynchTask = new CmiSynchTask(context);
        cmiSynchTask.execute();
    }

    public HashMap<String, String> generateConditionsHashmap(String conditions) {

        HashMap<String, String> responMap = null;
        if (conditions != null && !conditions.equals("")) {
            if (conditions.contains("#@#")) {
                String[] conditionsArray = conditions.split("#@#");
                int conditionCount = conditionsArray.length;
                if (conditionCount > 0) {
                    responMap = generateHashMap(conditionsArray);
//                    Log.d("Type", "Called On Type" + responMap.keySet());
//                    Log.d("Type", "Called On Type" + responMap.values());
                }
            }
        }
        return responMap;
    }

    public void downloadTheCourse(final MyLearningModel learningModel, final View view, final int position) {

        boolean isZipFile = false;

        final String[] downloadSourcePath = {null};


        switch (learningModel.getObjecttypeId()) {
            case "52":
                downloadSourcePath[0] = learningModel.getSiteURL() + "/content/sitefiles/"
                        + learningModel.getSiteID() + "/usercertificates/" + learningModel.getSiteID() + "/"
                        + learningModel.getContentID() + ".pdf";
                isZipFile = false;
                break;
            case "11":
            case "14":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getStartPage();
                isZipFile = false;
                break;
            case "8":
            case "9":
            case "10":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
                isZipFile = true;
                break;
            default:
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID()
                        + ".zip";
                isZipFile = true;
                break;
        }

        final boolean finalisZipFile = isZipFile;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int statusCode = 0;
                //code to do the HTTP request
                if (finalisZipFile) {

                    statusCode = webAPIClient.checkFileFoundOrNot(downloadSourcePath[0], appUserModel.getAuthHeaders());

                    if (statusCode != 200) {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/downloadfiles/"
                                + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], view, learningModel, position);

                    } else {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                                + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], view, learningModel, position);

                    }
                } else {

                    downloadThin(downloadSourcePath[0], view, learningModel, position);
                }
//                int statusCode = vollyService.checkResponseCode(downloadSourcePath[0]);

            }
        });
        thread.start();

    }

    public void downloadThin(String downloadStruri, View view, final MyLearningModel learningModel, final int position) {

        downloadStruri = downloadStruri.replace(" ", "%20");
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        Uri downloadUri = Uri.parse(downloadStruri);
        String extensionStr = "";
        switch (learningModel.getObjecttypeId()) {
            case "52":
            case "11":
            case "14":

                String[] startPage = null;
                if (learningModel.getStartPage().contains("/")) {
                    startPage = learningModel.getStartPage().split("/");
                    extensionStr = startPage[1];
                } else {
                    extensionStr = learningModel.getStartPage();
                }
                break;
            case "8":
            case "9":
            case "10":
                extensionStr = learningModel.getContentID() + ".zip";
                break;
            default:
                extensionStr = learningModel.getContentID() + ".zip";
                break;
        }

        String localizationFolder = "";
        String[] startPage = null;
        if (learningModel.getStartPage().contains("/")) {
            startPage = learningModel.getStartPage().split("/");
            localizationFolder = "/" + startPage[0];
        } else {
            localizationFolder = "";
        }
        String downloadDestFolderPath = "";
        if (extensionStr.contains(".zip")) {

            downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                    + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID();

        } else {
            downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                    + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID() + localizationFolder;
        }

        boolean success = (new File(downloadDestFolderPath)).mkdirs();

        final String finalDownloadedFilePath = downloadDestFolderPath + "/" + extensionStr;

        final Uri destinationUri = Uri.parse(finalDownloadedFilePath);
        final String finalDownloadDestFolderPath = downloadDestFolderPath;
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new com.thin.downloadmanager.DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Log.d(TAG, "onDownloadComplete: ");

                        if (finalDownloadedFilePath.contains(".zip")) {
                            String zipFile = finalDownloadedFilePath;
                            String unzipLocation = finalDownloadDestFolderPath;
                            UnZip d = new UnZip(zipFile,
                                    unzipLocation);
                            File zipfile = new File(zipFile);
                            zipfile.delete();
                        }
                        myLearningAdapter.notifyDataSetChanged();

                        if (!learningModel.getStatus().equalsIgnoreCase("Not Started")) {
                            callMetaDataService(learningModel);
                        }
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Log.d(TAG, "onDownloadFailed: " + +errorCode);
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        Log.d(TAG, "onProgress: " + progress);
                        View v = myLearninglistView.getChildAt(position - myLearninglistView.getFirstVisiblePosition());
                        if (v != null) {
                            updateStatus(position, progress);

                        }

                    }

                });
        int downloadId = downloadManager.add(downloadRequest);
    }

    public void callMetaDataService(MyLearningModel learningModel) {
        String paramsString = "_studid=" + learningModel.getUserID() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + learningModel.getSiteURL() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        vollyService.getJsonObjResponseVolley("MLADP", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?" + paramsString, appUserModel.getAuthHeaders(), learningModel);

    }

    private void updateStatus(int index, int Status) {
        // Update ProgressBar
        // Update Text to ColStatus
        View v = myLearninglistView.getChildAt(index - myLearninglistView.getFirstVisiblePosition());
        TextView txtBtnDownload = (TextView) v.findViewById(R.id.btntxt_download);
        CircleProgressBar circleProgressBar = (CircleProgressBar) v.findViewById(R.id.circle_progress);
        circleProgressBar.setVisibility(View.VISIBLE);
        txtBtnDownload.setVisibility(View.GONE);
        circleProgressBar.setProgress(Status);
        // Enabled Button View
        if (Status >= 100) {
            if (isAdded()) {
                txtBtnDownload.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
                txtBtnDownload.setVisibility(View.VISIBLE);
                circleProgressBar.setVisibility(View.GONE);
                txtBtnDownload.setEnabled(false);

            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        Log.d(TAG, "onActivityResult first:");

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COURSE_CLOSE_CODE && resultCode == RESULT_OK) {

            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");
                Log.d(TAG, "onActivityResult if getCourseName :" + myLearningModel.getCourseName());

                File myFile = new File(myLearningModel.getOfflinepath());

                if (!myFile.exists()) {

                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {


                        getStatusFromServer(myLearningModel);

                    } else {

                        if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {
                            int i = -1;
                            i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");

                            if (i == 1) {
                                Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();
                                injectFromDbtoModel();
                            } else {

                                Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }
            }
        }

        if (requestCode == DETAIL_CLOSE_CODE && resultCode == RESULT_OK) {

//            Toast.makeText(context, "Detail Status updated!", Toast.LENGTH_SHORT).show();

            if (data.getStringExtra("refresh").equalsIgnoreCase("refresh")) {

                injectFromDbtoModel();

            }
        }

    }

    public void getStatusFromServer(final MyLearningModel myLearningModel) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String paramsString = "";
        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {

            paramsString = "userId="
                    + myLearningModel.getUserID()
                    + "&scoId="
                    + myLearningModel.getScoId()
                    + "&TrackObjectTypeID="
                    + myLearningModel.getObjecttypeId()
                    + "&TrackContentID="
                    + myLearningModel.getContentID()
                    + "&TrackScoID=" + myLearningModel.getScoId()
                    + "&SiteID=" + myLearningModel.getSiteID()
                    + "&OrgUnitID=" + myLearningModel.getSiteID()
                    + "&isonexist=onexit";

        } else {
            paramsString = "userId="
                    + myLearningModel.getUserID()
                    + "&scoId="
                    + myLearningModel.getScoId();

        }

        vollyService.getJsonObjResponseVolley("UPDATESTATUS", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentStatus?" + paramsString, appUserModel.getAuthHeaders());

        resultListner = new ResultListner() {
            @Override
            public void statusUpdateFromServer(boolean serverUpdated, JSONObject result) {
                int i = -1;

                Log.d(TAG, "statusUpdateFromServer JSONObject :" + result);

                JSONArray jsonArray = null;
                try {
                    if (result.has("contentstatus")) {
                        jsonArray = result.getJSONArray("contentstatus");

                    }
                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String status = jsonObject.get("status").toString();
                        String progress = "";
                        if (jsonObject.has("progress")) {
                            progress = jsonObject.get("progress").toString();
                        }
                        i = db.updateContentStatus(myLearningModel, status, progress);
                        if (i == 1) {

                            injectFromDbtoModel();
                            Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
    }


}
