package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView historyName1, historyDesc1;
    private TextView historyName2, historyDesc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем элементы истории
        historyName1 = findViewById(R.id.historyName1);
        historyDesc1 = findViewById(R.id.historyDesc1);
        historyName2 = findViewById(R.id.historyName2);
        historyDesc2 = findViewById(R.id.historyDesc2);

        // Инициализация базы данных
        AppDatabase.getInstance(this);

        // Кнопка сканирования
        LinearLayout scanButton = findViewById(R.id.scanButton);
        if (scanButton != null) {
            scanButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            });
        }

        // Ручной ввод товара
        TextView manualInputButton = findViewById(R.id.manualInput);
        if (manualInputButton != null) {
            manualInputButton.setOnClickListener(v -> {
                EditText inputField = new EditText(this);
                inputField.setHint("Введите штрихкод:");
                inputField.setPadding(50, 40, 50, 40);
                new AlertDialog.Builder(this)
                        .setTitle("Ручной ввод товара")
                        .setMessage("Введите штрихкод:")
                        .setView(inputField)
                        .setPositiveButton("Найти", (dialog, which) -> {
                            String barcode = inputField.getText().toString().trim();
                            if (barcode.isEmpty()) {
                                Toast.makeText(this, "Поле не должно быть пустым", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Executors.newSingleThreadExecutor().execute(() -> {
                                Product product = AppDatabase.getInstance(this)
                                        .productDao()
                                        .getProductByBarcode(barcode);

                                // Сохранение товара в историю, если товар нашелся
                                if (product != null) {
                                    ScanHistory historyItem = new ScanHistory();
                                    historyItem.productName = product.name;
                                    historyItem.productPrice = product.price;
                                    historyItem.productQuantity = product.quantity;
                                    historyItem.timestamp = System.currentTimeMillis();
                                    AppDatabase.getInstance(this).productDao().insertHistory(historyItem);
                                }

                                runOnUiThread(() -> {
                                    if (product != null) {
                                        Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                                        intent.putExtra("PRODUCT_NAME", product.name);
                                        intent.putExtra("PRODUCT_PRICE", product.price);
                                        intent.putExtra("PRODUCT_QUANTITY", product.quantity);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(this, "Товар с таким штрихкодом не найден", Toast.LENGTH_LONG).show();
                                    }
                                });
                            });
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            });
        }

        // Кнопка профиля
        TextView profileButton = findViewById(R.id.profileButton);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ScanHistory> historyList = AppDatabase.getInstance(this)
                    .productDao()
                    .getAllHistory();

            runOnUiThread(() -> {
                if (historyList != null && !historyList.isEmpty()) {
                    // Первая запись
                    if (historyList.size() > 0) {
                        ScanHistory item1 = historyList.get(0);
                        if (historyName1 != null) historyName1.setText(item1.productName);
                        if (historyDesc1 != null) historyDesc1.setText(String.format("Цена: %.2f | Кол-во: %d шт", item1.productPrice, item1.productQuantity));
                    } else {
                        if (historyName1 != null) historyName1.setText("");
                        if (historyDesc1 != null) historyDesc1.setText("");
                    }

                    // Вторая запись
                    if (historyList.size() > 1) {
                        ScanHistory item2 = historyList.get(1);
                        if (historyName2 != null) historyName2.setText(item2.productName);
                        if (historyDesc2 != null) historyDesc2.setText(String.format("Цена: %.2f | Кол-во: %d шт", item2.productPrice, item2.productQuantity));
                    } else {
                        if (historyName2 != null) historyName2.setText("");
                        if (historyDesc2 != null) historyDesc2.setText("");
                    }
                } else {
                    if (historyName1 != null) historyName1.setText("");
                    if (historyDesc1 != null) historyDesc1.setText("");
                    if (historyName2 != null) historyName2.setText("");
                    if (historyDesc2 != null) historyDesc2.setText("");
                }
            });
        });
    }
}