package com.example.furnitureapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.furnitureapp.R;
import com.example.furnitureapp.models.Product;
import java.util.List;
import java.util.ArrayList;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private OnProductClickListener listener;
    private boolean isCartMode;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener, boolean isCartMode) {
        this.products = products;
        this.listener = listener;
        this.isCartMode = isCartMode;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        
        // Загрузка изображения из ресурсов
        int imageResourceId = holder.itemView.getContext().getResources()
                .getIdentifier(product.getImg(), "drawable", 
                        holder.itemView.getContext().getPackageName());
        holder.productImage.setImageResource(imageResourceId);
        
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("%d ₸", (int) Math.round(product.getPrice())));
        
        holder.addToCartButton.setText(isCartMode ? "Удалить" : "В корзину");
        
        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
        holder.addToCartButton.setOnClickListener(v -> {
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.button_scale);
            v.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onAddToCartClick(product);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = new ArrayList<>(newProducts);
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        Button addToCartButton;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
} 