package com.example.supportelderly.UI;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.supportelderly.R;

import java.util.Objects;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Klasa obsługująca widok związany z wejściem w alarm pojawiający się jako notyfikacja.
 */
public final class AlarmLandingPageFragment extends Fragment implements SensorEventListener {

    private long lastTime;
    private int count;
    private float lastX;
    private float lastY;
    private float lastZ;

    static final int SHAKE_THRESHOLD = 100;
    public SensorManager sensorManager;
    public Sensor accSensor;
    private Vibrator vibrator;

    public static int DATA_X = SensorManager.DATA_X;
    public static int DATA_Y = SensorManager.DATA_Y;
    public static int DATA_Z = SensorManager.DATA_Z;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_alarm_landing_page, container, false);
        vibrator = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final Button btnForLaunchMainActivity = v.findViewById(R.id.load_main_activity_btn);
        final Button dismiss = v.findViewById(R.id.dismiss_btn);
        btnForLaunchMainActivity.setOnClickListener(v1 -> {
            startActivity(new Intent(getContext(), MainActivity.class));
            vibrator.cancel();
            getActivity().finish();
        });
        dismiss.setOnClickListener(p -> {
            vibrator.cancel();
            getActivity().finish();
        });
        PowerManager pm = (PowerManager) Objects.requireNonNull(getActivity()).getSystemService(Context.POWER_SERVICE);

        startVibration();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (accSensor != null)
            sensorManager.registerListener(this, accSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                float x = event.values[SensorManager.DATA_X];
                float y = event.values[SensorManager.DATA_Y];
                float z = event.values[SensorManager.DATA_Z];
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    lastTime = currentTime;
                    count++; //dla testu potrząsania
                    if (count == 10) {
                        vibrator.cancel();
                        Objects.requireNonNull(getActivity()).finish();
                    }
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
    }

    public void startVibration() {
        vibrator.vibrate(new long[]{100, 1000, 100, 500, 100, 1000}, 0);
    }
}