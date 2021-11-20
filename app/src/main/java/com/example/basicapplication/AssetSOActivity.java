package com.example.basicapplication;

public class AssetSOActivity extends MainMenu {
    /*
    public static final int CAMERA_PERMISSION_CODE = 100;
    RecyclerView recycler_view;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    private CodeScanner mCodeScanner;
    private Spinner spinUnit, spinRuangan;
    ArrayList<String> unitList = new ArrayList<>();
    ArrayList<String> unitListValue = new ArrayList<>();
    ArrayList<String> ruanganList = new ArrayList<>();
    ArrayList<String> ruanganListValue = new ArrayList<>();
    ArrayAdapter<String> unitAdapter;
    ArrayAdapter<String> ruanganAdapter;
    EditText txtBarcode, txtKeterangan;
    public static List<AssetConfirmModel> modelList = new ArrayList<>();
    private RequestQueue mQueue;
    SharedPreferences sharedPreferences;
    String savedUser;
    //DatabaseHelper mDB;
    Button btnProcess;
    //ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_so);

        //mDB =new DatabaseHelper(this);
        spinUnit = findViewById(R.id.spnSPBU);
        spinRuangan = findViewById(R.id.spnRuangan);
        txtBarcode = findViewById(R.id.etBarcode);
        txtKeterangan = findViewById(R.id.etKeterangan);
        btnProcess = findViewById(R.id.btnCompare);
        //pb = findViewById(R.id.progressBar3);
        //pb.setVisibility(View.GONE);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        if (sharedPreferences != null) {
            savedUser = sharedPreferences.getString("Username", "");

            unitRequest(savedUser);
        }
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                AssetSOActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                        txtBarcode.setText(result.getText());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = txtBarcode.getText().toString();
                String keterangan = txtKeterangan.getText().toString();

                if (barcode.equals("")){
                    Toast.makeText(AssetSOActivity.this, "Please scan the barcode or input the barcode !", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor data = myDB.getAssetbyBarcode(barcode);
                if (data.getCount()!= 0) {
                    boolean result = myDB.updateData(barcode, keterangan, "Compared");
                    if (result){
                        Toast.makeText(AssetSOActivity.this, "Data has been proceed", Toast.LENGTH_SHORT).show();
                        txtBarcode.setText("");
                        txtKeterangan.setText("");
                        mCodeScanner.startPreview();
                    }else{
                        Toast.makeText(AssetSOActivity.this, "Something went wrong. Please check your data!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AssetSOActivity.this, "Barcode No : "+ barcode +" isn't on the list.", Toast.LENGTH_SHORT).show();
                    txtBarcode.setText("");
                    txtKeterangan.setText("");
                    mCodeScanner.startPreview();
                }
                data.close();
            }
        });

        spinUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.spnSPBU) {
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

        spinRuangan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (adapterView.getId() == R.id.spnRuangan) {
                    String selectedRuangan = String.valueOf(ruanganListValue.get(position));
                    int getPosition = spinUnit.getSelectedItemPosition();
                    String selectedUnit = String.valueOf(unitListValue.get(getPosition));

                    if (!selectedRuangan.equals("0")) {
                        //Cek data exist
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
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(AssetSOActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(AssetSOActivity.this, new String[]{permission},
                    requestCode);
        } else {
            Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show();
            mCodeScanner.startPreview();
        }
    }

    private void jsonrequest(String kodeunit, String koderuangan) {
        String jsonUrl = "http://203.77.249.186:8031/Asset/ListAssetbyUserLogin?KodeUnit=" + kodeunit + "&KodeRuangan=" + koderuangan;
        JsonArrayRequest request = new JsonArrayRequest(jsonUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue = Volley.newRequestQueue(AssetSOActivity.this);
        mQueue.add(request);
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
                        String NamaUnit = jsonObject.getString("NamaUnit");
                        String KodeUnit = jsonObject.getString("KodeUnit");
                        unitList.add(NamaUnit);
                        unitListValue.add(KodeUnit);

                        unitAdapter = new ArrayAdapter<>(AssetSOActivity.this,
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
                Toast.makeText(AssetSOActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                //error.printStackTrace();
            }
        });

        mQueue = Volley.newRequestQueue(AssetSOActivity.this);
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
                        String NamaRuangan = jsonObject.getString("NamaRuangan");
                        String KodeRuangan = jsonObject.getString("KodeRuangan");

                        ruanganList.add(NamaRuangan);
                        ruanganListValue.add(KodeRuangan);
                        ruanganAdapter = new ArrayAdapter<>(AssetSOActivity.this,
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

        mQueue = Volley.newRequestQueue(AssetSOActivity.this);
        mQueue.add(request);
    }
     */
}