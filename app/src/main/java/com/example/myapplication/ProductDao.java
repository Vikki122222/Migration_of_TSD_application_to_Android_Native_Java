package com.example.myapplication;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    Product getProductByBarcode(String barcode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product product);

    @Insert
    void insertHistory(ScanHistory history);

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    java.util.List<ScanHistory> getAllHistory();
}