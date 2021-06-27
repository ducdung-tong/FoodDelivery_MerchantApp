package com.group4.fooddelivery_merchantapp.activity.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.order.OrderActivity;
import com.group4.fooddelivery_merchantapp.activity.store.StoreActivity;
import com.group4.fooddelivery_merchantapp.model.OnDataListener;

public class MainActivity extends AppCompatActivity {
    ImageView iv_store, iv_order;
    ProgressBar progressBarOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();
    }

    private void Init() {
        iv_store = findViewById(R.id.main_iv_store);
        iv_order = findViewById(R.id.main_iv_order);
        progressBarOrder = findViewById(R.id.progress_order);

        iv_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent storeIntent = new Intent(MainActivity.this, StoreActivity.class);
                startActivity(storeIntent);
            }
        });

        iv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity.firebase.getOrderCollection(new OnDataListener() {
                    @Override
                    public void onStart() {
                        progressBarOrder.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess() {
                        Intent orderIntent = new Intent(MainActivity.this, OrderActivity.class);
                        startActivity(orderIntent);
                        progressBarOrder.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }
}