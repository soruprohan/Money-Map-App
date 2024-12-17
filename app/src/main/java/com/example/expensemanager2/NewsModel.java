package com.example.expensemanager2;
public class NewsModel {
    private String title;
    private String publishDate;
    private String content;
    private String imageUrl;
    private String canonicalLink;

    public NewsModel(String title, String publishDate, String content, String imageUrl, String canonicalLink) {
        this.title = title;
        this.publishDate = publishDate;
        this.content = content;
        this.imageUrl = imageUrl;
        this.canonicalLink = canonicalLink;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCanonicalLink() {
        return canonicalLink;
    }
}
