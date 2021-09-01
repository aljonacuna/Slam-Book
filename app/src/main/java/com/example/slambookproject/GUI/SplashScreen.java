package com.example.slambookproject.GUI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.slambookproject.R;

public class SplashScreen extends AppCompatActivity {
    public static final String Filename = "account";
    public static final String passTag = "password";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                directto();
            }
        },1000);
    }
    public void directto(){
        sharedPreferences = getSharedPreferences(Filename, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(passTag)){
            Intent intent = new Intent(SplashScreen.this, Login.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(SplashScreen.this, Signup.class);
            startActivity(intent);
            finish();
        }
    }
}