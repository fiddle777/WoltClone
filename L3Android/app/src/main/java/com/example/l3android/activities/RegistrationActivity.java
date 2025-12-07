package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.CREATE_BASIC_USER_URL;
import static com.example.l3android.Utils.Constants.CREATE_DRIVER_URL;
import static com.example.l3android.Utils.Constants.VALIDATE_USER_URL;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l3android.R;
import com.example.l3android.Utils.RestOperations;
import com.example.l3android.model.BasicUser;
import com.example.l3android.model.Driver;
import com.example.l3android.model.VehicleType;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText licenceField = findViewById(R.id.regLicenceField);
        EditText birthDateField = findViewById(R.id.regBirthDateField);
        birthDateField.setOnClickListener(v -> showBirthDatePicker(birthDateField));
        Spinner vehicleSpinner = findViewById(R.id.regVehicleTypeSpinner);
        CheckBox isDriverCheckBox = findViewById(R.id.regIsDriver);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"SCOOTER", "CAR", "MOTORCYCLE"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleSpinner.setAdapter(adapter);

        setDriverFieldsEnabled(false, licenceField, birthDateField, vehicleSpinner);
        isDriverCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDriverFieldsEnabled(isChecked, licenceField, birthDateField, vehicleSpinner);
            if (!isChecked) {
                licenceField.setText("");
                birthDateField.setText("");
                vehicleSpinner.setSelection(0);
            }
        });
    }
    private void showBirthDatePicker(EditText birthDateField) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    String formatted = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
                    birthDateField.setText(formatted);
                },
                year,
                month,
                day
        );

        dialog.show();
    }
    private void setDriverFieldsEnabled(
            boolean enabled,
            EditText licenceField,
            EditText birthDateField,
            Spinner vehicleSpinner
    ) {
        licenceField.setEnabled(enabled);
        birthDateField.setEnabled(enabled);
        vehicleSpinner.setEnabled(enabled);

        // Visual feedback (greyed out when disabled)
        float alpha = enabled ? 1.0f : 0.5f;
        licenceField.setAlpha(alpha);
        birthDateField.setAlpha(alpha);
        vehicleSpinner.setAlpha(alpha);
    }



    public void createAccount(View view) {

        EditText login = findViewById(R.id.regLoginField);
        EditText psw = findViewById(R.id.regPasswordField);
        EditText name = findViewById(R.id.regNameField);
        EditText surname = findViewById(R.id.regSurnameField);
        EditText phone = findViewById(R.id.regPhoneField);
        EditText address = findViewById(R.id.regAddressField);
        EditText licence = findViewById(R.id.regLicenceField);
        EditText birthDate = findViewById(R.id.regBirthDateField);
        Spinner vehicleSpinner = findViewById(R.id.regVehicleTypeSpinner);
        CheckBox isDriverCheckBox = findViewById(R.id.regIsDriver);

        String loginStr = login.getText().toString().trim();
        String pswStr = psw.getText().toString().trim();
        String nameStr = name.getText().toString().trim();
        String surnameStr = surname.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String addressStr = address.getText().toString().trim();
        String licenceStr = licence.getText().toString().trim();
        String birthStr = birthDate.getText().toString().trim();
        String vehicleStr = (String) vehicleSpinner.getSelectedItem();

        // Common required fields
        if (loginStr.isEmpty()) {
            login.setError("Username is required");
            login.requestFocus();
            return;
        }
        if (pswStr.isEmpty()) {
            psw.setError("Password is required");
            psw.requestFocus();
            return;
        }
        if (nameStr.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }
        if (surnameStr.isEmpty()) {
            surname.setError("Surname is required");
            surname.requestFocus();
            return;
        }
        if (phoneStr.isEmpty()) {
            phone.setError("Phone is required");
            phone.requestFocus();
            return;
        }
        if (addressStr.isEmpty()) {
            address.setError("Address is required");
            address.requestFocus();
            return;
        }

        String userInfo;
        String targetUrl;
        Gson gson = new Gson();

        if (isDriverCheckBox.isChecked()) {
            // driver specific required fields
            if (licenceStr.isEmpty()) {
                licence.setError("Licence number is required for drivers");
                licence.requestFocus();
                return;
            }
            if (birthStr.isEmpty()) {
                birthDate.setError("Birth date is required for drivers");
                birthDate.requestFocus();
                return;
            }

            java.time.LocalDate bDate;
            try {
                bDate = java.time.LocalDate.parse(birthStr); //  yyyy-MM-dd
            } catch (Exception e) {
                birthDate.setError("Use format yyyy-MM-dd");
                birthDate.requestFocus();
                return;
            }

            Driver driver = new Driver(
                    loginStr,
                    pswStr,
                    nameStr,
                    surnameStr,
                    phoneStr,
                    addressStr,
                    licenceStr,
                    birthStr,
                    VehicleType.valueOf(vehicleStr)
            );

            userInfo = gson.toJson(driver, Driver.class);
            targetUrl = CREATE_DRIVER_URL;

        } else {
            // client reg
            BasicUser basicUser = new BasicUser(
                    loginStr,
                    pswStr,
                    nameStr,
                    surnameStr,
                    phoneStr,
                    addressStr
            );

            userInfo = gson.toJson(basicUser, BasicUser.class);
            targetUrl = CREATE_BASIC_USER_URL;
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        String finalUserInfo = userInfo;
        String finalTargetUrl = targetUrl;

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(finalTargetUrl, finalUserInfo);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}