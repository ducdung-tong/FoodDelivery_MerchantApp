package com.group4.fooddelivery_merchantapp.activity.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.order.OrderActivity;
import com.group4.fooddelivery_merchantapp.activity.store.StoreActivity;
import com.group4.fooddelivery_merchantapp.model.OnDataListener;

public class MainActivity extends AppCompatActivity {
    ImageView iv_store, iv_order, iv_banner;
    TextView tv_merchantName;
    TextView tv_logout;
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
        iv_banner = findViewById(R.id.main_iv_banner);
        tv_logout = findViewById(R.id.main_tv_logout);
        tv_merchantName = findViewById(R.id.main_tv_name);
        progressBarOrder = findViewById(R.id.progress_order);
        tv_merchantName.setText(WelcomeActivity.firebase.merchant.getName());

        Glide.with(this).load(WelcomeActivity.firebase.merchant.getImage().get(0)).into(iv_banner);

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

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                navigateToLogin();
            }
        });
    }

    private void navigateToLogin() {
        Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }
}