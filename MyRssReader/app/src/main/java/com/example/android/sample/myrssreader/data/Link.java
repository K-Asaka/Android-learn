package com.example.android.sample.myrssreader.data;

/**
 * 記事をあらわすオブジェクト
 */
public class Link {

    // データベースの主キー
    private long id;
    // 記事タイトル
    private String title;
    // 概要
    private String description;
    // 発行日
    private long pubDate;
    // 記事へのリンクURL
    private String url;
    // 配信サイトのデータベースの主キー
    private long siteId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPubDate() {
        return pubDate;
    }

    public void setPubDate(long pubDate) {
        this.pubDate = pubDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSiteId() {
        return siteId;
    }

    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }

}
