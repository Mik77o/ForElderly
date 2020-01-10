package com.example.supportelderly.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.supportelderly.Helpers.BigToastClassHelper;
import com.example.supportelderly.R;

import static com.example.supportelderly.Helpers.ActionBarClassHelper.createCustomActionBar;

/**
 * Klasa odpowiedzialana za ustawienia aplikacji: ustawienia danych opiekuna, danych logowania
 * i inne preferencje.
 */
public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;
    private TextView info, info_about_log_settings;
    private EditText name;
    private EditText setPersonalInfoTV;
    private EditText setPasswordTV;
    private GridLayout gridLayoutSettings, gridLayoutButtons;
    private EditText phoneNumber;
    private Switch switchMode, switchModeAlarm;
    public SharedPreferences.Editor editor;
    private ActionBar actionBar;

    public static final String MyPreference = "myPref";
    public static final String Name = "nameKey";
    public static final String PhoneNumber = "phoneKey";
    public static final String PersonalData = "personKey";
    public static final String Password = "passwordKey";
    public static final String Mode = "modeKey";
    public static final String ModeAlarm = "modeAlarmKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        createCustomActionBar(actionBar, "Ustawienia");

        setContentView(R.layout.activity_settings);

        name = findViewById(R.id.name_O);
        phoneNumber = findViewById(R.id.phone_number_O);
        gridLayoutSettings = findViewById(R.id.grid_login_data);
        gridLayoutButtons = findViewById(R.id.grid_buttons_data);

        info_about_log_settings = findViewById(R.id.info_settings_for_login);
        setPersonalInfoTV = findViewById(R.id.set_personal_info);
        setPasswordTV = findViewById(R.id.set_password);
        switchMode = findViewById(R.id.switch_mode);
        switchModeAlarm = findViewById(R.id.mode_of_setting_alarm);

        info = findViewById(R.id.info_about_saving_secure_number);

        sharedpreferences = getSharedPreferences(MyPreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Mode)) {
            switchMode.setChecked(true);
            switchMode.setTextColor(getResources().getColor(R.color.colorAccent));
            gridLayoutSettings.setVisibility(View.VISIBLE);
            gridLayoutButtons.setVisibility(View.VISIBLE);
        } else {
            switchMode.setChecked(false);
            switchMode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            gridLayoutSettings.setVisibility(View.INVISIBLE);
            gridLayoutButtons.setVisibility(View.INVISIBLE);
        }

        if (sharedpreferences.contains(ModeAlarm)) {
            switchModeAlarm.setChecked(true);
            switchModeAlarm.setText("Tryb zegar (alarmy) - aktywny");
            switchModeAlarm.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            switchModeAlarm.setChecked(false);
            switchModeAlarm.setText("Tryb zegar (alarmy) - nieaktywny");
            switchModeAlarm.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        if (sharedpreferences.contains(Name) && sharedpreferences.contains(PhoneNumber)) {
            name.setText(sharedpreferences.getString(Name, ""));
            phoneNumber.setText(sharedpreferences.getString(PhoneNumber, ""));
            name.setSelection(name.getText().length());
            phoneNumber.setSelection(phoneNumber.getText().length());
            info.setText("Numer do opiekuna jest ustawiony");
        } else {
            info.setText("Numer do opiekuna nie jest ustawiony");
        }

        if (sharedpreferences.contains(PersonalData) && sharedpreferences.contains(Password)) {
            setPersonalInfoTV.setText(sharedpreferences.getString(PersonalData, ""));
            setPersonalInfoTV.setSelection(setPersonalInfoTV.getText().length());
            setPasswordTV.setText(sharedpreferences.getString(Password, ""));
            setPasswordTV.setSelection(setPasswordTV.getText().length());

            info_about_log_settings.setText("Dane logowania są ustawione");
        } else {
            info_about_log_settings.setText("Dane logowania nie są ustawione");
        }
    }

    public void saveSettings(View view) {
        if (!name.getText().toString().isEmpty() && !phoneNumber.getText().toString().isEmpty()) {
            String n = name.getText().toString();
            String pn = phoneNumber.getText().toString();
            editor = sharedpreferences.edit();
            editor.putString(Name, n);
            editor.putString(PhoneNumber, pn);
            editor.commit();
            new BigToastClassHelper().setBigToast("Numer opiekuna został zapisany!", this);
            info.setText("Numer do opiekuna jest ustawiony");
            name.setText("");
            phoneNumber.setText("");
        }
    }

    public void clearSettings(View view) {

        if (sharedpreferences.contains(Name) && sharedpreferences.contains(PhoneNumber)) {
            editor = sharedpreferences.edit();
            name = findViewById(R.id.name_O);
            phoneNumber = findViewById(R.id.phone_number_O);
            name.setText("");
            phoneNumber.setText("");
            editor.remove("phoneKey");
            editor.remove("nameKey");
            editor.commit();
            new BigToastClassHelper().setBigToast("Usunięto zapisany numer!", this);
            info.setText("Numer do opiekuna nie jest ustawiony");
        }
    }

    public void getSettings(View view) {
        name = findViewById(R.id.name_O);
        phoneNumber = findViewById(R.id.phone_number_O);
        sharedpreferences = getSharedPreferences(MyPreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name)) {
            name.setText(sharedpreferences.getString(Name, ""));
            name.setSelection(name.getText().toString().length());
        }
        if (sharedpreferences.contains(PhoneNumber))
            phoneNumber.setText(sharedpreferences.getString(PhoneNumber, ""));
        phoneNumber.setSelection(phoneNumber.getText().toString().length());
    }

    public void setModeOfLaunchingApp(View view) {

        if (sharedpreferences.contains(Mode)) {
            editor = sharedpreferences.edit();
            editor.remove("modeKey");
            editor.commit();
            switchMode.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            gridLayoutSettings.setVisibility(View.INVISIBLE);
            gridLayoutButtons.setVisibility(View.INVISIBLE);
        } else {
            editor = sharedpreferences.edit();
            editor.putString(Mode, "mode_active");
            editor.commit();
            switchMode.setTextColor(getResources().getColor(R.color.colorAccent));
            gridLayoutSettings.setVisibility(View.VISIBLE);
            gridLayoutButtons.setVisibility(View.VISIBLE);
        }
    }

    public void setViewOfAlarmSetting(View view) {

        if (sharedpreferences.contains(ModeAlarm)) {
            editor = sharedpreferences.edit();
            editor.remove("modeAlarmKey");
            editor.commit();
            switchModeAlarm.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            switchModeAlarm.setText("Tryb zegar (alarmy) - nieaktywny");

        } else {
            editor = sharedpreferences.edit();
            editor.putString(ModeAlarm, "mode_clock_active");
            editor.commit();
            switchModeAlarm.setTextColor(getResources().getColor(R.color.colorAccent));
            switchModeAlarm.setText("Tryb zegar (alarmy) - aktywny");
        }
    }


    public void deleteDataForLogging(View view) {
        if (sharedpreferences.contains(PersonalData) && sharedpreferences.contains(Password)) {
            editor = sharedpreferences.edit();
            setPersonalInfoTV = findViewById(R.id.set_personal_info);
            setPasswordTV = findViewById(R.id.set_password);
            setPersonalInfoTV.setText("");
            setPasswordTV.setText("");
            editor.remove("personKey");
            editor.remove("passwordKey");
            editor.commit();
            new BigToastClassHelper().setBigToast("Usunięto dane logowania!", this);
            info_about_log_settings.setText("Dane logowania nie są ustawione.");
        }
    }

    public void saveDataForLogging(View view) {
        if (!setPersonalInfoTV.getText().toString().isEmpty() && !setPasswordTV.getText().toString().isEmpty() && setPasswordTV.getText().length() > 3 && !setPasswordTV.getText().toString().isEmpty() && setPasswordTV.getText().length() <= 8) {
            String personalInfoTemp = setPersonalInfoTV.getText().toString();
            String setPasswordTemp = setPasswordTV.getText().toString();
            editor = sharedpreferences.edit();
            editor.putString(PersonalData, personalInfoTemp);
            editor.putString(Password, setPasswordTemp);
            editor.commit();
            new BigToastClassHelper().setBigToast("Dane logowania zostały zapisane!", this);
            info_about_log_settings.setText("Dane logowania są ustawione.");
        } else {
            new BigToastClassHelper().setBigToast("Pamiętaj, że nazwa użytkownika oraz hasło nie mogą być puste. Hasło powinno zawierać od 4 do 8 znaków.", this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}