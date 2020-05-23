package com.example.android.sample.myrssreader.data;

public class Link {

    // DBの主キー
    private long id;
    // タイトル
    private String title;
    // 説明
    private String description;
    // 発行日
    private long pubDate;
    // リンク先URL
    private String url;
    // このリンクを配信していたRSSフィードのURL
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
