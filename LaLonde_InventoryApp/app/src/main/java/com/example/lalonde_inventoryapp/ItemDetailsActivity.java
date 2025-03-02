package com.example.lalonde_inventoryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * This activity allows users to see details about an inventory item.
 * Users also have the option to increase, decrease, or remove an item from inventory.
 *
 * @author Carl LaLonde
 *
 * Date: 2/23/2025
 */
public class ItemDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView itemNameTextView, itemQuantityTextView, itemDescriptionTextView;
    private int itemId;
    private String itemName, itemDescription;
    private int itemQuantity;
    private SharedPreferences sharedPreferences;
    private Button updateItemButton;
    private Button removeItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details_screen);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Initialize bottom navigation bar buttons.
        Button itemsButton = findViewById(R.id.itemsButton);
        Button addItemButton = findViewById(R.id.addItemButton);
        Button messagesButton = findViewById(R.id.messagesButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set up onClick listeners for bottom navigation buttons.
        itemsButton.setOnClickListener(view -> openActivity(ItemsActivity.class));
        addItemButton.setOnClickListener(view -> openActivity(AddNewItemActivity.class));
        messagesButton.setOnClickListener(view -> openActivity(MessagesActivity.class));
        logoutButton.setOnClickListener(view -> logoutUser());

        // Initialize user interface items.
        itemNameTextView = findViewById(R.id.itemName);
        itemQuantityTextView = findViewById(R.id.quantityValue);
        itemDescriptionTextView = findViewById(R.id.itemDescription);
        Button decreaseQuantityButton = findViewById(R.id.decreaseButton);
        Button increaseQuantityButton = findViewById(R.id.increaseButton);
        updateItemButton = findViewById(R.id.updateItemButton);
        removeItemButton = findViewById(R.id.removeItemButton);
        ImageView backButton = findViewById(R.id.backButton);

        // Disable update button initially
        updateItemButton.setEnabled(false);
        updateItemButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));

        // Get item details.
        itemId = getIntent().getIntExtra("itemId", -1);
        itemName = getIntent().getStringExtra("itemName");
        itemDescription = getIntent().getStringExtra("itemDescription");
        itemQuantity = getIntent().getIntExtra("itemQuantity", 0);

        // Display item details.
        itemNameTextView.setText(itemName);
        itemDescriptionTextView.setText(itemDescription); // Fixed typo
        itemQuantityTextView.setText(String.valueOf(itemQuantity));

        // Decrease quantity button.
        decreaseQuantityButton.setOnClickListener(view -> {
            if (itemQuantity > 0) {
                itemQuantity--;
                itemQuantityTextView.setText(String.valueOf(itemQuantity));
                enableUpdateButton();
            }
        });

        // Increase quantity button.
        increaseQuantityButton.setOnClickListener(view -> {
            itemQuantity++;
            itemQuantityTextView.setText(String.valueOf(itemQuantity));
            enableUpdateButton();
        });

        // Update item quantity in the database.
        updateItemButton.setOnClickListener(view -> {
            if (itemId == -1) {
                Toast.makeText(this, "Error: Item ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if there are any changes before updating.
            if (itemQuantity == getIntent().getIntExtra("itemQuantity", 0)) {
                Toast.makeText(this, "No changes detected.", Toast.LENGTH_SHORT).show();
                updateItemButton.setEnabled(false);
                updateItemButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
                return;
            }

            boolean updated = dbHelper.updateItemQuantity(itemId, itemQuantity);
            if (updated) {
                Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("itemUpdated", true);
                setResult(RESULT_OK, resultIntent); // Notify previous activity of update
                finish();
            } else {
                Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove an item from inventory.
        removeItemButton.setOnClickListener(view -> {
            if (itemId == -1) {
                Toast.makeText(this, "Error: Item ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean deleted = dbHelper.deleteItem(itemId);
            if (deleted) {
                Toast.makeText(this, "Item removed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Item removal failed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button to return to the previous screen.
        backButton.setOnClickListener(view -> onBackPressed());
    }

    // Opens the correct activity when a button is clicked.
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(ItemDetailsActivity.this, activityClass);
        startActivity(intent);
    }

    // Logs out the user.
    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        startActivity(new Intent(ItemDetailsActivity.this, LoginActivity.class));
        finish();
    }

    // Enables the update button and sets its color to match the remove button.
    private void enableUpdateButton() {
        updateItemButton.setEnabled(true);
        updateItemButton.setBackgroundTintList(removeItemButton.getBackgroundTintList());
    }
}