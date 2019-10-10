package com.developer.pinedo.masterapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.adapter.CardAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardClient;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.lite.core.MercadoPagoServices;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    StringRequest stringRequest;
    private static final String IP = Utils.IP;
    RecyclerView recycler_card;
    CardAdapterRecicleView cardAdapterRecicleView;
    ArrayList<CardClient> pictures = new ArrayList<>();

    private static final int REQUEST_CODE = 100;
    private static final int RESULT_CANCELED = 101;

    MercadoPagoCheckout checkout;
    MercadoPagoServices mercadoPagoServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        showToolbar("Mis tarjetas",true);

        requestQueue = Volley.newRequestQueue(this);


        recycler_card = findViewById(R.id.recycler_card);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_card.setLayoutManager(linearLayoutManager);


        mercadoPagoServices = new MercadoPagoServices.Builder()
                .setContext(this)
                .setPublicKey("TEST-2ef9627a-353c-48e4-8782-cde57e6f8875")
                .build();


        getCard(Utils.AccessToken(this));

    }

    public void submit(View view){


/*
        mercadoPagoServices.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {

            }

            @Override
            public void failure(ApiException apiException) {

            }
        });
*/



    }



    private void getCard(final String access_token_local) {
        String url=IP+"/api/card";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    pictures.removeAll(pictures);

                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("results");

                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = null;
                        jsonObject = jsonArray.getJSONObject(i);

                        pictures.add(new CardClient(jsonObject.getString("id"),jsonObject.getString("number_card"),jsonObject.getString("titular")));
                    }


                    cardAdapterRecicleView = new CardAdapterRecicleView(pictures,R.layout.card_item_card,CardActivity.this);
                    recycler_card.setAdapter(cardAdapterRecicleView);
                }catch (JSONException e){

                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + access_token_local);
                return params;
            }
        };

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //toolbar.setTitleTextColor(0x000000);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

    }

}
