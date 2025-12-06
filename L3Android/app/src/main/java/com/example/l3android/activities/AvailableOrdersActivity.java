package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.GET_AVAILABLE_ORDERS;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.l3android.R;
import com.example.l3android.Utils.RestOperations;
import com.example.l3android.model.FoodOrder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AvailableOrdersActivity extends AppCompatActivity {

    private int driverId;
    private List<FoodOrder> availableOrders;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_orders);

        Intent intent = getIntent();
        driverId = intent.getIntExtra("driverId", 0);

        ListView listView = findViewById(R.id.availableOrdersList);

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_AVAILABLE_ORDERS);

                handler.post(() -> {
                    try {
                        Type listType = new TypeToken<List<FoodOrder>>() {}.getType();
                        availableOrders = new Gson().fromJson(response, listType);

                        DriverOrdersAdapter adapter = new DriverOrdersAdapter(
                                AvailableOrdersActivity.this,
                                availableOrders,
                                DriverOrdersAdapter.Mode.AVAILABLE_ORDERS,
                                driverId
                        );
                        listView.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load available orders", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() ->
                        Toast.makeText(this, "Network error while loading orders", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
