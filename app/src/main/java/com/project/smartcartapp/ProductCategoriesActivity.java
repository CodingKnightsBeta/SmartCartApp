package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.smartcartapp.Model.Category;
import com.project.smartcartapp.Model.Products;
import com.project.smartcartapp.ViewHolder.CategoryViewHolder;
import com.project.smartcartapp.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class ProductCategoriesActivity extends AppCompatActivity {

    private DatabaseReference CategoryRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);

        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Category");

        recyclerView = findViewById(R.id.recycler_categories);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(CategoryRef, Category.class)
                .build();



        FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull final Category model) {
                        holder.txtCategoryName.setText(model.getCatName());

                        Picasso.get().load(model.getCatImage()).into(holder.imgCategory);

                        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ProductCategoriesActivity.this,HomeActivity.class);
                                intent.putExtra("category", model.getCatName());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout,parent,false);
                        CategoryViewHolder holder = new CategoryViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

}
