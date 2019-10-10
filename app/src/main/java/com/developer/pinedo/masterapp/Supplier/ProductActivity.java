package com.developer.pinedo.masterapp.Supplier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.developer.pinedo.masterapp.LoginActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.SpinnerCategoryAdapter;
import com.developer.pinedo.masterapp.entities.Product;
import com.developer.pinedo.masterapp.models.CardMenuChef;
import com.developer.pinedo.masterapp.models.SpinnerCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String IP = Utils.IP;
    private static final String TAG = "ProductActivity";

    SharedPreferences prefs;

    final int COD_SELECTED=10;
    final int COD_PHOTO=20;

    private final String FOLDER_ROOT="misImagenes/";
    private final String FOLDER_IMAGE=FOLDER_ROOT + "misFotos";

    Button btn_register;

    TextView etTitle,etPrice,etDescription,inactive_product,drop_product;
    ArrayList<SpinnerCategory> categories;
    ArrayList<SpinnerCategory> subcategories;
    ImageView content_image;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    int subcategory_id;

    private SpinnerCategoryAdapter adaptarCat;

    AlertDialog dialog;

    String path;
    Bitmap bitmap;

    String restoredText,access_token,provider;

    ProgressBar progress_bar;
    int product_id=0;

    String price_clean="";

    boolean is_new=true;

    Spinner sp_category,sp_subcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        showToolbar("",true);
        Bundle param=getIntent().getExtras();
        categories=new ArrayList<>();
        subcategories=new ArrayList<>();
        CardMenuChef cardChef=null;
        etTitle=findViewById(R.id.etTitle);
        etPrice=findViewById(R.id.etPrice);
        etDescription=findViewById(R.id.etDescription);
        btn_register =findViewById(R.id.btn_register);
        progress_bar =findViewById(R.id.progress_bar);
        inactive_product =findViewById(R.id.inactive_product);
        drop_product =findViewById(R.id.drop_product);

        content_image=findViewById(R.id.content_image);
        sp_category = findViewById(R.id.sp_category);
        sp_subcategory = findViewById(R.id.sp_subcategory);

        getCategories();

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpinnerCategory row= (SpinnerCategory) adapterView.getItemAtPosition(i);
                String clickCategory= row.getTitle();

                getSubcategory(row.getSlug());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_subcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpinnerCategory row= (SpinnerCategory) adapterView.getItemAtPosition(i);
                subcategory_id =row.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(param != null) {
            is_new = false;
            inactive_product.setVisibility(View.VISIBLE);
            drop_product.setVisibility(View.VISIBLE);
            cardChef = (CardMenuChef) param.getSerializable("menu");
            etTitle.setText(cardChef.getCard_name());
            etPrice.setText(cardChef.getCard_price());
            etDescription.setText(cardChef.getCard_description());
            product_id = cardChef.getCard_id();

            Glide
                    .with(getApplicationContext())
                    .load(cardChef.getCard_photo())
                    .centerCrop()
                    .placeholder(R.drawable.ic_boton_camara)
                    .into(content_image);

        }

        content_image.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        etPrice.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                EditText price = view.findViewById(R.id.etPrice);
                if(!price.equals("") && price.length()>3){
                    price_clean = (price.getText().toString()).replace(",","");
                    String price_st = NumberFormat.getNumberInstance(Locale.US).format(Float.valueOf(price_clean));
                    price.setText(price_st);
                    price.setSelection(price.getText().length());
                }


                return false;
            }
        });

        inactive_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductActivity.this,"Inactivar producto",Toast.LENGTH_LONG).show();
            }
        });

        drop_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductActivity.this,"Borrar Producto",Toast.LENGTH_LONG).show();
            }
        });


    }

    public void getCategories(){

        String url=IP + "/api/category";


        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);

                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0,price=0,cat=0;
                    String category="",price_st="",img="";
                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);
                            img = IP + "/"+jsonObject.getString("url");

                            categories.add(new SpinnerCategory(Integer.parseInt(jsonObject.getString("id")),
                                    jsonObject.getString("url"),jsonObject.getString("title"),
                                    jsonObject.getString("slug")));

                        }


                        adaptarCat = new SpinnerCategoryAdapter(getApplicationContext(),categories);

                        sp_category.setAdapter(adaptarCat);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void getSubcategory(String slug) {
        String url=IP + "/api/subcategory/" + slug + "/edit";
        subcategories.removeAll(subcategories);

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);

                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0,price=0,cat=0;
                    String category="",price_st="",img="";
                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            //pictures.add(new CardCategory(jsonObject.getString("id"),img,jsonObject.getString("title")));
                            //categories.add(new CardCategory(jsonObject.getString("id"),jsonObject.getString("photo"),jsonObject.getString("title")));
                            subcategories.add(new SpinnerCategory(Integer.parseInt(jsonObject.getString("id")),
                                    jsonObject.getString("url"),jsonObject.getString("title"),
                                    jsonObject.getString("slug")));

                        }

                        adaptarCat = new SpinnerCategoryAdapter(getApplicationContext(),subcategories);

                        sp_subcategory.setAdapter(adaptarCat);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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


    public void saveProduct(){

        String url=IP+"/api/product";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    if((obj.getString("status")).equals("created")){
                        etDescription.setText("");
                        etPrice.setText("");
                        etTitle.setText("");
                        content_image.setImageDrawable(getResources().getDrawable(R.drawable.imagen_no_disponible));

                        AlertDialog.Builder mBuilder=new AlertDialog.Builder(ProductActivity.this,R.style.AlertDialogTheme);


                        View mView = getLayoutInflater().inflate(R.layout.dialog_ok,null);

                        mBuilder.setView(mView);
                        mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                                finish();
                            }
                        });

                        dialog = mBuilder.create();
                        dialog.setTitle("");
                        dialog.show();



                    }else{
                        Toast.makeText(ProductActivity.this,obj.getString("msg"),Toast.LENGTH_LONG).show();
                    }


                    progress_bar.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        btn_register.setVisibility(View.VISIBLE);
                        Toast.makeText(ProductActivity.this,"Problemas con la solicitud",Toast.LENGTH_LONG).show();
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
                String image = Utils.imageToString(bitmap);
                Map<String, String> params=new HashMap<String,String>();
                params.put("title",etTitle.getText().toString());
                params.put("description",etDescription.getText().toString());
                params.put("price",price_clean);
                params.put("subcategory_id","" + subcategory_id);
                if(!image.isEmpty()){
                    params.put("image",image);
                }


                return params;
            }

        };
        requestQueue= Volley.newRequestQueue(ProductActivity.this);
        requestQueue.add(stringRequest);
    }

    public void editProduct(){

        String url=IP+"/api/product/"+product_id;
        stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if((obj.getString("status")).equals("edited")){
                        etDescription.setText("");
                        etPrice.setText("");
                        etTitle.setText("");
                        content_image.setImageDrawable(getResources().getDrawable(R.drawable.imagen_no_disponible));
                        Toast.makeText(ProductActivity.this,"Producto fue Editado",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ProductActivity.this,obj.getString("msg"),Toast.LENGTH_LONG).show();
                    }


                    progress_bar.setVisibility(View.GONE);
                    btn_register.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.setVisibility(View.GONE);
                        btn_register.setVisibility(View.VISIBLE);
                        Toast.makeText(ProductActivity.this,"Problemas con la solicitud",Toast.LENGTH_LONG).show();
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
                String image = Utils.imageToString(bitmap);
                Map<String, String> params=new HashMap<String,String>();
                params.put("title",etTitle.getText().toString());
                params.put("description",etDescription.getText().toString());
                params.put("price",etPrice.getText().toString());
                params.put("subcategory_id","" + subcategory_id);
                if(!image.isEmpty()){
                    params.put("image",image);
                }

                return params;
            }

        };
        requestQueue= Volley.newRequestQueue(ProductActivity.this);
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
            Utils.redirecTo(ProductActivity.this,LoginActivity.class);
        }
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

    public void ShowOptions(){
        final CharSequence[] options={"Tomar foto","Cargar imagen","Cancelar"};
        final AlertDialog.Builder alerBuilder=new AlertDialog.Builder(ProductActivity.this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                progress_bar.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.GONE);
                if(is_new){
                    saveProduct();
                }else{
                    editProduct();
                }

                break;
            case R.id.content_image:
                if(permissionCamera()) {
                    ShowOptions();
                    btn_register.setEnabled(true);
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
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
                    MediaScannerConnection.scanFile(ProductActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

                    bitmap = this.rotateImage(BitmapFactory.decodeFile(path));
                    content_image.setImageBitmap(bitmap);
                    content_image.setRotation(90f);
                    break;
            }
        }
    }

    public Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exifInterface=null;
        try {
            exifInterface=new ExifInterface(path);
        }catch (Exception e){
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix=new Matrix();

        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
        }

        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

}
