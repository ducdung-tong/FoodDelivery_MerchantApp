package com.group4.fooddelivery_merchantapp.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.main.WelcomeActivity;
import com.group4.fooddelivery_merchantapp.adapter.OrderAdapter;
import com.group4.fooddelivery_merchantapp.model.Order;

import java.util.ArrayList;

public class ConfirmedOrderActivity extends AppCompatActivity {

    ImageButton buttonBack;
    RecyclerView recyclerViewConfirmedOrder;
    TextView textViewNoNews;
    ArrayList<Order> confirmedOrderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_order);

        initView();
        initRecyclerViewAndAdapter();
    }

    private void initRecyclerViewAndAdapter() {
        confirmedOrderList = WelcomeActivity.firebase.orderList.get(1);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewConfirmedOrder.setLayoutManager(layoutManager);
        OrderAdapter orderAdapter = new OrderAdapter(this, confirmedOrderList);
        recyclerViewConfirmedOrder.setAdapter(orderAdapter);

        if (confirmedOrderList.size() == 0)
            textViewNoNews.setVisibility(View.VISIBLE);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        buttonBack = findViewById(R.id.btn_back);
        recyclerViewConfirmedOrder = findViewById(R.id.recyclerview_order);
        textViewNoNews = findViewById(R.id.tv_no_data);
    }
}