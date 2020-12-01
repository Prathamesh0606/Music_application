package com.phoenix.music_application;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

//import android.support.v7.app.NotificationCompat;

public class MediaPlayerService extends Service {

    static MediaPlayer mediaPlayer;
    String songToPlay;
    public static boolean isplaying;

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


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "--service destroyed--", Toast.LENGTH_SHORT).show();
        //mediaPlayer.stop();
        isplaying = false;
        //mediaPlayer.release();
    }

    public static boolean isMediaPlayernull() {
        return mediaPlayer == null;
    }

    public static int getPosition() { return mediaPlayer.getCurrentPosition(); }

    public static void seekto(int prog) { mediaPlayer.seekTo(prog); }

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
        return null;
    }



}