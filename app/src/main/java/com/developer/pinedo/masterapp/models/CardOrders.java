package com.developer.pinedo.masterapp.models;

import com.mercadopago.lite.model.Card;

import java.io.Serializable;

public class CardOrders implements Serializable {

    private int id;
    private String date_service;
    private String date_service_formated;
    private String restriction;
    private String reason;
    private String address;
    private String client_id;
    private String client;
    private String photo_client;
    private String status_id;
    private String status;
    private String supplier;
    private String supplier_short;
    private String photo_supplier;
    private String supplier_id;
    private String subtotal;

    public CardOrders(int id, String date_service, String date_service_formated, String restriction, String reason, String address, String client_id, String client, String photo_client, String status_id, String status, String supplier, String supplier_short, String photo_supplier, String supplier_id, String subtotal) {
        this.id = id;
        this.date_service = date_service;
        this.date_service_formated = date_service_formated;
        this.restriction = restriction;
        this.reason = reason;
        this.address = address;
        this.client_id = client_id;
        this.client = client;
        this.photo_client = photo_client;
        this.status_id = status_id;
        this.status = status;
        this.supplier = supplier;
        this.supplier_short = supplier_short;
        this.photo_supplier = photo_supplier;
        this.supplier_id = supplier_id;
        this.subtotal = subtotal;
    }

    public CardOrders(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate_service() {
        return date_service;
    }

    public void setDate_service(String date_service) {
        this.date_service = date_service;
    }

    public String getDate_service_formated() {
        return date_service_formated;
    }

    public void setDate_service_formated(String date_service_formated) {
        this.date_service_formated = date_service_formated;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getPhoto_client() {
        return photo_client;
    }

    public void setPhoto_client(String photo_client) {
        this.photo_client = photo_client;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplier_short() {
        return supplier_short;
    }

    public void setSupplier_short(String supplier_short) {
        this.supplier_short = supplier_short;
    }

    public String getPhoto_supplier() {
        return photo_supplier;
    }

    public void setPhoto_supplier(String photo_supplier) {
        this.photo_supplier = photo_supplier;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "CardOrders{" +
                "id=" + id +
                ", date_service='" + date_service + '\'' +
                ", date_service_formated='" + date_service_formated + '\'' +
                ", restriction='" + restriction + '\'' +
                ", reason='" + reason + '\'' +
                ", address='" + address + '\'' +
                ", client_id='" + client_id + '\'' +
                ", client='" + client + '\'' +
                ", photo_client='" + photo_client + '\'' +
                ", status_id='" + status_id + '\'' +
                ", status='" + status + '\'' +
                ", supplier='" + supplier + '\'' +
                ", supplier_short='" + supplier_short + '\'' +
                ", photo_supplier='" + photo_supplier + '\'' +
                ", supplier_id='" + supplier_id + '\'' +
                ", subtotal='" + subtotal + '\'' +
                '}';
    }
}

