package com.example.basicapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {
    DatabaseHelper myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        myDB = new DatabaseHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commonmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuSO){
            startActivity(new Intent(MainMenu.this, AssetSOActivity.class));
        }
        if (id == R.id.menuConfirm){
            startActivity(new Intent(MainMenu.this, ConfirmationSOActivity.class));
        }
        if (id == R.id.menuAsset){
            startActivity(new Intent(MainMenu.this, AllAssetActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}