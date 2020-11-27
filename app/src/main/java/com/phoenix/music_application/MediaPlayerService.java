package com.phoenix.music_application;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

//import android.support.v7.app.NotificationCompat;

/**
 * Created by Valdio Veliu on 16-07-11.
 */
public class MediaPlayerService extends Service {





    static MediaPlayer mediaPlayer;
    String songToPlay;
    SeekBar seeker;
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

    public static int getAudioSession() {
        if (mediaPlayer != null) {
            return mediaPlayer.getAudioSessionId();
        }
        else {
            return 0;
        }
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
/*
    public void threadMethod() {

        //update playing-time
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mediaPlayer != null) {
                    try {
                        Message message = new Message();
                        message.what = mediaPlayer.getCurrentPosition();

                        handler.sendMessage(message);

                        Thread.sleep(1000);
                    }catch (InterruptedException ignored) {

                    }
                }
            }
        }).start();
    }

    //updating seek-bar and play-time
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextIl8n")

        @Override
        public void handleMessage(Message message) {
            int progress = message.what;
            seeker.setProgress(progress);

            playTime.setText(songTime(progress));
        }
    };
*/
    //return time-text string
    public String songTime(int time) {

        int sec = time / 1000 % 60;

        if(sec < 10) { return (time / 1000 / 60) + ":0" + sec; }
        else { return (time / 1000 / 60) + ":" + sec; }
    }

}