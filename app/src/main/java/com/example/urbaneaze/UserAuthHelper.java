package com.example.urbaneaze;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserAuthHelper {
    private UserDatabaseHelper dbHelper;

    public UserAuthHelper(Context context) {
        dbHelper = new UserDatabaseHelper(context);
    }

    // ✅ Check if email exists in the database
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + UserDatabaseHelper.TABLE_USERS +
                " WHERE " + UserDatabaseHelper.COLUMN_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // ✅ Register User with Password Hashing
    public boolean registerUser(String id, String name, String email, String phone, String flatNo,
                                String role, String societyId, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (isEmailExists(email)) {
            return false;  // Email already registered
        }

        // Hash the password before storing
        String hashedPassword = hashPassword(password);

        ContentValues values = new ContentValues();
        values.put(UserDatabaseHelper.COLUMN_ID, id);
        values.put(UserDatabaseHelper.COLUMN_NAME, name);
        values.put(UserDatabaseHelper.COLUMN_EMAIL, email);
        values.put(UserDatabaseHelper.COLUMN_PHONE, phone);
        values.put(UserDatabaseHelper.COLUMN_FLAT_NO, flatNo);
        values.put(UserDatabaseHelper.COLUMN_ROLE, role);
        values.put(UserDatabaseHelper.COLUMN_SOCIETY_ID, societyId);
        values.put(UserDatabaseHelper.COLUMN_PASSWORD, hashedPassword);  // Storing hashed password

        long result = db.insert(UserDatabaseHelper.TABLE_USERS, null, values);
        db.close();  // ✅ Closing database to prevent memory leaks
        return result != -1;
    }

    // ✅ User Login Authentication
    public boolean authenticateUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String hashedPassword = hashPassword(password);  // Hash input password for comparison

        Cursor cursor = db.rawQuery("SELECT 1 FROM " + UserDatabaseHelper.TABLE_USERS +
                        " WHERE " + UserDatabaseHelper.COLUMN_EMAIL + " = ? AND " +
                        UserDatabaseHelper.COLUMN_PASSWORD + " = ?",
                new String[]{email, hashedPassword});

        boolean isAuthenticated = cursor.moveToFirst();
        cursor.close();
        db.close();  // ✅ Close database after checking
        return isAuthenticated;
    }

    // ✅ Secure Password Hashing (SHA-256)
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();  // Return hashed password
        } catch (NoSuchAlgorithmException e) {
            Log.e("UserAuthHelper", "SHA-256 Algorithm not found", e);
            return null;
        }
    }
}
