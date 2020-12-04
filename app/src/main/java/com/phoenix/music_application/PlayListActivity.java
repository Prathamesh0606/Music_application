package com.phoenix.music_application;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {

    ListView listView;
    String[] songNames;
    File file;
    ArrayList<String> artistNames, albumNames;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        listView = findViewById(R.id.playListView);

        //folderpath is static variable inside settings screen activity, which gives path of folder selected to serach for songs
        if (settingsActivity.folderPath == null) {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());           //generates new file object with /mnt as root directory
        } else {
            file = new File(settingsActivity.folderPath);
        }
        final ArrayList<Audio> songs = scanDeviceForMp3Files();

        //for (int i = 0; i < songs.size(); i++) { Log.i("null" ,songs.get(i).getTitle()); }

        //final ArrayList<ArrayList<Audio>> artistSongs = scanDeviceForArtistMp3Files();
        //final ArrayList<ArrayList<Audio>> albumSongs = scanDeviceForAlbumMp3Files();

        songNames = new String[songs.size()];

        for (int i = 0; i < songs.size(); i++) {
            songNames[i] = songs.get(i).getTitle();             //did this so that song doesnt show .mp3 in its name
        }

        //arrayadapter to
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.songs_layout, R.id.songNameText, songNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
                Intent i = new Intent(PlayListActivity.this, MainActivity.class);
                Bundle b = new Bundle();

                i.putExtra("songIndex", position);
                b.putSerializable("songList", (ArrayList<Audio>) songs);
                i.putExtras(b);
                startActivity(i);

            }
        });

    }

    private ArrayList<ArrayList<Audio>> scanDeviceForAlbumMp3Files() {

        int i;
        ArrayList<ArrayList<Audio>> tempAlbumList = new ArrayList<ArrayList<Audio>>();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,

        };
        final String sortOrder = MediaStore.Audio.AudioColumns.ALBUM + " COLLATE LOCALIZED ASC";

        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, selection, null, sortOrder);

        if (cursor != null) {
            cursor.moveToFirst();

            i = -1;

            while (!cursor.isAfterLast()) {
                Audio a = new Audio();

                String t = cursor.getString(0);
                String ar = cursor.getString(1);
                String p = cursor.getString(2);

                a.setTitle(t);
                a.setArtist(ar);
                a.setPath(cursor.getString(2));
                a.setDuration(cursor.getString(3));

                if (!albumNames.contains(cursor.getString(4))) {
                    albumNames.add(cursor.getString(4));
                    i++;
                }

                if (p != null) { tempAlbumList.get(i).add(a); }

                cursor.moveToNext();
            }
        }

        return tempAlbumList;
    }

    private ArrayList<ArrayList<Audio>> scanDeviceForArtistMp3Files() {

        int i;
        ArrayList<ArrayList<Audio>> tempArtistList = new ArrayList<ArrayList<Audio>>();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,

        };
        final String sortOrder = MediaStore.Audio.AudioColumns.ARTIST + " COLLATE LOCALIZED ASC";

        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, selection, null, sortOrder);

        if (cursor != null) {
            cursor.moveToFirst();

            i = -1;

            while (!cursor.isAfterLast()) {
                Audio a = new Audio();

                String t = cursor.getString(0);
                String ar = cursor.getString(1);
                String p = cursor.getString(2);

                a.setTitle(t);
                a.setArtist(ar);
                a.setPath(cursor.getString(2));
                a.setDuration(cursor.getString(3));

                if (!artistNames.contains(ar)) {
                    artistNames.add(ar);
                    i++;
                }

                if (p != null) { tempArtistList.get(i).add(a); }

                cursor.moveToNext();
            }
        }

        return tempArtistList;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private ArrayList<Audio> scanDeviceForMp3Files() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,

        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        ArrayList<Audio> mp3Files = new ArrayList<>();

        Uri uri;
        Cursor cursor = null;
        uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        try {
            if (settingsActivity.folderPath == null) {

                cursor = getContentResolver().query(uri, projection, selection, null, sortOrder);
            } else {

                Toast.makeText(this, settingsActivity.folderPath, Toast.LENGTH_LONG).show();
                File base = new File(settingsActivity.folderPath);
                String p = base.getName();
                cursor = getContentResolver().query(uri, projection, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%" + p + "%"}, sortOrder);
            }


            if (cursor != null) {
                cursor.moveToFirst();
//

                while (!cursor.isAfterLast()) {
                    Audio a = new Audio();
                    String t = cursor.getString(0);
                    String ar = cursor.getString(1);
                    String p = cursor.getString(2);
                    a.setTitle(t);
                    a.setArtist(ar);

                    a.setPath(cursor.getString(2));

                    a.setDuration(cursor.getString(3));

                    //if (!artistNames.contains(cursor.getString(1))) { artistNames.add(ar); }

                    if (p != null) {
                        mp3Files.add(a);

                    }
                    cursor.moveToNext();
                }


            }
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (settingsActivity.folderPath == null)
            PreferencesConfig.writeInPref(this, mp3Files);
        return mp3Files;

    }
}