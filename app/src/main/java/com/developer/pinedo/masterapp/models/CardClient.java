package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardClient implements Serializable{

    private String card_id;
    private String card_number;
    private String card_titular;


    public CardClient(String card_id, String card_number, String card_titular) {
        this.card_id = card_id;
        this.card_number = card_number;
        this.card_titular = card_titular;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_titular() {
        return card_titular;
    }

    public void setCard_titular(String card_titular) {
        this.card_titular = card_titular;
    }
}

