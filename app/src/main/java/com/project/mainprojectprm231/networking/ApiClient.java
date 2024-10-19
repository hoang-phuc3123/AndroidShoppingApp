package com.project.mainprojectprm231.networking;

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
}