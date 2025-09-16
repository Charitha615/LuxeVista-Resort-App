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


    private static final String TABLE_BOOKINGS = "bookings";
    private static final String COLUMN_BOOKING_ID = "_id";
    private static final String COLUMN_BOOKING_USER_ID = "user_id";
    private static final String COLUMN_BOOKING_ROOM_ID = "room_id";
    private static final String COLUMN_BOOKING_CHECK_IN = "check_in";
    private static final String COLUMN_BOOKING_CHECK_OUT = "check_out";
    private static final String COLUMN_BOOKING_GUESTS = "guests";
    private static final String COLUMN_BOOKING_TOTAL_PRICE = "total_price";
    private static final String COLUMN_BOOKING_STATUS = "status";

    private static final String TABLE_SERVICES = "services";
    private static final String COLUMN_SERVICE_ID = "_id";
    private static final String COLUMN_SERVICE_NAME = "service_name";
    private static final String COLUMN_SERVICE_DESCRIPTION = "service_description";
    private static final String COLUMN_SERVICE_PRICE = "service_price";
    private static final String COLUMN_SERVICE_TYPE = "service_type";
    private static final String COLUMN_SERVICE_AVAILABLE = "service_available";
    private static final String COLUMN_SERVICE_DURATION = "service_duration";

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

        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BOOKING_USER_ID + " INTEGER,"
                + COLUMN_BOOKING_ROOM_ID + " INTEGER,"
                + COLUMN_BOOKING_CHECK_IN + " TEXT,"
                + COLUMN_BOOKING_CHECK_OUT + " TEXT,"
                + COLUMN_BOOKING_GUESTS + " INTEGER,"
                + COLUMN_BOOKING_TOTAL_PRICE + " REAL,"
                + COLUMN_BOOKING_STATUS + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_BOOKING_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_BOOKING_ROOM_ID + ") REFERENCES " + TABLE_ROOMS + "(" + COLUMN_ROOM_ID + ")" + ")";
        db.execSQL(CREATE_BOOKINGS_TABLE);

        String CREATE_SERVICES_TABLE = "CREATE TABLE " + TABLE_SERVICES + "("
                + COLUMN_SERVICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SERVICE_NAME + " TEXT,"
                + COLUMN_SERVICE_DESCRIPTION + " TEXT,"
                + COLUMN_SERVICE_PRICE + " REAL,"
                + COLUMN_SERVICE_TYPE + " TEXT,"
                + COLUMN_SERVICE_AVAILABLE + " INTEGER,"
                + COLUMN_SERVICE_DURATION + " INTEGER" + ")";
        db.execSQL(CREATE_SERVICES_TABLE);

        // Insert some sample rooms
        insertSampleRooms(db);

        Log.d("DatabaseHelper", "Database created with users and rooms tables");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);

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

    public long addBooking(int userId, int roomId, String checkIn, String checkOut,
                           int guests, double totalPrice, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKING_USER_ID, userId);
        values.put(COLUMN_BOOKING_ROOM_ID, roomId);
        values.put(COLUMN_BOOKING_CHECK_IN, checkIn);
        values.put(COLUMN_BOOKING_CHECK_OUT, checkOut);
        values.put(COLUMN_BOOKING_GUESTS, guests);
        values.put(COLUMN_BOOKING_TOTAL_PRICE, totalPrice);
        values.put(COLUMN_BOOKING_STATUS, status);

        long result = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return result;
    }

    public Cursor getBookingById(int bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT b.*, u.username, u.email, r.room_type, r.room_price " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "INNER JOIN " + TABLE_USERS + " u ON b.user_id = u.id " +
                "INNER JOIN " + TABLE_ROOMS + " r ON b.room_id = r._id " +
                "WHERE b." + COLUMN_BOOKING_ID + " = ?";

        return db.rawQuery(query, new String[]{String.valueOf(bookingId)});
    }

    public Cursor getUserBookings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, r.room_type, r.room_price FROM " + TABLE_BOOKINGS + " b " +
                "INNER JOIN " + TABLE_ROOMS + " r ON b.room_id = r._id " +
                "WHERE b.user_id = ? ORDER BY b.check_in DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public int cancelBooking(int bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKING_STATUS, "Cancelled");
        return db.update(TABLE_BOOKINGS, values, COLUMN_BOOKING_ID + "=?",
                new String[]{String.valueOf(bookingId)});
    }

    public int deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // First delete user's bookings
        db.delete(TABLE_BOOKINGS, COLUMN_BOOKING_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        // Then delete user
        return db.delete(TABLE_USERS, COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)});
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
    }

    public int updateUser(int userId, String username, String email, String contact,
                          String address, String gender, String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_CONTACT, contact);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_COUNTRY, country);

        return db.update(TABLE_USERS, values, COLUMN_ID + "=?",
                new String[]{String.valueOf(userId)});
    }


    private void insertSampleServices(SQLiteDatabase db) {
        try {
            // Spa Services
            ContentValues values1 = new ContentValues();
            values1.put(COLUMN_SERVICE_NAME, "Luxury Spa Treatment");
            values1.put(COLUMN_SERVICE_DESCRIPTION, "Full body massage with aromatherapy oils and hot stone therapy");
            values1.put(COLUMN_SERVICE_PRICE, 120.00);
            values1.put(COLUMN_SERVICE_TYPE, "Spa");
            values1.put(COLUMN_SERVICE_AVAILABLE, 1);
            values1.put(COLUMN_SERVICE_DURATION, 90);
            db.insert(TABLE_SERVICES, null, values1);

            ContentValues values2 = new ContentValues();
            values2.put(COLUMN_SERVICE_NAME, "Couples Massage");
            values2.put(COLUMN_SERVICE_DESCRIPTION, "Romantic side-by-side massage for couples");
            values2.put(COLUMN_SERVICE_PRICE, 200.00);
            values2.put(COLUMN_SERVICE_TYPE, "Spa");
            values2.put(COLUMN_SERVICE_AVAILABLE, 1);
            values2.put(COLUMN_SERVICE_DURATION, 120);
            db.insert(TABLE_SERVICES, null, values2);

            // Dining Services
            ContentValues values3 = new ContentValues();
            values3.put(COLUMN_SERVICE_NAME, "Beachfront Dinner");
            values3.put(COLUMN_SERVICE_DESCRIPTION, "Private romantic dinner on the beach with personal chef");
            values3.put(COLUMN_SERVICE_PRICE, 150.00);
            values3.put(COLUMN_SERVICE_TYPE, "Dining");
            values3.put(COLUMN_SERVICE_AVAILABLE, 1);
            values3.put(COLUMN_SERVICE_DURATION, 120);
            db.insert(TABLE_SERVICES, null, values3);

            // Poolside Services
            ContentValues values4 = new ContentValues();
            values4.put(COLUMN_SERVICE_NAME, "Private Cabana Rental");
            values4.put(COLUMN_SERVICE_DESCRIPTION, "Exclusive poolside cabana with butler service");
            values4.put(COLUMN_SERVICE_PRICE, 75.00);
            values4.put(COLUMN_SERVICE_TYPE, "Poolside");
            values4.put(COLUMN_SERVICE_AVAILABLE, 1);
            values4.put(COLUMN_SERVICE_DURATION, 240);
            db.insert(TABLE_SERVICES, null, values4);

            // Beach Tours
            ContentValues values5 = new ContentValues();
            values5.put(COLUMN_SERVICE_NAME, "Sunset Beach Tour");
            values5.put(COLUMN_SERVICE_DESCRIPTION, "Guided tour to the best sunset viewing spots with champagne");
            values5.put(COLUMN_SERVICE_PRICE, 50.00);
            values5.put(COLUMN_SERVICE_TYPE, "Beach Tour");
            values5.put(COLUMN_SERVICE_AVAILABLE, 1);
            values5.put(COLUMN_SERVICE_DURATION, 90);
            db.insert(TABLE_SERVICES, null, values5);

            Log.d("DatabaseHelper", "Sample services inserted successfully");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting sample services: " + e.getMessage());
        }
    }

    // Add service management methods
    public long addService(String name, String description, double price,
                           String type, boolean available, int duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE_NAME, name);
        values.put(COLUMN_SERVICE_DESCRIPTION, description);
        values.put(COLUMN_SERVICE_PRICE, price);
        values.put(COLUMN_SERVICE_TYPE, type);
        values.put(COLUMN_SERVICE_AVAILABLE, available ? 1 : 0);
        values.put(COLUMN_SERVICE_DURATION, duration);

        long result = db.insert(TABLE_SERVICES, null, values);
        db.close();
        return result;
    }

    public Cursor getAllServices() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SERVICES, null, null, null, null, null, COLUMN_SERVICE_TYPE + ", " + COLUMN_SERVICE_NAME);
    }

    public Cursor getService(int serviceId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SERVICES, null, COLUMN_SERVICE_ID + "=?",
                new String[]{String.valueOf(serviceId)}, null, null, null);
    }

    public int updateService(int serviceId, String name, String description, double price,
                             String type, boolean available, int duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE_NAME, name);
        values.put(COLUMN_SERVICE_DESCRIPTION, description);
        values.put(COLUMN_SERVICE_PRICE, price);
        values.put(COLUMN_SERVICE_TYPE, type);
        values.put(COLUMN_SERVICE_AVAILABLE, available ? 1 : 0);
        values.put(COLUMN_SERVICE_DURATION, duration);

        return db.update(TABLE_SERVICES, values, COLUMN_SERVICE_ID + "=?",
                new String[]{String.valueOf(serviceId)});
    }

    public int deleteService(int serviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SERVICES, COLUMN_SERVICE_ID + "=?",
                new String[]{String.valueOf(serviceId)});
    }

    // Add method to get all bookings with user and room details
    public Cursor getAllBookingsWithDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, u.username, u.email, r.room_type " +
                "FROM " + TABLE_BOOKINGS + " b " +
                "INNER JOIN " + TABLE_USERS + " u ON b.user_id = u.id " +
                "INNER JOIN " + TABLE_ROOMS + " r ON b.room_id = r._id " +
                "ORDER BY b.check_in DESC";
        return db.rawQuery(query, null);
    }
}