package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardComment implements Serializable{

    private String card_name;
    private String card_title;
    private String card_description;

    public CardComment(String card_name, String card_title, String card_description) {
        this.card_name = card_name;
        this.card_title = card_title;
        this.card_description = card_description;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_title() {
        return card_title;
    }

    public void setCard_title(String card_title) {
        this.card_title = card_title;
    }

    public String getCard_description() {
        return card_description;
    }

    public void setCard_description(String card_description) {
        this.card_description = card_description;
    }
}

