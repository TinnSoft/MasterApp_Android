package com.developer.pinedo.masterapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.developer.pinedo.masterapp.Client.MainActivity;
import com.developer.pinedo.masterapp.Client.New_client.NewClientActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.auth.api.Auth;

import org.json.JSONException;
import org.json.JSONObject;



import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    TextView btn_login,btn_register,btn_google;
    EditText etEmail,etPassword;

    private static final String TAG = "GOOGLE_LOGIN";
    private static final String IP = Utils.IP;

    StringRequest stringRequest;
    ProgressDialog progress_bar;
    RequestQueue requestQueue;
    GoogleApiClient googleApiClient;

    SignInButton sign_in_button;
    private static final int SIGN_IN_CODE=777;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (Utils.getItem(this,"is_new").isEmpty()){
            Utils.redirecTo(this,NewClientActivity.class);
        }


        btn_register=findViewById(R.id.btn_register);
        btn_login=findViewById(R.id.btn_login);

        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btn_google = findViewById(R.id.btn_google);
        progressBar = findViewById(R.id.progressBar_init_sesion);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();



        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(i,SIGN_IN_CODE);
            }
        });
        permissionCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        btn_register.setVisibility(View.VISIBLE);
    }

    public boolean permissionCamera(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }


        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            loadDialogRecomendation();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    public void loadDialogRecomendation(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Permisos Desactivados");
        dialog.setMessage("Debe aceptar los permisos para el correcto funcionamiento del App");
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            Utils.redirecTo(getApplicationContext(),MainActivity.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE){
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignrResult(result);
        }
    }

    private void handleSignrResult(GoogleSignInResult result) {

        if(result.isSuccess()){

            GoogleSignInAccount account=result.getSignInAccount();

            goMainScreen(account);
        }else{
            Toast.makeText(getApplicationContext(),"No se pudo iniciar sesión",Toast.LENGTH_LONG).show();
        }
    }

    private void goMainScreen(GoogleSignInAccount account) {

        registerUserGoogle(account);
    }

    private void registerUserGoogle(final GoogleSignInAccount account) {
        progress_bar = new ProgressDialog(this);
        progress_bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress_bar.setIndeterminate(true);
        progress_bar.setProgress(0);
        progress_bar.show();

        String url=IP+"/api/social_auth";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {



                    JSONObject obj = new JSONObject(response);

                    if(obj.getString("token_type").equals("Bearer")){

                        SharedPreferences.Editor editor = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("access_token", obj.getString("access_token"));
                        editor.putString("provider", "google");
                        editor.apply();
                        Utils.redirecTo(LoginActivity.this,MainActivity.class);
                    }else{
                        Toast.makeText(getApplicationContext(),"Error: "+obj.getString("message"),Toast.LENGTH_LONG).show();
                        //Snackbar.make(view, obj.getString("message"), Snackbar.LENGTH_LONG).show();
                    }
                    progress_bar.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progress_bar.hide();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                //return super.getHeaders();
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String,String>();
                params.put("name",account.getDisplayName());
                params.put("email",account.getEmail());
                if(account.getPhotoUrl() != null){
                    params.put("photo",account.getPhotoUrl().toString());
                }
                params.put("provider","google");
                params.put("provider_user_id",account.getId());

                return params;

            }

        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:
                progressBar.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.GONE);
                Intent i=new Intent(this,RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.btn_login:
                if(!etEmail.getText().toString().isEmpty() |!etPassword.getText().toString().isEmpty() ) {
                    login(view);
                }else{
                    Toast.makeText(LoginActivity.this,"Campos vacios",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void login(final View view) {
        btn_login.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String url=IP+"/api/login";
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response.toString());
                    if(obj.getString("status").equals("error")){
                        Toast.makeText(getApplicationContext(),"Error: "+obj.getString("message"),Toast.LENGTH_LONG).show();
                            //Snackbar.make(view, obj.getString("message"), Snackbar.LENGTH_LONG).show();
                    }else{

                        JSONObject data=new JSONObject(response);
                        SharedPreferences.Editor editor = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("access_token", data.getJSONObject("data").getString("access_token"));
                        editor.putString("provider", "manual");
                        editor.apply();
                        Utils.redirecTo(LoginActivity.this,MainActivity.class);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);
               btn_login.setVisibility(View.VISIBLE);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        btn_login.setVisibility(View.VISIBLE);

                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String,String>();
                params.put("email",etEmail.getText().toString());
                params.put("password",etPassword.getText().toString());
                return params;
            }
        };

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
