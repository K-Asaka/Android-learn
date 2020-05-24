package com.example.android.sample.myrssreader.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.sample.myrssreader.database.RssRepository;

// RSSフィード配信サイトの登録を解除するLoader
public class DeleteSiteLoader extends AsyncTaskLoader<Integer> {
    // 削除対象のID
    private long id;

    public DeleteSiteLoader(Context context, long id) {
        super(context);
        this.id = id;
    }

    public long getTargetId() {
        return id;
    }

    @Override
    public Integer loadInBackground() {
        // サイトをデータベースから削除する
        return RssRepository.deleteSite(getContext(), id);
    }
}
