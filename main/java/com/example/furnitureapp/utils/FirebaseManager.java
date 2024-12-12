package com.example.furnitureapp.utils;



import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.furnitureapp.models.Order;
import com.example.furnitureapp.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final DatabaseReference database;
    private Context context;
    
    private FirebaseManager(Context context) {
        this.context = context.getApplicationContext();
        database = FirebaseDatabase.getInstance().getReference();
    }
    
    public static FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }
    
    public interface OnProductsLoadedListener {
        void onProductsLoaded(List<Product> products);
        void onError(String error);
    }
    
    public interface OnProductLoadedListener {
        void onProductLoaded(Product product);
        void onError(String error);
    }
    
    public void loadProducts(OnProductsLoadedListener listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            // Если нет сети, загружаем из кэша
            CacheManager cacheManager = CacheManager.getInstance(context);
            if (cacheManager.isCacheValid()) {
                List<Product> cachedProducts = cacheManager.getCachedProducts();
                if (!cachedProducts.isEmpty()) {
                    listener.onProductsLoaded(cachedProducts);
                    return;
                }
            }
            listener.onError("Нет подключения к интернету");
            return;
        }

        database.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> products = new ArrayList<>();
                
                Log.d("FirebaseManager", "Raw data from Firebase:");
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Log.d("FirebaseManager", "Product data: " + productSnapshot.getValue());
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        products.add(product);
                    } else {
                        Log.e("FirebaseManager", "Failed to parse product from: " + productSnapshot.getValue());
                    }
                }
                
                // Кэшируем полученные данные
                CacheManager.getInstance(context).cacheProducts(products);
                listener.onProductsLoaded(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseManager", "Database error: " + error.getMessage());
                listener.onError(error.getMessage());
            }
        });
    }
    
    public void submitOrder(String address, String phone, List<Product> products) {
        DatabaseReference orderRef = database.child("orders").push();
        Order order = new Order(address, phone, products);
        orderRef.setValue(order);
    }
    
    public void loadProductById(int productId, OnProductLoadedListener listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            // Если нет сети, ищем в кэше
            List<Product> cachedProducts = CacheManager.getInstance(context).getCachedProducts();
            for (Product product : cachedProducts) {
                if (product.getId() == productId) {
                    listener.onProductLoaded(product);
                    return;
                }
            }
            listener.onError("Нет подключения к интернету");
            return;
        }

        database.child("products").orderByChild("_id").equalTo(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            if (product != null) {
                                listener.onProductLoaded(product);
                                return;
                            }
                        }
                        listener.onError("Товар не найден");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }
} 