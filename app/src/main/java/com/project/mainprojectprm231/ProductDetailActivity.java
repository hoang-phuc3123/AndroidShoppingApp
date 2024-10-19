package com.project.mainprojectprm231;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Receive data from Intent
        String name = getIntent().getStringExtra("productName");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        double price = getIntent().getDoubleExtra("price", 0);
        String description = getIntent().getStringExtra("fullDescription");
        String brand = getIntent().getStringExtra("categoryName"); // Assuming category name is used as brand
        float rating = getIntent().getFloatExtra("rating", 0);

        TextView productName = findViewById(R.id.product_name_detail);
        TextView productPrice = findViewById(R.id.product_price_detail);
        TextView productDescription = findViewById(R.id.product_description_detail);
        TextView productBrand = findViewById(R.id.product_brand_detail);
        RatingBar productRating = findViewById(R.id.product_rating_detail);
        ImageView productImage = findViewById(R.id.product_image_detail);
        ImageView backButton = findViewById(R.id.back_button);
        ImageView cart = findViewById(R.id.ic_cart);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);

        productName.setText(name);
        productPrice.setText(String.format(Locale.getDefault(), "$%.2f", price));
        productDescription.setText(description);
        productBrand.setText(brand);
        productRating.setRating(rating);

        Glide.with(this).load(imageUrl).into(productImage);

        backButton.setOnClickListener(v -> onBackPressed());

        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(ProductDetailActivity.this, name + " added to cart", Toast.LENGTH_SHORT).show();
        });
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }
}
