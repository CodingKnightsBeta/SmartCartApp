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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartcartapp.Model.Products;
import com.project.smartcartapp.Prevalent.Prevalent;
import com.project.smartcartapp.ViewHolder.AdminProductsAdapter;
import com.project.smartcartapp.ViewHolder.ProductsAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewProductsAdminActivity extends AppCompatActivity {

    private SearchView searchView;
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
        setContentView(R.layout.activity_view_products_admin);

        Paper.init(this);
        currentList = Paper.book().read(Prevalent.currentList).toString();
        currentUserPhone = Paper.book().read(Prevalent.UserPhoneKey).toString();
        loadingBar = new ProgressDialog(this);

        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        searchView = findViewById(R.id.view_products_search_view);
        recyclerView = findViewById(R.id.view_products_recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        AdminProductsAdapter adapter = new AdminProductsAdapter(new ArrayList<Products>(), ViewProductsAdminActivity.this);
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

                    AdminProductsAdapter adapter = new AdminProductsAdapter(list, ViewProductsAdminActivity.this);
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
                AdminProductsAdapter adapter = new AdminProductsAdapter(productList, ViewProductsAdminActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
