package com.example.android.sample.myrssreader.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.sample.myrssreader.database.RssRepository;

/**
 * サイトを削除するためのLoader
 */
public class DeleteSiteLoader extends AsyncTaskLoader<Integer>{
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
        return RssRepository.deleteSite(getContext(), id);
    }

}
