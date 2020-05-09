package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.project.smartcartapp.Model.ListProduct;
import com.project.smartcartapp.Model.Lists;
import com.project.smartcartapp.Prevalent.Prevalent;
import com.project.smartcartapp.ViewHolder.ListProductViewHolder;
import com.project.smartcartapp.ViewHolder.ListsViewHolder;
import com.project.smartcartapp.ViewHolder.ProductViewHolder;

import java.util.HashMap;

public class NewListActivity extends AppCompatActivity {

    private DatabaseReference UserProductListsRef, listRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ProgressDialog loadingBar;

    private Query query;

    private TextView title;
    private EditText lName, lDescription;
    private Button addProductsButton, startShoppingButtton;

    private String status, UserPhoneKey,currentList="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        Paper.init(this);
        UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);

        status = getIntent().getStringExtra("status");

        title = (TextView) findViewById(R.id.create_list_title);
        lName = (EditText) findViewById(R.id.list_name_input);
        lDescription = (EditText) findViewById(R.id.list_description_input);
        addProductsButton = (Button) findViewById(R.id.add_product_to_list_button);
        startShoppingButtton = (Button) findViewById(R.id.start_shopping_button);
        loadingBar = new ProgressDialog(this);


        recyclerView = findViewById(R.id.add_list_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(status.equals("new")){
            title.setText("New List");
            recyclerView.setVisibility(View.INVISIBLE);
            query = FirebaseDatabase.getInstance().getReference().child("Shopping Lists").child("6767676767").child("1").child("products");
            addProductsButton.setText("Add Products");
        }
        else{

            lName.setText(getIntent().getStringExtra("listName"));
            lDescription.setText(getIntent().getStringExtra("listDescription"));
            lName.setKeyListener(null);
            lDescription.setKeyListener(null);

            title.setText("Edit List");
//            currentList = Paper.book().read(Prevalent.currentList);
            currentList = getIntent().getStringExtra("listID");
            addProductsButton.setText("Select List");
            query = FirebaseDatabase.getInstance().getReference().child("Shopping Lists").child(UserPhoneKey).child(currentList).child("products");
        }

        addProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status.equals("new")){
                    createList();
                }
                else{
                    Paper.book().write(Prevalent.currentList,lName.getText().toString());
                    Intent intent = new Intent(NewListActivity.this,MenuActivity.class);
                    startActivity(intent);
                }
            }
        });
        startShoppingButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewListActivity.this, "Map activity will be displayed",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(NewListActivity.this,MenuActivity.class);
//                startActivity(intent);
            }
        });

    }

    private void createList(){
        String name = lName.getText().toString();
        String description = lDescription.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please enter list name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this,"Please enter list description",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Add List");
            loadingBar.setMessage("Creating new List");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            createNewList(name,description);
        }
    }

    private void createNewList(final String name,final String description) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        //query = FirebaseDatabase.getInstance().getReference().child("Shopping Lists").child(UserPhoneKey).child("1").child("products");


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Shopping Lists").child(UserPhoneKey).child(name).exists())){
                    HashMap<String,Object> ListDataMap = new HashMap<>();
                    ListDataMap.put("lid",name);
                    ListDataMap.put("lname",name);
                    ListDataMap.put("description",description);

                    RootRef.child("Shopping Lists").child(UserPhoneKey).child(name).updateChildren(ListDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Paper.book().write(Prevalent.currentList,name);

                                Toast.makeText(NewListActivity.this,"New List created",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(NewListActivity.this,MenuActivity.class);
                                startActivity(intent);
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(NewListActivity.this,"Network Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(NewListActivity.this,"List already exists. Choose another List Name",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ListProduct> options = new FirebaseRecyclerOptions.Builder<ListProduct>()
                .setQuery(query, ListProduct.class)
                .build();

        FirebaseRecyclerAdapter<ListProduct, ListProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<ListProduct, ListProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListProductViewHolder holder, int position, @NonNull ListProduct model) {
                        holder.txtProductName.setText(model.getPname());
                    }

                    @NonNull
                    @Override
                    public ListProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_product_layout, parent, false);
                        ListProductViewHolder holder = new ListProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

}
