package com.instancy.instancylearning.chatmessanger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.HubResult;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;

import static com.instancy.instancylearning.utils.StaticValues.ACTION_INTENT_TEXT_MESSAGE_INCOMING;

/**
 * Created by gongguopei87@gmail.com on 2015/8/13.
 */
public class ChatService {

    HubConnection connection; //signal r
    HubProxy hub;
    SignalRFuture<Void> awaitConnection;
    String TAG = ChatService.class.getSimpleName();

    AppUserModel appUserModel;

    DemonThread thread;

    String connectionID = "-1";

    private Context context;

    String name = "";

    PreferencesManager preferencesManager;

    private static ChatService chatService;

    private ChatService(Context context) {
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        name = preferencesManager.getStringValue(StaticValues.KEY_USERNAME);
        initConnection();
    }

    public static ChatService newInstance(Context context) {
        if (chatService == null) {
            chatService = new ChatService(context);
        }
        return chatService;
    }

    private void initConnection() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        connection = new HubConnection(appUserModel.getSiteURL(), null, true, new Logger() {
            @Override
            public void log(String s, LogLevel logLevel) {

                Log.d(TAG, "log: here in   " + s);

                if (s.contains("ConnectionId:")) {

                    String array1[] = s.split("ConnectionId:");

                    Log.d(TAG, "log: ConnectionId result array  -------------- " + array1[1]);

                    Log.d(TAG, "log: ConnectionId getConnectionId -------------- " + connection.getConnectionId());

                }

                if (s.contains("Connected")) {
                    connectionID = connection.getConnectionId();
                    loginMethod();
                }

                if (s.contains("Trigger onData with data:")) {

                    String array1[] = s.split("Trigger onData with data:");

                    if (array1.length > 1) {

                        Log.d(TAG, "log: ConnectionId users List array  -------------- " + array1[1]);

                        try {
                            JSONObject jsonObject = new JSONObject(array1[1]);
                            Log.d(TAG, "log: ConnectionId users List jsonObject  -------------- " + jsonObject);

                            preferencesManager.setStringValue(array1[1], StaticValues.CHAT_LIST);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }

        });
        hub = connection.createHubProxy("chat");


        // toreceive from chatserver
        hub.on("SendPrivateMessage", new SubscriptionHandler2<String, String>() {
                    @Override
                    public void run(String msgType, String msg) {
//                        Intent intent = new Intent();
//                        intent.setAction(ACTION_INTENT_TEXT_MESSAGE_INCOMING);
//                        intent.putExtra("name", "server");
//                        intent.putExtra("body", msg);
//                        context.sendBroadcast(intent);
                        Log.d("SendPrivateMessage", "yes, it works " + msg);

                    }
                },
                String.class, String.class);

        awaitConnection = connection.start();

        thread = new DemonThread();
        thread.start();
        // https://stackoverflow.com/questions/32505390/signalr-integration-in-android-studio/


    }

    public void destroy() {
        if (thread != null) {
            thread.cancel();
            thread = null;
        }
        if (connection != null && connection.getState() == ConnectionState.Connected) {
            connection.stop();
        }
    }

    public void sendMessage(String buddyId, String message) {
        new MessageSendTask().execute(buddyId, message);
    }

    public void userCameOnline() {
//        new UserCameOnlineTask().execute()
    }

    public void loginMethod() {

        new loginTask().execute(name, appUserModel.getSiteIDValue(), "" + appUserModel.getUserIDValue(), "content/sitefiles/profile.jpg");
    }

    private class MessageSendTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            hub.invoke("SendPrivateMessage", params[0], params[1], true, "");

            return "Ok";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: MessageSendTask  " + s);
        }
    }

    private class loginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(final String... params) {

            JSONObject jsonObject = new JSONObject();


            try {
                jsonObject.put("ChatuserName", params[0]);
                jsonObject.put("ChatSiteID", params[1]);
                jsonObject.put("ChatUserID", params[2]);
                jsonObject.put("ChatProfilepath", params[3]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            hub.invoke("Login", jsonObject);

            return "Ok";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: loginTask  " + s);

        }
    }

    private class UserCameOnlineTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            hub.invoke("OtherNewUserLogin", params[0], params[1], true, "");

            return "Ok";
        }
    }


    private class DemonThread extends Thread {

        boolean isRunning = true;

        @Override
        public void run() {
            while (isRunning) {
                try {
                    awaitConnection.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            awaitConnection.cancel();
        }

        public void cancel() {
            if (isRunning) {
                isRunning = false;
            }
        }
    }

    public static JSONObject decodeResult(Object result) throws JSONException {
        if (result == null)
            return null;

        Gson gson = new GsonBuilder().serializeNulls().create();//Fix the missing null attributes problem
        return new JSONObject(gson.toJson(result));
    }

}
