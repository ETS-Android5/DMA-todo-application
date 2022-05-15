package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapplication.Database.DB;
import com.example.todoapplication.Models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText fullNameInput, usernameInput, passwordInput, confirmPasswordInput;
    Button registerBtn;
    TextView redirectText;

    DB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // hide title bar
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = DB.getInstance(this);

        // initialize views
        fullNameInput = findViewById(R.id.full_name_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerBtn = findViewById(R.id.register_btn);
        redirectText = findViewById(R.id.auth_redirect);

        // set on click listeners
        redirectText.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.auth_redirect:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.register_btn:
                String fullName = fullNameInput.getText().toString();
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                if(fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                    Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!password.equals(confirmPassword)){
                    Toast.makeText(this, "Your passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }else if(password.length()<5){
                    Toast.makeText(this, "Password must 5 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(database.userDAO().usernameIsTaken(username)){
                    Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(fullName, username, password);
                database.userDAO().insert(newUser);
                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();

                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
        }
    }
}