package com.phoenix.music_application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    Button playBtton;
    public SeekBar start, end;
    public TextView startText;
    public TextView endText;
    public TextView songNameTextView;
    public TextView artistNameTextView;
    static MediaPlayer mediaPlayer;
    ImageView settingsIcon;
    ImageView imageView;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    ImageView addToFavs;
    Animation animation;
    ArrayList<Audio> songsList;
    ArrayList<Audio> audioList;
    int SongTotalTime = 0;
    Uri uri;
    Audio a;
    Cursor cursor = null;
    private MediaPlayerService player;
    boolean serviceBound = false;
    int time = 0;


    //public static final String Broadcast_PLAY_NEW_AUDIO = "com.phoenix.music_application.PlayNewAudio";


    private final int DEFAULT_PERMISSIONS_REQ_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //To hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();


        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();

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
        Log.e("artiust: ", "songPath");
        try {
            songsList = PreferencesConfig.readFromPref(this);

            //songsList = (ArrayList) i.getStringArrayListExtra("songList");
            int pos = i.getIntExtra("songIndex", 0);
            String songPath = songsList.get(pos).getPath();

            uri = Uri.parse(songPath);
            Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);
            intent.putExtra("song", songsList.get(pos).getPath());
            String title = songsList.get(pos).getTitle();

            String artist = songsList.get(pos).getArtist();

            String duration = songsList.get(pos).getDuration();
            startService(intent);

            songNameTextView.setText(title);
            artistNameTextView.setText(artist);
            SongTotalTime = Integer.parseInt(duration);
            endText.setText(createTimeText(SongTotalTime));
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
        //SongTotalTime = MediaPlayerService.getSongDuration();
        //Toast.makeText(this, SongTotalTime, Toast.LENGTH_SHORT).show();
        //endText.setText(SongTotalTime);
//        song.start();


        //Control Seek bar track line / play line
        start = findViewById(R.id.PlayLine);
        if (SongTotalTime != 0) {
            start.setMax(SongTotalTime);
        }
        start.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    start.setProgress(progress);
                    MediaPlayerService.seekto(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        Message message = new Message();

                        message.what = time;
                        //Log.i("pos", String.valueOf(MediaPlayerService.getposition()));
                        handler.sendMessage(message);
                        time += 1000;
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        }).start();


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
            String totalTime = createTimeText(SongTotalTime);
            startText.setText(Time);

            //Time calculation
            //String remainingTime = createTimeText(SongTotalTime - SeekBarPosition);
            endText.setText(totalTime);
        }
    };

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


    //Getting permissions from user
    //used Easypermissions library, you can check it out here - https://codinginflow.com/tutorials/android/easypermissions
    //It is simpler representation to request permissions, easier to understand
    @AfterPermissionGranted(1)
    private void getPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions to read songs from storage",
                    DEFAULT_PERMISSIONS_REQ_CODE, perms);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            getPermissions();
        }
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
