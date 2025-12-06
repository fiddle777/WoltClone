package com.example.l3android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.l3android.R;
import com.example.l3android.model.User;
import com.google.gson.Gson;

public class MyInfoActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        // Receive user JSON from WoltRestaurants
        Intent intent = getIntent();
        String userJson = intent.getStringExtra("userJson");
        currentUser = new Gson().fromJson(userJson, User.class);

        // Bind views
        TextView nameText = findViewById(R.id.infoName);
        TextView loginText = findViewById(R.id.infoLogin);
        TextView emailText = findViewById(R.id.infoEmail);
        TextView idText = findViewById(R.id.infoId);

        // Fill fields
        if (currentUser != null) {
            nameText.setText("Name: " + currentUser.getName());
            loginText.setText("Login: " + currentUser.getLogin());
            emailText.setText("Email: " + currentUser.getSurname());
            idText.setText("User ID: " + currentUser.getId());
        }
    }
}
