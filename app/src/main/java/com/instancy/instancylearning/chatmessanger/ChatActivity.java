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
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.getFileNameFromPath;
import static com.instancy.instancylearning.utils.Utilities.getMimeTypeFromUri;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

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
    TextView userName, userLocation;

    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    SignalAService signalAService;

    Button btnSent, btnAttachment;

    EditText messageEdit;

    // Chat Integration here

    private static final int TOTAL_MESSAGES_COUNT = 100;

    private RecyclerView mMessageRecycler;

    private ChatListAdapter chatListAdapter;

    private List<BaseMessage> mMessageList;

    Communicator communicator;

    private int GALLERY = 1;
    Bitmap bitmapAttachment = null;
    String endocedImageStr = "";

    public ChatActivity() {


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

                sendMessageToServer(message, peopleListingModel, "");

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

            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

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

                // Log.d(TAG, "messageRecieved: " + messageReceived);
            }
        };

        signalAService = SignalAService.newInstance(getApplicationContext());
        signalAService.communicator = communicator;
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

        if (jsonArray.length() > 0) {

            BaseMessage baseMessage = new BaseMessage();

            baseMessage.chatID = peopleListingModel.chatConnectionUserId;

            baseMessage.fromUserID = appUserModel.getUserIDValue();

            baseMessage.toUserID = peopleListingModel.userID;

            baseMessage.messageChat = jsonArray.getString(2);

            baseMessage.attachemnt = jsonArray.getString(5);

            baseMessage.markAsRead = "true";

            baseMessage.fromUserName = jsonArray.getString(1);

            baseMessage.toUsername = appUserModel.getUserName();

            baseMessage.profilePic = peopleListingModel.memberProfileImage;

            baseMessage.sentDate = jsonArray.getString(6);

            baseMessage.itsMe = false;

            mMessageList.add(baseMessage);
        }
        chatListAdapter.reloadAllContent(mMessageList);
        mMessageRecycler.scrollToPosition(chatListAdapter.getItemCount() - 1);
    }

    public void sendMessageToServer(String messageStr, PeopleListingModel peopleListingModel, String attachment) {

        String dateString = getCurrentDateTime("dd/MM/yyyy HH:mm:ss.SSS");
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("FromUserID", appUserModel.getUserIDValue());
            jsonObject.put("ToUserID", peopleListingModel.userID);
            jsonObject.put("Message", messageStr);
            jsonObject.put("Attachment", attachment);
            jsonObject.put("SendDateTime", dateString);
            jsonObject.put("MarkAsRead", "false");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String parameterString = jsonObject.toString();

        if (isNetworkConnectionAvailable(this, -1)) {

//            String replaceDataString = parameterString.replace("\"", "\\\"");
//            String addQuotes = ('"' + replaceDataString + '"');

            sendNewChatDataToServer(jsonObject.toString(), messageStr, attachment);
        } else {
            Toast.makeText(this, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }

    public void sendNewChatDataToServer(final String postData, final String message, final String attachment) {

        String urlString = appUserModel.getWebAPIUrl() + "/Chat/InsertChatObjectDetails";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
//                Log.d(TAG, "onResponse: " + s);

                if (s.contains("1")) {
                    signalAService.sendMessage("" + peopleListingModel.chatConnectionUserId, message, attachment);
                    generateNewConversation(peopleListingModel, message, attachment);
                    messageEdit.setText("");
                } else {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
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
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void generateNewConversation(PeopleListingModel peopleListingModel, String message, String attachment) {

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
                    final String mimeType = getMimeTypeFromUri(contentURI);
                    Log.d(TAG, "onActivityResult: " + fileName);
                    bitmapAttachment = bitmap;

                    new CountDownTimer(1000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            endocedImageStr = convertToBase64(bitmapAttachment);
                            try {
                                encodeAttachment(appUserModel.getUserIDValue(), peopleListingModel.userID, fileName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            uploadAttachmentToServer(fileName, bitmap, mimeType);

                        }
                    }.start();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();

                }
            }

        }

    }
//    https://www.survivingwithandroid.com/2013/05/android-http-downlod-upload-multipart.html

    private String convertToBase64(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);

        return encodedImage;
    }


//    public void uploadAttachmentToServerMultipart(String fileName, Bitmap bitmapAttachments, String mimeType) {
//        byte[] multipartBody = new byte[0];
//        String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
//        try {
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            if (bitmapAttachments != null) {
//                bitmapAttachments.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
//            }
//
//            byte[] fileByteArray = byteArrayOutputStream.toByteArray();
//
//            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream2);
//            try {
//                // the first file
//                buildPart(dataOutputStream, fileByteArray, fileName);
//                // send multipart form data necesssary after file data
//                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//                Map<String, String> params = new HashMap<>();
//                params.put("filename", fileName);
//                params.put("strSiteID", appUserModel.getSiteIDValue());
//                params.put("strfromUserId", appUserModel.getUserIDValue());
//                params.put("strtoUserId", peopleListingModel.userID);
////                params.put("fileData", fileByteArray.toString());
//                dataOutputStream.writeBytes(params.toString());
//                // pass to multipart body
//                multipartBody = byteArrayOutputStream2.toByteArray();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String url = appUserModel.getWebAPIUrl() + "/MobileLMS/UploadSendMessageAttachment";
//            MultipartRequest multipartRequest = new MultipartRequest(url, base64EncodedCredentials, mimeType, multipartBody, new Response.Listener<NetworkResponse>() {
//                @Override
//                public void onResponse(NetworkResponse response) {
//                    Toast.makeText(ChatActivity.this, "completed", Toast.LENGTH_SHORT).show();
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    try {
//                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            VolleySingleton.getInstance(ChatActivity.this).addToRequestQueue(multipartRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
//        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
////        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
////                + fileName + "\"" + lineEnd);
//        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; fileData=\""
//                + fileName + "\"" + lineEnd);
//
//        dataOutputStream.writeBytes(lineEnd);
//
//        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
//        int bytesAvailable = fileInputStream.available();
//
//        int maxBufferSize = 1024 * 1024;
//        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//        byte[] buffer = new byte[bufferSize];
//
//        // read file and write it into form...
//        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//        while (bytesRead > 0) {
//            dataOutputStream.write(buffer, 0, bufferSize);
//            bytesAvailable = fileInputStream.available();
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//        }
//
//        dataOutputStream.writeBytes(lineEnd);
//    }

    public void encodeAttachment(String fromUserId, String toUserID, String fileName) throws JSONException {

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        if (bitmapAttachment != null) {
            endocedImageStr = convertToBase64(bitmapAttachment);
        }

        if (endocedImageStr.length() < 10) {
            Toast.makeText(ChatActivity.this, "Invalid attached file", Toast.LENGTH_SHORT).show();
        } else {

            Log.d(TAG, "validateNewForumCreation: " + endocedImageStr);

            if (isNetworkConnectionAvailable(this, -1)) {

                String replaceDataString = endocedImageStr.replace("\"", "\\\"");
                String addQuotes = ('"' + replaceDataString + '"');
                sendChatAttachmentDataToServer(addQuotes, fromUserId, toUserID, fileName);

            } else {
                Toast.makeText(ChatActivity.this, "" + getResources().getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendChatAttachmentDataToServer(final String postData, String fromUserId, String toUserID, final String fileName) {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/UploadMessageAttachmentAndroid?fileName=" + fileName + "&strSiteID=" + appUserModel.getSiteIDValue() + "&strfromUserId=" + fromUserId + "&strtoUserId=" + toUserID;

        StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
                if (s.length() > 0) {

                    String attachmentStr = s.replaceAll("^\"|\"$", "");

                    sendMessageToServer(fileName, peopleListingModel, attachmentStr);

                } else {

                    Toast.makeText(ChatActivity.this, "Attachment cannot be posted. Contact site admin.", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ChatActivity.this, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
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

        RequestQueue rQueue = Volley.newRequestQueue(ChatActivity.this);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

}

