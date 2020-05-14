package com.example.motionsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// 모션센서에는 허가권 요청이 필요없음
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
