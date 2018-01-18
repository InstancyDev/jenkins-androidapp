package com.instancy.instancylearning.chatmessanger;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.peoplelisting.PeopleProfileExpandAdapter;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 5/19/2017.
 * https://github.com/timigod/android-chat-ui
 * https://blog.sendbird.com/android-chat-tutorial-building-a-messaging-ui
 */

public class ChatFragment extends AppCompatActivity {


    String TAG = ChatFragment.class.getSimpleName();

    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    PreferencesManager preferencesManager;

    PeopleListingModel peopleListingModel;
    TextView userName, userLocation;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    PeopleProfileExpandAdapter profileDynamicAdapter;

    Button btnSent;

    EditText messageEdit;

    // Chat Integration here

    private static final int TOTAL_MESSAGES_COUNT = 100;

    private RecyclerView mMessageRecycler;

    private MessageListAdapter mMessageAdapter;

    private List<BaseMessage> mMessageList;

    public ChatFragment() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_activity);
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(this);

        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        vollyService = new VollyService(resultCallback, this);

        peopleListingModel = new PeopleListingModel();

        peopleListingModel = (PeopleListingModel) getIntent().getSerializableExtra("peopleListingModel");

        btnSent = (Button) findViewById(R.id.button_chatbox_send);

        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.button_chatbox_send), iconFont);

        initilizeView();
        initVolleyCallback();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                peopleListingModel.userDisplayname + "</font>"));

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace(); //
        }


        if (isNetworkConnectionAvailable(this, -1)) {


        } else {

            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }

        mMessageList = new ArrayList<>();


        for (int i = 0; i < 10; i++) {
            BaseMessage baseMessage = new BaseMessage();

            if (i % 2 == 0) {
                baseMessage.userID = "1";
                baseMessage.description = "Give me some description";
                baseMessage.companyName = "Me";
            } else {
                baseMessage.userID = "2";
                baseMessage.description = "No Yar please come here i will give you full information ok ritht";
                baseMessage.companyName = "" + peopleListingModel.userDisplayname;
            }
            mMessageList.add(baseMessage);
        }

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, mMessageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        // chat load methods

        refreshPeopleListing(true);
    }


    public void initilizeView() {

        messageEdit = (EditText) findViewById(R.id.edittext_chatbox);

        messageEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    btnSent.setTextColor(getResources().getColor(R.color.colorStatusOther));
                    btnSent.setEnabled(true);

                } else {
                    btnSent.setTextColor(getResources().getColor(R.color.colorDarkGrey));
                    btnSent.setEnabled(false);
                }
            }
        });


    }


    public void refreshPeopleListing(Boolean isFirstLoad) {
        if (isNetworkConnectionAvailable(this, -1)) {
//            peopleListingTabsEitherFromDatabaseOrAPI();
            if (isFirstLoad) {

                svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));

            }

            String paramsString = "fromuserid=" + appUserModel.getUserIDValue()  + "&touserid=" +peopleListingModel.connectionUserID + "&msg=&markasread=true";

            vollyService.getJsonObjResponseVolley("CHATHISTORY", appUserModel.getWebAPIUrl() + "/Chat/GetUserChatHistory?" + paramsString, appUserModel.getAuthHeaders());
        } else {

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Chat fragment");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void initVolleyCallback() {

        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("CHATHISTORY")) {
                    if (response != null) {


                    } else {
                    }
                }


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
                if (requestType.equalsIgnoreCase("CHATHISTORY")) {
                    if (response != null) {

                        Log.d(TAG, "notifySuccess: in  CHATHISTORY " + response);
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

}

