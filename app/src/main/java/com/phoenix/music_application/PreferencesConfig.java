package com.phoenix.music_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;


//Class to write arraylist into preferences so that it gets stored in storage.
public class PreferencesConfig {
    private static final String KEY = "list_key";


    //this method writes arraylist from Playlistactivity class to preferences

    public static void writeInPref(Context context, ArrayList<File> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);          //gson is library made to convert arralist type to json and store in preferences as string
        //It is retrieved by converting string from preferences again to arraylist

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY, jsonString);
        editor.apply();
    }

    public static ArrayList<File> readFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString(KEY, "null");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<File>>() {
        }.getType();  //creates template kindof thing, im not sure
        ArrayList<File> list = gson.fromJson(jsonString, type);
        return list;
    }
}
