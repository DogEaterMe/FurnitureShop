package com.example.furnitureapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.furnitureapp.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private List<Product> cartItems;
    private static final String PREF_NAME = "cart_preferences";
    private static final String CART_ITEMS = "cart_items";

    private CartManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCartItems();
    }

    public static CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadCartItems() {
        String jsonCartItems = preferences.getString(CART_ITEMS, "");
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        cartItems = gson.fromJson(jsonCartItems, type);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCartItems() {
        String jsonCartItems = gson.toJson(cartItems);
        preferences.edit().putString(CART_ITEMS, jsonCartItems).apply();
    }

    public void addToCart(Product product) {
        if (!cartItems.contains(product)) {
            cartItems.add(product);
            saveCartItems();
        }
    }

    public void removeFromCart(Product product) {
        cartItems.remove(product);
        saveCartItems();
    }

    public List<Product> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void clearCart() {
        cartItems.clear();
        saveCartItems();
    }

    public double getTotal() {
        double total = 0;
        for (Product product : cartItems) {
            total += product.getPrice();
        }
        return total;
    }

    public boolean isInCart(Product product) {
        return cartItems.contains(product);
    }
} 