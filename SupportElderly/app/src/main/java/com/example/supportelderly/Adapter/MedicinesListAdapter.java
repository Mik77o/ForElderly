package com.example.supportelderly.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.supportelderly.Model.Medicine;
import com.example.supportelderly.R;

import java.util.ArrayList;

/**
 * Adapter dla listy leków (przygotowanie widoku).
 */
public class MedicinesListAdapter extends BaseAdapter {

    private Context contextOfApplication;
    private int layout;
    private ArrayList<Medicine> medicinesList;

    public MedicinesListAdapter(Context context, int layout, ArrayList<Medicine> medicinesList) {
        this.contextOfApplication = context;
        this.layout = layout;
        this.medicinesList = medicinesList;
    }

    @Override
    public int getCount() {
        return medicinesList.size();
    }

    @Override
    public Object getItem(int position) {
        return medicinesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtMedicines, txtDose, txtFrequency, txtPlace;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) contextOfApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtMedicines = row.findViewById(R.id.txtMedicines);
            holder.txtDose = row.findViewById(R.id.txtDose);
            holder.txtFrequency = row.findViewById(R.id.txtFrequency);
            holder.txtPlace = row.findViewById(R.id.txtPlace);
            holder.imageView = row.findViewById(R.id.imgMedicines);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Medicine medicine = medicinesList.get(position);

        holder.txtMedicines.setText(String.format("Lek: %s", medicine.getNameOfMedicine()));
        holder.txtDose.setText(String.format("Dawka: %s", medicine.getDose()));
        holder.txtFrequency.setText(String.format("Częstotliwość stosowania: %s", medicine.getFrequency()));
        holder.txtPlace.setText(String.format("Miejsce leku: %s", medicine.getPlace()));
        byte[] medicineImage = medicine.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(medicineImage, 0, medicineImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
