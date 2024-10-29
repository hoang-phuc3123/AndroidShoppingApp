package com.project.mainprojectprm231.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.project.mainprojectprm231.R;
import com.project.mainprojectprm231.adapters.CartAdapter;
import com.project.mainprojectprm231.models.ApiCartResponse;
import com.project.mainprojectprm231.models.CartItem;
import com.project.mainprojectprm231.networking.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CartFragment extends Fragment {
    private static final String PREF_NAME = "UserPrefs";

    private List<CartItem> cartList;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private TextView totaluniTextReview;
    private double cartTotalPrice = 0.0;
    private double totalunitprice = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        // Hide bottom navigation bar
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.GONE);
        }

        initializeViews(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show bottom navigation bar again when fragment is destroyed
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
    }

    private void loadProductsFragment() {
        Fragment productsFragment = new ProductsFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, productsFragment);
        fragmentTransaction.commit();

        // Update the bottom navigation view to show the correct selected item
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initializeViews(View view) {
        // Initialize cartList
        cartList = new ArrayList<>();

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        totalPriceTextView = view.findViewById(R.id.total_value);
        totaluniTextReview = view.findViewById(R.id.unitpricevalue);
        ImageView backButton = view.findViewById(R.id.back_buttoncart);
        Button checkoutButton = view.findViewById(R.id.button_checkout);
        ImageView deleteAllButton = view.findViewById(R.id.deleteall);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartList, getContext());
        recyclerView.setAdapter(cartAdapter);

        // Set click listeners
        backButton.setOnClickListener(v -> loadProductsFragment());
        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Checkout cart", Toast.LENGTH_SHORT).show();
        });
        deleteAllButton.setOnClickListener(v -> clearAllCartItems());

        // Fetch cart items when the fragment is created
        fetchCartItems();
    }

    private void fetchCartItems() {
        int page = 0;
        int size = cartList.size();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int cartId = sharedPreferences.getInt("cartId", 0);

        ApiClient.fetchCartItems(cartId, page, size, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to fetch cart items", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && getActivity() != null) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    ApiCartResponse apiCartResponse = gson.fromJson(jsonResponse, ApiCartResponse.class);

                    getActivity().runOnUiThread(() -> {
                        if (apiCartResponse != null && apiCartResponse.isSuccess() && apiCartResponse.getData() != null) {
                            cartList.clear();
                            cartList.addAll(apiCartResponse.getData().getContent());

                            if (!cartList.isEmpty()) {
                                cartTotalPrice = 0.0;
                                totalunitprice = 0.0;
                                for (CartItem item : cartList) {
                                    cartTotalPrice += item.getTotalPrice();
                                    totalunitprice += item.getUnitPrice();
                                }

                                totalPriceTextView.setText("$" + String.format("%.2f", cartTotalPrice));
                                totaluniTextReview.setText("$" + String.format("%.2f", totalunitprice));
                                cartAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "No cart items available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "No cart items available", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to fetch cart items", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    public void removeFromCart(int itemId) {
        ApiClient.removeFromCart(itemId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to remove item from cart", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        CartItem itemToRemove = null;
                        for (CartItem item : cartList) {
                            if (item.getItemId() == itemId) {
                                itemToRemove = item;
                                break;
                            }
                        }

                        if (itemToRemove != null) {
                            cartList.remove(itemToRemove);

                            // Recalculate totals
                            cartTotalPrice = 0.0;
                            totalunitprice = 0.0;
                            for (CartItem item : cartList) {
                                cartTotalPrice += item.getTotalPrice();
                                totalunitprice += item.getUnitPrice();
                            }

                            totalPriceTextView.setText("$" + String.format("%.2f", cartTotalPrice));
                            totaluniTextReview.setText("$" + String.format("%.2f", totalunitprice));
                            cartAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to remove item from cart", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void clearAllCartItems() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int cartId = sharedPreferences.getInt("cartId", 0);

        ApiClient.clearCart(cartId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to clear cart items", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        cartList.clear();
                        cartAdapter.notifyDataSetChanged();
                        cartTotalPrice = 0.0;
                        totalunitprice = 0.0;
                        totalPriceTextView.setText("$0.00");
                        totaluniTextReview.setText("$0.00");
                        Toast.makeText(getContext(), "All cart items cleared", Toast.LENGTH_SHORT).show();
                    });
                } else if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to clear cart items", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    public void updateCartItemQuantity(int itemId, int quantity) {
        ApiClient.updateCartItemQuantity(itemId, quantity, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        for (CartItem item : cartList) {
                            if (item.getItemId() == itemId) {
                                item.setQuantity(quantity);
                                item.setTotalPrice(item.getUnitPrice() * quantity);
                                break;
                            }
                        }

                        cartTotalPrice = 0.0;
                        totalunitprice = 0.0;
                        for (CartItem item : cartList) {
                            cartTotalPrice += item.getTotalPrice();
                            totalunitprice += item.getUnitPrice();
                        }

                        totalPriceTextView.setText("$" + String.format("%.2f", cartTotalPrice));
                        totaluniTextReview.setText("$" + String.format("%.2f", totalunitprice));
                        cartAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Quantity updated successfully", Toast.LENGTH_SHORT).show();
                    });
                } else if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}