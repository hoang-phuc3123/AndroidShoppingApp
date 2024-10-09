package com.project.mainprojectprm231.networking;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiClient {
    private static final String BASE_URL = "https://66e1bb3ec831c8811b56281f.mockapi.io/products";

    public static void fetchProducts(Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();

        client.newCall(request).enqueue(callback);
    }
}

