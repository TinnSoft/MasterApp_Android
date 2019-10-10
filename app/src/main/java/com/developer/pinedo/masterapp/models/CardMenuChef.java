package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardMenuChef implements Serializable{

    private int card_id;
    private String card_photo;
    private String card_name;
    private String card_star;
    private String card_description;
    private String card_price;


    public CardMenuChef(int card_id, String card_photo, String card_name, String card_star, String card_description, String card_price) {
        this.card_id = card_id;
        this.card_photo = card_photo;
        this.card_name = card_name;
        this.card_star = card_star;
        this.card_description = card_description;
        this.card_price = card_price;
    }

    public CardMenuChef(String card_photo, String card_name, String card_star, String card_description, String card_price) {
        this.card_id = card_id;
        this.card_photo = card_photo;
        this.card_name = card_name;
        this.card_star = card_star;
        this.card_description = card_description;
        this.card_price = card_price;
    }

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCard_photo() {
        return card_photo;
    }

    public void setCard_photo(String card_photo) {
        this.card_photo = card_photo;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_star() {
        return card_star;
    }

    public void setCard_star(String card_star) {
        this.card_star = card_star;
    }

    public String getCard_description() {
        return card_description;
    }

    public void setCard_description(String card_description) {
        this.card_description = card_description;
    }

    public String getCard_price() {
        return card_price;
    }

    public void setCard_price(String card_price) {
        this.card_price = card_price;
    }

    @Override
    public String toString() {
        return "CardMenuChef{" +
                "card_id=" + card_id +
                ", card_photo='" + card_photo + '\'' +
                ", card_name='" + card_name + '\'' +
                ", card_star='" + card_star + '\'' +
                ", card_description='" + card_description + '\'' +
                ", card_price='" + card_price + '\'' +
                '}';
    }
}

