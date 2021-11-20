package com.example.basicapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ConfirmationSOActivity extends MainMenu {
    public RecyclerView recyclerView;
    private Spinner spinUnit, spinRuang;
    private final ArrayList<String> unitList = new ArrayList<>();
    private final ArrayList<String> unitListValue = new ArrayList<>();
    private final ArrayList<String> ruanganList = new ArrayList<>();
    private final ArrayList<String> ruanganListValue = new ArrayList<>();
    private ArrayAdapter<String> unitAdapter;
    private ArrayAdapter<String> ruanganAdapter;
    private RequestQueue mQueue;
    private Button btnConfirm;
    SharedPreferences sharedPreferences;
    String savedUser;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_so);
        List<AssetConfirmModel> list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerAsset);
        spinUnit = findViewById(R.id.spnSPBU2);
        spinRuang = findViewById(R.id.spnRuangan2);
        btnConfirm = findViewById(R.id.btnProcess);
        //pb = findViewById(R.id.progressBar2);
        //pb.setVisibility(View.GONE);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        if (sharedPreferences != null) {
            savedUser = sharedPreferences.getString("Username", "");

            unitRequest(savedUser);
        }

        spinUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.spnSPBU2) {
                    ruanganList.clear();
                    String selectedUnit = String.valueOf(unitListValue.get(position));
                    if (!Objects.equals(selectedUnit, "0")) {
                        ruanganRequest(selectedUnit);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinRuang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.spnRuangan2) {
                    String selectedRuangan = String.valueOf(ruanganListValue.get(position));
                    int getValueUnit = spinUnit.getSelectedItemPosition();
                    String selectedUnit = String.valueOf(unitListValue.get(getValueUnit));
                    if (!selectedRuangan.equals("0")) {
                        Cursor cursor = myDB.getAssetLists(selectedUnit, selectedRuangan);
                        try {
                            while (cursor.moveToNext()) {
                                AssetConfirmModel model = new AssetConfirmModel();
                                model.setKodeUnit(cursor.getString(0));
                                model.setKodeRuangan(cursor.getString(1));
                                model.setAssetId(cursor.getString(2));
                                model.setAssetName(cursor.getString(3));
                                model.setKodeBarcode(cursor.getString(4));
                                model.setKeterangan(cursor.getString(5));
                                model.setStatus(cursor.getString(6));
                                list.add(model);
                            }
                        } catch (Exception ex) {
                            Toast.makeText(ConfirmationSOActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        } finally {
                            cursor.close();
                        }

                        setAdapter(list);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Progress Bar
                //pb.setVisibility(View.VISIBLE);
                //Push data to Database
                String jsonData ="";
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonArray2 = new JSONObject();

                for (int i=0; i < list.size() ; i++) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("UserId",savedUser);
                        jsonObject.put("KodeUnit",list.get(i).getKodeUnit());
                        jsonObject.put("KodeRuangan",list.get(i).getKodeRuangan());
                        jsonObject.put("AssetId",list.get(i).getAssetId());
                        jsonObject.put("Keterangan",list.get(i).getKeterangan());
                        jsonObject.put("Status",list.get(i).getStatus());

                        jsonArray.put(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jsonArray2.put("ListDetail", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                confirmSO(jsonArray2);
            }
        });
    }

    public void setAdapter(List<AssetConfirmModel> list) {
        AssetConfirmAdapter myAdapter = new AssetConfirmAdapter(this, list);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
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

                        unitAdapter = new ArrayAdapter<>(ConfirmationSOActivity.this,
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
                Toast.makeText(ConfirmationSOActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                //error.printStackTrace();
            }
        });

        mQueue = Volley.newRequestQueue(ConfirmationSOActivity.this);
        mQueue.add(request);
    }

    private void ruanganRequest(String unitid) {
        String jsonUrl = "http://203.77.249.186:8031/Asset/GetKodeRuangan?unitid=" + unitid;
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                ruanganList.add("Select Ruangan");
                ruanganListValue.add("0");

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String KodeRuangan = jsonObject.getString("KodeRuangan");
                        String NamaRuangan = jsonObject.getString("NamaRuangan");
                        ruanganList.add(NamaRuangan);
                        ruanganListValue.add(KodeRuangan);

                        ruanganAdapter = new ArrayAdapter<>(ConfirmationSOActivity.this,
                                android.R.layout.simple_spinner_item, ruanganList);
                        ruanganAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinRuang.setAdapter(ruanganAdapter);
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

        mQueue = Volley.newRequestQueue(ConfirmationSOActivity.this);
        mQueue.add(request);
    }

    private void confirmSO(JSONObject dataSO) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://203.77.249.186:8031/api/ApiAsset/InsertResult";

        final String requestBody = dataSO.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
                int getValueRuangan = spinRuang.getSelectedItemPosition();
                String selectedRuangan = String.valueOf(ruanganListValue.get(getValueRuangan));
                int getValueUnit = spinUnit.getSelectedItemPosition();
                String selectedUnit = String.valueOf(unitListValue.get(getValueUnit));
                myDB.deleteData(selectedUnit, selectedRuangan);

                Toast.makeText(ConfirmationSOActivity.this, "Confirmation Succeed", Toast.LENGTH_SHORT).show();

                //pb.setVisibility(View.GONE);

                Intent intent = new Intent(ConfirmationSOActivity.this, MainMenu.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("VOLLEY", error.toString());
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}