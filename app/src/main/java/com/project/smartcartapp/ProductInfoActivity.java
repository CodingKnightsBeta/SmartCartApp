package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartcartapp.Model.Products;

public class ProductInfoActivity extends AppCompatActivity {

    private Button addToListButton;
    private ImageView productImage;
    private TextView pname, price, description;

    private String pid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        addToListButton = (Button) findViewById(R.id.add_to_list_button);
        productImage = (ImageView) findViewById(R.id.product_details_image);
        pname = (TextView) findViewById(R.id.product_details_name);
        price = (TextView) findViewById(R.id.product_details_price);
        description = (TextView) findViewById(R.id.product_details_description);

        pid = getIntent().getStringExtra("pid");

        getProductDetails(pid);

        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToProductList();
            }
        });
    }

    public void addToProductList(){
        Toast.makeText(this,"Product Added to List",Toast.LENGTH_LONG).show();

    }

    private void getProductDetails(String pid) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Products product = dataSnapshot.getValue(Products.class);

                    pname.setText(product.getPname());
                    price.setText(product.getPrice());
                    description.setText(product.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
