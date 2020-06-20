package com.example.camerafreeview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview cp;
    private Button btnCapture;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        //카메라로부터 이미지 얻기
                        mCamera.takePicture(null, null, mPicture);
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 카메라 인스턴스 가져와서 멤버에 설정
        mCamera = getCameraInstance();

        if(checkAutoFocusFunction()){
            setAutoFocusFunction(); // 사진 촬영시 자동초점
            setAutoFocusPreview(); // 미리보기에 자동초점
        }
        else
            Toast.makeText(getApplicationContext(), "자동 초점 기능 미지원 기기", Toast.LENGTH_SHORT).show();

        if(checkCameraHardware(this)){
            cp = new CameraPreview(this, mCamera);
            FrameLayout preview = findViewById(R.id.camera_preview);
            preview.addView(cp);

            hideSystemUI();
        }
        else
            Toast.makeText(getApplicationContext(), "카메라 미지원, 앱 종료", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //카메라 해제코드
    }

    private void hideSystemUI(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // 카메라 하드웨어 감
    private boolean checkCameraHardware(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return true; // 카메라 있음
        else
            return false; // 카메라 없음
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
        }catch(Exception e){

        }
        return c;
    }

    // JPEG 콜백 객체
    private Camera.PictureCallback mPicture = new Camera.PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

            if(pictureFile == null){
                Log.d("MyCameraApp", "미디어파일 생성 실패, 권한을 확인하세요");
                return;
            }

            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.flush();
                fos.close();
                mCamera.startPreview();
            }catch(FileNotFoundException e){
                Log.d("MyCameraApp", "해당 파일이 없습니다");
                e.printStackTrace();
            }catch(IOException e){
                Log.d("MyCameraApp", "파일 접근 오류");
                e.printStackTrace();
            }
        }
    };

    // 이미지 또는 비디오 저장을 위한 파일 객체 생성
    private static File getOutputMediaFile(int type){
        // 안전을 위해 SDcard가 마운트되었는지 확인
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");
        // 생성된 이미지를 응용프로그램간에 공유하고 앱을 제거한 후에도 유지하려는 경우에 적합

        // 저장 디렉토리가 없으면 생성
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // 미디어 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaStorageDir;
    }

    private boolean checkAutoFocusFunction(){
        boolean isOK = false;

        Camera.Parameters params = mCamera.getParameters();
        List<String> focus = params.getSupportedFocusModes();

        if(focus.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            isOK = true;

        return isOK;
    }

    private void setAutoFocusFunction(){
        Camera.Parameters params = mCamera.getParameters();

        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        mCamera.setParameters(params);
    }

    private void setAutoFocusPreview(){
        Camera.Parameters params = mCamera.getParameters();

        if(params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)){
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setParameters(params);
        }
    }
}