package com.example.basicapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link BarcodeInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarcodeInputFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private static final String TAG = "BarcodeInput";
    private PageViewModel pageViewModel;
    public EditText txtBarcode;
    // String textBarcode;

    public static BarcodeInputFragment newInstance() {
        return new BarcodeInputFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        pageViewModel.setIndex(TAG);

        // Get the EditText object and the text
        txtBarcode = view.findViewById(R.id.etBarcode);

        /*
        txtBarcode.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                try{
                    int getValueRuangan = spinRuangan.getSelectedItemPosition();
                    String selectedRuangan = String.valueOf(ruanganListValue.get(getValueRuangan));
                    int getValueUnit = spinUnit.getSelectedItemPosition();
                    String selectedUnit = String.valueOf(unitListValue.get(getValueUnit));

                    String barcode = txtBarcode.getText().toString();

                    Cursor data = myDB.getAssetbyBarcode(barcode, selectedUnit, selectedRuangan);
                    if (data.getCount()!= 0) {
                        while(data.moveToNext()){
                            txtNamaAsset.setText(data.getString(4).toString());
                        }
                    }
                    else{
                        txtNamaAsset.setText("Not Found");
                    }
                    return false;
                }
                catch (Exception ex){
                    txtNamaAsset.setText("Not Found");
                    Toast.makeText(AssetSOActivity.this, "Something wrong !", Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });
        */


        // Add Text Watcher on name input text
        txtBarcode.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //pageViewModel.setName(charSequence.toString());
            }
            @Override public void afterTextChanged(Editable editable) {
                Log.e("textChanging", txtBarcode.getText().toString());

                Bundle result = new Bundle();
                result.putString("bundleKey", txtBarcode.getText().toString());
                // The child fragment needs to still set the result on its parent fragment manager
                getParentFragmentManager().setFragmentResult("requestKey", result);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barcode_input, container, false);

        // Get the EditText object and the text
        //txtBarcode = view.findViewById(R.id.etBarcode);

        return view;
    }

    /*
    public BarcodeInputFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    */

    /*
    public void updateNumber(){
        txtBarcode.getText().clear();
    }
     */
}