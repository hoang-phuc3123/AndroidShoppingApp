package com.project.mainprojectprm231.networking;

import android.text.TextUtils;
import android.util.Log;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;

import org.json.JSONObject;

public class ApiClient {
    private static final String BASE_URL = "https://shop-mobile-api.opalwed.id.vn/api/v1";
    private static final OkHttpClient client = new OkHttpClient();

    public static void fetchProducts(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/product")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchStoreLocation(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/location")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchStoreLocationDetail(int id ,Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/location/" + id)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void fetchCartItems(int userId, int page, int size, Callback callback) {
        String url = String.format(BASE_URL + "/cart?id=%d&page=%d&size=%d", userId, page, size);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d("url", "url: " + url);
        client.newCall(request).enqueue(callback);
    }

    public static void registerUser(String email, String username, String password, String phone, String address, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("phone", phone);
            jsonObject.put("address", address);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + "/user")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getUserData(String uid, Callback callback) {
        String url = BASE_URL + "/user?UID=" + uid;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void addToCart(int productId, int userId, Callback callback) {
        if (productId <= 0 || (userId <= 0)) {
            Log.e("ApiClient", "Invalid parameters for addToCart. Product ID: " + productId + ", User ID: " + userId);
            return;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("productId", productId);
            jsonObject.put("quantity", 1);

            Log.d("ApiClient", "JSON request body: " + jsonObject.toString());
        } catch (Exception e) {
            Log.e("ApiClient", "Error creating JSON request body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + "/cart/add")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void updateQuantityCart(int itemId, int quantity, Callback callback) {
        if (itemId <= 0 || (quantity <= 0)) {
            Log.e("ApiClient", "Invalid parameters for addToCart. Item ID: " + itemId + ", Quantity: " + quantity);
            return;
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("itemId", itemId);
            jsonObject.put("quantity", quantity);

            Log.d("ApiClient", "JSON request body: " + jsonObject.toString());
        } catch (Exception e) {
            Log.e("ApiClient", "Error creating JSON request body: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + "/cart/update?itemId=" + itemId + "&quantity=" + quantity)
                .put(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void removeFromCart(int itemId, Callback callback) {
        String url = BASE_URL + "/cart/remove?itemId=" + itemId;
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        Log.d("url", "Remove from cart URL: " + url);
        client.newCall(request).enqueue(callback);
    }
    public static void clearCart(int cartId, Callback callback) {
        String url = BASE_URL + "/cart/clear?cartId=" + cartId;
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static void updateCartItemQuantity(int itemId, int quantity, Callback callback) {
        String url = String.format(BASE_URL + "/cart/update?itemId=%d&quantity=%d", itemId, quantity);

        // Create empty request body since we're passing parameters in URL
        RequestBody emptyBody = RequestBody.create(null, new byte[0]);

        Request request = new Request.Builder()
                .url(url)
                .put(emptyBody)  // PUT request requires a body, even if empty
                .build();

        Log.d("ApiClient", "Update cart URL: " + url);
        client.newCall(request).enqueue(callback);
    }



}