package com.group4.fooddelivery_merchantapp.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.group4.fooddelivery_merchantapp.activity.store.EditProductActivity;
import com.group4.fooddelivery_merchantapp.model.Product;
import com.group4.fooddelivery_merchantapp.model.ProductImage;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ListProductViewHolder> {

    Context context;
    List<Product> products;

    FirebaseFirestore root;
    StorageReference storage;
    FirebaseStorage mStorage;

    ProgressDialog progressDialog;

    public ListProductAdapter() {
    }

    public ListProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;

        root = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(context);
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
                openConfirmDialog(getPosition());
            } else if (v.equals(bt_edit)) {
                Intent editProductActivity = new Intent(context, EditProductActivity.class);
                editProductActivity.putExtra("Product", String.valueOf(getPosition()));
                context.startActivity(editProductActivity);
            }
        }
    }

    private void openConfirmDialog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowsAttribute = window.getAttributes();
        windowsAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowsAttribute);

        dialog.setCancelable(false);


        Button bt_cancel = dialog.findViewById(R.id.dialog_cancel);
        Button bt_yes = dialog.findViewById(R.id.dialog_yes);
        TextView tv_title = dialog.findViewById(R.id.dialog_title);
        TextView tv_details = dialog.findViewById(R.id.dialog_details);

        tv_title.setText(R.string.delete_title);
        tv_details.setText(R.string.delete_details);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                removeAt(position);
            }
        });

        dialog.show();
    }

    public void removeAt(int position) {
        progressDialog.setMessage("Đang xóa thông tin...");
        progressDialog.show();
        removeFromFirestore(products.get(position), position);
    }

    private void removeFromFirestore(Product product, int position) {
        root.collection("Product/").document(product.getID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeImage(product, position);
                    }
                });
    }

    private void removeImage(Product product, int position) {
        for (ProductImage image : product.getImage()) {
            root.collection("Product/" + product.getID() + "/Photos/")
                    .document(image.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("IMG", "Remove from firestore: " + image.getId());
                            mStorage.getReferenceFromUrl(image.getUri())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e("IMG", "Remove from storage: " + image.getId());
                                            if (image.getUri().equals(
                                                    product.getImage().get(product.getImage().size() - 1).getUri())) {
                                                products.remove(position);
                                                notifyItemChanged(position);
                                                notifyItemRangeRemoved(position, products.size());
                                                progressDialog.dismiss();
                                                Toasty.success(context,"Xóa món thành công!").show();
                                            }

                                        }
                                    });
                        }
                    });
        }
    }
}
