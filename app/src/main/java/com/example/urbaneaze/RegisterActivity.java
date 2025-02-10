package com.example.urbaneaze;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameField, emailField, phoneField, flatNoField, societyIdField, passwordField, confirmPasswordField;
    private Spinner roleSpinner;
    private Button registerButton;
    private String selectedRole = "resident"; // Default role
    private UserAuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI Components
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        phoneField = findViewById(R.id.phoneField);
        flatNoField = findViewById(R.id.flatNoField);
        societyIdField = findViewById(R.id.societyIdField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        roleSpinner = findViewById(R.id.roleSpinner);
        registerButton = findViewById(R.id.registerButton);

        // Initialize Database Helper
        authHelper = new UserAuthHelper(this);

        // Set up Role Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Listener for Role Selection
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = "resident";
            }
        });

        // Register Button Click
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String userId = UUID.randomUUID().toString();
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String flatNo = flatNoField.getText().toString().trim();
        String societyId = societyIdField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        // Input Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || flatNo.isEmpty() || societyId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("All fields are required!");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match!");
            return;
        }
        if (password.length() < 6) {
            showToast("Password must be at least 6 characters!");
            return;
        }

        // Store Data in SQLite
        boolean isRegistered = authHelper.registerUser(userId, name, email, phone, flatNo, selectedRole, societyId, password);
        if (isRegistered) {
            showToast("Registration Successful!");
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            showToast("Email already exists!");
        }
    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
