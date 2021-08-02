package com.phoenix.music_application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadLyrics extends AsyncTask<String, Void, String> {
    @SuppressLint("StaticFieldLeak")
    Context ctx;
    ;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;


    public DownloadLyrics(Context ctx, AsyncResponse delegate) {
        this.ctx = ctx;
        this.delegate = delegate;

    }


    @Override
    public String doInBackground(String... strings) {
        StringBuilder result;
        //String artist = strings[1];
        InputStream in;
        result = new StringBuilder();

        try {
            URL url = new URL(strings[0]);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(20000);
            connection.setRequestMethod("GET");
            connection.connect();

            in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while ((data != -1)) {
                char current = (char) data;
                result.append(current);
                data = reader.read();
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

//        LyricsActivity.progressBar.setVisibility(View.VISIBLE);
        MainActivity.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        LyricsActivity.progressBar.setVisibility(View.GONE);
        MainActivity.progressBar.setVisibility(View.GONE);

        try {
            JSONObject json = new JSONObject(s);
            JSONObject message = json.getJSONObject("message");
            JSONObject body = message.getJSONObject("body");
            JSONObject lyr = body.getJSONObject("lyrics");
            String lyrics = lyr.getString("lyrics_body");
            String[] lyricswithoutOtherCrap = lyrics.split("[/*]+");


            if (!lyricswithoutOtherCrap[0].equals("")) {

                MainActivity.lyricsView.setText(lyricswithoutOtherCrap[0]);
                delegate.processFinish(lyricswithoutOtherCrap[0]);
            } else
                MainActivity.lyricsView.setText(ctx.getString(R.string.could_not_find_lyrics));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


