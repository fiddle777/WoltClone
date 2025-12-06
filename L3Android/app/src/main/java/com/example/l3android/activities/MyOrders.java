package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.GET_ORDERS_BY_USER;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class MyOrders extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get user ID from previous activity
        Intent intent = getIntent();
        userId = intent.getIntExtra("id", 0);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_ORDERS_BY_USER + userId);
                System.out.println("Orders response: " + response);

                handler.post(() -> {
                    try {
                        if (!"Error".equals(response) && !response.isEmpty()) {
                            Type ordersListType = new TypeToken<List<FoodOrder>>() {}.getType();
                            List<FoodOrder> ordersListFromJson =
                                    new Gson().fromJson(response, ordersListType);

                            ListView ordersListElement = findViewById(R.id.myOrderList);
                            MyOrdersAdapter adapter = new MyOrdersAdapter(this, ordersListFromJson);
                            ordersListElement.setAdapter(adapter);

                            // Click = open chat for that order
                            ordersListElement.setOnItemClickListener((parent, view, position, id) -> {
                                FoodOrder selectedOrder = ordersListFromJson.get(position);
                                Intent intentChat =
                                        new Intent(MyOrders.this, ChatSystem.class);
                                intentChat.putExtra("orderId", selectedOrder.getId());
                                intentChat.putExtra("userId", userId);
                                startActivity(intentChat);
                            });
                        } else {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
