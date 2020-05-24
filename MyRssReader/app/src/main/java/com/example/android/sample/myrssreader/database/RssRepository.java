package com.example.android.sample.myrssreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.sample.myrssreader.data.Link;
import com.example.android.sample.myrssreader.data.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * Feedや記事の登録・検索・削除を行う
 */
public class RssRepository {

    private RssRepository() {

    }

    public static long insertSite(Context context, Site site) {
        SQLiteDatabase database = new RssDBHelper(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RssDBHelper.Site.TITLE, site.getTitle());
        values.put(RssDBHelper.Site.DESCRIPTION, site.getDescription());
        values.put(RssDBHelper.Site.URL, site.getUrl());

        long id = database.insert(RssDBHelper.Site.TABLE_NAME, null, values);

        database.close();

        return id;
    }

    public static int deleteSite(Context context, long id) {
        SQLiteDatabase database = new RssDBHelper(context).getWritableDatabase();

        int affected = database.delete(
                RssDBHelper.Site.TABLE_NAME,
                RssDBHelper.Site.ID + " = ?",
                new String[]{String.valueOf(id)});

        if (affected > 0) {
            // 記事も削除する
            database.delete(RssDBHelper.Link.TABLE_NAME,
                    RssDBHelper.Link.SITE_ID + " = ?",
                    new String[] {String.valueOf(id)});
        }

        database.close();

        return affected;
    }

    public static List<Site> getAllSites(Context context) {
        SQLiteDatabase database = new RssDBHelper(context).getReadableDatabase();

        List<Site> sites = new ArrayList<>();

        Cursor cursor = database.query(RssDBHelper.Site.TABLE_NAME,
                RssDBHelper.Site.PROJECTION,
                null, null, null, null, null, null);

        while(cursor.moveToNext()) {
            Site site = new Site();
            long feedId = cursor.getLong(cursor.getColumnIndex(RssDBHelper.Site.ID));

            site.setId(feedId);
            site.setTitle(cursor.getString(cursor.getColumnIndex(RssDBHelper.Site.TITLE)));
            site.setDescription(cursor.getString(
                    cursor.getColumnIndex(RssDBHelper.Site.DESCRIPTION)));
            site.setUrl(cursor.getString(cursor.getColumnIndex(RssDBHelper.Site.URL)));

            // 紐づくリンク情報の件数を取得する
            long linksCount = DatabaseUtils.queryNumEntries(database,
                    RssDBHelper.Link.TABLE_NAME, RssDBHelper.Link.SITE_ID + " = ?",
                    new String[]{String.valueOf(feedId)});

            site.setLinkCount(linksCount);

            sites.add(site);
        }

        cursor.close();

        database.close();

        return sites;
    }

    public static int insertLinks(Context context, long feedId, List<Link> links) {
        SQLiteDatabase database = new RssDBHelper(context).getReadableDatabase();

        int insertedRows = 0;

        for(Link link : links) {
            ContentValues values = new ContentValues();
            values.put(RssDBHelper.Link.TITLE, link.getTitle());
            values.put(RssDBHelper.Link.DESCRIPTION, link.getDescription());
            values.put(RssDBHelper.Link.PUB_DATE, link.getPubDate());
            values.put(RssDBHelper.Link.LINK_URL, link.getUrl());
            values.put(RssDBHelper.Link.SITE_ID, feedId);

            long id = database.insertWithOnConflict(RssDBHelper.Link.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE);

            if (id >= 0) {
                // insert成功
                link.setId(id);
                insertedRows++;
            }
        }

        database.close();

        return insertedRows;
    }

    public static List<Link> getAllLinks(Context context) {
        SQLiteDatabase database = new RssDBHelper(context).getReadableDatabase();

        List<Link> links = new ArrayList<>();

        Cursor cursor = database.query(RssDBHelper.Link.TABLE_NAME,
                RssDBHelper.Link.PROJECTION, null, null, null, null,
                RssDBHelper.Link.PUB_DATE + " DESC", null);

        while(cursor.moveToNext()) {
            Link link = new Link();

            link.setId(cursor.getLong(
                    cursor.getColumnIndex(RssDBHelper.Link.ID)));
            link.setTitle(cursor.getString(
                    cursor.getColumnIndex(RssDBHelper.Link.TITLE)));
            link.setDescription(cursor.getString(
                    cursor.getColumnIndex(RssDBHelper.Link.DESCRIPTION)));
            link.setPubDate(cursor.getLong(
                    cursor.getColumnIndex(RssDBHelper.Link.PUB_DATE)));
            link.setUrl(cursor.getString(
                    cursor.getColumnIndex(RssDBHelper.Link.LINK_URL)));
            link.setSiteId(cursor.getLong(
                    cursor.getColumnIndex(RssDBHelper.Link.SITE_ID)));

            links.add(link);
        }

        cursor.close();

        database.close();

        return links;
    }

}
