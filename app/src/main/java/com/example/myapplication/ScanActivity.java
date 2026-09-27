package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;

    private PreviewView cameraPreview;
    private TextView scanStatus;

    private BarcodeScanner barcodeScanner;
    private ExecutorService cameraExecutor;
    private Handler handler;

    private boolean scanFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        cameraPreview = findViewById(R.id.cameraPreview);
        scanStatus = findViewById(R.id.scanStatus);
        TextView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
        handler = new Handler(Looper.getMainLooper());
        cameraExecutor = Executors.newSingleThreadExecutor();
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_EAN_13,
                                Barcode.FORMAT_EAN_8,
                                Barcode.FORMAT_UPC_A,
                                Barcode.FORMAT_UPC_E,
                                Barcode.FORMAT_CODE_128,
                                Barcode.FORMAT_CODE_39,
                                Barcode.FORMAT_CODE_93,
                                Barcode.FORMAT_CODABAR,
                                Barcode.FORMAT_ITF
                        )
                        .build();
        barcodeScanner = BarcodeScanning.getClient(options);
        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE
            );
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();
                Preview preview =
                        new Preview.Builder().build();
                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA;
                preview.setSurfaceProvider(
                        cameraPreview.getSurfaceProvider()
                );
                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                )
                                .build();
                imageAnalysis.setAnalyzer(
                        cameraExecutor,
                        this::analyzeImage
                );
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );
                scanStatus.setText("Наведите камеру на штрихкод или QR");
            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "Не удалось открыть камеру",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void analyzeImage(ImageProxy imageProxy) {
        if (scanFinished) {
            imageProxy.close();
            return;
        }
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }
        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );
        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (scanFinished) {
                        return;
                    }
                    for (Barcode barcode : barcodes) {
                        String barcodeValue =
                                barcode.getRawValue();
                        if (barcodeValue != null &&
                                !barcodeValue.isEmpty()) {
                            scanFinished = true;
                            runOnUiThread(() -> {
                                scanStatus.setText(
                                        "Код успешно распознан"
                                );
                                Toast.makeText(
                                        ScanActivity.this,
                                        "Код: " + barcodeValue,
                                        Toast.LENGTH_SHORT
                                ).show();
                                handler.postDelayed(() -> {
                                    openProductScreen(
                                            barcodeValue
                                    );
                                }, 1000);
                            });
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Ошибки отдельных кадров игнорируем
                })
                .addOnCompleteListener(task ->
                        imageProxy.close()
                );
    }

    private void openProductScreen(String barcodeValue) {
        Intent intent = new Intent(
                ScanActivity.this,
                ProductActivity.class
        );
        intent.putExtra(
                "barcode",
                barcodeValue
        );
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(
                        this,
                        "Для сканирования нужен доступ к камере",
                        Toast.LENGTH_LONG
                ).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}