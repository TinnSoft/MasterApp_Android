package com.developer.pinedo.masterapp.Supplier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.adapter.CustomListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectCategoryActivity extends AppCompatActivity {

    Button btn_confirm;
    private ExpandableListView listView;
    private CustomListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        listView = findViewById(R.id.listViewCat);
        initData();
        listAdapter = new CustomListAdapter(this,listDataHeader,listHash);
        listView.setAdapter(listAdapter);


        btn_confirm = findViewById(R.id.btn_confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                result.putExtra("subcategory","1");
                setResult(RESULT_OK,result);
                finish();
            }
        });
    }

    private void initData(){
        listDataHeader = new ArrayList<>();
        listHash=new HashMap<>();

        listDataHeader.add("test1");
        listDataHeader.add("test2");

        List<String> test1 = new ArrayList<>();
        test1.add("Test 1");

        List<String> test2 = new ArrayList<>();
        test2.add("Test 22");
        test2.add("Test 2222");

        listHash.put(listDataHeader.get(0),test1);
        listHash.put(listDataHeader.get(1),test2);

    }
}
