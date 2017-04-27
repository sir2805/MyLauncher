package com.example.pc.mylauncher;

import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
public class InstalledAppsFragment extends Fragment implements InstalledAppsAdapter.ListItemClickListener,
        InstalledAppsAdapter.ListMenuItemClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<AppModel>> {

    private InstalledAppsAdapter mAdapter;
    private RecyclerView mNumbersList;
    private ProgressBar mLoadingIndicator;

    private int mColums = 4;

//    public interface ApplicationsLoadedListener {
//        void onApplicationsLoaded();
//    }

    //private ApplicationsLoadedListener mAppsLoadedListener;


    @Override
    public Loader<ArrayList<AppModel>> onCreateLoader(int id, Bundle bundle) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        return new AppsLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AppModel>> loader, ArrayList<AppModel> apps) {
        mAdapter.setData(apps);
        //mAppsLoadedListener.onApplicationsLoaded();
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNumbersList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AppModel>> loader) {
        mAdapter.setData(null);
    }

    public InstalledAppsFragment() {};

    public static Fragment newInstance() {
        Fragment fragment = new InstalledAppsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setColums(String colums) {
        if (colums.equals(getString(R.string.pref_icons_show_small_value))) {
            mColums = 4;
        } else if (colums.equals(getString(R.string.pref_icons_show_big_value))) {
            mColums = 5;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNumbersList = (RecyclerView) getActivity().findViewById(R.id.apps_rv_unique);

        mLoadingIndicator = (ProgressBar) (getActivity().findViewById(R.id.pb_loading_indicator));

//        if (DataHandler.getColorScheme()) {
//            mNumbersList.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        } else {
//            mNumbersList.setBackgroundColor(getResources().getColor(R.color.darkcolorPrimary));
//        }

        mAdapter = new InstalledAppsAdapter(this);
        mNumbersList.setAdapter(mAdapter);
        mNumbersList.setHasFixedSize(true);

        mNumbersList.setOnCreateContextMenuListener(this);
//        mNumbersList.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//                int count = parent.getChildCount();
//                for(int i = 0; i < count; i++) {
//                    View child = parent.getChildAt(i);
//                    int position = parent.getChildAdapterPosition(child);
//                    if(position  == 2 * mColums || position == mColums) {
//                        drawBackground(c, parent, i);
//                    }
//                }
//            }
//
//            private void drawBackground(Canvas c, RecyclerView parent, int index) {
//                int l = parent.getLeft();
//                int t = parent.getChildAt(index).getTop();
//                int r = parent.getRight();
//                int b = parent.getChildAt(index).getBottom();
//
//                Paint mPaint = new Paint();
//                mPaint.setARGB(100, 255, 255, 0);
//                c.drawRect(l, t, r, b, mPaint);
//            }
//        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_installed_apps,
                container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setColums(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getActivity().getString(R.string.pref_icon_amount_key), getString(R.string.pref_icons_show_small_value)));

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mColums += 2;
        } else {
            if (mColums >= 6) {
                mColums -= 2;
            }
        }
        mAdapter.setColumns(mColums);
        AppsManager.setColumns(mColums);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), mColums);
        mNumbersList.setLayoutManager(mLayoutManager);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == mColums + 1 || position == 2 * (mColums + 1)) {
                    return mColums;
                }
                return 1;
            }
        });
    }

    @Override
    public void onListItemClick(AppModel app) {
        if (app != null) {
            app.setClicks(app.getClicks() + 1);
            Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(app.getApplicationPackageName());

            if (intent != null) {
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onListMenuItemClick(int id, AppModel app) {

        switch (id) {
            case 0:
                AppsManager.addToFavourites(app);
                Intent intent = new Intent();
                intent.setAction(AppsManager.FAVOURITE_STATE_CHANGED);
                getActivity().sendBroadcast(intent);
                return true;
            case 1:
                Intent infoIntent =
                        new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + app.getApplicationPackageName()));
                startActivity(infoIntent);
                return true;

            case 2:
                Intent uninstallIntent =
                        new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:" + app.getApplicationPackageName()));
                startActivity(uninstallIntent);
                return true;
            default:
                return false;
        }
    }
}