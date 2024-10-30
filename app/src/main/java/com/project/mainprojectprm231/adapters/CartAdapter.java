package com.project.mainprojectprm231.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.mainprojectprm231.CartActivity;
import com.project.mainprojectprm231.R;
import com.project.mainprojectprm231.models.CartItem;
import com.project.mainprojectprm231.networking.ApiClient;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<CartItem> cartItems;
    private final Context context;

    public CartAdapter(List<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        // Set the data to the views
        holder.productName.setText(cartItem.getProductName());
        holder.brandProduct.setText(cartItem.getProductType());
        holder.productQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.priceProduct.setText("$" + String.format("%.2f", cartItem.getUnitPrice()));

        // Load image using Glide
        Glide.with(context)
                .load(cartItem.getProductImage())
                .into(holder.productImage);

        // Handle quantity increase button click with debounce
        holder.buttonIncrease.setOnClickListener(v -> {
            holder.handler.removeCallbacks(holder.debounceRunnable);
            holder.debounceRunnable = () -> {
                int currentQuantity = cartItem.getQuantity();
                int newQuantity = currentQuantity + 1;

                ApiClient.updateQuantityCart(cartItem.getItemId(), newQuantity, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ((CartActivity) context).runOnUiThread(() -> {
                                cartItem.setQuantity(newQuantity);
                                notifyItemChanged(position);
                                sendCartUpdateBroadcast();
                            });
                        }
                    }
                });
            };
            holder.handler.postDelayed(holder.debounceRunnable, 300); // 300ms debounce time
        });

        // Handle quantity decrease button click with debounce
        holder.buttonDecrease.setOnClickListener(v -> {
            holder.handler.removeCallbacks(holder.debounceRunnable);
            holder.debounceRunnable = () -> {
                int currentQuantity = cartItem.getQuantity();
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;

                    ApiClient.updateQuantityCart(cartItem.getItemId(), newQuantity, new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                ((CartActivity) context).runOnUiThread(() -> {
                                    cartItem.setQuantity(newQuantity);
                                    notifyItemChanged(position);
                                    sendCartUpdateBroadcast();
                                });
                            }
                        }
                    });
                }
            };
            holder.handler.postDelayed(holder.debounceRunnable, 300); // 300ms debounce time
        });

        // Handle delete item button click
        holder.trashButton.setOnClickListener(v -> {
            int itemId = cartItem.getItemId();
            Log.d("TAG", "itemId: " + itemId);
            ((CartActivity) context).removeFromCart(itemId); // Call remove method in CartActivity
        });
    }

    private void sendCartUpdateBroadcast() {
        int totalQuantity = 0;
        for (CartItem item : cartItems) {
            totalQuantity += item.getQuantity();
        }
        Intent intent = new Intent("UPDATE_CART_BADGE");
        intent.putExtra("cartItemCount", totalQuantity);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView brandProduct;
        TextView productQuantity;
        TextView priceProduct;
        Button buttonIncrease;
        Button buttonDecrease;
        Button trashButton;
        Handler handler;
        Runnable debounceRunnable;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            brandProduct = itemView.findViewById(R.id.brandproduct);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            priceProduct = itemView.findViewById(R.id.priceproduct);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            trashButton = itemView.findViewById(R.id.trashcan);
            handler = new Handler(Looper.getMainLooper());
        }
    }
}