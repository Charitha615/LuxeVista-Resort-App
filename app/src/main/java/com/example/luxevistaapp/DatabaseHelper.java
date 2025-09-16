package com.example.luxevistaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LuxeVista.db";
    private static final int DATABASE_VERSION = 3; // Incremented to 3

    // User table constants
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_CONTACT = "contact";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_PASSWORD = "password";

    // Room table constants
    private static final String TABLE_ROOMS = "rooms";
    private static final String COLUMN_ROOM_ID = "_id"; // CHANGED to _id for SimpleCursorAdapter
    private static final String COLUMN_ROOM_TYPE = "room_type";
    private static final String COLUMN_ROOM_DESCRIPTION = "room_description";
    private static final String COLUMN_ROOM_PRICE = "room_price";
    private static final String COLUMN_ROOM_CAPACITY = "room_capacity";
    private static final String COLUMN_ROOM_AVAILABLE = "room_available";
    private static final String COLUMN_ROOM_IMAGE = "room_image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_CONTACT + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_GENDER + " TEXT,"
                + COLUMN_COUNTRY + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create rooms table
        String CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS + "("
                + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // Now _id
                + COLUMN_ROOM_TYPE + " TEXT,"
                + COLUMN_ROOM_DESCRIPTION + " TEXT,"
                + COLUMN_ROOM_PRICE + " REAL,"
                + COLUMN_ROOM_CAPACITY + " INTEGER,"
                + COLUMN_ROOM_AVAILABLE + " INTEGER,"
                + COLUMN_ROOM_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_ROOMS_TABLE);

        // Insert some sample rooms
        insertSampleRooms(db);

        Log.d("DatabaseHelper", "Database created with users and rooms tables");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);

        // Create tables again
        onCreate(db);

        Log.d("DatabaseHelper", "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Check if rooms table exists, if not create it
        if (!doesTableExist(db, TABLE_ROOMS)) {
            Log.d("DatabaseHelper", "Rooms table doesn't exist, creating it");
            String CREATE_ROOMS_TABLE = "CREATE TABLE " + TABLE_ROOMS + "("
                    + COLUMN_ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ROOM_TYPE + " TEXT,"
                    + COLUMN_ROOM_DESCRIPTION + " TEXT,"
                    + COLUMN_ROOM_PRICE + " REAL,"
                    + COLUMN_ROOM_CAPACITY + " INTEGER,"
                    + COLUMN_ROOM_AVAILABLE + " INTEGER,"
                    + COLUMN_ROOM_IMAGE + " TEXT" + ")";
            db.execSQL(CREATE_ROOMS_TABLE);
            insertSampleRooms(db);
        }
    }

    private void insertSampleRooms(SQLiteDatabase db) {
        try {
            // Ocean View Suite
            ContentValues values1 = new ContentValues();
            values1.put(COLUMN_ROOM_TYPE, "Ocean View Suite");
            values1.put(COLUMN_ROOM_DESCRIPTION, "Luxurious suite with breathtaking ocean views, king-sized bed, and private balcony.");
            values1.put(COLUMN_ROOM_PRICE, 299.99);
            values1.put(COLUMN_ROOM_CAPACITY, 2);
            values1.put(COLUMN_ROOM_AVAILABLE, 1);
            values1.put(COLUMN_ROOM_IMAGE, "");
            db.insert(TABLE_ROOMS, null, values1);

            // Deluxe Room
            ContentValues values2 = new ContentValues();
            values2.put(COLUMN_ROOM_TYPE, "Deluxe Room");
            values2.put(COLUMN_ROOM_DESCRIPTION, "Spacious room with premium amenities, comfortable queen bed, and garden view.");
            values2.put(COLUMN_ROOM_PRICE, 199.99);
            values2.put(COLUMN_ROOM_CAPACITY, 2);
            values2.put(COLUMN_ROOM_AVAILABLE, 1);
            values2.put(COLUMN_ROOM_IMAGE, "");
            db.insert(TABLE_ROOMS, null, values2);

            // Family Suite
            ContentValues values3 = new ContentValues();
            values3.put(COLUMN_ROOM_TYPE, "Family Suite");
            values3.put(COLUMN_ROOM_DESCRIPTION, "Perfect for families, with separate bedrooms, living area, and kitchenette.");
            values3.put(COLUMN_ROOM_PRICE, 349.99);
            values3.put(COLUMN_ROOM_CAPACITY, 4);
            values3.put(COLUMN_ROOM_AVAILABLE, 1);
            values3.put(COLUMN_ROOM_IMAGE, "");
            db.insert(TABLE_ROOMS, null, values3);

            Log.d("DatabaseHelper", "Sample rooms inserted successfully");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting sample rooms: " + e.getMessage());
        }
    }

    public boolean doesTableExist(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // User management methods
    public long addUser(String username, String email, String contact, String address,
                        String gender, String country, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_CONTACT, contact);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_COUNTRY, country);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public Cursor getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null,
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);
    }

    // Room management methods
    public long addRoom(String type, String description, double price,
                        int capacity, boolean available, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_TYPE, type);
        values.put(COLUMN_ROOM_DESCRIPTION, description);
        values.put(COLUMN_ROOM_PRICE, price);
        values.put(COLUMN_ROOM_CAPACITY, capacity);
        values.put(COLUMN_ROOM_AVAILABLE, available ? 1 : 0);
        values.put(COLUMN_ROOM_IMAGE, image);

        long result = db.insert(TABLE_ROOMS, null, values);
        db.close();
        return result;
    }

    public Cursor getAllRooms() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Double-check that the rooms table exists
        if (!doesTableExist(db, TABLE_ROOMS)) {
            Log.d("DatabaseHelper", "Rooms table doesn't exist in getAllRooms(), creating it");
            onCreate(db);
        }

        return db.query(TABLE_ROOMS, null, null, null, null, null, COLUMN_ROOM_TYPE);
    }

    public Cursor getRoom(int roomId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Double-check that the rooms table exists
        if (!doesTableExist(db, TABLE_ROOMS)) {
            Log.d("DatabaseHelper", "Rooms table doesn't exist in getRoom(), creating it");
            onCreate(db);
        }

        return db.query(TABLE_ROOMS, null, COLUMN_ROOM_ID + "=?",
                new String[]{String.valueOf(roomId)}, null, null, null);
    }

    public int updateRoom(int roomId, String type, String description, double price,
                          int capacity, boolean available, String image) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Double-check that the rooms table exists
        if (!doesTableExist(db, TABLE_ROOMS)) {
            Log.d("DatabaseHelper", "Rooms table doesn't exist in updateRoom(), creating it");
            onCreate(db);
            return 0; // Return 0 since we just created the table, so no rows were updated
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_ROOM_TYPE, type);
        values.put(COLUMN_ROOM_DESCRIPTION, description);
        values.put(COLUMN_ROOM_PRICE, price);
        values.put(COLUMN_ROOM_CAPACITY, capacity);
        values.put(COLUMN_ROOM_AVAILABLE, available ? 1 : 0);
        values.put(COLUMN_ROOM_IMAGE, image);

        return db.update(TABLE_ROOMS, values, COLUMN_ROOM_ID + "=?",
                new String[]{String.valueOf(roomId)});
    }

    public int deleteRoom(int roomId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Double-check that the rooms table exists
        if (!doesTableExist(db, TABLE_ROOMS)) {
            Log.d("DatabaseHelper", "Rooms table doesn't exist in deleteRoom(), creating it");
            onCreate(db);
            return 0; // Return 0 since we just created the table, so no rows were deleted
        }

        return db.delete(TABLE_ROOMS, COLUMN_ROOM_ID + "=?",
                new String[]{String.valueOf(roomId)});
    }

    // Utility method to convert bitmap to base64 string
    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Utility method to convert base64 string to bitmap
    public Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error converting base64 to bitmap: " + e.getMessage());
            return null;
        }
    }
}