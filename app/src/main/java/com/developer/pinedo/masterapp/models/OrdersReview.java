package com.developer.pinedo.masterapp.models;

import java.io.Serializable;

public class OrdersReview implements Serializable{

    private int id;
    private String product;
    private String image;
    private String units;
    private String subtotal;

    public OrdersReview(int id, String product, String image, String units, String subtotal) {
        this.id = id;
        this.product = product;
        this.image = image;
        this.units = units;
        this.subtotal = subtotal;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "OrdersReview{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", image='" + image + '\'' +
                ", units='" + units + '\'' +
                ", subtotal='" + subtotal + '\'' +
                '}';
    }
}

