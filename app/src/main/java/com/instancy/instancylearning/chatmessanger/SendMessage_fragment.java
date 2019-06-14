package com.instancy.instancylearning.chatmessanger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;

import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


import static android.content.Context.BIND_ABOVE_CLIENT;


import static com.instancy.instancylearning.utils.Utilities.getButtonDrawable;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringHOmeMethod;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

import static com.instancy.instancylearning.utils.Utilities.showToast;

//https://www.spaceotechnologies.com/swipe-delete-listview-android-example/

/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class SendMessage_fragment extends Fragment implements AdapterView.OnItemClickListener {

    String TAG = SendMessage_fragment.class.getSimpleName();
    AppUserModel appUserModel;

    VollyService vollyService;
    IResult resultCallback = null;
    ResultListner resultListner = null;

    SVProgressHUD svProgressHUD;

    @BindView(R.id.userschatlist)
    SwipeMenuListView usersChatListView;

    SendMessageAdapter chatMessageAdapter;
    List<PeopleListingModel> peopleListingModelList = null;
    List<ChatListModel> chatListModelList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;

    int selectedId = 0;

    TextView nodata_label;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    SignalAService signalAService;

    String recepientID = "default";

    String userStatus = "";

    Communicator communicator;

    @Nullable
    @BindView(R.id.spnrRoles)
    Spinner spnrRoles;

    public SendMessage_fragment() {


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

        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
    }

    public void addMessageCountToUserList(JSONArray messageReceived) throws JSONException {

        if (peopleListingModelList.size() > 0) {
            for (int i = 0; i < peopleListingModelList.size(); i++) {
                String userID = messageReceived.getString(4);
                if (peopleListingModelList.get(i).userID.equalsIgnoreCase(userID)) {
                    peopleListingModelList.get(i).chatCount = peopleListingModelList.get(i).chatCount + 1;
                }
            }
        }
//        chatMessageAdapter.refreshList(peopleListingModelList);
    }


    public void startSignalService() {

        communicator = new Communicator() {
            @Override
            public void messageRecieved(JSONArray messageReceived) {

                Log.d(TAG, "messageRecieved: in userslist chat " + messageReceived);
                try {
                    addMessageCountToUserList(messageReceived);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void userOnline(int typeUpdate, JSONArray objReceived) {

                switch (typeUpdate) {
                    case 1://NewOnlineUser
                        Log.d(TAG, "NewOnlineUser: " + objReceived);
                        try {
                            updateNewOnlineUser(objReceived);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2://UpdateOnlineUserList
                        Log.d(TAG, "UpdateOnlineUserList: " + objReceived);
                        try {
                            updateOnlineUserList(objReceived);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3://StatusChanged
                        Log.d(TAG, "StatusChanged: " + objReceived);
                        try {
                            updateStatusChanged(objReceived);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }
        };
        signalAService = SignalAService.newInstance(context);
        signalAService.communicator = communicator;
        signalAService.startSignalA();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);
        // startSignalService();
    }


    public void updateOnlineUserList(JSONArray onlineUserObj) throws JSONException {
        //    [{"SendDateTime":"0001-01-01T00:00:00","SiteID":374,"UnReadCount":0,"UserID":1,"ProfPic":"\/Content\/SiteFiles\/374\/ProfileImages\/1_381.gif","FullName":"Instancy Admin","Country":null,"Myconid":0,"ConnectionStatus":0,"RoleID":0,"UserStatus":0,"Role":null,"friendsList":null,"ConnectionId":"a471fa59-e092-4b56-942f-c700f97f60cf","isOnline":true,"ConnectionAcceptID":-1}]

        if (chatListModelList.size() > 0) {
            for (int i = 0; i < chatListModelList.size(); i++) {
                Log.d(TAG, "updateOnlineUserList: " + onlineUserObj);

                if (onlineUserObj != null && onlineUserObj.length() > 0) {
                    JSONArray userJsonAry = onlineUserObj.getJSONArray(0);
                    for (int k = 0; k < onlineUserObj.length(); k++) {
                        JSONObject userJsonOnj = userJsonAry.getJSONObject(k);
                        if (chatListModelList.get(i).userID == userJsonOnj.optInt("UserID")) {
                            chatListModelList.get(i).unReadCount = userJsonOnj.optInt("UnReadCount");
                            chatListModelList.get(i).chatConnectionUserId = userJsonOnj.optString("ConnectionId");
                            chatListModelList.get(i).chatConnectionStatus = true;

                        }
                    }
                }

            }
        }
        chatMessageAdapter.refreshList(chatListModelList);
    }

    public void updateNewOnlineUser(JSONArray onlineUserObj) throws JSONException {
        //    [{"SendDateTime":"0001-01-01T00:00:00","SiteID":374,"UnReadCount":0,"UserID":1,"ProfPic":"\/Content\/SiteFiles\/374\/ProfileImages\/1_381.gif","FullName":"Instancy Admin","Country":null,"Myconid":0,"ConnectionStatus":0,"RoleID":0,"UserStatus":0,"Role":null,"friendsList":null,"ConnectionId":"a471fa59-e092-4b56-942f-c700f97f60cf","isOnline":true,"ConnectionAcceptID":-1}]

        JSONObject userJsonOnj = onlineUserObj.getJSONObject(0);

        if (chatListModelList.size() > 0) {
            for (int i = 0; i < chatListModelList.size(); i++) {
                Log.d(TAG, "updateNewOnlineUser: " + onlineUserObj);
                if (chatListModelList.get(i).userID == userJsonOnj.optInt("UserID")) {

                    chatListModelList.get(i).unReadCount = userJsonOnj.optInt("UnReadCount");
                    chatListModelList.get(i).chatConnectionUserId = userJsonOnj.optString("ConnectionId");
                    chatListModelList.get(i).chatConnectionStatus = true;

                }
            }
        }
        chatMessageAdapter.refreshList(chatListModelList);
    }


    public void updateStatusChanged(JSONArray onlineUserObj) throws JSONException {
        //    [{"SendDateTime":"0001-01-01T00:00:00","SiteID":374,"UnReadCount":0,"UserID":1,"ProfPic":"\/Content\/SiteFiles\/374\/ProfileImages\/1_381.gif","FullName":"Instancy Admin","Country":null,"Myconid":0,"ConnectionStatus":0,"RoleID":0,"UserStatus":0,"Role":null,"friendsList":null,"ConnectionId":"a471fa59-e092-4b56-942f-c700f97f60cf","isOnline":true,"ConnectionAcceptID":-1}]

        JSONObject userJsonOnj = onlineUserObj.getJSONObject(0);

        if (chatListModelList.size() > 0) {
            for (int i = 0; i < chatListModelList.size(); i++) {
                Log.d(TAG, "updateStatusChanged: " + onlineUserObj);
                if (chatListModelList.get(i).userID == userJsonOnj.optInt("UserID")) {

                    chatListModelList.get(i).chatConnectionUserId = "default";
                    chatListModelList.get(i).chatConnectionStatus = false;

                }
            }
        }
        chatMessageAdapter.refreshList(chatListModelList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sendmessage_fragment, container, false);

        ButterKnife.bind(this, rootView);

        peopleListingModelList = new ArrayList<PeopleListingModel>();
        try {
            generateUserChatList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chatListModelList = new ArrayList<ChatListModel>();
        //   initilizeSwipe();
        chatMessageAdapter = new SendMessageAdapter(getActivity(), BIND_ABOVE_CLIENT, chatListModelList);
        usersChatListView.setAdapter(chatMessageAdapter);
        usersChatListView.setOnItemClickListener(this);
        usersChatListView.setEmptyView(rootView.findViewById(R.id.nodata_label));
        nodata_label = (TextView) rootView.findViewById(R.id.nodata_label);
        nodata_label.setText(getLocalizationValue(JsonLocalekeys.message_label_nouserlabel));
        initilizeView();
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            getChatConnectionUserList();
        }
        updateGameSpinner();


        return rootView;
    }

    public void initilizeSwipe() {

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;
                }
            }
        };

        // set creator
        usersChatListView.setMenuCreator(creator);

        // step 2. listener item click event
        usersChatListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ChatListModel item = chatListModelList.get(position);
                switch (index) {
                    case 0:
                        // open
                        Toast.makeText(context, "Archived", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        // delete
//                        chatListModelList.remove(position);
//                        chatMessageAdapter.notifyDataSetChanged();
                        deleteMethod(chatListModelList.get(position));
                        break;
                    default:

                        break;
                }
                return false;
            }

        });

    }

    private void createMenu1(SwipeMenu menu) {

        Drawable iconArchive = getButtonDrawable(R.string.fa_icon_archive, context, uiSettingsModel.getAppHeaderTextColor());

        Drawable iconDelete = getButtonDrawable(R.string.fa_icon_trash, context, uiSettingsModel.getAppHeaderTextColor());


        SwipeMenuItem item1 = new SwipeMenuItem(
                context);
        item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18,
                0x5E)));
        item1.setWidth(dp2px(75));
        item1.setIcon(iconArchive);
        item1.setTitle("Archive");
        menu.addMenuItem(item1);
        SwipeMenuItem item2 = new SwipeMenuItem(
                context);
        item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                0xCE)));
        item2.setWidth(dp2px(75));
        item2.setIcon(iconDelete);
        item2.setTitle("Delete");
        item2.setTitleColor(getResources().getColor(R.color.colorRed));
        menu.addMenuItem(item2);
    }

    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + getLocalizationValue(JsonLocalekeys.sidemenu_button_messagingbutton) + "</font>"));

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

        item_search.setVisible(false);
        itemInfo.setVisible(false);
        item_filter.setVisible(false);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
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

                    chatMessageAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });

        }
    }

    public void deleteMethod(ChatListModel chatListModel) {

        final JSONObject parameters = new JSONObject();

        try {
            parameters.put("FromUserID", appUserModel.getSiteIDValue());
            parameters.put("ToUserID", chatListModel.chatConnectionUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false).setTitle(getLocalizationValue(JsonLocalekeys.profile_alerttitle_stringconfirmation)).setMessage(getLocalizationValue(JsonLocalekeys.asktheexpert_alertsubtitle_areyousurewanttopermanentlydeletequestion))
                .setPositiveButton(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_deleteoption), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        deleteChatConverFromServer(parameters.toString());
                    }
                }).setNegativeButton(getLocalizationValue(JsonLocalekeys.asktheexpert_actionsheet_canceloption),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialog.create();
        alertDialogAndroid.show();
    }

    public void deleteChatConverFromServer(final String postData) {


        String urlString = appUserModel.getWebAPIUrl() + "/Chat/DeleteChatConversation";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.discussionforum_alerttitle_stringsuccess), Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
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
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

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
            case R.id.mylearning_filter:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChatListModel chatListModel = (ChatListModel) parent.getItemAtPosition(position);
        PeopleListingModel peopleListingModel = convertTopeopleModel(chatListModel);
        switch (view.getId()) {
            case R.id.txtPeople:
                Intent intentDetail = new Intent(context, ChatActivity.class);
                intentDetail.putExtra("peopleListingModel", peopleListingModel);
                startActivity(intentDetail);
                updateCountValue(chatListModel);
                // peopleListingModelList.get(position).chatCount = 0;
                break;
            default:
        }
    }

    public PeopleListingModel convertTopeopleModel(ChatListModel chatListModel) {
        PeopleListingModel peopleListingModel = new PeopleListingModel();

//        public int userID = 0;
//        public String fullName = "";
//        public int unReadCount = 0;
//        public String profPic = "";
//        public String sendDateTime = "";
//        public String country = "";
//        public int myConid = 0;
//        public int connectionStatus = 0;
//        public int roleID = 0;
//        public int siteID = 374;
//        public int userStatus = 0;
//        public String role = "";
//        public int rankNo = 0;
//        public int archivedUserID = -1;
//        public String latestMessage = "";
//        public String jobTitle = "";
//
        peopleListingModel.userDisplayname = chatListModel.fullName;
        peopleListingModel.chatConnectionUserId = chatListModel.chatConnectionUserId;
        peopleListingModel.userID = "" + chatListModel.userID;
        peopleListingModel.memberProfileImage = chatListModel.profPic;
        if (chatListModel.chatConnectionStatus) {
            peopleListingModel.chatUserStatus = "Online";
        } else {
            peopleListingModel.chatUserStatus = "Offline";
        }
        peopleListingModel.chatCount = chatListModel.unReadCount;


        return peopleListingModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedId != 0) {
            chatMessageAdapter.refreshList(getSelectedRoleList(selectedId));
        } else {
            chatMessageAdapter.refreshList(chatListModelList);
        }

    }

    public void updateCountValue(ChatListModel chatListModel) {

        if (chatListModelList != null && chatListModelList.size() > 0) {

            for (int k = 0; k < chatListModelList.size(); k++) {

                if (chatListModel.userID == chatListModelList.get(k).userID) {

                    chatListModelList.get(k).unReadCount = 0;
                }
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (signalAService != null) {
            signalAService.stopSignalA();
        }
    }

    public void getChatConnectionUserList() {

        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("FromUserID", appUserModel.getUserIDValue());
            parameters.put("intSiteiD", appUserModel.getSiteIDValue());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        String urlString = appUserModel.getWebAPIUrl() + "Chat/GetChatConnectionUserList";
        vollyService.getStringResponseFromPostMethod(parameterString, "GetChatConnectionUserList", urlString);
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                svProgressHUD.dismiss();

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");

            }

            @Override
            public void notifySuccess(String requestType, String response) {
//                Log.d(TAG, "Volley String post" + response);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("GetChatConnectionUserList")) {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                chatListModelList = generateOnlineUsersList(jsonObject);
                                chatMessageAdapter.refreshList(chatListModelList);
                                startSignalService();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {

                svProgressHUD.dismiss();
            }
        };
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


    public void generateUserChatList() throws JSONException {

        String chatListStr = preferencesManager.getStringValue(StaticValues.CHAT_LIST);
        if (chatListStr.length() > 10) {
//            Log.d(TAG, "log: ConnectionId users List jsonObject  -------------- " + jsonObject);
            JSONArray jsonArray = new JSONArray(chatListStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJsonOnj = jsonArray.getJSONObject(i);
                Log.d(TAG, "generateUserChatList: " + userJsonOnj);

                PeopleListingModel peopleListingModel = new PeopleListingModel();
                peopleListingModel.chatConnectionUserId = userJsonOnj.getString("ConnectionId");
                peopleListingModel.userID = userJsonOnj.getString("ChatuserID");
                peopleListingModel.chatUserStatus = userJsonOnj.getString("status");
                peopleListingModel.userDisplayname = userJsonOnj.getString("Username");
                peopleListingModel.siteID = userJsonOnj.getString("UserChatSiteID");
                peopleListingModel.memberProfileImage = userJsonOnj.getString("ChatProfileImagepath");
                peopleListingModel.chatCount = userJsonOnj.getInt("UnReadCount");
                peopleListingModel.siteURL = appUserModel.getSiteURL();
                peopleListingModel.siteID = appUserModel.getSiteIDValue();
                peopleListingModelList.add(peopleListingModel);

            }
        }

    }

    public List<ChatListModel> generateOnlineUsersList(JSONObject jsonObject) throws JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table");

        List<ChatListModel> chatListModelList = new ArrayList<>();

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonColumnObj = jsonTableAry.getJSONObject(i);

            ChatListModel chatListModel = new ChatListModel();

            chatListModel.userID = jsonColumnObj.optInt("UserID");
            chatListModel.fullName = jsonColumnObj.optString("FullName");
            chatListModel.unReadCount = jsonColumnObj.optInt("UnReadCount");
            chatListModel.profPic = jsonColumnObj.optString("ProfPic");
            chatListModel.sendDateTime = jsonColumnObj.optString("SendDateTime");
            chatListModel.country = jsonColumnObj.optString("Country");
            chatListModel.myConid = jsonColumnObj.optInt("Myconid");
            chatListModel.connectionStatus = jsonColumnObj.optInt("ConnectionStatus");
            chatListModel.roleID = jsonColumnObj.optInt("RoleID");
            chatListModel.siteID = jsonColumnObj.optInt("SiteID");
            chatListModel.userStatus = jsonColumnObj.optInt("UserStatus");
            chatListModel.role = jsonColumnObj.optString("Role");
            chatListModel.rankNo = jsonColumnObj.optInt("RankNo");
            chatListModel.archivedUserID = jsonColumnObj.optInt("ArchivedUserID");
            chatListModel.latestMessage = jsonColumnObj.optString("LatestMessage");
            chatListModel.jobTitle = jsonColumnObj.optString("JobTitle");
            chatListModel.chatConnectionUserId = "Default";
            chatListModel.chatConnectionStatus = false;

            if (chatListModel.connectionStatus == 1 && chatListModel.userStatus == 1 && appUserModel.getSiteIDValue().equalsIgnoreCase("" + chatListModel.siteID)) {
                if (chatListModel.roleID == 8 || chatListModel.roleID == 6 || chatListModel.roleID == 12) {
                    boolean isDuplicate = false;
                    for (int j = 0; j < chatListModelList.size(); j++) {
                        if (chatListModelList.get(j).userID == chatListModel.userID) {
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate)
                        chatListModelList.add(chatListModel);
                }
            }
        }

        return chatListModelList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

//        signalAService.stopSignalA();
    }

    public void updateGameSpinner() {

        final ArrayList<String> userRolesList = getRolesList();
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, userRolesList);
        spnrRoles.setAdapter(spinnerAdapter);
        spnrRoles.setSelection(0, true);
        spnrRoles.setVisibility(View.VISIBLE);
        View v = spnrRoles.getSelectedView();
        ((TextView) v).setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        spnrRoles.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppTextColor())));

        spnrRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spnrPosition, long id) {

                Log.d(TAG, "onItemSelected: gamesList " + userRolesList.get(spnrPosition));

// (item.RoleID == 12 Manager || item.RoleID == 8 admin|| item.RoleID == 16 groupadmin

                switch (spnrPosition) {
                    case 0:
                        chatMessageAdapter.refreshList(chatListModelList);
                        selectedId = 0;
                        break;
                    case 1://16
                        chatMessageAdapter.refreshList(getSelectedRoleList(16));
                        selectedId = 16;
                        break;
                    case 2://8
                        chatMessageAdapter.refreshList(getSelectedRoleList(8));
                        selectedId = 8;
                        break;
                    case 3://12
                        chatMessageAdapter.refreshList(getSelectedRoleList(12));
                        selectedId = 12;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    public List<ChatListModel> getSelectedRoleList(int roleID) {
        List<ChatListModel> chatListModelList1 = new ArrayList<>();
        for (int i = 0; i < chatListModelList.size(); i++) {
            if (roleID == chatListModelList.get(i).roleID) {
                chatListModelList1.add(chatListModelList.get(i));
            }
        }
        return chatListModelList1;
    }

    public ArrayList<String> getRolesList() {

        ArrayList<String> userRoles = new ArrayList<>();

//        userRoles.add("All");
//        userRoles.add("Group Admin");//16
//        userRoles.add("Admin");//8
//        userRoles.add("Manager");//12

        userRoles.add(getLocalizationValue(JsonLocalekeys.myconnections_label_alllabel));
        userRoles.add(getLocalizationValue(JsonLocalekeys.myconnections_label_groupadminlabel));//16
        userRoles.add(getLocalizationValue(JsonLocalekeys.myconnections_label_adminlabel));//8
        userRoles.add(getLocalizationValue(JsonLocalekeys.myconnections_label_managerlabel));//12

        return userRoles;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}