package com.example.pc.mylauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
public class FavouriteAppsFragment extends Fragment implements FavouriteAppsAdapter.ListItemClickListener {

    private FavouriteAppsAdapter mAdapter;
    private RecyclerView mNumbersList;
    private BroadcastReceiver br;

    private int mColums = 4;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO look for wait for loaderManager to load
        //registerForContextMenu(mNumbersList);
    }

    public FavouriteAppsFragment() {};

    public static Fragment newInstance() {
        Fragment fragment = new FavouriteAppsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite_apps, container, false);

        mNumbersList = (RecyclerView) rootView.findViewById(R.id.favourite_apps_rv_unique_value);

        mNumbersList.setHasFixedSize(true);
        mNumbersList.setOnCreateContextMenuListener(this);
        mAdapter = new FavouriteAppsAdapter(this);
        mNumbersList.setAdapter(mAdapter);

        return rootView;
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
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), mColums);
        mNumbersList.setLayoutManager(mLayoutManager);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mAdapter.notifyDataSetChanged();
            }
        };
        getContext().registerReceiver(br, new IntentFilter(AppsManager.FAVOURITE_STATE_CHANGED));

    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(br);
    }

    public void setColums(String colums) {
        if (colums.equals(getString(R.string.pref_icons_show_small_value))) {
            mColums = 4;
        } else if (colums.equals(getString(R.string.pref_icons_show_big_value))) {
            mColums = 5;
        }
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
}