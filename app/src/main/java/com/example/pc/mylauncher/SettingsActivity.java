package com.example.pc.mylauncher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        changeTheme(sharedPreferences.getString(getString(R.string.pref_theme_key), getString(R.string.pref_theme_light_value)));
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

//    @Override
//    protected void onDestroy() {
//        final Intent intent = IntentCompat.makeMainActivity(new ComponentName(
//                SettingsActivity.this, MainAppActivity.class));
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        super.onDestroy();
//    }
    void changeTheme(String id) {
    if (id.equals(getString(R.string.pref_theme_light_value))) {
        setTheme(R.style.AppTheme_LightNoBackground);
    } else if (id.equals(getString(R.string.pref_theme_dark_value))) {
        setTheme(R.style.AppTheme_DarkNoBackground);
    }
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}