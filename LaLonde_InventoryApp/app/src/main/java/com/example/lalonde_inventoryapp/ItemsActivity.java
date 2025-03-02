package com.example.lalonde_inventoryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * This activity displays the list of inventory items in a grid layout.
 * This is the first screen users will see after a successful login.
 *
 * @author Carl LaLonde
 *
 * Date: 2/23/2025
 */
public class ItemsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private GridLayout gridLayout;
    private ArrayList<Item> itemsList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_screen);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        gridLayout = findViewById(R.id.gridlayout);
        itemsList = new ArrayList<>();

        // Initialize buttons
        Button itemsButton = findViewById(R.id.itemsButton);
        Button addItemButton = findViewById(R.id.addItemButton);
        Button messagesButton = findViewById(R.id.messagesButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set up click listeners
        itemsButton.setOnClickListener(view -> openActivity(ItemsActivity.class));
        addItemButton.setOnClickListener(view -> openActivity(AddNewItemActivity.class));
        messagesButton.setOnClickListener(view -> openActivity(MessagesActivity.class));
        logoutButton.setOnClickListener(view -> logoutUser());

        // Load and display inventory items
        loadInventory();
    }

    // Opens the correct activity when a button is clicked.
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(ItemsActivity.this, activityClass);
        startActivity(intent);
    }

    // Logs out the user.
    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        startActivity(new Intent(ItemsActivity.this, LoginActivity.class));
        finish();
    }

    // Load inventory items from the database and add them to a list.
    private void loadInventory() {
        itemsList.clear(); // Clear existing list to prevent duplication
        Cursor cursor = dbHelper.getInventoryItems();

        // If no items are found.
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No items found!", Toast.LENGTH_SHORT).show();
        }

        // Creates new item objects.
        while (cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndex("item_id"));
            String itemName = cursor.getString(cursor.getColumnIndex("name"));
            String itemDescription = cursor.getString(cursor.getColumnIndex("description"));
            int itemQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));

            itemsList.add(new Item(itemId, itemName, itemDescription, itemQuantity));
        }
        cursor.close(); // Close the cursor.

        populateGridLayout(); // Display the items.
    }

    // Displays grid inventory items from the list.
    private void populateGridLayout() {
        gridLayout.removeAllViews(); // Clear grid layout before repopulating

        // Go through the list.
        for (Item item : itemsList) {
            View itemView = getLayoutInflater().inflate(R.layout.grid_item, gridLayout, false);

            // Sets item name and quantity.
            TextView itemName = itemView.findViewById(R.id.itemName);
            TextView itemQuantity = itemView.findViewById(R.id.itemQuantity);

            itemName.setText(item.getName());
            itemQuantity.setText("Qty: " + item.getQuantity());

            // Sets onClick listeners to open item in ItemDetailsActivity.
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(ItemsActivity.this, ItemDetailsActivity.class);
                intent.putExtra("itemId", item.getId());
                intent.putExtra("itemName", item.getName());
                intent.putExtra("itemDescription", item.getDescription());
                intent.putExtra("itemQuantity", item.getQuantity());
                startActivity(intent);
            });

            gridLayout.addView(itemView);
        }
    }

    //Reloads inventory when returning to the screen.
    @Override
    protected void onResume() {
        super.onResume();
        loadInventory(); // Refresh inventory when user returns
    }
}