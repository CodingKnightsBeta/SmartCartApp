package com.project.smartcartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.smartcartapp.Model.Users;
import com.project.smartcartapp.Prevalent.Prevalent;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText InputPhoneNumber, InputPassword;
    private TextView AdminLink, NotAdminLink;
    private ProgressDialog loadingBar;

    private String parentDbName = "Users";

    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);
        rememberMe = (CheckBox) findViewById(R.id.remember_me_checkbox);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        Paper.init(this);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Admin Login");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

    }
    private void LoginUser(){
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Account Login");
            loadingBar.setMessage("Loging in ...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            //Background process of creating account
            AllowAccessToAccount(phone,password);


        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(rememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
            Paper.book().write(Prevalent.currentList,"");
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phone).exists()){
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){
//                            if(parentDbName.equals("Admins")){
//                                Toast.makeText(LoginActivity.this, "Welcome Administrator "+usersData.getName()+" ...", Toast.LENGTH_SHORT).show();
//                                loadingBar.dismiss();
//                                Intent intent = new Intent(LoginActivity.this,AdminCategoryActivity.class);
//                                startActivity(intent);
//                            }
                            if(parentDbName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Welcome "+usersData.getName()+" ...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(intent);
                            }
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Incorrect password.",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            InputPassword.setText("");
                        }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this,"No account with this number exists. Please create a new account",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
