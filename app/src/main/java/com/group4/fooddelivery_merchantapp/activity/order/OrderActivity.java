package com.group4.fooddelivery_merchantapp.activity.order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.main.WelcomeActivity;
import com.group4.fooddelivery_merchantapp.model.OnDataListener;

public class OrderActivity extends AppCompatActivity {

    RelativeLayout pendingOrderSection, confirmedOrderSection, deliveringOrderSection, succeededOrderSection;
    TextView textViewPendingQuantity, textViewConfirmedQuantity, textViewDeliveringQuantity, textViewSucceededQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initView();
        forwardSection();
    }

    private void initView() {
        pendingOrderSection = findViewById(R.id.pending_order_section);
        confirmedOrderSection = findViewById(R.id.confirmed_order_section);
        deliveringOrderSection = findViewById(R.id.delivering_order_section);
        succeededOrderSection = findViewById(R.id.succeeded_order_section);

        textViewPendingQuantity = findViewById(R.id.tv_pending_quantity);
        textViewConfirmedQuantity = findViewById(R.id.tv_confirmed_quantity);
        textViewDeliveringQuantity = findViewById(R.id.tv_delivering_quantity);
        textViewSucceededQuantity = findViewById(R.id.tv_succeeded_quantity);

        setOrderSectionQuantity();

        getDataChange();
    }

    private void getDataChange() {
        WelcomeActivity.firebase.onNewOrderChangeListener(new OnDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                setOrderSectionQuantity();
            }
        });
    }

    public void setOrderSectionQuantity () {
        textViewPendingQuantity.setVisibility(View.VISIBLE);
        textViewConfirmedQuantity.setVisibility(View.VISIBLE);
        textViewDeliveringQuantity.setVisibility(View.VISIBLE);
        textViewSucceededQuantity.setVisibility(View.VISIBLE);

        Log.e("new size-- 0", String.valueOf(WelcomeActivity.firebase.orderList.get(0).size()));
        Log.e("new size-- 1", String.valueOf(WelcomeActivity.firebase.orderList.get(1).size()));
        Log.e("new size-- 2", String.valueOf(WelcomeActivity.firebase.orderList.get(2).size()));
        Log.e("new size-- 3", String.valueOf(WelcomeActivity.firebase.orderList.get(3).size()));

        textViewPendingQuantity.setText(String.valueOf(WelcomeActivity.firebase.orderList.get(0).size()));
        textViewConfirmedQuantity.setText(String.valueOf(WelcomeActivity.firebase.orderList.get(1).size()));
        textViewDeliveringQuantity.setText(String.valueOf(WelcomeActivity.firebase.orderList.get(2).size()));
        textViewSucceededQuantity.setText(String.valueOf(WelcomeActivity.firebase.orderList.get(3).size()));

        if (WelcomeActivity.firebase.orderList.get(0).size() == 0) {
            textViewPendingQuantity.setVisibility(View.INVISIBLE);
        }
        if (WelcomeActivity.firebase.orderList.get(1).size() == 0) {
            textViewConfirmedQuantity.setVisibility(View.INVISIBLE);
        }
        if (WelcomeActivity.firebase.orderList.get(2).size() == 0) {
            textViewDeliveringQuantity.setVisibility(View.INVISIBLE);
        }
        if (WelcomeActivity.firebase.orderList.get(3).size() == 0) {
            textViewSucceededQuantity.setVisibility(View.INVISIBLE);
        }
    }

    private void forwardSection() {
        pendingOrderSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, PendingOrderActivity.class);
                startActivity(intent);
            }
        });

        confirmedOrderSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, ConfirmedOrderActivity.class);
                startActivity(intent);
            }
        });

        deliveringOrderSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, DeliveringOrderActivity.class);
                startActivity(intent);
            }
        });

        succeededOrderSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, SucceededOrderActivity.class);
                startActivity(intent);
            }
        });
    }
}