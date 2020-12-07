package com.phoenix.music_application;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.phoenix.music_application.Services.NotificationActionServices;

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

            //------------------------------------------------------------------------------------------------------------------------------//

            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (pos == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context, NotificationActionServices.class)
                        .setAction(Action_Previous);
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_previous = R.drawable.previous_song;
            }

            //------------------------------------------------------------------------------------------------------------------------------//

            Intent intentPlay = new Intent(context, NotificationActionServices.class)
                    .setAction(Action_Play);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            //------------------------------------------------------------------------------------------------------------------------------//

            PendingIntent pendingIntentNext;
            int drw_next;
            if (pos == size) {
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionServices.class)
                        .setAction(Action_Next);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.next_song;
            }

            //------------------------------------------------------------------------------------------------------------------------------//

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
                    */
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))

                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(playbutton, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)

                    .build();

            notificationManagerCompat.notify(1, notification);
        }
    }
}