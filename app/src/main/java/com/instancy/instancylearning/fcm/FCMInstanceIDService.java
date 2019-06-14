package com.instancy.instancylearning.fcm;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import java.io.IOException;

/**
 * Created by Upendranath on 3/21/2018.
 */

public class FCMInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FCMInstanceIDService";
    PreferencesManager preferencesManager;
    private Context context;

    @Override
    public void onTokenRefresh() {
        context = this;
        preferencesManager = PreferencesManager.getInstance();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token string: " + refreshedToken);

        if (preferencesManager != null)
            preferencesManager.setStringValue(refreshedToken, StaticValues.FCM_KEY);
        else
            Log.d(TAG, "onTokenRefresh: Refreshed not updated ");

    }
}
