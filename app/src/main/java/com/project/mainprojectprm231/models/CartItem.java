package com.project.mainprojectprm231.models;

public class CartItem {
    private int itemId;
    private String productName;
    private String productImage;
    private String productType;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    // Constructor to initialize all properties
    public CartItem(int itemId, String productName,String productImage,String productType , int quantity, double unitPrice, double totalPrice) {
        this.itemId = itemId;
        this.productName = productName;
        this.productImage = productImage;
        this.productType = productType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters for all properties
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductType() {
        return productType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Override toString() method for better object representation
//    @Override
//    public String toString() {
//        return "Item{" +
//                "itemId=" + itemId +
//                ", productName='" + productName + '\'' +
//                ", quantity=" + quantity +
//                ", unitPrice=" + unitPrice +
//                ", totalPrice=" + totalPrice +
//                '}';
//    }
}

