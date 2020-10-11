package com.phoenix.music_application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class LyricsActivity extends Activity {
    DownloadLyrics downloadLyrics;
    static ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    static TextView textView;
    String fullURL;
    String songName;
    String artistName;
    String apiKey = "&apikey=d56948ee4bc42159f087b0f2a1c07ae6";
    String baseURL = "https://api.musixmatch.com/ws/1.1/matcher.lyrics.get";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyrics_screen);
        songName = "SCOTTIE BEAM";
        artistName = "FREDDIE GIBBS";


        //Encode the songName string to UTF8 so that it can be understood by server as a query.
        //this wont work unless you do encoding
        try {
            String encodedString = URLEncoder.encode(songName, "UTF-8");
            String encodedArtistName = URLEncoder.encode(artistName, "UTF-8");
            Log.i("encoded", songName);
            fullURL = baseURL + "?q_track=" + encodedString + "&q_artist=" + encodedArtistName + apiKey;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        textView = (TextView) findViewById(R.id.textView);    //the textview which is supposed to be displaying lyrics
        downloadLyrics = new DownloadLyrics(this);        //asynctask class
        downloadLyrics.execute(fullURL);

    }
}


