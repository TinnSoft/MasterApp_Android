package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardFilters implements Serializable {

    private String card_id;
    private String card_photo;
    private String card_title;
    private String card_slug;

    public CardFilters(String card_id, String card_photo, String card_title, String card_slug) {
        this.card_id = card_id;
        this.card_photo = card_photo;
        this.card_title = card_title;
        this.card_slug = card_slug;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_photo() {
        return card_photo;
    }

    public void setCard_photo(String card_photo) {
        this.card_photo = card_photo;
    }

    public String getCard_title() {
        return card_title;
    }

    public void setCard_title(String card_title) {
        this.card_title = card_title;
    }

    public String getCard_slug() {
        return card_slug;
    }

    public void setCard_slug(String card_slug) {
        this.card_slug = card_slug;
    }
}