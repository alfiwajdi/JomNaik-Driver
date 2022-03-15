package com.app.jomnaik.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.app.jomnaik.R;
import com.app.jomnaik.activities.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        SharedPreferences.Editor e = getSharedPreferences("token",MODE_PRIVATE).edit();
        e.putString("id",s);
        e.apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String body = remoteMessage.getNotification().getBody();
        String title = remoteMessage.getNotification().getTitle();
        show(body,title);
    }

    private void show(String msg, String title) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ONE_ID);
            builder.setSmallIcon(R.drawable.ic_logo).setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }
        else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_logo)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true);

            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(uri);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(pendingIntent);
            int mNotificationID = 001;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(mNotificationID, mBuilder.build());
        }
    }
}
