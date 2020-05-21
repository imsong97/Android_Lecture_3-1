package com.example.bluetoothlist;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_REQUEST_ENABLE = 1;
    public static String EXTRA_DEVICE_ADDRESS = "device address"; // 반환되는 인텐트 정보 출력을 위한 문자열

    // 블루투스 장치 어댑터
    private BluetoothAdapter ba;

    // 장치 관리 목록 리스트
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> paired;

    ListView newList, pairedList;

    private Button btnScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.scan);
        btnScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //검색 기능 수행
            }
        });

        //ArrayAdapter 초기화
        paired = new ArrayAdapter<String>(this, R.layout.device_name);
        adapter = new ArrayAdapter<String>(this, R.layout.device_name);

        pairedList = findViewById(R.id.paired);
        pairedList.setAdapter(paired);

        newList = findViewById(R.id.search);
        newList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
