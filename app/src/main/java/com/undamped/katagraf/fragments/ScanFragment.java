package com.undamped.katagraf.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.undamped.katagraf.R;
import com.undamped.katagraf.database.ItemDatabase;
import com.undamped.katagraf.database.ProductDatabase;
import com.undamped.katagraf.models.Item;
import com.undamped.katagraf.models.Product;
import com.undamped.katagraf.notif.ReminderBroadcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.ALARM_SERVICE;

public class ScanFragment extends Fragment {

    @BindView(R.id.scan_barcode) ImageView scan_barcode;
    @BindView(R.id.productID) EditText productID;
    @BindView(R.id.detailsLayout) ConstraintLayout detailsLayout;
    @BindView(R.id.productName) EditText productName;
    @BindView(R.id.textMFD) Button textMFD;
    @BindView(R.id.textBestBefore) EditText textBestBefore;
    @BindView(R.id.quantityText) EditText quantityText;
    @BindView(R.id.ingredientText) EditText ingredientText;
    @BindView(R.id.filledNameField) TextInputLayout nameLayout;
    @BindView(R.id.filledBBTextField) TextInputLayout bbLayout;
    @BindView(R.id.filledQuantityField) TextInputLayout quantityLayout;
    @BindView(R.id.ingredientsField) TextInputLayout ingredientsField;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.fetch_button) Button fetch_button;

    private boolean productExists;
    private Calendar dateCalendar;

    public ScanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scan, container, false);

        ButterKnife.bind(this, root);
        createNotificationChannel();

        scan_barcode.setOnClickListener(view -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
            intentIntegrator.setPrompt("Scan a item's QR Code");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        });

        fetch_button.setOnClickListener(view -> {
            try {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e){
                Log.e("Error:","Keyboard not opened");
            }

            String barcode = productID.getText().toString();
            if (barcode.length() == 12) {
                searchProduct(productID.getText().toString());
                fetch_button.setVisibility(View.GONE);
            } else
                Snackbar.make(view, "Enter a proper barcode!", Snackbar.LENGTH_LONG).show();
        });

        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        textMFD.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                    dateCalendar = Calendar.getInstance();
                    dateCalendar.set(Calendar.YEAR, year);
                    dateCalendar.set(Calendar.MONTH, month);
                    dateCalendar.set(Calendar.DATE, date);
                    textMFD.setText(DateFormat.format("EEEE, MMM d yyyy", dateCalendar).toString());
//                    dateMeeting = String.valueOf(DateFormat.format("d MMM", dateCalendar));

                }
            }, YEAR, MONTH, DATE);

            datePickerDialog.show();
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
                fetch_button.setVisibility(View.GONE);
                searchProduct(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void searchProduct(String barcode) {
        detailsLayout.setVisibility(View.VISIBLE);
        Product scannedProd = ProductDatabase.getInstance(getContext()).ProductDao().fetchProduct(barcode);
        productID.setEnabled(false);

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
            textBestBefore.setText(String.valueOf(scannedProd.getBestBefore()));
            ingredientText.setText(scannedProd.getIngredientString());
            ingredientText.setEnabled(false);
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
        quantityLayout.setError(null);
        ingredientsField.setError(null);

        String name = productName.getText().toString();
        String quantity = quantityText.getText().toString();
        String manufactureDate = textMFD.getText().toString();
        String bestBefore = textBestBefore.getText().toString();
        String ingredientsText = ingredientText.getText().toString();

        if (name.trim().isEmpty())
            nameLayout.setError("Product name cannot be empty");
        else if (manufactureDate.startsWith("Manufact"))
            Toast.makeText(getContext(), "Please select the Manufacturing date!", Toast.LENGTH_LONG).show();
        else if (bestBefore.trim().isEmpty())
            bbLayout.setError("Best before month cannot be empty");
        else if (quantity.equals("0") || quantity.isEmpty())
            quantityLayout.setError("Quantity cannot be empty or zero");
        else if (ingredientsText.isEmpty())
            ingredientsField.setError("At least one ingredient must be present");
        else {
            dateCalendar.add(Calendar.MONTH, Integer.parseInt(bestBefore));
            String expDate = DateFormat.format("EEEE, MMM d yyyy", dateCalendar).toString();
            List<String> ingredients = Arrays.asList(ingredientsText.split(",|, "));

            if (!productExists)
                saveProductToDb(productID.getText().toString(), name, bestBefore, ingredients);

            Item item = new Item(name, expDate, Integer.parseInt(quantity), ingredients);

            //Save it to database and OnSuccess move to HomeFragment
            ItemDatabase itemDb = ItemDatabase.getInstance(getContext());
            itemDb.ItemDao().insertItem(item);
            createNotification(item);
            Toast.makeText(getContext(), "Item added to database", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_frame, new InventoryFragment()).commit();
        }
    }

    private void saveProductToDb(String barcode, String productName, String bestBefore, List<String> ingredients) {
        Product newProd = new Product(Long.parseLong(barcode), productName, Integer.parseInt(bestBefore), ingredients);
        ProductDatabase.getInstance(getContext()).ProductDao().insertProduct(newProd);
    }

    private void createNotification(Item item) {
        Intent intent = new Intent(getContext(), ReminderBroadcast.class);
        intent.putExtra("ItemName", item.getName());

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.HOUR_OF_DAY, 9);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) System.currentTimeMillis(), intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Channel for reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyUs", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}