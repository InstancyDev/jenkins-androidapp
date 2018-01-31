package com.instancy.instancylearning.chatmessanger;

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


import android.widget.Toast;


import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;

import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.chatmessanger.SendMessageAdapter;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


import static android.content.Context.BIND_ABOVE_CLIENT;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.showToast;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class SendMessage_fragment extends Fragment implements AdapterView.OnItemClickListener {

    String TAG = SendMessage_fragment.class.getSimpleName();
    AppUserModel appUserModel;

    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;

    @BindView(R.id.userschatlist)
    ListView usersChatListView;

    SendMessageAdapter chatMessageAdapter;
    List<PeopleListingModel> peopleListingModelList = null;
    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    ResultListner resultListner = null;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    SignalAService signalAService;

    String recepientID = "default";

    String userStatus = "";

    Communicator communicator;

    public SendMessage_fragment() {


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
        chatMessageAdapter.refreshList(peopleListingModelList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        };
        signalAService = SignalAService.newInstance(context);
        signalAService.communicator = communicator;
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

        chatMessageAdapter = new SendMessageAdapter(getActivity(), BIND_ABOVE_CLIENT, peopleListingModelList);
        usersChatListView.setAdapter(chatMessageAdapter);
        usersChatListView.setOnItemClickListener(this);
        usersChatListView.setEmptyView(rootView.findViewById(R.id.nodata_label));
        initilizeView();
        return rootView;
    }

    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + "Messaging" + "</font>"));

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

                    chatMessageAdapter.filter(newText.toLowerCase(Locale.getDefault()));

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
            case R.id.mylearning_filter:
                break;

        }

        return super.onOptionsItemSelected(item);
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
            case R.id.card_view:
                Intent intentDetail = new Intent(context, ChatFragment.class);
                intentDetail.putExtra("peopleListingModel", peopleListingModelList.get(position));
                peopleListingModelList.get(position).chatCount = 0;
                startActivity(intentDetail);
                break;
            default:
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        chatMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

}