package com.project.mainprojectprm231;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Retrieve data from Intent
        String name = getIntent().getStringExtra("productName");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        double price = getIntent().getDoubleExtra("price", 0);
        String description = getIntent().getStringExtra("fullDescription");
        String brand = getIntent().getStringExtra("categoryName");
        float rating = getIntent().getFloatExtra("rating", 0);
        int productId = getIntent().getIntExtra("productId", 0);

        // Check if required data is available
        if (name == null || imageUrl == null || description == null || brand == null) {
            Toast.makeText(this, "Missing product details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up UI elements
        TextView productName = findViewById(R.id.product_name_detail);
        TextView productPrice = findViewById(R.id.product_price_detail);
        TextView productDescription = findViewById(R.id.product_description_detail);
        TextView productBrand = findViewById(R.id.product_brand_detail);
        RatingBar productRating = findViewById(R.id.product_rating_detail);
        ImageView productImage = findViewById(R.id.product_image_detail);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        ImageView backButton = findViewById(R.id.back_button);
        cart = findViewById(R.id.ic_cart);

        // Set data to the views
        productName.setText(name);
        productPrice.setText(String.format(Locale.getDefault(), "$%.2f", price));
        productDescription.setText(description);
        productBrand.setText(brand);
        productRating.setRating(rating);
        Glide.with(this).load(imageUrl).into(productImage);

        // Handle back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Display initial cart item count on badge
        updateCartBadge(getCartItemCount());

        // Handle add to cart button click
        addToCartButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", 0);

            // Validate the product ID, user ID, and cart ID
            if (productId == 0 || userId == 0) {
                Toast.makeText(ProductDetailActivity.this, "Invalid cart details", Toast.LENGTH_SHORT).show();
                Log.d("ProductDetailActivity", "Product ID: " + productId);
                Log.d("ProductDetailActivity", "User ID: " + userId);
                return;
            }

            // Make the API call to add the product to the cart
            ApiClient.addToCart(productId, userId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            // Update cart badge
                            updateCartBadge(getCartItemCount() + 1);
                            Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        });

        // Handle cart icon click
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(cartUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(cartUpdateReceiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartUpdateReceiver != null) {
            unregisterReceiver(cartUpdateReceiver);
        }
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
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt("cartItemCount", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean cartModified = data != null && data.getBooleanExtra("cartModified", false);
            if (cartModified) {
                // Reset cart color to black
                cart.setColorFilter(Color.BLACK);

                // Update badge if cart was modified
                updateCartBadge(getCartItemCount());
            }
        }
    }
}