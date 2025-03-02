package com.example.lalonde_inventoryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * This activity allows user to view messages.  The app
 * will send a message to the user notifying them when an
 * items quantity has reached 2 or less.
 *
 * @author Carl LaLonde
 *
 * Date: 2/23/2025
 */
public class MessagesActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private LinearLayout notificationContainer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_screen);

        //Initialize database and save data.
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        //Initialize user interface elements.
        notificationContainer = findViewById(R.id.notificationContainer);
        ImageView backButton = findViewById(R.id.backButton);

        //Initialize bottom navigation bar buttons.
        Button itemsButton = findViewById(R.id.itemsButton);
        Button addItemButton = findViewById(R.id.addItemButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        //Set up onClick listeners.
        itemsButton.setOnClickListener(view -> openActivity(ItemsActivity.class));
        addItemButton.setOnClickListener(view -> openActivity(AddNewItemActivity.class));
        logoutButton.setOnClickListener(view -> logoutUser());
        backButton.setOnClickListener(view -> onBackPressed());

        //Load notifications.
        loadNotifications();
    }

    //Opens the correct activity when a button is clicked
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(MessagesActivity.this, activityClass);
        startActivity(intent);
    }

    //Logs out the user.
    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        startActivity(new Intent(MessagesActivity.this, LoginActivity.class));
        finish();
    }

    //Loads notifications when the screen is opened.
    private void loadNotifications() {
        notificationContainer.removeAllViews(); // Clear previous messages

        ArrayList<String> notifications = getLowStockNotifications();

        if (notifications.isEmpty()) {
            addNotification("No new notifications.");
        } else {
            for (String message : notifications) {
                addNotification(message);
            }
        }
    }

    //Adds a new notification to the messages screen.
    private void addNotification(String message) {
        TextView notificationText = new TextView(this);
        notificationText.setText(message);
        notificationText.setTextSize(16);
        notificationText.setPadding(16, 16, 16, 16);
        notificationText.setBackgroundResource(R.drawable.notification_background);

        //Sets margins and widths of messages.
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);

        notificationText.setLayoutParams(params);
        notificationContainer.addView(notificationText);
    }

    //Gets notifications from a list.
    private ArrayList<String> getLowStockNotifications() {
        ArrayList<String> notifications = new ArrayList<>();
        Cursor cursor = dbHelper.getInventoryItems();

        while (cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

            //Send notification is quantity is 2 or less.
            if (quantity <= 2) {
                notifications.add(itemName + " quantity is down to " + quantity + ". Order more!");
            }
        }
        cursor.close();
        return notifications;
    }
}