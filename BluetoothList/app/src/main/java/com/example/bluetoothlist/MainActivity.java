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
import android.widget.TextView;
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
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });
        btnDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //자신의 장치 탐색 허용
                ensureDiscoverable();
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
        // 제목 변경
        setTitle("장치 스캐닝 중....");

        // 새 장치의 부제목 표시
        findViewById(R.id.title2).setVisibility(View.VISIBLE);

        // 이미 장치를 발견한 경우 스캐닝 중지
        if(ba.isDiscovering())
            ba.cancelDiscovery();

        // 장치 검색 요청
        ba.startDiscovery();
    }

    public void ensureDiscoverable(){
        if(ba.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
            startActivity(intent);
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ba.cancelDiscovery(); // 연결 작업을 위해 검색을 취소

            // 장치 이름과 MAC주소 얻기
            String info = ((TextView)view).getText().toString();
            String address = info.substring(info.length() - 17);

            // 연결을 위한 코드
            Toast.makeText(getApplicationContext(), address + "로 연결합니다", Toast.LENGTH_SHORT).show();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 기기 검색 상태 브로드캐스트 수신 처리
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // Intent에서 장치 객체 가져오기

                if(d.getBondState() != BluetoothDevice.BOND_BONDED)  // 목록에 없는경우 검색기기의 목록 추가
                   device.add(d.getName() + "\n" + d.getAddress());

            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){ // 기기 검색 완료 상태인 브로드캐스트 수신 시 처리
                if(device.getCount() == 0){
                    String noDevices = "검색된 장치가 없습니다".toString();
                    setTitle(noDevices);
                    btnScan.setVisibility(View.VISIBLE);
                    device.add(noDevices);
                }
                else
                    setTitle("연결할 장치를 선택하세요");
            }
        }
    };
}
