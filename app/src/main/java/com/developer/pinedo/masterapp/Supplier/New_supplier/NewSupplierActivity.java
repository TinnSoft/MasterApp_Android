package com.developer.pinedo.masterapp.Supplier.New_supplier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Supplier.ConvertToSupplierActivity;

public class NewSupplierActivity extends AppCompatActivity {

     Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_supplier);

        btn_next=findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(NewSupplierActivity.this,ConvertToSupplierActivity.class);
                startActivity(i);
            }
        });
    }
}
