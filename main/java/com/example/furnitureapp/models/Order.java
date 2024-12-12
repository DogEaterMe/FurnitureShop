package com.example.furnitureapp.models;

import java.util.List;

public class Order {
    private String address;
    private String phone;
    private List<Product> products;
    private long timestamp;

    public Order() {} // Необходим для Firebase

    public Order(String address, String phone, List<Product> products) {
        this.address = address;
        this.phone = phone;
        this.products = products;
        this.timestamp = System.currentTimeMillis();
    }

    // Геттеры и сеттеры
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 