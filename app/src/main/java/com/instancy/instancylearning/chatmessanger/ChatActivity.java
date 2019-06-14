package com.instancy.instancylearning.chatmessanger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.instancy.instancylearning.askexpertenached.BasicAuthInterceptor;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.interfaces.Service;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.getFileNameFromPath;
import static com.instancy.instancylearning.utils.Utilities.getMimeTypeFromUri;
import static com.instancy.instancylearning.utils.Utilities.getRealPathFromURI;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.toRequestBody;

/**
 * Created by Upendranath on 5/19/2017.
 * https://github.com/timigod/android-chat-ui
 * https://blog.sendbird.com/android-chat-tutorial-building-a-messaging-ui
 */

public class ChatActivity extends AppCompatActivity {


    String TAG = ChatActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    PreferencesManager preferencesManager;
    PeopleListingModel peopleListingModel;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;
    SignalAService signalAService;
    Button btnSent, btnAttachment;
    EditText messageEdit;
    // Chat Integration here
    private RecyclerView mMessageRecycler;
    private ChatListAdapter chatListAdapter;
    private List<BaseMessage> mMessageList;
    Communicator communicator;
    private int GALLERY = 1;
    Service service;
    String finalfileName = "", finalPath = "";
    Uri contentURIFinal;

    public ChatActivity() {


    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, ChatActivity.this);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_activity);
        appUserModel = AppUserModel.getInstance();
//        appUserModel.setAuthHeaders("A459QN8BU4:jTV1fyibJgicZtGfZy7EMKOYk67I1GhvgJqgrHMr");
//        appUserModel.setWebAPIUrl("http://192.168.11.162/WebApi/");
        svProgressHUD = new SVProgressHUD(this);

        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        peopleListingModel = new PeopleListingModel();

        peopleListingModel = (PeopleListingModel) getIntent().getSerializableExtra("peopleListingModel");

        btnSent = (Button) findViewById(R.id.button_chatbox_send);
        btnAttachment = (Button) findViewById(R.id.button_attachment);
//        btnAttachment.setVisibility(View.GONE);
        chatInitilization();
        btnSent.setEnabled(false);

        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEdit.getText().toString().trim();

                sendMessageToServer(message, peopleListingModel);

            }
        });

        btnAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                choosePhotoFromGallary();
            }
        });


        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.button_chatbox_send), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.button_attachment), iconFont);


        initilizeView();
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                peopleListingModel.userDisplayname + "</font>"));

        getSupportActionBar().setSubtitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                peopleListingModel.chatUserStatus + "</font>"));


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

            Toast.makeText(this, JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.network_alerttitle_nointernet, this), Toast.LENGTH_SHORT).show();

        }

        mMessageList = new ArrayList<>();

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        chatListAdapter = new ChatListAdapter(this, mMessageList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(linearLayoutManager);

        mMessageRecycler.setAdapter(chatListAdapter);

        linearLayoutManager.setStackFromEnd(true);

        // chat load methods

        refreshPeopleListing(true);


        // Multipart

        BasicAuthInterceptor interceptor = new BasicAuthInterceptor(appUserModel.getAuthHeaders());
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Change base URL to your upload server URL.
        service = new Retrofit.Builder().baseUrl(appUserModel.getWebAPIUrl() + "Chat/InsertUserChatData/").client(client).build().create(Service.class);

    }

    public void chatInitilization() {

        communicator = new Communicator() {
            @Override
            public void messageRecieved(JSONArray messageReceived) {

                try {
                    generateReceiveConversition(messageReceived);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void userOnline(int typeUpdate, JSONArray objReceived) {
                Log.d(TAG, "messageRecieved: " + objReceived);
            }
        };
        signalAService = SignalAService.newInstance(getApplicationContext());
        signalAService.communicator = communicator;
    }

    public void initilizeView() {

        messageEdit = (EditText) findViewById(R.id.edittext_chatbox);
        messageEdit.setHint(getLocalizationValue(JsonLocalekeys.message_typemessage_here));
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

                svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));

            }

            String countBool = "true";
            if (peopleListingModel.chatCount == 0) {
                countBool = "false";
            } else {
                countBool = "true";
            }
            String paramsString = "fromuserid=" + appUserModel.getUserIDValue() + "&touserid=" + peopleListingModel.userID + "&msg=&markasread=" + countBool;

            vollyService.getJsonObjResponseVolley("CHATHISTORY", appUserModel.getWebAPIUrl() + "Chat/GetUserChatHistory?" + paramsString, appUserModel.getAuthHeaders());
        } else {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Chat fragment");
//        chatService.destroy();
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
//                Log.d(TAG, "Volley requester " + requestType);
//                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("CHATHISTORY")) {
                    if (response != null) {
                        try {
                            generateChatConversition(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                    }
                }

                svProgressHUD.dismiss();
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
//                Log.d(TAG, "Volley String post" + response);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("CHATHISTORY")) {
                    if (response != null) {

//                        Log.d(TAG, "notifySuccess: in  CHATHISTORY " + response);
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


    public void generateChatConversition(JSONObject jsonObject) throws JSONException {

        JSONArray chatHistoryTable = jsonObject.getJSONArray("Table1");
        mMessageList = new ArrayList<>();

        if (chatHistoryTable.length() > 0) {

            for (int i = 0; i < chatHistoryTable.length(); i++) {
                JSONObject singleChatObj = chatHistoryTable.getJSONObject(i);

                BaseMessage baseMessage = new BaseMessage();

                if (singleChatObj.has("ChatID")) {
                    baseMessage.chatID = singleChatObj.getString("ChatID");
                }

                if (singleChatObj.has("FromUserID")) {
                    baseMessage.fromUserID = singleChatObj.getString("FromUserID");
                }

                if (singleChatObj.has("ToUserID")) {
                    baseMessage.toUserID = singleChatObj.getString("ToUserID");
                }

                if (singleChatObj.has("Message")) {
                    baseMessage.messageChat = singleChatObj.getString("Message");
                }

                if (singleChatObj.has("Attachment")) {
                    baseMessage.attachemnt = singleChatObj.getString("Attachment");
                }

                if (singleChatObj.has("MarkAsRead")) {
                    baseMessage.markAsRead = singleChatObj.getString("MarkAsRead");
                }

                if (singleChatObj.has("FromStatus")) {
                    baseMessage.fromStatus = singleChatObj.getString("FromStatus");
                }

                if (singleChatObj.has("ToStatus")) {
                    baseMessage.toStatus = singleChatObj.getString("ToStatus");
                }

                if (singleChatObj.has("FromUserName")) {
                    baseMessage.fromUserName = singleChatObj.getString("FromUserName");
                }


                if (singleChatObj.has("ToUserName")) {
                    baseMessage.toUsername = singleChatObj.getString("ToUserName");
                }
                if (singleChatObj.has("ProfPic")) {
                    baseMessage.profilePic = singleChatObj.getString("ProfPic");
                }

                if (singleChatObj.has("SendDateTime")) {

                    baseMessage.sentDate = formatDate(singleChatObj.getString("SendDateTime"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
                }

                if (appUserModel.getUserIDValue().equalsIgnoreCase(baseMessage.fromUserID)) {
                    baseMessage.itsMe = true;
                } else {
                    baseMessage.itsMe = false;
                }

                mMessageList.add(baseMessage);
            }
            chatListAdapter.reloadAllContent(mMessageList);
        }

    }

    public void generateReceiveConversition(JSONArray jsonArray) throws JSONException {

        //     {"ReceiverConnectionID":"b7ee996f-78d2-4047-81b4-3bd9cb1cf6ec","Message":"test hi","AttachmentName":"","AttachmentPath":"","ReceiverUserID":1,"FromMobile":false,"SenderConnectionID":"4104582b-364b-49bb-b6cc-b003c4396c53","SenderFullName":"Richard Parker","SenderProfPic":"\/Content\/SiteFiles\/374\/ProfileImages\/8_Will Smith.jpg","SenderUserID":8,"SentDateTime":"2019-01-16T16:27:40"}

        JSONObject jsonObject = jsonArray.getJSONObject(0);

        if (jsonObject != null) {

            BaseMessage baseMessage = new BaseMessage();

            baseMessage.chatID = peopleListingModel.chatConnectionUserId;

            baseMessage.fromUserID = appUserModel.getUserIDValue();

            baseMessage.toUserID = peopleListingModel.userID;

            baseMessage.messageChat = jsonObject.optString("Message");

            baseMessage.attachemnt = jsonObject.optString("AttachmentPath");

            baseMessage.markAsRead = "true";

            baseMessage.fromUserName = jsonObject.optString("SenderFullName");

            baseMessage.toUsername = appUserModel.getUserName();

            baseMessage.profilePic = peopleListingModel.memberProfileImage;

            baseMessage.sentDate = formatDate(jsonObject.optString("SentDateTime"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

//            baseMessage.sentDate = jsonObject.optString("SentDateTime");

            baseMessage.itsMe = false;

            mMessageList.add(baseMessage);
        }
        chatListAdapter.reloadAllContent(mMessageList);
        mMessageRecycler.scrollToPosition(chatListAdapter.getItemCount() - 1);
    }

    public void sendMessageToServer(String messageStr, PeopleListingModel peopleListingModel) {

        String dateString = getCurrentDateTime("yyyy-MM-dd HH:mm:ss aa");

        Map<String, RequestBody> parameters = new HashMap<String, RequestBody>();
        parameters.put("FromUserID", toRequestBody(appUserModel.getUserIDValue()));
        parameters.put("ToUserID", toRequestBody(peopleListingModel.userID));
        parameters.put("Message", toRequestBody(messageStr));
        parameters.put("SendDateTime", toRequestBody(dateString));
        parameters.put("MarkAsRead", toRequestBody("false"));

        if (isNetworkConnectionAvailable(this, -1)) {

            uploadFileThroughMultiPart(parameters, contentURIFinal);

        } else {
            Toast.makeText(this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }

    }

    public void generateNewConversation(PeopleListingModel peopleListingModel, String message, String attachment) {
        messageEdit.setText("");
        String dateString = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

        BaseMessage baseMessage = new BaseMessage();

        baseMessage.chatID = peopleListingModel.chatConnectionUserId;

        baseMessage.fromUserID = appUserModel.getUserIDValue();

        baseMessage.toUserID = peopleListingModel.userID;

        baseMessage.messageChat = message;

        baseMessage.attachemnt = attachment;

        baseMessage.markAsRead = "false";

        baseMessage.fromStatus = "";

        baseMessage.toStatus = "";

        baseMessage.fromUserName = "";

        baseMessage.toUsername = "";

        baseMessage.profilePic = peopleListingModel.memberProfileImage;

        baseMessage.sentDate = dateString;


        if (appUserModel.getUserIDValue().equalsIgnoreCase(baseMessage.fromUserID)) {
            baseMessage.itsMe = true;
        } else {
            baseMessage.itsMe = false;
        }

        mMessageList.add(baseMessage);

        chatListAdapter.reloadAllContent(mMessageList);
        mMessageRecycler.scrollToPosition(chatListAdapter.getItemCount() - 1);
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first: ");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    final String fileName = getFileNameFromPath(contentURI, this);
//                final String mimeType = getMimeTypeFromUri(contentURI);
                    Log.d(TAG, "onActivityResult: " + fileName);
                    finalPath = getRealPathFromURI(ChatActivity.this, contentURI);
                    contentURIFinal = contentURI;
                    finalfileName = fileName;
                    sendMessageToServer("", peopleListingModel);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ChatActivity.this, getLocalizationValue(JsonLocalekeys.asktheexpert_labelfailed), Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private void uploadFileThroughMultiPart(Map<String, RequestBody> parameters, Uri fileUri) {
        // create upload service client

        MultipartBody.Part body = null;
        if (finalfileName.length() > 0 && fileUri != null) {
            File file = new File(finalPath);

            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(getContentResolver().getType(fileUri)),
                            file
                    );

            // MultipartBody.Part is used to send also the actual file name
            body = MultipartBody.Part.createFormData("Image", file.getName(), requestFile);

        }

        // finally, execute the request
//        Call<ResponseBody> call = service.upload(description, body);
        Call<ResponseBody> call = service.uploadFileWithPartMap(parameters, body);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.v("Upload", "success");
                signalAService.sendMessage(peopleListingModel, messageEdit.getText().toString(), finalfileName);
                contentURIFinal = null;
                finalfileName = "";
                String responseRecievedPath = null;
                try {
                    responseRecievedPath = response.body().string();
                    Log.d(TAG, "onResponse: " + responseRecievedPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                responseRecievedPath = responseRecievedPath.replace("\"", "");
                generateNewConversation(peopleListingModel, messageEdit.getText().toString(), responseRecievedPath);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(ChatActivity.this, "Message cannot be posted. Contact site admin.", Toast.LENGTH_SHORT).show();
                svProgressHUD.dismiss();
            }
        });
    }


}

