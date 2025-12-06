package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.MARK_ORDER_DELIVERED;
import static com.example.l3android.Utils.Constants.HOME_URL;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.l3android.R;
import com.example.l3android.Utils.RestOperations;
import com.example.l3android.model.FoodOrder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriverOrdersAdapter extends ArrayAdapter<FoodOrder> {

    public DriverOrdersAdapter(@NonNull Context context, @NonNull List<FoodOrder> orders) {
        super(context, 0, orders);
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_driver_order, parent, false);
        }

        FoodOrder order = getItem(position);

        TextView title = view.findViewById(R.id.driverOrderTitle);
        TextView restaurant = view.findViewById(R.id.driverOrderRestaurant);
        TextView status = view.findViewById(R.id.driverOrderStatus);
        TextView price = view.findViewById(R.id.driverOrderPrice);
        Button completeBtn = view.findViewById(R.id.driverOrderCompleteButton);

        if (order != null) {
            title.setText("Order #" + order.getId());

            if (order.getRestaurantName() != null) {
                restaurant.setText(order.getRestaurantName());
            } else {
                restaurant.setText("Restaurant: unknown");
            }

            if (order.getStatus() != null) {
                status.setText("Status: " + order.getStatus());
            } else {
                status.setText("Status: N/A");
            }

            if (order.getPrice() != null) {
                price.setText("€" + String.format("%.2f", order.getPrice()));
            } else {
                price.setText("Price: N/A");
            }

            completeBtn.setOnClickListener(v -> {
                Executor executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    try {
                        String response = RestOperations
                                .sendPost(MARK_ORDER_DELIVERED + order.getId(), "");
                        handler.post(() -> {
                            order.setStatus("DELIVERED");
                            notifyDataSetChanged();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }

        return view;
    }
}
