package com.example.basicapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.example.basicapplication.BarcodeScannerFragment.CAMERA_PERMISSION_CODE;

public class BarcodeScannerShowActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private PageViewModel pageViewModel;
    private Button openBarcode;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner_show);

        // Get the button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            super.onBackPressed();
        });

        // Set up the Code Scanner object
        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        mCodeScanner.setDecodeCallback(result -> this.runOnUiThread(() -> {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);

            Bundle bundle = new Bundle();
            bundle.putString("bundleKey", result.getText());

            // set Fragmentclass Arguments
            AssetSOFragment fragobj = new AssetSOFragment();
            fragobj.setArguments(bundle);

            super.onBackPressed();
        }));
        scannerView.setOnClickListener(view1 -> mCodeScanner.startPreview());
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission},
                    requestCode);
        } else {
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show();
            mCodeScanner.startPreview();
        }
    }

    public CodeScanner getCodeScanner(){
        return mCodeScanner;
    }

    public CodeScannerView getScannerView(){
        return scannerView;
    }
}