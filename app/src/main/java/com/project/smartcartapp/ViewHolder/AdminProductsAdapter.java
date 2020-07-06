package com.project.smartcartapp.ViewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.smartcartapp.EditProductActivity;
import com.project.smartcartapp.Model.Products;
import com.project.smartcartapp.Prevalent.Prevalent;
import com.project.smartcartapp.ProductInfoActivity;
import com.project.smartcartapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

public class AdminProductsAdapter extends RecyclerView.Adapter<AdminProductsAdapter.productItemsAdminViewHolder>{
    ArrayList<Products> list;
    private Context context;

    public AdminProductsAdapter(ArrayList<Products> list, Context context){
        this.list = list;
        this.context = context;
    }
    @NonNull
    public AdminProductsAdapter.productItemsAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
        AdminProductsAdapter.productItemsAdminViewHolder holder = new AdminProductsAdapter.productItemsAdminViewHolder(view);
        return holder;
    }

    public void onBindViewHolder(@NonNull AdminProductsAdapter.productItemsAdminViewHolder holder, final int position) {
        holder.txtProductName.setText(list.get(position).getPname());
        holder.txtProductDescription.setText(list.get(position).getDescription());
        holder.txtProductPrice.setText("Price : "+list.get(position).getPrice()+" Rs");
        Picasso.get().load(list.get(position).getImage()).into(holder.imageView);

        holder.productInfoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("pid", list.get(position).getPid());
                context.startActivity(intent);
            }
        });


    }

    public int getItemCount() {
        return list.size();
    }

    public class productItemsAdminViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProductName, txtProductDescription, txtProductPrice;
        public ImageView imageView;
        public RelativeLayout productInfoRelativeLayout;
        public Button addToListButton;
        public productItemsAdminViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.product_image);
            txtProductName = (TextView) itemView.findViewById(R.id.product_name);
            txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
            txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
            productInfoRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.product_relative_layout);
            addToListButton = (Button) itemView.findViewById(R.id.direct_add_to_list_button);
            addToListButton.setVisibility(View.INVISIBLE);
        }
    }

}
