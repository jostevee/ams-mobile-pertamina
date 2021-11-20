package com.example.basicapplication;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class AllAssetFragment extends MainFragment {
    private RecyclerView recyclerView;
    private final List<AssetModel> modelList = new ArrayList<>();
    private RequestQueue mQueue;
    private Button btnGen;
    private Spinner spinUnit, spinRuangan;
    private TableLayout tableAsset;
    private ProgressDialog progressDialog;

    String savedUser;

    ArrayList<String> unitList = new ArrayList<>();
    ArrayList<String> unitListValue = new ArrayList<>();
    ArrayList<String> ruanganList = new ArrayList<>();
    ArrayList<String> ruanganListValue = new ArrayList<>();
    ArrayAdapter<String> unitAdapter;
    ArrayAdapter<String> ruanganAdapter;
    SharedPreferences sharedPreferences;

    //ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Layout untuk fragment
        View view = inflater.inflate(R.layout.activity_all_asset, container, false);
        recyclerView = view.findViewById(R.id.recyclerAsset);

        //layout untuk fragment
        //return inflater.inflate(R.layout.activity_all_asset, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinUnit = requireActivity().findViewById(R.id.spinnerSPBU);
        spinRuangan = requireActivity().findViewById(R.id.spinnerRuangan);
        btnGen = requireActivity().findViewById(R.id.btnGenerate);
        tableAsset = requireActivity().findViewById(R.id.tableAsset);

        // How to implement recycler asset through this method
        //recyclerView = view.findViewById(R.id.recyclerAsset);

        //progressBar = getActivity().findViewById(R.id.progressBar4);
        //progressBar.setVisibility(View.GONE);

        // Progress Dialog
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Processing your data from server");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        sharedPreferences = requireContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        if (sharedPreferences != null) {
            savedUser = sharedPreferences.getString("Username", "");
            unitRequest(savedUser);
        }

        btnGen.setOnClickListener(v -> {
            progressDialog.setTitle("Processing your data");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            //progressBar.setVisibility(View.VISIBLE);

            modelList.clear();
            int getValueRuangan = spinRuangan.getSelectedItemPosition();
            String selectedRuangan = String.valueOf(ruanganListValue.get(getValueRuangan));
            int getValueUnit = spinUnit.getSelectedItemPosition();
            String selectedUnit = String.valueOf(unitListValue.get(getValueUnit));
            jsonrequest(selectedUnit, selectedRuangan);
        });

        spinUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(adapterView.getId() == R.id.spinnerSPBU){
                    ruanganList.clear();
                    String selectedUnit = String.valueOf(unitListValue.get(position));
                    if (!Objects.equals(selectedUnit, "0")){
                        ruanganRequest(selectedUnit);
                        progressDialog.show();
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
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, response -> {
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
        }, error -> {
            progressDialog.dismiss();
            //Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            //error.printStackTrace();
        });
        mQueue = Volley.newRequestQueue(requireContext());
        mQueue.add(request);
    }

    private void ruanganRequest(String unitid){
        String jsonUrl ="http://203.77.249.186:8031/Asset/GetKodeRuangan?unitid="+unitid;
        //Toast.makeText(getContext(), unitid, Toast.LENGTH_LONG).show();
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, response -> {
            JSONObject jsonObject;
            ruanganList.add("ALL");
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
                    spinRuangan.setAdapter(ruanganAdapter);
                    btnGen.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            //error.printStackTrace();
        });
        mQueue = Volley.newRequestQueue(requireContext());
        mQueue.add(request);
    }

    private void jsonrequest(String kodeunit, String koderuangan){
        String jsonUrl ="http://203.77.249.186:8031/Asset/ListAssetbyUserLogin?KodeUnit="+kodeunit+"&KodeRuangan="+koderuangan;
        tableAsset.setVisibility(View.VISIBLE);
        //Log.d("FULL_URL", "http://203.77.249.186:8031/Asset/ListAssetbyUserLogin?KodeUnit="+kodeunit+"&KodeRuangan="+koderuangan);
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                progressDialog.dismiss();

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
                //progressBar.setVisibility(View.GONE);
            }
        }, error -> {
            Toast.makeText(requireContext(), "Failed to fetch data, please check your connection/server", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            Log.e("ERROR","Ini ada error");
            //error.printStackTrace();
        });

        // Prevent Volley bug send twice
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mQueue = Volley.newRequestQueue(requireContext());
        mQueue.add(request);
    }

    public void setAssetAdapter (List<AssetModel> lst) {
        AssetAdapter myAdapter = new AssetAdapter(requireContext(),lst) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(myAdapter);

        //recyclerView.setHasFixedSize(true);
    }
}
