package com.group4.fooddelivery_merchantapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.main.WelcomeActivity;
import com.group4.fooddelivery_merchantapp.activity.order.OrderDetailActivity;
import com.group4.fooddelivery_merchantapp.model.Order;
import com.group4.fooddelivery_merchantapp.model.OrderItem;

import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    Context context;
    ArrayList<OrderItem> itemList;

    public OrderItemAdapter() {
    }

    public OrderItemAdapter(Context context, ArrayList<OrderItem> list) {
        this.context = context;
        this.itemList = list;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new OrderItemViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = itemList.get(position);
        Glide.with(context).load(item.getProduct().getImage().get(0).getUri()).into(holder.imgItem);
        holder.tv_item_name.setText(item.getProduct().getName());
        holder.tv_item_size.setText(item.getSize());
        holder.tv_item_quantity.setText(String.valueOf(item.getQuantity()));
        holder.tv_temp_price.setText(String.valueOf(item.getQuantity() * item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public static final class OrderItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_item_name, tv_item_size, tv_item_quantity, tv_temp_price;
        ImageView imgItem;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_item_size = itemView.findViewById(R.id.tv_item_size);
            tv_item_quantity = itemView.findViewById(R.id.tv_item_quantity);
            tv_temp_price = itemView.findViewById(R.id.tv_temp_price);
        }
    }
}
