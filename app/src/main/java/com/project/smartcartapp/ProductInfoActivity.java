package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartcartapp.Model.Products;
import com.project.smartcartapp.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProductInfoActivity extends AppCompatActivity {

    private Button addToListButton;
    private ImageView productImage;
    private TextView pname, price, description;

    private String pid = "";
    private String productName, section, currentList, currentUserPhone;
    public ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        Paper.init(this);
        currentList = Paper.book().read(Prevalent.currentList).toString();
        currentUserPhone = Paper.book().read(Prevalent.UserPhoneKey).toString();

        loadingBar = new ProgressDialog(this);

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
                if(currentList.equals("")){
                    AlertDialog.Builder alertDB = new AlertDialog.Builder(ProductInfoActivity.this);
                    alertDB.setTitle("No List Selected");
                    alertDB.setMessage("Please create a new list or select an existing list first");
                    alertDB.setCancelable(false);
                    alertDB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ProductInfoActivity.this,MenuActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertDialog = alertDB.create();
                    alertDialog.show();
                }
                else {
                    loadingBar.setTitle("Add Product to List");
                    loadingBar.setMessage("Adding " + productName + " to list" + currentList);
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    addProductToList(pid, productName, section);
                }
            }
        });
    }


    private void getProductDetails(String pid) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Products product = dataSnapshot.getValue(Products.class);

                    pname.setText("Product Name: "+product.getPname());
                    price.setText("Price : "+product.getPrice()+" Rs.");
                    description.setText(product.getDescription());
                    productName = product.getPname();
                    section = product.getSection();
                    Picasso.get().load(product.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addProductToList(final String pid,final String pname, final String section) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!(dataSnapshot.child("Shopping Lists").child(currentUserPhone).child(currentList).child("products").exists())){
                if(true){
                    HashMap<String,Object> ListProductDataMap = new HashMap<>();
                    ListProductDataMap.put("pid",pid);
                    ListProductDataMap.put("pname",pname);
                    ListProductDataMap.put("section",section);

                    RootRef.child("Shopping Lists").child(currentUserPhone).child(currentList).child("products").child(pid).updateChildren(ListProductDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(ProductInfoActivity.this,"Product Added to List",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
//                                Intent intent = new Intent(ProductInfoActivity.this,MenuActivity.class);
//                                startActivity(intent);
                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(ProductInfoActivity.this,"Network Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(ProductInfoActivity.this,"Product Already Exists in List",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
