package com.example.android.sample.myrssreader.data;

public class Site {

    // DBの主キー
    private long id;
    // タイトル
    private String title;
    // 説明
    private String description;
    // URL
    private String url;
    // 保存されているリンク数
    private long linkCount;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLinkCount() {
        return linkCount;
    }

    public void setLinkCount(long linkCount) {
        this.linkCount = linkCount;
    }
}
