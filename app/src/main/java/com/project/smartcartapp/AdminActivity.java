package com.project.smartcartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity {
    private Button GenerateRules, goToAddProducts, ViewProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        GenerateRules = (Button) findViewById(R.id.generate_rules_btn);
        goToAddProducts = (Button) findViewById(R.id.add_products_page_btn);
        ViewProducts = (Button) findViewById(R.id.view_products_admin_btn);

        GenerateRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, Recommend.class);
                startActivity(intent);
            }
        });
        goToAddProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
        ViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ViewProductsAdminActivity.class);
                startActivity(intent);
            }
        });
    }
}
