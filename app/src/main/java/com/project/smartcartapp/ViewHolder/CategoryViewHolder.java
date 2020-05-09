package com.project.smartcartapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.smartcartapp.Interface.ItemClickListner;
import com.project.smartcartapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtCategoryName;
    public ImageView imgCategory;
    public ItemClickListner listner;
    public RelativeLayout relativeLayout;
    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        txtCategoryName = (TextView) itemView.findViewById(R.id.category_name);
        imgCategory = (ImageView) itemView.findViewById(R.id.category_image);
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.category_relative_layout);
    }
    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }
    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(),false);
    }

}
