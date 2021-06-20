package com.group4.fooddelivery_merchantapp.model;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.net.InternetDomainName;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ModifiedFirebase {
    FirebaseFirestore root;
    StorageReference storage;

    public String merchantId = "EOPPrOWpbfp2XCcjCQkT";
    public ArrayList<Product> productList;


    public ModifiedFirebase() {
        productList = new ArrayList<Product>();
        root = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void initProductData(final OnDataListener listener) {
        listener.onStart();
        root.collection("Product/")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.e("Firebasse", document.getId());
                            if (document == null) break;
                            Product product = new Product();
                            product.setID((String) document.getId());
                            product.setName((String) document.get("Name"));
                            product.setName_En(document.get("Name_En").toString());
                            product.setStatus((String) document.get("Status"));
                            product.setPrice((ArrayList<String>) document.get("Price"));
                            product.setSize((ArrayList<String>) document.get("Size"));
                            product.setMerchant(merchantId);
                            product.setRating((String) document.get("Rating"));
                            product.setCreate(document.get("Create").toString());
                            getImageList(product);
                        }
                        listener.onSuccess();
                    }
                });
    }

    public void getImageList(Product product) {
        ArrayList<ProductImage> images = new ArrayList<ProductImage>();
        root.collection("Product/" + product.getID() + "/Photos/")
                .orderBy("Type", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ProductImage img = new ProductImage();
                            Log.e("Firebasse", "img" + document.getId());
                            if (document == null)
                                break;
                            img.setUri(document.get("Image_Link").toString());
                            img.setId(document.getId());
                            img.setType(document.get("Type").toString());
                            images.add(img);
                        }
                        product.setImage(images);
                        productList.add(product);
                    }
                });
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }
}
