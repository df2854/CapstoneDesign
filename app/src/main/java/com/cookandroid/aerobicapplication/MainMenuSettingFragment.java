package com.cookandroid.aerobicapplication;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class MainMenuSettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // preferences.xml 파일을 화면에 표시
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
