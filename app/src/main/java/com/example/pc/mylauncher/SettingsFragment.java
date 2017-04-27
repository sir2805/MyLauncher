package com.example.pc.mylauncher;
/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.example.pc.mylauncher.data.AppsListContract;
import com.example.pc.mylauncher.data.AppsListDbHelper;

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {


    public SettingsFragment() {
    }

    public static Fragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_launcher);
        Preference preference = findPreference(getString(R.string.pref_uri_amount_key));
        preference.setSummary(android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getString(R.string.pref_uri_amount_key), getString(R.string.pref_theme_light_value)));
        preference.setOnPreferenceChangeListener(this);
        Preference clearURIPref = findPreference(getString(R.string.pref_uri_clear_key));
        clearURIPref.setOnPreferenceClickListener(this);
        Preference clearFavouritePref = findPreference(getString(R.string.pref_favourite_clear_key));
        clearFavouritePref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.pref_uri_amount_key))) {
            Toast error = Toast.makeText(getContext(), "Please select a number between 1 and 100", Toast.LENGTH_SHORT);

            String sizeKey = getString(R.string.pref_uri_amount_key);
            if (preference.getKey().equals(sizeKey)) {
                String stringSize = ((String) (newValue)).trim();
                if (stringSize.equals("")) stringSize = "10";
                try {
                    int size = Integer.parseInt(stringSize);
                    if (size > 100 || size <= 0) {
                        error.show();
                        return false;
                    }
                } catch (NumberFormatException nfe) {
                    error.show();
                    return false;
                }
                preference.setSummary(stringSize);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.pref_uri_clear_key))) {
            AppsListDbHelper dbHelper = new AppsListDbHelper(SettingsFragment.this.getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete(AppsListContract.URLEntry.TABLE_NAME, null, null);
            return true;
        } else if (preference.getKey().equals(getString(R.string.pref_favourite_clear_key))) {
            AppsManager.removeAllFavourites(getContext());
            Intent intent = new Intent();
            intent.setAction(AppsManager.FAVOURITE_STATE_CHANGED);
            getActivity().sendBroadcast(intent);
            return true;
        } else {
            return false;
        }
    }
}
