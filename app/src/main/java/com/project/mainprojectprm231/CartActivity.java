package com.project.mainprojectprm231;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.project.mainprojectprm231.models.ApiCartResponse;
import com.project.mainprojectprm231.networking.ApiClient;
import com.project.mainprojectprm231.models.CartItem;
import com.project.mainprojectprm231.adapters.CartAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {

    private static final String PREF_NAME = "UserPrefs";
    private static final String CART_ITEM_COUNT_KEY = "cartItemCount";
    private static final String CART_ID_KEY = "cartId";
    private static final String UPDATE_CART_BADGE_ACTION = "UPDATE_CART_BADGE";

    private List<CartItem> cartList;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private TextView totalUnitPriceTextView;
    private double cartTotalPrice = 0.0;
    private double totalUnitPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize cart list and views
        cartList = new ArrayList<>();
        initViews();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartList, this);
        recyclerView.setAdapter(cartAdapter);

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartscreen), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        // Fetch cart items initially
        fetchCartItems();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewCart);
        totalPriceTextView = findViewById(R.id.total_value);
        totalUnitPriceTextView = findViewById(R.id.unitpricevalue);

        ImageView backButton = findViewById(R.id.back_buttoncart);
        Button checkoutButton = findViewById(R.id.button_checkout);
        ImageView deleteAllButton = findViewById(R.id.deleteall);

        backButton.setOnClickListener(v -> onBackPressed());
        checkoutButton.setOnClickListener(v -> startActivity(new Intent(this, OrderActivity.class)));
        deleteAllButton.setOnClickListener(v -> clearAllCartItems());
    }

    private void fetchCartItems() {
        int cartId = getCartIdFromPreferences();

        ApiClient.fetchCartItems(cartId, 0, cartList.size(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> showToast("Failed to fetch cart items"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ApiCartResponse apiCartResponse = new Gson().fromJson(response.body().string(), ApiCartResponse.class);
                    runOnUiThread(() -> handleCartResponse(apiCartResponse));
                } else {
                    runOnUiThread(() -> showToast("Failed to fetch cart items"));
                }
            }
        });
    }

    private void handleCartResponse(ApiCartResponse apiCartResponse) {
        if (apiCartResponse != null && apiCartResponse.isSuccess()) {
            cartList.clear();
            cartList.addAll(apiCartResponse.getData().getContent());
            updateCartPrices();
            cartAdapter.notifyDataSetChanged();
        } else {
            showToast("No cart items available");
        }
    }

    public void updateCartItemQuantity(int itemId, int newQuantity) {
        for (CartItem item : cartList) {
            if (item.getItemId() == itemId) {
                item.setQuantity(newQuantity);
                updateCartPrices();
                break;
            }
        }
    }

    public void removeFromCart(int itemId) {
        ApiClient.removeFromCart(itemId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> showToast("Failed to remove item from cart"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        removeItemFromList(itemId);
                        updateCartPrices();
                        cartAdapter.notifyDataSetChanged();
                        updateCartItemCountInPreferences(cartList.size());
                        sendCartUpdateBroadcast();
                        showToast("Item removed from cart");
                    });
                } else {
                    runOnUiThread(() -> showToast("Failed to remove item from cart"));
                }
            }
        });
    }

    private void removeItemFromList(int itemId) {
        for (CartItem item : cartList) {
            if (item.getItemId() == itemId) {
                cartList.remove(item);
                break;
            }
        }
    }

    private void clearAllCartItems() {
        int cartId = getCartIdFromPreferences();
        ApiClient.clearCart(cartId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> showToast("Failed to clear cart items"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        cartList.clear();
                        updateCartPrices();
                        cartAdapter.notifyDataSetChanged();
                        updateCartItemCountInPreferences(0);
                        sendCartUpdateBroadcast();
                        showToast("All cart items cleared");
                    });
                } else {
                    runOnUiThread(() -> showToast("Failed to clear cart items"));
                }
            }
        });
    }

    private void updateCartPrices() {
        cartTotalPrice = 0.0;
        totalUnitPrice = 0.0;
        for (CartItem item : cartList) {
            cartTotalPrice += item.getTotalPrice();
            totalUnitPrice += item.getUnitPrice();
        }
        totalPriceTextView.setText(String.format("$%.2f", cartTotalPrice));
        totalUnitPriceTextView.setText(String.format("$%.2f", totalUnitPrice));
    }

    private void updateCartItemCountInPreferences(int count) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putInt(CART_ITEM_COUNT_KEY, count).apply();
    }

    private int getCartIdFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(CART_ID_KEY, 0);
    }

    private void sendCartUpdateBroadcast() {
        Intent intent = new Intent(UPDATE_CART_BADGE_ACTION);
        intent.putExtra(CART_ITEM_COUNT_KEY, cartList.size());
        sendBroadcast(intent);
    }

    private void showToast(String message) {
        Toast.makeText(CartActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
