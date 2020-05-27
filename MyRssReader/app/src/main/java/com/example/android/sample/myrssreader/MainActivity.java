package com.example.android.sample.myrssreader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.android.sample.myrssreader.data.Link;

public class MainActivity extends AppCompatActivity
        implements SiteListFragment.SiteListFragmentListener,
        LinkListFragment.LinkListFragmentListener {

    // 定期フェッチのジョブID
    private static final int JOB_FETCH_FEED = 1;
    // ジョブの実行間隔(ms)
    private static final long INTERVAL = 60L * 60L * 1000L;
    // 2ペインの画面かどうか
    private boolean mIsDualPane = false;
    // ナビゲーションドロワーのトグル
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIsDualPane = findViewById(R.id.DualPaneContainer) != null;

        // NavigationDrawerの設定を行う
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        // ドロワーのトグルを有効にする
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(mDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // 定期実行するタスクを設定する
        setRepeatingTask();
    }

    private void setRepeatingTask() {
        // 定期的に新しい記事がないかをチェックする
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // API Levelが21以上の場合には、JobSchedulerを使用する
            ComponentName jobService = new ComponentName(this, PollingJob.class);

            JobInfo fetchJob = new JobInfo.Builder(JOB_FETCH_FEED, jobService)
                    .setPeriodic(INTERVAL)
                    .setPersisted(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();

            // JoinSchedulerを獲得する
            JobScheduler scheduler = (JobScheduler)getSystemService(
                    Context.JOB_SCHEDULER_SERVICE);

            // ジョブを登録する
            scheduler.schedule(fetchJob);
        } else {
            // API Level 21未満の場合には、AlarmManagerを使用する
            Intent serviceIntent = new Intent(this, PollingService.class);
            // すでに登録されているかをチェックする
            boolean isExist = (PendingIntent.getService(this, JOB_FETCH_FEED,
                    serviceIntent, PendingIntent.FLAG_NO_CREATE) != null);

            if (!isExist) {
                PendingIntent operation = PendingIntent.getService(
                        this, JOB_FETCH_FEED, serviceIntent,
                        PendingIntent.FLAG_NO_CREATE);

                // AlarmManagerを使用する
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

                long trigger = SystemClock.elapsedRealtime() + INTERVAL;

                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                        trigger, INTERVAL, operation);
            }
        }
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

    @Override
    public void onLinkClicked(@NonNull Link link) {
        // リンクがタップされたら、リンク先のWebページを開く

        if (mIsDualPane) {
            // 横幅が広い場合には、WebViewFragmentをDetailに入れる
            WebPageFragment fragment = WebPageFragment.newInstance(link.getUrl());
            getFragmentManager().beginTransaction()
                    .replace(R.id.DetailContainer, fragment)
                    .commit();
        } else {
            // 横幅が狭い場合には、Chrome Custom Tabsを使用する

            // ツールバーの色
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);

            // CustomTabsIntentを作成するためのビルダークラス
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

            // Custom Tabsの設定を行い、CustomTabsIntentを作成する
            // Webサイトのタイトルを表示する
            CustomTabsIntent intent = builder.setShowTitle(true)
                    .setToolbarColor(colorPrimary)      // ツールバーの色を指定する
                    .build();

            // Chrome Custom TabsでWebページを開く
            intent.launchUrl(this, Uri.parse(link.getUrl()));
        }
    }

    @Override
    public void onSiteDeleted(long id) {
        LinkListFragment fragment = (LinkListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.MasterContainer);

        if (fragment != null) {
            fragment.removeLinks(id);
        }
    }

    @Override
    public void onSiteAdded() {
        LinkListFragment fragment = (LinkListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.MasterContainer);

        if (fragment != null) {
            fragment.reload();
        }
    }
}
