package com.example.fragmenttext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    FragmentManager fm;
    FragmentTransaction ft;
    LandscapeFragment lf;
    PortraitFragment pf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        lf = new LandscapeFragment();
        pf = new PortraitFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //가로, 세로 모드 확인
        Configuration config = getResources().getConfiguration();

        //매니저 필요
        //트랜잭션 필요

        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            //가로 일때
            ft.replace(android.R.id.content, lf);
        }
        else{
            ft.replace(android.R.id.content, pf);
        }

        ft.commit(); //교체
    }
}
//메인 java, xml
//추가 자바: 가로, 세로 파일 (fragment 상속)
//추가 xml: 가로, 세로 파일