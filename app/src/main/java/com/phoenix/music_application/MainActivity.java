package com.phoenix.music_application;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.chibde.visualizer.LineVisualizer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    Button playBtton;
    Button skipToNextBtn, skipToPrevButton;
    public SeekBar start;
    public TextView startText;
    public TextView endText;
    public TextView songNameTextView;
    public TextView artistNameTextView;
    Button lyricsIcon;
    Button settingsIcon;
    CardView cardView;
    ImageView vinylArt;
    private Button libraryButton;
    Animation animation;
    ArrayList<Audio> songsList;
    int SongTotalTime = 0;
    Uri uri;
    public String songName, artistName = null;
    static int pos;
    boolean isLyricsVisible = false;

    SharedPreferences sharedPreferences;
    Animation fade_out, fade_in;
    ImageView albumArt;
    LineVisualizer lineVisualizer;
    ObjectAnimator rotateCard;

    //for album art
    MediaMetadataRetriever metaRetriver;
    byte[] art;

    //for lyrics
    DownloadLyrics downloadLyrics;
    public TextView lyricsView;
    ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    String fullURL;

    String apiKey = "&apikey=d56948ee4bc42159f087b0f2a1c07ae6";
    String baseURL = "https://api.musixmatch.com/ws/1.1/matcher.lyrics.get";

    //public static final String Broadcast_PLAY_NEW_AUDIO = "com.phoenix.music_application.PlayNewAudio";


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
        metaRetriver = new MediaMetadataRetriever();

        //Id implementation
        songNameTextView = findViewById(R.id.songTitle);
        albumArt = findViewById(R.id.albumArt_image);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        lyricsView = (TextView) findViewById(R.id.lyricsView);
        artistNameTextView = findViewById(R.id.artistName);
        lyricsIcon = findViewById(R.id.lyricsButton);
        playBtton = findViewById(R.id.playPauseButton);
        settingsIcon= findViewById(R.id.settingsButton);
        skipToNextBtn = findViewById(R.id.nextButton);
        skipToPrevButton = findViewById(R.id.previousButton);
        startText = findViewById(R.id.runningTime);
        endText = findViewById(R.id.totalTime);
        cardView = findViewById(R.id.albumArt_cardView);
        animation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        lineVisualizer = findViewById(R.id.lineViz);
        vinylArt = findViewById(R.id.albumArt_vinylArt);
        start = findViewById(R.id.seeker);

        //switch from main to lib--
        libraryButton= (Button) findViewById(R.id.libraryButton);
        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openlibActivity();
            }
        });

        //rotate animation for album art
        rotateCard = ObjectAnimator.ofFloat(cardView, "rotation", 0, 360);
        rotateCard.setInterpolator(new LinearInterpolator());
        rotateCard.setDuration(2500);
        rotateCard.setRepeatCount(ObjectAnimator.INFINITE);
        rotateCard.setRepeatMode(ObjectAnimator.RESTART);

        //line visualizer attributes
        lineVisualizer.setStrokeWidth(2);
        lineVisualizer.setColor(ContextCompat.getColor(this, R.color.design_default_color_secondary));

        Intent i = getIntent();


        try {

//            if(songsList==null)
            songsList = PreferencesConfig.readFromPref(this);


            int p = LoadInt(getApplicationContext(), "position");
            pos = i.getIntExtra("songIndex", p);

            SaveInt(getApplicationContext(), "position", pos);
            String songPath = songsList.get(pos).getPath();
            metaRetriver.setDataSource(songPath);
            art = metaRetriver.getEmbeddedPicture();

            uri = Uri.parse(songPath);
            Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);
            intent.putExtra("song", songPath);


            albumArt.setBackgroundColor(Color.GRAY);
            String title = songsList.get(pos).getTitle();

            String artist = songsList.get(pos).getArtist();

            String duration = songsList.get(pos).getDuration();
            startService(intent);

            //set icon to pause
            playBtton.setBackgroundResource(R.drawable.pausebutton_icon);

            //initialize visualizer and albumArt animation
            cardView.setRadius(1000);
            vinylArt.setVisibility(View.VISIBLE);

            rotateCard.start();

            songNameTextView.setText(title);
            songName = title;
            artistName = artist;

            artistNameTextView.setText(artist);
            SongTotalTime = Integer.parseInt(duration);
            endText.setText(createTimeText(SongTotalTime));

            lineVisualizer.setPlayer(MediaPlayerService.getAudioSession());

            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            albumArt.setImageBitmap(songImage);
        } catch (Exception e) {
            albumArt.setBackgroundColor(Color.GRAY);

            //artistNameTextView.setText("Unknown Artist");

            e.printStackTrace();
        }

        //Control Seek bar track line / play line
        start = findViewById(R.id.seeker);
        if (SongTotalTime != 0) {
            start.setMax(SongTotalTime);
        }


        start.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!MediaPlayerService.isMediaPlayernull() && fromUser) {

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

        //skip to next song button onclick method
        skipToNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos < songsList.size()-1) {
                    pos++;

                } else pos = 0;
                Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);

                intent.putExtra("song", songsList.get(pos).getPath());
                albumArt.setImageResource(R.drawable.album_art_default);
                String title = songsList.get(pos).getTitle();

                String artist = songsList.get(pos).getArtist();

                String duration = songsList.get(pos).getDuration();
                metaRetriver.setDataSource(songsList.get(pos).getPath());

                //album art implementation
                try {
                    art = metaRetriver.getEmbeddedPicture();
                    Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                    albumArt.setImageBitmap(songImage);
                } catch (Exception e) {
                    albumArt.setBackgroundColor(Color.GRAY);
                }
//
                startService(intent);

                songNameTextView.setText(title);
                songName = title;

                artistNameTextView.setText(artist);
                SongTotalTime = Integer.parseInt(duration);
                endText.setText(createTimeText(SongTotalTime));
                start.setMax(SongTotalTime);
            }
        });


        //skip to previous song
        skipToPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos > 0) {
                    pos--;

                } else pos = 0;
                Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);

                intent.putExtra("song", songsList.get(pos).getPath());
                albumArt.setImageResource(R.drawable.album_art_default);
                String title = songsList.get(pos).getTitle();

                String artist = songsList.get(pos).getArtist();

                String duration = songsList.get(pos).getDuration();
                metaRetriver.setDataSource(songsList.get(pos).getPath());

                //album art implementation
                try {
                    art = metaRetriver.getEmbeddedPicture();
                    Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                    albumArt.setImageBitmap(songImage);
                } catch (Exception e) {
                    albumArt.setBackgroundColor(Color.GRAY);
                }
//
                startService(intent);

                songNameTextView.setText(title);
                songName = title;

                artistNameTextView.setText(artist);
                SongTotalTime = Integer.parseInt(duration);
                endText.setText(createTimeText(SongTotalTime));
                start.setMax(SongTotalTime);
            }
        });


        //lyrics button onclick method
        lyricsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isLyricsVisible) {
                    try {
                        String encodedString = URLEncoder.encode(songName, "UTF-8");
                        String encodedArtistName = URLEncoder.encode(artistName, "UTF-8");

                        fullURL = baseURL + "?q_track=" + encodedString + "&q_artist=" + encodedArtistName + apiKey;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //the textview which is supposed to be displaying lyrics
                    cardView.setAlpha((float) 0.3);
                    lyricsView.setVisibility(View.VISIBLE);
                    lyricsView.setMovementMethod(new ScrollingMovementMethod());
                    isLyricsVisible = true;
                    downloadLyrics = new DownloadLyrics(MainActivity.this);        //asynctask class
                    downloadLyrics.execute(fullURL);

                }
                else if (isLyricsVisible) {
                    cardView.setAlpha(1);
                    lyricsView.setVisibility(View.INVISIBLE);
                    isLyricsVisible = false;

                }
            }
        });
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (!MediaPlayerService.isMediaPlayernull()) {
                    int mCurrentPosition = MediaPlayerService.getPosition();
                    startText.setText(createTimeText(mCurrentPosition));
                    start.setProgress(mCurrentPosition);


                    //to play next song automatically after completion of current
                    //same code as skipToNextBtn
                    if (mCurrentPosition >= MediaPlayerService.getSongDuration()) {
                        if (pos < songsList.size() - 1) {
                            pos++;

                        } else pos = 0;
                        Intent intent = new Intent(MainActivity.this, MediaPlayerService.class);

                        intent.putExtra("song", songsList.get(pos).getPath());


                        albumArt.setImageResource(R.drawable.album_art_default);
                        String title = songsList.get(pos).getTitle();

                        String artist = songsList.get(pos).getArtist();

                        String duration = songsList.get(pos).getDuration();
                        metaRetriver.setDataSource(songsList.get(pos).getPath());
                        SongTotalTime = Integer.parseInt(duration);
                        start.setMax(SongTotalTime);

                        //album art implementation
                        try {
                            art = metaRetriver.getEmbeddedPicture();
                            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                            albumArt.setImageBitmap(songImage);
                        } catch (Exception e) {
                            albumArt.setBackgroundColor(Color.GRAY);
                        }
//
                        startService(intent);

                        songNameTextView.setText(title);
                        songName = title;

                        artistNameTextView.setText(artist);
                        SongTotalTime = Integer.parseInt(duration);
                        endText.setText(createTimeText(SongTotalTime));

                    }
                }

                mHandler.postDelayed(this, 1000);
            }
        });


    }


    private final Handler mHandler = new Handler();
//Make sure you update Seekbar on UI thread


    //Time Shows
    public String createTimeText(int time) {

        int sec = time / 1000 % 60;

        if (sec < 10) {
            return (time / 1000 / 60) + ":0" + sec;
        }
        else { return (time / 1000 / 60) + ":" + sec; }
    }

    public void PlayButton(View view) {


        if (!MediaPlayerService.isplaying) {

            MediaPlayerService.play();

            //change icon to pause
            playBtton.startAnimation(fade_out);
            playBtton.setBackgroundResource(R.drawable.pausebutton_icon);
            playBtton.startAnimation(fade_in);

            //initialize audio visualization
            lineVisualizer.setPlayer(MediaPlayerService.getAudioSession());

            //circular album art with centered vinyl graphic
            cardView.setRadius(1000);
            vinylArt.setVisibility(View.VISIBLE);

            //start rotate animation
            if(MediaPlayerService.getPosition() == 0) { rotateCard.start(); }
            else { rotateCard.resume(); }
        }
        else {

            MediaPlayerService.pause();

            //change icon to play
            playBtton.startAnimation(fade_out);
            playBtton.setBackgroundResource(R.drawable.playbutton_icon);
            playBtton.startAnimation(fade_in);

            //pause rotate animation
            rotateCard.pause();
        }
    }

    //public void openPlayList(View view) { startActivity(new Intent(this, PlayListActivity.class)); }

    public void SaveInt(Context context, String key, int value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int LoadInt(Context context, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, 0);
    }

    //Getting permissions from user
    //used EasyPermissions library, you can check it out here - https://codinginflow.com/tutorials/android/easypermissions
    //It is simpler representation to request permissions, easier to understand
    @AfterPermissionGranted(1)
    private void getPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            int DEFAULT_PERMISSIONS_REQ_CODE = 1;
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

    public void openlibActivity(){
        Intent intent = new Intent(this, libActivity.class);
        startActivity(intent);
    }
}

//LET THIS CODE BE HERE, I THINK WE WILL NEED IT LATER


/*    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
//        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));
            }
        }
        assert cursor != null;
        cursor.close();
    }


}*/
