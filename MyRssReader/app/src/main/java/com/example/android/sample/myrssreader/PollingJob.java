package com.example.android.sample.myrssreader;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.android.sample.myrssreader.data.Site;
import com.example.android.sample.myrssreader.database.RssRepository;
import com.example.android.sample.myrssreader.net.HttpGet;
import com.example.android.sample.myrssreader.parser.RSSParser;

import java.util.List;

// 条件を指定してJobInfoにまとめる
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollingJob extends JobService {
    private JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        new DownloadTask().execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            List<Site> sites = RssRepository.getAllSites(PollingJob.this);

            int newArticles = 0;

            // すべての登録済みRSS配信サイトからダウンロードする
            for (Site site : sites) {
                HttpGet httpGet = new HttpGet(site.getUrl());

                // ダウンロードする
                if (!httpGet.get()) {
                    continue;
                }

                RSSParser parser = new RSSParser();

                // ダウンロードしたフィードを解析する
                if (!parser.parse(httpGet.getResponse())) {
                    continue;
                }

                newArticles += RssRepository.insertLinks(PollingJob.this,
                        site.getId(), parser.getLinkList());
            }

            if (newArticles > 0) {
                // 新しいリンクがある場合には通知する
                NotificationUtil.notifyUpdate(PollingJob.this, newArticles);
            }

            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Void result) {
            jobFinished(params, false);
        }
    }
}
