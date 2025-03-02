package com.example.lalonde_inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * The database helper handles SQLite database operations such as user
 * authentication, inventory management, and notifications.
 *
 * @author Carl LaLonde
 *
 * Date: 2/23/2025
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    //Users table.
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    //Inventory items table.
    private static final String TABLE_INVENTORY_ITEMS = "inventory";
    private static final String COLUMN_ITEM_ID = "item_id";
    private static final String COLUMN_ITEM_NAME = "name";
    private static final String COLUMN_ITEM_DESCRIPTION = "description";
    private static final String COLUMN_ITEM_CATEGORY = "category";
    private static final String COLUMN_ITEM_QUANTITY = "quantity";

    //Initialize the database.
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creates users table.
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT);";

        //Creates inventory items table.
        String createItemsTable = "CREATE TABLE " + TABLE_INVENTORY_ITEMS + " ("
                + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ITEM_NAME + " TEXT, "
                + COLUMN_ITEM_DESCRIPTION + " TEXT, "
                + COLUMN_ITEM_CATEGORY + " TEXT, "
                + COLUMN_ITEM_QUANTITY + " INTEGER);";

        db.execSQL(createUsersTable);
        db.execSQL(createItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY_ITEMS);
        onCreate(db);
    }

    //Adds a new user to the database.
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    //Authenticate user's username and password.
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND "
                + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    //Adds a new inventory item to the database.
    public boolean addItem(String name, String description, String category, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, name);
        values.put(COLUMN_ITEM_DESCRIPTION, description);
        values.put(COLUMN_ITEM_CATEGORY, category);
        values.put(COLUMN_ITEM_QUANTITY, quantity);

        long result = db.insert(TABLE_INVENTORY_ITEMS, null, values);
        db.close();
        return result != -1;
    }

    //Gets all inventory items.
    public Cursor getInventoryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INVENTORY_ITEMS, null);
    }

    //Update inventory items quantity.
    public boolean updateItemQuantity(int itemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);

        int result = db.update("inventory", values, "item_id=?", new String[]{String.valueOf(itemId)});
        db.close();
        return result > 0;
    }

    //Delete an item from inventory.
    public boolean deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("inventory", "item_id=?", new String[]{String.valueOf(itemId)});
        db.close();
        return result > 0;
    }

    //Get low quantity notifications.
    public ArrayList<String> getNotifications() {
        ArrayList<String> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Check for low quantities of 2 or less.
        Cursor cursor = db.rawQuery("SELECT name, quantity FROM inventory WHERE quantity <= 2", null);
        while (cursor.moveToNext()) {
            String message = cursor.getString(0) + " quantity is down to " + cursor.getInt(1) + ". Order more!";
            notifications.add(message);
        }
        cursor.close();
        db.close();

        return notifications;
    }
}

