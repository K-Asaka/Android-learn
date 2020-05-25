package com.example.android.sample.myrssreader.net;

import android.renderscript.ScriptGroup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpGet {

    // 接続のタイムアウト(ミリ秒)
    private static final int CONNECT_TIMEOUT_MS = 3000;
    // 読み込みのタイムアウト(ミリ秒)
    private static final int READ_TIMEOUT_MS = 5000;

    // 接続先URL
    private String url;
    // HTTPステータスコード
    private int status;
    // レスポンスの入力ストリーム
    private InputStream in;

    public HttpGet(String url) {
        this.url = url;
    }

    public boolean get() {
        try {
            URL url = new URL(this.url);

            // 通信の設定を行う
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");    // メソッド
            con.setConnectTimeout(CONNECT_TIMEOUT_MS);  // 接続のタイムアウト
            con.setReadTimeout(READ_TIMEOUT_MS);    // 読み込みのタイムアウト
            con.setInstanceFollowRedirects(true);   // リダイレクト許可

            // 接続
            con.connect();

            // ステータスコードの取得
            status = con.getResponseCode();

            if (status == 200) {
                // 成功したら、レスポンスの入力ストリームへの参照を
                // bufferedInputStreamとして持つ
                in = new BufferedInputStream(con.getInputStream());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 失敗
        return false;
    }
    // レスポンスの入力ストリームを返す
    public InputStream getResponse() {
        return in;
    }

    // HTTPステータスコードを返す
    public int getStatus() {
        return status;
    }
}
