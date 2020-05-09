package com.example.tmaplocation;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout mapView;
    Button btnZoomIn, btnZoomOut, btnMyLocation, btnSearch;
    EditText edtSearch;
    TMapView tMapView;
    TMapData tMapData;

    ArrayList<TMapPOIItem> poiResult;
    LocationManager locationManager;
    Bitmap rightButton;
    BitmapFactory.Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentView();
        initInstance();
        eventListener();
    }

    // 위젯변수 및 필요객체 등의 맴버변수 초기화
    private void intentView(){
        mapView = findViewById(R.id.mapView);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.edtSearch);

//        options.inSampleSize = 16;
//        rightButton = new BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow, options);
    }

    // 필요 객체 변수 인스턴스화
    private void initInstance(){
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xx116aec2087874bc3bdf51d6e01802871");
        mapView.addView(tMapView);
        tMapData = new TMapData();

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        poiResult = new ArrayList<>();
    }

    // 각 버튼 이벤트리스너 등록
    private void eventListener(){
        btnZoomIn.setOnClickListener(listener);
        btnZoomOut.setOnClickListener(listener);
        btnSearch.setOnClickListener(listener);
        btnMyLocation.setOnClickListener(listener);
    }
    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSearch:
                    String strData = edtSearch.getText().toString();
                    if(!strData.equals(""))
                        searchPOI(strData);
                    else
                        Toast.makeText(getApplicationContext(), "검색어를 입력하세요",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnZoomIn:
                    tMapView.MapZoomIn();
                    break;
                case R.id.btnZoomOut:
                    tMapView.MapZoomOut();
                    break;
                case R.id.btnMyLocation:
                    try{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
                    }catch(SecurityException e){
                    }
                    break;
            }
        }
    };

    // 위치 수신자 인스턴스화
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            tMapView.setCenterPoint(lon,lat);
            tMapView.setLocationPoint(lon, lat);
            tMapView.setIconVisibility(true);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    // 마커 설정
    private void setMarker(String name, double lat, double lon){

    }

    // 통합검색
    private void searchPOI(String strData){
        tMapData.findAllPOI(strData, (TMapData.FindAllPOIListenerCallback) (arrayList)->{
            poiResult.addAll(arrayList);

            tMapView.setCenterPoint(arrayList.get(0).getPOIPoint().getLongitude(), arrayList.get(0).getPOIPoint().getLatitude(), true);

            for(int i=0; i<arrayList.size(); i++){
                TMapPOIItem item = (TMapPOIItem) arrayList.get(i);
                Log.d("POI Name:", item.getPOIName().toString()+", "
                        +"Address: "+item.getPOIAddress().replace("null","")+", "
                        +"Point: "+item.getPOIPoint().toString()
                        +"Contents: " +item.getPOIContent());
                TMapMarkerItem markerItem = new TMapMarkerItem();
                markerItem.setTMapPoint(item.getPOIPoint());
                markerItem.setCalloutTitle(item.getPOIName());
                markerItem.setCalloutSubTitle(item.getPOIAddress());
                markerItem.setCanShowCallout(true);

                tMapView.addMarkerItem(item.getPOIName(), markerItem);
            }
        });
    }
}
