package com.project.mainprojectprm231.networking;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiClient {
    private static final String BASE_URL = "https://shop-mobile-api.opalwed.id.vn/api/v1/product";
    private static final OkHttpClient client = new OkHttpClient();

    public static void fetchProducts(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        client.newCall(request).enqueue(callback);
    }
}