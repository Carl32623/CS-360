package com.example.lalonde_inventoryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity displays a basic login screen.  The user can fill
 * in username and password fields to login to the app or create
 * a new account.
 *
 * @author Carl LaLonde
 *
 * Date: 2/23/2025
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton, createLoginButton;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        usernameInput = findViewById(R.id.UserNameInput);
        passwordInput = findViewById(R.id.PasswordInput);
        loginButton = findViewById(R.id.LoginButton);
        createLoginButton = findViewById(R.id.CreateLoginButton);
        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        //Check is user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(LoginActivity.this, ItemsActivity.class));
            finish();
        }

        //Set up onClick listeners
        loginButton.setOnClickListener(view -> loginUser());
        createLoginButton.setOnClickListener(view -> createNewAccount());
    }

    //Verifies the users username and password in the database to log them in.
    private void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        //Makes sure both username and password fields are filled out.
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        //Checks username and password with database.
        if (dbHelper.authenticateUser(username, password)) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

            //Saves user login session.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            //After successful login, user is taken to Items screen.
            startActivity(new Intent(LoginActivity.this, ItemsActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }

    //Creates a new user account.
    private void createNewAccount() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        //Makes sure password and username is filled out.
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter a username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        //Adds new user to database
        if (dbHelper.addUser(username, password)) {
            Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
        }
    }

    //Logs the user out when app is closed.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }
}
