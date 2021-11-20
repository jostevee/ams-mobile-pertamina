package com.example.basicapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllAssetActivity extends MainMenu {
    private RecyclerView recyclerView;
    private final List<AssetModel> modelList = new ArrayList<>();
    private RequestQueue mQueue;
    private Button btnGen;
    private Spinner spinUnit, spinRuangan;
    ArrayList<String> unitList = new ArrayList<>();
    ArrayList<String> unitListValue = new ArrayList<>();
    ArrayList<String> ruanganList = new ArrayList<>();
    ArrayList<String> ruanganListValue = new ArrayList<>();
    ArrayAdapter<String> unitAdapter;
    ArrayAdapter<String> ruanganAdapter;
    SharedPreferences sharedPreferences;
    String savedUser;
    ProgressBar progressBar;
    TextView profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_asset);
        spinUnit = findViewById(R.id.spinnerSPBU);
        spinRuangan = findViewById(R.id.spinnerRuangan);
        recyclerView = findViewById(R.id.recyclerAsset);
        btnGen = findViewById(R.id.btnGenerate);
        //progressBar = findViewById(R.id.progressBar4);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        if (sharedPreferences != null) {
            savedUser = sharedPreferences.getString("Username", "");
            unitRequest(savedUser);
        }

        btnGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                modelList.clear();
                int getValueRuangan = spinRuangan.getSelectedItemPosition();
                String selectedRuangan = String.valueOf(ruanganListValue.get(getValueRuangan));
                int getValueUnit = spinUnit.getSelectedItemPosition();
                String selectedUnit = String.valueOf(unitListValue.get(getValueUnit));
                jsonrequest(selectedUnit, selectedRuangan);
            }
        });

        spinUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(adapterView.getId() == R.id.spinnerSPBU){
                    ruanganList.clear();
                    String selectedUnit = String.valueOf(unitListValue.get(position));
                    if (!Objects.equals(selectedUnit, "0")){
                        ruanganRequest(selectedUnit);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void unitRequest(String userid) {
        String jsonUrl = "http://203.77.249.186:8031/Asset/GetUserUnit?userid="+userid;
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                unitList.add("Select Unit");
                unitListValue.add("0");

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String KodeUnit = jsonObject.getString("KodeUnit");
                        String NamaUnit = jsonObject.getString("NamaUnit");

                        unitList.add(NamaUnit);
                        unitListValue.add(KodeUnit);

                        unitAdapter = new ArrayAdapter<>(AllAssetActivity.this,
                                android.R.layout.simple_spinner_item, unitList);
                        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinUnit.setAdapter(unitAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AllAssetActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                //error.printStackTrace();
            }
        });

        mQueue = Volley.newRequestQueue(AllAssetActivity.this);
        mQueue.add(request);
    }

    private void ruanganRequest(String unitid){
        String jsonUrl ="http://203.77.249.186:8031/Asset/GetKodeRuangan?unitid="+unitid;
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                ruanganList.add("ALL");
                ruanganListValue.add("0");

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String KodeRuangan = jsonObject.getString("KodeRuangan");
                        String NamaRuangan = jsonObject.getString("NamaRuangan");

                        ruanganList.add(NamaRuangan);
                        ruanganListValue.add(KodeRuangan);

                        ruanganAdapter = new ArrayAdapter<>(AllAssetActivity.this,
                                android.R.layout.simple_spinner_item, ruanganList);
                        ruanganAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinRuangan.setAdapter(ruanganAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue = Volley.newRequestQueue(AllAssetActivity.this);
        mQueue.add(request);
    }

    private void jsonrequest(String kodeunit, String koderuangan){
        String jsonUrl ="http://203.77.249.186:8031/Asset/ListAssetbyUserLogin?KodeUnit="+kodeunit+"&KodeRuangan="+koderuangan;
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        AssetModel model = new AssetModel();
                        model.setKodeRuangan(jsonObject.getString("KodeRuangan"));
                        model.setAssetid(String.valueOf(jsonObject.getInt("AssetId")));
                        model.setBarcode(jsonObject.getString("KodeBarcode"));
                        model.setAssetname(jsonObject.getString("NamaAsset"));
                        model.setTahunperolehan(String.valueOf(jsonObject.getInt("TahunPerolehan")));
                        modelList.add(model);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setAssetAdapter(modelList);
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue = Volley.newRequestQueue(AllAssetActivity.this);
        mQueue.add(request);
    }

    public void setAssetAdapter (List<AssetModel> lst) {
        AssetAdapter myAdapter = new AssetAdapter(this,lst) ;
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
    }
}