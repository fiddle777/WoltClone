package com.example.l3android.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.l3android.R;
import com.example.l3android.model.FoodOrder;

import java.util.List;

public class MyOrdersAdapter extends ArrayAdapter<FoodOrder> {

    public MyOrdersAdapter(@NonNull Context context, @NonNull List<FoodOrder> orders) {
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
                    .inflate(R.layout.item_order, parent, false);
        }

        FoodOrder order = getItem(position);

        TextView restaurantLabel = view.findViewById(R.id.orderRestaurant);
        TextView orderTitle = view.findViewById(R.id.orderTitle);
        TextView orderDate = view.findViewById(R.id.orderDate);
        TextView orderItems = view.findViewById(R.id.orderItems);
        TextView orderPrice = view.findViewById(R.id.orderPrice);

        if (order != null) {
            // Restaurant name
            if (order.getRestaurantName() != null) {
                restaurantLabel.setText(order.getRestaurantName());
            } else {
                restaurantLabel.setText("Unknown restaurant");
            }

            // Title: Order #ID
            orderTitle.setText("Order #" + order.getId());

            // Date
            String rawDate = order.getDateCreated();
            if (rawDate != null && !rawDate.isEmpty()) {
                String displayDate = rawDate.replace('T', ' ');
                int dotIndex = displayDate.indexOf('.');
                if (dotIndex > 0) {
                    displayDate = displayDate.substring(0, dotIndex);
                }
                orderDate.setText(displayDate);
            } else {
                orderDate.setText("Date unknown");
            }

            // Items summary
            if (order.getItemsSummary() != null && !order.getItemsSummary().isEmpty()) {
                orderItems.setText(order.getItemsSummary());
            } else {
                orderItems.setText("No items visible");
            }

            // Price
            if (order.getPrice() != null) {
                orderPrice.setText("€" + String.format("%.2f", order.getPrice()));
            } else {
                orderPrice.setText("Price: N/A");
            }
        }

        return view;
    }
}

