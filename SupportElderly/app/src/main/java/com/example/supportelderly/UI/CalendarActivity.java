package com.example.supportelderly.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;

import com.example.supportelderly.R;

import java.util.Calendar;

import static com.example.supportelderly.Helpers.ActionBarClassHelper.createCustomActionBar;

/**
 * Klasa odpowiedzialna za wyświetlanie widoku kalendarza oraz przejścia do systemowego, w celu zapisania ważnej daty.
 */
public class CalendarActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView add_dateCV;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        createCustomActionBar(actionBar, "Kalendarz");

        setContentView(R.layout.activity_calendar);

        add_dateCV = findViewById(R.id.add_date);
        add_dateCV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.add_date) {
            Calendar cal = Calendar.getInstance();
            Intent intentForCalendarEvents = new Intent(Intent.ACTION_EDIT);
            intentForCalendarEvents.setType("vnd.android.cursor.item/event");
            intentForCalendarEvents.putExtra("beginTime", cal.getTimeInMillis());
            intentForCalendarEvents.putExtra("allDay", true);
            intentForCalendarEvents.putExtra("rule", "FREQ=YEARLY");
            intentForCalendarEvents.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
            intentForCalendarEvents.putExtra("title", "Dodaj ważną datę");
            startActivity(intentForCalendarEvents);
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