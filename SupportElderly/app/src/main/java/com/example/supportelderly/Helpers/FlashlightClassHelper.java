package com.example.supportelderly.Helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.widget.ImageView;

import com.example.supportelderly.R;

/**
 * Klasa związana z obsługą latarki
 */
public class FlashlightClassHelper {

    public void turnOnFlash(CameraManager cameraManager, CardView cardViewOnOffButton, ImageView modeIB, String mCameraId, Context context) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(mCameraId, true);
                playOnOffSound(context);
                cardViewOnOffButton.setCardBackgroundColor(Color.YELLOW);
                modeIB.setImageResource(R.drawable.flash_on);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOffFlash(CameraManager cameraManager, CardView cardViewOnOffButton, ImageView modeIB, String mCameraId, Context context) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(mCameraId, false);
                playOnOffSound(context);
                cardViewOnOffButton.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardBackgroundColor));
                modeIB.setImageResource(R.drawable.flash_off);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playOnOffSound(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.light_sound);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    public boolean checkFlashlightAvailability(Context context) {
        boolean isFlashAvailable = context.getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {
            new BigToastClassHelper().setBigToast("Twoje urządzenie nie wspiera lampy błyskowej", context);
        }
        return isFlashAvailable;
    }
}
