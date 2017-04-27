package com.example.pc.mylauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

import com.example.pc.mylauncher.data.AppsListDbHelper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by PC on 11.04.2017.
 */

public class AppsLoader extends AsyncTaskLoader<ArrayList<AppModel>> {
    private ArrayList<AppModel> mInstalledApps;
    private SQLiteDatabase mDb;
    private static boolean dbUsed = false;

    private final PackageManager mPm;
    private PackageIntentReceiver mPackageObserver;

    public AppsLoader(Context context) {
        super(context);
        AppsListDbHelper appsListDbHelper = new AppsListDbHelper(context);
        mDb = appsListDbHelper.getWritableDatabase();

        mPm = context.getPackageManager();
    }

    @Override
    public ArrayList<AppModel> loadInBackground() {
        // retrieve the list of installed applications
        List<PackageInfo> apps = mPm.getInstalledPackages(0);

        if (apps == null) {
            apps = new ArrayList<>();
        }

        final Context context = getContext();

        // create corresponding apps and load their labels
        ArrayList<AppModel> items = new ArrayList<AppModel>(apps.size());
        for (int i = 0; i < apps.size(); i++) {
            String pkg = apps.get(i).packageName;

            // only apps which are launchable
            if (context.getPackageManager().getLaunchIntentForPackage(pkg) != null) {
                AppModel app = new AppModel(context, apps.get(i));
//                app.loadLabel(context);
                items.add(app);
            }
        }

        // sort the list
        Collections.sort(items, ALPHA_COMPARATOR);

        return items;
    }

    @Override
    public void deliverResult(ArrayList<AppModel> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps);
            }
        }

        ArrayList<AppModel> oldApps = apps;
        AppsManager.setAppList(apps);
        if (!dbUsed) {
            //dbUsed = true;
            AppsManager.getAllFromDb(mDb);
        }
        mInstalledApps = apps;

        Intent intent = new Intent();
        intent.setAction(AppsManager.FAVOURITE_STATE_CHANGED);
        getContext().sendBroadcast(intent);

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mInstalledApps != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mInstalledApps);
        }

        // watch for changes in app install and uninstall operation
        if (mPackageObserver == null) {
            mPackageObserver = new PackageIntentReceiver(this);
        }

        if (takeContentChanged() || mInstalledApps == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(ArrayList<AppModel> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    @Override
    protected void onReset() {
        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mInstalledApps != null) {
            onReleaseResources(mInstalledApps);
            mInstalledApps = null;
        }

        // Stop monitoring for changes.
        if (mPackageObserver != null) {
            getContext().unregisterReceiver(mPackageObserver);
            mPackageObserver = null;
        }
    }

    /**
     * Helper method to do the cleanup work if needed, for example if we're
     * using Cursor, then we should be closing it here
     *
     * @param apps
     */
    protected void onReleaseResources(ArrayList<AppModel> apps) {
        // do nothing
    }


    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<AppModel> ALPHA_COMPARATOR = new Comparator<AppModel>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppModel object1, AppModel object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };
}
