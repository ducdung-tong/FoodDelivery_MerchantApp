package com.group4.fooddelivery_merchantapp.activity.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.main.WelcomeActivity;
import com.group4.fooddelivery_merchantapp.adapter.ListProductAdapter;
import com.group4.fooddelivery_merchantapp.activity.main.LoginActivity;
import com.group4.fooddelivery_merchantapp.model.Product;

import java.util.ArrayList;

public class ListProductActivity extends AppCompatActivity {

    RecyclerView list_product;
    ImageButton bt_edit, bt_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        Init();
    }

    private void Init() {
        list_product = findViewById(R.id.list_product);

        ListProductAdapter productAdapter = new ListProductAdapter(ListProductActivity.this, WelcomeActivity.firebase.productList);
        LinearLayoutManager productLayoutManager = new LinearLayoutManager(ListProductActivity.this, RecyclerView.VERTICAL, false);
        list_product.setLayoutManager(productLayoutManager);
        list_product.setAdapter(productAdapter);
    }
}