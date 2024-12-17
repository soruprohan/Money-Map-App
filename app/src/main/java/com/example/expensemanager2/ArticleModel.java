package com.example.expensemanager2;

public class ArticleModel {
    private String id;
    private String title;
    private String publishOn;
    private String imageUrl;
    private String structuredInsights;
    private String articleLink;

    // Constructor
    public ArticleModel(String id, String title, String publishOn, String imageUrl, String structuredInsights, String articleLink) {
        this.id = id;
        this.title = title;
        this.publishOn = publishOn;
        this.imageUrl = imageUrl;
        this.structuredInsights = structuredInsights;
        this.articleLink = articleLink;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPublishOn() { return publishOn; }
    public String getImageUrl() { return imageUrl; }
    public String getStructuredInsights() { return structuredInsights; }
    public String getArticleLink() { return articleLink; }
}
