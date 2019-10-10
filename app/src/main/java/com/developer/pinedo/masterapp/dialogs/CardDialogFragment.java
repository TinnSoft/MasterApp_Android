package com.developer.pinedo.masterapp.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.LoginActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class CardDialogFragment extends DialogFragment implements View.OnClickListener{


    private static final String TAG = "CardDialogFragment";
    private static final String IP = Utils.IP;
    final int COD_SELECTED=10;
    final int COD_PHOTO=20;
    SharedPreferences prefs;

    JsonObjectRequest jsonObjectRequest;
    ProgressDialog progressDialog;


    private CardDialogFragment myApp;

    StringRequest stringRequest;
    ProgressDialog progress_bar;
    RequestQueue requestQueue;

    View view;
    Button btn_select_image;
    ImageView content_image;
    EditText etNumberCard,etTitular,etDateExpiration,etCode;
    String path;
    Bitmap bitmap;

    MenuItem menuItem;


    String restoredText,access_token,provider;

    private final String FOLDER_ROOT="misImagenes/";
    private final String FOLDER_IMAGE=FOLDER_ROOT + "misFotos";

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_card,container,false);
        Toolbar toolbar = view.findViewById(R.id.toolbar_pro);
//        toolbar.setTitle("Productos");

//        menuItem=view.findViewById(R.id.opt_new);
//        menuItem.setVisible(false);


        requestQueue = Volley.newRequestQueue(getActivity());

        if(getArguments()!=null){
            if(getArguments().containsKey("code")) {
                getProduct(getArguments().getString("code").toString());
            }
        }



        etNumberCard = view.findViewById(R.id.etNumberCard);
        etTitular = view.findViewById(R.id.etTitular);
        etDateExpiration = view.findViewById(R.id.etDateExpiration);
        etCode = view.findViewById(R.id.etCode);
        btn_select_image = view.findViewById(R.id.btn_select_image);


        btn_select_image.setOnClickListener(this);


        /*if(permissionCamera()){

            btn_select_image.setEnabled(true);
        }else{
            btn_select_image.setEnabled(false);
        }*/


       ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        setHasOptionsMenu(true);

        return view;
    }





    public boolean permissionCamera(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }


        if((getActivity().checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED) &&
                (getActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((getActivity().shouldShowRequestPermissionRationale(CAMERA)) ||
                (getActivity().shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            loadDialogRecomendation();
        }else{
            getActivity().requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){
            if(grantResults.length == 2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED ){
                content_image.setEnabled(true);
            }else{
                requestPermissionManual();
            }
        }

    }

    public void  requestPermissionManual(){
        final CharSequence[] options = {"Si","No"};
        final AlertDialog.Builder alerBuilder=new AlertDialog.Builder(getActivity());
        alerBuilder.setTitle("¿Desea configurar los permisos de forma manual?");
        alerBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Si")){
                    Intent i= new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getActivity().getPackageName(),null);
                    i.setData(uri);
                    startActivity(i);
                }else{
                    Toast.makeText(getActivity(),"Los permisos no fueron aceptados",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });

        alerBuilder.show();
    }

    public void loadDialogRecomendation(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
        dialog.setTitle("Permisos Desactivados");
        dialog.setMessage("Debe aceptar los permisos para el correcto funcionamiento del App");
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_select_image:
                saveProduct();
                break;
            case R.id.content_image:

                break;
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
            String authorities=getActivity().getApplicationContext().getPackageName()+".provider";
            Uri imageUri=FileProvider.getUriForFile(getActivity(),authorities,image);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else {
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        }


        startActivityForResult(i,COD_PHOTO);

    }

    @Override
    public void onStart() {
        super.onStart();

        prefs = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            access_token = prefs.getString("access_token", "");//"No name defined" is the default value.
            provider = prefs.getString("provider", "");
        }else{
            Utils.redirecTo(getActivity(),LoginActivity.class);
        }
    }

    public void ShowOptions(){
        final CharSequence[] options={"Tomar foto","Cargar imagen","Cancelar"};
        final AlertDialog.Builder alerBuilder=new AlertDialog.Builder(getActivity());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            switch (requestCode){
                case  COD_SELECTED:
                    Uri path_selected=data.getData();
//                    content_image.setImageURI(path_selected);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),path_selected);
                        content_image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case COD_PHOTO:
                    MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });

                    bitmap= BitmapFactory.decodeFile(path);
                    content_image.setImageBitmap(bitmap);
                    break;
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog=super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }



  /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItem = menu.getItem(0);

        menuItem.setVisible(false);
        inflater.inflate(R.menu.save_menu,menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

         if(id == android.R.id.home){
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProduct(String code) {

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Espere por favor...");
        progressDialog.show();

        String url=IP+"/api/product/" + code+"/edit";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);

                    Glide
                            .with(getActivity().getApplicationContext())
                            .load(IP+"/"+data.getString("url_image"))
                            .centerCrop()
                            //.placeholder(R.drawable.loading_spinner)
                            .into(content_image);


                    progressDialog.hide();

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
                });

        requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public void saveProduct(){

        progress_bar=new ProgressDialog(getActivity());
        progress_bar.setMessage("Espere por favor");
        progress_bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress_bar.setIndeterminate(true);
        progress_bar.setProgress(0);
        progress_bar.show();
        String url=IP+"/api/card";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress_bar.hide();

                try {
                    JSONObject obj = new JSONObject(response);
                    etNumberCard.setText("");
                    etTitular.setText("");
                    etDateExpiration.setText("");
                    etCode.setText("");

                    Toast.makeText(getActivity(),"Tarjeta guardada",Toast.LENGTH_LONG).show();


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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer "+access_token);
                params.put("Accept", "application/json");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params=new HashMap<String,String>();
                params.put("number_card",etNumberCard.getText().toString());
                params.put("titular",etTitular.getText().toString());
                params.put("expiration_date",etDateExpiration.getText().toString());
                params.put("code",etCode.getText().toString());


                return params;
            }

        };
        requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


}
