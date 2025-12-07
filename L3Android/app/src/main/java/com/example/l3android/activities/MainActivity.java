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
import com.example.l3android.model.BasicUser;
import com.example.l3android.model.Driver;
import com.example.l3android.model.User;
import com.example.l3android.model.VehicleType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDate;
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
                    // Network error
                    if (response == null || response.isEmpty() || "Error".equals(response)) {
                        Toast.makeText(MainActivity.this,
                                "Server error. Try again.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JsonObject userJson = new JsonParser().parse(response).getAsJsonObject();
                    String userType = userJson.get("userType").getAsString();
                    User currentUser;

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

                    boolean isDriver = userType.equals("com.example.l3web.model.Driver");
                    boolean isBasicUser = userType.equals("com.example.l3web.model.BasicUser");
                    boolean isRestaurant = userType.equals("com.example.l3web.model.Restaurant");
                    Log.d("ACHTUNGASOHAOSIHGAUserTypeCheck", "isDriver=" + isDriver
                            + ", isBasicUser=" + isBasicUser
                            + ", isRestaurant=" + isRestaurant);

                    // building curretn user here

                    int id = userJson.get("id").getAsInt();

                    String loginStr   = userJson.has("login")   && !userJson.get("login").isJsonNull()
                            ? userJson.get("login").getAsString()
                            : null;
                    String nameStr    = userJson.has("name")    && !userJson.get("name").isJsonNull()
                            ? userJson.get("name").getAsString()
                            : null;
                    String surnameStr = userJson.has("surname") && !userJson.get("surname").isJsonNull()
                            ? userJson.get("surname").getAsString()
                            : null;
                    String phoneStr   = userJson.has("phoneNumber") && !userJson.get("phoneNumber").isJsonNull()
                            ? userJson.get("phoneNumber").getAsString()
                            : null;

                    String addressStr = userJson.has("address") && !userJson.get("address").isJsonNull()
                            ? userJson.get("address").getAsString()
                            : null;
                    String licenceStr = userJson.has("licence") && !userJson.get("licence").isJsonNull()
                            ? userJson.get("licence").getAsString()
                            : null;
                    String bDateStr = userJson.has("bDate") && !userJson.get("bDate").isJsonNull()
                            ? userJson.get("bDate").getAsString()
                            : null;
                    String vehicleTypeStr = userJson.has("vehicleType") && !userJson.get("vehicleType").isJsonNull()
                            ? userJson.get("vehicleType").getAsString()
                            : null;

                    if (isDriver) {
                        Driver d = new Driver();
                        d.setId(id);
                        d.setLogin(loginStr);
                        d.setName(nameStr);
                        d.setSurname(surnameStr);
                        d.setPhoneNumber(phoneStr);
                        d.setAddress(addressStr);
                        d.setLicence(licenceStr);

                        // oh the sweet release of death
                        if (bDateStr != null && !bDateStr.isEmpty()) {
                            try {
                                d.setbDate(bDateStr);
                            } catch (Exception e) {
                                Log.e("ACHTUNG_BDATE_PARSE", "Failed to parse bDate: " + bDateStr, e);
                            }
                        }

                        if (vehicleTypeStr != null && !vehicleTypeStr.isEmpty()) {
                            try {
                                d.setVehicleType(VehicleType.valueOf(vehicleTypeStr));
                            } catch (IllegalArgumentException e) {
                                Log.e("ACHTUNG_VEHICLE_PARSE", "Unknown vehicle type: " + vehicleTypeStr, e);
                            }
                        }

                        currentUser = d;

                    } else if (isBasicUser) {
                        BasicUser bu = new BasicUser();
                        bu.setId(id);
                        bu.setLogin(loginStr);
                        bu.setName(nameStr);
                        bu.setSurname(surnameStr);
                        bu.setPhoneNumber(phoneStr);
                        bu.setAddress(addressStr);
                        currentUser = bu;

                    } else {
                        User u = new User();
                        u.setId(id);
                        u.setLogin(loginStr);
                        u.setName(nameStr);
                        u.setSurname(surnameStr);
                        u.setPhoneNumber(phoneStr);
                        currentUser = u;
                    }

                    // block restaurants
                    if (isRestaurant) {
                        Toast.makeText(MainActivity.this,
                                "Restaurant accounts can only use the desktop application.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // driver vs normal user nav
                    if (isDriver) {
                        Log.d("POPLIASOIFUAOISUFONavigation", "Navigating to DriverOrdersActivity");
                        Intent intent = new Intent(MainActivity.this, DriverOrdersActivity.class);
                        intent.putExtra("userJsonObject", response);
                        intent.putExtra("driverId", userJson.get("id").getAsInt());
                        intent.putExtra("currentUser", currentUser); // <--- important
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, WoltRestaurants.class);
                        intent.putExtra("userJsonObject", response);
                        intent.putExtra("currentUser", currentUser); // <--- important
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