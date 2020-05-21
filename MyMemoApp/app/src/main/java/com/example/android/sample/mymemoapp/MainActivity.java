package com.example.android.sample.mymemoapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MemoLoadFragment.MemoLoadFragmentListener,
        SettingFragment.SettingFragmentListener {

    // 2ペインかどうか
    private boolean isDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2ペインの場合のMaster
        View masterView = findViewById(R.id.FragmentContainer);

        // 2ペインかどうかは、Masterがあるかどうかで判定する
        isDualPane = masterView != null;
        if (isDualPane) {
            // 2ペインの場合には、メモの一覧FragmentをMasterに追加する
            getFragmentManager().beginTransaction()
                    .replace(R.id.FragmentContainer, new MemoLoadFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューを生成する
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // 設定アクション
            case R.id.action_settings:
                if (isDualPane) {
                    // 2ペインの場合
                    FragmentManager manager = getFragmentManager();

                    if (manager.findFragmentById(R.id.FragmentContainer)
                            instanceof SettingFragment) {
                        // すでに設定Fragmentがある場合にはメモの一覧をMasterに追加する
                        getFragmentManager().beginTransaction()
                                .replace(R.id.FragmentContainer, new MemoLoadFragment())
                                .commit();
                    } else {
                        // 設定Fragmentがない場合には設定FragmentをMasterに追加する
                        getFragmentManager().beginTransaction()
                                .replace(R.id.FragmentContainer, new SettingFragment())
                                .commit();
                    }

                } else {
                    // 2ペインでない場合には、設定画面を起動する
                    Intent intent = new Intent(this, SettingActivity.class);
                    startActivity(intent);
                }
                return true;

            // 保存アクション
            case R.id.action_save:
                // このアクションが、このActivityにあるのは、2ペインの場合のみ
                MemoFragment memoFragment = (MemoFragment)getFragmentManager()
                        .findFragmentById(R.id.MemoFragment);
                memoFragment.save();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMemoSelected(Uri uri) {
        // メモの一覧からメモが押された
        if (isDualPane) {
            // 2ペインの場合には、メモを更新する
            MemoFragment memoFragment = (MemoFragment)getFragmentManager()
                    .findFragmentById(R.id.MemoFragment);
            memoFragment.load(uri);

        } else {
            // 2ペインでない場合には、メモ画面を起動する
            Intent intent = new Intent(this, MemoActivity.class);
            intent.putExtra(MemoActivity.BUNDLE_KEY_URI, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onSettingChanged() {
        // 設定が変化した場合。
        // この画面で設定が変化するのは、2ペインの場合のみ
        MemoFragment memoFragment = (MemoFragment)getFragmentManager()
                .findFragmentById(R.id.MemoFragment);
        memoFragment.reflectSettings();
    }
}
