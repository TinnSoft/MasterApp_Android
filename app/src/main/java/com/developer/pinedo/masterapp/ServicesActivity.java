package com.developer.pinedo.masterapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.database.App;
import com.developer.pinedo.masterapp.entities.Product;
import com.developer.pinedo.masterapp.models.CardChef;
import com.mercadopago.android.px.core.MercadoPagoCheckout;


import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesActivity extends AppCompatActivity {

    private static final String TAG = "ServicesActivity";
    private static final String IP = Utils.IP;
    private int day,month,year,hour,minutes;
    private TextView fecha_chef_text,hour_chef_text,amount_person_text,alimentary_restriction_text,address_text;
    App db;
    Product row = null;
    String date_service,hour_service,restriction_alimentary,address,list_products;
    TextView btn_accept;
    EditText ed_restriction,ed_address;
    CardChef cardChef=null;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    ProgressDialog progressDialog;
    Activity activity;

    private static final int REQUEST_CODE = 100;
    private static final int RESULT_CANCELED = 101;
    Button button;

    MercadoPagoCheckout checkout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        activity=this;
        fecha_chef_text=findViewById(R.id.fecha_chef_text);
        hour_chef_text=findViewById(R.id.hour_chef_text);
        alimentary_restriction_text=findViewById(R.id.alimentary_restriction_text);
        address_text=findViewById(R.id.address_text);

        restriction_alimentary="";
        showToolbar("",true);

        Bundle param=getIntent().getExtras();

        db = Room.databaseBuilder(getApplicationContext(),App.class,"production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();




        if(param != null) {
            cardChef = (CardChef) param.getSerializable("supplier");
        }

    }

    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(0x000000);
        toolbar.setNavigationIcon(R.drawable.ic_backgreen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

    }

    public void onClick(View view){
        final Calendar c;

        final View mView;
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(ServicesActivity.this);
        final AlertDialog dialog;
        WindowManager.LayoutParams wmlp;
        TextView btnCancel;

        switch (view.getId()){
            case R.id.fecha_chef:
                c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                c.add(Calendar.DAY_OF_MONTH, 2);

                DatePickerDialog datePickerDialog=new DatePickerDialog(this,  R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        String month_text = (month > 9) ? "0" + month : month + "";

                        date_service = year + "/" + (month_text) + "/" + dayOfMonth;
                        fecha_chef_text.setText(dayOfMonth+"/"+month_text+"/"+year);

                    }
                },year,month,day);

                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();

                break;
            case R.id.hour_chef:

                c = Calendar.getInstance();
                hour=c.get(Calendar.HOUR_OF_DAY);
                minutes=c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog=new TimePickerDialog(this, R.style.DialogTheme,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hour_service=hourOfDay + ":" + minute;
                        hour_chef_text.setText(hourOfDay+":"+minute);
                    }
                },hour,minutes,false);
                timePickerDialog.show();
                break;

            case R.id.alimentary_restriction:
                mBuilder=new AlertDialog.Builder(ServicesActivity.this);
                mView = getLayoutInflater().inflate(R.layout.dialog_restriction,null);

                //btnCancel= mView.findViewById(R.id.btnCancelRestriction);

                ed_restriction = mView.findViewById(R.id.ed_restriction);

                if (!restriction_alimentary.isEmpty()){
                    ed_restriction.setText(restriction_alimentary);
                }


                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                mBuilder.setPositiveButton(Html.fromHtml("<font color='#3BC4A5'>Aceptar</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(ed_restriction.getText().toString().isEmpty()){
                            restriction_alimentary="";
                        }else{
                            restriction_alimentary = ed_restriction.getText().toString();
                        }

                        alimentary_restriction_text.setText(restriction_alimentary);
                        dialog.cancel();
                    }
                });



                mBuilder.setView(mView);

                dialog = mBuilder.create();

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                wmlp = dialog.getWindow().getAttributes();

                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.x = 100;   //x position
                wmlp.y = 200;   //y position

                dialog.show();



               /* btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restriction_alimentary = ed_restriction.getText().toString();
                        alimentary_restriction_text.setText(restriction_alimentary);
                        dialog.dismiss();
                    }
                });*/

              /*  btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
*/
                break;
            case R.id.address:
                mBuilder=new AlertDialog.Builder(ServicesActivity.this,R.style.AlertDialogTheme);
                mView=getLayoutInflater().inflate(R.layout.dialog_address,null);
                ed_address = mView.findViewById(R.id.ed_address);

                mBuilder.setView(mView);

                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                mBuilder.setPositiveButton(Html.fromHtml("<font color='#3BC4A5'>Aceptar</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        address = ed_address.getText().toString();
                        address_text.setText(address);
                        alimentary_restriction_text.setText(restriction_alimentary);
                        dialog.cancel();
                    }
                });

                dialog=mBuilder.create();

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                wmlp = dialog.getWindow().getAttributes();

                wmlp.gravity = Gravity.TOP | Gravity.LEFT;
                wmlp.x = 10;   //x position
                wmlp.y = 200;   //y position
                dialog.show();

                break;
            case R.id.program_order:
                createPreferenceMercadoPago();
                //requestOrder(date_service + " " + hour_service, restriction_alimentary,cardChef.getProduct_id());
                break;

        }
    }

    public void createPreferenceMercadoPago() {
        List<Product> products = db.productDao().getAllProduct();

        try {

            JSONArray body = new JSONArray();
            JSONObject raw = new JSONObject();
            for (Product pro : products) {
                raw.put("product_id",pro.getProduct_id());
                raw.put("quantity",pro.getQuantity());
                body.put(raw.toString());
            }

            list_products = body.toString();
            requestOrder(date_service + " " + hour_service, restriction_alimentary);
        }catch (Exception JSONException){
            Log.d("JORGE",JSONException.getMessage());
        }



        /*checkout = new MercadoPagoCheckout
                .Builder("TEST-2ef9627a-353c-48e4-8782-cde57e6f8875","397912362-72ede695-8bd1-43f1-8ddb-b6b78e07c055")
                .build();
        checkout.startPayment(ServicesActivity.this,REQUEST_CODE);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){

            if(resultCode == MercadoPagoCheckout.PAYMENT_RESULT_CODE){

                final Payment payment = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);
                requestOrder(date_service + " " + hour_service, restriction_alimentary);
            }else if(resultCode == RESULT_CANCELED){
                if (data != null && data.getExtras() != null && data.getExtras().containsKey(MercadoPagoCheckout.EXTRA_ERROR)) {
                    final MercadoPagoError mercadoPagoError = (MercadoPagoError) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_ERROR);
                    Toast.makeText(ServicesActivity.this,"Pago Canceled"+mercadoPagoError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void requestOrder(final String date_service, final String restriction_alimentary) {
        progressDialog=new ProgressDialog(activity);
        progressDialog.setMessage("Espere por favor...");
        progressDialog.show();
        String url=IP+"/api/order";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response.toString());
                    Intent i = new Intent(activity,EndsplashActivity.class);
                    startActivity(i);
                    progressDialog.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Log.d("JORGE",error.networkResponse.statusCode+"");
                        Log.d("JORGE",error.getMessage());
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
                params.put("date_service",date_service);
                params.put("restriction_alimentary",(restriction_alimentary.isEmpty())?"":restriction_alimentary);
                params.put("address",address);
                params.put("products",list_products);

                Log.d("JORGE",params.toString());
                return params;
            }
        };

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}