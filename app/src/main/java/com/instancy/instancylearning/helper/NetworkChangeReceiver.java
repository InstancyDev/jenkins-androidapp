package com.instancy.instancylearning.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.instancy.instancylearning.asynchtask.CmiSynchTask;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 8/16/2017 Working on Instancy-Playground-Android.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    String TAG = NetworkChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (isNetworkConnectionAvailable(context, -1)) {
                Log.e(TAG, "Conectivity Success !!! ");
                CmiSynchTask cmiSynchTask = new CmiSynchTask(context);
                cmiSynchTask.execute();
            } else {
                Log.e(TAG, "Conectivity Failure !!! ");

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}
