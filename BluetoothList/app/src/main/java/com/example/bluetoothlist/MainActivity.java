package com.example.bluetoothlist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int ACTION_REQUEST_ENABLE = 1;
    public static String EXTRA_DEVICE_ADDRESS = "device address"; // 반환되는 인텐트 정보 출력을 위한 문자열

    // 블루투스 장치 어댑터
    private BluetoothAdapter ba = null;

    // 장치 관리 목록 리스트
    private ArrayAdapter<String> device;
    private ArrayAdapter<String> paired;

    ListView newList, pairedList;

    private Button btnScan = null;
    private Button btnDiscoverable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.scan);
        btnDiscoverable = findViewById(R.id.discoverable);

        btnScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //검색 기능 수행
            }
        });
        btnDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //자신의 장치 탐색 허용
            }
        });

        //ArrayAdapter 초기화
        paired = new ArrayAdapter<String>(this, R.layout.device_name); // 이미 페어링 된 장치용터 어댑터
        device = new ArrayAdapter<String>(this, R.layout.device_name); // 새로발견한 장치용 어댑터

        // 리스트뷰
        pairedList = findViewById(R.id.paired);
        pairedList.setAdapter(paired);
        pairedList.setOnItemLongClickListener((AdapterView.OnItemLongClickListener)mDeviceClickListener);
        newList = findViewById(R.id.search);
        newList.setAdapter(device);
        newList.setOnItemLongClickListener((AdapterView.OnItemLongClickListener)mDeviceClickListener);

        // 로컬 블루투스 어댑터 획득
        ba = BluetoothAdapter.getDefaultAdapter();
        if(ba == null){
            // 블루투스를 지원하지 않는 장치의 경우 처리작업
            Toast.makeText(getApplicationContext(), "미지원 기기", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 블루투스가 꺼져있을 경우 활성화 되도록 요청
        if(!ba.isEnabled()){
            //활성화
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, ACTION_REQUEST_ENABLE);
        }

        //브로드캐스트 리시버 수신기 등록처리
        //장치발견 시 브로드캐스트 리시버 등록
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        //검색이 완료되면 브로드캐스트 리시버 등록
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 현재 페어링 된 장치 세트를 가져와서 리스트에 추가
        Set<BluetoothDevice> pairedDevice = ba.getBondedDevices();

        paired.clear();

        if(pairedDevice.size() > 0){ // 페어링 된 장치가 있는 경우 각 장치를 추가
            findViewById(R.id.title2).setVisibility(View.VISIBLE);
            for(BluetoothDevice device : pairedDevice){
                paired.add(device.getName() + "\n" + device.getAddress());
            }
        }
        else{ // 페어링 된 장치가 없는 경우
            String noDevice = "페어링 된 장치가 없습니다.".toString();
            paired.add(noDevice);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 장치 검색이 수행중이면 중단
        if(ba != null)
            ba.cancelDiscovery();

        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
            Toast.makeText(getApplicationContext(), "블루투스 활성화 완료", Toast.LENGTH_SHORT).show();
        else{
            Toast.makeText(getApplicationContext(), "블루투스 활성화 실패", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void doDiscovery(){

    }

    public void ensureDiscoverable(){

    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
