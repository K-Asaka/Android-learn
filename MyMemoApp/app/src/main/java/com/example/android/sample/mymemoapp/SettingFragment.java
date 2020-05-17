package com.example.android.sample.mymemoapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ファイル名を指定する
        getPreferenceManager().setSharedPreferencesName(
                SettingPrefUtil.PREF_FILE_NAME);

        // Preferencesの設定ファイルを指定
        addPreferencesFromResource(R.xml.preferences);
    }
}
