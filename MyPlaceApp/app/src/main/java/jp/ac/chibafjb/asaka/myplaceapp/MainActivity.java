package jp.ac.chibafjb.asaka.myplaceapp;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import jp.ac.chibafjb.asaka.myplaceapp.camera.CameraFragment;
import jp.ac.chibafjb.asaka.myplaceapp.camera.CameraLegacyFragment;
import jp.ac.chibafjb.asaka.myplaceapp.camera.PictureFragment;
import jp.ac.chibafjb.asaka.myplaceapp.location.LoggedDateFragment;
import jp.ac.chibafjb.asaka.myplaceapp.location.LoggedMapFragment;
import jp.ac.chibafjb.asaka.myplaceapp.location.Place;

public class MainActivity extends AppCompatActivity
        implements LoggedDateFragment.LoggedDateFragmentListener,
        View.OnClickListener {

    // ナビゲーションドロワーのトグル
    private ActionBarDrawerToggle mDrawerToggle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 本日の日付文字列
        String date = String.format(Place.DATE_STR_FORMAT,
                System.currentTimeMillis());

        // 地図フラグメントを生成する
        LoggedMapFragment mapFragment = LoggedMapFragment.newInstance(date);
        // ギャラリーフラグメントを生成する
        PictureFragment pictureFragment = PictureFragment.newInstance(date);

        // 両フラグメントを追加する
        getFragmentManager().beginTransaction()
                .replace(R.id.MapContainer, mapFragment)
                .replace(R.id.GalleryContainer, pictureFragment)
                .commit();

        // NavigationDrawerの設定を行う
        DrawerLayout drawerLayout =
                (DrawerLayout)findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.app_name, R.string.app_name);
        // ドロワーのトグルを有効にする
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(mDrawerToggle);

        Toolbar toolbar = (Toolbar)findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        findViewById(R.id.CameraButton).setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // ドロワーのトグルの状態を同期する
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ドロワーからある日付が選ばれた
    @Override
    public void onDateSelected(String date) {
        // 地図に反映する
        LoggedMapFragment mapFragment = (LoggedMapFragment)getFragmentManager()
                .findFragmentById(R.id.MapContainer);
        mapFragment.setDate(date);

        // ギャラリーに反映する
        PictureFragment pictureFragment = (PictureFragment)getFragmentManager()
                .findFragmentById(R.id.GalleryContainer);
        pictureFragment.setDate(date);
    }

    @Override
    public void onClick(View v) {
        // 「撮影する」ボタンが押された
        if (v.getId() == R.id.CameraButton) {
            Fragment cameraFragment;
            // カメラフラグメントを表示する
            if (Build.VERSION.SDK_INT >= 21) {
                cameraFragment = new CameraFragment();
            } else {
                cameraFragment = new CameraLegacyFragment();
            }

            getFragmentManager().beginTransaction()
                    .replace(R.id.CameraContainer, cameraFragment)
                    .addToBackStack(null)   // バックスタックに入れる
                    .commit();
        }
    }
}
