package com.example.todoapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    public static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getUserId();

        ImageView applicationLogo = findViewById(R.id.splash_screen_logo);
        TextView tag = findViewById(R.id.splash_screen_tag);

        Animation logoTransition = AnimationUtils.loadAnimation(this, R.anim.splash_screen_logo);
        Animation tagTransition = AnimationUtils.loadAnimation(this, R.anim.splash_screen_tag);

        applicationLogo.startAnimation(logoTransition);
        tag.startAnimation(tagTransition);
    }

    public void getUserId(){
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(LoginActivity.USER_ID, -1);

        // add a slight delay before launching either the login or the main activity
        Handler handler = new Handler();
        handler.postDelayed(()-> {
                Intent intent;

                if(userId!=-1){
                    intent = new Intent(this, MainActivity.class);
                }else{
                    intent = new Intent(this, LoginActivity.class);
                }

                startActivity(intent);

                // do not allow to go "back" to this activity once in login/main activity
                finish();
        }, SPLASH_DELAY);
    }
}