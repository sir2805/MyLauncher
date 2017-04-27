package com.example.pc.mylauncher;

/**
 * Created by PC on 11.04.2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;

import com.example.pc.mylauncher.data.AppsListContract;
import com.example.pc.mylauncher.data.AppsListDbHelper;

import java.io.File;

/**
 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public class AppModel {

    private final Context mContext;
    private final PackageInfo mInfo;


    private String mAppLabel;
    private String mPackageName;
    private long installationTime;
    private int clicks;
    private boolean isFavourite;
    private Drawable mIcon;

//    private boolean mMounted;
//    private final File mApkFile;

    public AppModel(Context context, PackageInfo info) {
        mContext = context;
        mInfo = info;
        installationTime = mInfo.firstInstallTime;

        mAppLabel = info.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
        mPackageName = info.packageName;
        mIcon = info.applicationInfo.loadIcon(mContext.getPackageManager());
        clicks = 0;
        isFavourite = false;
//        mApkFile = new File(info.sourceDir);
    }

//    public PackageInfo getAppInfo() {
//        return mInfo;
//    }

    public String getApplicationPackageName() {
        return mPackageName;
    }

    public long getInstallationTime() {
        return installationTime;
    }

    public int getClicks() {
        return clicks;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public void setExtraInfo(int clicks, boolean isFavourite) {
        setClicks(clicks);
        setFavourite(isFavourite);
    }

    public String getLabel() {
        return mAppLabel;
    }

//    public void addToFavourite() {
//        isFavourite = true;
//    }
//
//    public void removeFromFavourite() {
//        isFavourite = false;
//    }
//
//    public boolean isFavourite() {
//        return isFavourite;
//    }

    public Drawable getIcon() {
//        if (mIcon == null) {
//            if (mApkFile.exists()) {
//                mIcon = mInfo.loadIcon(mContext.getPackageManager());
//                return mIcon;
//            } else {
//                mMounted = false;
//            }
//        } else if (!mMounted) {
//            // If the app wasn't mounted but is now mounted, reload
//            // its icon.
//            if (mApkFile.exists()) {
//                mMounted = true;
//                mIcon = mInfo.loadIcon(mContext.getPackageManager());
//                return mIcon;
//            }
//        } else {
            return mIcon;
//        }
//
//        return mContext.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
    }


//    void loadLabel(Context context) {
//        if (mAppLabel == null || !mMounted) {
//            if (!mApkFile.exists()) {
//                mMounted = false;
//                mAppLabel = mInfo.packageName;
//            } else {
//                mMounted = true;
//                CharSequence label = mInfo.loadLabel(context.getPackageManager());
//                mAppLabel = label != null ? label.toString() : mInfo.packageName;
//            }
//        }
//    }

    @Override
    public String toString() {
        return mAppLabel;
    }

    public long addToDb(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(AppsListContract.AppsListEntry.COLUMN_APP_PACKAGENAME, mPackageName);
        cv.put(AppsListContract.AppsListEntry.COLUMN_APP_CLICKS, 0);
        cv.put(AppsListContract.AppsListEntry.COLUMN_APP_IS_FAVOURITE, 0);
        return db.insert(AppsListContract.AppsListEntry.TABLE_NAME, null, cv);
    }
}