package com.group4.fooddelivery_merchantapp.model;

import android.net.Uri;

public class ProductImage {
    String type;
    String id;
    String uri;

    public ProductImage() {
    }

    public ProductImage(String type, String id, String uri) {
        this.type = type;
        this.id = id;
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ProductImage clone() {
        ProductImage image = new ProductImage();
        image.id = this.id;
        image.uri = this.uri;
        image.type = this.type;
        return  image;
    }
}
