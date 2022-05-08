package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapplication.Database.DB;
import com.example.todoapplication.Models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SHARED_PREF = "shared_pref";
    public static final String USER_ID = "user_id";
    public static final String USER_ID_EXTRA = "user_id_extra";

    EditText usernameInput, passwordInput;
    Button signInBtn;
    TextView redirectText;

    DB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // hide title bar
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = DB.getInstance(this);

        // initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        signInBtn = findViewById(R.id.sign_in_btn);
        redirectText = findViewById(R.id.auth_redirect);

        // set up click listeners
        redirectText.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch(viewId){
            case R.id.auth_redirect:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.sign_in_btn:
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString();

                try {
                    User user = database.userDAO().getUserAccount(username, password);
                    saveUserDetails(user.getId());

                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch(Exception e){
                    Toast.makeText(this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void saveUserDetails(int id){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USER_ID, id);
        editor.apply();
    }
}