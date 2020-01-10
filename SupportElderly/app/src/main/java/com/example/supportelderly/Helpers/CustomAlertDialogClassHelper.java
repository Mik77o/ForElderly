package com.example.supportelderly.Helpers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.supportelderly.R;

/**
 * Klasa dotycząca własnego okna alertowego
 */
public class CustomAlertDialogClassHelper {

    public void setCustomAlert(@NonNull String titleAD, String messageAD, Context context, DialogInterface.OnClickListener onClickListener_yes, DialogInterface.OnClickListener onClickListener_no) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_alert_dialog, null);

        TextView title = layout.findViewById(R.id.title);
        TextView message = layout.findViewById(R.id.message);

        title.setText(titleAD);
        message.setText(messageAD);
        builder.setPositiveButton("Tak", onClickListener_yes);
        builder.setNegativeButton("Nie", onClickListener_no);

        builder.setView(layout);
        builder.show();
    }

    public AlertDialog.Builder setCustomAlert(Context context, String titleAD, String messageAD, boolean type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_alert_dialog, null);

        TextView title = layout.findViewById(R.id.title);

        if (type) {
            TextView message = layout.findViewById(R.id.message);
            message.setText(messageAD);
        }

        title.setText(titleAD);
        builder.setView(layout);
        return builder;
    }
}
