package com.example.anandundavia.heatbeattracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Database.LOCALDB = new Database(this);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ftm = fm.beginTransaction();
        Fragment fragToLoad;
        if (!Database.LOCALDB.isUserRegistered())
        {
            fragToLoad = new FirstTime();
        } else
        {
            fragToLoad = new HomeFragment();
            //new Thread(new RandomDataGen(this)).start();
        }
        ftm.replace(R.id.container, fragToLoad).commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_settings)
        {
            if (Database.LOCALDB.isUserRegistered())
            {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ftm = fm.beginTransaction();
                ftm.replace(R.id.container, new Settings());
                ftm.addToBackStack(null);
                ftm.commit();

            } else
            {
                Toast.makeText(this, "Register First!", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}