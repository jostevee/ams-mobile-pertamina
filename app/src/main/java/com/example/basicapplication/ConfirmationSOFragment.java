package com.example.basicapplication;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfirmationSOFragment extends MainFragment {
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
    private ProgressDialog progressDialog;
    private TableLayout tableConfirmAsset;

    SharedPreferences sharedPreferences;
    String savedUser;

    //ProgressBar pb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //layout untuk fragment
        View view = inflater.inflate(R.layout.activity_confirm_so, container, false);

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.recyclerAssetConfirm);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<AssetConfirmModel> list = new ArrayList<>();
        //recyclerView = getView().findViewById(R.id.recyclerAsset);

        spinUnit = requireActivity().findViewById(R.id.spnSPBU2);
        spinRuang = requireActivity().findViewById(R.id.spnRuangan2);
        btnConfirm = requireActivity().findViewById(R.id.btnProcess);
        tableConfirmAsset = requireActivity().findViewById(R.id.tableConfirmAsset);

        // How to implement recycler asset through this method
        //recyclerView = view.findViewById(R.id.recyclerAssetConfirm);

        //pb = findViewById(R.id.progressBar2);
        //pb.setVisibility(View.GONE);

        // Progress Dialog
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Processing your data from server");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        sharedPreferences = requireContext().getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
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
                        progressDialog.show();
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
                        try (Cursor cursor = myDB.getAssetLists(selectedUnit, selectedRuangan)) {
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
                                Log.v("Khusus", cursor.getString(3));
                            }
                            if(cursor.getCount() > 0){
                                tableConfirmAsset.setVisibility(View.VISIBLE);
                                btnConfirm.setEnabled(true);
                            }
                        } catch (Exception ex) {
                            Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
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

                progressDialog.setTitle("Processing your data");
                progressDialog.setMessage("Please wait");
                progressDialog.show();

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
        AssetConfirmAdapter myAdapter = new AssetConfirmAdapter(requireContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(myAdapter);

        //recyclerView.setHasFixedSize(true);
    }

    private void unitRequest(String userid) {
        String jsonUrl = "http://203.77.249.186:8031/Asset/GetUserUnit?userid=" + userid;
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                unitList.add("Select Unit");
                unitListValue.add("0");
                progressDialog.dismiss();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String KodeUnit = jsonObject.getString("KodeUnit");
                        String NamaUnit = jsonObject.getString("NamaUnit");
                        unitList.add(NamaUnit);
                        unitListValue.add(KodeUnit);

                        unitAdapter = new ArrayAdapter<>(requireContext(),
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
                progressDialog.dismiss();

                //Toast.makeText(requireContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                //error.printStackTrace();
            }
        });
        mQueue = Volley.newRequestQueue(requireContext());
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
                progressDialog.dismiss();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String KodeRuangan = jsonObject.getString("KodeRuangan");
                        String NamaRuangan = jsonObject.getString("NamaRuangan");
                        ruanganList.add(NamaRuangan);
                        ruanganListValue.add(KodeRuangan);

                        ruanganAdapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, ruanganList);
                        ruanganAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinRuang.setAdapter(ruanganAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, error -> {
            progressDialog.dismiss();

            //Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            //error.printStackTrace();
        });
        mQueue = Volley.newRequestQueue(requireContext());
        mQueue.add(request);
    }

    private void confirmSO(JSONObject dataSO) {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
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

                Intent inten = new Intent(requireActivity(), SuccessActivity.class);
                inten.putExtra("FRAGMENT_ID", 1);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(inten);
                progressDialog.dismiss();
                requireActivity().finish();

                //startCounting();
                //Toast.makeText(requireContext(), "Confirmation Succeed", Toast.LENGTH_SHORT).show();
                //pb.setVisibility(View.GONE);
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
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } // returned data is not JSONObject?

                }
                Intent intent = new Intent(requireActivity(), SuccessActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                progressDialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            /*
            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
             */

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                assert response != null;
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void startCounting() {
        /* Called when the activity is first created. */
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 2000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    //startActivity(requireActivity().getIntent());
                }
            }
        };
        splashThread.start();
    }
}
