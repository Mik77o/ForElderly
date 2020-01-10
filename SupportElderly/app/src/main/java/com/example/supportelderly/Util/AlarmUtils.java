package com.example.supportelderly.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.SparseBooleanArray;

import com.example.supportelderly.Data.DatabaseHelperForAlarms;
import com.example.supportelderly.Model.Alarm;
import com.example.supportelderly.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_FRI;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_IMAGE;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_IS_ENABLED;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_LABEL;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_MON;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_SAT;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_SUN;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_THURS;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_TIME;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_TUES;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms.COL_WED;
import static com.example.supportelderly.Data.DatabaseHelperForAlarms._ID;
import static com.example.supportelderly.Helpers.ConverterForImageClassHelper.convertBitmapToByteArrayAU;

/**
 * Klasa pomocnicza dla budowania listy alarmów, zapisu ich do bazy danych oraz odpowiedniego formatu czasu.
 */
public final class AlarmUtils {

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());

    private static final int REQUEST_ALARM = 1;
    private static final String[] PERMISSIONS_ALARM = {
            Manifest.permission.VIBRATE
    };

    private AlarmUtils() {
        throw new AssertionError();
    }

    public static void checkAlarmPermissions(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        final int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.VIBRATE
        );

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ALARM,
                    REQUEST_ALARM
            );
        }

    }

    public static ContentValues toContentValues(Alarm alarm) {

        final ContentValues cv = new ContentValues(11);

        cv.put(COL_TIME, alarm.getTime());
        cv.put(COL_LABEL, alarm.getLabel());

        Bitmap tmpBitmap = alarm.getImage();
        convertBitmapToByteArrayAU(cv, COL_IMAGE, tmpBitmap);

        final SparseBooleanArray days = alarm.getDays();
        cv.put(COL_MON, days.get(Alarm.MON) ? 1 : 0);
        cv.put(COL_TUES, days.get(Alarm.TUES) ? 1 : 0);
        cv.put(COL_WED, days.get(Alarm.WED) ? 1 : 0);
        cv.put(COL_THURS, days.get(Alarm.THURS) ? 1 : 0);
        cv.put(COL_FRI, days.get(Alarm.FRI) ? 1 : 0);
        cv.put(COL_SAT, days.get(Alarm.SAT) ? 1 : 0);
        cv.put(COL_SUN, days.get(Alarm.SUN) ? 1 : 0);

        cv.put(DatabaseHelperForAlarms.COL_IS_ENABLED, alarm.isEnabled());

        return cv;

    }

    public static ArrayList<Alarm> buildAlarmList(Cursor c, Context context) {

        if (c == null) return new ArrayList<>();

        final int size = c.getCount();

        final ArrayList<Alarm> alarms = new ArrayList<>(size);

        if (c.moveToFirst()) {
            do {

                final long id = c.getLong(c.getColumnIndex(_ID));
                final long time = c.getLong(c.getColumnIndex(COL_TIME));
                final String label = c.getString(c.getColumnIndex(COL_LABEL));
                final byte[] image = c.getBlob(c.getColumnIndex(COL_IMAGE));
                final boolean mon = c.getInt(c.getColumnIndex(COL_MON)) == 1;
                final boolean tues = c.getInt(c.getColumnIndex(COL_TUES)) == 1;
                final boolean wed = c.getInt(c.getColumnIndex(COL_WED)) == 1;
                final boolean thurs = c.getInt(c.getColumnIndex(COL_THURS)) == 1;
                final boolean fri = c.getInt(c.getColumnIndex(COL_FRI)) == 1;
                final boolean sat = c.getInt(c.getColumnIndex(COL_SAT)) == 1;
                final boolean sun = c.getInt(c.getColumnIndex(COL_SUN)) == 1;
                final boolean isEnabled = c.getInt(c.getColumnIndex(COL_IS_ENABLED)) == 1;
                final Alarm alarm;
                if (image != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    alarm = new Alarm(id, time, label, bitmap);
                } else {
                    Bitmap btmp = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.medicine_icon);
                    alarm = new Alarm(id, time, label, btmp);
                }
                alarm.setDay(Alarm.MON, mon);
                alarm.setDay(Alarm.TUES, tues);
                alarm.setDay(Alarm.WED, wed);
                alarm.setDay(Alarm.THURS, thurs);
                alarm.setDay(Alarm.FRI, fri);
                alarm.setDay(Alarm.SAT, sat);
                alarm.setDay(Alarm.SUN, sun);

                alarm.setIsEnabled(isEnabled);

                alarms.add(alarm);

            } while (c.moveToNext());
        }

        return alarms;

    }

    public static String getReadableTime(long time) {
        return TIME_FORMAT.format(time);
    }

    public static String getAmPm(long time) {
        return AM_PM_FORMAT.format(time);
    }

    public static boolean isAlarmActive(Alarm alarm) {

        final SparseBooleanArray days = alarm.getDays();

        boolean isActive = false;
        int count = 0;

        while (count < days.size() && !isActive) {
            isActive = days.valueAt(count);
            count++;
        }

        return isActive;

    }

    public static String getActiveDaysAsString(Alarm alarm) {

        StringBuilder builder = new StringBuilder("Active Days: ");

        if (alarm.getDay(Alarm.MON)) builder.append("Monday, ");
        if (alarm.getDay(Alarm.TUES)) builder.append("Tuesday, ");
        if (alarm.getDay(Alarm.WED)) builder.append("Wednesday, ");
        if (alarm.getDay(Alarm.THURS)) builder.append("Thursday, ");
        if (alarm.getDay(Alarm.FRI)) builder.append("Friday, ");
        if (alarm.getDay(Alarm.SAT)) builder.append("Saturday, ");
        if (alarm.getDay(Alarm.SUN)) builder.append("Sunday.");

        if (builder.substring(builder.length() - 2).equals(", ")) {
            builder.replace(builder.length() - 2, builder.length(), ".");
        }

        return builder.toString();

    }

}
