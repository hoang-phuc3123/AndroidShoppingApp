package com.project.mainprojectprm231;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.mainprojectprm231.adapters.OrderItemAdapter;
import com.project.mainprojectprm231.models.ApiCartResponse;
import com.project.mainprojectprm231.models.CartItem;
import com.project.mainprojectprm231.models.StoreLocation;
import com.project.mainprojectprm231.models.StoreLocationResponse;
import com.project.mainprojectprm231.networking.ApiClient;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {
    private static final String PREF_NAME = "UserPrefs";
    private MaterialAutoCompleteTextView storeLocationAutoComplete;
    private MaterialAutoCompleteTextView paymentMethodAutoComplete;
    private EditText billingAddressEdit;
    private TextView totalAmountText;
    private RecyclerView recyclerView;
    private List<StoreLocation> storeLocations;
    private List<CartItem> orderItems;
    private int selectedStoreId = 0;
    private double totalAmount = 0.0;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize views
        storeLocationAutoComplete = findViewById(R.id.spinner_store_location);
        paymentMethodAutoComplete = findViewById(R.id.spinner_payment_method);
        billingAddressEdit = findViewById(R.id.edit_billing_address);
        totalAmountText = findViewById(R.id.textTotalAmount);
        recyclerView = findViewById(R.id.recyclerViewOrderItems);
        Button submitOrderBtn = findViewById(R.id.button_submit_order);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get cart items
        fetchCartItems();

        // Setup payment method dropdown
        setupPaymentMethodDropdown();

        // Fetch store locations
        fetchStoreLocations();

        // Setup submit button
        submitOrderBtn.setOnClickListener(v -> submitOrder());
    }

    private void setupPaymentMethodDropdown() {
        String[] paymentMethods = {"Cash", "Credit Card", "Bank Transfer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, paymentMethods);
        paymentMethodAutoComplete.setAdapter(adapter);
    }

    private void fetchStoreLocations() {
        ApiClient.fetchStoreLocation(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(OrderActivity.this,
                        "Failed to fetch store locations", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    try {
                        Gson gson = new Gson();
                        StoreLocationResponse locationResponse = gson.fromJson(jsonResponse, StoreLocationResponse.class);

                        if (locationResponse != null && locationResponse.isSuccess() &&
                                locationResponse.getData() != null &&
                                locationResponse.getData().getContent() != null) {

                            storeLocations = locationResponse.getData().getContent();
                            runOnUiThread(() -> setupStoreLocationDropdown());
                        } else {
                            runOnUiThread(() -> Toast.makeText(OrderActivity.this,
                                    "No store locations available", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(OrderActivity.this,
                                "Error processing store location data", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(OrderActivity.this,
                            "Failed to fetch store locations", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void setupStoreLocationDropdown() {
        if (storeLocations == null || storeLocations.isEmpty()) {
            Toast.makeText(this, "No store locations available", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<StoreLocation> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, storeLocations);
        storeLocationAutoComplete.setAdapter(adapter);

        storeLocationAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            StoreLocation selectedLocation = storeLocations.get(position);
            selectedStoreId = selectedLocation.getId();
        });
    }

    private void submitOrder() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int cartId = sharedPreferences.getInt("cartId", 0);

        if (cartId == 0) {
            Toast.makeText(this, "Invalid cart ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedStoreId == 0) {
            Toast.makeText(this, "Please select a store location", Toast.LENGTH_SHORT).show();
            return;
        }

        String billingAddress = billingAddressEdit.getText().toString();
        if (billingAddress.isEmpty()) {
            Toast.makeText(this, "Please enter billing address", Toast.LENGTH_SHORT).show();
            return;
        }

        CharSequence paymentMethod = paymentMethodAutoComplete.getText();
        if (paymentMethod == null || paymentMethod.toString().isEmpty()) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create order object
        try {
            JSONObject orderData = new JSONObject();
            orderData.put("cartId", cartId);
            orderData.put("storeLocationId", selectedStoreId);
            orderData.put("paymentStatus", "PENDING");
            orderData.put("paymentMethod", paymentMethod.toString());
            orderData.put("billingAddress", billingAddress);

            // ApiClient.createOrder(orderData, callback);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating order", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int cartId = sharedPreferences.getInt("cartId", 0);

        ApiClient.fetchCartItems(cartId, 0, 100, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(OrderActivity.this, "Failed to fetch cart items", Toast.LENGTH_SHORT).show();
                    finish(); // Return to previous activity if we can't load cart items
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    try {
                        Gson gson = new Gson();
                        ApiCartResponse apiCartResponse = gson.fromJson(jsonResponse, ApiCartResponse.class);

                        if (apiCartResponse != null && apiCartResponse.isSuccess() && apiCartResponse.getData() != null) {
                            orderItems = apiCartResponse.getData().getContent();

                            runOnUiThread(() -> {
                                OrderItemAdapter adapter = new OrderItemAdapter(orderItems, OrderActivity.this);
                                recyclerView.setAdapter(adapter);

                                // Calculate total
                                totalAmount = 0.0;
                                for (CartItem item : orderItems) {
                                    totalAmount += item.getTotalPrice();
                                }
                                totalAmountText.setText(String.format("$%.2f", totalAmount));
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(OrderActivity.this, "No items in cart", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(OrderActivity.this, "Error processing cart data", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                }
            }
        });
    }
}