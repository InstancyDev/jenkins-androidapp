package com.instancy.instancylearning.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

/**
 * Created by Upendranath on 3/21/2018.
 */

public class FCMInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FCMInstanceIDService";

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token string: " + refreshedToken);

    }
}
