package com.developer.pinedo.masterapp.Client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.developer.pinedo.masterapp.CardActivity;
import com.developer.pinedo.masterapp.FragmentMenu.CategoriesFragment;
import com.developer.pinedo.masterapp.FragmentMenu.ListChefFragment;
import com.developer.pinedo.masterapp.FragmentMenu.PaymentFragment;
import com.developer.pinedo.masterapp.LoginActivity;
import com.developer.pinedo.masterapp.ProfileActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Supplier.New_supplier.NewSupplierActivity;
import com.developer.pinedo.masterapp.Supplier.SupplierActivity;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.database.App;
import com.developer.pinedo.masterapp.FragmentMenu.ProfileFragment;
import com.developer.pinedo.masterapp.FragmentMenu.SubcategoryFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener,
        ListChefFragment.OnFragmentInteractionListener,
        PaymentFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        CategoriesFragment.OnFragmentInteractionListener,
        SubcategoryFragment.OnFragmentInteractionListener
        {


    GoogleApiClient googleApiClient;
    public final static String TAG="GOOGLE_LOGIN";
    TextView title_menu,change_mode;

    private static final String IP = Utils.IP;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    ProgressDialog progress_bar;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    String restoredText,access_token,provider;
    View headerView;
    InputMethodManager imm;
    ViewGroup viewContainer;

    NavigationView navigationView;
    App db;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        toolbar.setTitle("");

//        toolbar.setLogo(getResources().getDrawable(R.drawable.ic_logo_master_verde));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navegador));

        setSupportActionBar(toolbar);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                ProductDialogFragment newFrament = new ProductDialogFragment();
//
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                transaction.add(android.R.id.content, newFrament).addToBackStack(null).commit();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //fragmento de category
        Fragment fragment = new CategoriesFragment();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navegador_black);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        headerView = navigationView.getHeaderView(0);
        title_menu = headerView.findViewById(R.id.title_menu);
        Utils.imageProfile = headerView.findViewById(R.id.imageProfile);
        //title_email = headerView.findViewById(R.id.title_email);
        change_mode = headerView.findViewById(R.id.change_mode);

        change_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String is_supplier = Utils.getItem(MainActivity.this,"is_supplier");

                if(is_supplier. equals("true")){
                    startActivity(new Intent(MainActivity.this,SupplierActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(MainActivity.this,NewSupplierActivity.class));
                }

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

        db = Room.databaseBuilder(getApplicationContext(),App.class,"production")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        db.productDao().deleteAll();

    }



    public void getDataToken(final String token_google){


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();

                if(!newToken.equals(token_google)){

                    reNewToken(newToken);
                }else{
                    Utils.setItem(MainActivity.this,"token_google",token_google);

                }
            }
        });
    }

    private void reNewToken(final String newToken) {
        String url=IP+"/api/google";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.setItem(MainActivity.this,"TOKEN_GOOGLE",newToken);
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
                params.put("Authorization", "Bearer " + Utils.AccessToken(MainActivity.this));
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


            @Override
    protected void onStart() {
        super.onStart();

        getDataUser(Utils.AccessToken(this));
        getOrder();
        imm.hideSoftInputFromWindow(findViewById(R.id.content_main).getWindowToken(), 0);

        prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            access_token = prefs.getString("access_token", "");//"No name defined" is the default value.
            provider = prefs.getString("provider", "");
        }else{
            Utils.redirecTo(getApplicationContext(),LoginActivity.class);
        }

        /*OptionalPendingResult<GoogleSignInResult> opr=Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if(opr.isDone()){

            GoogleSignInResult result=opr.get();
            handleSignInResult(result);
        }else{

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }

    private void getOrder() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


    private void getDataUser(final String access_token_local) {
        editor = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();

        String url=IP+"/api/user";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject data = new JSONObject(response);

                    String name[] = data.getString("name").split(" ");
                    String photo="";

                    title_menu.setText(name[0]);

                    editor.putString("name", data.getString("name"));
                    editor.putString("email", data.getString("email"));
                    editor.putString("photo", data.getString("photo"));
                    editor.putString("token_google", data.getString("token_google"));

                    if(!data.getString("stakeholder_id").equals("null")){
                        editor.putString("is_supplier", "true");
                        editor.putString("stakeholder_id", data.getString("stakeholder_id"));
                    }else{
                        editor.putString("is_supplier", "false");
                    }

                    editor.apply();


                    if(data.getString("photo").equals("null")){
                        Utils.imageProfile.setImageDrawable(getResources().getDrawable(R.drawable.camera));
                    }else{
                        editor.putString("photo", data.getString("photo"));

                        photo = (Utils.getItem(MainActivity.this,"provider").equals("google"))?data.getString("photo"):IP+"/"+data.getString("photo");

                        Glide
                                .with(getApplicationContext())
                                //.load(IP+"/"+data.getString("photo"))
                                .load(photo)
                                .centerCrop()
                                .placeholder(R.drawable.ic_profile)
                                .into(Utils.imageProfile);
                        //Picasso.get().load(IP+"/"+data.getString("photo")).into(Utils.imageProfile);
                    }

                    getDataToken(data.getString("token_google"));


                }catch (JSONException e){
                    Log.d("JORGE","errpr");
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 401){
                            handleCloseSession();
                        }
                        Log.d("JORGE","errp2r");
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

    private void handleSignInResult(GoogleSignInResult result) {

        if(result.isSuccess()){

            GoogleSignInAccount account=result.getSignInAccount();

//            nameTextView.setText(account.getDisplayName());
//            emailTextView.setText(account.getEmail());
//            idTextView.setText(account.getId());

            Glide
                    .with(getApplicationContext())
                    .load(account.getPhotoUrl().toString())
                    .centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .into(Utils.imageProfile);

            //Picasso.get().load(account.getPhotoUrl().toString()).into(Utils.imageProfile);

        }else{

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        boolean fragmentSelected = false;



        if (id == R.id.nav_orders) {
            Intent i =new Intent(MainActivity.this,OrdersActivity.class);
            startActivity(i);
        }
        /*else if (id == R.id.nav_payment) {
            fragment = new PaymentFragment();
            fragmentSelected = true;
        } else if (id == R.id.nav_history) {
            fragment = new ListChefFragment();
            fragmentSelected = true;
        }*/
        else if (id == R.id.nav_session) {
            handleCloseSession();
        }
        else if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            /*fragment = new ProfileFragment();
            fragmentSelected = true;*/
        }
        /*else if (id == R.id.nav_card) {
            Intent i =new Intent(MainActivity.this,CardActivity.class);
            startActivity(i);
        }*/

        if (fragmentSelected == true) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleCloseSession() {

        if(provider.equals("manual")){
            SharedPreferences.Editor pref = getApplicationContext().getSharedPreferences(Utils.MY_PREFS_NAME, 0).edit();
            pref.clear();
            pref.commit();
            Utils.redirecTo(MainActivity.this,LoginActivity.class);
        }else {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        SharedPreferences.Editor pref = getApplicationContext().getSharedPreferences(Utils.MY_PREFS_NAME, 0).edit();
                        pref.clear();
                        pref.commit();
                        Utils.redirecTo(MainActivity.this,LoginActivity.class);
                    } else {
                        Toast.makeText(getApplicationContext(), "No se pudo borrar Sesion", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
