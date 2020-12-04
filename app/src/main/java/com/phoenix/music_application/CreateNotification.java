package com.phoenix.music_application;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class CreateNotification {
    public static final String Channel_ID="Channel_1";
    public static final String Action_Previous="action_previous";
    public static final String Action_Play="action_play";
    public static final String Action_Next="action_next";
    public static Notification notification;

    public static void createNotification(Context context, Audio track, int playbutton, int pos, int size, Bitmap icon){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
            //Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.album_art_default);

            //create Notification
            notification = new NotificationCompat.Builder(context, Channel_ID)
                    .setSmallIcon(R.drawable.library_music)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getArtist())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    /*
                    .setStyle(new Notification.MediaStyle()
                            .setShowActionsInCompactView( 0,1,2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))

                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    */
                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}