package com.example.supportelderly.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.supportelderly.R;

/**
 * Klasa odpowiedzialna za ładowanie ekranu powitalnego.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler h = new Handler();

        // 3 seconds
        long splashTime = 3000L;
        h.postDelayed(this::goToMainActivity, splashTime);
    }

    private void goToMainActivity() {

        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}

