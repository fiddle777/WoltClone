package com.example.l3android.activities;

import static com.example.l3android.Utils.Constants.CREATE_BASIC_USER_URL;
import static com.example.l3android.Utils.Constants.VALIDATE_USER_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l3android.R;
import com.example.l3android.Utils.RestOperations;
import com.example.l3android.model.BasicUser;
import com.example.l3android.activities.MainActivity;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
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
    }

    public void createAccount(View view) {

        TextView login = findViewById(R.id.regLoginField);
        TextView psw = findViewById(R.id.regPasswordField);
        TextView name = findViewById(R.id.regNameField);
        TextView surname = findViewById(R.id.regSurnameField);
        TextView phone = findViewById(R.id.regPhoneField);
        //susirenkat likusius duomenis

        //Patikrinti, ar buvo pasirinktas driver ar ne
        String userInfo = "{}";
        if (((CheckBox) findViewById(R.id.regIsDriver)).isChecked()) {
            //Kurt driver
        } else {

            BasicUser basicUser = new BasicUser(login.getText().toString(), psw.getText().toString(), name.getText().toString(), surname.getText().toString(), phone.getText().toString(), "addressHardcode");
            Gson gson = new Gson();
            userInfo = gson.toJson(basicUser, BasicUser.class);
            System.out.println(userInfo);
        }


        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String finalUserInfo = userInfo;
        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(CREATE_BASIC_USER_URL, finalUserInfo);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty()) {
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            } catch (IOException e) {
                //Toast reikes
            }

        });

    }
}