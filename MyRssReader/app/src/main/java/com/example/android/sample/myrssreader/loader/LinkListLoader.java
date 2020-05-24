package com.example.android.sample.myrssreader.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.sample.myrssreader.data.Link;
import com.example.android.sample.myrssreader.data.Site;
import com.example.android.sample.myrssreader.database.RssRepository;

import java.util.List;

// リンクの一覧を取得するLoader
public class LinkListLoader extends AsyncTaskLoader<List<Link>> {

    public LinkListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Link> loadInBackground() {
        // 登録されているリンクをすべて取得する
        return RssRepository.getAllLinks(getContext());
    }
}
