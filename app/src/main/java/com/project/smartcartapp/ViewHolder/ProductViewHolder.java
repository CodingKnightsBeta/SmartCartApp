package com.project.smartcartapp.ViewHolder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.smartcartapp.Interface.ItemClickListner;
import com.project.smartcartapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;
    public RelativeLayout productInfoRelativeLayout;
    public Button addToListButton;

    public ItemClickListner listner;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        productInfoRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.product_relative_layout);
        addToListButton = (Button) itemView.findViewById(R.id.direct_add_to_list_button);
    }
    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }
    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(),false);
    }
}
