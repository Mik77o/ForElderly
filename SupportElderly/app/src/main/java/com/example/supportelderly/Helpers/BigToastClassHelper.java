package com.example.supportelderly.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supportelderly.R;

/**
 * Klasa definiująca własny Toast.
 */
public class BigToastClassHelper {

    private Toast toastInfo;

    public void setBigToast(@NonNull String content, Context context) {

        toastInfo = new Toast(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_toast, null);

        ImageView image = layout.findViewById(R.id.image);
        image.setImageResource(R.mipmap.ic_launcher);

        TextView toastText = layout.findViewById(R.id.cont);
        toastText.setText(content);

        toastInfo = Toast.makeText(context, content, Toast.LENGTH_LONG);
        toastInfo.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toastInfo.setView(layout);
        toastInfo.show();
    }

    public void setBigToastForLoadingData(@NonNull String content, Context context, boolean longDuration) {

        toastInfo = new Toast(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_toast_for_loading_data, null);

        ImageView image = layout.findViewById(R.id.downloading);
        image.setImageResource(R.drawable.download);

        TextView toastText = layout.findViewById(R.id.cont);
        toastText.setText(content);

        if (longDuration) {
            toastInfo = Toast.makeText(context, content, Toast.LENGTH_LONG);
        } else {
            toastInfo = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        }
        toastInfo.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toastInfo.setView(layout);
        toastInfo.show();
    }

    @SuppressLint("ShowToast")
    public void setBigToast(@NonNull String content, Context context, boolean durationShort) {

        toastInfo = new Toast(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_toast, null);

        ImageView image = layout.findViewById(R.id.image);
        image.setImageResource(R.mipmap.ic_launcher);

        TextView toastText = layout.findViewById(R.id.cont);
        toastText.setText(content);

        if (durationShort) {
            toastInfo = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toastInfo = Toast.makeText(context, content, Toast.LENGTH_LONG);
        }
        toastInfo.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toastInfo.setView(layout);
        toastInfo.show();
    }
}
