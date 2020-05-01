package com.example.lifecycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void onClick(View v){
        if(v.getId() == R.id.btnReturn)
            goReturn();
    }

    public void goReturn(){
        Intent intent = getIntent();
        int num = intent.getIntExtra("num",0);
        int iRes = num*num;

        Intent intentReturn = new Intent(getApplicationContext(),MainActivity.class);
        intentReturn.putExtra("result", iRes);
        setResult(RESULT_OK, intentReturn);

        finish();
    }
}
