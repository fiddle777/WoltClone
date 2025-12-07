package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.MARK_ORDER_DELIVERED;
import static com.example.l3android.Utils.Constants.ASSIGN_ORDER_TO_DRIVER;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.l3android.R;
import com.example.l3android.Utils.RestOperations;
import com.example.l3android.model.FoodOrder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriverOrdersAdapter extends ArrayAdapter<FoodOrder> {

    public enum Mode {
        MY_DELIVERIES,        // button = "Mark delivered"
        AVAILABLE_ORDERS      //button = "Accept order"
    }

    private final List<FoodOrder> orders;
    private final Mode mode;
    private final int driverId;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());


    public DriverOrdersAdapter(@NonNull Context context,
                               @NonNull List<FoodOrder> orders) {
        this(context, orders, Mode.MY_DELIVERIES, 0);
    }

    public DriverOrdersAdapter(@NonNull Context context,
                               @NonNull List<FoodOrder> orders,
                               @NonNull Mode mode,
                               int driverId) {
        super(context, 0, orders);
        this.orders = orders;
        this.mode = mode;
        this.driverId = driverId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_driver_order, parent, false);
        }

        FoodOrder order = orders.get(position);

        TextView title  = view.findViewById(R.id.driverOrderTitle);
        TextView details = view.findViewById(R.id.driverOrderDetails);
        TextView status = view.findViewById(R.id.driverOrderStatus);
        TextView price  = view.findViewById(R.id.driverOrderPrice);
        Button actionButton = view.findViewById(R.id.driverOrderCompleteButton);

        if (order != null) {
            // Title
            String titleText = (order.getName() != null)
                    ? order.getName()
                    : "Order #" + order.getId();
            title.setText(titleText);

            // Details: restaurant + items
            StringBuilder detailsBuilder = new StringBuilder();

            if (order.getRestaurantName() != null && !order.getRestaurantName().isEmpty()) {
                detailsBuilder.append(order.getRestaurantName());
            }
            if (order.getItemsSummary() != null && !order.getItemsSummary().isEmpty()) {
                if (detailsBuilder.length() > 0) {
                    detailsBuilder.append(" | ");
                }
                detailsBuilder.append(order.getItemsSummary());
            }

            String buyerName = order.getBuyerName();
            String buyerPhone = order.getBuyerPhone();
            String buyerAddress = order.getBuyerAddress();

            boolean hasBuyerInfo =
                    (buyerName != null && !buyerName.isEmpty()) ||
                            (buyerPhone != null && !buyerPhone.isEmpty()) ||
                            (buyerAddress != null && !buyerAddress.isEmpty());

            if (hasBuyerInfo) {
                if (detailsBuilder.length() > 0) {
                    detailsBuilder.append("\n");
                }

                boolean firstPiece = true;

                if (buyerName != null && !buyerName.isEmpty()) {
                    detailsBuilder.append("Client: ").append(buyerName);
                    firstPiece = false;
                }
                if (buyerPhone != null && !buyerPhone.isEmpty()) {
                    if (!firstPiece) detailsBuilder.append(" | ");
                    detailsBuilder.append("☎ ").append(buyerPhone);
                    firstPiece = false;
                }
                if (buyerAddress != null && !buyerAddress.isEmpty()) {
                    if (!firstPiece) detailsBuilder.append(" | ");
                    detailsBuilder.append(buyerAddress);
                }
            }

            details.setText(detailsBuilder.toString());

            if (order.getStatus() != null) {
                status.setText(order.getStatus());
            } else {
                status.setText("Status: N/A");
            }

            // Price
            if (order.getPrice() != null) {
                price.setText("€" + String.format("%.2f", order.getPrice()));
            } else {
                price.setText("Price: N/A");
            }

            if (mode == Mode.MY_DELIVERIES) {
                setupMarkDeliveredButton(order, actionButton);
            } else {
                setupAcceptOrderButton(order, actionButton);
            }
        }

        return view;
    }

    private void setupMarkDeliveredButton(FoodOrder order, Button btn) {
        // Already delivered = disabled
        if ("DELIVERED".equalsIgnoreCase(order.getStatus())) {
            btn.setText("Delivered");
            btn.setEnabled(false);
            return;
        }

        btn.setText("Mark delivered");
        btn.setEnabled(true);

        btn.setOnClickListener(v -> executor.execute(() -> {
            try {
                String url = MARK_ORDER_DELIVERED + order.getId();
                String response = RestOperations.sendPost(url, "");

                handler.post(() -> {
                    if (!"Error".equals(response) && !response.isEmpty()) {
                        order.setStatus("DELIVERED");
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), "Order marked delivered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to mark delivered", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private void setupAcceptOrderButton(FoodOrder order, Button btn) {
        btn.setText("Accept order");
        btn.setEnabled(true);

        btn.setOnClickListener(v -> executor.execute(() -> {
            try {
                JsonObject obj = new JsonObject();
                obj.addProperty("orderId", order.getId());
                obj.addProperty("driverId", driverId);

                String payload = new Gson().toJson(obj);
                String response = RestOperations.sendPost(ASSIGN_ORDER_TO_DRIVER, payload);

                handler.post(() -> {
                    if (!"Error".equals(response) && !response.isEmpty()) {
                        Toast.makeText(getContext(), "Order taken", Toast.LENGTH_SHORT).show();

                        // Remove from list and go back to "My deliveries"
                        orders.remove(order);
                        notifyDataSetChanged();

                        if (getContext() instanceof Activity) {
                            ((Activity) getContext()).finish();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to take order", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
