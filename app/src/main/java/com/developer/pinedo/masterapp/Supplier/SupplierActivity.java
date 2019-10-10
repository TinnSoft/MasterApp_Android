package com.developer.pinedo.masterapp.Supplier;

import android.app.AlertDialog;
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
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.Client.MainActivity;
import com.developer.pinedo.masterapp.FragmentMenuSupplier.MenuCommentFragment;
import com.developer.pinedo.masterapp.FragmentMenuSupplier.MenuInfoFragment;
import com.developer.pinedo.masterapp.FragmentMenuSupplier.MenuPhotoFragment;
import com.developer.pinedo.masterapp.FragmentMenuSupplier.MenuSupplierFragment;
import com.developer.pinedo.masterapp.LoginActivity;
import com.developer.pinedo.masterapp.Managers.Supplier;
import com.developer.pinedo.masterapp.Managers.SupplierManager;
import com.developer.pinedo.masterapp.ProfileActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.RegisterActivity;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.models.CardChef;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SupplierActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MenuSupplierFragment.OnFragmentInteractionListener,
        MenuCommentFragment.OnFragmentInteractionListener,
        MenuPhotoFragment.OnFragmentInteractionListener,
        MenuInfoFragment.OnFragmentInteractionListener{

    SharedPreferences.Editor editor;


    private static final String TAG = "SupplierActivity";

    private static final String IP = Utils.IP;

    SharedPreferences prefs;

    View headerView;

    final int COD_SELECTED=10;
    final int COD_PHOTO=20;

    private final String FOLDER_ROOT="misImagenes/";
    private final String FOLDER_IMAGE=FOLDER_ROOT + "misFotos";


    String path,access_token,restoredText;
    Bitmap bitmap;
    int number_menu=1;

    MenuSupplierFragment fragmentMenu;
    MenuInfoFragment menuInfoFragment;
    MenuPhotoFragment menuPhotoFragment;
    MenuCommentFragment menuCommentFragment;
    CircleImageView photo_profile;
    ToggleButton btnMenu,btnComment,btnPhoto,btnInfo;
    Button btn_edit;
    TextView name_card,tv_description,change_mode,title_menu;
    RequestQueue requestQueue;
    AlertDialog dialog;
    Button add_product;

    StringRequest stringRequest;

    CardChef cardChef=null;


    SupplierManager supplierManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        add_product = findViewById(R.id.add_product);
        toolbar.setBackgroundColor(getResources().getColor(R.color.grey_sup));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(number_menu==1){
                    startActivity(new Intent(getApplicationContext(),ProductActivity.class));
                }else{
                    Toast.makeText(SupplierActivity.this,"Show Add info",Toast.LENGTH_LONG).show();
                }


            }
        });


        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navegador));

        supplierManager = SupplierManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));

        name_card = findViewById(R.id.name_card);
        photo_profile = findViewById(R.id.photo_profile);
        btnMenu = findViewById(R.id.btnMenu);
        btnComment = findViewById(R.id.btnComment);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnInfo = findViewById(R.id.btnInfo);
        btn_edit = findViewById(R.id.btn_edit);

        tv_description=findViewById(R.id.tv_description);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_supplier);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        headerView = navigationView.getHeaderView(0);

        Utils.imageProfile = headerView.findViewById(R.id.imageProfile);
        title_menu = headerView.findViewById(R.id.title_menu);

        change_mode = headerView.findViewById(R.id.change_mode);

        change_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SupplierActivity.this,MainActivity.class));
                finish();
            }
        });


        photo_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(permissionCamera()) {
                    ShowOptions();

                }
            }
        });

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navegador);

        navigationView.setNavigationItemSelectedListener(this);
        btnMenu.setChecked(true);
        btnMenu.setTextColor(getResources().getColor(R.color.white));


        fragmentMenu = new MenuSupplierFragment();
        menuInfoFragment=new MenuInfoFragment();
        menuPhotoFragment=new MenuPhotoFragment();
        menuCommentFragment=new MenuCommentFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container_fragment,fragmentMenu).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();


        prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            access_token = prefs.getString("access_token", "");//"No name defined" is the default value.

        }else{
            Utils.redirecTo(SupplierActivity.this,LoginActivity.class);
        }

        getDataUser(Utils.AccessToken(this));
    }

    private void getDataUser(final String access_token_local) {
        editor = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        String url=IP+"/api/user";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    String photo="";
                    JSONObject data = new JSONObject(response);
                    String artistic="";
                    if(data.getString("artistic_name").length()>10){
                        String name[] = data.getString("artistic_name").split(" ");
                        artistic = name[0];
                    }else{
                        artistic = data.getString("artistic_name");
                    }

                    Supplier supplier=new Supplier();
                    supplier.setName(data.getString("artistic_name"));
                    title_menu.setText(artistic);
                    name_card.setText(artistic);
                    supplier.setDescription(data.getString("description"));
                    tv_description.setText(data.getString("description"));

                    Utils.setItem(SupplierActivity.this,"stakeholder_id",data.getString("stakeholder_id"));

                    if(data.getString("photo").equals("null")){

                        Utils.imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.camera));
                    }else{
                        editor.putString("photo", data.getString("photo"));

                        photo = (Utils.getItem(SupplierActivity.this,"provider").equals("google"))?data.getString("photo"):IP+"/"+data.getString("photo");

                        Glide
                                .with(getApplicationContext())
                                //.load(IP+"/"+data.getString("photo"))
                                .load(photo)
                                .centerCrop()
                                .placeholder(R.drawable.ic_profile)
                                .into(Utils.imageProfile);
                        //Picasso.get().load(IP+"/"+data.getString("photo")).into(Utils.imageProfile);
                    }



                    if(data.getString("photo").equals("null")){
                        Utils.imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.camera));
                    }else{
                        supplier.setPhoto(data.getString("photo"));
                        Glide
                                .with(getApplicationContext())
                                .load(data.getString("photo"))
                                .centerCrop()
                                .placeholder(R.drawable.ic_profile)
                                .into(photo_profile);
                        //Picasso.get().load(IP+"/"+data.getString("photo")).into(Utils.imageProfile);
                    }
                    supplierManager.saveSuppplier(supplier);

                }catch (JSONException e){
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
                params.put("Authorization", "Bearer " + access_token_local);

                return params;
            }
        };

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void ShowOptions(){
        final CharSequence[] options={"Tomar foto","Cargar imagen","Cancelar"};
        final AlertDialog.Builder alerBuilder=new AlertDialog.Builder(SupplierActivity.this);
        alerBuilder.setTitle("Cambiar Foto");
        alerBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Tomar foto")){
                    takePhoto();
                }else{
                    if(options[which].equals("Cargar imagen")){
                        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/");
                        startActivityForResult(i.createChooser(i,"Seleccione la aplicación"),COD_SELECTED);
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

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path_selected);
                        photo_profile.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case COD_PHOTO:
                    MediaScannerConnection.scanFile(SupplierActivity.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });


                    bitmap = BitmapFactory.decodeFile(path);

                    updatePhoto(bitmap);

                    photo_profile.setImageBitmap(bitmap);
                    break;
            }
        }
    }
    void updatePhoto(Bitmap photo){
        String url=IP+"/api/upload-photo";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    Glide
                            .with(getApplicationContext())
                            .load(IP+"/"+obj.getString("photo"))
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile)
                            .into(photo_profile);

                    Glide
                            .with(getApplicationContext())
                            .load(IP+"/"+obj.getString("photo"))
                            .centerCrop()
                            .placeholder(R.drawable.ic_profile)
                            .into(Utils.imageProfile);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SupplierActivity.this,"Problemas con la solicitud",Toast.LENGTH_LONG).show();
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
                if(!image.isEmpty()){
                    params.put("image",image);
                }

                return params;
            }

        };
        requestQueue= Volley.newRequestQueue(SupplierActivity.this);
        requestQueue.add(stringRequest);
    }

    public void takePhoto(){
        File fileImage=new File(Environment.getExternalStorageDirectory(),FOLDER_IMAGE);
        boolean is_created = fileImage.exists();

        String name_image="";

        if(is_created == false){
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






    public void getDataToken(final String token_google){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SupplierActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();


                if(!newToken.equals(token_google)){

                    reNewToken(newToken);
                }else{
                    Utils.setItem(SupplierActivity.this,"token_google",token_google);
                }
            }
        });
    }

    private void reNewToken(final String newToken) {
        String url=IP+"/api/google";
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.setItem(SupplierActivity.this,"TOKEN_GOOGLE",newToken);
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
                params.put("Authorization", "Bearer " + Utils.AccessToken(SupplierActivity.this));

                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("token_google",newToken);
                return params;
            }
        };

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    public void onClick(View v){
        android.support.v4.app.FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putSerializable("supplier",cardChef);

        switch (v.getId()){
            case R.id.btnInfo:
                number_menu=4;
                add_product.setVisibility(View.GONE);
                btnInfo.setChecked(true);
                btnInfo.setTextColor(getResources().getColor(R.color.white));
                btnMenu.setChecked(false);
                btnComment.setChecked(false);
                btnPhoto.setChecked(false);
                btnMenu.setTextColor(getResources().getColor(R.color.mainBackground));
                btnComment.setTextColor(getResources().getColor(R.color.mainBackground));
                btnPhoto.setTextColor(getResources().getColor(R.color.mainBackground));
                menuInfoFragment.setArguments(args);
                transaction.replace(R.id.container_fragment,menuInfoFragment);
                break;
            case R.id.btnPhoto:
                number_menu=3;
                add_product.setVisibility(View.VISIBLE);
                btnPhoto.setChecked(true);
                btnPhoto.setTextColor(getResources().getColor(R.color.white));
                btnMenu.setChecked(false);
                btnComment.setChecked(false);
                btnInfo.setChecked(false);
                btnComment.setTextColor(getResources().getColor(R.color.mainBackground));
                btnMenu.setTextColor(getResources().getColor(R.color.mainBackground));
                btnInfo.setTextColor(getResources().getColor(R.color.mainBackground));
                menuPhotoFragment.setArguments(args);
                transaction.replace(R.id.container_fragment,menuPhotoFragment);
                break;
            case R.id.btnComment:
                number_menu=2;
                add_product.setVisibility(View.GONE);
                btnComment.setChecked(true);
                btnMenu.setChecked(false);
                btnPhoto.setChecked(false);
                btnInfo.setChecked(false);
                btnMenu.setTextColor(getResources().getColor(R.color.mainBackground));
                btnPhoto.setTextColor(getResources().getColor(R.color.mainBackground));
                btnComment.setTextColor(getResources().getColor(R.color.white));
                btnInfo.setTextColor(getResources().getColor(R.color.mainBackground));
                menuCommentFragment.setArguments(args);
                transaction.replace(R.id.container_fragment,menuCommentFragment);
                break;
            case R.id.btnMenu:
                number_menu=1;
                add_product.setVisibility(View.VISIBLE);
                btnMenu.setChecked(true);
                btnComment.setChecked(false);
                btnPhoto.setChecked(false);
                btnInfo.setChecked(false);
                btnComment.setTextColor(getResources().getColor(R.color.mainBackground));
                btnPhoto.setTextColor(getResources().getColor(R.color.mainBackground));
                btnInfo.setTextColor(getResources().getColor(R.color.mainBackground));
                btnMenu.setTextColor(getResources().getColor(R.color.white));
                fragmentMenu.setArguments(args);
                transaction.replace(R.id.container_fragment,fragmentMenu);
                break;

            case R.id.btn_edit:

                startActivity(new Intent(this, ProfileActivity.class));

               /* AlertDialog.Builder mBuilder=new AlertDialog.Builder(this,R.style.AlertDialogTheme);

                View mView = getLayoutInflater().inflate(R.layout.dialog_edit_field,null);
//                btnCancel= mView.findViewById(R.id.btnCancelRestriction);
                final EditText etName = mView.findViewById(R.id.etName);
                final EditText etDescription = mView.findViewById(R.id.etDescription);

                etDescription.setHint("Description");
                etName.setText(supplierManager.getSupplier().getName());
                etDescription.setText(tv_description.getText().toString());

                mBuilder.setView(mView);
                mBuilder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateName(etName.getText().toString(),etDescription.getText().toString(),1);
                    }
                });

                mBuilder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.cancel();
                    }
                });

                dialog = mBuilder.create();
                dialog.setTitle("Editar");
                dialog.show();*/
                break;
        }

        transaction.commit();
    }

    private void updateName(final String name,final String description,int user_id) {
        String url=IP + "/api/stakeholder/" + user_id+"/description";


        stringRequest=new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("data");
                    name_card.setText(name);
                    tv_description.setText(description);
                    Toast.makeText(SupplierActivity.this,"Descripcipón Editada",Toast.LENGTH_LONG).show();

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
                params.put("description",description);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + Utils.AccessToken(SupplierActivity.this));
                params.put("Accept", "application/json");
                return params;
            }
        };


        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_supplier);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.supplier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders_supplier) {
            startActivity(new Intent(SupplierActivity.this,OrderSupplierActivity.class));
        }
        else if (id == R.id.nav_profile) {
            startActivity(new Intent(SupplierActivity.this, ProfileActivity.class));
        }
        /*} else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_supplier);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
