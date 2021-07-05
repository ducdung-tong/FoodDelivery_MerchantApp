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

public class OrderHistoryActivity extends AppCompatActivity {

    ImageButton buttonBack;
    RecyclerView recyclerViewSucceededOrder;
    TextView textViewNoNews;
    ArrayList<Order> orderHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        initView();
        initRecyclerViewAndAdapter();
    }

    private void initRecyclerViewAndAdapter() {
        orderHistoryList = WelcomeActivity.firebase.orderList.get(3);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewSucceededOrder.setLayoutManager(layoutManager);
        OrderAdapter orderAdapter = new OrderAdapter(this, orderHistoryList);
        recyclerViewSucceededOrder.setAdapter(orderAdapter);

        if (orderHistoryList.size() == 0)
            textViewNoNews.setVisibility(View.VISIBLE);
    }

    private void initView() {
        buttonBack = findViewById(R.id.btn_back);
        recyclerViewSucceededOrder = findViewById(R.id.recyclerview_order);
        textViewNoNews = findViewById(R.id.tv_no_data);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}