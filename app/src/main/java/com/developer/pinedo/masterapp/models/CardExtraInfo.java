package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardExtraInfo implements Serializable {

    private String card_id;
    private String card_title;
    private String card_description;
    private String card_business;
    private String card_date_init;
    private String card_date_end;
    private String card_finished;
    private int card_type_information_id;

    public CardExtraInfo(String card_id, String card_title, String card_description, String card_business, String card_date_init, String card_date_end, String card_finished, int card_type_information_id) {
        this.card_id = card_id;
        this.card_title = card_title;
        this.card_description = card_description;
        this.card_business = card_business;
        this.card_date_init = card_date_init;
        this.card_date_end = card_date_end;
        this.card_finished = card_finished;
        this.card_type_information_id = card_type_information_id;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
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

    public String getCard_business() {
        return card_business;
    }

    public void setCard_business(String card_business) {
        this.card_business = card_business;
    }

    public String getCard_date_init() {
        return card_date_init;
    }

    public void setCard_date_init(String card_date_init) {
        this.card_date_init = card_date_init;
    }

    public String getCard_date_end() {
        return card_date_end;
    }

    public void setCard_date_end(String card_date_end) {
        this.card_date_end = card_date_end;
    }

    public String getCard_finished() {
        return card_finished;
    }

    public void setCard_finished(String card_finished) {
        this.card_finished = card_finished;
    }

    public int getCard_type_information_id() {
        return card_type_information_id;
    }

    public void setCard_type_information_id(int card_type_information_id) {
        this.card_type_information_id = card_type_information_id;
    }

    @Override
    public String toString() {
        return "CardExtraInfo{" +
                "card_id='" + card_id + '\'' +
                ", card_title='" + card_title + '\'' +
                ", card_description='" + card_description + '\'' +
                ", card_business='" + card_business + '\'' +
                ", card_date_init='" + card_date_init + '\'' +
                ", card_date_end='" + card_date_end + '\'' +
                ", card_finished='" + card_finished + '\'' +
                ", card_type_information_id=" + card_type_information_id +
                '}';
    }
}


