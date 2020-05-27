package com.example.android.sample.myrssreader.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.sample.myrssreader.database.RssRepository;
import com.example.android.sample.myrssreader.data.Link;

import java.util.List;

/**
 * リンクの一覧を取得するためのLoader
 */
public class LinkListLoader extends AsyncTaskLoader<List<Link>>{

    public LinkListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Link> loadInBackground() {
        return RssRepository.getAllLinks(getContext());
    }
}
