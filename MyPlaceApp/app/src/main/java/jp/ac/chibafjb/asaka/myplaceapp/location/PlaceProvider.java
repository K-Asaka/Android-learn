package jp.ac.chibafjb.asaka.myplaceapp.location;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.support.annotation.NonNull;

/**
 * メモ管理用のContentProvider
 */
public class PlaceProvider extends ContentProvider {

    // <authority>
    private static final String AUTHORITY    = "jp.ac.chibafjb.asaka.myplaceapp.place";
    // <path>
    private static final String CONTENT_PATH = "places";

    // MIME TYPEのプレフィックス。
    // 複数要素にはvnd.android.cursor.dirを、
    // 単一要素にはvnd.android.cursor.itemを使用することとされている
    public static final String MIME_DIR_PREFIX = "vnd.android.cursor.dir/";

    // 独自のMIME TYPEを設定する。
    public static final String MIME_ITEM = "vnd.myplaceapp.place";
    public static final String MIME_TYPE_MULTIPLE = MIME_DIR_PREFIX + MIME_ITEM;

    // このContentProviderがハンドルするURI
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);

    // 位置情報のリストのリクエスト
    private static final int URI_MATCH_PLACE_LIST = 1;

    // URIとの一致をチェックするUriMatcher
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sMatcher.addURI(AUTHORITY, CONTENT_PATH, URI_MATCH_PLACE_LIST);
    }

    // 位置情報用のデータベース
    private SQLiteDatabase mPlaceDB;

    @Override
    public boolean onCreate() {
        // SQLiteDatabaseオブジェクトを取得する
        PlaceDBHelper placeHelper = new PlaceDBHelper(getContext());
        mPlaceDB = placeHelper.getWritableDatabase();

        return true;
    }

    // query()、insert()、update()、delete()の各処理で最初に署名チェックを行う
    private boolean checkSignaturePermission() {
        // 自プロセスのPID
        int myPid = android.os.Process.myPid();
        // 呼び出し元のPID
        int callingPid = Binder.getCallingPid();

        // 自身で使用しているなら、チェック不要
        if(myPid == callingPid) {
            return true;
        }

        Context context = getContext();

        // onCreate()が呼ばれていない段階だと、getContext()はnullを返す
        if (context == null) {
            return false;
        }

        PackageManager packagemanager = context.getPackageManager();

        // 自身のパッケージ名
        String myPackage = context.getPackageName();

        // minSdkVersionが19以上なので、getCallingPackage()を使用する
        String callingPackage =  getCallingPackage();

        // 2つのアプリの署名を比較して、一致している場合にtrueを返す
        return packagemanager.checkSignatures(myPackage, callingPackage)
                == PackageManager.SIGNATURE_MATCH;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // 呼び出し元の署名をチェックする
        if (!checkSignaturePermission()) {
            // 署名が異なる場合にはSecurityExceptionをスローする
            throw new SecurityException();
        }

        // URIが正しいことをチェックしておく
        Cursor cursor;
        if (sMatcher.match(uri) == URI_MATCH_PLACE_LIST) {
            // limitやdistinctを有効にする
            String limit = uri.getQueryParameter("limit");
            boolean distinct = "true".equalsIgnoreCase(
                    uri.getQueryParameter("distinct"));

            cursor = mPlaceDB.query(distinct, PlaceDBHelper.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder, limit);

        } else {
            throw new IllegalArgumentException("invalid uri: " + uri);
        }

        // 指定したURIへの通知イベントを受信するようにする
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        if (sMatcher.match(uri) == URI_MATCH_PLACE_LIST) {
            return MIME_TYPE_MULTIPLE;
        } else {
            throw new IllegalArgumentException("invalid uri: " + uri);
        }

    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        // 呼び出し元の署名をチェックする
        if (!checkSignaturePermission()) {
            // 署名が異なる場合にはSecurityExceptionをスローする
            throw new SecurityException();
        }

        // 入力値の検証を行う
        if (!validateInput(values)) {
            // 入力値がおかしい場合にはIllegalArgumentExceptionをスローする
            throw new IllegalArgumentException("invalid values");
        }

        int match = sMatcher.match(uri);

        // IDを指定してinsertはおかしいので、IDなし以外は例外をスローする
        if (match == URI_MATCH_PLACE_LIST) {
            long id = mPlaceDB.insertOrThrow(PlaceDBHelper.TABLE_NAME, null, values);

            if (id >= 0) {
                // 渡されたURIに、IDを付けて返す
                Uri newUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(id));
                // 変更を通知する
                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(newUri, null);
                }

                return newUri;
            } else {
                // insertに失敗した場合
                return null;
            }
        }

        throw new IllegalArgumentException("invalid uri: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // 呼び出し元の署名をチェックする
        if (!checkSignaturePermission())
            throw new SecurityException();

        if (sMatcher.match(uri) == URI_MATCH_PLACE_LIST) {
            int affected = mPlaceDB.delete(PlaceDBHelper.TABLE_NAME,
                    selection, selectionArgs);

            // 変更を通知する
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }

            return affected;
        }

        throw new IllegalArgumentException("invalid uri: " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // 呼び出し元の署名をチェックする
        if (!checkSignaturePermission())
            throw new SecurityException();

        if (sMatcher.match(uri) == URI_MATCH_PLACE_LIST) {
            int affected = mPlaceDB.update(PlaceDBHelper.TABLE_NAME,
                    values, selection, selectionArgs);

            // 変更を通知する
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }

            return affected;
        }

        throw new IllegalArgumentException("invalid uri: " + uri);
    }

    private boolean validateInput(ContentValues values) {
        // 必須項目を満たしていることを確認する
        return values.getAsDouble(PlaceDBHelper.COLUMN_LATITUDE) != null
                && values.getAsDouble(PlaceDBHelper.COLUMN_LONGITUDE) != null
                && values.getAsInteger(PlaceDBHelper.COLUMN_TIME) != null;
    }
}

