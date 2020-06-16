package jp.ac.chibafjb.asaka.myplaceapp.location;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 位置情報を保存するデータベース
 */
public class PlaceDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Place.db";

    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "PLACE";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_REGISTER_TIME = "register_time";

    public PlaceDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LATITUDE + " REAL NOT NULL, "
                + COLUMN_LONGITUDE + " REAL NOT NULL, "
                + COLUMN_TIME + " INTEGER NOT NULL, "
                + COLUMN_REGISTER_TIME + " TIMESTAMP DEFAULT (DATETIME('now', 'localtime'))"
                + ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
