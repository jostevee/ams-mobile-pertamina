package com.example.basicapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link BarcodeScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarcodeScannerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    public static final int CAMERA_PERMISSION_CODE = 100;

    // TODO: Rename and change types of parameters
    private static final String TAG = "BarcodeScanner";
    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private PageViewModel pageViewModel;
    private Button openBarcode;

    public static BarcodeScannerFragment newInstance() {
        return new BarcodeScannerFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the Page View Model
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        pageViewModel.setIndex(TAG);

        //DESIGN NO ACTIVITY
        // Set up the Code Scanner object
        //scannerView = requireView().findViewById(R.id.scanner_view);
        //mCodeScanner = new CodeScanner(requireContext(), scannerView);

        // Get the Button
        openBarcode = view.findViewById(R.id.btnOpenBarcode);
        openBarcode.setOnClickListener(v -> {
            Intent inten = new Intent(requireContext(), BarcodeScannerShowActivity.class);
            startActivity(inten);
        });

        //DESIGN NO ACTIVITY
        /*
        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        mCodeScanner.setDecodeCallback(result -> requireActivity().runOnUiThread(() -> {
            Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);

            Bundle bundle = new Bundle();
            bundle.putString("bundleKey", result.getText());
            // The child fragment needs to still set the result on its parent fragment manager
            getParentFragmentManager().setFragmentResult("requestKey", bundle);

            //barcode = result.getText();
            //Log.e("barcodeText", barcode);
        }));
        scannerView.setOnClickListener(view1 -> mCodeScanner.startPreview());
         */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barcode_scanner, container, false);

        // DESIGN NO ACTIVITY
        // scannerView = view.findViewById(R.id.scanner_view);

        return view;
    }

    /*
    public CodeScanner getCodeScanner(){
        return mCodeScanner;
    }

    public CodeScannerView getScannerView(){
        return scannerView;
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission},
                    requestCode);
        } else {
            Toast.makeText(getContext(), "Permission Already Granted", Toast.LENGTH_SHORT).show();
            mCodeScanner.startPreview();
        }
    }

    /*
    public BarcodeScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    */
}