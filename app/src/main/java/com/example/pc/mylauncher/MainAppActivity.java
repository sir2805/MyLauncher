package com.example.pc.mylauncher;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.pc.mylauncher.data.AppsListContract;
import com.example.pc.mylauncher.data.AppsListDbHelper;
//import com.yandex.metrica.YandexMetrica;

import java.util.LinkedList;
import java.util.List;


public class MainAppActivity
        extends FragmentActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        //InstalledAppsFragment.AddToFavouriteListener,
        //InstalledAppsFragment.ApplicationsLoadedListener,
        PopupMenu.OnMenuItemClickListener {

    private static int NUM_PAGES = 2;

    private int maxURL;

    private ViewPager mPager;

    private AutoCompleteTextView mURIBar;
    private ArrayAdapter<String> mURIAdapter;

    private PagerAdapter mPagerAdapter;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        YandexMetrica.activate(getApplicationContext(), "ec93f291-882c-4925-ac95-e654ca26edf1");

        AppsListDbHelper appsListDbHelper = new AppsListDbHelper(this);
        mDb = appsListDbHelper.getWritableDatabase();

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("showWelcome", true)) {
            Intent intent = new Intent(this, ScreenSlideActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
       // sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        changeTheme(sharedPreferences.getString(getString(R.string.pref_theme_key), getString(R.string.pref_theme_light_value)));
        setNumPages(sharedPreferences.getBoolean(getString(R.string.pref_favourite_show_key),
                getResources().getBoolean(R.bool.pref_favourite_show_default)));
        maxURL = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.pref_uri_amount_key),
                getResources().getString(R.string.pref_uri_amount_default)));
        // Instantiate a ViewPager and a PagerAdapter.
        setContentView(R.layout.activity_main_app);

        mPager = (ViewPager) findViewById(R.id.app_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);
        mURIBar = (AutoCompleteTextView) findViewById(R.id.main_app_search);
        mURIBar.setOnKeyListener(
                new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            String sURL = mURIBar.getText().toString();
                            addURL(sURL);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + sURL));
                            startActivity(browserIntent);
//                            String packageName = AppsManager.getPackageName(mURIBar.getText().toString());
//                            if (packageName != null) {
//                                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(MainAppActivity.this, "No such app", Toast.LENGTH_SHORT).show();
//                            }
                            return true;
                        }
                        return false;
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateURLAdapter();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.visualizer_menu);
        popup.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainAppActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return false;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return InstalledAppsFragment.newInstance();
            } else {
                return FavouriteAppsFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.all_apps);
            } else if (position == 1) {
                return getResources().getString(R.string.favourite_apps);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onStop() {
        AppsListDbHelper helper = new AppsListDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        AppsManager.dumpToDb(db);
        updateURLTable();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//
//        AppsManager.dumpToDb(db);
//        updateURLTable();
//        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this)
//                .unregisterOnSharedPreferenceChangeListener(this);
//        db.close();
        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        AppsListDbHelper helper = new AppsListDbHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.close();
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(MainAppActivity.this.getString(R.string.pref_icon_amount_key))) {
            Log.i("MAIN_APP", "Icons");
        } else if (key.equals(MainAppActivity.this.getString(R.string.pref_theme_key))) {
            Log.i("MAIN_APP", "Theme");
            recreate();

        } else if (key.equals(MainAppActivity.this.getString(R.string.pref_favourite_show_key))) {
            Log.i("MAIN_APP", "Screens");
            setNumPages(sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_favourite_show_default)));
            mPagerAdapter.notifyDataSetChanged();

//            recreate();
        } else if (key.equals(MainAppActivity.this.getString(R.string.pref_uri_amount_key))) {
            Log.i("MAIN_APP", "URI");
            maxURL = Integer.parseInt(sharedPreferences.getString(getResources().getString(R.string.pref_uri_amount_key),
                    getResources().getString(R.string.pref_uri_amount_default)));
            //updateURLAdapter();
        }
    }

    void setNumPages(boolean val) {
        if (val) {
            NUM_PAGES = 2;
        } else {
            NUM_PAGES = 1;
        }
    }

    void changeTheme(String id) {
        if (id.equals(getString(R.string.pref_theme_light_value))) {
            setTheme(R.style.AppTheme_Light);
        } else if (id.equals(getString(R.string.pref_theme_dark_value))) {
            setTheme(R.style.AppTheme_Dark);
        }
    }

//    @Override
//    public void onApplicationsLoaded() {
//        mURIAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, AppsManager.getAppLabels());
//        mURIBar.setAdapter(mURIAdapter);
//    }

    private void updateURLTable() {
        Cursor cursor = mDb.query(
                AppsListContract.URLEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                AppsListContract.URLEntry.COLUMN_TIMESTAMP + " DESC"
        );

        int difference = cursor.getCount() - maxURL;
        List<Integer> ids = new LinkedList<>();
        while (difference-- > 0) {
            cursor.moveToNext();
            int id = cursor.getInt(cursor.getColumnIndex(AppsListContract.URLEntry._ID));
            ids.add(id);
        }
        for (int id : ids) {
            mDb.delete(AppsListContract.URLEntry.TABLE_NAME, AppsListContract.URLEntry._ID + "=" + id, null);
        }
    }

    private List<String> loadURLFromDb() {
        Cursor cursor = mDb.query(
                AppsListContract.URLEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                AppsListContract.URLEntry.COLUMN_TIMESTAMP + " DESC"
        );

        List<String>URLs = new LinkedList<>();
        int idx = 0;
        while (cursor.moveToNext() && idx++ < maxURL) {
            String url = cursor.getString(
                    cursor.getColumnIndex(AppsListContract.URLEntry.COLUMN_URL));
            URLs.add(url);
        }
        return URLs;
    }

    private void addURL(String URL) {
        ContentValues cv = new ContentValues();
        cv.put(AppsListContract.URLEntry.COLUMN_URL, URL);
        mDb.insert(AppsListContract.URLEntry.TABLE_NAME, null, cv);
        updateURLAdapter();
    }

    private void updateURLAdapter() {
        mURIAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loadURLFromDb());
        mURIBar.setAdapter(mURIAdapter);
    }
}
