package com.project.mainprojectprm231.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.project.mainprojectprm231.R;
import com.project.mainprojectprm231.adapters.ProductAdapter;
import com.project.mainprojectprm231.models.Product;
import com.project.mainprojectprm231.networking.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProductsFragment extends Fragment {
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> originalProductList; // Keep the original list for filtering
    private Spinner sortSpinner;
    private Button filterButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns for grid layout

        sortSpinner = view.findViewById(R.id.sort_spinner);
        filterButton = view.findViewById(R.id.filter_button);

        productList = new ArrayList<>();
        originalProductList = new ArrayList<>(); // Keep a copy of the original list for filtering
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        setupSortSpinner();
        setupFilterButton();

        fetchProducts();

        return view;
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortProducts(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sortProducts(int position) {
        switch (position) {
            case 0: // Price: Low to High
                productList.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case 1: // Price: High to Low
                productList.sort((product1, product2) -> Double.compare(product2.getPrice(), product1.getPrice()));
                break;
            case 2: // Popularity
                // Assuming you have a popularity field in the Product model
                // productList.sort(Comparator.comparingInt(Product::getPopularity));
                break;
            case 3: // Category
                // Assuming you have a category field in the Product model
                // productList.sort(Comparator.comparing(Product::getCategory));
                break;
        }
        productAdapter.notifyDataSetChanged();
    }

    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> openFilterDialog());
    }

    private void openFilterDialog() {
        // Create and display a dialog with filtering options
        FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
        filterDialogFragment.setTargetFragment(ProductsFragment.this, 0);
        filterDialogFragment.show(getParentFragmentManager(), "FilterDialog");
    }

    @SuppressLint("NotifyDataSetChanged")
    public void applyFilters(String brand, double minPrice, double maxPrice, int minRating) {
        productList.clear();
        for (Product product : originalProductList) {
            boolean matchesBrand = (brand == null || brand.isEmpty() || (product.getBrand() != null && product.getBrand().equalsIgnoreCase(brand)));
            boolean matchesPrice = false;
            boolean matchesRating = false;

            if (matchesBrand && matchesPrice && matchesRating) {
                productList.add(product);
            }
        }

        if (productList.isEmpty()) {
            Toast.makeText(getContext(), "No products match the filters.", Toast.LENGTH_SHORT).show();
        }

        productAdapter.notifyDataSetChanged();
    }

    private void fetchProducts() {
        ApiClient.fetchProducts(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    Product[] products = gson.fromJson(jsonResponse, Product[].class);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            productList.clear();
                            originalProductList.clear();
                            productList.addAll(Arrays.asList(products));
                            originalProductList.addAll(Arrays.asList(products));
                            productAdapter.notifyDataSetChanged();
                        });
                    }
                }
            }
        });
    }
}