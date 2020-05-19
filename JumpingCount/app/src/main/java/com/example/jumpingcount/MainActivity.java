package com.example.jumpingcount;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView tvCount;
    SensorManager sm;
    Sensor ac;
    int count = 1;
    boolean up = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCount = findViewById(R.id.tvCount);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        ac = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sm.registerListener(this, ac, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            double total = Math.sqrt(Math.pow(event.values[0],2) + Math.pow(event.values[1],2) + Math.pow(event.values[2],2));

            double jump = total - 9.8;
            double down = 9.8 - total;

            if(jump > 5)
                up = true;

            if(up==true && down > 5){
                tvCount.setText(String.valueOf(count));
                count += 1;
                up = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
