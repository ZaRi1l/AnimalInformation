package com.example.animalinformation;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.widget.ImageView;


public class LoadingActivity extends AppCompatActivity {
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}