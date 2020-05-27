package com.example.android.sample.myrssreader.parser;

import com.example.android.sample.myrssreader.data.Link;
import com.example.android.sample.myrssreader.data.Site;

import org.w3c.dom.Document;

import java.util.List;

/**
 * RSS 1.0 / 2.0 / ATOM用パーサーのインターフェイス
 */
public interface FeedParser {

    // 解析する
    boolean parse(Document document);

    // 解析結果のSiteを取得する
    Site getSite();

    // 解析結果のリンクリストを受け取る
    List<Link> getLinkList();

}
