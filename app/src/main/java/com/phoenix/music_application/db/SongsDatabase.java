package com.phoenix.music_application.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Song.class}, version = 1)
public abstract class SongsDatabase extends RoomDatabase {

    public abstract SongDao songDao();

    private static SongsDatabase INSTANCE;

    public static SongsDatabase getDbInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SongsDatabase.class, "lyrics_db")
                    .allowMainThreadQueries()
                    .build();


        }

        return INSTANCE;
    }

}
