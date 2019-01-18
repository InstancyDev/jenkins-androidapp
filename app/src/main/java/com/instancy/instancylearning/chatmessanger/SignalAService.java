package com.instancy.instancylearning.chatmessanger;

import android.content.Context;
import android.content.OperationApplicationException;

import android.util.Base64;
import android.util.Log;


import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.zsoft.signala.hubs.HubConnection;
import com.zsoft.signala.hubs.HubInvokeCallback;
import com.zsoft.signala.hubs.HubOnDataCallback;
import com.zsoft.signala.hubs.IHubProxy;
import com.zsoft.signala.transport.StateBase;
import com.zsoft.signala.transport.longpolling.LongPollingTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Upendranath on 7/18/2017
 */
public class SignalAService {

    //   https://github.com/erizet/SignalA refer here
    String TAG = SignalAService.class.getSimpleName();

    protected HubConnection con = null;


    protected IHubProxy hub = null;

    AppUserModel appUserModel;

    String connectionID = "-1";

    private Context context;

    String name = "";

    public Communicator communicator;

    PreferencesManager preferencesManager;

    private static SignalAService chatService;

    private SignalAService(Context context) {
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        name = preferencesManager.getStringValue(StaticValues.KEY_USERNAME);
        try {
            try {
                initConnection();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    public static SignalAService newInstance(Context context) {
        if (chatService == null) {
            chatService = new SignalAService(context);
        }
        return chatService;
    }

    private void initConnection() throws OperationApplicationException {

        String url = appUserModel.getWebAPIUrl().replace("api/", "signalr/");

        String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);

        //   String url = "http://192.168.11.162/WebApi/signalr/";

        //    String base64EncodedCredentials = Base64.encodeToString("A459QN8BU4:jTV1fyibJgicZtGfZy7EMKOYk67I1GhvgJqgrHMr".getBytes(), Base64.NO_WRAP);


        con = new HubConnection(url, context, new LongPollingTransport()) {

            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                super.OnStateChanged(oldState, newState);

                switch (newState.getState()) {
                    case Connected:
//                        Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                        connectionID = con.getConnectionId();
                        loginMethod();
                        break;
                    case Disconnected:
//                        Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }

            }

        };

        hub = con.CreateHubProxy("ChatHub");
        con.addHeader("Authorization", "basic " + base64EncodedCredentials);
//        hub.On("SendPrivateMessage", new HubOnDataCallback() {
//            @Override
//            public void OnReceived(JSONArray args) {
//                communicator.messageRecieved(args);
//            }
//
//
//        });

        hub.On("ReceievePrivateMessage", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                communicator.messageRecieved(args);
            }

        });

        hub.On("NewOnlineUser", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                Log.d(TAG, "OnReceived:  NewOnlineUser" + args);
                communicator.userOnline(1, args);

            }

        });

        hub.On("UpdateOnlineUserList", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                Log.d(TAG, "OnReceived: UpdateOnlineUserList " + args);
                if (args != null) {
                    communicator.userOnline(2, args);
                }
            }
        });
        hub.On("StatusChanged", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                Log.d(TAG, "OnReceived: StatusChanged " + args);
                if (args != null) {
                    communicator.userOnline(3, args);
                }
            }
        });
    }
    void loginMethod() {

        JSONObject jsonObject = new JSONObject();

        JSONArray array = new JSONArray();
        try {

            jsonObject.put("UserID", appUserModel.getUserIDValue());
            jsonObject.put("FullName", name);
            jsonObject.put("ProfPic", "content/sitefiles/profile.jpg");
            jsonObject.put("SiteID", appUserModel.getSiteIDValue());
            jsonObject.put("isOnline", true);
            jsonObject.put("ChatUserID", appUserModel.getUserIDValue());
            jsonObject.put("ConnectionId", connectionID);
            jsonObject.put("ConnectionAcceptID", -1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(jsonObject);
        HubInvokeCallback callback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
//                Log.d(TAG, "OnResult: HubInvokeCallback  " + response);
                // preferencesManager.setStringValue(response, StaticValues.CHAT_LIST);
            }

            @Override
            public void OnError(Exception ex) {
//                Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnError: HubInvokeCallback  " + ex.getMessage());
            }
        };
        hub.Invoke("otherOnlineUser", array, callback);
    }

    void logOutMethod() {
        JSONObject jsonObject = new JSONObject();

        JSONArray array = new JSONArray();
        try {

            jsonObject.put("UserID", appUserModel.getUserIDValue());
            jsonObject.put("FullName", name);
            jsonObject.put("ProfPic", "content/sitefiles/profile.jpg");
            jsonObject.put("SiteID", appUserModel.getSiteIDValue());
            jsonObject.put("isOnline", true);
            jsonObject.put("ChatUserID", appUserModel.getUserIDValue());
            jsonObject.put("ConnectionId", connectionID);
            jsonObject.put("ConnectionAcceptID", -1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(jsonObject);
        HubInvokeCallback callback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
//                Log.d(TAG, "OnResult: HubInvokeCallback  " + response);
                preferencesManager.setStringValue(response, StaticValues.CHAT_LIST);
            }

            @Override
            public void OnError(Exception ex) {
//                Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnError: HubInvokeCallback  " + ex.getMessage());
            }
        };
        hub.Invoke("StatusChanged", array, callback);
    }

    public void startSignalA() {
        if (con != null)
            con.Start();
    }

    public void stopSignalA() {
        if (con != null) {
            logOutMethod();
            //     con.Stop();
        }
    }

    public void sendMessage(PeopleListingModel peopleListingModel, String messageStr, String attachemnt) {

        JSONObject jsonObject = new JSONObject();

        JSONArray array = new JSONArray();
        try {

            jsonObject.put("ReceiverConnectionID", peopleListingModel.chatConnectionUserId);
            jsonObject.put("Message", messageStr);
            jsonObject.put("AttachmentName", attachemnt);
            jsonObject.put("AttachmentPath", peopleListingModel.attachmentPath);
            jsonObject.put("ReceiverUserID", peopleListingModel.userID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(jsonObject);

        HubInvokeCallback callback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                Log.d(TAG, "OnResult: HubInvokeCallback sendMessage  " + response);

            }

            @Override
            public void OnError(Exception ex) {
                //   Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnError: HubInvokeCallback  sendMessage " + ex.getMessage());
            }
        };

        hub.Invoke("SendPrivateMessage", array, callback);
    }

}
