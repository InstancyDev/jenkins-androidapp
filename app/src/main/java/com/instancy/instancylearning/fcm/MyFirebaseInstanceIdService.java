package com.instancy.instancylearning.fcm;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

/**
 * Created by EswarLakshmi on 30/05/17.
 * AIzaSyCMsqVvFC7sWk_l0xwD6bLIc4jjbVIdU5w server key for fcm
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {


    private AppController aController = null;
    private String userid = null;
    private String TAG = MyFirebaseInstanceIdService.class.getSimpleName();
    private Context context;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token : " + refreshedToken);
        context = this;
        aController = AppController.getInstance();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        PreferencesManager.getInstance().setStringValue(refreshedToken, StaticValues.FCM_KEY);
    }

}
