package com.example.myapplication;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.Executors;

@Database(entities = {Product.class, ScanHistory.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    public abstract ProductDao productDao();
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "tsd_database")
                    .addCallback(roomCallback)
                    .build();
        }
        return INSTANCE;
    }
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                ProductDao dao = INSTANCE.productDao();
                // Товар 1
                Product p1 = new Product();
                p1.barcode = "001";
                p1.name = "Помада матовая стойкая";
                p1.price = 1250.0;
                p1.quantity = 88;
                dao.insertProduct(p1);
                // Товар 2
                Product p2 = new Product();
                p2.barcode = "002";
                p2.name = "Тушь для ресниц объемная";
                p2.price = 890.0;
                p2.quantity = 45;
                dao.insertProduct(p2);
            });
        }
    };
}