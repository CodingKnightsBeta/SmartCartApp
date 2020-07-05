package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.project.smartcartapp.ViewHolder.ProductsAdapter;
import com.project.smartcartapp.ViewHolder.RecommendedProductsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class RecommendationsActivity extends AppCompatActivity {

    private Query ProductRef;
    private DatabaseReference ListRef, RulesRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private static String currentList;
    private static String currentUserPhone;
    public static ProgressDialog loadingBar;
    public ArrayList<Products> productList;
    public HashMap<ArrayList,ArrayList> rules = new HashMap<>();
    public ArrayList recomendations = new ArrayList();
    private TextView reccStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        Paper.init(this);
        currentList = Paper.book().read(Prevalent.currentList).toString();
        currentUserPhone = Paper.book().read(Prevalent.UserPhoneKey).toString();
        loadingBar = new ProgressDialog(this);

        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");
        ListRef = FirebaseDatabase.getInstance().getReference().child("Shopping Lists");

        reccStatus = findViewById(R.id.recc_status);
        recyclerView = findViewById(R.id.recc_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ProductsAdapter adapter = new ProductsAdapter(new ArrayList<Products>(), RecommendationsActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();


        ProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    productList = new ArrayList<>();
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        productList.add(data.getValue(Products.class));
                    }
                    getRecommendations();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getRecommendations() {
        ListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("product",""+dataSnapshot.child(currentUserPhone).child(currentList).child("products").getChildrenCount());
                ArrayList<String> list = new ArrayList<>();
                for(DataSnapshot pro: dataSnapshot.child(currentUserPhone).child(currentList).child("products").getChildren()){
                    list.add(pro.getKey()+"");
                }
                Log.d("product",""+list);

                RulesRef = FirebaseDatabase.getInstance().getReference().child("Rules");

                RulesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot aRule: dataSnapshot.getChildren()){
                            rules.put(new ArrayList(Arrays.asList(aRule.getKey().toString().split(", "))),new ArrayList(Arrays.asList(aRule.getValue().toString().split(", "))));
                        }

                        Log.d("product all rules",rules+"");

                        rules.keySet().stream().filter(a-> list.containsAll(a)).forEach(v -> {
                            Log.d("product item",""+rules.get(v)+"");
                            for(Object item: rules.get(v)){
                                if((!recomendations.contains(item)) && (!list.contains(item))){
                                    recomendations.add(item);
                                }
                            }
                        });
                        Log.d("product recc",""+recomendations);

                        ArrayList<Products> recomProdList = new ArrayList<>();

                        productList.forEach(v -> {
                            if(recomendations.contains(v.getPid())){
                                recomProdList.add(v);
                            }
                        });
                        if(!recomProdList.isEmpty()){
                            recyclerView.setVisibility(View.VISIBLE);
                            reccStatus.setVisibility(View.INVISIBLE);
                            RecommendedProductsAdapter adapter = new RecommendedProductsAdapter(recomProdList, RecommendationsActivity.this);
                            recyclerView.setAdapter(adapter);
                        }
                        else {
                            recyclerView.setVisibility(View.INVISIBLE);
                            reccStatus.setText("No recommendations for you yet!");
                            reccStatus.setVisibility(View.VISIBLE);
                        }
                        Log.d("recom",""+recomProdList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void viewProductDetails(String pid){
        Intent intent = new Intent(RecommendationsActivity.this, ProductInfoActivity.class);
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
