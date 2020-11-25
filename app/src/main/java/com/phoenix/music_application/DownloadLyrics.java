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


    public DownloadLyrics(Context ctx) {
        this.ctx = ctx;

    }


    @Override
    protected String doInBackground(String... strings) {

        //String artist = strings[1];
        InputStream in;
        String result = "";

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
                result += current;
                data = reader.read();
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        LyricsActivity.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        LyricsActivity.progressBar.setVisibility(View.GONE);

        try {
            JSONObject json = new JSONObject(s);
            JSONObject message = json.getJSONObject("message");
            JSONObject body = message.getJSONObject("body");
            JSONObject lyr = body.getJSONObject("lyrics");
            String lyrics = lyr.getString("lyrics_body");
            String[] lyricswithoutOtherCrap = lyrics.split("[/*]+");

            LyricsActivity.textView.setText(lyricswithoutOtherCrap[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


