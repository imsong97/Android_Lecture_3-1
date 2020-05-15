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

        // 장치의 센서객체 생성
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);

        // 각 종류별 센서객체 생성
        s_gravity = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        s_ac = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        s_lac = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        s_gy = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    // 액티비티 생명주기에서, 화면이 활성화 될 때 센서값 모니터링 시작
    @Override
    protected void onResume() {
        super.onResume();

        // 센서 등록
        sm.registerListener(this, s_gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, s_ac, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, s_lac, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, s_gy, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // 화면 비활성화 때 모니터링 중지
    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this); // 센서 등록 해제
    }

    // 센서값이 바뀔 때 함수 자동 호출 -> event에 상태값이 전달됨
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_GRAVITY:
                x_gravity.setText("X: " + String.format("%.2f", event.values[0]));
                y_gravity.setText("Y: " + String.format("%.2f", event.values[1]));
                z_gravity.setText("Z: " + String.format("%.2f", event.values[2]));
                double total = Math.sqrt(Math.pow(event.values[0],2) + Math.pow(event.values[1],2) + Math.pow(event.values[2],2));
                total_gravity.setText("Total: " + String.format("%.2f", total));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                x_ac.setText("X: " + String.format("%.2f", event.values[0]));
                y_ac.setText("Y: " + String.format("%.2f", event.values[1]));
                z_ac.setText("Z: " + String.format("%.2f", event.values[2]));
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                x_lac.setText("X: " + String.format("%.2f", event.values[0]));
                y_lac.setText("Y: " + String.format("%.2f", event.values[1]));
                z_lac.setText("Z: " + String.format("%.2f", event.values[2]));
                break;
            case Sensor.TYPE_GYROSCOPE:
                x_gy.setText("X: " + String.format("%.2f", event.values[0]));
                y_gy.setText("Y: " + String.format("%.2f", event.values[1]));
                z_gy.setText("Z: " + String.format("%.2f", event.values[2]));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
