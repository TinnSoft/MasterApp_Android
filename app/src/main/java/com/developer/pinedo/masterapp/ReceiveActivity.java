package com.developer.pinedo.masterapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.Client.MainActivity;

import com.developer.pinedo.masterapp.Client.PaymentSplashActivity;
import com.developer.pinedo.masterapp.adapter.ChefAdapterRecicleView;
import com.developer.pinedo.masterapp.adapter.OrderReviewAdapterRecicleView;
import com.developer.pinedo.masterapp.adapter.OrdersAdapterRecicleView;
import com.developer.pinedo.masterapp.database.App;
import com.developer.pinedo.masterapp.entities.Product;
import com.developer.pinedo.masterapp.models.CardChef;
import com.developer.pinedo.masterapp.models.CardOrders;
import com.developer.pinedo.masterapp.models.OrdersReview;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ReceiveActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ReceiveActivity";

    TextView tv_supplier,tv_subtotal,btn_cancel,tv_address,ed_description,tv_restriction,tv_date_service;
    GoogleApiClient googleApiClient;
    ImageView im_supplier;
    private static final String IP = Utils.IP;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    Activity activity;
    EditText ed_reason;
    String reason, response="";
    int order_id;
    RecyclerView recyclerView;
    Button btn_payment;
    App db;


    MercadoPagoCheckout checkout;

    String date_service,hour_service,restriction_alimentary,address,list_products;

    ArrayList<OrdersReview> pictures = new ArrayList<>();


    private static final int REQUEST_CODE = 100;
    private static final int RESULT_CANCELED = 101;

    private OrderReviewAdapterRecicleView ordersAdapterRecicleView;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        activity = this;
        showToolbar("",true);

        btn_payment = findViewById(R.id.btn_payment);
        tv_address = findViewById(R.id.tv_address);
        tv_restriction = findViewById(R.id.tv_restriction);
        ed_description = findViewById(R.id.ed_description);
        btn_cancel = findViewById(R.id.btn_cancel);
        tv_date_service = findViewById(R.id.tv_date_service);
        btn_payment.setOnClickListener(this);

        tv_supplier=findViewById(R.id.tv_supplier);
        tv_subtotal=findViewById(R.id.tv_subtotal);
        im_supplier=findViewById(R.id.im_supplier);


        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent i = getIntent();

        if(i.getExtras() != null){
            CardOrders row = (CardOrders) i.getSerializableExtra("order");
            tv_address.setText(row.getAddress());
            ed_description.setText("");
            tv_date_service.setText(row.getDate_service_formated());
            tv_restriction.setText(row.getRestriction());
            tv_supplier.setText(row.getSupplier_short());
            tv_subtotal.setText("$"+row.getSubtotal());
            order_id = row.getId();
            getOrderDetail(order_id);

           Glide
                    .with(getApplicationContext())
                    .load(i.getExtras().get("url"))
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(im_supplier);

            if(row.getStatus_id().equals("3")){
                btn_cancel.setVisibility(View.GONE);
                btn_payment.setVisibility(View.GONE);
            }else if(row.getStatus_id().equals("2")){
                btn_payment.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.VISIBLE);
            }else{
                btn_payment.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.VISIBLE);
           }

        }

     /*   title.setText(title_text);
        tv_price.setText(price);
        tv_date_service.setText(date_service);
        tv_restriction.setText(restriction);
        tv_address.setText(address);*/

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    public void getOrderDetail(int order_id){
        String url=IP + "/api/order/detail/"+order_id;
        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("results");
                    String img="";

                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            img = IP + "/images/default-user.png";
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            img = (jsonObject.getString("url_image")==null)?img:IP+"/"+jsonObject.getString("url_image");


                            pictures.add(new OrdersReview(Integer.parseInt(jsonObject.getString("id")),
                                    jsonObject.getString("product"),
                                    img,
                                    jsonObject.getString("quantity"),
                                    jsonObject.getString("subtotal")
                                    ));

                        }

                        ordersAdapterRecicleView = new OrderReviewAdapterRecicleView(pictures,R.layout.cardview_review_orders,ReceiveActivity.this);
                        recyclerView.setAdapter(ordersAdapterRecicleView);


                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }

                } catch (JSONException e) {

                    Toast.makeText(getApplicationContext(),"error2",Toast.LENGTH_SHORT).show();
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
                params.put("Authorization", "Bearer " + Utils.AccessToken(ReceiveActivity.this));
                return params;
            }};

        requestQueue= Volley.newRequestQueue(ReceiveActivity.this);
        requestQueue.add(stringRequest);
    }

    public void showToolbar(String titulo,boolean upButton){
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }


    public void onClick(View view){
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(ReceiveActivity.this);
        final AlertDialog dialog;

        final View mView;
        TextView btnCancel;

        switch(view.getId()){
            case R.id.btn_payment:
                createPreferenceMercadoPago();
                break;

            /*case R.id.btn_confirmservice:
                response="true";
                Toast.makeText(getApplicationContext(),"Numero de Orden"+order_id,Toast.LENGTH_SHORT).show();
                processOrder(order_id,response);
                break;*/
            case R.id.btn_cancel:
                response="false";
                Button btn_accept;

                mBuilder=new AlertDialog.Builder(ReceiveActivity.this);
                mView=getLayoutInflater().inflate(R.layout.dialog_cancel_request,null);

                btnCancel= mView.findViewById(R.id.btnCancelRestriction);
                btn_accept= mView.findViewById(R.id.btn_accept);

                ed_reason = mView.findViewById(R.id.ed_reason);

                mBuilder.setView(mView);
                dialog=mBuilder.create();
                dialog.show();

                btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reason = ed_reason.getText().toString();
                        processOrder(order_id,response,reason);
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Toast.makeText(getApplicationContext(),"Numero de Orden " + order_id,Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public void createPreferenceMercadoPago() {

        //requestOrder(order_id);
        checkout = new MercadoPagoCheckout
                .Builder("TEST-2ef9627a-353c-48e4-8782-cde57e6f8875","397912362-72ede695-8bd1-43f1-8ddb-b6b78e07c055")
                .build();
        checkout.startPayment(ReceiveActivity.this,REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){

            if(resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE){
                final Payment payment = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);
                if (payment.getPaymentStatus().equals("approved")){
                    requestOrder(order_id);
                }else{
                    Toast.makeText(getApplicationContext(),"Problemas para procesar tu pago" ,Toast.LENGTH_SHORT).show();
                }
                //
            }else if(resultCode == RESULT_CANCELED){
                if (data != null && data.getExtras() != null && data.getExtras().containsKey(MercadoPagoCheckout.EXTRA_ERROR)) {
                    final MercadoPagoError mercadoPagoError = (MercadoPagoError) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR);
                    Toast.makeText(ReceiveActivity.this,"Pago Canceled"+mercadoPagoError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void requestOrder(int order_id) {

        String url=IP+"/api/payment/"+order_id;

        stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response.toString());
                    Intent i = new Intent(activity,PaymentSplashActivity.class);
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + Utils.AccessToken(activity));
                params.put("Accept", "application/json");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String,String>();
                params.put("order_id",""+order_id);

                return params;
            }

        };

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void processOrder(int order_id, final String response) {
        String url=IP + "/api/order/" + order_id;

        stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Intent i=new Intent(ReceiveActivity.this,MainActivity.class);
                startActivity(i);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accept", response);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + Utils.AccessToken(activity));
                params.put("Accept", "application/json");

                return params;
            }
        };;

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void processOrder(int order_id, final String response,final String reason) {
        String url=IP + "/api/order/" + order_id;

        stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Intent i=new Intent(ReceiveActivity.this,MainActivity.class);
                startActivity(i);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accept", response);
                params.put("reason", reason);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + Utils.AccessToken(activity));
                params.put("Accept", "application/json");

                return params;
            }
        };;

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
