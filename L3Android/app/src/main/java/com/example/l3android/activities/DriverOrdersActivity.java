package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.GET_ORDERS_FOR_DRIVER;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.l3android.R;
import com.example.l3android.Utils.RestOperations;
import com.example.l3android.model.FoodOrder;
import com.example.l3android.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriverOrdersActivity extends AppCompatActivity {

    private int driverId;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_orders);

        Intent intent = getIntent();
        driverId = intent.getIntExtra("driverId", 0);

        listView = findViewById(R.id.driverOrdersList);
        Button takeMoreBtn = findViewById(R.id.takeMoreOrdersButton);

        takeMoreBtn.setOnClickListener(v -> {
            Intent i = new Intent(DriverOrdersActivity.this, AvailableOrdersActivity.class);
            i.putExtra("driverId", driverId);
            startActivity(i);
        });

        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh
        loadOrders();
    }

    private void loadOrders() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_ORDERS_FOR_DRIVER + driverId);

                handler.post(() -> {
                    try {
                        Type listType = new TypeToken<List<FoodOrder>>() {}.getType();
                        List<FoodOrder> orders = new Gson().fromJson(response, listType);

                        DriverOrdersAdapter adapter = new DriverOrdersAdapter(this, orders);
                        listView.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

