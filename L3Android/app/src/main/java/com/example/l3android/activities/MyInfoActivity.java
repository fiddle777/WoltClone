package com.example.l3android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.l3android.R;
import com.example.l3android.Utils.Constants;
import com.example.l3android.Utils.RestOperations;
import com.example.l3android.model.BasicUser;
import com.example.l3android.model.Driver;
import com.example.l3android.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyInfoActivity extends AppCompatActivity {

    private User currentUser;

    private EditText editName;
    private EditText editSurname;
    private EditText editPhone;
    private EditText editAddress;
    private EditText editLicence;
    private EditText editBirthDate;
    private TextView infoLogin;
    private TextView infoId;
    private TextView infoUserType;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        bindViews();
        readUserFromIntent();
        fillFields();

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void bindViews() {
        infoLogin = findViewById(R.id.infoLogin);
        infoId = findViewById(R.id.infoId);
        infoUserType = findViewById(R.id.infoUserType);

        editName = findViewById(R.id.editName);
        editSurname = findViewById(R.id.editSurname);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        editLicence = findViewById(R.id.editLicence);
        editBirthDate = findViewById(R.id.editBirthDate);

        btnSave = findViewById(R.id.btnSaveUser);
    }

    private void readUserFromIntent() {
        Intent intent = getIntent();
        String userJson = intent.getStringExtra("userJson");

        if (userJson == null || userJson.isEmpty()) {
            return;
        }

        Gson gson = new Gson();
        JsonObject json = new JsonParser().parse(userJson).getAsJsonObject();

        String userType = "";
        if (json.has("userType") && !json.get("userType").isJsonNull()) {
            userType = json.get("userType").getAsString();
        }

        if (userType.contains("Driver")) {
            currentUser = gson.fromJson(json, Driver.class);
        } else if (userType.contains("BasicUser")) {
            currentUser = gson.fromJson(json, BasicUser.class);
        } else {
            currentUser = gson.fromJson(json, User.class);
        }
    }


    private void fillFields() {
        if (currentUser == null) {
            return;
        }

        infoLogin.setText("Login: " + currentUser.getLogin());
        infoId.setText("User ID: " + currentUser.getId());

        editName.setText(currentUser.getName());
        editSurname.setText(currentUser.getSurname());
        editPhone.setText(currentUser.getPhoneNumber());

        // Default type text
        infoUserType.setText("Type: User");

        // Address (BasicUser / Driver)
        if (currentUser instanceof BasicUser) {
            BasicUser basicUser = (BasicUser) currentUser;
            editAddress.setText(basicUser.getAddress() != null ? basicUser.getAddress() : "");
            editAddress.setVisibility(View.VISIBLE);
        } else {
            editAddress.setVisibility(View.GONE);
        }

        // Driver extras
        if (currentUser instanceof Driver) {
            Driver driver = (Driver) currentUser;
            infoUserType.setText("Type: Driver");

            editLicence.setVisibility(View.VISIBLE);
            editBirthDate.setVisibility(View.VISIBLE);

            if (driver.getLicence() != null) {
                editLicence.setText(driver.getLicence());
            }
            if (driver.getbDate() != null) {
                editBirthDate.setText(driver.getbDate().toString()); // yyyy-MM-dd
            }
        } else if (currentUser.isAdmin()) {
            infoUserType.setText("Type: Admin");
            editLicence.setVisibility(View.GONE);
            editBirthDate.setVisibility(View.GONE);
        } else {
            // Normal BasicUser
            infoUserType.setText("Type: User");
            editLicence.setVisibility(View.GONE);
            editBirthDate.setVisibility(View.GONE);
        }
    }

    private void saveChanges() {
        if (currentUser == null) {
            return;
        }

        // Update local object from UI
        String newName = editName.getText().toString().trim();
        String newSurname = editSurname.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();
        String newAddress = editAddress.getText().toString().trim();
        String newLicence = editLicence.getText().toString().trim();
        String newBirthDate = editBirthDate.getText().toString().trim();

        currentUser.setName(newName);
        currentUser.setSurname(newSurname);
        currentUser.setPhoneNumber(newPhone);

        JsonObject body = new JsonObject();
        body.addProperty("id", currentUser.getId());
        body.addProperty("name", newName);
        body.addProperty("surname", newSurname);
        body.addProperty("phoneNumber", newPhone);

        if (currentUser instanceof BasicUser) {
            BasicUser basicUser = (BasicUser) currentUser;
            basicUser.setAddress(newAddress);
            body.addProperty("address", newAddress);
        }

        if (currentUser instanceof Driver) {
            Driver driver = (Driver) currentUser;
            driver.setLicence(newLicence);
            body.addProperty("licence", newLicence);

            if (!newBirthDate.isEmpty()) {
                try {
                    // Validate format
                    LocalDate parsed = LocalDate.parse(newBirthDate); // yyyy-MM-dd
                    driver.setbDate(parsed);
                    body.addProperty("bDate", newBirthDate);
                } catch (Exception e) {
                    Toast.makeText(this,
                            "Bad date format. Use yyyy-MM-dd.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        // Send to backend
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        String jsonToSend = body.toString();

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(Constants.UPDATE_USER_INFO, jsonToSend);

                handler.post(() -> {
                    if ("Error".equals(response) || response == null || response.isEmpty()) {
                        Toast.makeText(MyInfoActivity.this,
                                "Failed to save changes",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyInfoActivity.this,
                                "Changes saved",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> Toast.makeText(MyInfoActivity.this,
                        "Network error while saving",
                        Toast.LENGTH_SHORT).show());
            }
        });
    }
}
