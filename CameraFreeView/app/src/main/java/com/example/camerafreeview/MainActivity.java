package com.example.camerafreeview;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
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
    private Button btnCapture, btnFlash, btnVideo;
    private boolean flash = false;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

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

        btnFlash = findViewById(R.id.btnFlash);
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(!flash){ // 초기 false상태 -> 꺼져있음
                    Toast.makeText(getApplicationContext(), "플래쉬 켜짐", Toast.LENGTH_SHORT).show(); // 상태 알림
                    playFlash(flash);
                    btnFlash.setText("Flash OFF"); // 텍스트바꾸기,  텍스트와 상태는 반대: off상태 -> 텍스트는 on표시
                    flash = true; //상태 바꾸고
                }
                else{
                    Toast.makeText(getApplicationContext(), "플래쉬 꺼짐", Toast.LENGTH_SHORT).show();
                    playFlash(flash);
                    btnFlash.setText("Flash ON");
                    flash = false;
                }
            }
        });

        btnVideo = findViewById(R.id.btnVideo);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording){ // 녹화 중지
                    mediaRecorder.stop(); // 중지
                    releaseMediaRecorder(); //미디어레코드 객체 해제
                    mCamera.lock(); // 카메라 접근 잠금

                    btnVideo.setText("Video");
                    isRecording = false;
                }
                else{ // 영상 촬영
                    if (prepareVideoRecorder()) { // 카메라가 사용가능 & 잠금이 해제되면 녹화 시작 가능
                        mediaRecorder.start(); // 시작

                        btnVideo.setText("Recording");
                        isRecording = true;
                    } else // 준비가 안되면 객체 해제
                        releaseMediaRecorder();

                }
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
        releaseMediaRecorder();
        releaseCamera();
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private void hideSystemUI(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // 카메라 하드웨어 감지
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
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
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

    private void playFlash(boolean flashMode){
        Camera.Parameters params = mCamera.getParameters();
        if(!flashMode)
            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        else
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        mCamera.setParameters(params);
        mCamera.startPreview();
    }

    private boolean prepareVideoRecorder(){
        mCamera = getCameraInstance();
        mediaRecorder = new MediaRecorder();

        // 1. 카메라 언락 후 미디어 레코드에 카메라 객체 설정
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        // 2. 오디오 비디오 소스 설정
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 3. 캠코더프로파일을 통해 동영상 출력 형식과 인코딩 설정(API 8이상 요구)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // 4. 동영상 파일 설정
        mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // 5. 미디어 레코더에 미리보기 화면을 설정
        mediaRecorder.setPreviewDisplay(cp.getHolder().getSurface());

        // 6. 미디어 레코드 준비
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("TAG", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("TAG", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;
    }
}