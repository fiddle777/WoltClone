package com.example.l3android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l3android.model.BasicUser;
import com.example.l3android.model.Driver;
import com.example.l3android.model.Restaurant;
import com.example.l3android.model.User;
import com.google.gson.Gson;

import java.lang.reflect.Executable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.BaseStream;

public class WoltRestaurants extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wolt_restaurants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Prieiga prie duomenu is preaito acitivity
        Intent intent = getIntent();
        String userInfo = intent.getStringExtra("userJsonObject");
        Gson gson = new Gson();
        var connectedUser = gson.fromJson(userInfo, User.class);
        if(connectedUser instanceof Driver){

        }else if(connectedUser instanceof Restaurant){
            //neleisti sito
        }else{
            //basic user in this case
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(()->{
                String response = RestOperations.sendGet();
            });
        }
    }
    public void viewPurchaseHistory(View view){
    }
    public void viewMyAccount(View view) {
    }
}