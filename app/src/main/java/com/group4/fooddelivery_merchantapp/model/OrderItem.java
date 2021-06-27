package com.group4.fooddelivery_merchantapp.model;

public class OrderItem {
    Product product;
    int quantity;
    int price;
    String size;

    public OrderItem(Product product, int quantity, int price, String size) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.size = size;
    }

    public OrderItem(){}

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
