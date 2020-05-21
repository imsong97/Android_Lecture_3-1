package com.example.sensorlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tvList;
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvList = findViewById(R.id.tvList);

        //센서 매니저 객체
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        //센서 목록
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);

        //결과출력을 위한 반복문
        res = "[전체 센서 수] " + sensors.size() + "\n\n";
        int num = 0;
        for(Sensor s:sensors)
            res += ++num + ") 센서 이름: " + s.getName() + "\n";

        //결과표시
        tvList.setText(res);
    }
}
