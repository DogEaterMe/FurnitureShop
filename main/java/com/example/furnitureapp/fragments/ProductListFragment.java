package com.example.furnitureapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductListFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    private RecyclerView productsRecycler;
    private SearchView searchView;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private String currentCategory;
    private CartManager cartManager;
    private ProgressBar progressBar;
    private Spinner sortSpinner;
    private CheckBox searchInDescription;
    private static final int SORT_BY_NAME = 0;
    private static final int SORT_BY_PRICE_ASC = 1;
    private static final int SORT_BY_PRICE_DESC = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productsRecycler = view.findViewById(R.id.products_recycler);
        searchView = view.findViewById(R.id.search_view);
        progressBar = view.findViewById(R.id.progress_bar);
        sortSpinner = view.findViewById(R.id.sort_spinner);
        searchInDescription = view.findViewById(R.id.search_in_description);

        cartManager = CartManager.getInstance(requireContext());
        
        setupRecyclerView();
        setupSearchView();
        setupSortSpinner();
        
        if (getArguments() != null) {
            currentCategory = getArguments().getString("category", null);
            String searchQuery = getArguments().getString("search_query", null);
            
            if (searchQuery != null && !searchQuery.isEmpty()) {
                searchView.setQuery(searchQuery, false);
            }
        }
        
        loadProducts();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(new ArrayList<>(), this, false);
        productsRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        productsRecycler.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Поиск товаров");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortProducts(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadProducts() {
        showProgress(true);
        FirebaseManager.getInstance(requireContext()).loadProducts(new FirebaseManager.OnProductsLoadedListener() {
            @Override
            public void onProductsLoaded(List<Product> products) {
                if (!isAdded()) return;
                
                Log.d("ProductListFragment", "Loaded products: " + products.size());
                for (Product product : products) {
                    Log.d("ProductListFragment", "Product: " + product.getName() + 
                          ", Category: " + product.getCategory());
                }
                
                allProducts = products;
                filterProducts(searchView.getQuery().toString());
                showProgress(false);
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                
                Log.e("ProductListFragment", "Error loading products: " + error);
                showProgress(false);
                showError(error);
            }
        });
    }

    private void sortProducts(int sortType) {
        List<Product> sortedList = new ArrayList<>(allProducts);
        
        switch (sortType) {
            case SORT_BY_NAME:
                Collections.sort(sortedList, 
                    (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
            case SORT_BY_PRICE_ASC:
                Collections.sort(sortedList, 
                    (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case SORT_BY_PRICE_DESC:
                Collections.sort(sortedList, 
                    (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
        }
        
        filterProducts(searchView.getQuery().toString(), sortedList);
    }

    private void filterProducts(String query) {
        filterProducts(query, allProducts);
    }

    private void filterProducts(String query, List<Product> products) {
        if (products == null) {
            adapter.updateProducts(new ArrayList<>());
            return;
        }

        List<Product> filteredList = products.stream()
                .filter(product -> {
                    // Фильтр по категории
                    if (currentCategory != null && !currentCategory.isEmpty()) {
                        if (!currentCategory.equals(product.getCategory())) {
                            return false;
                        }
                    }
                    
                    // Фильтр по поисковому запросу
                    if (query != null && !query.isEmpty()) {
                        String lowercaseQuery = query.toLowerCase();
                        String name = product.getName().toLowerCase();
                        String description = product.getDescription().toLowerCase();
                        
                        return name.contains(lowercaseQuery) || 
                               (searchInDescription.isChecked() && description.contains(lowercaseQuery));
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());

        adapter.updateProducts(filteredList);
        ItemAnimator.animateRecyclerView(requireContext(), productsRecycler);
    }

    @Override
    public void onProductClick(Product product) {
        Bundle args = new Bundle();
        args.putInt("productId", product.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_products_list_to_product_detail, args);
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartManager.addToCart(product);
        Toast.makeText(requireContext(), "Товар добавлен в корзину", 
                Toast.LENGTH_SHORT).show();
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        if (getView() == null) return;
        
        Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Повторить", v -> loadProducts())
                .show();
    }
} 