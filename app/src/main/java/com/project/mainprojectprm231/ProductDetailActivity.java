package com.project.mainprojectprm231;

import android.content.Intent;
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
                    runOnUiThread(() -> {
                        Log.e("AddToCart", "API call failed: " + e.getMessage());
                        Toast.makeText(ProductDetailActivity.this, "Failed to add product to cart", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(ProductDetailActivity.this, "Product added to cart successfully", Toast.LENGTH_SHORT).show();

                            // Increase cart item count and save to SharedPreferences
                            int newCartItemCount = getCartItemCount() + 1;
                            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                            editor.putInt("cartItemCount", newCartItemCount);
                            editor.apply();

                            // Update cart badge
                            updateCartBadge(newCartItemCount);

                            // Change cart icon color to red
                            cart.setColorFilter(Color.RED);

                            // Change cart icon back to black after 4 seconds
                            cart.postDelayed(() -> cart.setColorFilter(Color.BLACK), 4000);
                        });
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        Log.e("AddToCart", "API call unsuccessful. Code: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                        runOnUiThread(() -> {
                            Toast.makeText(ProductDetailActivity.this, "Failed to add product to cart. Status: " + response.code(), Toast.LENGTH_LONG).show();
                        });
                    }
                }
            });
        });

        // Handle cart icon click
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void updateCartBadge(int itemCount) {
        TextView cartBadge = findViewById(R.id.cart_badge);
        if (itemCount > 0) {
            cartBadge.setText(String.valueOf(itemCount));
            cartBadge.setVisibility(View.VISIBLE);
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
