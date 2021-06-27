package com.group4.fooddelivery_merchantapp.activity.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.main.MainActivity;
import com.group4.fooddelivery_merchantapp.activity.main.WelcomeActivity;
import com.group4.fooddelivery_merchantapp.adapter.OrderAdapter;
import com.group4.fooddelivery_merchantapp.adapter.OrderItemAdapter;
import com.group4.fooddelivery_merchantapp.model.OnDataListener;
import com.group4.fooddelivery_merchantapp.model.Order;

import es.dmoral.toasty.Toasty;

public class OrderDetailActivity extends AppCompatActivity {

    Button buttonNextState;
    ImageButton buttonBack;
    static RecyclerView recyclerViewItems;
    TextView textViewUserName, textViewUserPhoneNumber, textViewUserAddress, textViewTime, textViewSubTotal, textViewDiscount,
        textViewTotalAmount, textViewShippingCost;
    Order order;
    int orderList = 0;
    int orderIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initView();
        initRecyclerViewItems();
        forwardOrderToNextState();
    }

    private void initView() {
        buttonBack = findViewById(R.id.btn_back);
        buttonNextState = findViewById(R.id.btn_next_state);
        textViewUserName = findViewById(R.id.tv_user_name);
        textViewUserPhoneNumber = findViewById(R.id.tv_user_phone_number);
        textViewUserAddress = findViewById(R.id.tv_user_address);
        textViewTime = findViewById(R.id.tv_order_time);
        textViewSubTotal = findViewById(R.id.tv_order_subtotal);
        textViewDiscount = findViewById(R.id.tv_order_discount);
        textViewShippingCost = findViewById(R.id.tv_shipping_cost);
        textViewTotalAmount = findViewById(R.id.tv_order_total);
        recyclerViewItems = findViewById(R.id.recyclerview_order_items);

        setButtonNextStateText();
        setTextViewValues();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setButtonNextStateText () {
        int orderType = getIntent().getIntExtra("OrderType", 0);
        int indexInList = getIntent().getIntExtra("IndexInList", 0);
        if (orderType == 0) {
            buttonNextState.setText(getString(R.string.confirm_order));
        }
        if (orderType == 1) {
            buttonNextState.setText(getString(R.string.deliver_order));
        }
        if (orderType == 2) {
            buttonNextState.setText(getString(R.string.order_success));
        }
        if (orderType == 3) {
            buttonNextState.setVisibility(View.INVISIBLE);
        }
        orderList = orderType;
        orderIndex = indexInList;
        order = WelcomeActivity.firebase.orderList.get(orderType).get(indexInList);
    }

    private void initRecyclerViewItems() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewItems.setLayoutManager(layoutManager);
        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(this, order.getListOrderItems());
        recyclerViewItems.setAdapter(orderItemAdapter);
    }

    private void setTextViewValues() {
        textViewUserName.setText(order.getUserName());
        textViewUserPhoneNumber.setText(order.getUserPhoneNumber());
        textViewUserAddress.setText(order.getUserAddress());
        textViewTime.setText(order.getTime());
        textViewSubTotal.setText(String.valueOf(order.getTmpPrice()));
        textViewDiscount.setText(String.valueOf(order.getDiscount()));
        textViewShippingCost.setText(String.valueOf(order.getFreightCost()));
        textViewTotalAmount.setText(String.valueOf(order.getTotalAmount()));
    }

    private void forwardOrderToNextState() {
        buttonNextState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nextStatus = "";
                if (orderList == 0) {
                    nextStatus = "Confirmed";
                }
                if (orderList == 1) {
                    nextStatus = "Delivering";
                }
                if (orderList == 2) {
                    nextStatus = "Succeeded";
                }

                WelcomeActivity.firebase.setOrderStatus(order, nextStatus, new OnDataListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess() {
                        Toasty.normal(OrderDetailActivity.this, getString(R.string.moved_order_next_state), Toasty.LENGTH_SHORT).show();

                        Intent intent = new Intent(OrderDetailActivity.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
            }
        });
    }
}