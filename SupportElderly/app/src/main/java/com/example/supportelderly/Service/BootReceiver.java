package com.example.supportelderly.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.supportelderly.Data.DatabaseHelperForAlarms;
import com.example.supportelderly.Model.Alarm;

import java.util.List;
import java.util.concurrent.Executors;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static com.example.supportelderly.Service.LoadAlarmsService.AlarmReceiver.setReminderAlarms;

/**
 * Klasa odpowiedzialna za przywracanie alarmów po reboot-cie.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Executors.newSingleThreadExecutor().execute(() -> {
                final List<Alarm> alarms = DatabaseHelperForAlarms.getInstance(context).getAlarms();
                setReminderAlarms(context, alarms);
            });
        }
    }

}
