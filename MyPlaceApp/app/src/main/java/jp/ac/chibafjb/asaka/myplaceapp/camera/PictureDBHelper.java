package jp.ac.chibafjb.asaka.myplaceapp.camera;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 日付情報を保存するデータベース
 */
class PictureDBHelper extends SQLiteOpenHelper {
    // データベース名
    private static final String DB_NAME = "Picture.db";
    // データベースのバージョン
    private static final int DB_VERSION = 1;
    // テーブル名
    public static final String TABLE_NAME = "PICTURE";
    // IDカラム
    public static final String COLUMN_ID = "_id";
    // ファイルパスカラム
    public static final String COLUMN_FILE_PATH = "file_path";
    // 日付文字列をキーにして関連付ける
    public static final String COLUMN_DATE_STR = "date_str";
    // 登録日付
    public static final String COLUMN_REGISTER_TIME = "register_time";

    public PictureDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FILE_PATH + " TEXT NOT NULL, "
                + COLUMN_DATE_STR + " TEXT NOT NULL, "
                + COLUMN_REGISTER_TIME + " TIMESTAMP DEFAULT (DATETIME('now', 'localtime'))"
                + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
