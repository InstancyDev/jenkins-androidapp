package com.instancy.instancylearning.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.mainactivities.Splash_activity;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Upendranath on 3/21/2018.
 */

public class FCMMessagingService extends FirebaseMessagingService {


    private static final String TAG = "FCMMessagingService";
    PreferencesManager preferencesManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        preferencesManager = PreferencesManager.getInstance();

        Log.d("PUSH", remoteMessage.getData().toString());

        Map<String, String> params = remoteMessage.getData();
        if (params != null) {
            JSONObject jsonObject = new JSONObject(params);
            String mTitle = jsonObject.optString("title");
            String mBody = jsonObject.optString("body");
            //Calling method to generate notification

            if (mBody.length() > 0) {
                sendNotification(jsonObject, mTitle, mBody);
            } else {
                sendNotification(jsonObject, jsonObject.optString("title"), jsonObject.optString("Message "));
            }

//            {"Message":"","siteid":"374","forumid":"","contentid":"42","contextmenuid":"5"}
        }
    }

    private void sendNotification(JSONObject jsonObject, String mTitle, String mBody) {

        String userID = preferencesManager.getStringValue(StaticValues.KEY_USERID);

        Intent intent;
// The id of the channel.
        String channelID = "instancychanel";

        if (userID != null && !userID.equalsIgnoreCase("")) {
            intent = new Intent(this, SideMenu.class);
            intent.putExtra("PUSH", true);
            intent.putExtra(StaticValues.FCM_OBJECT, (Serializable) jsonObject.toString());

        } else {
            intent = new Intent(this, Splash_activity.class);
        }

        PendingIntent pendingIntent = TaskStackBuilder.create(this)
                // add all of SecondActvity's parents to the stack,
                // followed by SecondActvity itself
                .addNextIntentWithParentStack(intent)
                .addParentStack(SideMenu.class)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mTitle)
                .setContentText(mBody)
                .setAutoCancel(true).setWhen(0)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mBody))
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
//            notificationBuilder.setColor(getResources().getColor(R.color.colorInGreen));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelID, mTitle, importance);
//            notificationManager.createNotificationChannel(mChannel);
        }
    }
}