package com.hackthon.jejuhackathon.src;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hackthon.jejuhackathon.R;
import com.hackthon.jejuhackathon.src.login.LogInActivity;
import com.hackthon.jejuhackathon.src.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
