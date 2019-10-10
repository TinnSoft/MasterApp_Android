package com.developer.pinedo.masterapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.Client.MainActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "RegisterActivity";
    private static final String IP = Utils.IP;
    Button btn_register;
    EditText etName,etPhone,etEmail,etPassword,etConfirmation;

    StringRequest stringRequest;
    ProgressDialog progress_bar;
    RequestQueue requestQueue;
    String token_google;
    ImageView content_image;

    final int COD_SELECTED=10;
    final int COD_PHOTO=20;

    private final String FOLDER_ROOT="misImagenes/";
    private final String FOLDER_IMAGE=FOLDER_ROOT + "misFotos";
    GoogleApiClient googleApiClient;

    String path;
    Bitmap bitmap;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        requestQueue= Volley.newRequestQueue(this);

        //showToolbar("",true);

        content_image = findViewById(R.id.content_image);
        btn_register = findViewById(R.id.btn_register);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmation = findViewById(R.id.etConfirmation);
        progressbar = findViewById(R.id.progressbar);

        btn_register.setOnClickListener(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token_google = instanceIdResult.getToken();
            }
        });

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

    }

     @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:

                if(etPassword.getText().toString().equals(etConfirmation.getText().toString())) {
                    createUser();
                }else{
                    Toast.makeText(RegisterActivity.this,"La contraseña y la confirmación deben coincidir",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.content_image:
                ShowOptions();
                break;
        }
    }

    public void ShowOptions(){
        final CharSequence[] options={"Tomar foto","Cargar imagen","Cancelar"};
        final AlertDialog.Builder alerBuilder=new AlertDialog.Builder(RegisterActivity.this);
        alerBuilder.setTitle("Seleccione un opcion");
        alerBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Tomar foto")){

                    if(permissionCamera()){
                        takePhoto();
                    }


                }else{
                    if(options[which].equals("Cargar imagen")){
                        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/");
                        startActivityForResult(i.createChooser(i,"Seleccione la acplicion"),COD_SELECTED);
                    }else{
                        dialog.dismiss();
                    }
                }
            }
        });

        alerBuilder.show();

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

    public void takePhoto(){
        File fileImage=new File(Environment.getExternalStorageDirectory(),FOLDER_IMAGE);
        boolean is_created = fileImage.exists();

        String name_image="";

        if(is_created==false){
            is_created=fileImage.mkdirs();
        }

        if(is_created==true){
            name_image = (System.currentTimeMillis()/1000)+".jpg";
        }

        path = Environment.getExternalStorageDirectory()+File.separator+FOLDER_IMAGE+File.separator+name_image;


        File image=new File(path);

        Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri=FileProvider.getUriForFile(this,authorities,image);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else {
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        }

        startActivityForResult(i,COD_PHOTO);

    }

    public void createUser(){
        progressbar.setVisibility(View.VISIBLE);
        btn_register.setVisibility(View.GONE);
        String url=IP+"/api/register";
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject data=new JSONObject(response);
                    SharedPreferences.Editor editor = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("access_token", data.getJSONObject("data").getString("access_token"));
                    //editor.putString("token_google", data.getJSONObject("data").getString("token_google"));
                    editor.putString("provider", "manual");
                    editor.apply();

                    progressbar.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                    Intent i=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressbar.setVisibility(View.GONE);
                        btn_register.setVisibility(View.VISIBLE);
                        NetworkResponse networkResponse = error.networkResponse;
                        String jsonError = new String(networkResponse.data);

                        JSONObject errors= null;

                        try {
                            errors = new JSONObject(jsonError).getJSONObject("errors");

                            if (errors.has("email")){
                                etEmail.setError("El Email ya existe");
                                etEmail.requestFocus();
                            }

                            if(errors.has("name")){
                                etName.setError("El nombre no puede estar vacio");
                                etName.requestFocus();
                            }

                            if(errors.has("phone")){
                                etPhone.setError("El celular ya existe");
                                etPhone.requestFocus();
                            }

                        } catch (JSONException e) {
                            Log.d("JORGE-exception",e.getMessage());

                            e.printStackTrace();
                        }

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/jsoque ln");
                params.put("Accept", "application/json");

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = Utils.imageToString(bitmap);

                Map<String, String> params=new HashMap<String,String>();
                params.put("name",etName.getText().toString());
                params.put("email",etEmail.getText().toString());
                params.put("phone",etPhone.getText().toString());
                params.put("password",etPassword.getText().toString());
                params.put("token_google",token_google);
                params.put("image", image);

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){


            switch (requestCode){
                case  COD_SELECTED:
                    Uri path_selected=data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path_selected);
                        content_image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case COD_PHOTO:
                    MediaScannerConnection.scanFile(RegisterActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });


                    bitmap = BitmapFactory.decodeFile(path);
                    content_image.setImageBitmap(bitmap);
                    break;
            }
        }
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
