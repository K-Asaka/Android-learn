package com.example.android.sample.myrssreader.parser;

import android.text.TextUtils;

import com.example.android.sample.myrssreader.data.Link;
import com.example.android.sample.myrssreader.data.Site;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static android.provider.DocumentsContract.*;

public class RSS1Parser implements FeedParser {

    private Site site;
    private List<Link> links;

    @Override
    public boolean parse(Document document) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try {
            this.site = new Site();

            // チャンネルのタイトル
            String title = xPath.evaluate("//channel/title/text()", document);
            this.site.setTitle(title);

            // チャンネルの説明
            String description = xPath.evaluate("//channel/description/text()", document);

            // リンクリスト
            this.links = new ArrayList<>();

            // このドキュメント内の<item>要素をすべて取り出す
            NodeList items = (NodeList)xPath.evaluate("//item", document, XPathConstants.NODESET);

            // 日付文字列をDate型に変換するためのDateFormatter
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.ENGLISH);

            // リンク数
            int article_count = items.getLength();
            for (int i = 0; i < article_count; i++) {
                Node item = items.item(i);

                // リンク情報
                Link link = new Link();
                link.setTitle(xPath.evaluate("./title/text()", item));  // タイトル
                link.setUrl(xPath.evaluate("./link/text()", item)); // URL
                link.setDescription(xPath.evaluate("./description/text()", item));  // 説明

                String pubDate = xPath.evaluate("./date/text()", item);     // 発行日

                if (TextUtils.isEmpty(pubDate)) {
                    link.setPubDate(-1L);
                } else {
                    Date publishTime = dateFormat.parse(pubDate);
                    link.setPubDate(publishTime.getTime());
                }
                links.add(link);
            }
            return true;
        } catch (XPathExpressionException | ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public List<Link> getLinkList() {
        return links;
    }
}
