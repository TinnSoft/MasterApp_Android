package com.developer.pinedo.masterapp.Supplier;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.ReceiveActivity;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.OrderReviewAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardOrders;
import com.developer.pinedo.masterapp.models.OrdersReview;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReceiveSupplierActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "ReceiveActivity";

    TextView tv_supplier,tv_subtotal,tv_address,tv_restriction,tv_date_service;
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
    Button btn_confirmservice,btn_cancel;

    ArrayList<OrdersReview> pictures = new ArrayList<>();

    private OrderReviewAdapterRecicleView ordersAdapterRecicleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_supplier);
        activity=this;
        showToolbar("",true);

        tv_supplier=findViewById(R.id.tv_supplier);
        tv_subtotal=findViewById(R.id.tv_subtotal);
        im_supplier=findViewById(R.id.im_supplier);
        tv_address=findViewById(R.id.tv_address);
        tv_restriction=findViewById(R.id.tv_restriction);
        tv_date_service=findViewById(R.id.tv_date_service);

        btn_confirmservice=findViewById(R.id.btn_confirmservice);
        btn_cancel=findViewById(R.id.btn_cancel);


        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent i = getIntent();

        if(i.getExtras() != null){

            CardOrders row = (CardOrders) i.getSerializableExtra("order");
            String price = "$ "+NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(row.getSubtotal()));
            tv_address.setText(row.getAddress());
            tv_date_service.setText(row.getDate_service_formated());
            tv_restriction.setText(row.getRestriction());
            tv_supplier.setText(row.getSupplier_short());
            tv_subtotal.setText(price);
            order_id = row.getId();
            getOrderDetail(order_id);


            Glide
                    .with(getApplicationContext())
                    .load(i.getExtras().get("url"))
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(im_supplier);


            if(row.getStatus_id().equals("2")){
                btn_confirmservice.setVisibility(View.INVISIBLE);
                btn_cancel.setVisibility(View.INVISIBLE);
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

                        ordersAdapterRecicleView = new OrderReviewAdapterRecicleView(pictures,R.layout.cardview_review_orders,ReceiveSupplierActivity.this);
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
                params.put("Authorization", "Bearer " + Utils.AccessToken(ReceiveSupplierActivity.this));
                return params;
            }};

        requestQueue= Volley.newRequestQueue(ReceiveSupplierActivity.this);
        requestQueue.add(stringRequest);
    }

    public void showToolbar(String titulo,boolean upButton){
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backgreen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }


    public void onClick(View view){
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(ReceiveSupplierActivity.this);
        final AlertDialog dialog;

        final View mView;
        TextView btnCancel,btn_accept;

        switch(view.getId()){
            case R.id.btn_confirmservice:
                response="true";
                Toast.makeText(getApplicationContext(),"Numero de Orden"+order_id,Toast.LENGTH_SHORT).show();
                processOrder(order_id,response);
                break;
            case R.id.btn_cancel:
                response="false";
                mBuilder=new AlertDialog.Builder(ReceiveSupplierActivity.this);
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

    private void processOrder(int order_id, final String response) {
        String url=IP + "/api/order/" + order_id;

        stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Intent i=new Intent(ReceiveSupplierActivity.this,OrderSupplierActivity.class);
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

                Intent i=new Intent(ReceiveSupplierActivity.this,MainActivity.class);
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
        };

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
