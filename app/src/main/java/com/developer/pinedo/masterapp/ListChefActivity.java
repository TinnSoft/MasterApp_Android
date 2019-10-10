package com.developer.pinedo.masterapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.developer.pinedo.masterapp.Client.MainActivity;
import com.developer.pinedo.masterapp.adapter.ChefAdapterRecicleView;
import com.developer.pinedo.masterapp.adapter.FilterAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardCategory;
import com.developer.pinedo.masterapp.models.CardChef;
import com.developer.pinedo.masterapp.models.CardFilters;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
//import com.mercadopago.android.px.internal.callbacks.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ListChefActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "ListChefFragment";
    StringRequest stringRequest;
    ArrayList<CardChef> pictures = new ArrayList<>();
    ArrayList<CardFilters> filters = new ArrayList<>();

    InputMethodManager imm;

    ToggleButton btnBurger,btnChicken,btnPasta,btnPizza,btnFilter;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    List<String> catetories=new ArrayList<>();
    RecyclerView picturesRecycler,filterRecycler;

    List<String> f=new ArrayList();
    TextView main_title,filter_price,filter_goal,filter_services;
    SharedPreferences prefs;

    TextView tv_message;
    ProgressBar progresss_recycler;

    private ChefAdapterRecicleView chefAdapterRecicleView;

    private FilterAdapterRecicleView filterAdapterRecicleView;

    private static final String IP = Utils.IP;
    String restoredText,access_token,provider,slug="";
    CardCategory cardCategory;
    CardView card_filter;
    GoogleApiClient googleApiClient;

    SearchView txtsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chef);

        Bundle param=getIntent().getExtras();
        String title="Test title",id="";

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        showToolbar("",true);

        if (param != null) {

            cardCategory = (CardCategory) param.getSerializable("chef");
            id = cardCategory.getCard_id();
            title = cardCategory.getCard_title();
            slug = cardCategory.getCard_slug();
        }

        //slug=(slug.isEmpty())?Utils.getItem(ListChefActivity.this,"slug"):slug;
        slug = Utils.getItem(ListChefActivity.this,"slug");
        title = Utils.getItem(ListChefActivity.this,"title");

        filter_price = findViewById(R.id.filter_price);
        filter_goal = findViewById(R.id.filter_goal);
        filter_services = findViewById(R.id.filter_services);
        txtsearch = findViewById(R.id.txtsearch);



        btnBurger = findViewById(R.id.btn_burger);
        card_filter = findViewById(R.id.card_filter);
        main_title = findViewById(R.id.main_title);
        tv_message = findViewById(R.id.tv_message);

        main_title.setText(title);

        btnFilter= findViewById(R.id.btn_filter);
        progresss_recycler = findViewById(R.id.progresss_recycler);

        requestQueue = Volley.newRequestQueue(this);

        profiles(slug);
        btnFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    card_filter.setVisibility(View.VISIBLE);
                }else{
                    card_filter.setVisibility(View.GONE);
                }
            }
        });

        filter_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                filter_price.setTypeface(boldTypeface);*/
                profiles(slug);

            }
        });

        filter_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progresss_recycler.setVisibility(View.VISIBLE);

            }
        });

        filter_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progresss_recycler.setVisibility(View.VISIBLE);

            }
        });

        filterRecycler=findViewById(R.id.filterRecycler);
        LinearLayoutManager linearLayoutManagerF=new LinearLayoutManager(this);
        linearLayoutManagerF.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterRecycler.setLayoutManager(linearLayoutManagerF);


        picturesRecycler=findViewById(R.id.chefRecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);

        SearchView txtsearch = findViewById(R.id.txtsearch);

        txtsearch.setIconifiedByDefault(false);
        txtsearch.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        txtsearch.setSubmitButtonEnabled(true);

        /*filterRecycler.addOnItemTouchListener(new RecyclerItemClickListener(ListChefActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                f.add(filters.get(position).getCard_slug());
                chefAdapterRecicleView.filterCategory(filters.get(position).getCard_slug());
            }
        }));
*/

        txtsearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }
            }
        });

        /*imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);*/

    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
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


    @Override
    protected void onStart() {
        super.onStart();

        listCategories();





        prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            access_token = prefs.getString("access_token", "");//"No name defined" is the default value.
            provider = prefs.getString("provider", "");

        }else{
            Utils.redirecTo(ListChefActivity.this,LoginActivity.class);
        }
    }


    public  void listCategories(){
        String url=IP + "/api/filter/"+slug;

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("results");

                    int cat=0;
                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);


                            filters.add(new CardFilters(
                                            jsonObject.getString("id"),
                                    jsonObject.getString("url"),jsonObject.getString("title"),
                                            jsonObject.getString("slug")
                                    )
                            );
                        }


                        filterAdapterRecicleView = new FilterAdapterRecicleView(filters,R.layout.cardview_filter,ListChefActivity.this);
                        filterRecycler.setAdapter(filterAdapterRecicleView);



                    } catch (JSONException e) {
                        Toast.makeText(ListChefActivity.this,"error",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    Toast.makeText(ListChefActivity.this,"error2",Toast.LENGTH_SHORT).show();
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
                params.put("Authorization", "Bearer " +access_token);
                return params;
            }};

        requestQueue= Volley.newRequestQueue(ListChefActivity.this);
        requestQueue.add(stringRequest);
       /* catetories.add("pizza");
        catetories.add("burger");
        catetories.add("pasta");
        catetories.add("chicken");*/
    }

    public void profiles(String id){

        pictures.removeAll(pictures);

        progresss_recycler.setVisibility(View.VISIBLE);
        String url=IP + "/api/stakeholder/"+id+"/editAll";
        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);

                    JSONArray jsonArray=data.optJSONArray("results");

                    int numero=0,price=0,cat=0;
                    String category="",price_st="",img="";
                    try {

                        if(jsonArray.length()>0){
                            tv_message.setVisibility(View.GONE);
                            for (int i=0;i<jsonArray.length();i++) {
                                img = IP + "/images/default-user.png";
                                JSONObject jsonObject = null;
                                jsonObject = jsonArray.getJSONObject(i);

                                numero = (int)(Math.random()*5+1);

                                double random = 0 + Math.random() * (5 - 0);
                                Random rand = new Random();
                                price = 1000 + rand.nextInt((100000 - 1000) + 1);

                                price_st = "$ "+NumberFormat.getNumberInstance(Locale.US).format(price);
                                DecimalFormat df = new DecimalFormat("#.0");

                                if(!jsonObject.getString("photo").equals(null)){
                                    img = IP+"/"+jsonObject.getString("photo");
                                }

                                pictures.add(new CardChef(Integer.parseInt(jsonObject.getString("id")),
                                        img,
                                        jsonObject.getString("name"),
                                        String.valueOf(df.format(random)), jsonObject.getString("slug"),
                                        price_st,jsonObject.getString("slug")));
                            }
                        }else{
                            tv_message.setVisibility(View.VISIBLE);
                        }


                        progresss_recycler.setVisibility(View.GONE);

                        chefAdapterRecicleView = new ChefAdapterRecicleView(pictures,R.layout.card_view_chef,ListChefActivity.this);
                        picturesRecycler.setAdapter(chefAdapterRecicleView);

                    } catch (JSONException e) {
                        Toast.makeText(ListChefActivity.this,"error",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    Toast.makeText(ListChefActivity.this,"error2",Toast.LENGTH_SHORT).show();
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

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " +access_token);
                return params;
            }};

        requestQueue= Volley.newRequestQueue(ListChefActivity.this);
        requestQueue.add(stringRequest);

    }

    private void handleCloseSession() {

        if(provider.equals("manual")){
            SharedPreferences.Editor pref = getApplicationContext().getSharedPreferences(Utils.MY_PREFS_NAME, 0).edit();
            pref.clear();
            pref.commit();
            Utils.redirecTo(ListChefActivity.this,LoginActivity.class);
        }else {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        SharedPreferences.Editor pref = getApplicationContext().getSharedPreferences(Utils.MY_PREFS_NAME, 0).edit();
                        pref.clear();
                        pref.commit();
                        Utils.redirecTo(ListChefActivity.this,LoginActivity.class);
                    } else {
                        Toast.makeText(getApplicationContext(), "No se pudo borrar Sesion", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(chefAdapterRecicleView!=null){
            chefAdapterRecicleView.filter(newText);
            return true;
        }else{
            return  false;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
