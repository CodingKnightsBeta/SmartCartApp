package com.project.smartcartapp.ViewHolder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.smartcartapp.HomeActivity;
import com.project.smartcartapp.MenuActivity;
import com.project.smartcartapp.Model.Products;
import com.project.smartcartapp.Prevalent.Prevalent;
import com.project.smartcartapp.ProductInfoActivity;
import com.project.smartcartapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.productItemsViewHolder>{
    ArrayList<Products> list;
    private Context context;

    public ProductsAdapter(ArrayList<Products> list, Context context){
        this.list = list;
        this.context = context;
    }
    @NonNull
    public productItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
        productItemsViewHolder holder = new productItemsViewHolder(view);
        return holder;
    }

    public void onBindViewHolder(@NonNull productItemsViewHolder holder,final int position) {
        holder.txtProductName.setText(list.get(position).getPname());
        holder.txtProductDescription.setText(list.get(position).getDescription());
        holder.txtProductPrice.setText("Price : "+list.get(position).getPrice()+" Rs");
        Picasso.get().load(list.get(position).getImage()).into(holder.imageView);

        holder.productInfoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ProductInfoActivity.class);
                intent.putExtra("pid", list.get(position).getPid());
                context.startActivity(intent);
            }
        });

        holder.addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Paper.book().read(Prevalent.currentList).toString().equals("")){
                    AlertDialog.Builder alertDB = new AlertDialog.Builder(context);
                    alertDB.setTitle("No List Selected");
                    alertDB.setMessage("Please create a new list or select an existing list first");
                    alertDB.setCancelable(false);
                    alertDB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, MenuActivity.class);
                            context.startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = alertDB.create();
                    alertDialog.show();
                }
                else {
                    HomeActivity.loadingBar.setTitle("Add Product to List");
                    HomeActivity.loadingBar.setMessage("Adding " + list.get(position).getPname() + " to list" + Paper.book().read(Prevalent.currentList).toString());
                    HomeActivity.loadingBar.setCanceledOnTouchOutside(false);
                    HomeActivity.loadingBar.show();
                    HomeActivity.addProductToList(list.get(position).getPid(),list.get(position).getPname(), context);
                }

            }
        });
    }

    public int getItemCount() {
        return list.size();
    }

    public class productItemsViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProductName, txtProductDescription, txtProductPrice;
        public ImageView imageView;
        public RelativeLayout productInfoRelativeLayout;
        public Button addToListButton;
        public productItemsViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.product_image);
            txtProductName = (TextView) itemView.findViewById(R.id.product_name);
            txtProductPrice = (TextView) itemView.findViewById(R.id.product_price);
            txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
            productInfoRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.product_relative_layout);
            addToListButton = (Button) itemView.findViewById(R.id.direct_add_to_list_button);
        }
    }
}
