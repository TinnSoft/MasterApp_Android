package com.developer.pinedo.masterapp.models;

public class SpinnerCategory {
    private int id;
    private String url;
    private String title;
    private String slug;

    public SpinnerCategory(int id, String url, String title, String slug) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.slug = slug;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
