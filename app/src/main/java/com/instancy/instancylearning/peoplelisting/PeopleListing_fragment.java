package com.instancy.instancylearning.peoplelisting;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.chatmessanger.ChatFragment;
import com.instancy.instancylearning.chatmessanger.SignalAService;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.utils.StaticValues.EVENT_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.Utilities.generateHashMap;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.showToast;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class PeopleListing_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener {

    String TAG = PeopleListing_fragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;

    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.peoplelsitview)
    ListView peopleListView;

    PeopleListingAdapter peopleListingAdapter;
    List<PeopleListingModel> peopleListingModelList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    String filterContentType = "", consolidationType = "all", sortBy = "";
    ResultListner resultListner = null;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    boolean isFromCatogories = false;


    SignalAService signalAService;

    @BindView(R.id.segmentedswitch)
    SegmentedGroup segmentedSwitch;

    @BindView(R.id.expertsbtn)
    RadioButton expertsBtn;

    @BindView(R.id.allPeoplebtn)
    RadioButton allPBtn;

    @BindView(R.id.myconnectionbtn)
    RadioButton myConBtn;

    @BindView(R.id.pendingbtn)
    RadioButton pendingBtn;

    String TABBALUE = "Experts";

    String recepientID = "default";

    String userStatus = "";

    Communicator communicator;

    public PeopleListing_fragment() {


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
//        chatService = ChatService.newInstance(context);


        String isViewed = preferencesManager.getStringValue(StaticValues.KEY_HIDE_ANNOTATION);
        if (isViewed.equalsIgnoreCase("true")) {
            appcontroller.setAlreadyViewd(true);
        } else {

            appcontroller.setAlreadyViewd(false);
        }
        vollyService = new VollyService(resultCallback, context);

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
        signalAService = SignalAService.newInstance(context);
        signalAService.startSignalA();
    }

    public void refreshPeopleListing(Boolean isRefreshed) {
        if (isNetworkConnectionAvailable(getContext(), -1)) {
//            peopleListingTabsEitherFromDatabaseOrAPI();
            if (!isRefreshed) {

                svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
            }

            String paramsString = "ComponentID=78&ComponentInstanceID=3473&UserID=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&Locale=en-us&FilterType=" + TABBALUE;

            vollyService.getJsonObjResponseVolley("PEOPLELISTING", appUserModel.getWebAPIUrl() + "/MobileLMS/GetPeopleListData?" + paramsString, appUserModel.getAuthHeaders());
        } else
            injectFromDbtoModel();

    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
//                Log.d(TAG, "Volley requester " + requestType);
//                Log.d(TAG, "Volley JSON post" + response);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("PEOPLELISTING")) {
                    if (response != null) {
                        try {
                            db.injectPeopleListingListIntoSqLite(response, TABBALUE);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                if (requestType.equalsIgnoreCase("PEOPLELISTINGTABS")) {
                    if (response != null) {
                        try {
                            db.injectPeopleListingListIntoSqLite(response, TABBALUE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }


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
//                Log.d(TAG, "Volley String post" + response);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("REMOVEACTION")) {
                    if (response != null) {
                        refreshPeopleListing(true);
//                        Log.d(TAG, "notifySuccess: in  REMOVEACTION " + response);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(response)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.dismiss();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();


                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }

                if (requestType.equalsIgnoreCase("ACCEPTACTION")) {
                    if (response != null) {

//                        Log.d(TAG, "notifySuccess: in  ACCEPTACTION " + response);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(response)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.dismiss();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                        refreshPeopleListing(true);
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                }

                if (requestType.equalsIgnoreCase("ADDACTION")) {
                    if (response != null) {
//                        Log.d(TAG, "notifySuccess: in  ADDACTION " + response);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(response)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        dialog.dismiss();

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();


                        refreshPeopleListing(true);
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    svProgressHUD.dismiss();
                }

                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {

                svProgressHUD.dismiss();
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.peoplelisting_fragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);


        peopleListingAdapter = new PeopleListingAdapter(getActivity(), BIND_ABOVE_CLIENT, peopleListingModelList);
        peopleListView.setAdapter(peopleListingAdapter);
        peopleListView.setOnItemClickListener(this);
        peopleListView.setEmptyView(rootView.findViewById(R.id.nodata_label));

        pendingBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        allPBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        expertsBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        myConBtn.setTextColor(getResources().getColor(R.color.colorWhite));

        expertsBtn.setTypeface(null, Typeface.BOLD);

        segmentedSwitch.setOnCheckedChangeListener(this);

        segmentedSwitch.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        pendingBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        expertsBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        allPBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        myConBtn.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        peopleListingModelList = new ArrayList<PeopleListingModel>();

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshPeopleListing(false);
//            peopleListingTabsEitherFromDatabaseOrAPI();
        } else {
            injectFromDbtoModel();
        }

        initilizeView();

        return rootView; // 31732298673 //
    }


    public void injectFromDbtoModel() {
        peopleListingModelList = db.fetchPeopleListModelList(TABBALUE);
        if (peopleListingModelList != null) {
            peopleListingAdapter.refreshList(peopleListingModelList);
        } else {
            peopleListingModelList = new ArrayList<PeopleListingModel>();
            peopleListingAdapter.refreshList(peopleListingModelList);
        }
    }

    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);


        communicator = new Communicator() {
            @Override
            public void messageRecieved(JSONArray messageReceived) {

                Toast.makeText(context, "Receive in Chat List " + messageReceived, Toast.LENGTH_SHORT).show();
            }
        };


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

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
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

                    peopleListingAdapter.filter(newText.toLowerCase(Locale.getDefault()));

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
//                Log.d(TAG, "onOptionsItemSelected :mylearning_info_help ");
                appcontroller.setAlreadyViewd(false);
                preferencesManager.setStringValue("false", StaticValues.KEY_HIDE_ANNOTATION);
                peopleListingAdapter.notifyDataSetChanged();
                break;
            case R.id.mylearning_filter:
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshPeopleListing(true);
            MenuItemCompat.collapseActionView(item_search);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.btntxt_download:
                if (isNetworkConnectionAvailable(context, -1)) {
                } else {
                    showToast(context, "No Internet");
                }
                break;
            case R.id.btn_contextmenu:
                View v = peopleListView.getChildAt(position - peopleListView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                peopelContextMenuMethod(position, view, txtBtnDownload, peopleListingModelList.get(position), uiSettingsModel, appUserModel);
                break;
            default:

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");
        EVENT_FRAGMENT_OPENED_FIRSTTIME = 2;
//        chatService.destroy();
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

    public void peopelContextMenuMethod(final int position, final View v, ImageButton btnselected, final PeopleListingModel peopleListingModel, UiSettingsModel uiSettingsModel, final AppUserModel userModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.peoplecontextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(false);//view profile
        menu.getItem(1).setVisible(false);//view content
        menu.getItem(2).setVisible(false);//accept connection
        menu.getItem(3).setVisible(false);//remove connection
        menu.getItem(4).setVisible(false);//ignore enrollment
        menu.getItem(5).setVisible(false);//send message
        menu.getItem(6).setVisible(false);//add to my connection
        menu.getItem(7).setVisible(true);

        if (peopleListingModel.viewProfileAction) {
            menu.getItem(0).setVisible(true);
        }

        if (peopleListingModel.viewContentAction) {
            menu.getItem(1).setVisible(false);
        }

        // comment for cvcta
        if (peopleListingModel.sendMessageAction) {
            menu.getItem(5).setVisible(true);
        }

        if (peopleListingModel.addToMyConnectionAction) {
            menu.getItem(6).setVisible(true);
        }

        if (peopleListingModel.acceptAction) {
            menu.getItem(2).setVisible(true);
        }

        if (peopleListingModel.removeFromMyConnectionAction) {
            menu.getItem(3).setVisible(true);
        }

        if (peopleListingModel.ignoreAction) {
            menu.getItem(4).setVisible(true);
        }

        if (peopleListingModel.viewProfileAction) {
            menu.getItem(0).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("View Profile")) {
                    Intent intentDetail = new Intent(context, PeopleListingProfile.class);
                    intentDetail.putExtra("peopleListingModel", peopleListingModel);
                    startActivity(intentDetail);

//                    Toast.makeText(context, "View Profile", Toast.LENGTH_SHORT).show();
                }
                if (item.getTitle().toString().equalsIgnoreCase("View Content")) {
//                    Toast.makeText(context, "View Content", Toast.LENGTH_SHORT).show();
                }

                if (item.getTitle().toString().equalsIgnoreCase("Accept Connection")) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        acceptConnectionAction(peopleListingModel);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                    }

                }

                if (item.getTitle().toString().equalsIgnoreCase("Send Message")) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {

                        try {
                            recepientID = generateUserChatList(peopleListingModel.userID);
                            peopleListingModel.chatConnectionUserId = recepientID;
                            peopleListingModel.chatUserStatus = userStatus;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Intent intentDetail = new Intent(context, ChatFragment.class);
                        intentDetail.putExtra("peopleListingModel", peopleListingModel);
                        startActivity(intentDetail);

                    } else {
                        Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                    }

                }

                if (item.getTitle().toString().equalsIgnoreCase("Remove Connection")) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(getResources().getString(R.string.removeconnectionalertmessage)).setTitle(getResources().getString(R.string.removeconnectionalert))
                                .setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();
                                removeConnectionAction(peopleListingModel);

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();


                    } else {
                        Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                    }
                }
                if (item.getTitle().toString().equalsIgnoreCase("Ignore Connection")) {
//                    Toast.makeText(context, "Ignore Connection", Toast.LENGTH_SHORT).show();

                }
                if (item.getTitle().toString().equalsIgnoreCase("Cancel")) {

//                    Intent intentDetail = new Intent(context, ChatFragment.class);
//                    intentDetail.putExtra("peopleListingModel", peopleListingModel);
//                    startActivity(intentDetail);

                }

                if (item.getTitle().toString().equalsIgnoreCase("Add to My Connections")) {//

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        addConnectionAction(peopleListingModel);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            }
        });
        popup.show();//showing popup menu

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void removeConnectionAction(PeopleListingModel peopleListingModel) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String paramsString = "SelectedObjectID="
                + peopleListingModel.userID
                + "&SelectAction=RemoveConnection&UserName="
                + peopleListingModel.userDisplayname + "&UserID=" + peopleListingModel.userID + "&mainSiteUserid=" + peopleListingModel.mainSiteUserID + "&SiteID=" + peopleListingModel.siteID + "&Locale=en-us";

        String paramsEncodeString = paramsString.replaceAll(" ", "%20");

        vollyService.getStringResponseVolley("REMOVEACTION", appUserModel.getWebAPIUrl() + "/MobileLMS/PeopleListingActions?" + paramsEncodeString, appUserModel.getAuthHeaders());
    }

    public void acceptConnectionAction(PeopleListingModel peopleListingModel) {

        String paramsString = "SelectedObjectID="
                + peopleListingModel.userID
                + "&SelectAction=Accept&UserName="
                + peopleListingModel.userDisplayname + "&UserID=" + peopleListingModel.userID + "&mainSiteUserid=" + peopleListingModel.mainSiteUserID + "&SiteID=" + peopleListingModel.siteID + "&Locale=en-us";

        String paramsEncodeString = paramsString.replaceAll(" ", "%20");

        vollyService.getStringResponseVolley("ACCEPTACTION", appUserModel.getWebAPIUrl() + "/MobileLMS/PeopleListingActions?" + paramsEncodeString, appUserModel.getAuthHeaders());
    }

    public void addConnectionAction(PeopleListingModel peopleListingModel) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String paramsString = "SelectedObjectID="
                + peopleListingModel.userID
                + "&SelectAction=AddConnection&UserName="
                + peopleListingModel.userDisplayname + "&UserID=" + peopleListingModel.userID + "&mainSiteUserid=" + peopleListingModel.mainSiteUserID + "&SiteID=" + peopleListingModel.siteID + "&Locale=en-us";

        String paramsEncodeString = paramsString.replaceAll(" ", "%20");

        vollyService.getStringResponseVolley("ADDACTION", appUserModel.getWebAPIUrl() + "/MobileLMS/PeopleListingActions?" + paramsEncodeString, appUserModel.getAuthHeaders());
    }


    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int isChecked) {
        MenuItemCompat.collapseActionView(item_search);
        switch (isChecked) {
            case R.id.expertsbtn:
                expertsBtn.setTypeface(null, Typeface.BOLD);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
//                sortByDate("before");
                TABBALUE = "Experts";
                refreshPeopleListing(true);
                break;
            case R.id.allPeoplebtn:
                allPBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
//                catalogAdapter.refreshList(catalogModelsList);

                TABBALUE = "All";
                refreshPeopleListing(true);
                break;
            case R.id.myconnectionbtn:
                myConBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                pendingBtn.setTypeface(null, Typeface.NORMAL);
                TABBALUE = "MyConnections";
//                sortByDate("after");
                refreshPeopleListing(true);

                break;
            case R.id.pendingbtn:
                pendingBtn.setTypeface(null, Typeface.BOLD);
                expertsBtn.setTypeface(null, Typeface.NORMAL);
                myConBtn.setTypeface(null, Typeface.NORMAL);
                allPBtn.setTypeface(null, Typeface.NORMAL);
                TABBALUE = "Pending";
                refreshPeopleListing(true);
//                sortByDate("after");

                break;
            default:
                // Nothing to do
                TABBALUE = "Experts";
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void sortByDate(String typeTime) {
//        List<MyLearningModel> myLearningModelList = new ArrayList<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        if (typeTime.equalsIgnoreCase("before")) {
//
//            for (int i = 0; i < catalogModelsList.size(); i++) {
//                Date strDate = null;
//                String checkDate = catalogModelsList.get(i).getEventstartTime();
//
//                try {
//                    strDate = sdf.parse(checkDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                if (new Date().before(strDate)) {
//                    myLearningModelList.add(catalogModelsList.get(i));
//                    catalogAdapter.refreshList(myLearningModelList);
////                 Toast.makeText(context, typeTime + " if  event " + strDate, Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//
//        } else {
//
//            for (int i = 0; i < catalogModelsList.size(); i++) {
//                Date strDate = null;
//                String checkDate = catalogModelsList.get(i).getEventstartTime();
//
//                try {
//                    strDate = sdf.parse(checkDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                if (new Date().after(strDate)) {
//                    myLearningModelList.add(catalogModelsList.get(i));
//                    catalogAdapter.refreshList(myLearningModelList);
////                 Toast.makeText(context, typeTime + " if  event " + strDate, Toast.LENGTH_SHORT).show();
//                } else {
//
//                    catalogAdapter.refreshList(myLearningModelList);
//                }
//
//
//            }
//
//
//        }

    }

    public String generateUserChatList(String userID) throws JSONException {
        String receipent = "default";

        String chatListStr = preferencesManager.getStringValue(StaticValues.CHAT_LIST);
        if (chatListStr.length() > 10) {
//            Log.d(TAG, "log: ConnectionId users List jsonObject  -------------- " + jsonObject);
            JSONArray jsonArray = new JSONArray(chatListStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJsonOnj = jsonArray.getJSONObject(i);
                Log.d(TAG, "generateUserChatList: " + userJsonOnj);
                if (userJsonOnj.getString("ChatuserID").equalsIgnoreCase(userID)) {
                    receipent = userJsonOnj.getString("ConnectionId");
                    userStatus = userJsonOnj.getString("status");

                }
            }
        }
        return receipent;
    }

}