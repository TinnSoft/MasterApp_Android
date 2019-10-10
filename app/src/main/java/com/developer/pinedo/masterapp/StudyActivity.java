package com.developer.pinedo.masterapp;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.Supplier.SupplierActivity;
import com.developer.pinedo.masterapp.models.CardExtraInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudyActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener{

    private static final String IP = Utils.IP;
    private static final String TAG = "StudyActivity";

    SharedPreferences prefs;
    Button btn_save;

    EditText etTitle,etDescription,etBusiness,etDateInit,etDateEnd;

    StringRequest stringRequest;
    String access_token,restoredText,provider;

    ProgressBar progress_bar;
    RequestQueue requestQueue;
    TextInputLayout tilInit,tilEnd;

    CardExtraInfo cardExtraInfo;

    int study_id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        btn_save = findViewById(R.id.btn_save);

        showToolbar("",true);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etBusiness = findViewById(R.id.etBusiness);
        etDateInit = findViewById(R.id.etDateInit);
        etDateEnd = findViewById(R.id.etDateEnd);

        progress_bar =findViewById(R.id.progress_bar);

        btn_save.setOnClickListener(this);
        /*etDateInit.setOnClickListener(this);
        etDateEnd.setOnClickListener(this);*/

        etDateInit.setOnFocusChangeListener(this);
        etDateEnd.setOnFocusChangeListener(this);

        Bundle param=getIntent().getExtras();


        if (param != null) {
            cardExtraInfo = (CardExtraInfo) param.getSerializable("study");
            etTitle.setText(cardExtraInfo.getCard_title());
            etDescription.setText(cardExtraInfo.getCard_description());
            etBusiness.setText(cardExtraInfo.getCard_business());
            etDateInit.setText(cardExtraInfo.getCard_date_init());
            etDateEnd.setText(cardExtraInfo.getCard_date_end());
            study_id = Integer.parseInt(cardExtraInfo.getCard_id());
            btn_save.setText("Editar");

        }
    }

    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(0x000000);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
    }

    @Override
    public void onClick(View view) {

        final Calendar c;

        int day,month,year;
        ;

        switch (view.getId()){
            case R.id.btn_save:

                if (study_id == 0){
                    saveProduct();
                }else{
                    editStudy(study_id);
                }

                break;
            case R.id.etDateInit:
                DatePickerDialog datePickerDialog;
                c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                c.add(Calendar.DAY_OF_MONTH, 2);

                datePickerDialog=new DatePickerDialog(this,  R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String month_text =(month<=9)?("0"+month):String.valueOf(month);
                        etDateInit.setText(year+"-"+month_text+"-"+dayOfMonth);
                    }
                },year,month,day);

                //datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
                break;
            case R.id.etDateEnd:
                DatePickerDialog datePickerDialog2;
                c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                c.add(Calendar.DAY_OF_MONTH, 2);

                datePickerDialog2 = new DatePickerDialog(this,  R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String month_text =(month<=9)?("0"+month):String.valueOf(month);
                        etDateEnd.setText(year+"-"+month_text+"-"+dayOfMonth);
                    }
                },year,month,day);

                //datePickerDialog2.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog2.show();
                break;
        }

    }

    public void editStudy(int study_id){

        String url=IP+"/api/study-experience/"+study_id;

        stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);
                    if((obj.getString("status")).equals("updated")){
                        etTitle.setText("");
                        etDescription.setText("");
                        etBusiness.setText("");
                        etDateInit.setText("");
                        etDateEnd.setText("");

                        Utils.redirecTo(StudyActivity.this, SupplierActivity.class);

                    }else{
                        Toast.makeText(StudyActivity.this,obj.getString("msg"),Toast.LENGTH_LONG).show();
                    }


                    progress_bar.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    btn_save.setVisibility(View.VISIBLE);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        btn_save.setVisibility(View.VISIBLE);
                        Toast.makeText(StudyActivity.this,"Problemas con la solicitud",Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer "+access_token);
                params.put("Accept", "application/json");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String,String>();
                params.put("title",etTitle.getText().toString());
                params.put("description",etDescription.getText().toString());
                params.put("business",etBusiness.getText().toString());
                params.put("date_init",etDateInit.getText().toString());
                params.put("date_end",etDateEnd.getText().toString());

                return params;
            }

        };
        requestQueue= Volley.newRequestQueue(StudyActivity.this);
        requestQueue.add(stringRequest);
    }

    public void saveProduct(){

        String url=IP+"/api/study";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if((obj.getString("status")).equals("created")){
                        etTitle.setText("");
                        etDescription.setText("");
                        etBusiness.setText("");
                        etDateInit.setText("");
                        etDateEnd.setText("");
                        Utils.redirecTo(StudyActivity.this, SupplierActivity.class);

                    }else{
                        Toast.makeText(StudyActivity.this,obj.getString("msg"),Toast.LENGTH_LONG).show();
                    }


                    progress_bar.setVisibility(View.GONE);
                    btn_save.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        btn_save.setVisibility(View.VISIBLE);
                        Toast.makeText(StudyActivity.this,"Problemas con la solicitud",Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer "+access_token);
                params.put("Accept", "application/json");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String,String>();
                params.put("title",etTitle.getText().toString());
                params.put("description",etDescription.getText().toString());
                params.put("business",etBusiness.getText().toString());
                params.put("date_init",etDateInit.getText().toString());
                params.put("date_end",etDateEnd.getText().toString());
                params.put("type_information_id","1");
                Log.e("JORGE",params.toString());

                return params;
            }

        };
        requestQueue= Volley.newRequestQueue(StudyActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            access_token = prefs.getString("access_token", "");//"No name defined" is the default value.
            provider = prefs.getString("provider", "");
        }else{
            Utils.redirecTo(StudyActivity.this,LoginActivity.class);
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        final Calendar c;

        int day,month,year;


        switch (view.getId()){
            case R.id.etDateInit:
                DatePickerDialog datePickerDialog;
                c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                c.add(Calendar.DAY_OF_MONTH, 2);

                datePickerDialog=new DatePickerDialog(this,  R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String month_text =(month<=9)?("0"+month):String.valueOf(month);
                        etDateInit.setText(year+"-"+month_text+"-"+dayOfMonth);

                    }
                },year,month,day);

                //datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
                break;
            case R.id.etDateEnd:
                DatePickerDialog datePickerDialog2;
                c = Calendar.getInstance();
                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                c.add(Calendar.DAY_OF_MONTH, 2);

                datePickerDialog2=new DatePickerDialog(this,  R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String month_text =(month<=9)?("0"+month):String.valueOf(month);
                        etDateEnd.setText(year+"-"+month_text+"-"+dayOfMonth);

                    }
                },year,month,day);

                //datePickerDialog2.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog2.show();
                break;
        }
    }
}
