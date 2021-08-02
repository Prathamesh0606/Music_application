package com.phoenix.music_application.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Song {

    @PrimaryKey(autoGenerate = true)
    public int songId;

    @ColumnInfo(name = "song_title")
    public String title;

    @ColumnInfo(name = "song_artist")
    public String artist;

    @ColumnInfo(name = "song_lyrics")
    public String lyrics;
}
