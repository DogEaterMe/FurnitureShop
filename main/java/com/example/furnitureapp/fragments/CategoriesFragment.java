package com.example.furnitureapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.furnitureapp.R;
import com.example.furnitureapp.adapters.CategoryAdapter;
import java.util.Arrays;
import java.util.List;

public class CategoriesFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    private RecyclerView categoriesRecycler;
    private final List<String> categories = Arrays.asList("sofa", "closet", "bed", "table");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        categoriesRecycler = view.findViewById(R.id.categories_recycler);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        CategoryAdapter adapter = new CategoryAdapter(categories, this);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoriesRecycler.setAdapter(adapter);
    }

    @Override
    public void onCategoryClick(String category) {
        Bundle args = new Bundle();
        args.putString("category", category);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_categories_to_products_list, args);
    }
} 