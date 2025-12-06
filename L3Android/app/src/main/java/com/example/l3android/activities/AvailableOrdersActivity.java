package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.GET_AVAILABLE_ORDERS;
import static com.example.l3android.Utils.Constants.ASSIGN_ORDER_TO_DRIVER;

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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AvailableOrdersActivity extends AppCompatActivity {

    private int driverId;
    private List<FoodOrder> availableOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_orders);

        Intent intent = getIntent();
        driverId = intent.getIntExtra("driverId", 0);

        ListView listView = findViewById(R.id.availableOrdersList);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_AVAILABLE_ORDERS);

                handler.post(() -> {
                    try {
                        Type listType = new TypeToken<List<FoodOrder>>() {}.getType();
                        availableOrders = new Gson().fromJson(response, listType);

                        DriverOrdersAdapter adapter =
                                new DriverOrdersAdapter(this, availableOrders);
                        listView.setAdapter(adapter);

                        // CLICK = take this order
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            FoodOrder selectedOrder = availableOrders.get(position);
                            takeOrder(selectedOrder.getId());
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void takeOrder(int orderId) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // Build JSON: { "orderId": X, "driverId": Y }
                JsonObject obj = new JsonObject();
                obj.addProperty("orderId", orderId);
                obj.addProperty("driverId", driverId);

                String payload = new Gson().toJson(obj);

                String response = RestOperations.sendPost(ASSIGN_ORDER_TO_DRIVER, payload);

                handler.post(() -> {
                    if (!"Error".equals(response) && !response.isEmpty()) {
                        Toast.makeText(this, "Order taken", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to My Deliveries and refresh and stuff and like you know
                    } else {
                        Toast.makeText(this, "Failed to take order", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
