package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.smartcartapp.Model.Products;
import com.project.smartcartapp.Prevalent.Prevalent;
import com.project.smartcartapp.ViewHolder.ProductViewHolder;
import com.project.smartcartapp.ViewHolder.ProductsAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private SearchView searchView;
//    private DatabaseReference ProductRef;
    private Query ProductRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private static String currentList;
    private static String currentUserPhone;
    public static ProgressDialog loadingBar;
    public ArrayList<Products> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String category = getIntent().getStringExtra("category");

        Paper.init(this);
        currentList = Paper.book().read(Prevalent.currentList).toString();
        currentUserPhone = Paper.book().read(Prevalent.UserPhoneKey).toString();
        loadingBar = new ProgressDialog(this);

        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products").orderByChild("category").equalTo(category);

        searchView = findViewById(R.id.products_search_view);
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ProductsAdapter adapter = new ProductsAdapter(new ArrayList<Products>(), HomeActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    list = new ArrayList<>();
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        list.add(data.getValue(Products.class));

                    }

                    ProductsAdapter adapter = new ProductsAdapter(list, HomeActivity.this);
                    recyclerView.setAdapter(adapter);

                }
                if(searchView != null ){
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            searchProducts(newText);
                            return true;
                        }
                    });
                }
            }

            private void searchProducts(String str){
                ArrayList<Products> productList = new ArrayList<>();
                for(Products product: list){
                    if(product.getPname().toLowerCase().contains(str.toLowerCase())){
                        productList.add(product);
                    }
                }
                ProductsAdapter adapter = new ProductsAdapter(productList, HomeActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//
//        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
//                .setQuery(ProductRef, Products.class)
//                .build();
//
////        if(options.getSnapshots().isEmpty()){
////            Toast.makeText(this,"Sorry, Currently there are no items in this category",Toast.LENGTH_LONG).show();
////        }
//
//        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
//                        holder.txtProductName.setText(model.getPname());
//                        holder.txtProductDescription.setText(model.getDescription());
//                        holder.txtProductPrice.setText("Price : "+model.getPrice()+" Rs");
//                       Picasso.get().load(model.getImage()).into(holder.imageView);
//
//                        holder.productInfoRelativeLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                               Intent intent = new Intent(HomeActivity.this,ProductInfoActivity.class);
//                               intent.putExtra("pid", model.getPid());
//                               startActivity(intent);
//                            }
//                        });
//
//                         holder.addToListButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                addProductToList(model.getPid(),model.getPname());
//                            }
//                        });
//                    }
//
//                    @NonNull
//                    @Override
//                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout,parent,false);
//                        ProductViewHolder holder = new ProductViewHolder(view);
//                        return holder;
//                    }
//                };
//
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();

    }

    public void viewProductDetails(String pid){
        Intent intent = new Intent(HomeActivity.this, ProductInfoActivity.class);
        intent.putExtra("pid", pid);
        startActivity(intent);
    }


    public static void addProductToList(final String pid, final String pname, final Context context) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!(dataSnapshot.child("Shopping Lists").child(currentUserPhone).child(currentList).child("products").exists())){

                    HashMap<String,Object> ListProductDataMap = new HashMap<>();
                    ListProductDataMap.put("pid",pid);
                    ListProductDataMap.put("pname",pname);

                    RootRef.child("Shopping Lists").child(currentUserPhone).child(currentList).child("products").child(pid).updateChildren(ListProductDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(context,"Product Added to List",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(context,"Network Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

//                else{
//                    Toast.makeText(HomeActivity.this,"Product Already Exists in List",Toast.LENGTH_SHORT).show();
//                    loadingBar.dismiss();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

