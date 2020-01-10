package com.example.supportelderly.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.supportelderly.Helpers.BigToastClassHelper;
import com.example.supportelderly.Helpers.CustomAlertDialogClassHelper;
import com.example.supportelderly.Helpers.FlashlightClassHelper;
import com.example.supportelderly.R;
import com.example.supportelderly.View.ClockView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.example.supportelderly.UI.SettingsActivity.Mode;
import static com.example.supportelderly.UI.SettingsActivity.MyPreference;
import static com.example.supportelderly.UI.SettingsActivity.Password;
import static com.example.supportelderly.UI.SettingsActivity.PersonalData;

/**
 * Klasa odpowiedzialna za uruchomienie głównego interfejsu aplikacji i jego obsługi.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CALL = 1;

    private Context context;
    private FlashlightClassHelper flashlightClassHelper;
    private CameraManager mCameraManager;
    private String mCameraId;
    private CardView OnOffButton;
    private ImageView modeIB;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private boolean isTorchSwitchOn;
    private boolean availability;
    private boolean mode = false;
    private TextView navUsername;

    private static final String AutoLogin = "autoLogKey";

    private String[] days = new String[]{"Niedziela", "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota"};

    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        flashlightClassHelper = new FlashlightClassHelper();

        setContentView(R.layout.activity_main);

        NavigationView nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(item -> {
            Intent intentSideBar;
            int id = item.getItemId();
            switch (id) {
                case R.id.helpItem:
                    mode = true;
                    requestPermissionsForCalling();
                    break;
                case R.id.callItem:
                    mode = false;
                    requestPermissionsForCalling();
                    break;
                case R.id.smsItem:
                    intentSideBar = new Intent(MainActivity.this, SMSActivity.class);
                    startActivity(intentSideBar);
                case R.id.localizationItem:
                    intentSideBar = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intentSideBar);
                    break;
                case R.id.medicineItem:
                    intentSideBar = new Intent(MainActivity.this, MainMedicinesActivity.class);
                    startActivity(intentSideBar);
                    break;
                case R.id.alarmsItem:
                    intentSideBar = new Intent(MainActivity.this, MainActivityForAlarms.class);
                    startActivity(intentSideBar);
                    break;
                case R.id.settingsItem:
                    intentSideBar = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intentSideBar);
                    break;
                case R.id.logoutItem:
                    new CustomAlertDialogClassHelper().setCustomAlert("Zamykanie aplikacji", "Na pewno chcesz zamknąć aplikację?", MainActivity.this, (dialog, which) -> {
                        if (sharedpreferences.contains(AutoLogin)) {
                            editor = sharedpreferences.edit();
                            editor.remove("autoLogKey");
                            editor.apply();
                        }
                        finishAndRemoveTask();
                    }, (dialog, which) -> dialog.cancel());
                    break;
                default:
                    return true;
            }


            return true;

        });

        View headerView = nv.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nameOfUser);

        sharedpreferences = getSharedPreferences(MyPreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(PersonalData)) {
            navUsername.setText(sharedpreferences.getString(PersonalData, ""));

        } else {
            navUsername.setText("Użytkownik");
        }

        isTorchSwitchOn = false;
        availability = flashlightClassHelper.checkFlashlightAvailability(context);

        if (availability) {
            mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                mCameraId = mCameraManager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        ClockView clockV = findViewById(R.id.calendarView);
        clockV.setOnClickListener(this);

        CardView helpCV = findViewById(R.id.help);
        helpCV.setOnClickListener(this);

        CardView callCV = findViewById(R.id.call);
        callCV.setOnClickListener(this);

        CardView list_of_medicinesCV = findViewById(R.id.list_of_medicines);
        list_of_medicinesCV.setOnClickListener(this);

        CardView smsCV = findViewById(R.id.write_sms);
        smsCV.setOnClickListener(this);

        CardView localizationCV = findViewById(R.id.localization);
        localizationCV.setOnClickListener(this);

        CardView exit_appCV = findViewById(R.id.exit_app);
        exit_appCV.setOnClickListener(this);

        CardView list_of_alarmsCV = findViewById(R.id.list_of_alarms);
        list_of_alarmsCV.setOnClickListener(this);

        CardView settingsCV = findViewById(R.id.settings);
        settingsCV.setOnClickListener(this);

        OnOffButton = findViewById(R.id.flashlight);
        OnOffButton.setOnClickListener(this);

        modeIB = findViewById(R.id.mode_fl);
        modeIB.setOnClickListener(this);

        String dateFormatString = DateFormat.getDateInstance().format(new Date());
        TextView dateView = findViewById(R.id.dateView);
        Calendar calendar = Calendar.getInstance();
        String day = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];


        dateView.setText(dateFormatString + "  " + day);

        if (sharedpreferences.contains(Mode) && sharedpreferences.contains(PersonalData) && sharedpreferences.contains(Password)) {
            if (sharedpreferences.contains(AutoLogin)) {
                new BigToastClassHelper().setBigToast("Jesteś zalogowany :)", this, true);
            } else {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.login_window, null);
                final EditText personalInfo = mView.findViewById(R.id.personal_info);
                final EditText password = mView.findViewById(R.id.password);
                final CheckBox checkAutoLogin = mView.findViewById(R.id.auto_login_checkbox);
                Button login = mView.findViewById(R.id.but_login);
                mBuilder.setView(mView);
                mBuilder.setCancelable(false);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                login.setOnClickListener(view -> {
                    if (!personalInfo.getText().toString().isEmpty() && Objects.equals(sharedpreferences.getString(PersonalData, ""), personalInfo.getText().toString().trim()) && !password.getText().toString().isEmpty() && Objects.equals(sharedpreferences.getString(Password, ""), password.getText().toString().trim())) {
                        if (checkAutoLogin.isChecked()) {
                            editor = sharedpreferences.edit();
                            editor.putString(AutoLogin, "auto_log");
                            editor.commit();
                        } else {
                            editor = sharedpreferences.edit();
                            editor.remove("autoLogKey");
                            editor.commit();
                        }
                        new BigToastClassHelper().setBigToast("Zalogowano poprawnie :)", this);
                        dialog.dismiss();
                    } else {
                        new BigToastClassHelper().setBigToast("Podano błędne dane logowania :(", this);
                    }
                });
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if (sharedpreferences.contains(PersonalData)) {
            navUsername.setText(sharedpreferences.getString(PersonalData, ""));

        } else {
            navUsername.setText("Użytkownik");
        }

    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.help:
                mode = true;
                requestPermissionsForCalling();
                break;

            case R.id.call:
                mode = false;
                requestPermissionsForCalling();
                break;

            case R.id.write_sms:
                intent = new Intent(this, SMSActivity.class);
                startActivity(intent);
                break;

            case R.id.list_of_medicines:
                intent = new Intent(this, MainMedicinesActivity.class);
                startActivity(intent);
                break;

            case R.id.exit_app:
                new CustomAlertDialogClassHelper().setCustomAlert("Zamykanie aplikacji", "Na pewno chcesz zamknąć aplikację?", this, (dialog, which) -> {
                    if (sharedpreferences.contains(AutoLogin)) {
                        editor = sharedpreferences.edit();
                        editor.remove("autoLogKey");
                        editor.apply();
                    }
                    finishAndRemoveTask();
                }, (dialog, which) -> dialog.cancel());
                break;

            case R.id.localization:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.calendarView:
                intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                break;

            case R.id.list_of_alarms:
                intent = new Intent(this, MainActivityForAlarms.class);
                startActivity(intent);
                break;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.mode_fl:
                try {
                    if (isTorchSwitchOn && availability) {
                        flashlightClassHelper.turnOffFlash(mCameraManager, OnOffButton, modeIB, mCameraId, context);
                        isTorchSwitchOn = false;
                    } else if (availability) {
                        flashlightClassHelper.turnOnFlash(mCameraManager, OnOffButton, modeIB, mCameraId, context);
                        isTorchSwitchOn = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
        }
    }

    public void requestPermissionsForCalling() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else if (mode) {
            Intent intent = new Intent(this, CallForHelpActivity.class);
            startActivityForResult(intent, REQUEST_CALL);
        } else {
            Intent intent = new Intent(this, CallActivity.class);
            startActivityForResult(intent, REQUEST_CALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new BigToastClassHelper().setBigToast("Pozwolenie na wykonywanie połączeń.", getApplicationContext());
                if (mode) {
                    Intent intent = new Intent(this, CallForHelpActivity.class);
                    startActivityForResult(intent, REQUEST_CALL);
                } else {
                    Intent intent = new Intent(this, CallActivity.class);
                    startActivityForResult(intent, REQUEST_CALL);
                }
            } else {
                new BigToastClassHelper().setBigToast("Brak pozwolenia na wykonywanie połączeń!", getApplicationContext());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTorchSwitchOn && availability) {
            flashlightClassHelper.turnOffFlash(mCameraManager, OnOffButton, modeIB, mCameraId, context);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isTorchSwitchOn && availability) return;
        flashlightClassHelper.turnOffFlash(mCameraManager, OnOffButton, modeIB, mCameraId, context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isTorchSwitchOn && availability) return;
        flashlightClassHelper.turnOnFlash(mCameraManager, OnOffButton, modeIB, mCameraId, context);
    }

    public void exitAppFromLoginWindow(View view) {
        finishAndRemoveTask();
    }
}

