package com.group4.fooddelivery_merchantapp.model;

import android.net.Uri;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Product {

    private String ID;
    private String Name;
    private String Name_En;
    private String Merchant;
    private String Rating;
    private String Status;
    private ArrayList<String> Price;
    private ArrayList<String> Size;
    private ArrayList<ProductImage> Image;
    private ProductImage MainImage;
    private String Sales;
    private String Create;

    public Product() {
        Price = new ArrayList<>();
        Size = new ArrayList<>();
        Image = new ArrayList<>();
        MainImage = new ProductImage();
    };

    public Product(String ID, String name, String name_En, String merchant, String rating, String status, ArrayList<String> price, ArrayList<String> size, ArrayList<ProductImage> image, String sales, String create) {
        this.ID = ID;
        Name = name;
        Name_En = name_En;
        Merchant = merchant;
        Rating = rating;
        Status = status;
        Price = price;
        Size = size;
        Image = image;
        Sales = sales;
        Create = create;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName_En() {
        return Name_En;
    }

    public void setName_En(String name_En) {
        Name_En = name_En;
    }

    public String getMerchant() {
        return Merchant;
    }

    public void setMerchant(String merchant) {
        Merchant = merchant;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ArrayList<String> getPrice() {
        return Price;
    }

    public void setPrice(ArrayList<String> price) {
        Price = price;
    }

    public ArrayList<String> getSize() {
        return Size;
    }

    public void setSize(ArrayList<String> size) {
        Size = size;
    }

    public ArrayList<ProductImage> getImage() {
        return Image;
    }

    public void setImage(ArrayList<ProductImage> image) {
        Image = image;
    }

    public String getSales() {
        return Sales;
    }

    public void setSales(String sales) {
        Sales = sales;
    }

    public String getCreate() {
        return Create;
    }

    public void setCreate(String create) {
        Create = create;
    }

    public ProductImage getMainImage() {
        return MainImage;
    }

    public void setMainImage(ProductImage mainImage) {
        MainImage = mainImage;
    }
}