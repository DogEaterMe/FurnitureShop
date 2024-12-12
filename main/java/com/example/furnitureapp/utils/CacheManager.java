package com.example.furnitureapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.furnitureapp.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CacheManager {
    private static CacheManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private static final String PREF_NAME = "cache_preferences";
    private static final String PRODUCTS_CACHE = "products_cache";
    private static final String LAST_UPDATE = "last_update";
    private static final long CACHE_VALIDITY_PERIOD = 24 * 60 * 60 * 1000; // 24 часа

    private CacheManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context.getApplicationContext());
        }
        return instance;
    }

    public void cacheProducts(List<Product> products) {
        String jsonProducts = gson.toJson(products);
        preferences.edit()
                .putString(PRODUCTS_CACHE, jsonProducts)
                .putLong(LAST_UPDATE, System.currentTimeMillis())
                .apply();
    }

    public List<Product> getCachedProducts() {
        String jsonProducts = preferences.getString(PRODUCTS_CACHE, null);
        if (jsonProducts == null) return new ArrayList<>();

        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(jsonProducts, type);
    }

    public boolean isCacheValid() {
        long lastUpdate = preferences.getLong(LAST_UPDATE, 0);
        return System.currentTimeMillis() - lastUpdate < CACHE_VALIDITY_PERIOD;
    }

    public void clearCache() {
        preferences.edit().clear().apply();
    }
} 