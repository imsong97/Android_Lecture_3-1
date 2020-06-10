package com.example.camerafreeview;

import android.app.Activity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class CheckPermissionActicity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //사용자 os버전 검사 -> 23이상 버전인지 판단 -> 허가권요청정보 표시 유무
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            int permissionCheck = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCheck2 = checkSelfPermission(Manifest.permission.CAMERA);

            if(permissionCheck== PackageManager.PERMISSION_DENIED || permissionCheck2==PackageManager.PERMISSION_DENIED){ //PERMISSION_DENIED -> 거부
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 1000);
                else
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 1000);
            }
            else{
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1000){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_DENIED){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(this, "권한 요청을 거부하였습니다", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "권한 설정 실패, 앱 종료", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
