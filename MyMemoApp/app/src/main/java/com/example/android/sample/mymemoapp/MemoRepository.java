package com.example.android.sample.mymemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * MemoProviderと、Memo情報を必要とするクラスの橋渡しをする
 */
public class MemoRepository {

    // ファイル名フォーマット prefix-yyyy-mm-dd-HH-MM-SS.txt
    private static final String MEMO_FILE_FORMAT = "%1$s-%2$tF-%2$tH-%2$tM-%2$tS.txt";

    // インスタンスを作らせない
    private MemoRepository() {}

    // メモを新規に保存する
    public static Uri create(Context context, String memo) {

        // 出力先ディレクトリを取得
        File outputDir = getOutputDir(context);

        if (outputDir == null) {
            // 何らかの原因でディレクトリが見つからなかった
            return null;
        }

        File outputFile = getFileName(context, outputDir);

        if (outputFile == null
                || !writeToFile(outputFile, memo)) {
            // ファイルの書き込みに失敗した場合
            return null;
        }

        String title = memo.length() > 10 ? memo.substring(0, 10) : memo;

        ContentValues values = new ContentValues();
        values.put(MemoDBHelper.TITLE, title);
        values.put(MemoDBHelper.DATA, outputFile.getAbsolutePath());
        values.put(MemoDBHelper.DATE_ADDED, System.currentTimeMillis());

        return context.getContentResolver().insert(MemoProvider.CONTENT_URI, values);
    }

    // 既存のメモを更新する
    public static int update(Context context, Uri uri, String memo) {

        String id = uri.getLastPathSegment();

        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MemoDBHelper.DATA}, MemoDBHelper._ID + " = ?", new String[]{id}, null);

        if (cursor == null) {
            return 0;
        }

        String filePath = null;
        while (cursor.moveToNext()) {
            filePath = cursor.getString(cursor.getColumnIndex(MemoDBHelper.DATA));
        }

        cursor.close();

        if (TextUtils.isEmpty(filePath)) {
            return 0;
        }

        File outputFile = new File(filePath);

        if (writeToFile(outputFile, memo)) {
            // ファイルの書き込みに失敗した場合
            return 0;
        }

        return 1;
    }

    // メモの出力先ディレクトリを取得する
    private static File getOutputDir(Context context) {
        File outputDir;

        if (Build.VERSION.SDK_INT >= 19) {
            outputDir = context.getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS);
        } else {
            outputDir = new File(context.getExternalFilesDir(null),
                    "Documents");
        }

        if (outputDir == null) {
            // 外部ストレージがマウントされていない等の場合
            return null;
        }

        boolean isExist = true;

        if (!outputDir.exists()
                || !outputDir.isDirectory()) {
            isExist = outputDir.mkdirs();
        }

        if (isExist) {
            return outputDir;

        } else {
            // ディレクトリの作成に失敗した場合
            return null;
        }
    }

    // 出力先ファイルを取得する
    private static File getFileName(Context context, File outputDir) {
        String fileNamePrefix = SettingPrefUtil.getFileNamePrefix(context);

        Calendar now = Calendar.getInstance();

        String fileName = String.format(MEMO_FILE_FORMAT, fileNamePrefix, now);

        return new File(outputDir, fileName);
    }

    // ファイルにメモを書き込む
    private static boolean writeToFile(File outputFile, String memo) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFile);
            writer.write(memo);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    // メモを読み込む
    public static String findMemoByUri(Context context, Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
            }

        } catch (FileNotFoundException fnfe) {
            // ファイルが削除されるなどして見つからなかった場合
            fnfe.printStackTrace();
            return context.getString(R.string.error_memo_file_not_found);

        } catch (IOException ioe) {
            // ファイルの読み込みに失敗した場合
            ioe.printStackTrace();
            return context.getString(R.string.error_memo_file_load_failed);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }

    // メモの一覧を取得します
    public static Cursor query(Context context) {
        return context.getContentResolver().query(
                MemoProvider.CONTENT_URI,
                null, null, null, MemoDBHelper.DATE_MODIFIED + " DESC");
    }
}
