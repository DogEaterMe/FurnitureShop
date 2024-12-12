package com.example.furnitureapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.furnitureapp.R;
import com.example.furnitureapp.models.Product;
import com.example.furnitureapp.utils.CartManager;
import com.example.furnitureapp.utils.FirebaseManager;
import com.google.android.material.snackbar.Snackbar;

public class ProductDetailFragment extends Fragment {
    private ImageView productImage;
    private TextView productName;
    private TextView productPrice;
    private TextView stockStatus;
    private TextView productDescription;
    private Button addToCartButton;
    private ProgressBar progressBar;
    private CartManager cartManager;
    private Product currentProduct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartManager = CartManager.getInstance(requireContext());
        initViews(view);
        loadProduct();
    }

    private void initViews(View view) {
        productImage = view.findViewById(R.id.product_image);
        productName = view.findViewById(R.id.product_name);
        productPrice = view.findViewById(R.id.product_price);
        stockStatus = view.findViewById(R.id.stock_status);
        productDescription = view.findViewById(R.id.product_description);
        addToCartButton = view.findViewById(R.id.add_to_cart_button);
        progressBar = view.findViewById(R.id.progress_bar);

        addToCartButton.setOnClickListener(v -> onAddToCartClick());
    }

    private void loadProduct() {
        showProgress(true);
        int productId = getArguments().getInt("productId");
        
        FirebaseManager.getInstance(requireContext()).loadProductById(productId, 
                new FirebaseManager.OnProductLoadedListener() {
            @Override
            public void onProductLoaded(Product product) {
                if (!isAdded()) return;
                
                currentProduct = product;
                updateUI();
                showProgress(false);
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                
                showProgress(false);
                Snackbar.make(requireView(), error, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Повторить", v -> loadProduct())
                        .show();
            }
        });
    }

    private void updateUI() {
        if (currentProduct == null) return;

        // Загрузка изображения из ресурсов
        int imageResourceId = requireContext().getResources()
                .getIdentifier(currentProduct.getImg(), "drawable", 
                        requireContext().getPackageName());
        productImage.setImageResource(imageResourceId);

        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("%.2f ₽", currentProduct.getPrice()));
        stockStatus.setText(currentProduct.isInStock() ? "В наличии" : "Нет в наличии");
        stockStatus.setTextColor(requireContext().getColor(
                currentProduct.isInStock() ? android.R.color.holo_green_dark : 
                        android.R.color.holo_red_dark));
        productDescription.setText(currentProduct.getDescription());

        updateAddToCartButton();
    }

    private void updateAddToCartButton() {
        boolean isInCart = cartManager.isInCart(currentProduct);
        addToCartButton.setText(isInCart ? "Убрать из корзины" : "В корзину");
    }

    private void onAddToCartClick() {
        if (cartManager.isInCart(currentProduct)) {
            cartManager.removeFromCart(currentProduct);
            Toast.makeText(requireContext(), "Товар удален из корзины", 
                    Toast.LENGTH_SHORT).show();
        } else {
            cartManager.addToCart(currentProduct);
            Toast.makeText(requireContext(), "Товар добавлен в корзину", 
                    Toast.LENGTH_SHORT).show();
        }
        updateAddToCartButton();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        addToCartButton.setEnabled(!show);
    }
} 