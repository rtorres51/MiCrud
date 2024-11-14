package com.example.micrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos
    private static final String DATABASE_NAME = "users.db";

    // Tabla de usuarios
    private static final String TABLE_USERS = "users";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Tabla de citas
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COL_ID = "id";
    private static final String COL_CLIENT_NAME = "client_name";
    private static final String COL_SERVICE = "service";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla de usuarios
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USERNAME + " TEXT PRIMARY KEY, " +
                COL_PASSWORD + " TEXT)");

        // Crear la tabla de citas
        db.execSQL("CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CLIENT_NAME + " TEXT, " +
                COL_SERVICE + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Borrar las tablas si existen y recrearlas
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    // Métodos para manejar usuarios

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1; // Retorna true si el registro es exitoso
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?", new String[]{username, password});

        return cursor.getCount() > 0; // Retorna true si existe un usuario con esas credenciales
    }

    // Métodos para manejar citas

    // Insertar nueva cita
    public boolean insertAppointment(String clientName, String service, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CLIENT_NAME, clientName);
        contentValues.put(COL_SERVICE, service);
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_TIME, time);
        long result = db.insert(TABLE_APPOINTMENTS, null, contentValues);
        return result != -1;
    }

    // Obtener todas las citas
    public Cursor getAllAppointments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_APPOINTMENTS, null);
    }

    // Actualizar una cita
    public boolean updateAppointment(int id, String clientName, String service, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CLIENT_NAME, clientName);
        contentValues.put(COL_SERVICE, service);
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_TIME, time);
        int result = db.update(TABLE_APPOINTMENTS, contentValues, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Eliminar una cita
    public boolean deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_APPOINTMENTS, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}