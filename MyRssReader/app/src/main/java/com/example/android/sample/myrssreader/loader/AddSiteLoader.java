package com.example.android.sample.myrssreader.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.example.android.sample.myrssreader.data.Link;
import com.example.android.sample.myrssreader.data.Site;
import com.example.android.sample.myrssreader.database.RssRepository;
import com.example.android.sample.myrssreader.net.HttpGet;
import com.example.android.sample.myrssreader.parser.RSSParser;

import java.io.InputStream;
import java.util.List;

public class AddSiteLoader extends AsyncTaskLoader<Site> {
    private String url;

    public AddSiteLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public Site loadInBackground() {
        if (!TextUtils.isEmpty(this.url)) {
            // RSSフィードをダウンロード
            HttpGet httpGet = new HttpGet(this.url);
            if (!httpGet.get()) {
                // 通信に失敗
                return null;
            }
            // ダウンロードしたレスポンスの解析
            InputStream in = httpGet.getResponse();
            RSSParser parser = new RSSParser();

            if (!parser.parse(in)) {
                // 解析に失敗
                return null;
            }

            // 解析結果を取り出す
            Site site = parser.getSite();
            List<Link> links = parser.getLinkList();

            // DBに登録する
            site.setUrl(url);
            site.setLinkCount(links.size());

            // サイトを登録する
            long feedId = RssRepository.insertSite(getContext(), site);
            site.setId(feedId);

            if (feedId > 0 && links.size() > 0) {
                // 記事を登録する
                RssRepository.insertLinks(getContext(), feedId, links);

                return site;
            }
        }
        return null;
    }
}
