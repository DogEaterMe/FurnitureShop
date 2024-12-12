package com.example.furnitureapp.models;

import com.google.firebase.database.PropertyName;

public class Product {
    @PropertyName("_id")
    private int _id;
    
    @PropertyName("_name")
    private String _name = "";
    
    @PropertyName("_description")
    private String _description = "";
    
    @PropertyName("_category")
    private String _category = "";
    
    @PropertyName("_img")
    private String _img = "";
    
    @PropertyName("_inStock")
    private boolean _inStock;
    
    @PropertyName("_price")
    private double _price;

    public Product() {} // Необходим для Firebase

    public Product(int id, String name, String description, String category, 
                  String img, boolean inStock, double price) {
        this._id = id;
        this._name = name;
        this._description = description;
        this._category = category;
        this._img = img;
        this._inStock = inStock;
        this._price = price;
    }

    @PropertyName("_id")
    public int getId() { return _id; }
    
    @PropertyName("_id")
    public void setId(int id) { this._id = id; }
    
    @PropertyName("_name")
    public String getName() { return _name; }
    
    @PropertyName("_name")
    public void setName(String name) { this._name = name; }
    
    @PropertyName("_description")
    public String getDescription() { return _description; }
    
    @PropertyName("_description")
    public void setDescription(String description) { this._description = description; }
    
    @PropertyName("_category")
    public String getCategory() { return _category; }
    
    @PropertyName("_category")
    public void setCategory(String category) { this._category = category; }
    
    @PropertyName("_img")
    public String getImg() { return _img; }
    
    @PropertyName("_img")
    public void setImg(String img) { this._img = img; }
    
    @PropertyName("_inStock")
    public boolean isInStock() { return _inStock; }
    
    @PropertyName("_inStock")
    public void setInStock(boolean inStock) { this._inStock = inStock; }
    
    @PropertyName("_price")
    public double getPrice() { return _price; }
    
    @PropertyName("_price")
    public void setPrice(double price) { this._price = price; }
} 