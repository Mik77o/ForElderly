package com.example.supportelderly.Data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.supportelderly.Model.Alarm;
import com.example.supportelderly.Util.AlarmUtils;

import java.util.List;

/**
 * Helper dla operacji CRUD związanych z alarmami.
 */
public final class DatabaseHelperForAlarms extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "alarms.db";
    private static final int SCHEMA = 1;

    private static final String TABLE_NAME = "alarms";

    public static final String _ID = "_id";
    public static final String COL_TIME = "time";
    public static final String COL_LABEL = "label";
    public static final String COL_IMAGE = "image";
    public static final String COL_MON = "mon";
    public static final String COL_TUES = "tues";
    public static final String COL_WED = "wed";
    public static final String COL_THURS = "thurs";
    public static final String COL_FRI = "fri";
    public static final String COL_SAT = "sat";
    public static final String COL_SUN = "sun";
    public static final String COL_IS_ENABLED = "is_enabled";

    private Context context;

    @SuppressLint("StaticFieldLeak")
    private static DatabaseHelperForAlarms sInstance = null;

    public static synchronized DatabaseHelperForAlarms getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelperForAlarms(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelperForAlarms(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.i(getClass().getSimpleName(), "Tworzenie bazy danych...");

        final String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TIME + " INTEGER NOT NULL, " +
                COL_LABEL + " TEXT, " +
                COL_IMAGE + " BLOB, " +
                COL_MON + " INTEGER NOT NULL, " +
                COL_TUES + " INTEGER NOT NULL, " +
                COL_WED + " INTEGER NOT NULL, " +
                COL_THURS + " INTEGER NOT NULL, " +
                COL_FRI + " INTEGER NOT NULL, " +
                COL_SAT + " INTEGER NOT NULL, " +
                COL_SUN + " INTEGER NOT NULL, " +
                COL_IS_ENABLED + " INTEGER NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(CREATE_ALARMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        throw new UnsupportedOperationException("This shouldn't happen yet!");
    }

    public long addAlarm() {
        return addAlarm(new Alarm());
    }

    private long addAlarm(Alarm alarm) {
        return getWritableDatabase().insert(TABLE_NAME, null, AlarmUtils.toContentValues(alarm));
    }

    public int updateAlarm(Alarm alarm) {
        final String where = _ID + "=?";
        final String[] whereArgs = new String[]{Long.toString(alarm.getId())};
        return getWritableDatabase()
                .update(TABLE_NAME, AlarmUtils.toContentValues(alarm), where, whereArgs);
    }

    public int deleteAlarm(Alarm alarm) {
        return deleteAlarm(alarm.getId());
    }

    public void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM alarms";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.execute();
        database.close();
    }

    private int deleteAlarm(long id) {
        final String where = _ID + "=?";
        final String[] whereArgs = new String[]{Long.toString(id)};
        return getWritableDatabase().delete(TABLE_NAME, where, whereArgs);
    }

    public List<Alarm> getAlarms() {

        Cursor c = null;
        try {
            c = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null);
            return AlarmUtils.buildAlarmList(c, context);
        } finally {
            if (c != null && !c.isClosed()) c.close();
        }

    }

}
