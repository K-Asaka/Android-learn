package com.example.android.sample.myrssreader.parser;

import com.example.android.sample.myrssreader.data.Link;
import com.example.android.sample.myrssreader.data.Site;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class RSSParser {

    // サイト情報
    private Site site;
    // リンク情報
    private List<Link> links;

    // RSSフィードの入力ストリームを解析する
    public boolean parse(InputStream in) {
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        dbfactory.setNamespaceAware(false);

        try {
            DocumentBuilder builder = dbfactory.newDocumentBuilder();
            Document document = builder.parse(in);
            in.close();

            // RSSのバージョンを判定し、適切なパーサーを得る
            FeedParser parser = getParser(document);

            if (parser != null && parser.parse(document)) {
                // 解析成功時
                this.site = parser.getSite();   // サイト情報
                this.links = parser.getLinkList();  // リンク情報
                retrun true;        // 成功時、trueを返す
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            // 設定エラーでDocumentBUilderが生成できなかった場合
            // parseできなかった場合
            // フィードの文法がおかしい場合
            e.printStackTrace();
        }
        return false;
    }

    public Site getSite() {
        return site;
    }

    public List<Link> getLinkList() {
        return links;
    }

    private FeedParser getParser(Document document) {
        NodeList children = document.getChildNodes();
        FeedParser parser = null;

        for (int i = 0; i < children.getLength(); i++) {
            String childName = children.item(i).getNodeName();

            // 「rdf:RDF」はRSS1.0、「rss」はRSS2.0とする
            if (childName.equals("rdf:RDF")) {
                // RSS 1.0
                parser = new RSS1Parser();
            } else if (childName.equals("rss")) {
                // RSS 2.0
                parser = new RSS2Parser();
            }
        }
        return parser;
    }
}
