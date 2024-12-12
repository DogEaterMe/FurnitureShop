package com.example.furnitureapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;
import com.example.furnitureapp.R;
import com.example.furnitureapp.adapters.BannerAdapter;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private ViewPager2 bannerViewPager;
    private SearchView searchView;
    private Handler sliderHandler = new Handler();
    private final List<Integer> bannerImages = Arrays.asList(
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        bannerViewPager = view.findViewById(R.id.banner_viewpager);
        searchView = view.findViewById(R.id.search_view);
        
        setupBanner();
        setupSearch();
    }

    private void setupBanner() {
        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        bannerViewPager.setAdapter(bannerAdapter);
        
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private void setupSearch() {
        // Настраиваем внешний вид SearchView
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Поиск товаров");

        // Настраиваем обработку поиска только при явном нажатии кнопки поиска
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    Bundle args = new Bundle();
                    args.putString("search_query", query);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_navigation_home_to_products_list, args);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentPosition = bannerViewPager.getCurrentItem();
            if (currentPosition == bannerImages.size() - 1) {
                bannerViewPager.setCurrentItem(0);
            } else {
                bannerViewPager.setCurrentItem(currentPosition + 1);
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
} 