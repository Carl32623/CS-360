package com.example.lalonde_inventoryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity allows the user to add new items to their inventory.
 * Users can fill out a name, description, category for the item and
 * the quantity for the item.
 *
 * @author Carl LaLonde
 *
 * Date: 2/23/2025
 */
public class AddNewItemActivity extends AppCompatActivity {

    private EditText itemNameInput, itemDescriptionInput, itemCategoryInput;
    private TextView quantityValue;
    private int quantity = 0;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item_screen);

        //Initialize database and save data.
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        //Initialize user interface elements.
        itemNameInput = findViewById(R.id.itemNameInput);
        itemDescriptionInput = findViewById(R.id.itemDescriptionInput);
        itemCategoryInput = findViewById(R.id.categoryInput);
        quantityValue = findViewById(R.id.quantityValue);

        //Initialize user interface buttons.
        Button decreaseQuantityButton = findViewById(R.id.decreaseButton);
        Button increaseQuantityButton = findViewById(R.id.increaseButton);
        Button submitItemButton = findViewById(R.id.submitButton);
        ImageView backButton = findViewById(R.id.backButton);

        //Initialize bottom navigation bar buttons.
        Button itemsButton = findViewById(R.id.itemsButton);
        Button messagesButton = findViewById(R.id.messagesButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set up click listeners
        itemsButton.setOnClickListener(view -> openActivity(ItemsActivity.class));
        messagesButton.setOnClickListener(view -> openActivity(MessagesActivity.class));
        logoutButton.setOnClickListener(view -> logoutUser());

        //Set up onCLick listeners.
        decreaseQuantityButton.setOnClickListener(view -> decreaseQuantity());
        increaseQuantityButton.setOnClickListener(view -> increaseQuantity());
        submitItemButton.setOnClickListener(view -> submitNewItem());
        backButton.setOnClickListener(view -> onBackPressed());
    }

    //Opens the correct activity when a button is clicked.
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(AddNewItemActivity.this, activityClass);
        startActivity(intent);
    }

    //Logs out the user.
    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        startActivity(new Intent(AddNewItemActivity.this, LoginActivity.class));
        finish();
    }

    //Decrease the quantity.
    private void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
            quantityValue.setText(String.valueOf(quantity));
        }
    }

    //Increase the quantity.
    private void increaseQuantity() {
        quantity++;
        quantityValue.setText(String.valueOf(quantity));
    }

    //Adds new item to database.
    private void submitNewItem() {
        String name = itemNameInput.getText().toString().trim();
        String description = itemDescriptionInput.getText().toString().trim();
        String category = itemCategoryInput.getText().toString().trim();

        //Makes sure the Name, Description, and Category are filled out.
        if (name.isEmpty() || description.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //Inserts new item into the database.
        boolean success = dbHelper.addItem(name, description, category, quantity);
        if (success) {
            Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddNewItemActivity.this, ItemsActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
        }
    }
}