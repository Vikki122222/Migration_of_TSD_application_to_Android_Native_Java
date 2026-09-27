package com.example.myapplication;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_history")
public class ScanHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String productName;
    public double productPrice;
    public int productQuantity;
    public long timestamp; // время сканирования для сортировки
}