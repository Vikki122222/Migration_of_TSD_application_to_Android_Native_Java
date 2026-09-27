package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        String name = getIntent().getStringExtra("PRODUCT_NAME");
        double price = getIntent().getDoubleExtra("PRODUCT_PRICE", 0.0);
        int quantity = getIntent().getIntExtra("PRODUCT_QUANTITY", 0);

        // Текстовые поля по ID
        TextView nameTextView = findViewById(R.id.productName);
        TextView priceTextView = findViewById(R.id.productPrice);
        TextView quantityTextView = findViewById(R.id.productQuantity);

        // Заполнение полей данными из базы
        if (nameTextView != null && name != null) {
            nameTextView.setText(name);
        }
        if (priceTextView != null) {
            priceTextView.setText(String.format("%.2f", price));
        }
        if (quantityTextView != null) {
            quantityTextView.setText(quantity + " шт");
        }

        // Кнопка "Продолжить сканирование"
        TextView scanAgainButton = findViewById(R.id.scanAgainButton);
        if (scanAgainButton != null) {
            scanAgainButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProductActivity.this, ScanActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // Кнопка "Сброс" (ведет на главную)
        TextView errorButton = findViewById(R.id.errorButton);
        if (errorButton != null) {
            errorButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // Кнопка "Главная" внизу
        TextView homeButton = findViewById(R.id.homeButton);
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // Кнопка "Профиль"
        TextView profileButton = findViewById(R.id.profileButton);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
            });
        }
    }
}