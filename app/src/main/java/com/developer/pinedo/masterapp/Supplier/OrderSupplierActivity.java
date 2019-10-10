package com.developer.pinedo.masterapp.Supplier;

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
import com.developer.pinedo.masterapp.adapter.Supplier.OrdersSupAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardOrders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderSupplierActivity extends AppCompatActivity {

    private static final String TAG = "OrdersActivity";
    private OrdersSupAdapterRecicleView ordersSupAdapterRecicleView;

    RecyclerView picturesRecycler;
    ArrayList<CardOrders> pictures=new ArrayList<>();

    private static final String IP = Utils.IP;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    ProgressBar progressBar;
    TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_supplier);
        showToolbar("Solicitudes",true);

        requestQueue= Volley.newRequestQueue(this);

        picturesRecycler=findViewById(R.id.ordersRecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);
        progressBar = findViewById(R.id.progressBar);
        tv_message = findViewById(R.id.tv_message);

        buildOrders();
    }

    public void buildOrders(){
        progressBar.setVisibility(View.VISIBLE);
        String url=IP + "/api/order-supplier";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);


                    JSONArray jsonArray=data.optJSONArray("results");
                    int price=0,cat=0;
                    String category="",price_st="",img="";

                    try {
                        if(jsonArray.length()>0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                img = IP + "/images/default-user.png";
                                JSONObject jsonObject = null;
                                jsonObject = jsonArray.getJSONObject(i);

                            /*if(!jsonObject.getString("url").equals(null)){
                                img = IP+"/"+jsonObject.getString("url");
                            }*/

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


                        ordersSupAdapterRecicleView = new OrdersSupAdapterRecicleView(pictures,R.layout.card_view_supplier_orders,OrderSupplierActivity.this);
                        picturesRecycler.setAdapter(ordersSupAdapterRecicleView );
                        progressBar.setVisibility(View.GONE);

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
                params.put("Authorization", "Bearer " + Utils.AccessToken(OrderSupplierActivity.this));
                return params;
            }};

        requestQueue= Volley.newRequestQueue(OrderSupplierActivity.this);
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
        toolbar.setNavigationIcon(R.drawable.ic_backgreen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

    }
}
