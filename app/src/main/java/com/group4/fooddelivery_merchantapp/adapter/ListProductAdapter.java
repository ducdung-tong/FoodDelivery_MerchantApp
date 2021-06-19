package com.group4.fooddelivery_merchantapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.main.WelcomeActivity;
import com.group4.fooddelivery_merchantapp.activity.store.EditProductActivity;
import com.group4.fooddelivery_merchantapp.model.Product;

import java.util.List;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ListProductViewHolder> {

    Context context;
    List<Product> products;

    FirebaseFirestore root;
    StorageReference storage;

    public ListProductAdapter() {
    }

    public ListProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;

        root = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public ListProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ListProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListProductAdapter.ListProductViewHolder holder, int position) {
        Product p = products.get(position);
        holder.tv_name.setText(p.getName());
        int numPrice = p.getPrice().size() - 1;
        String price = "Giá: " + p.getPrice().get(0) + " ~ " + p.getPrice().get(numPrice) + "VNĐ";
        holder.tv_price.setText(price);
        Glide.with(context).load(p.getImage().get(0).getUri()).into(holder.iv_image);
        holder.tv_status.setText(p.getStatus());
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public class ListProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_price, tv_status;
        ImageView iv_image;
        ImageButton bt_edit, bt_delete;

        public ListProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.product_item_name);
            tv_price = itemView.findViewById(R.id.product_item_price);
            tv_status = itemView.findViewById(R.id.product_item_status);
            iv_image = itemView.findViewById(R.id.product_item_image);
            bt_edit = itemView.findViewById(R.id.product_item_modify);
            bt_delete = itemView.findViewById(R.id.product_item_delete);

            bt_delete.setOnClickListener(this);
            bt_edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                if (v.equals(bt_delete)) {
                removeAt(getPosition());
            } else if (v.equals(bt_edit)) {
                Intent editProductActivity = new Intent(context, EditProductActivity.class);
                editProductActivity.putExtra("Product", String.valueOf(getPosition()));
                context.startActivity(editProductActivity);
            }
        }
    }

    public void removeAt(int position) {
        removeFromFirestore(products.get(position).getID());
        products.remove(position);
        notifyItemChanged(position);
        notifyItemRangeRemoved(position, products.size());
    }

    private void removeFromFirestore(String ID) {
        root.collection("Product/").document(ID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        StorageReference fileRef = storage.child("ProductImage/" + WelcomeActivity.firebase.merchantId
                                + "/" + ID);
                        fileRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("IMG", "Remove images storage");
                                    }
                                });
                    }
                });
    }
}
