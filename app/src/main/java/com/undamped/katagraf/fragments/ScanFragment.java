package com.undamped.katagraf.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.undamped.katagraf.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanFragment extends Fragment {

    @BindView(R.id.scan_barcode) ImageView scan_barcode;
    @BindView(R.id.productID) EditText productID;

    public ScanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scan, container, false);

        ButterKnife.bind(this, root);

        scan_barcode.setOnClickListener(view -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
            intentIntegrator.setPrompt("Scan a item's QR Code");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set the content and format of scan message
                productID.setText(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}