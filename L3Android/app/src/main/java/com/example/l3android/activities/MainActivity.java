package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.VALIDATE_USER_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l3android.R;
import com.example.l3android.Utils.RestOperations;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void validateUser(View view) {
        TextView login = findViewById(R.id.loginField);
        TextView password = findViewById(R.id.passwordField);

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", login.getText().toString());
        jsonObject.addProperty("password", password.getText().toString());
        String info = gson.toJson(jsonObject);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(VALIDATE_USER_URL, info);

                handler.post(() -> {
                    // Network / server error
                    if (response == null || response.isEmpty() || "Error".equals(response)) {
                        Toast.makeText(MainActivity.this,
                                "Server error. Try again.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JsonObject userJson = new JsonParser().parse(response).getAsJsonObject();

                    // Read status from backend
                    String status = userJson.has("status") && !userJson.get("status").isJsonNull()
                            ? userJson.get("status").getAsString()
                            : "OK";

                    if ("NO_USER".equals(status)) {
                        Toast.makeText(MainActivity.this,
                                "User with this login does not exist.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ("WRONG_PASSWORD".equals(status)) {
                        Toast.makeText(MainActivity.this,
                                "Incorrect password.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String userType = userJson.get("userType").getAsString();

                    boolean isDriver = userType.equals("com.example.l3web.model.Driver");
                    boolean isBasicUser = userType.equals("com.example.l3web.model.BasicUser");
                    boolean isRestaurant = userType.equals("com.example.l3web.model.Restaurant");
                    Log.d("ACHTUNGASOHAOSIHGAUserTypeCheck", "isDriver=" + isDriver
                            + ", isBasicUser=" + isBasicUser
                            + ", isRestaurant=" + isRestaurant);

                    // Block restaurants
                    if (isRestaurant) {
                        Toast.makeText(MainActivity.this,
                                "Restaurant accounts can only use the desktop application.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Driver vs normal user nav
                    if (isDriver) {
                        Log.d("POPLIASOIFUAOISUFONavigation", "Navigating to DriverOrdersActivity");
                        Intent intent = new Intent(MainActivity.this, DriverOrdersActivity.class);
                        intent.putExtra("userJsonObject", response);
                        intent.putExtra("driverId", userJson.get("id").getAsInt());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, WoltRestaurants.class);
                        intent.putExtra("userJsonObject", response);
                        startActivity(intent);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(MainActivity.this,
                        "Network error. Try again.",
                        Toast.LENGTH_SHORT).show());
            }
        });
    }


    public void loadRegWindow(View view) {
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }
}