package com.example.l3android.activities;

import android.app.DatePickerDialog;
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
import java.util.Calendar;
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
    private EditText editLogin;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        bindViews();
        readUserFromIntent();
        editBirthDate.setOnClickListener(v -> showDatePicker());
        editBirthDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker();
            }
        });
        fillFields();

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void bindViews() {
        infoLogin = findViewById(R.id.infoLogin);
        infoId = findViewById(R.id.infoId);
        infoUserType = findViewById(R.id.infoUserType);

        editLogin = findViewById(R.id.editLogin);
        editPassword = findViewById(R.id.editPassword);

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

        User fromIntent = (User) intent.getSerializableExtra("currentUser");
        if (fromIntent != null) {
            currentUser = fromIntent;
            return;
        }

        // Fallback: old behaviour using JSON string
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

        infoLogin.setText("Login: " + nullToEmpty(currentUser.getLogin()));
        infoId.setText("User ID: " + currentUser.getId());

        editLogin.setText(nullToEmpty(currentUser.getLogin()));
        editPassword.setText("");

        editName.setText(nullToEmpty(currentUser.getName()));
        editSurname.setText(nullToEmpty(currentUser.getSurname()));
        editPhone.setText(nullToEmpty(currentUser.getPhoneNumber()));

        infoUserType.setText("Type: User");

        if (currentUser instanceof BasicUser) {
            BasicUser basicUser = (BasicUser) currentUser;
            editAddress.setVisibility(View.VISIBLE);
            editAddress.setText(nullToEmpty(basicUser.getAddress()));
        } else {
            editAddress.setText("");
            editAddress.setVisibility(View.VISIBLE);
        }

        // Driver extras: licence, birth date
        if (currentUser instanceof Driver) {
            Driver driver = (Driver) currentUser;
            infoUserType.setText("Type: Driver");

            editLicence.setVisibility(View.VISIBLE);
            editBirthDate.setVisibility(View.VISIBLE);

            editLicence.setText(nullToEmpty(driver.getLicence()));

            if (driver.getbDate() != null) {
                editBirthDate.setText(driver.getbDate().toString()); // yyyy-MM-dd
            } else {
                editBirthDate.setText("");
            }
        } else if (currentUser.isAdmin()) {
            infoUserType.setText("Type: Admin");
            editLicence.setVisibility(View.GONE);
            editBirthDate.setVisibility(View.GONE);
        } else {
            infoUserType.setText("Type: User");
            editLicence.setVisibility(View.GONE);
            editBirthDate.setVisibility(View.GONE);
        }
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private void saveChanges() {
        if (currentUser == null) {
            return;
        }

        // Updated from UI
        String newLogin = editLogin.getText().toString().trim();
        String newPassword = editPassword.getText().toString().trim();
        String newName = editName.getText().toString().trim();
        String newSurname = editSurname.getText().toString().trim();
        String newPhone = editPhone.getText().toString().trim();
        String newAddress = editAddress.getText().toString().trim();
        String newLicence = editLicence.getText().toString().trim();
        String newBirthDate = editBirthDate.getText().toString().trim();

        JsonObject body = new JsonObject();
        body.addProperty("id", currentUser.getId());

        // Login
        if (!newLogin.isEmpty() && !newLogin.equals(currentUser.getLogin())) {
            currentUser.setLogin(newLogin);
            body.addProperty("login", newLogin);
        }

        // Password
        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
            body.addProperty("password", newPassword);
        }

        // Basic fields
        currentUser.setName(newName);
        currentUser.setSurname(newSurname);
        currentUser.setPhoneNumber(newPhone);

        body.addProperty("name", newName);
        body.addProperty("surname", newSurname);
        body.addProperty("phoneNumber", newPhone);

        // BasicUser fields
        if (currentUser instanceof BasicUser) {
            BasicUser basicUser = (BasicUser) currentUser;
            basicUser.setAddress(newAddress);
            body.addProperty("address", newAddress);
        }

        // Driver fields
        if (currentUser instanceof Driver) {
            Driver driver = (Driver) currentUser;
            driver.setLicence(newLicence);
            body.addProperty("licence", newLicence);

            if (!newBirthDate.isEmpty()) {
                try {
                    java.time.LocalDate.parse(newBirthDate); // expects yyyy-MM-dd
                    driver.setbDate(newBirthDate);
                    body.addProperty("bDate", newBirthDate);
                } catch (Exception e) {
                    Toast.makeText(this,
                            "Bad date format. Use yyyy-MM-dd.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                driver.setbDate(null);
                body.addProperty("bDate", (String) null);
            }
        }

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

    private void showDatePicker() {
        if (!(currentUser instanceof Driver)) {
            return;
        }

        final Calendar c = Calendar.getInstance();
        String existing = editBirthDate.getText().toString().trim();

        if (!existing.isEmpty() && !"null".equalsIgnoreCase(existing)) {
            try {
                LocalDate ld = LocalDate.parse(existing);
                c.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());
            } catch (Exception ignored) {
            }
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String formatted = String.format("%04d-%02d-%02d",
                            year1, monthOfYear + 1, dayOfMonth);
                    editBirthDate.setText(formatted);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
