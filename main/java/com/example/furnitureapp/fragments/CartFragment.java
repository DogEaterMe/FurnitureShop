package com.example.furnitureapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.furnitureapp.R;
import com.example.furnitureapp.adapters.ProductAdapter;
import com.example.furnitureapp.models.Product;
import com.example.furnitureapp.utils.CartManager;
import com.example.furnitureapp.utils.FirebaseManager;
import com.example.furnitureapp.utils.ItemAnimator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class CartFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    private RecyclerView cartRecycler;
    private TextView totalPrice;
    private Button checkoutButton;
    private CartManager cartManager;
    private ProductAdapter adapter;
    private View emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        cartManager = CartManager.getInstance(requireContext());
        
        cartRecycler = view.findViewById(R.id.cart_recycler);
        totalPrice = view.findViewById(R.id.total_price);
        checkoutButton = view.findViewById(R.id.checkout_button);
        emptyView = view.findViewById(R.id.empty_view);
        
        setupRecyclerView();
        updateCartState();
        
        checkoutButton.setOnClickListener(v -> showCheckoutDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartState();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(cartManager.getCartItems(), this, true);
        cartRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        cartRecycler.setAdapter(adapter);
        ItemAnimator.animateRecyclerView(requireContext(), cartRecycler);
    }

    private void updateCartState() {
        List<Product> cartItems = cartManager.getCartItems();
        adapter.updateProducts(cartItems);
        updateTotal();
        
        boolean isEmpty = cartItems.isEmpty();
        cartRecycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        checkoutButton.setEnabled(!isEmpty);
    }

    private void updateTotal() {
        totalPrice.setText(String.format("Итого: %.2f ₸", cartManager.getTotal()));
    }

    private void showCheckoutDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_checkout, null);
        TextInputEditText addressInput = dialogView.findViewById(R.id.address_input);
        TextInputEditText phoneInput = dialogView.findViewById(R.id.phone_input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Оформление заказа")
                .setView(dialogView)
                .setPositiveButton("Заказать", (dialog, which) -> {
                    String address = addressInput.getText().toString().trim();
                    String phone = phoneInput.getText().toString().trim();
                    
                    if (address.isEmpty()) {
                        showError("Введите адрес доставки");
                        return;
                    }
                    if (phone.isEmpty()) {
                        showError("Введите номер телефона");
                        return;
                    }
                    
                    submitOrder(address, phone);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void submitOrder(String address, String phone) {
        FirebaseManager.getInstance(requireContext()).submitOrder(address, phone, cartManager.getCartItems());
        cartManager.clearCart();
        updateCartState();
        showSuccessMessage();
    }

    private void showSuccessMessage() {
        Snackbar.make(requireView(), "Заказ успешно оформлен", Snackbar.LENGTH_LONG).show();
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProductClick(Product product) {
        Bundle args = new Bundle();
        args.putInt("productId", product.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_cart_to_product_detail, args);
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartManager.removeFromCart(product);
        updateCartState();
        Snackbar.make(requireView(), "Товар удален из корзины", Snackbar.LENGTH_SHORT)
                .setAction("Отменить", v -> {
                    cartManager.addToCart(product);
                    updateCartState();
                })
                .show();
    }
} 