package com.example.pc.mylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

public class WelcomeSettingsFragment extends Fragment implements View.OnClickListener {
    SharedPreferences.Editor mEditor;

    public WelcomeSettingsFragment() {
    }

    public static Fragment newInstance() {
        WelcomeSettingsFragment fragment = new WelcomeSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_welcome_settings, container, false);

        rootView.findViewById(R.id.four_by_six).setOnClickListener(this);
        rootView.findViewById(R.id.five_by_seven).setOnClickListener(this);
        rootView.findViewById(R.id.dark_theme).setOnClickListener(this);
        rootView.findViewById(R.id.light_theme).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {

        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.dark_theme:
                if (checked) {
                    mEditor.putBoolean("isLight", false).apply();
                }
                break;
            case R.id.light_theme:
                if (checked) {
                    mEditor.putBoolean("isLight", true).apply();
                }
                break;
            case R.id.five_by_seven:
                if (checked) {
                    mEditor.putInt("columns", 5).apply();
                }
                break;
            case R.id.four_by_six:
                if (checked) {
                    mEditor.putInt("columns", 4).apply();
                }
                break;
        }
    }
}