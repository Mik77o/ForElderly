package com.example.supportelderly.UI;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.supportelderly.Adapter.MedicinesListAdapter;
import com.example.supportelderly.Helpers.BigToastClassHelper;
import com.example.supportelderly.Helpers.CustomAlertDialogClassHelper;
import com.example.supportelderly.Model.Medicine;
import com.example.supportelderly.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.supportelderly.Helpers.ActionBarClassHelper.createCustomActionBar;
import static com.example.supportelderly.Helpers.ConverterForImageClassHelper.convertImageViewToByteArray;
import static com.example.supportelderly.Helpers.ConverterForImageClassHelper.setImageViewWithByteArray;

/**
 * Klasa dotycząca obsługi listy wyświetlanych leków zapisanych w bazie danych.
 */
public class MedicinesList extends AppCompatActivity {

    private ArrayList<Medicine> listOfMedicines;
    private MedicinesListAdapter adapter = null;
    private TextView textEmptyList;
    private ImageView imageViewMedicines;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        createCustomActionBar(actionBar, "Lista zapisanych leków");

        setContentView(R.layout.medicine_list_activity);

        GridView gridViewForListOfMedicines = findViewById(R.id.gridView);
        textEmptyList = findViewById(R.id.empty_list_text);

        listOfMedicines = new ArrayList<>();
        adapter = new MedicinesListAdapter(this, R.layout.medicines_items, listOfMedicines);
        gridViewForListOfMedicines.setAdapter(adapter);

        // get all data from sqlite
        Cursor cursor = MainMedicinesActivity.databaseHelperForMedicines.getData("SELECT * FROM MEDICINES");
        listOfMedicines.clear();
        while (cursor.moveToNext()) {
            Medicine tmpMedicine = new Medicine();
            int id = cursor.getInt(0);
            String nameOfMedicine = cursor.getString(1);
            String dose = cursor.getString(2);
            String frequency = cursor.getString(3);
            String place = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            tmpMedicine.setName(nameOfMedicine);
            tmpMedicine.setDose(dose);
            tmpMedicine.setFrequency(frequency);
            tmpMedicine.setPlace(place);
            tmpMedicine.setImage(image);
            tmpMedicine.setId(id);

            listOfMedicines.add(tmpMedicine);
        }
        if (cursor.getCount() < 1) {
            textEmptyList.setVisibility(View.VISIBLE);
        } else {
            textEmptyList.setVisibility(View.INVISIBLE);
        }

        adapter.notifyDataSetChanged();

        gridViewForListOfMedicines.setOnItemLongClickListener((parent, view, position, id) -> {

            CharSequence[] items = {"Aktualizuj wpis", "Usuń wpis"};
            AlertDialog.Builder dialog;

            dialog = new CustomAlertDialogClassHelper().setCustomAlert(MedicinesList.this, "Wybierz akcję", null, false);
            dialog.setItems(items, (dialog1, item) -> {
                if (item == 0) {
                    // update
                    Cursor c = MainMedicinesActivity.databaseHelperForMedicines.getData("SELECT id FROM MEDICINES");
                    ArrayList<Integer> arrID = new ArrayList<>();
                    while (c.moveToNext()) {
                        arrID.add(c.getInt(0));
                    }
                    // show dialog update at here
                    showDialogUpdate(MedicinesList.this, arrID.get(position));

                } else {
                    // delete
                    Cursor c = MainMedicinesActivity.databaseHelperForMedicines.getData("SELECT id FROM MEDICINES");
                    ArrayList<Integer> arrID = new ArrayList<>();
                    while (c.moveToNext()) {
                        arrID.add(c.getInt(0));
                    }
                    showDialogForDeleteChoice(arrID.get(position));
                }
            });
            dialog.show();
            return true;
        });
    }

    private void showDialogUpdate(Activity activity, final int position) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_medicine_info_window);
        dialog.setTitle("Update");

        imageViewMedicines = dialog.findViewById(R.id.imageViewMedicines);
        final EditText edtMedicine = dialog.findViewById(R.id.edtMedicine);
        final EditText edtDose = dialog.findViewById(R.id.edtDose);
        final EditText edtFrequency = dialog.findViewById(R.id.edtFrequency);
        final EditText edtPlace = dialog.findViewById(R.id.edtPlace);
        CardView btnUpdate = dialog.findViewById(R.id.btnUpdate);

        Cursor c = MainMedicinesActivity.databaseHelperForMedicines.getData("SELECT * FROM MEDICINES");
        while (c.moveToNext()) {
            if (c.getInt(0) == position) {
                edtMedicine.setText(c.getString(1));
                edtMedicine.setSelection(edtMedicine.getText().length());
                edtDose.setText(c.getString(2));
                edtFrequency.setText(c.getString(3));
                edtPlace.setText(c.getString(4));
                byte[] image = c.getBlob(5);
                setImageViewWithByteArray(imageViewMedicines, image);
                break;
            }
        }

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.9);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.9);
        Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        dialog.show();

        imageViewMedicines.setOnClickListener(v -> {
            // request photo library
            ActivityCompat.requestPermissions(
                    MedicinesList.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    888
            );
        });

        btnUpdate.setOnClickListener(v -> {
            try {
                final Medicine medicineUpdate = new Medicine(edtMedicine.getText().toString().trim(), edtDose.getText().toString().trim(), edtFrequency.getText().toString().trim(), edtPlace.getText().toString().trim(), convertImageViewToByteArray(imageViewMedicines), position);
                MainMedicinesActivity.databaseHelperForMedicines.updateData(medicineUpdate);
                dialog.dismiss();

                new BigToastClassHelper().setBigToast("Pomyślnie zaktualizowano wpis.", getApplicationContext());
            } catch (Exception error) {
                Log.e("Update error", error.getMessage());
            }
            updateMedicinesList();
        });
    }

    private void showDialogForDeleteChoice(final int idMedicine) {
        AlertDialog.Builder dialog;

        dialog = new CustomAlertDialogClassHelper().setCustomAlert(MedicinesList.this, "Ostrzeżenie!!!", "Na pewno chcesz usunąć wpis?", true);

        dialog.setPositiveButton("Tak", (dial, which) -> {
            try {
                MainMedicinesActivity.databaseHelperForMedicines.deleteData(idMedicine);
                new BigToastClassHelper().setBigToast("Pomyślnie usunięto wpis.", getApplicationContext());
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
            updateMedicinesList();
        });

        dialog.setNegativeButton("Nie", (dial, which) -> dial.dismiss());
        dialog.show();
    }

    private void updateMedicinesList() {
        // get all data from sqlite
        Cursor cursor = MainMedicinesActivity.databaseHelperForMedicines.getData("SELECT * FROM MEDICINES");
        listOfMedicines.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nameOfMedicine = cursor.getString(1);
            String dose = cursor.getString(2);
            String frequency = cursor.getString(3);
            String place = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            listOfMedicines.add(new Medicine(nameOfMedicine, dose, place, frequency, image, id));
        }
        if (cursor.getCount() < 1) {
            textEmptyList.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            } else {
                new BigToastClassHelper().setBigToast("Nie masz uprawnień, by skorzystać z galerii.", getApplicationContext());
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewMedicines.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.back_remove_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                new CustomAlertDialogClassHelper().setCustomAlert("Usuwanie listy leków!", "Czy na pewno chcesz usunąć listę zapisanych leków?", this, (dialog, which) -> {
                    MainMedicinesActivity.databaseHelperForMedicines.deleteAll();
                    if (listOfMedicines != null) {
                        listOfMedicines.clear();
                    }
                    adapter.notifyDataSetChanged();
                    textEmptyList.setVisibility(View.VISIBLE);
                }, (dialog, which) -> dialog.dismiss());
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}