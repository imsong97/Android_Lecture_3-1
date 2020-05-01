package com.example.lifecycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText edtNum;
    TextView txtReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNum = findViewById(R.id.edtNum);
        txtReturn = findViewById(R.id.txtReturn);
    }

    public void onClick(View v){
        if(v.getId() == R.id.btnSend)
            sendValue();
    }

    public void sendValue(){
        try{
            int num = Integer.parseInt(edtNum.getText().toString());
            Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
            intent.putExtra("num",num);
            startActivityForResult(intent,0);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"숫자를 입력하세요",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        int res = data.getIntExtra("result",0);
        txtReturn.setText("반환값: " + res);
    }
}
