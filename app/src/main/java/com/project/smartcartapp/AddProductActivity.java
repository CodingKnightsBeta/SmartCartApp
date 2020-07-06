package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Random;

public class AddProductActivity extends AppCompatActivity {

    Spinner categorySpinner;
    EditText pName, pDescription, price, section;
    Button addProductBtn;
    ImageView productImage;
    String prodName, prodDesc, prodPrice, prodSection, prodCategory, prodImage;

    ArrayAdapter<String> arrayAdapter;
    String categories[] = {"Fashion","Groceries","Drinks"};
    private static final int galleryPick = 1;
    private Uri imageUri;
    private String downloadImageUrl;
    private StorageReference ProductImagesRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");

        pName = (EditText) findViewById(R.id.add_product_product_name);
        pDescription = (EditText) findViewById(R.id.add_product_product_description);
        price = (EditText) findViewById(R.id.add_product_product_price);
        section = (EditText) findViewById(R.id.add_product_product_section);
        productImage = (ImageView) findViewById(R.id.add_product_product_image);
        addProductBtn = (Button) findViewById(R.id.add_product_button);
        categorySpinner = (Spinner) findViewById(R.id.add_product_category_spinner);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        categorySpinner.setAdapter(arrayAdapter);

        loadingBar = new ProgressDialog(this);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext()," "+categories[position], Toast.LENGTH_SHORT).show();
                prodCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }

    private void OpenGallery() {
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("Image/*");
        startActivityForResult(gallery, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPick && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }

    private void ValidateProductData() {
        prodName = pName.getText().toString();
        prodDesc = pDescription.getText().toString();
        prodPrice = price.getText().toString();
        prodSection = section.getText().toString();

        if(imageUri == null){
            Toast.makeText(this,"Please select a product image.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(prodName)){
            Toast.makeText(this, "Please enter the Product Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(prodDesc)){
            Toast.makeText(this, "Please enter the Product Description", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(prodPrice)){
            Toast.makeText(this, "Please enter the Product Price", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(prodSection)){
            Toast.makeText(this, "Please enter the Product Section", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d("productInfo",prodName+" "+prodDesc+" "+prodPrice+" "+prodSection+" "+prodCategory+" "+imageUri);
            StoreProductData();
        }
    }

    private void StoreProductData() {
        loadingBar.setTitle("Adding Product");
        loadingBar.setMessage("Adding Product "+prodName);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        StorageReference storageReference = ProductImagesRef.child(imageUri.getLastPathSegment());

        final UploadTask uploadTask = storageReference.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProductActivity.this, "Error :"+e.toString(), Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddProductActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl= storageReference.getDownloadUrl().toString();
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddProductActivity.this, "Image url fetched...", Toast.LENGTH_SHORT).show();
                            saveDataToDataBase();
                        }
                    }
                });
            }
        });
    }

    private void saveDataToDataBase() {
        DatabaseReference prodReference = FirebaseDatabase.getInstance().getReference().child("Products");

        prodReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = (dataSnapshot.getChildrenCount()+1)+"";
                HashMap<String, Object> productInfo = new HashMap<>();
                productInfo.put("pid",id);
                productInfo.put("pname",prodName);
                productInfo.put("description",prodDesc);
                productInfo.put("price",prodPrice);
                productInfo.put("section",prodSection);
                productInfo.put("category",prodCategory);
                productInfo.put("image",downloadImageUrl);

                prodReference.child(id).updateChildren(productInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            loadingBar.dismiss();
                            Toast.makeText(AddProductActivity.this, "Product Added to the database successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddProductActivity.this,MenuActivity.class);
                            startActivity(intent);
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(AddProductActivity.this, "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
