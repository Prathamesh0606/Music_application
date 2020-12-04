package com.phoenix.music_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;


public class libActivity extends Activity {

    ConstraintLayout openAllsongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_screen);
        openAllsongs= findViewById(R.id.openAllsongs);

    }

    public void openAllsongs(View v){

        Intent intent = new Intent(libActivity.this, Recycler_view.class);
        startActivity(intent);
    }


}
