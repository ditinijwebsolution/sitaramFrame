package com.om.sitaramfrem.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.activities.ActivityLogin;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.ValidationUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = PushNotificationService.class.getSimpleName();

    private static final String BROKEN_CHANNEL_ID = "SitaramFrem"; //It older name.
    public PushNotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
       // removeBrokenChannel(this);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    private String getNotificationChannelId() {
        return getString(R.string.notification_channel_id);
    }

    private String getNotificationChannelName() {
        return getString(R.string.notification_channel_name);
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if (notificationManager != null) {
                    List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

                    for (int i = 0; channelList != null && i < channelList.size(); i++) {
                        notificationManager.deleteNotificationChannel(channelList.get(i).getId());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                String channelId = getNotificationChannelId();
                String channelName = getNotificationChannelName();

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                NotificationChannel mNotificationChannel = new NotificationChannel(channelId, channelName, importance);
                mNotificationChannel.setSound(getUriForSoundName(getApplicationContext()), audioAttributes);
                notificationManager.createNotificationChannel(mNotificationChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private Uri getUriForSoundName(@NonNull Context context) {
        int resourceId = R.raw.notification_sound;
        return new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(getResources().getResourcePackageName(resourceId))
                .appendPath(getResources().getResourceTypeName(resourceId))
                .appendPath(getResources().getResourceEntryName(resourceId))
                .build();
        /*return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName()
                + "/raw/" + R.raw.notification_sound);*/
    }

    public int createNotificationId() {
        int notificationId = 0;

        try {
            //notificationId = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(new Date()));
            String mill = new SimpleDateFormat("ddHHmmssSSS", Locale.US).format(new Date());
            mill = mill.substring(mill.length() - 5);
            PrintLog.e(TAG, "Notification ID " + mill);
            notificationId = Integer.parseInt(mill);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notificationId;
    }

    public static void removeNotification(Context mContext, int notificationId) {
        try {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeAllNotifications(Context mContext) {
        try {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Handle received push notification message to display push notification in notification bar.
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {
            if (remoteMessage != null) {
                int notificationId = createNotificationId();

                PendingIntent pendingIntent = null;
                    /*Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher);*/

                String title = "";
                String message = "";
                String orderId = "";

                if (remoteMessage.getNotification() != null) {
                    if (ValidationUtil.validateString(remoteMessage.getNotification().getTitle())) {
                        title = remoteMessage.getNotification().getTitle();
                    }

                    if (ValidationUtil.validateString(remoteMessage.getNotification().getBody())) {
                        message = remoteMessage.getNotification().getBody();
                    }
                }
               /* if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {

                    JSONObject json = new JSONObject(remoteMessage.getData());

                    if (ValidationUtil.validateString(json.getString("title"))) {
                        title = json.getString("title");
                        PrintLog.e(TAG, "Notification title :- " + title);
                    }

                    if (ValidationUtil.validateString(json.getString("body"))) {
                        message = json.getString("body");
                    }

                    if (ValidationUtil.validateString(json.getString("order_id"))) {
                        orderId = json.getString("order_id");
                    }
                }*/

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, getNotificationChannelId())
                                .setSmallIcon(R.drawable.ic_small_notification)
                                .setColor(ContextCompat.getColor(this, R.color.white))
                                .setContentTitle(title)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setDefaults(Notification.DEFAULT_LIGHTS)
                                .setSound(getUriForSoundName(getApplicationContext()));

                pendingIntent = getNotificationPendingIntent(this);
                if (pendingIntent != null) {
                    notificationBuilder.setContentIntent(pendingIntent);
                }

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                createNotificationChannel(notificationManager);
                notificationManager.notify(notificationId, notificationBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getNotificationPendingIntent(Context mContext) {
        PendingIntent pendingIntent = null;

        try {


            Intent mIntent = new Intent(this, ActivityLogin.class);
            // mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            //pendingIntent = PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_ONE_SHOT);
            //pendingIntent = PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity
                        (this, 0, mIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity
                        (this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pendingIntent;
    }

    private void removeBrokenChannel(Context context){
        NotificationManagerCompat.from(context)
                .deleteNotificationChannel(BROKEN_CHANNEL_ID);
    }
}
