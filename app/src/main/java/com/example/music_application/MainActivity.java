package com.example.music_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button playBtton;
    SeekBar start, end;
    TextView startText, endText;
    MediaPlayer song;
    ImageView imageView;
    Animation animation;
    int SongTotalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //To hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Id implementation
        playBtton = findViewById(R.id.play);
        startText = findViewById(R.id.TextStart);
        endText = findViewById(R.id.TextEnd);
        imageView = findViewById(R.id.img);
        animation = AnimationUtils.loadAnimation(this, R.anim.rotation);

        //Song Added
        song = MediaPlayer.create(this, R.raw.song);
        song.setLooping(true);
        song.seekTo(0);
        song.setVolume(0.5f, 0.5f);
        SongTotalTime = song.getDuration();

        //Control Seek bar track line / play line
        start = findViewById(R.id.PlayLine);
        start.setMax(SongTotalTime);
        start.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    song.seekTo(progress);
                    start.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Volume control
        end = findViewById(R.id.volume);
        end.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                song.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Up date song time line
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (song != null) {
                    try {
                        Message message = new Message();
                        message.what = song.getCurrentPosition();
                        handler.sendMessage(message);
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }).start();

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message message) {
            int SeekBarPosition = message.what;
            //Update song seek bar
            start.setProgress(SeekBarPosition);

            //Update Labels
            String Time = createTimeText(SeekBarPosition);
            startText.setText(Time);

            //Time calculation
            String remainingTime = createTimeText(SongTotalTime - SeekBarPosition);
            endText.setText("- " + remainingTime);
        }
    };

    //Time Shows
    public String createTimeText(int time){
        String timeText;
        int min =  time / 1000 / 60;
        int sec = time / 1000 % 60;
        timeText = min + ":";
        if (sec < 10 ) timeText += "0";
        timeText += sec;
        return timeText;
    }


    public void PlayButton(View view) {
        if (!song.isPlaying()) {
            //Stopped
            song.start();
            //Rotation start
            imageView.startAnimation(animation);
            playBtton.setBackgroundResource(R.drawable.ic_pause_black_24dp);
        }else {
            //Played
            song.pause();
            imageView.clearAnimation();
            playBtton.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

}
