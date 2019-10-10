package com.developer.pinedo.masterapp.Client;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.developer.pinedo.masterapp.EndsplashActivity;
import com.developer.pinedo.masterapp.R;

public class PaymentSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_splash);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(PaymentSplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            };
        }, 3000);
    }
}
