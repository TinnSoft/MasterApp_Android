package com.developer.pinedo.masterapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.developer.pinedo.masterapp.models.CardOrders;

public class DetailOrderActivity extends AppCompatActivity {

    CardOrders cardChef=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        Bundle param=getIntent().getExtras();
        if(param != null) {
            cardChef = (CardOrders) param.getSerializable("chef");

        }
    }
}
