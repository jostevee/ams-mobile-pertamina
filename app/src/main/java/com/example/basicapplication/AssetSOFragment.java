package com.example.basicapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.widget.ViewPager2;

import static android.content.Context.MODE_PRIVATE;

public class AssetSOFragment extends MainFragment {
    public static final int CAMERA_PERMISSION_CODE = 100;
    public static List<AssetConfirmModel> modelList = new ArrayList<>();
    private Spinner spinUnit, spinRuangan;
    private RequestQueue mQueue;
    private ProgressDialog progressDialog;

    // Get Scanner View
    BarcodeScannerShowActivity barcodeScannerShowActivity = new BarcodeScannerShowActivity();
    CodeScanner codeScanner = this.barcodeScannerShowActivity.getCodeScanner();
    CodeScannerView scannerView = this.barcodeScannerShowActivity.getScannerView();

    ArrayList<String> unitList = new ArrayList<>();
    ArrayList<String> unitListValue = new ArrayList<>();
    ArrayList<String> ruanganList = new ArrayList<>();
    ArrayList<String> ruanganListValue = new ArrayList<>();
    ArrayAdapter<String> unitAdapter;
    ArrayAdapter<String> ruanganAdapter;
    SharedPreferences sharedPreferences;

    String savedUser;
    String barcode;
    String keterangan;
    Button btnProcess;
    EditText txtKeterangan;
    TextView txtNamaAsset;

    private TabLayout tabLayout;
    private final int[] tabIcons = {
            R.drawable.ic_scanner,
            R.drawable.ic_manual_input
    };

    // For ViewPager2
    @StringRes
    private static final int[] TAB_TITLES =
            new int[] { R.string.tab_text_1, R.string.tab_text_2};
    private ViewPager2 viewPager;

    //int[] colorIntArray = {R.color.walking,R.color.running,R.color.biking,R.color.paddling,R.color.golfing};
    /* for FAB
    private final int[] iconIntArray = {
            R.drawable.ic_chats,
            R.drawable.ic_new_chat
    };
    */

    //RecyclerView recycler_view;
    //CameraSource cameraSource;
    //BarcodeDetector barcodeDetector;
    //DatabaseHelper mDB;
    //ProgressBar pb;
    //EditText txtBarcode;

    public static AssetSOFragment newInstance() {
        return new AssetSOFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //layout untuk fragment
        return inflater.inflate(R.layout.activity_asset_so, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // We set the listener on the child fragmentManager
        //mDB =new DatabaseHelper(this);

        // Get objects in current fragment layout
        spinUnit = requireActivity().findViewById(R.id.spnSPBU);
        spinRuangan = requireActivity().findViewById(R.id.spnRuangan);
        txtKeterangan = requireActivity().findViewById(R.id.etKeterangan);
        btnProcess = requireActivity().findViewById(R.id.btnCompare);
        txtNamaAsset = requireActivity().findViewById(R.id.NamaAsset);

        // Progress Dialog
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Processing your data from server");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Add Text Watcher on name input text
        /* txtBarcode.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //pageViewModel.setName(charSequence.toString());
            }
            @Override public void afterTextChanged(Editable editable) {
                Log.e("textChanging", txtBarcode.getText().toString());

                barcode = txtBarcode.getText().toString();
                //Toast.makeText(getContext().getApplicationContext(), txtBarcode.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
         */

        //pb = getActivity().findViewById(R.id.progressBar3);
        //pb.setVisibility(View.GONE);

        sharedPreferences = requireContext().getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        if (sharedPreferences != null) {
            savedUser = sharedPreferences.getString("Username", "");
            unitRequest(savedUser);
        }

        // Check if the barcode field value NOT EQUALS to zero
        if (barcode != null){
            txtNamaAsset.setVisibility(View.VISIBLE);
        } else {
            txtNamaAsset.setVisibility(View.INVISIBLE);
        }

        // We set the listener on the child fragmentManager
        // Input Data MANUAL
        requireActivity().getSupportFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                barcode = bundle.getString("bundleKey");
                Log.e("Barcode", barcode);
                txtNamaAsset.setVisibility(View.VISIBLE);

                // Do something with the result
                try{
                    int getValueRuangan = spinRuangan.getSelectedItemPosition();
                    String selectedRuangan = String.valueOf(ruanganListValue.get(getValueRuangan));
                    int getValueUnit = spinUnit.getSelectedItemPosition();
                    String selectedUnit = String.valueOf(unitListValue.get(getValueUnit));

                    Cursor data = myDB.getAssetbyBarcode(barcode, selectedUnit, selectedRuangan);
                    if (data.getCount()!= 0){
                        while(data.moveToNext()){
                            txtNamaAsset.setText(data.getString(4).toString());
                        }
                    } else {
                        txtNamaAsset.setText("Not Found");
                    }

                } catch (Exception ex){
                    txtNamaAsset.setText("Not Found");
                    Toast.makeText(requireContext(), "Something wrong !", Toast.LENGTH_SHORT).show();

                }
            }
        });

        // Listener from the Barcode (ACTIVITY MODE)
        // Input Data SCANNER
        if (getArguments() != null) {
            barcode = getArguments().getString("bundleKey");
        }

        btnProcess.setOnClickListener(v -> {
            //barcode = txtBarcode.getText().toString();
            //Log.e("barcode", barcode);

            progressDialog.setTitle("Processing your data");
            progressDialog.setMessage("Please wait");
            progressDialog.show();

            // Define string from EditText
            if(TextUtils.isEmpty(txtKeterangan.getText())) {
                txtKeterangan.setError("Please enter this field");
                progressDialog.dismiss();
                return;
            } else {
                //proceed with operation
                keterangan = txtKeterangan.getText().toString();
            }

            // Get the spinner value
            int getValueRuangan = spinRuangan.getSelectedItemPosition();
            String selectedRuangan = String.valueOf(ruanganListValue.get(getValueRuangan));
            int getValueUnit = spinUnit.getSelectedItemPosition();
            String selectedUnit = String.valueOf(unitListValue.get(getValueUnit));

            if (barcode.equals("")){
                Toast.makeText(requireContext(), "Please scan the barcode or input the barcode !", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }

            Cursor data = myDB.getAssetbyBarcode(barcode, selectedUnit, selectedRuangan);
            if (data.getCount()!= 0) {
                boolean result = myDB.updateData(barcode, keterangan, "Compared");
                if (result){
                    Intent inten = new Intent(requireActivity(), SuccessActivity.class);
                    inten.putExtra("FRAGMENT_ID", 0);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(inten);
                    progressDialog.dismiss();
                    requireActivity().finish();

                    //startCounting();
                    //Toast.makeText(requireContext(), "Data has been proceed", Toast.LENGTH_SHORT).show();
                    //codeScanner.startPreview();
                    //txtBarcode.setText("");
                    //codeScanner.startPreview();
                } else {
                    Toast.makeText(requireContext(), "Something went wrong. Please check your data!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            } else {
                Toast.makeText(requireContext(), "Barcode No : " + barcode + " isn't on the list.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

                //codeScanner.startPreview();
                //txtBarcode.setText("");
                //codeScanner.startPreview();
            }
            data.close();
        });


        spinUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.spnSPBU) {
                    ruanganList.clear();
                    String selectedUnit = String.valueOf(unitListValue.get(position));
                    if (!Objects.equals(selectedUnit, "0")) {
                        ruanganRequest(selectedUnit);
                        btnProcess.setEnabled(true);
                        progressDialog.show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinRuangan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.spnRuangan) {
                    String selectedRuangan = String.valueOf(ruanganListValue.get(position));
                    int getPosition = spinUnit.getSelectedItemPosition();
                    String selectedUnit = String.valueOf(unitListValue.get(getPosition));

                    if (!selectedRuangan.equals("0")) {
                        // Cek data exist
                        Cursor data = myDB.getAssetLists(selectedUnit, selectedRuangan);
                        if (data.getCount() == 0) {
                            //pb.setVisibility(View.VISIBLE);
                            jsonrequest(selectedUnit, selectedRuangan);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(requireActivity());
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsPagerAdapter);
        tabLayout = view.findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(requireContext().getResources().getString(TAB_TITLES[position]))
        ).attach();
        setupTabIcons();

        // ViewPager trick (DEPRECATED)
        //mContext.getResources().getString(TAB_TITLES[position]);
        //tabLayout.setupWithViewPager(viewPager);

        /* SEARCH FILTER FUNCTION
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        EditText editText = (EditText) MenuItemCompat.getActionView(searchItem);
        /**
         * Enabling Search Filter
         *
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                AddFriendsActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        */

        /* Define specific tasks for FAB button on different fragment focused
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
                if(tab.getPosition() == 0){
                    Log.i("TAG","1");
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent isi = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), AddFriendsActivity.class);
                            startActivity(isi);
                        }
                    });
                } else if (tab.getPosition() == 1){
                    Log.i("TAG","2");
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Intent isi = new Intent(getActivity().getApplicationContext(), NewChatActivity.class);
                            //startActivity(isi);
                        }
                    });
                } else {
                    Log.i("TAG","3");
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
         */
    }

    private void setupTabIcons() {
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]).select();
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);

        /*
        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setText("Contacts and Groups");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[0], 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);
        */
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission},
                    requestCode);
        } else {
            Toast.makeText(requireContext(), "Permission Already Granted", Toast.LENGTH_SHORT).show();
            //codeScanner.startPreview();
        }
    }

    private void jsonrequest(String kodeunit, String koderuangan) {
        String jsonUrl = "http://203.77.249.186:8031/Asset/ListAssetbyUserLogin?KodeUnit=" + kodeunit + "&KodeRuangan=" + koderuangan;
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, response -> {
            JSONObject jsonObject;
            for (int i = 0; i < response.length(); i++) {
                try {
                    jsonObject = response.getJSONObject(i);
                    AssetConfirmModel model = new AssetConfirmModel();
                    model.setKodeUnit(jsonObject.getString("KodeUnit"));
                    model.setKodeRuangan(jsonObject.getString("KodeRuangan"));
                    model.setAssetId(jsonObject.getString("AssetId"));
                    model.setAssetName(jsonObject.getString("NamaAsset"));
                    model.setKeterangan("");
                    model.setStatus("Not Compare");
                    modelList.add(model);

                    String unit = jsonObject.getString("KodeUnit");
                    String ruangan = jsonObject.getString("KodeRuangan");
                    String assetid = jsonObject.getString("AssetId");
                    String assetname = jsonObject.getString("NamaAsset");
                    String kodebacode = jsonObject.getString("KodeBarcode");
                    myDB.addData(unit, ruangan, assetid, assetname, kodebacode, "", "Not Compared");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //pb.setVisibility(View.GONE);
        }, error -> {
            //Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            //error.printStackTrace();
        });
        mQueue = Volley.newRequestQueue(requireContext());
        mQueue.add(request);
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
                    String NamaUnit = jsonObject.getString("NamaUnit");
                    String KodeUnit = jsonObject.getString("KodeUnit");
                    Log.e("KodeUn", KodeUnit);
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

    private void ruanganRequest(String unitid) {
        String jsonUrl = "http://203.77.249.186:8031/Asset/GetKodeRuangan?unitid=" + unitid;
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                ruanganList.add("Select Ruangan");
                ruanganListValue.add("0");
                progressDialog.dismiss();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String NamaRuangan = jsonObject.getString("NamaRuangan");
                        String KodeRuangan = jsonObject.getString("KodeRuangan");
                        Log.e("KodeRu", KodeRuangan);

                        ruanganList.add(NamaRuangan);
                        ruanganListValue.add(KodeRuangan);
                        ruanganAdapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_spinner_item, ruanganList);
                        ruanganAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinRuangan.setAdapter(ruanganAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
