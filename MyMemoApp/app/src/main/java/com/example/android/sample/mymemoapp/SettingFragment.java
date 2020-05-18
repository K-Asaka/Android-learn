package com.example.android.sample.mymemoapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.prefs.PreferenceChangeEvent;

public class SettingFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
    // 変更イベントをアクティビティなどに通知する
    public interface SettingFragmentListener {
        void onSettingChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ファイル名を指定する
        getPreferenceManager().setSharedPreferencesName(
                SettingPrefUtil.PREF_FILE_NAME);

        // Preferencesの設定ファイルを指定
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // サマリーを更新する
        setTypefaceSummary(getPreferenceManager().getSharedPreferences());
        setPrefixSummary(getPreferenceManager().getSharedPreferences());
    }

    // 装飾のサマリーを更新する
    private void setTypefaceSummary(SharedPreferences sharedPreferences) {
        // Preferenceのキーを取得
        String key = getActivity().getString(R.string.key_text_style);

        // Preferenceを取得
        Preference preference = findPreference(key);

        // 現在選択されている値を取得
        Set<String> selected = sharedPreferences.getStringSet(key, Collections.<String>emptySet());

        // サマリーを設定
        preference.setSummary(TextUtils.join("/", selected.toArray()));
    }

    // プレフィックスのサマリーを更新する
    private void setPrefixSummary(SharedPreferences sharedPreferences) {
        // Preferenceのキーを取得
        String key = getActivity().getString(R.string.key_file_name_prefix);

        // Preferenceを取得
        Preference preference = findPreference(key);

        // 現在選択されている値を取得
        String prefix = sharedPreferences.getString(key, "");

        // サマリーを設定
        preference.setSummary(prefix);
    }

    @Override
    public void onResume() {
        super.onResume();
        // SharedPreferencesの値が変更されたイベントを監視するリスナーを登録
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // リスナーの登録を解除
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // アクティビティを取得
        Activity activity = getActivity();

        // ActivityがSettingFragmentListenerを実装しているのであれば、通知する
        if (activity instanceof SettingFragmentListener) {
            SettingFragmentListener listener = (SettingFragmentListener)activity;

            // アクティビティに変更通知
            listener.onSettingChanged();
        }

        // サマリーに反映する
        if (activity.getString(R.string.key_text_style).equals(key)) {
            setTypefaceSummary(sharedPreferences);
        } else if (activity.getString(R.string.key_file_name_prefix).equals(key)) {
            setPrefixSummary(sharedPreferences);
        }
    }
}
