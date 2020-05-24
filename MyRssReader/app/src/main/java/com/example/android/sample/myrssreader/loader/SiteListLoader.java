package com.example.android.sample.myrssreader.loader;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.sample.myrssreader.data.Site;
import com.example.android.sample.myrssreader.database.RssRepository;

import java.util.List;

public class SiteListLoader extends AsyncTaskLoader<List<Site>> {

    public SiteListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Site> loadInBackground() {
        // 登録されているRSSフィード配信サイトをすべて取得する
        return RssRepository.getAllSites(getContext());
    }
}
