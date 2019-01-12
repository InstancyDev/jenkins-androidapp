package com.instancy.instancylearning.chatmessanger;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.instancy.instancylearning.interfaces.Communicator;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;


/**
 * Created by gongguopei87@gmail.com on 2015/8/13.
 */
public class SignalAServiceMicrosoft {

    String TAG = SignalAServiceMicrosoft.class.getSimpleName();

    protected HubConnection con = null;


    protected HubProxy hub = null;

    AppUserModel appUserModel;

    String connectionID = "-1";

    private Context context;

    String name = "";

    public Communicator communicator;

    PreferencesManager preferencesManager;

    private static SignalAServiceMicrosoft chatService;

    private SignalAServiceMicrosoft(Context context) {
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        name = preferencesManager.getStringValue(StaticValues.KEY_USERNAME);
        initConnection();
    }

    public static SignalAServiceMicrosoft newInstance(Context context) {
        if (chatService == null) {
            chatService = new SignalAServiceMicrosoft(context);
        }
        return chatService;
    }

    private void initConnection() {
//        String url = "http://yournextuapi.instancysoft.com/signalr/"; //appUserModel.getWebAPIUrl();

        String url = "http://192.168.11.162/WebApi/signalr/";
//        String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
//
//        headers.put("Authorization", "Basic " + base64EncodedCredentials);

        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                String base64EncodedCredentials = Base64.encodeToString("A459QN8BU4:jTV1fyibJgicZtGfZy7EMKOYk67I1GhvgJqgrHMr".getBytes(), Base64.NO_WRAP);
                request.addHeader("Authorization", "basic " + base64EncodedCredentials);
            }
        };


        con = new HubConnection(url);
        con.setCredentials(credentials);
        con.connected(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run:  Called " + con.getConnectionData());
                connectionID = con.getConnectionId();
                loginMethod();
//                hub.invoke("SendToAll").onError(new ErrorCallback() {
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Log.d(TAG, "onError: Hello Error");
//                    }
//                }).done(new Action<Void>() {
//                    @Override
//                    public void run(Void aVoid) throws Exception {
//                        Log.d("<Debug", "Hello Void."); // Works fine
//                    }
//                });
            }
        });
        hub = con.createHubProxy("chathub");

        ClientTransport clientTransport = new ServerSentEventsTransport((con.getLogger()));
        SignalRFuture<Void> signalRFuture = con.start(clientTransport);

        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("SimpleSignalR", e.toString());
            return;
        }

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
            jsonObject.put("ConnectionId", connectionID);
            jsonObject.put("ConnectionAcceptID", -1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(jsonObject);//OtherOnlineUser
        hub.invoke("otherOnlineUser", array).done(new Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                Log.d("<Debug", "message sent.");
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                Log.d("<onError", "OtherOnlineUser onError sent.");

            }
        });
    }

    //
//    void testMethod() {
//
//        JSONObject jsonObject = new JSONObject();
//
//        JSONArray array = new JSONArray();
//        try {
//
//            jsonObject.put("name", connectionID);
//            jsonObject.put("message", -1);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        array.put(jsonObject);
//        HubInvokeCallback callback = new HubInvokeCallback() {
//            @Override
//            public void OnResult(boolean succeeded, String response) {
////                Log.d(TAG, "OnResult: HubInvokeCallback  " + response);
//                preferencesManager.setStringValue(response, StaticValues.CHAT_LIST);
//            }
//
//            @Override
//            public void OnError(Exception ex) {
////                Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "OnError: HubInvokeCallback  " + ex.getMessage());
//            }
//        };
//        hub.Invoke("SendToAll", array, callback);
//    }
//
//
    public void startSignalA() {
        if (con != null)
            con.start();
    }
//
//    public void stopSignalA() {
//        if (con != null)
//            con.Stop();
//    }
//
//    public void sendMessage(String buddyID, String messageStr, String attachemnt) {
//
//        HubInvokeCallback callback = new HubInvokeCallback() {
//            @Override
//            public void OnResult(boolean succeeded, String response) {
////                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
////                Log.d(TAG, "OnResult: HubInvokeCallback sendMessage  " + response);
//
//            }
//
//            @Override
//            public void OnError(Exception ex) {
//                //   Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
////                Log.d(TAG, "OnError: HubInvokeCallback  sendMessage " + ex.getMessage());
//            }
//        };
//        List<String> args = new ArrayList<String>(2);
//        args.add(buddyID);
//        args.add(messageStr);
//        args.add("true");
//        args.add(attachemnt);
//
//        hub.Invoke("SendPrivateMessage", args, callback);
//    }
}
