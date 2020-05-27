package com.example.android.sample.myrssreader;

import android.app.IntentService;
import android.content.Intent;

import com.example.android.sample.myrssreader.data.Site;
import com.example.android.sample.myrssreader.database.RssRepository;
import com.example.android.sample.myrssreader.net.HttpGet;
import com.example.android.sample.myrssreader.parser.RSSParser;

import java.util.List;

public class PollingService extends IntentService {
    private static final String TAG = "PollingService";

    public PollingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Site> sites = RssRepository.getAllSites(this);

        int newArticles = 0;

        // 登録済みの全サイトからダウンロードする
        for (Site site : sites) {
            // RSSフィードをダウンロード
            HttpGet httpGet = new HttpGet(site.getUrl());

            if (!httpGet.get()) {
                // ダウンロードに失敗
                continue;
            }

            RSSParser parser = new RSSParser();

            // ダウンロードしたRSSフィードを解析する
            if (!parser.parse(httpGet.getResponse()) {
                continue;
            }

            newArticles += RssRepository.insertLinks(this, site.getId(),
                    parser.getLinkList());
        }

        if (newArticles > 0) {
            // 新しいリンクがある場合には、通知する
            NotificationUtil.notifyUpdate(this, newArticles);
        }
    }
}
