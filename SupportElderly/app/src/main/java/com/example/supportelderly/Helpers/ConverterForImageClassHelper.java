package com.example.supportelderly.Helpers;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Klasa odpowiedzialna za konwersję obrazów.
 */
public class ConverterForImageClassHelper {

    public static void convertBitmapToByteArray(Bitmap bitmap, Parcel parcel) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();
            parcel.writeInt(byteArray.length);
            parcel.writeByteArray(byteArray);
        }
    }

    public static void convertBitmapToByteArrayAU(ContentValues contentValues, String colName, Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();
            contentValues.put(colName, byteArray);
        }
    }

    public static byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    public static Bitmap imageViewToBitmap(ImageView imageOfMedicine) {
        imageOfMedicine.setDrawingCacheEnabled(true);
        imageOfMedicine.buildDrawingCache();
        return Bitmap.createBitmap(imageOfMedicine.getDrawingCache());
    }

    public static void setImageViewWithByteArray(ImageView view, byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        view.setImageBitmap(bitmap);
    }
}
