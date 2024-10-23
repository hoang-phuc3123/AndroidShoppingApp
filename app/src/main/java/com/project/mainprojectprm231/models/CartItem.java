package com.project.mainprojectprm231;

public class CartItem {
    private String name;
    private String description;
    private double price;
    private int quantity;
    private int imageResourceId;

    public CartItem(String name, String description, double price, int quantity, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageResourceId = imageResourceId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
