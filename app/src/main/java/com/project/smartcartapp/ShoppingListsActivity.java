package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.smartcartapp.Model.Lists;
import com.project.smartcartapp.Model.Products;
import com.project.smartcartapp.Model.Users;
import com.project.smartcartapp.Prevalent.Prevalent;
import com.project.smartcartapp.ViewHolder.ListsViewHolder;
import com.project.smartcartapp.ViewHolder.ProductViewHolder;

import java.sql.Ref;

public class ShoppingListsActivity extends AppCompatActivity {

    private DatabaseReference UserListsRef, listRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_lists);

        Paper.init(this);
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        UserListsRef = FirebaseDatabase.getInstance().getReference().child("Lists");//.child("6767676767");
        //listRef = FirebaseDatabase.getInstance().getReference().child("Shopping Lists");//.child("6767676767");

        recyclerView = findViewById(R.id.recycler_lists);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Toast.makeText(ShoppingListsActivity.this, listRef.child(UserPhoneKey)+" ",Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Lists> options = new FirebaseRecyclerOptions.Builder<Lists>()
                .setQuery(UserListsRef, Lists.class)
                .build();


        FirebaseRecyclerAdapter<Lists, ListsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Lists, ListsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListsViewHolder holder, int position, @NonNull Lists model) {
                        holder.txtListName.setText(model.getLname());
                        holder.txtListDescription.setText(model.getDescription());
                    }

                    @NonNull
                    @Override
                    public ListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
                        ListsViewHolder holder = new ListsViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}