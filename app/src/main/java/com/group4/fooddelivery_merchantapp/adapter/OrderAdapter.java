package com.group4.fooddelivery_merchantapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.main.WelcomeActivity;
import com.group4.fooddelivery_merchantapp.activity.order.OrderActivity;
import com.group4.fooddelivery_merchantapp.activity.order.OrderDetailActivity;
import com.group4.fooddelivery_merchantapp.model.Order;
import com.group4.fooddelivery_merchantapp.model.OrderItem;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    ArrayList<Order> orderList;

    public OrderAdapter() {
    }

    public OrderAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.orderList = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tv_user_name.setText(order.getUserName());
        holder.tv_user_phone_number.setText(order.getUserPhoneNumber());
        holder.tv_order_time.setText(order.getTime());
        holder.tv_total_amount.setText(String.format("%d đ", order.getTmpPrice()));
        holder.tv_total_items.setText(String.format("%d %s", order.getListOrderItems().size(), context.getString(R.string.items)));
        holder.cardViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("OrderType", WelcomeActivity.firebase.orderList.indexOf(orderList));
                intent.putExtra("IndexInList", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static final class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tv_user_name, tv_user_phone_number, tv_total_amount, tv_total_items, tv_order_time, tv_detail;
        CardView cardViewOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_detail = itemView.findViewById(R.id.tv_order_detail);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_user_phone_number = itemView.findViewById(R.id.tv_user_phone);
            tv_total_amount = itemView.findViewById(R.id.tv_total_amount);
            tv_total_items = itemView.findViewById(R.id.tv_total_items);
            tv_order_time = itemView.findViewById(R.id.tv_order_time);
            cardViewOrder = itemView.findViewById(R.id.cardview_order);
        }
    }
}
