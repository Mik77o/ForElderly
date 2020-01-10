package com.example.supportelderly.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.supportelderly.Data.DatabaseHelperForMedicines;
import com.example.supportelderly.Helpers.BigToastClassHelper;
import com.example.supportelderly.Model.Medicine;
import com.example.supportelderly.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.supportelderly.Helpers.ActionBarClassHelper.createCustomActionBar;
import static com.example.supportelderly.Helpers.ConverterForImageClassHelper.convertImageViewToByteArray;

/**
 * Klasa dotycząca panelu dodawania informacji o lekach oraz ich wyświetlania.
 */
public class MainMedicinesActivity extends AppCompatActivity {

    private EditText edtMedicine, edtDose, edtPlace, edtTime;
    private CardView btnChoose, btnAdd, btnListOfMedicines;
    private ImageView imageView;
    private ActionBar actionBar;

    final int REQUEST_CODE_GALLERY = 1;

    public static DatabaseHelperForMedicines databaseHelperForMedicines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        createCustomActionBar(actionBar, "Dodawanie informacji o leku");

        setContentView(R.layout.activity_main_medicines);

        init();

        databaseHelperForMedicines = new DatabaseHelperForMedicines(this, "MEDICINESDB.sqlite", null, 1);

        databaseHelperForMedicines.queryData("CREATE TABLE IF NOT EXISTS MEDICINES(Id INTEGER PRIMARY KEY AUTOINCREMENT, nameOfMedicine VARCHAR, dose VARCHAR, place VARCHAR, frequency VARCHAR, image BLOB)");

        btnChoose.setOnClickListener(view -> ActivityCompat.requestPermissions(
                MainMedicinesActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY
        ));

        btnAdd.setOnClickListener(view -> {
            try {

                if (edtMedicine.getText().toString().isEmpty() || edtDose.getText().toString().isEmpty() || edtTime.getText().toString().isEmpty() || edtPlace.getText().toString().isEmpty()) {
                    new BigToastClassHelper().
                            setBigToast("Proszę wypełnić wszystkie pola!", getApplicationContext());
                } else {
                    Medicine medicineInsert = new Medicine(edtMedicine.getText().toString().trim(), edtDose.getText().toString().trim(), edtTime.getText().toString().trim(), edtPlace.getText().toString().trim(), convertImageViewToByteArray(imageView));
                    databaseHelperForMedicines.insertData(medicineInsert);
                    new BigToastClassHelper().
                            setBigToast("Pomyślnie dodano informacje o leku do listy!", getApplicationContext());
                    edtMedicine.setText("");
                    edtDose.setText("");
                    edtPlace.setText("");
                    edtTime.setText("");
                    imageView.setImageResource(R.drawable.medicine_icon);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        });

        btnListOfMedicines.setOnClickListener(view ->
        {
            Intent intent = new Intent(MainMedicinesActivity.this, MedicinesList.class);
            startActivity(intent);
            new BigToastClassHelper().setBigToastForLoadingData("Pobieranie danych...", this, true);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                new BigToastClassHelper().
                        setBigToast("Nie masz uprawnień, by otworzyć galerię!", getApplicationContext());
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                assert uri != null;
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        edtMedicine = findViewById(R.id.edtMedicine);
        edtDose = findViewById(R.id.edtDose);
        edtTime = findViewById(R.id.edtTime);
        edtPlace = findViewById(R.id.edtPlace);
        btnChoose = findViewById(R.id.btnChoose);
        btnAdd = findViewById(R.id.btnAdd);
        btnListOfMedicines = findViewById(R.id.btnListOfMedicines);
        imageView = findViewById(R.id.imageView);
    }
}
