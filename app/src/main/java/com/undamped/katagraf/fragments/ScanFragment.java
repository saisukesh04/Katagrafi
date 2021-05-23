package com.undamped.katagraf.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.undamped.katagraf.R;
import com.undamped.katagraf.database.ItemDatabase;
import com.undamped.katagraf.database.ProductDatabase;
import com.undamped.katagraf.models.Item;
import com.undamped.katagraf.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanFragment extends Fragment {

    @BindView(R.id.scan_barcode) ImageView scan_barcode;
    @BindView(R.id.productID) EditText productID;
    @BindView(R.id.detailsLayout) ConstraintLayout detailsLayout;
    @BindView(R.id.productName) EditText productName;
    @BindView(R.id.textMFD) EditText textMFD;
    @BindView(R.id.textBestBefore) EditText textBestBefore;
    @BindView(R.id.quantityText) EditText quantityText;
    @BindView(R.id.ingredientText) EditText ingredientText;
    @BindView(R.id.filledNameField) TextInputLayout nameLayout;
    @BindView(R.id.filledMFDField) TextInputLayout mfdLayout;
    @BindView(R.id.filledBBTextField) TextInputLayout bbLayout;
    @BindView(R.id.filledQuantityField) TextInputLayout quantityLayout;
    @BindView(R.id.ingredientsField) TextInputLayout ingredientsField;
    @BindView(R.id.saveButton) Button saveButton;

    private boolean productExists;

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

        saveButton.setOnClickListener(view -> saveDetail());

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
                searchProduct(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void searchProduct(String barcode) {
        detailsLayout.setVisibility(View.VISIBLE);
        Product scannedProd = ProductDatabase.getInstance(getContext()).ProductDao().fetchProduct(barcode);

        if (scannedProd == null) {
            //Entry doesn't exist in the database, ask the user to enter the data
            productName.setCursorVisible(true);
            productName.setClickable(true);
            productName.requestFocus();
            ingredientText.setCursorVisible(true);
            ingredientText.setClickable(true);
            nameLayout.setError("Product details not found, kindly enter it manually");
            productExists = false;
        } else {
            productExists = true;
            productName.setEnabled(false);
            textBestBefore.setEnabled(false);
            productName.setText(scannedProd.getName());
            textBestBefore.setText(scannedProd.getBestBefore());
            ingredientText.setText(scannedProd.getIngredients().toString());
        }
    }

    private void saveDetail() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(productName.getWindowToken(), 0);
        manager.hideSoftInputFromWindow(textBestBefore.getWindowToken(), 0);
        manager.hideSoftInputFromWindow(quantityText.getWindowToken(), 0);
        productName.clearFocus();
        textBestBefore.clearFocus();
        quantityText.clearFocus();
        nameLayout.setError(null);
        bbLayout.setError(null);
        mfdLayout.setError(null);
        quantityLayout.setError(null);
        ingredientsField.setError(null);

        String name = productName.getText().toString();
        String quantity = quantityText.getText().toString();
        String manufactureDate = textMFD.getText().toString();
        String bestBefore = textBestBefore.getText().toString();
        String ingredientsText = ingredientText.getText().toString();

        if (name.trim().isEmpty())
            nameLayout.setError("Product name cannot be empty");
        else if (manufactureDate.isEmpty())
            mfdLayout.setError("Manufacturing date cannot be empty");
        else if (bestBefore.trim().isEmpty())
            bbLayout.setError("Best before date cannot be empty");
        else if (quantity.equals("0") || quantity.isEmpty())
            quantityLayout.setError("Quantity cannot be empty or zero");
        else if (ingredientsText.isEmpty())
            ingredientsField.setError("At least one ingredient must be present");
        else {
            String expDate = null;
            List<String> ingredients = Arrays.asList(ingredientsText.split(",|, "));

            if (!productExists)
                saveProductToMongo(productID.getText().toString(), productName, bestBefore, ingredients);

            Item item = new Item(name, expDate, Integer.parseInt(quantity), ingredients);

            //Save it to database and OnSuccess move to HomeFragment
            ItemDatabase itemDb = ItemDatabase.getInstance(getContext());
            itemDb.ItemDao().insertItem(item);
            Toast.makeText(getContext(), "Item added to database", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_frame, new InventoryFragment()).commit();
        }
    }

    private void saveProductToMongo(String barcode, EditText productName, String bestBefore, List<String> ingredients) {
        //TODO : Save the product to mongoDb
    }
}