package com.example.android.sample.myrssreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Feedを管理するためのSQLiteOpenHelper
 */
public class RssDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME ="RssDB";
    private static final int DB_VERSION = 1;

    public static class Site {
        public static final String TABLE_NAME ="SITE";

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String URL = "url";

        public static final String[] PROJECTION = new String[] {
                ID, TITLE, DESCRIPTION, URL
        };

        private static final String CREATE_TABLE_SQL
                = "CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT, "
                + URL + " TEXT NOT NULL UNIQUE "
                + ")";
    }

    public static class Link {
        public static final String TABLE_NAME ="LINK";

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String PUB_DATE = "pubDate";
        public static final String LINK_URL = "linkUrl";
        public static final String SITE_ID = "siteId";
        public static final String REGISTER_TIME = "register_time";

        public static final String[] PROJECTION = new String[] {
                ID, TITLE, DESCRIPTION, PUB_DATE, LINK_URL, SITE_ID, REGISTER_TIME
        };

        private static final String CREATE_TABLE_SQL
                = "CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT NOT NULL, "
                + DESCRIPTION + " TEXT, "
                + PUB_DATE + " INTEGER, "
                + LINK_URL + " TEXT NOT NULL UNIQUE, "
                + SITE_ID + " INTEGER NOT NULL, "
                + REGISTER_TIME + " TIMESTAMP DEFAULT (DATETIME('now', 'localtime'))"
                + ")";
    }

    public RssDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Site.CREATE_TABLE_SQL);
        db.execSQL(Link.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
