package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardOverView implements Serializable{

    private int card_id;
    private String card_photo;
    private String card_name;
    private int card_amount;
    private String card_price;
    private int product_id;

    public CardOverView(int card_id,String card_photo, String card_name, int card_amount, String card_price,int product_id) {
        this.card_id = card_id;
        this.card_photo = card_photo;
        this.card_name = card_name;
        this.card_amount = card_amount;
        this.card_price = card_price;
        this.product_id = product_id;
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

    public int getCard_amount() {
        return card_amount;
    }

    public void setCard_amount(int card_amount) {
        this.card_amount = card_amount;
    }

    public String getCard_price() {
        return card_price;
    }

    public void setCard_price(String card_price) {
        this.card_price = card_price;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
}

