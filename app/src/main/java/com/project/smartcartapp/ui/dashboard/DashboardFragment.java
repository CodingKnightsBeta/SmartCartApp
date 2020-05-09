package com.project.smartcartapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.smartcartapp.HomeActivity;
import com.project.smartcartapp.MenuActivity;
import com.project.smartcartapp.Model.Category;
import com.project.smartcartapp.ProductCategoriesActivity;
import com.project.smartcartapp.R;
import com.project.smartcartapp.ViewHolder.CategoryViewHolder;
import com.squareup.picasso.Picasso;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private DatabaseReference CategoryRef;
    private RecyclerView recyclerView;
    private View CategoryView;
    RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        CategoryView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = (RecyclerView) CategoryView.findViewById(R.id.recycler_display_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Category");

        return CategoryView;
    }

    @Override
    public void onStart() {
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
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
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
