package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase.getInstance (this);
        setContentView(R.layout.activity_login);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button qrLoginButton = findViewById(R.id.qrLoginButton);

        // Вход по логину и паролю
        loginButton.setOnClickListener(v -> {
            String login = loginEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (login.isEmpty()) {
                loginEditText.setError("Введите логин");
                return;
            }
            if (password.isEmpty()) {
                passwordEditText.setError("Введите пароль");
                return;
            }
            openMainScreen();
        });

        // Вход по QR-коду
        qrLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    QRScanActivity.class
            );
            startActivity(intent);
        });
    }

    private void openMainScreen() {
        Intent intent = new Intent(
                LoginActivity.this,
                MainActivity.class
        );
        startActivity(intent);
        finish();
    }
}