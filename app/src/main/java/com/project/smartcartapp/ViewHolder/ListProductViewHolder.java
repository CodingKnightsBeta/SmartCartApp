package com.project.smartcartapp.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.smartcartapp.Interface.ItemClickListner;
import com.project.smartcartapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName;
    public Button deleteItemButton;
    public ItemClickListner listner;
    public ListProductViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = (TextView) itemView.findViewById(R.id.product_list_item);
        deleteItemButton = itemView.findViewById(R.id.remove_item_button);
    }
    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }
    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(),false);
    }


}
