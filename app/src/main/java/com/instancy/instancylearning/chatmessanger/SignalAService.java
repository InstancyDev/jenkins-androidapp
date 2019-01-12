package com.instancy.instancylearning.chatmessanger;

import android.content.Context;
import android.content.OperationApplicationException;

import android.util.Base64;
import android.util.Log;


import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.models.AppUserModel;
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

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gongguopei87@gmail.com on 2015/8/13.
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
//        appUserModel.setAuthHeaders("A459QN8BU4:jTV1fyibJgicZtGfZy7EMKOYk67I1GhvgJqgrHMr");
//        appUserModel.setWebAPIUrl("http://192.168.11.162/WebApi/");
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
        //    String url = "http://yournextuapi.instancysoft.com/signalr/"; //appUserModel.getWebAPIUrl();

        String url = "http://192.168.11.162/WebApi/signalr/";

//        String base64EncodedCredentials = Base64.encodeToString("A459QN8BU4:jTV1fyibJgicZtGfZy7EMKOYk67I1GhvgJqgrHMr".getBytes(), Base64.NO_WRAP);

        String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);

        con = new HubConnection(url, context, new LongPollingTransport()) {

            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                super.OnStateChanged(oldState, newState);

                switch (newState.getState()) {
                    case Connected:
//                        Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                        connectionID = con.getConnectionId();
                        loginMethod();
//                        testMethod();
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
        hub.On("SendPrivateMessage", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                communicator.messageRecieved(args);
            }


        });

        hub.On("NewOnlineUser", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                Log.d(TAG, "OnReceived:  NewOnlineUser" + args);

            }

        });

        hub.On("UpdateOnlineUserList", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                Log.d(TAG, "OnReceived: UpdateOnlineUserList " + args);
                if (args != null) {
                    communicator.userOnline(true,args);
                }
            }
        });
        hub.On("StatusChanged", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {
                Log.d(TAG, "OnReceived: StatusChanged " + args);
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

    void testMethod() {

        JSONObject jsonObject = new JSONObject();

        JSONArray array = new JSONArray();
        try {

            jsonObject.put("name", connectionID);
            jsonObject.put("message", -1);

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
        hub.Invoke("SendToAll", array, callback);
    }


    public void startSignalA() {
        if (con != null)
            con.Start();
    }

    public void stopSignalA() {
        if (con != null)
            con.Stop();
    }

    public void sendMessage(String buddyID, String messageStr, String attachemnt) {

        HubInvokeCallback callback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
//                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "OnResult: HubInvokeCallback sendMessage  " + response);

            }

            @Override
            public void OnError(Exception ex) {
                //   Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "OnError: HubInvokeCallback  sendMessage " + ex.getMessage());
            }
        };
        List<String> args = new ArrayList<String>(2);
        args.add(buddyID);
        args.add(messageStr);
        args.add("true");
        args.add(attachemnt);

        hub.Invoke("SendPrivateMessage", args, callback);
    }

    public void newOnlineUser(String buddyID, String messageStr, String attachemnt) {

        HubInvokeCallback callback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
//                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "OnResult: HubInvokeCallback sendMessage  " + response);

            }

            @Override
            public void OnError(Exception ex) {
                //   Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "OnError: HubInvokeCallback  sendMessage " + ex.getMessage());
            }
        };
        List<String> args = new ArrayList<String>(2);
        args.add(buddyID);
        args.add(messageStr);
        args.add("true");
        args.add(attachemnt);

        hub.Invoke("NewOnlineUser", args, callback);
    }

}
