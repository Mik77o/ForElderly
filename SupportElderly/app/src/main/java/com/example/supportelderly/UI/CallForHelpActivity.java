package com.example.supportelderly.UI;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;

import com.example.supportelderly.Helpers.BigToastClassHelper;
import com.example.supportelderly.Helpers.CustomAlertDialogClassHelper;
import com.example.supportelderly.R;

import static com.example.supportelderly.Helpers.ActionBarClassHelper.createCustomActionBar;
import static com.example.supportelderly.UI.SettingsActivity.MyPreference;
import static com.example.supportelderly.UI.SettingsActivity.Name;
import static com.example.supportelderly.UI.SettingsActivity.PhoneNumber;

/**
 * Klasa dotycząca wykonywania połączeń pod numery ratunkowe lub numer do opiekuna.
 */
public class CallForHelpActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView call_guardianCV, call_ambulanceCV, call_fire_brigadeCV, call_policeCV;
    private SharedPreferences sharedpreferences;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        createCustomActionBar(actionBar, "Wzywanie pomocy");

        setContentView(R.layout.activity_call_for_help);

        sharedpreferences = getSharedPreferences(MyPreference,
                Context.MODE_PRIVATE);

        call_guardianCV = findViewById(R.id.call_guardian);
        call_guardianCV.setOnClickListener(this);

        call_ambulanceCV = findViewById(R.id.call_ambulance);
        call_ambulanceCV.setOnClickListener(this);

        call_fire_brigadeCV = findViewById(R.id.call_fire_brigade);
        call_fire_brigadeCV.setOnClickListener(this);

        call_fire_brigadeCV = findViewById(R.id.call_fire_brigade);
        call_fire_brigadeCV.setOnClickListener(this);

        call_policeCV = findViewById(R.id.call_police);
        call_policeCV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_ambulance:
                new CustomAlertDialogClassHelper().setCustomAlert("Pogotowie ratunkowe!", "Czy na pewno chcesz zadzwonić na pogotowie ratunkowe?", this, (dialog, which) -> makePhoneCallForHelp("999"), (dialog, which) -> dialog.dismiss());
                break;
            case R.id.call_guardian:
                String name = "";
                String phoneNumber = "";
                if (sharedpreferences.contains(Name)) {
                    name = sharedpreferences.getString(Name, "");
                }
                if (sharedpreferences.contains(PhoneNumber)) {
                    phoneNumber = sharedpreferences.getString(PhoneNumber, "");
                }
                assert name != null;
                assert phoneNumber != null;
                if (name.isEmpty() || phoneNumber.isEmpty()) {
                    new BigToastClassHelper().setBigToast("Administrator aplikacji nie wprowadził danych. Proszę się z nim skontaktować.", getApplicationContext());
                } else {
                    String finalPhoneNumber = phoneNumber;
                    new CustomAlertDialogClassHelper().setCustomAlert
                            ("Opiekun/Rodzina!", "Czy na pewno chcesz zadzwonić do opiekuna/rodziny: " + name + "?", this, (dialog, which) -> makePhoneCallForHelp(finalPhoneNumber), (dialog, which) -> dialog.dismiss());
                }
                break;
            case R.id.call_police:
                new CustomAlertDialogClassHelper().setCustomAlert
                        ("Policja!", "Czy na pewno chcesz zadzwonić na policję?", this, (dialog, which) -> makePhoneCallForHelp("997"), (dialog, which) -> dialog.dismiss());
                break;
            case R.id.call_fire_brigade:
                new CustomAlertDialogClassHelper().setCustomAlert("Straż pożarna!", "Czy na pewno chcesz zadzwonić po straż pożarną?", this, (dialog, which) -> makePhoneCallForHelp("998"), (dialog, which) -> dialog.dismiss());
                break;
        }
    }

    private void makePhoneCallForHelp(String phoneNumber) {
        String dial = "tel:" + phoneNumber;
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}




