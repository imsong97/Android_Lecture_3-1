package com.example.listviewsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*       setContentView(R.layout.activity_main);

        ListView list = (ListView)findViewById(R.id.listview);

        //listview에 표시할 문자열
        final String mid[] = {"히어로즈", "24시", "종이의 집"};

        //ArrayAdapter에 문자열 결합
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, mid);

        //list객체에 어댑터 세팅
        list.setAdapter(adapter);

        //리스너 설정
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), mid[position], Toast.LENGTH_SHORT).show();
            }
        });
       */

        setContentView(R.layout.dynamiclist);
        final ArrayList<String> myList = new ArrayList<String>();
        ListView list = (ListView)findViewById(R.id.list1);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, myList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        final EditText edtTitle = (EditText)findViewById(R.id.edtTitle);
        Button btnAdd = (Button)findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myList.add(edtTitle.getText().toString()); //추
                adapter.notifyDataSetChanged();
            }
        });

        //제거
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                myList.remove(position);
                adapter.notifyDataSetChanged();

                return false;
            }
        });
    }
}
