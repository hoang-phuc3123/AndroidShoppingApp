package com.project.mainprojectprm231.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.project.mainprojectprm231.R;
import com.project.mainprojectprm231.adapters.BannerAdapter;
import com.project.mainprojectprm231.adapters.ProductAdapter;
import com.project.mainprojectprm231.models.ApiResponse;
import com.project.mainprojectprm231.models.Product;
import com.project.mainprojectprm231.networking.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProductsFragment extends Fragment {
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredProductList;
    private EditText searchEditText;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        searchEditText = view.findViewById(R.id.search_edit_text);
        view.findViewById(R.id.filter_button).setOnClickListener(v -> showFilterDialog());

        bannerViewPager = view.findViewById(R.id.banner_viewpager);
        bannerTabLayout = view.findViewById(R.id.banner_tab_layout);

        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        productAdapter = new ProductAdapter(filteredProductList);
        recyclerView.setAdapter(productAdapter);

        setupSearch();
        setupBannerCarousel();
        fetchProducts();

        return view;
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString());
            }
        });
    }

    private void filterProducts(String query) {
        filteredProductList.clear();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                filteredProductList.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    private void showFilterDialog() {
        // TODO: Implement category filter dialog
        Toast.makeText(getContext(), "Category filter not implemented yet", Toast.LENGTH_SHORT).show();
    }

    private void setupBannerCarousel() {
        List<String> bannerUrls = Arrays.asList(
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2071&q=80",
                "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2070&q=80",
                "https://images.unsplash.com/photo-1603302576837-37561b2e2302?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2068&q=80"
        );

        BannerAdapter bannerAdapter = new BannerAdapter(bannerUrls);
        bannerViewPager.setAdapter(bannerAdapter);

//        new TabLayoutMediator(bannerTabLayout, bannerViewPager,
//            (tab, position) -> {
//                View tabView = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
//                tab.setCustomView(tabView);
//            }
//        ).attach();

        // Auto-scroll feature
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                int currentItem = bannerViewPager.getCurrentItem();
                int totalItems = bannerAdapter.getItemCount();
                int nextItem = (currentItem + 1) % totalItems;
                bannerViewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000); // Change banner every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void fetchProducts() {
        ApiClient.fetchProducts(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    ApiResponse apiResponse = gson.fromJson(jsonResponse, ApiResponse.class);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null) {
                                productList.clear();
                                productList.addAll(apiResponse.getData().getContent());
                                filteredProductList.clear();
                                filteredProductList.addAll(productList);
                                productAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "No products available", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }
        });
    }
}