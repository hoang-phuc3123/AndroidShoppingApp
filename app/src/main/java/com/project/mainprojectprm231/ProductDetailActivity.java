package com.project.mainprojectprm231;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.project.mainprojectprm231.networking.ApiClient;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String PREF_NAME = "UserPrefs";
    private ImageView cart;
    private BroadcastReceiver cartUpdateReceiver;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // Retrieve product data from Intent
        String name = getIntent().getStringExtra("productName");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        double price = getIntent().getDoubleExtra("price", 0);
        String description = getIntent().getStringExtra("fullDescription");
        String brand = getIntent().getStringExtra("categoryName");
        float rating = getIntent().getFloatExtra("rating", 0);
        int productId = getIntent().getIntExtra("productId", 0);

        if (name == null || imageUrl == null || description == null || brand == null) {
            Toast.makeText(this, "Missing product details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // UI Elements
        TextView productName = findViewById(R.id.product_name_detail);
        TextView productPrice = findViewById(R.id.product_price_detail);
        TextView productDescription = findViewById(R.id.product_description_detail);
        TextView productBrand = findViewById(R.id.product_brand_detail);
        RatingBar productRating = findViewById(R.id.product_rating_detail);
        ImageView productImage = findViewById(R.id.product_image_detail);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        ImageView backButton = findViewById(R.id.back_button);
        cart = findViewById(R.id.ic_cart);

        // Set product data
        productName.setText(name);
        productPrice.setText(String.format(Locale.getDefault(), "$%.2f", price));
        productDescription.setText(description);
        productBrand.setText(brand);
        productRating.setRating(rating);
        Glide.with(this).load(imageUrl).into(productImage);

        // Handle back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Display initial cart item count
        updateCartBadge(getCartItemCount());

        addToCartButton.setOnClickListener(v -> addToCart(productId));

        cart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivityForResult(intent, 1);
        });

        // Register BroadcastReceiver to update cart badge
        cartUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int itemCount = intent.getIntExtra("cartItemCount", 0);
                updateCartBadge(itemCount);
            }
        };

        IntentFilter filter = new IntentFilter("UPDATE_CART_BADGE");
        LocalBroadcastManager.getInstance(this).registerReceiver(cartUpdateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(cartUpdateReceiver);
        }
    }

    private void addToCart(int productId) {
        int userId = sharedPreferences.getInt("userId", 0);

        if (productId == 0 || userId == 0) {
            Toast.makeText(ProductDetailActivity.this, "Invalid cart details", Toast.LENGTH_SHORT).show();
            Log.d("ProductDetailActivity", "Product ID: " + productId);
            Log.d("ProductDetailActivity", "User ID: " + userId);
            return;
        }

        progressDialog.show();

        ApiClient.addToCart(productId, userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> progressDialog.dismiss());

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        // Update cart badge
                        int newCount = getCartItemCount() + 1;
                        saveCartItemCount(newCount);
                        updateCartBadge(newCount);

                        // Notify user
                        Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();

                        // Broadcast cart update
                        Intent intent = new Intent("CART_UPDATED");
                        LocalBroadcastManager.getInstance(ProductDetailActivity.this).sendBroadcast(intent);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void updateCartBadge(int itemCount) {
        TextView cartBadge = findViewById(R.id.cart_badge);
        if (itemCount > 0) {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(itemCount));
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }

    private int getCartItemCount() {
        return sharedPreferences.getInt("cartItemCount", 0);
    }

    private void saveCartItemCount(int itemCount) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("cartItemCount", itemCount);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean cartModified = data != null && data.getBooleanExtra("cartModified", false);
            if (cartModified) {
                cart.setColorFilter(Color.BLACK);
                updateCartBadge(getCartItemCount());
            }
        }
    }
}
