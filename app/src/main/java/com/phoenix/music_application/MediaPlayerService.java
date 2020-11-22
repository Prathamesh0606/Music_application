package com.phoenix.music_application;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
//import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Valdio Veliu on 16-07-11.
 */
public class MediaPlayerService extends Service {



//    public static final String ACTION_PLAY = "com.phoenix.music_application.ACTION_PLAY";
//    public static final String ACTION_PAUSE = "com.phoenix.music_application.ACTION_PAUSE";
//    public static final String ACTION_PREVIOUS = "com.phoenix.music_application.ACTION_PREVIOUS";
//    public static final String ACTION_NEXT = "com.phoenix.music_application.ACTION_NEXT";
//    public static final String ACTION_STOP = "com.phoenix.music_application.ACTION_STOP";

    static MediaPlayer mediaPlayer;
    String songToPlay;
    static int duration;
    public static boolean isplaying;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "--service created--", Toast.LENGTH_LONG).show();


        //Time Shows


    }

//    public String createTimeText(int time) {
//        String timeText;
//        int min = time / 1000 / 60;
//        int sec = time / 1000 % 60;
//        timeText = min + ":";
//        if (sec < 10) timeText += "0";
//        timeText += sec;
//        return timeText;
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "--service started--", Toast.LENGTH_LONG).show();
        songToPlay = intent.getStringExtra("song");
        //Uri uri = Uri.parse(songToPlay);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songToPlay);
            mediaPlayer.prepare();
            mediaPlayer.start();
            isplaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Service.START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "--service destroyed--", Toast.LENGTH_LONG).show();
        mediaPlayer.stop();
        isplaying = false;
        mediaPlayer.release();
    }

    public static boolean isMediaPlayernull() {
        if (mediaPlayer == null)
            return true;
        else return false;
    }

    public static int getposition() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        return currentPosition;
    }

    public static void seekto(int prog) {
        mediaPlayer.seekTo(prog);
    }

    public static int getSongDuration() {
        if (mediaPlayer != null) {
            duration = mediaPlayer.getDuration();

        } else {
            duration = 0;
        }
        return duration;
    }

    public static void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isplaying = false;
        }
    }

    public static void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isplaying = true;
        }
    }

    public static void skipToNext() {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //MediaSession


}