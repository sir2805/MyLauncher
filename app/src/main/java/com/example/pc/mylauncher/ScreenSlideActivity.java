/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pc.mylauncher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

public class ScreenSlideActivity extends FragmentActivity{
    private static final int NUM_PAGES = 4;

    private NonSwipeableViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (NonSwipeableViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                // invalidateOptionsMenu();
            }
        });
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mNextButton = (Button)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNextButton.setText((mPager.getCurrentItem() >= mPagerAdapter.getCount() - 2)
                       ? R.string.action_finish
                        : R.string.action_next);
                if (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
                    SharedPreferences applicationPreferences = PreferenceManager.getDefaultSharedPreferences(ScreenSlideActivity.this);
                    applicationPreferences.edit().putBoolean("showWelcome", false).apply();
//                    getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), MainAppActivity.class.getName()), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
//                    getPackageManager().setComponentEnabledSetting(new ComponentName(getPackageName(), ScreenSlideActivity.class.getName()), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
                    Intent intent = new Intent(ScreenSlideActivity.this, MainAppActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                }
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return WelcomeFragment.newInstance();
                case 1:
                    return ScreenSlidePageFragment.create(R.string.app_description_first_title, R.string.app_description_first_info);
                case 2:
                    return ScreenSlidePageFragment.create(R.string.app_description_second_title, R.string.app_description_second_info);
                case 3:
                    return SettingsFragment.newInstance();
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
