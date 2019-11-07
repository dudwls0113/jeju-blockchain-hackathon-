package com.hackthon.jejuhackathon.src.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.SplashActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);

        setContentView(R.layout.activity_main);
    }
}
