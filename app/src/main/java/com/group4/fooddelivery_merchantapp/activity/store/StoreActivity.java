package com.group4.fooddelivery_merchantapp.activity.store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.adapter.ListProductAdapter;

public class StoreActivity extends AppCompatActivity {

    RelativeLayout rl_listProduct, rl_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Init();
    }

    private void Init() {
        rl_listProduct = findViewById(R.id.store_rl_list);
        rl_add = findViewById(R.id.store_rl_add);


        rl_listProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ListProductActivity = new Intent(StoreActivity.this, ListProductActivity.class);
                startActivity(ListProductActivity);
            }
        });

        rl_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddProductActivity = new Intent(StoreActivity.this, AddProductActivity.class);
                startActivity(AddProductActivity);
            }
        });
    }
}