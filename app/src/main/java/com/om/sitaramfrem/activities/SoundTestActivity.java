package com.om.sitaramfrem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.om.sitaramfrem.R;
import com.om.sitaramfrem.databinding.ActivityHomeBinding;
import com.om.sitaramfrem.databinding.ActivitySoundTestBinding;

import java.util.List;

public class SoundTestActivity extends AppCompatActivity {

    private ActivitySoundTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoundTestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNotification();
            }
        });
    }

    private void createNotification(){
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, binding.mEdtChannelId.getText().toString())
                        .setSmallIcon(R.drawable.ic_small_notification)
                        .setColor(ContextCompat.getColor(this, R.color.white))
                        .setContentTitle(binding.mEdtTitle.getText().toString())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(binding.mEdtDesc.getText().toString()))
                        .setContentText(binding.mEdtDesc.getText().toString())
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_LIGHTS);

        if(!binding.mRbDefault.isChecked()){
           notificationBuilder.setSound(getUriForSoundName());
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(notificationManager);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private Uri getUriForSoundName() {
        int resourceId = 0;
        if(binding.mRbCartoon.isChecked()){
            resourceId = R.raw.cartoonsms;
        }else if(binding.mRbClient.isChecked()){
            resourceId = R.raw.notification_sound;
        }
        if(resourceId!=0) {
            return new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(getResources().getResourcePackageName(resourceId))
                    .appendPath(getResources().getResourceTypeName(resourceId))
                    .appendPath(getResources().getResourceEntryName(resourceId))
                    .build();
        }else{
            return null;
        }
        /*return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName()
                + "/raw/" + R.raw.notification_sound);*/
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
                String channelId = binding.mEdtChannelId.getText().toString();
                String channelName = binding.mEdtChannelName.getText().toString();

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                NotificationChannel mNotificationChannel = new NotificationChannel(channelId, channelName, importance);
                if(!binding.mRbDefault.isChecked()) {
                    mNotificationChannel.setSound(getUriForSoundName(), audioAttributes);
                }
                notificationManager.createNotificationChannel(mNotificationChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}