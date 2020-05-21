package com.example.android.sample.mymemoapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import java.util.Collections;
import java.util.Set;

public class SettingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    // 変更イベントをActivityに通知する
    public interface SettingFragmentListener {
        void onSettingChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ファイル名を指定する
        getPreferenceManager().setSharedPreferencesName(
                SettingPrefUtil.PREF_FILE_NAME);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTypefaceSummary(getPreferenceManager().getSharedPreferences());
        setPrefixSummary(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences, String key) {
        // Activityを取得
        Activity activity = getActivity();

        // ActivityがSettingFragmentListenerを実装しているのであれば、通知する
        if (activity instanceof SettingFragmentListener) {
            SettingFragmentListener listener = (SettingFragmentListener)activity;

            // Activityに変更通知
            listener.onSettingChanged();
        }

        // サマリーに反映する
        if (activity.getString(R.string.key_text_style).equals(key)) {
            setTypefaceSummary(sharedPreferences);
        } else if (activity.getString(R.string.key_file_name_prefix).equals(key)) {
            setPrefixSummary(sharedPreferences);
        }
    }

    private void setTypefaceSummary(SharedPreferences sharedPreferences) {
        String key = getActivity().getString(R.string.key_text_style);

        Preference preference = findPreference(key);

        Set<String> selected = sharedPreferences.getStringSet(key, Collections.<String>emptySet());
        preference.setSummary(TextUtils.join("/", selected.toArray()));
    }

    private void setPrefixSummary(SharedPreferences sharedPreferences) {
        String key = getActivity().getString(R.string.key_file_name_prefix);

        Preference preference = findPreference(key);

        String prefix = sharedPreferences.getString(key, "");
        preference.setSummary(prefix);
    }

}
