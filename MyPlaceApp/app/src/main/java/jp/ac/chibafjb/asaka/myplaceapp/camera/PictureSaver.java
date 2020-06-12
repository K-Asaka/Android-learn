package jp.ac.chibafjb.asaka.myplaceapp.camera;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

class PictureSaver implements Runnable {
    // ファイル名のテンプレート
    private static final String FILE_NAME_TEMPLATE
            = "image-%1$tF-%1$tH-%1$tM-%1$tS-%1$tL.jpg";
    // 出力先ディレクトリ
    private File mOutputDir;
    // 画像データ
    private byte[] mData;

    // 日付文字列として使用するフォーマット(yyyy-mm-dd)
    public final static String DATE_STR_FORMAT = "%1$tF";

    private Context mContext;


    public PictureSaver(Context context, byte[] data) {
        mContext = context;
        mOutputDir = context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        mData = data;
    }

    @Override
    public void run() {
        // ファイル名を作成
        String fileName = String.format(FILE_NAME_TEMPLATE, Calendar.getInstance());
        // 出力先ファイル
        File outputFile = new File(mOutputDir, fileName);

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(outputFile);
            output.write(mData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 画像保存用のDB
        PictureDBHelper helper = new PictureDBHelper(mContext);
        SQLiteDatabase database = helper.getWritableDatabase();

        // ファイルパスと、今日の日付の文字列(yyyy-mm-dd)を保存する
        ContentValues values = new ContentValues();
        values.put(PictureDBHelper.COLUMN_FILE_PATH, outputFile.getAbsolutePath());
        values.put(PictureDBHelper.COLUMN_DATE_STR,
                String.format(DATE_STR_FORMAT, System.currentTimeMillis()));
        database.insert(PictureDBHelper.TABLE_NAME, null, values);
    }
}
