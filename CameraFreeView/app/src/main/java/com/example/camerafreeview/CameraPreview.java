package com.example.camerafreeview;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        mCamera = camera;

        // SurfaceHolder.Callback 설치 -> surface가 삭제되거나 생성될 때 알림을 받기 위해
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // surface를 생성,미리보기를 그릴 위치 선정
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch(IOException e){
            Log.d(TAG, "Error setting camera preview : "+ e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if(mHolder.getSurface() == null) // 미리보기를 위한 surface가 없는 경우
            return;

        // 변경 전 미리보기 중지
        try{
            mCamera.stopPreview();
        }catch(Exception e){

        }

        // 미리보기 크기 설정, 크기 조정, 회전 또는 형식 변경 코드
        mHolder.setFixedSize(width, height);

        // 새로운 설정으로 미보기
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch(IOException e){
            Log.d(TAG, "Error setting camera preview : "+ e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.release();
        mCamera = null;
    }
}
