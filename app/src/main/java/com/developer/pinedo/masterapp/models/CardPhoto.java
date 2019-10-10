package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardPhoto implements Serializable{

    private Integer card_id;
    private String card_photo;
    private String card_title;
    private String card_price;
    private String card_description;
    private String card_supplier;
    private String card_photo_supplier;

    public CardPhoto(Integer card_id, String card_photo, String card_title, String card_price, String card_description, String card_supplier, String card_photo_supplier) {
        this.card_id = card_id;
        this.card_photo = card_photo;
        this.card_title = card_title;
        this.card_price = card_price;
        this.card_description = card_description;
        this.card_supplier = card_supplier;
        this.card_photo_supplier = card_photo_supplier;
    }

    public Integer getCard_id() {
        return card_id;
    }

    public void setCard_id(Integer card_id) {
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

    public String getCard_price() {
        return card_price;
    }

    public void setCard_price(String card_price) {
        this.card_price = card_price;
    }

    public String getCard_description() {
        return card_description;
    }

    public void setCard_description(String card_description) {
        this.card_description = card_description;
    }

    public String getCard_supplier() {
        return card_supplier;
    }

    public void setCard_supplier(String card_supplier) {
        this.card_supplier = card_supplier;
    }

    public String getCard_photo_supplier() {
        return card_photo_supplier;
    }

    public void setCard_photo_supplier(String card_photo_supplier) {
        this.card_photo_supplier = card_photo_supplier;
    }
}

