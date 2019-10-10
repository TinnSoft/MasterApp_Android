package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardChef implements Serializable{

    private int card_id;
    private String card_photo;
    private String card_name;
    private String card_star;
    private String card_menu;
    private String card_price;
    private String card_category;

    public CardChef(int card_id,String card_photo, String card_name, String card_star, String card_menu, String card_price, String card_category) {
        this.card_id = card_id;
        this.card_photo = card_photo;
        this.card_name = card_name;
        this.card_star = card_star;
        this.card_menu = card_menu;
        this.card_price = card_price;
        this.card_category = card_category;
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

    public String getCard_menu() {
        return card_menu;
    }

    public void setCard_menu(String card_menu) {
        this.card_menu = card_menu;
    }

    public String getCard_price() {
        return card_price;
    }

    public void setCard_price(String card_price) {
        this.card_price = card_price;
    }

    public String getCard_category() {
        return card_category;
    }

    public void setCard_category(String card_category) {
        this.card_category = card_category;
    }
}

