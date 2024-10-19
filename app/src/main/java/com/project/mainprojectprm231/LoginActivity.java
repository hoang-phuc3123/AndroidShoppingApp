package com.project.mainprojectprm231;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.mainprojectprm231.networking.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_UID = "uid";

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            navigateToMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        buttonLogin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();
                                    saveUidToSharedPreferences(uid);
                                    fetchUserDataFromApi(uid);
                                }
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                                Log.e(TAG, "Login failed: " + errorMessage);
                                Toast.makeText(LoginActivity.this, "Authentication failed: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }

    private void saveUidToSharedPreferences(String uid) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_UID, uid);
        editor.apply();
    }

    private void fetchUserDataFromApi(String uid) {
        ApiClient.getUserData(uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    navigateToMainActivity();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "API Response: " + responseData);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject userData = jsonObject.getJSONObject("data");
                        String email = userData.getString("email");
                        String username = userData.getString("username");
                        String phone = userData.getString("phone");
                        String address = userData.getString("address");

                        // Save user data to SharedPreferences
                        saveUserDataToSharedPreferences(email, username, phone, address);

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "User data fetched successfully", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Error parsing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            navigateToMainActivity();
                        });
                    }
                } else {
                    String errorBody = response.body().string();
                    Log.e(TAG, "API call unsuccessful. Code: " + response.code() + ", Message: " + response.message() + ", Body: " + errorBody);
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Failed to fetch user data. Status: " + response.code(), Toast.LENGTH_LONG).show();
                        navigateToMainActivity();
                    });
                }
            }
        });
    }

    private void saveUserDataToSharedPreferences(String email, String username, String phone, String address) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("username", username);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}