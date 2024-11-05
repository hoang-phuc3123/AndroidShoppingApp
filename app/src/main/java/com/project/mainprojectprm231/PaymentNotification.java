package com.project.mainprojectprm231;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentNotification extends AppCompatActivity {

    TextView txtNotification;
    Button buttonBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_notification);

        txtNotification = findViewById(R.id.textViewNotify);
        buttonBackToMain = findViewById(R.id.buttonBackToMain);

        Intent intent = getIntent();
        txtNotification.setText(intent.getStringExtra("result"));

        buttonBackToMain.setOnClickListener(v -> {
            Intent mainIntent = new Intent(PaymentNotification.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        });
    }
}