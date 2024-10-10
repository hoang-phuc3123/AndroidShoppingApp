package com.project.mainprojectprm231;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Nhận dữ liệu từ Intent
        String name = getIntent().getStringExtra("name");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        double price = getIntent().getDoubleExtra("price", 0);
        String description = getIntent().getStringExtra("description");
        String brand = getIntent().getStringExtra("brand");
        int rating = getIntent().getIntExtra("rating", 0);

        TextView productName = findViewById(R.id.product_name_detail);
        TextView productPrice = findViewById(R.id.product_price_detail);
        TextView productDescription = findViewById(R.id.product_description_detail);
        TextView productBrand = findViewById(R.id.product_brand_detail);
        TextView productRating = findViewById(R.id.product_rating_detail);
        ImageView productImage = findViewById(R.id.product_image_detail);
        ImageView backButton = findViewById(R.id.back_button);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);

        productName.setText(name);
        productPrice.setText(String.valueOf(price));
        productDescription.setText(description);
        productBrand.setText(brand);
        productRating.setText(String.valueOf(rating));

        Glide.with(this).load(imageUrl).into(productImage);

        backButton.setOnClickListener(v -> onBackPressed());

        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(ProductDetailActivity.this, name + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }
}
