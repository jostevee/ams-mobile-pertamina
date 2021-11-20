package com.example.basicapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.textfield.TextInputLayout;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText eUsername;
    private EditText ePassword;
    private Button bLogin;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    private VideoView videoview;
    private TextInputLayout eUsernameLayout;
    private TextInputLayout ePasswordLayout;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    //private ConstraintLayout activityMain;
    //private TextView tvUsername;
    //private TextView tvPassword;
    //private androidx.appcompat.widget.AppCompatCheckBox showPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eUsername = findViewById(R.id.etUsername);
        ePassword = findViewById(R.id.etPassword);
        eUsernameLayout = findViewById(R.id.etUsernameLayout);
        ePasswordLayout = findViewById(R.id.etPasswordLayout);
        bLogin = findViewById(R.id.bLogin);
        btnLogin = findViewById(R.id.btnLogin);

        // Start animation
        startAnimations();

        // Progress Dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Processing your data");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        /*
        activityMain = findViewById(R.id.activity_main);
        tvUsername = findViewById(R.id.textViewUser);
        tvPassword = findViewById(R.id.textViewPass);
        showPass = findViewById(R.id.showPassword);

        videoview = findViewById(R.id.videoPlayback);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.videoplayback);
        videoview.setVideoURI(uri);
        videoview.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
            float screenRatio = videoview.getWidth() / (float) videoview.getHeight();
            float scaleX = videoRatio / screenRatio;
            if(scaleX >= 1f){
                videoview.setScaleX(scaleX);
            } else {
                videoview.setScaleY(1f / scaleX);
            }
        });
        videoview.start();

        //Set onClickListener, untuk menangani kejadian saat Checkbox diklik
        showPass.setOnClickListener(view -> {
            if(showPass.isChecked()){
                //Saat Checkbox dalam keadaan Checked, maka password akan di tampilkan
                ePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                //Jika tidak, maka password akan di sembuyikan
                ePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        */

        // SHOW PASSWORD
        ePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        // HIDE PASSWORD
        ePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        
        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        if (sharedPreferences != null) {
            String savedUsername = sharedPreferences.getString("Username", "");
            String savedPassword = sharedPreferences.getString("Password", "");
            /*
            try {
                //Intent intent = new Intent(MainActivity.this, MainMenu.class);
                Intent intent = new Intent(MainActivity.this, HomeNavigation.class);
                startActivity(intent);
            } finally {
                MainActivity.this.finish();
            }
            */
        }

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               
                String inputUsername = eUsername.getText().toString();
                String inputPassword = ePassword.getText().toString();

                if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill the blank field !", Toast.LENGTH_SHORT).show();
                } else {
                    new SoapCall().execute();
                }
            }
        });
    }

    private void startAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
        anim.reset();

        // Put Here RelativeLayout to original settings //
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();

        /*
        TextView iv = (TextView) findViewById(R.id.textView);
        iv.clearAnimation();
        iv.startAnimation(anim);
        */

        eUsernameLayout.clearAnimation();
        eUsernameLayout.startAnimation(anim);
        ePasswordLayout.clearAnimation();
        ePasswordLayout.startAnimation(anim);
        bLogin.clearAnimation();
        bLogin.startAnimation(anim);

        //tvUsername.clearAnimation();
        //tvUsername.startAnimation(anim);
        //tvPassword.clearAnimation();
        //tvPassword.startAnimation(anim);
        //showPass.clearAnimation();
        //showPass.startAnimation(anim);
    }

    private class SoapCall extends AsyncTask<String, Object, String>{

        public static final String NAME_SPACE ="SSO";
        public static final String URL ="http://203.77.249.186:8091/SSOWS.asmx";
        public static final String SOAP_ACTION = "SSO/ValidateUser";
        public static final String METHOD_NAME = "ValidateUser";
        public int TimeOut = 30000;
        SoapPrimitive response;
        //ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            /*
            progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            */
        }

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);
            request.addProperty("username", eUsername.getText().toString());
            request.addProperty("password", ePassword.getText().toString());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transportSE = new HttpTransportSE(URL, TimeOut);

            try{
                transportSE.call(SOAP_ACTION, envelope);
                response = (SoapPrimitive) envelope.getResponse();
                return response.toString();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("Error", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null && s.equalsIgnoreCase("true")){
                //store the credentials
                sharedPreferencesEditor.putString("Username", eUsername.getText().toString());
                //commit the data
                sharedPreferencesEditor.apply();

                Toast.makeText(MainActivity.this, "Login successfully !", Toast.LENGTH_SHORT).show();
                try {
                    //Intent intent = new Intent(MainActivity.this, MainMenu.class);
                    Intent intent = new Intent(MainActivity.this, HomeNavigation.class);
                    startActivity(intent);
                } finally {
                    MainActivity.this.finish();
                }
            }
            else{
                Toast.makeText(MainActivity.this, "Incorrect credentials !", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            //progressBar.setVisibility(View.GONE);
        }
    }

     /*
    @Override
    protected void onResume() {
        super.onResume();
        // to restart the video after coming from other activity like Sing up
        videoview.start();
    }
     */
}