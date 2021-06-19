package com.group4.fooddelivery_merchantapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.model.ProductImage;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    Context context;
    ArrayList<ProductImage> imageList;
    public ArrayList<ProductImage> tempImageList;

    public ImageAdapter() {
    }

    public ImageAdapter(Context context, ArrayList<ProductImage> imageList) {
        this.context = context;
        this.tempImageList = imageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(context).load(tempImageList.get(position + 1).getUri()).into(holder.iv_image);
    }

    @Override
    public int getItemCount() {
        if (tempImageList != null)
            return tempImageList.size() - 1;
        return 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image, bt_remove;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            bt_remove = itemView.findViewById(R.id.item_remove);
            iv_image = itemView.findViewById(R.id.iv_image);

            bt_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAt(getPosition());
                }
            });

        }

        public void removeAt(int position) {
            tempImageList.remove(position);
            notifyItemChanged(position);
            notifyItemRangeRemoved(position, tempImageList.size());
        }
    }
}
