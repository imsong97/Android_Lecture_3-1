package com.example.motionsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

// 모션센서에는 허가권 요청이 필요없음 -> manifest변경X, 허가권코드X
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView x_gravity, y_gravity, z_gravity, total_gravity;
    TextView x_ac, y_ac, z_ac;
    TextView x_lac, y_lac, z_lac;
    TextView x_gy, y_gy, z_gy;

    SensorManager sm; // 센서 관리자
    Sensor s_gravity, s_ac, s_lac, s_gy; // 모션센서 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x_gravity = findViewById(R.id.x_gravity);
        y_gravity = findViewById(R.id.y_gravity);
        z_gravity = findViewById(R.id.z_gravity);
        total_gravity = findViewById(R.id.total_gravity);

        x_ac = findViewById(R.id.x_ac);
        y_ac = findViewById(R.id.y_ac);
        z_ac = findViewById(R.id.z_ac);

        x_lac = findViewById(R.id.x_lac);
        y_lac = findViewById(R.id.y_lac);
        z_lac = findViewById(R.id.z_lac);

        x_gy = findViewById(R.id.x_gy);
        y_gy = findViewById(R.id.y_gy);
        z_gy = findViewById(R.id.z_gy);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
