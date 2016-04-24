package com.example.anandundavia.heatbeattracker;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by amit on 21-Apr-16.
 */
public class HeartBeatData extends AppCompatActivity{



    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.heart_beat_data);
        Log.d("CreatingActivity","Creating");
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
