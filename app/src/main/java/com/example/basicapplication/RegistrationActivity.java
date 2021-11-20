package com.example.basicapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {
    private EditText eRegUser;
    private EditText eRegPass;
    private Button bReg;

    public static Credentials credentials;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        eRegUser = findViewById(R.id.etRegUsername);
        eRegPass = findViewById(R.id.etRegPassword);
        bReg = findViewById(R.id.bRegister);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB",MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        bReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regUsername = eRegUser.getText().toString();
                String regPassword = eRegPass.getText().toString();

                if (validate(regUsername, regPassword)){
                    credentials = new Credentials(regUsername, regPassword, "");

                    //store the credentials
                    sharedPreferencesEditor.putString("Username",regUsername);
                    sharedPreferencesEditor.putString("Password", regPassword);

                    //commit the data
                    sharedPreferencesEditor.apply();

                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    Toast.makeText(RegistrationActivity.this,"Registration Successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean validate(String username, String password){
        if (username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all the information !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}