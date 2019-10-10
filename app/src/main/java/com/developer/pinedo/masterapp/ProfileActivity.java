package com.developer.pinedo.masterapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProfileActivity extends AppCompatActivity  implements View.OnClickListener, View.OnFocusChangeListener{

    private static final String TAG = "ProfileActivity ";

    StringRequest stringRequest;
    RequestQueue requestQueue;

    TextView etName,etEmail,tv_main,etPhone,etBirthDay;

    final int COD_SELECTED=10;
    final int COD_PHOTO=20;

    private final String FOLDER_ROOT="misImagenes/";
    private final String FOLDER_IMAGE=FOLDER_ROOT + "misFotos";

    Button btn_convert;

    private static final String IP= Utils.IP;

    String path,restoredText,access_token,provider;;

    AlertDialog dialog;

    Bitmap bitmap;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ImageView profile_image_card;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        showToolbar("",true);

        prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        profile_image_card = findViewById(R.id.profile_image_card);
        btn_convert = findViewById(R.id.btn_convert);
        etPhone = findViewById(R.id.etPhone);
        etEmail= findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        tv_main= findViewById(R.id.tv_main);
        etBirthDay= findViewById(R.id.etBirthDay);

        profile_image_card.setOnClickListener(this);


        etBirthDay.setOnFocusChangeListener(this);

        access_token = prefs.getString("access_token", "");//"No name defined" is the default value.
        getDataUser();

    }

    private void getDataUser() {
        String url=Utils.IP + "/api/user";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);
                    etName.setText(data.getString("name"));
                    etEmail.setText(data.getString("email"));
                    etPhone.setText(data.getString("phone"));
                    etBirthDay.setText(data.getString("birth_day"));

                  /*  tv_email.setText(data.getString("email"));
                    tv_description.setText(data.getString("description"));*/



                    Glide.with(getApplicationContext())
                            .load(data.getString("photo"))
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile)
                            .into(profile_image_card);

                } catch (JSONException e) {
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + Utils.AccessToken(ProfileActivity.this));
                params.put("Accept", "application/json");
                return params;
            }
        };


        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void updateInfo(View v){

        String url=IP+"/api/user";
        stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONObject data=new JSONObject(response);


                    Toast.makeText(ProfileActivity.this,"Información Actualizada",Toast.LENGTH_LONG).show();

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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + Utils.AccessToken(ProfileActivity.this));
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String,String>();
                params.put("name",etName.getText().toString());
                params.put("email",etEmail.getText().toString());
                params.put("birth_day",etBirthDay.getText().toString());
                params.put("phone",etPhone.getText().toString());

                return params;
            }
        };

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

    private void setPhotoProfile(final Bitmap bitmap) {
        String url=Utils.IP+"/api/user/update-photo";


        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject user = obj.getJSONObject("user");

                    /*Glide
                            .with(getApplicationContext())
                            .load(IP  + "/" + user.getString("photo"))
                            .centerCrop()
                            //.placeholder(R.drawable.loading_spinner)
                            .into(Utils.imageProfile);*/
                    Glide
                            .with(getApplicationContext())
                            .load(IP  + "/" + user.getString("photo"))
                            .centerCrop()
                            .placeholder(R.drawable.ic_boton_camara)
                            .into(profile_image_card);

//                    Picasso.get().load(IP  + "/" + user.getString("photo")).into(profile_image_card);
//                    Picasso.get().load(IP + "/" + user.getString("photo")).into(Utils.imageProfile);

                    Toast.makeText(ProfileActivity.this,"Foto Actualizada",Toast.LENGTH_LONG).show();
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
                //params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + access_token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = Utils.imageToString(bitmap);
                Map<String, String> params = new HashMap<String,String>();
                params.put("image",image);
                return params;
            }



        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            switch (requestCode){
                case  COD_SELECTED:
                    Uri path_selected=data.getData();
//                    content_image.setImageURI(path_selected);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path_selected);
                        profile_image_card.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case COD_PHOTO:
                    MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });

                    bitmap= BitmapFactory.decodeFile(path);
                    profile_image_card.setImageBitmap(bitmap);
                    setPhotoProfile(bitmap);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profile_image_card:
                if(permissionCamera()) {
                    ShowOptions();
                }
                break;
            case R.id.btn_convert:
                //Utils.redirecTo(getContext(),C.class);
                break;

            case R.id.etBirthDay:
                final Calendar c;

                int day,month,year;
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
                        etBirthDay.setText(year+"-"+month_text+"-"+dayOfMonth);

                    }
                },year,month,day);

                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
                break;

            case R.id.profile_name:
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(this);

                View mView=getLayoutInflater().inflate(R.layout.dialog_edit_field,null);
//                btnCancel= mView.findViewById(R.id.btnCancelRestriction);
                final EditText etDescription = mView.findViewById(R.id.etDescription);
                etDescription.setHint("Nombres");
                etDescription.setText(etName.getText().toString());


                mBuilder.setView(mView);
                mBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateName(etDescription.getText().toString(),1);
                    }
                });

                mBuilder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.cancel();
                    }
                });

                dialog=mBuilder.create();
                dialog.setTitle("Editar");
                dialog.show();

                break;
        }
    }

    private void updateName(final String name,int user_id) {
        String url=IP + "/api/user/" + user_id;

        stringRequest=new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("data");
                    etName.setText(name);
                    tv_main.setText(name);

                } catch (JSONException e) {
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
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params=new HashMap<String,String>();
                params.put("name",name);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + Utils.AccessToken(ProfileActivity.this));
                params.put("Accept", "application/json");
                return params;
            }
        };


        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void ShowOptions(){
        final CharSequence[] options={"Tomar foto","Cargar imagen","Cancelar"};
        final AlertDialog.Builder alerBuilder=new AlertDialog.Builder(this);
        alerBuilder.setTitle("Seleccione un opcion");
        alerBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Tomar foto")){
                    takePhoto();
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



    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(0x000000);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final Calendar c;

        int day,month,year;
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
                etBirthDay.setText(year+"-"+month_text+"-"+dayOfMonth);

            }
        },year,month,day);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }
}
