package com.phoenix.music_application;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

//import android.support.v7.app.NotificationCompat;

public class MediaPlayerService extends Service {

    static MediaPlayer mediaPlayer;
    private static Handler mHandler = new Handler();
    String songToPlay;
    // private final Handler mHandler = new Handler();
    private final IBinder binder = new LocalBinder();
    public static boolean isplaying = false;
    //private IBinder mBinder = new IBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "--service created--", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "--service started--", Toast.LENGTH_SHORT).show();
        songToPlay = intent.getStringExtra("song");

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

        //  Log.i("timer","finished");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "--service destroyed--", Toast.LENGTH_SHORT).show();
        stopSelf();
        //mediaPlayer.stop();
        isplaying = false;
        //mediaPlayer.release();
    }


    public static Thread performOnBackgroundThread(final int i) {
        final Thread t = new Thread() {
            @Override
            public void run() {
//            mHandler.postDelayed(this,10000);
                try {
                    Thread.sleep(i * 60000);
//                Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!MediaPlayerService.isMediaPlayernull()) {
                    MediaPlayerService.pause();
                    isplaying = false;
                }


            }
        };
        t.start();
        return t;
    }

    public static boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public static void replaySong() {
        mediaPlayer.seekTo(0);
    }

    public static boolean isMediaPlayernull() {
        return mediaPlayer == null;
    }

    public static int getPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public static void seekto(int prog) {
        mediaPlayer.seekTo(prog);
    }

    public static int getSongDuration() {
        return mediaPlayer.getDuration();

    }

    public static int getAudioSession() {
        if (mediaPlayer != null) { return mediaPlayer.getAudioSessionId(); }
        else { return 0; }
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "service bound", Toast.LENGTH_SHORT).show();
        int time = intent.getIntExtra("sleepTimerDuration", Integer.MAX_VALUE);
        //Log.i("time",String.valueOf(time));
        //sleepTimer(time);
        performOnBackgroundThread(time);
        return null;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(getApplicationContext(), "service unbound", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        int time = intent.getIntExtra("sleepTimerDuration", Integer.MAX_VALUE);
        Toast.makeText(getApplicationContext(), "service bound", Toast.LENGTH_SHORT).show();
        //Log.i("time",String.valueOf(time));
        //sleepTimer(time);
        performOnBackgroundThread(time);

    }

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }


}