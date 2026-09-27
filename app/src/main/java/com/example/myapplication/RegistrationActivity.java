package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText loginEditText;
    private EditText passwordEditText;
    private EditText passwordRepeatEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        nameEditText = findViewById(R.id.nameEditText);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordRepeatEditText = findViewById(R.id.passwordRepeatEditText);

        TextView registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> registerUser());

        TextView loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegistrationActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {

        String name = nameEditText.getText().toString().trim();
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String passwordRepeat = passwordRepeatEditText.getText().toString();

        if (name.isEmpty()) {
            nameEditText.setError("Введите имя");
            nameEditText.requestFocus();
            return;
        }

        if (login.isEmpty()) {
            loginEditText.setError("Введите логин");
            loginEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Введите пароль");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Пароль должен содержать минимум 6 символов");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(passwordRepeat)) {
            passwordRepeatEditText.setError("Пароли не совпадают");
            passwordRepeatEditText.requestFocus();
            return;
        }

        Toast.makeText(
                RegistrationActivity.this,
                "Регистрация выполнена",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(
                RegistrationActivity.this,
                MainActivity.class
        );

        startActivity(intent);
        finish();
    }
}