package com.instancy.instancylearning.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.mainactivities.Login_activity;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONObject;

import java.util.Map;

//https://www.codementor.io/flame3/send-push-notifications-to-android-with-firebase-du10860kb
//https://www.simplifiedcoding.net/firebase-cloud-messaging-android/

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    private String mTitle;
    private String mBody;
    PreferencesManager preferencesManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> params = remoteMessage.getData();
        if (params != null) {
            JSONObject jsonObject = new JSONObject(params);
            mTitle = jsonObject.optString("title");
            mBody = jsonObject.optString("body");
            //Calling method to generate notification
            sendNotification(jsonObject, mTitle, mBody);

        }
    }

    //    private void showNativeNotification() {
//        PendingIntent pendingIntent;
//
//        Intent intent = new Intent(this, SplashScreen.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(mTitle)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                .setContentText(mBody)
//                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(mTitle))
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(mBody))
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(Utils.getNotificationId(), notificationBuilder.build());
//    }
    private void sendNotification(JSONObject jsonObject, String mTitle, String mBody) {

        String link = jsonObject.optString("link");
        Intent intent;


        String userID = PreferencesManager.getInstance().getStringValue(StaticValues.KEY_USERID);

        if (userID != null && !userID.equalsIgnoreCase("")) {

            PreferencesManager.getInstance().setStringValue("true", StaticValues.KEY_HIDE_ANNOTATION);
             intent = new Intent(this, SideMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else {
             intent = new Intent(this, Login_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        PendingIntent pendingIntent = TaskStackBuilder.create(this)
                // add all of SecondActvity's parents to the stack,
                // followed by SecondActvity itself
                .addNextIntentWithParentStack(intent)
                .addParentStack(SideMenu.class)
                .getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);


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

        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher));

        ;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}