package com.group4.fooddelivery_merchantapp.model;

import android.net.Uri;

public class ProductImage {
    String extension;
    String type;
    String id;
    Uri uri;

    public ProductImage() {
    }

    public ProductImage(String type, String id, Uri uri) {
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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
