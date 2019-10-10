package com.developer.pinedo.masterapp.Client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.OrdersAdapterRecicleView;
import com.developer.pinedo.masterapp.database.App;
import com.developer.pinedo.masterapp.models.CardOrders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrdersActivity extends AppCompatActivity {

    App db;

    private static final String TAG = "OrdersActivity";
    private OrdersAdapterRecicleView ordersAdapterRecicleView;

    RecyclerView picturesRecycler;
    ArrayList<CardOrders> pictures=new ArrayList<>();

    private static final String IP = Utils.IP;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    ProgressBar load_recycler;
    TextView tv_message;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        showToolbar("Ordenes",true);

        load_recycler = findViewById(R.id.load_recycler);
        tv_message = findViewById(R.id.tv_message);

        requestQueue= Volley.newRequestQueue(this);

        picturesRecycler=findViewById(R.id.ordersRecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);

        myOrders();

//        overviewAdapterRecicleView
// = new OverviewAdapterRecicleView(buildPictures(),R.layout.card_view_overview,this);
//        picturesRecycler.setAdapter(overviewAdapterRecicleView);

    }

    public void myOrders(){
        load_recycler.setVisibility(View.VISIBLE);
        String url=IP + "/api/order";
        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("results");
                    String img="";

                    Log.d("JORGE",response);

                    try {
                        if(jsonArray.length()>0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                img = IP + "/images/default-user.png";
                                JSONObject jsonObject = null;
                                jsonObject = jsonArray.getJSONObject(i);

                                pictures.add(new CardOrders(
                                        Integer.parseInt(jsonObject.getString("id")),
                                        jsonObject.getString("date_service"),
                                        jsonObject.getString("date_service_formated"),
                                        jsonObject.getString("restriction"),
                                        jsonObject.getString("reason"),
                                        jsonObject.getString("address"),
                                        jsonObject.getString("client_id"),
                                        jsonObject.getString("client"),
                                        jsonObject.getString("photo_client"),
                                        jsonObject.getString("status_id"),
                                        jsonObject.getString("status"),
                                        jsonObject.getString("supplier"),
                                        jsonObject.getString("supplier_short"),
                                        jsonObject.getString("photo_supplier"),
                                        jsonObject.getString("supplier_id"),
                                        jsonObject.getString("subtotal")
                                ));
                            }
                        }else{
                            tv_message.setVisibility(View.VISIBLE);
                        }


                        ordersAdapterRecicleView = new OrdersAdapterRecicleView(pictures,R.layout.card_view_orders,OrdersActivity.this);
                        picturesRecycler.setAdapter(ordersAdapterRecicleView );
                        load_recycler.setVisibility(View.GONE);

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

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
                params.put("Authorization", "Bearer " + Utils.AccessToken(OrdersActivity.this));
                return params;
            }};

        requestQueue= Volley.newRequestQueue(OrdersActivity.this);
        requestQueue.add(stringRequest);


      /*  List<Product> products = db.productDao().getAllProduct();

        for(Product row:products){


            pictures.add(new CardOverView(row.getId(),row.getImage(),row.getTitle(), row.getAmount(),row.getPrice(),row.getProduct_id()));

        }
        return pictures;
        */


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
