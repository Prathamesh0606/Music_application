package com.phoenix.music_application.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface SongDao {

    @Query("SELECT song_lyrics FROM song WHERE song_title = :title and song_artist = :artist")
    public String loadLyrics(String title, String artist);

    @Insert
    public void insertSong(Song song);


}
