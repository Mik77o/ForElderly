package com.example.supportelderly.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.supportelderly.Model.Medicine;

/**
 * Helper dla operacji CRUD związanych z lekami.
 */
public class DatabaseHelperForMedicines extends SQLiteOpenHelper {

    public DatabaseHelperForMedicines(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(Medicine medicineObject) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO MEDICINES VALUES (NULL,?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, medicineObject.getNameOfMedicine());
        statement.bindString(2, medicineObject.getDose());
        statement.bindString(3, medicineObject.getFrequency());
        statement.bindString(4, medicineObject.getPlace());
        statement.bindBlob(5, medicineObject.getImage());

        statement.executeInsert();
    }

    public void updateData(Medicine medicineObject) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE MEDICINES SET nameOfMedicine = ?, dose = ?, frequency = ?, place = ?, image = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, medicineObject.getNameOfMedicine());
        statement.bindString(2, medicineObject.getDose());
        statement.bindString(3, medicineObject.getFrequency());
        statement.bindString(4, medicineObject.getPlace());
        statement.bindBlob(5, medicineObject.getImage());
        statement.bindDouble(6, (double) medicineObject.getId());

        statement.execute();
        database.close();
    }

    public void deleteData(int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM MEDICINES WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double) id);

        statement.execute();
        database.close();
    }

    public void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM MEDICINES";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.execute();
        database.close();
    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
