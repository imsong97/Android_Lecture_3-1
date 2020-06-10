package com.example.camerafreeview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Camera;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Camera c;
    private CameraPreview cp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c = getCameraInstance();
    }
}