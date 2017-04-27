package com.example.pc.mylauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pc.mylauncher.data.AppsListContract;
import com.example.pc.mylauncher.data.AppsListDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by PC on 14.04.2017.
 */

public class AppsManager {
    private AppsManager() {};
    private static List<AppModel> appList = new ArrayList<>();

    private static List<AppModel> appListSortedByClicks;
    private static List<AppModel> appListSortedByTime;

    private static List<AppModel> favouriteApps = new ArrayList<>();
    private static int mColumns;
    //private static List<String> appLabels = new ArrayList<>();

    public static final String FAVOURITE_STATE_CHANGED = "com.example.pc.mylauncher.favouritestatechanged";

    //public static List<String> getAppLabels() { return appLabels; }
    public static String getPackageName(String appLabel) {
        for(AppModel app : appList) {
            if (app.getLabel().equals(appLabel)) {
                return app.getApplicationPackageName();
            }
        }
        return null;
    }

    public static int getColumns() {
        return mColumns;
    }

    public static void setColumns(int mColums) {
        AppsManager.mColumns = mColums;
    }

    public static List<AppModel> setAppsSortedByClicks() {
        List<AppModel>sorted = new ArrayList<>(appList);
        Collections.sort(sorted, new Comparator<AppModel>() {
            @Override
            public int compare(AppModel o1, AppModel o2) {
                return o2.getClicks() - o1.getClicks();
            }
        });
        appListSortedByClicks = sorted;
        return appListSortedByClicks;
    }

    public static List<AppModel> setAppsSortedByTime() {
        List<AppModel>sorted = new ArrayList<>(appList);
        Collections.sort(sorted, new Comparator<AppModel>() {
            @Override
            public int compare(AppModel o1, AppModel o2) {
                return (int)(o2.getInstallationTime() - o1.getInstallationTime());
            }
        });
        appListSortedByTime = sorted;
        return appListSortedByTime;
    }


    public static void removeAllFavourites(Context context) {

        for(AppModel app : appList) {
            app.setFavourite(false);
        }
        AppsListDbHelper helper = new AppsListDbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        AppsManager.dumpToDb(db);
        db.close();
        favouriteApps.clear();
    }
    public static void setAppList(List<AppModel>newApps) {
        if (newApps == null) {
            return;
        }
        if (appList == null) {
            appList = newApps;
        } else {
//            if (appList.size() > newApps.size()) {// App is removed
//
//            } else if (appList.size() < newApps.size()) {//App is added
//                //findNewApp(newApps).addToDb();
//            } else {//App is updated
//            }
            appList.clear();
            appList.addAll(newApps);
        }
        setFavouriteApps();
        setAppsSortedByClicks();
        setAppsSortedByTime();
        //            appLabels = new ArrayList<>();
//            for (AppModel app : appList) {
//                appLabels.add(app.toString());
//            }
    }

    public static List<AppModel> getFavouriteApps() {
        return favouriteApps;
    }

    private static void setFavouriteApps () {
        favouriteApps.clear();
        for(AppModel app : appList) {
            if (app.isFavourite()) {
                favouriteApps.add(app);
            }
        }
    }

    public static void addToFavourites(AppModel app) {
        if (app.isFavourite()) {
            return;
        }
        favouriteApps.add(app);
        app.setFavourite(true);
    }
    public static AppModel getApp(int pos) {
        if (pos > (mColumns + 1) * 2) {
            return appList.get(pos - ((mColumns + 1) * 2 + 1));
        } else if (pos > mColumns + 1) {//new
            return appListSortedByTime.get(pos - mColumns - 2);
        } else //popular
            return appListSortedByClicks.get(pos - 1);
    }

//    private static void initApps() {
//
//    }

    public static int size() {
        if (appList.size() == 0) {
            return 0;
        }
        return appList.size() + ((mColumns + 1)* 2) + 1;
    }

    public static void removeFromFavourites(int pos) {
        favouriteApps.remove(pos).setFavourite(false);
//        appList.get(pos).setFavourite(false);
        //appList.get(pos).removeFromFavourite();
    }

//    private static AppModel findNewApp(List<AppModel> newApps) {
//        for (int i = 0; i < appList.size(); i++) {
//            if (!newApps.get(i).getApplicationPackageName().equals(appList.get(i).getApplicationPackageName())) {
//                newApps.get(i);
//            }
//        }
//        return newApps.get(newApps.size() - 1);
//    }
    public static void getAllFromDb(SQLiteDatabase db) {
        Cursor cursor = db.query(AppsListContract.AppsListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                AppsListContract.AppsListEntry.COLUMN_APP_PACKAGENAME);
        while(cursor.moveToNext()) {
            AppModel app = findByPackageName(cursor.getString(cursor.getColumnIndex(AppsListContract.AppsListEntry.COLUMN_APP_PACKAGENAME)));
            if (app != null) {
                if (cursor.getInt(cursor.getColumnIndex(AppsListContract.AppsListEntry.COLUMN_APP_IS_FAVOURITE)) == 0) {
                    app.setFavourite(false);
                } else if (cursor.getInt(cursor.getColumnIndex(AppsListContract.AppsListEntry.COLUMN_APP_IS_FAVOURITE)) == 1) {
                    app.setFavourite(true);
                }
                app.setClicks(cursor.getInt(cursor.getColumnIndex(AppsListContract.AppsListEntry.COLUMN_APP_CLICKS)));
            }
        }
        setFavouriteApps();
    }

    private static AppModel findByPackageName(String packageName) {
        for(AppModel app : appList) {
            if (app.getApplicationPackageName().equals(packageName)) {
                return app;
            }
        }
        return null;
    }

    public static void dumpToDb(SQLiteDatabase db) {
        db.delete(AppsListContract.AppsListEntry.TABLE_NAME, null, null);
        for (AppModel app : appList) {
            ContentValues cv = new ContentValues();
            cv.put(AppsListContract.AppsListEntry.COLUMN_APP_PACKAGENAME, app.getApplicationPackageName());
            if (app.isFavourite()) {
                cv.put(AppsListContract.AppsListEntry.COLUMN_APP_IS_FAVOURITE, 1);
            } else {
                cv.put(AppsListContract.AppsListEntry.COLUMN_APP_IS_FAVOURITE, 0);
            }
            cv.put(AppsListContract.AppsListEntry.COLUMN_APP_CLICKS, app.getClicks());
            db.insert(AppsListContract.AppsListEntry.TABLE_NAME, null, cv);
        }
    }
}
