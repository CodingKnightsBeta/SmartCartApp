package com.project.smartcartapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.project.smartcartapp.Interface.ItemClickListner;
import com.project.smartcartapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtListName, txtListDescription;
    public ItemClickListner listner;
    public ListsViewHolder(@NonNull View itemView) {
        super(itemView);
        txtListName = (TextView) itemView.findViewById(R.id.list_name);
        txtListDescription = (TextView) itemView.findViewById(R.id.list_description);

    }
    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }
    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(),false);
    }

}


