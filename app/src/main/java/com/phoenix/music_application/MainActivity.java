package com.phoenix.music_application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;

import com.developer.filepicker.view.FilePickerDialog;
import com.gauravk.audiovisualizer.*;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.gauravk.audiovisualizer.visualizer.HiFiVisualizer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button playBtton;
    SeekBar start, end;
    TextView startText;
    TextView endText;
    TextView songNameTextView;
    TextView artistNameTextView;
    static MediaPlayer song;
    ImageView settingsIcon;
    ImageView imageView;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    ImageView addToFavs;
    Animation animation;
    ArrayList<File> songsList;
    ArrayList<Audio> audioList;
    int SongTotalTime;
    Uri uri;
    Cursor cursor = null;
    private MediaPlayerService player;
    boolean serviceBound = false;


    public static final String Broadcast_PLAY_NEW_AUDIO = "com.phoenix.music_application.PlayNewAudio";


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //To hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");
//        ArrayList<String> s;
//        s = scanDeviceForMp3Files();
        if (song != null) {
            song.stop();
            song.release();

        }

        //Id implementation
        songNameTextView = findViewById(R.id.TextTitle);
        artistNameTextView = findViewById(R.id.TextName);
        settingsIcon = (ImageView) findViewById(R.id.settingsIcon);
        playBtton = findViewById(R.id.play);
        addToFavs = findViewById(R.id.add_to_favs_btn);
        startText = findViewById(R.id.TextStart);
        endText = findViewById(R.id.TextEnd);
        imageView = findViewById(R.id.img);
        animation = AnimationUtils.loadAnimation(this, R.anim.rotation);

        Intent i = getIntent();
//        Bundle bundle = getIntent().getExtras();
//        assert bundle != null;

        try {
            songsList = PreferencesConfig.readFromPref(this);

            //songsList = (ArrayList) i.getStringArrayListExtra("songList");
            int pos = i.getIntExtra("songIndex", 0);
            String songPath = songsList.get(pos).toString();

            uri = Uri.parse(songPath);
            Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);
            intent.putExtra("song", songsList.get(pos).getAbsolutePath());

            startService(intent);
            MediaMetadataRetriever metaRetriver;
            metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(getApplicationContext(), uri);
            String a = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String c = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String b = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            songNameTextView.setText(a);
            artistNameTextView.setText(c);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        if (songsList != null) {
//           // song = MediaPlayer.create(this, uri);
//
//        }
//        //Song Added
//        else {
//            song = MediaPlayer.create(this, R.raw.song);
//        }
        //song.setLooping(true);

//        song.seekTo(0);
//        song.setVolume(0.5f, 0.5f);
//        SongTotalTime = song.getDuration();
//        song.start();


        //Control Seek bar track line / play line
        start = findViewById(R.id.PlayLine);
        start.setMax(SongTotalTime);
//        start.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    song.seekTo(progress);
//                    start.setProgress(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });


        //Switching to settings
        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, settingsActivity.class);
                startActivity(intent);
            }
        });

        //Volume control


        //Up date song time line
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (song != null) {
//                    try {
//                        Message message = new Message();
//                        message.what = song.getCurrentPosition();
//                        handler.sendMessage(message);
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ignored) {
//
//                    }
//                }
//            }
//        }).start();


        //loadAudio();
        //play the first audio in the ArrayList
        //playAudio(audioList.get(0).);
//        for(Audio a:audioList) {
//            Log.i("name",a.getTitle());
//        }


    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(song !=null) {
//            song.stop();
//            song.release();
//
//        }


//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @SuppressLint("SetTextI18n")
//        @Override
//        public void handleMessage(Message message) {
//            int SeekBarPosition = message.what;
//            //Update song seek bar
//            start.setProgress(SeekBarPosition);
//
//            //Update Labels
//            String Time = createTimeText(SeekBarPosition);
//            startText.setText(Time);
//
//            //Time calculation
//            String remainingTime = createTimeText(SongTotalTime - SeekBarPosition);
//            endText.setText("- " + remainingTime);
//        }
//    };

    //Time Shows
    public String createTimeText(int time) {
        String timeText;
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        timeText = min + ":";
        if (sec < 10) timeText += "0";
        timeText += sec;
        return timeText;
    }

    int index = 0;

    public void PlayButton(View view) {
//        if(index==0) {
//            while(index<audioList.size())
//            playAudio(index);
//            index++;
//        }
//        Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//        sendBroadcast(broadcastIntent);

        if (!MediaPlayerService.isplaying) {
            //playAudio(0);
            //Stopped
            //song.start();
            //Rotation start
            //Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);
            MediaPlayerService.play();
            //stopService(intent);
            imageView.startAnimation(animation);
            playBtton.animate().rotation(-180);
            playBtton.clearAnimation();
            playBtton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
        } else {
            //Played
            //Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);
            //stopService(intent);
            MediaPlayerService.pause();
            imageView.clearAnimation();
            playBtton.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
            playBtton.animate().rotation(0);
            //playBtton.clearAnimation();

        }
    }

    boolean likeButtonpress = false;

    public void addToFavsBtnPressed(View view) {
        if (!likeButtonpress) {
            likeButtonpress = true;
            addToFavs.setImageResource(R.drawable.ic_favoritepressed_24);

        } else {
            likeButtonpress = false;
            addToFavs.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

    }

    public void openPlayList(View view) {
        startActivity(new Intent(this, PlayListActivity.class));
    }


    //getting audio files from a folder

    // @RequiresApi(api = Build.VERSION_CODES.Q)
    private ArrayList<String> scanDeviceForMp3Files() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,

        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        ArrayList<String> mp3Files = new ArrayList<>();

        Uri uri;
        try {
            if (settingsActivity.folderPath == null) {
                uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            } else {
                uri = Uri.fromFile(new File(settingsActivity.folderPath));
            }
            cursor = getContentResolver().query(uri, projection, selection, null, sortOrder);
            if (cursor != null) {
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    String displayName = cursor.getString(3);
                    String songDuration = cursor.getString(4);
                    cursor.moveToNext();
                    if (path != null && path.endsWith(".mp3")) {
                        mp3Files.add(path);

                    }
                }

            }

            // print to see list of mp3 files
            for (String file : mp3Files) {
                Log.i("TAG", file);

            }

        } catch (Exception e) {
            Log.e("TAG", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return mp3Files;
    }
}


//    //Binding this Client to the AudioPlayer Service
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
//            player = binder.getService();
//            serviceBound = true;
//
//            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            serviceBound = false;
//        }
//    };

//    private void playAudio(int audioIndex) {
//        //Check is service is active
//        if (!serviceBound) {
//            //Store Serializable audioList to SharedPreferences
//            StorageUtil storage = new StorageUtil(getApplicationContext());
//            storage.storeAudio(audioList);
//            storage.storeAudioIndex(audioIndex);
//
//            Intent playerIntent = new Intent(this, MediaPlayerService.class);
//            startService(playerIntent);
//            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        } else {
//            //Store the new audioIndex to SharedPreferences
//            StorageUtil storage = new StorageUtil(getApplicationContext());
//            storage.storeAudioIndex(audioIndex);
//
//            //Service is active
//            //Send a broadcast to the service -> PLAY_NEW_AUDIO
//            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
//            sendBroadcast(broadcastIntent);
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putBoolean("ServiceState", serviceBound);
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        serviceBound = savedInstanceState.getBoolean("ServiceState");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (serviceBound) {
//            unbindService(serviceConnection);
//            //service is active
//            player.stopSelf();
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    private void loadAudio() {
//        ContentResolver contentResolver = getContentResolver();
//
//        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
//        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
////        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
//        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
//
//        if (cursor != null && cursor.getCount() > 0) {
//            audioList = new ArrayList<>();
//            while (cursor.moveToNext()) {
//                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//
//                // Save to audioList
//                audioList.add(new Audio(data, title, album, artist));
//            }
//        }
//        assert cursor != null;
//        cursor.close();
//    }
//
//
//}
