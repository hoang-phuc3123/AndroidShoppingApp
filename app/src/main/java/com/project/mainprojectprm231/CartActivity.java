package com.project.mainprojectprm231;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ImageView backButton = findViewById(R.id.back_buttoncart);
        Button checkoutButton = findViewById(R.id.button_checkout);
        String name = getIntent().getStringExtra("name");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartscreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton.setOnClickListener(v -> onBackPressed());

        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(CartActivity.this, " Checkout cart", Toast.LENGTH_SHORT).show();
        });

    }
}