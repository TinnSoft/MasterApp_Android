package com.developer.pinedo.masterapp.Supplier;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.developer.pinedo.masterapp.adapter.SpinnerCategoryAdapter;
import com.developer.pinedo.masterapp.models.CardCategory;
import com.developer.pinedo.masterapp.models.SpinnerCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConvertToSupplierActivity extends AppCompatActivity {

    SharedPreferences prefs;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    ProgressDialog progress_bar;
    private static final String TAG = "SupplierActivity";
    private static final String IP = Utils.IP;

    EditText et_name,et_description,et_address,et_document;
    String restoredText,access_token,provider;
    ArrayList<CardCategory> pictures = new ArrayList<>();

    ArrayList<SpinnerCategory> categories;
    ArrayList<SpinnerCategory> subcategories;
    private SpinnerCategoryAdapter adaptarCat;
    int subcategory_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_to_supplier);

        et_name = findViewById(R.id.et_name);
        et_address = findViewById(R.id.et_address);
        et_description = findViewById(R.id.et_description);
        et_document = findViewById(R.id.et_document);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100){
            if(resultCode==RESULT_OK){

            }
        }
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
            Utils.redirecTo(getApplicationContext(),ConvertToSupplierActivity.class);
        }
    }

    public void onClick(View view){
        progress_bar=new ProgressDialog(this);
        progress_bar.setMessage("Espere por favor");
        progress_bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress_bar.setIndeterminate(true);
        progress_bar.setProgress(0);
        progress_bar.show();
        String url=IP+"/api/stakeholder";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress_bar.hide();


                try {
                    JSONObject obj = new JSONObject(response);
                    //Picasso.get().load(IP + "/" + user.getString("photo")).into(Utils.imageProfile);
                    Utils.setItem(ConvertToSupplierActivity.this,"is_supplier","true");
                    Toast.makeText(getApplicationContext(),"Te has convertido en un Proveedor",Toast.LENGTH_SHORT).show();
                    Utils.redirecTo(getApplication(),SupplierActivity.class);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progress_bar.hide();
                        Toast.makeText(
                                ConvertToSupplierActivity.this,"El numero de documento o Celular ya existe!",Toast.LENGTH_LONG).show();

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + access_token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //String image = Utils.imageToString(bitmap);
                Map<String, String> params = new HashMap<String,String>();
                params.put("name",et_name.getText().toString());
                params.put("description",et_description.getText().toString());
                params.put("document",et_document.getText().toString());
                params.put("address",et_address.getText().toString());
                return params;
            }
        };

        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
