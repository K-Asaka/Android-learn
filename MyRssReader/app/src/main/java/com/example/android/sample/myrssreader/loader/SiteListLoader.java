package com.example.android.sample.myrssreader.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.sample.myrssreader.database.RssRepository;
import com.example.android.sample.myrssreader.data.Site;

import java.util.List;

/**
 * サイトの一覧を取得するためのLoader
 */
public class SiteListLoader extends AsyncTaskLoader<List<Site>>{

    public SiteListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Site> loadInBackground() {
        return RssRepository.getAllSites(getContext());
    }
}
