package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class CardOrdersDetail implements Serializable{

    private int id;
    private String product;
    private String description;
    private String price;
    private String quantity;
    private String url_image;
    private String order_id;

    public CardOrdersDetail(int id, String product, String description, String price, String quantity, String url_image, String order_id) {
        this.id = id;
        this.product = product;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.url_image = url_image;
        this.order_id = order_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}

