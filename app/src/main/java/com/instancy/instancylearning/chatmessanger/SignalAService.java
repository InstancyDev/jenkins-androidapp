package com.instancy.instancylearning.chatmessanger;

import android.content.Context;
import android.content.OperationApplicationException;
import android.util.Log;
import android.widget.Toast;

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

    Communicator communicator;

    PreferencesManager preferencesManager;

    private static SignalAService chatService;

    private SignalAService(Context context) {
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        name = preferencesManager.getStringValue(StaticValues.KEY_USERNAME);
        try {
            initConnection();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

//        try {
//            communicator = (Communicator) context.getApplicationContext();
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.getApplicationContext()
//                    + " must implement Communicator");
//        }
    }

    public static SignalAService newInstance(Context context) {
        if (chatService == null) {
            chatService = new SignalAService(context);
        }
        return chatService;
    }

    private void initConnection() throws OperationApplicationException {

        String url = appUserModel.getSiteURL();

        con = new HubConnection(url.toString(), context, new LongPollingTransport()) {

            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                super.OnStateChanged(oldState, newState);

                switch (newState.getState()) {
                    case Connected:
                        Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
                        connectionID = con.getConnectionId();
                        loginMethod();
                        break;
                    case Disconnected:
                        Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }

            }
        };

        hub = con.CreateHubProxy("chat");


        hub.On("SendPrivateMessage", new HubOnDataCallback() {
            @Override
            public void OnReceived(JSONArray args) {

                Log.d(TAG, "OnReceived: " + args);
                communicator.messageRecieved(args);
            }
        });
    }

    public void loginMethod() {

        JSONObject jsonObject = new JSONObject();

        JSONArray array = new JSONArray();
        try {
            jsonObject.put("ChatuserName", name);
            jsonObject.put("ChatSiteID", appUserModel.getSiteIDValue());
            jsonObject.put("ChatUserID", appUserModel.getUserIDValue());
            jsonObject.put("ChatProfilepath", "content/sitefiles/profile.jpg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(jsonObject);

        HubInvokeCallback callback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
                Log.d(TAG, "OnResult: HubInvokeCallback  " + response);
                preferencesManager.setStringValue(response, StaticValues.CHAT_LIST);
            }

            @Override
            public void OnError(Exception ex) {
                Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnError: HubInvokeCallback  " + ex.getMessage());
            }
        };
        hub.Invoke("Login", array, callback);
    }

    public void startSignalA() {
        if (con != null)
            con.Start();
    }

    public void stopSignalA() {
        if (con != null)
            con.Stop();
    }

    public void sendMessage(String buddyID, String messageStr) {

        HubInvokeCallback callback = new HubInvokeCallback() {
            @Override
            public void OnResult(boolean succeeded, String response) {
//                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnResult: HubInvokeCallback sendMessage  " + response);

            }

            @Override
            public void OnError(Exception ex) {
                Toast.makeText(context, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnError: HubInvokeCallback  sendMessage " + ex.getMessage());
            }
        };
        List<String> args = new ArrayList<String>(2);
        args.add(buddyID);
        args.add(messageStr);
        args.add("true");
        args.add("");

        hub.Invoke("SendPrivateMessage", args, callback);
    }


}
