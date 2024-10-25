package com.project.mainprojectprm231;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        // Initialize cartList
        cartList = new ArrayList<>();

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewCart);
        totalPriceTextView = findViewById(R.id.total_value);
        totalUnitPriceTextView = findViewById(R.id.unitpricevalue);
        ImageView backButton = findViewById(R.id.back_buttoncart);
        Button checkoutButton = findViewById(R.id.button_checkout);
        ImageView deleteAllButton = findViewById(R.id.deleteall);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartList, this);
        recyclerView.setAdapter(cartAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartscreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set click listeners
        backButton.setOnClickListener(v -> onBackPressed());
        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(CartActivity.this, "Checkout cart", Toast.LENGTH_SHORT).show();
        });
        deleteAllButton.setOnClickListener(v -> clearAllCartItems());

        // Fetch cart items when the activity is created
        fetchCartItems();
    }

    private void fetchCartItems() {
        int page = 0;
        int size = cartList.size();

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int cartId = sharedPreferences.getInt("cartId", 0);

        ApiClient.fetchCartItems(cartId, page, size, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(CartActivity.this, "Failed to fetch cart items", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    ApiCartResponse apiCartResponse = gson.fromJson(jsonResponse, ApiCartResponse.class);

                    runOnUiThread(() -> {
                        if (apiCartResponse != null && apiCartResponse.isSuccess() && apiCartResponse.getData() != null) {
                            // Clear existing cart items and add new ones
                            cartList.clear();
                            cartList.addAll(apiCartResponse.getData().getContent());

                            if (!cartList.isEmpty()) {
                                // Reset cartTotalPrice
                                cartTotalPrice = 0.0;
                                totalUnitPrice = 0.0;
                                // Calculate total price of all items
                                for (CartItem item : cartList) {
                                    cartTotalPrice += item.getTotalPrice();
                                    totalUnitPrice += item.getUnitPrice();
                                    Log.d("TAG", "onResponse: " + item.getTotalPrice());
                                }

                                // Update total price display
                                totalPriceTextView.setText("$" + String.format("%.2f", cartTotalPrice));
                                totalUnitPriceTextView.setText("$" + String.format("%.2f", totalUnitPrice));
                                // Notify adapter of data change
                                cartAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(CartActivity.this, "No cart items available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CartActivity.this, "No cart items available", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(CartActivity.this, "Failed to fetch cart items", Toast.LENGTH_SHORT).show()
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
                runOnUiThread(() ->
                        Toast.makeText(CartActivity.this, "Failed to remove item from cart", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Remove item from cartList
                        for (CartItem item : cartList) {
                            if (item.getItemId() == itemId) {
                                cartList.remove(item);
                                break;
                            }
                        }

                        // Recalculate total price
                        cartTotalPrice = 0.0;
                        totalUnitPrice = 0.0;
                        for (CartItem item : cartList) {
                            cartTotalPrice += item.getTotalPrice();
                            totalUnitPrice += item.getUnitPrice();
                        }

                        // Update total price display
                        totalPriceTextView.setText("$" + String.format("%.2f", cartTotalPrice));
                        totalUnitPriceTextView.setText("$" + String.format("%.2f", totalUnitPrice));

                        // Notify adapter of data change
                        cartAdapter.notifyDataSetChanged();
                        Toast.makeText(CartActivity.this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(CartActivity.this, "Failed to remove item from cart", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void clearAllCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int cartId = sharedPreferences.getInt("cartId", 0);

        ApiClient.clearCart(cartId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(CartActivity.this, "Failed to clear cart items", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        cartList.clear();
                        cartAdapter.notifyDataSetChanged();
                        cartTotalPrice = 0.0;
                        totalUnitPrice = 0.0;
                        totalPriceTextView.setText("$0.00");
                        totalUnitPriceTextView.setText("$0.00");
                        Toast.makeText(CartActivity.this, "All cart items cleared", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(CartActivity.this, "Failed to clear cart items", Toast.LENGTH_SHORT).show()
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
                runOnUiThread(() ->
                        Toast.makeText(CartActivity.this, "Failed to update quantity", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Update the cart item in the list
                        for (CartItem item : cartList) {
                            if (item.getItemId() == itemId) {
                                item.setQuantity(quantity);
                                // Update total price for the item
                                item.setTotalPrice(item.getUnitPrice() * quantity);
                                break;
                            }
                        }

                        // Recalculate total prices
                        cartTotalPrice = 0.0;
                        totalUnitPrice = 0.0;
                        for (CartItem item : cartList) {
                            cartTotalPrice += item.getTotalPrice();
                            totalUnitPrice += item.getUnitPrice();
                        }

                        // Update UI
                        totalPriceTextView.setText("$" + String.format("%.2f", cartTotalPrice));
                        totalUnitPriceTextView.setText("$" + String.format("%.2f", totalUnitPrice));
                        cartAdapter.notifyDataSetChanged();
                        Toast.makeText(CartActivity.this, "Quantity updated successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(CartActivity.this, "Failed to update quantity", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
