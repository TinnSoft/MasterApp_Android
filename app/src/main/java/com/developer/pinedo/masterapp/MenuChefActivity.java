package com.developer.pinedo.masterapp;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.FragmentsMenuChef.CommentFragment;
import com.developer.pinedo.masterapp.FragmentsMenuChef.InfoFragment;
import com.developer.pinedo.masterapp.FragmentsMenuChef.MenuFragment;
import com.developer.pinedo.masterapp.FragmentsMenuChef.PhotoFragment;
import com.developer.pinedo.masterapp.database.App;
import com.developer.pinedo.masterapp.entities.Product;
import com.developer.pinedo.masterapp.models.CardChef;
import com.developer.pinedo.masterapp.models.DataUserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;

public class MenuChefActivity extends AppCompatActivity implements
        MenuFragment.OnFragmentInteractionListener,
        CommentFragment.OnFragmentInteractionListener,
        InfoFragment.OnFragmentInteractionListener,
        PhotoFragment.OnFragmentInteractionListener, View.OnClickListener {

    private static final String TAG= "MenuChefActivity";
    private static final String IP = Utils.IP;
    MenuFragment fragmentMenu;
    CommentFragment fragmentComment;
    InfoFragment fragmentInfo;
    PhotoFragment fragmentPhoto;
    CircleImageView photo_profile;
    ToggleButton btnMenu,btnComment,btnPhoto,btnInfo,btn_follow;
    TextView name_card;

    SharedPreferences prefs;

    App db;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    int stakeholder_id;
    String access_token,restoredText;
    Activity activity;
    int followers;
    ToggleButton btn_program;

    TextView tv_services,tv_follower,name_card_scroll;
    CardChef cardChef=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_chef);
        activity=this;
        showToolbar("",true);
         Bundle param=getIntent().getExtras();

         /*final App db= Room.databaseBuilder(getApplicationContext(),App.class,"production")
                 .allowMainThreadQueries()
                 .build();*/

         //db.userDao().insertAll(new Product("test","test description","10000","/proadasd/asdasd.pnh",123));

         //List<Product> product = db.userDao().getAllProduct();

        name_card = findViewById(R.id.name_card);
        photo_profile = findViewById(R.id.photo_profile);
        btnMenu = findViewById(R.id.btnMenu);
        btnComment = findViewById(R.id.btnComment);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnInfo = findViewById(R.id.btnInfo);
        btn_follow = findViewById(R.id.btn_follow);
        btn_program= findViewById(R.id.btn_program);
        name_card_scroll= findViewById(R.id.name_card_scroll);

        tv_services=findViewById(R.id.tv_services);
        tv_follower=findViewById(R.id.tv_follower);

        btnMenu.setChecked(true);
        btnMenu.setTextColor(getResources().getColor(R.color.white));

        DataUserProfile user=Utils.getDataUser(this);

       /*photo_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MenuChefActivity.this,ProfileActivity.class);
                startActivity(i);
            }
        });*/


        btn_follow.setOnClickListener(this);


         if(param != null) {
             cardChef = (CardChef) param.getSerializable("chef");
             stakeholder_id=cardChef.getCard_id();
             Utils.setItem(this,"stakeholder_id",String.valueOf(stakeholder_id));
//             TextView name_card = findViewById(R.id.name_card);
//             TextView card_point = findViewById(R.id.card_point);
             String[] name = (cardChef.getCard_name()).split(" ");
             name_card.setText(name[0]);
             name_card_scroll.setText(name[0]);

//             card_point.setText(cardChef.getCard_star());
             Glide
                     .with(this.activity.getApplicationContext())
                     .load(cardChef.getCard_photo())
                     .centerCrop()
                     .placeholder(R.drawable.ic_profile)
                     .into(photo_profile);

             getDataStakeholder(stakeholder_id);
         }




        fragmentMenu=new MenuFragment();
        fragmentComment=new CommentFragment();
        fragmentInfo=new InfoFragment();
        fragmentPhoto=new PhotoFragment();

        db = Room.databaseBuilder(getApplicationContext(),App.class,"production")
                .allowMainThreadQueries()
                .build();
        //db.productDao().deleteAll();

        getSupportFragmentManager().beginTransaction().add(R.id.container_fragment,fragmentMenu).commit();

        List<Product> list= db.productDao().getAllProduct();

        if(list.size()>0){
            btn_program.setEnabled(true);
            btn_program.setTextColor(getColor(R.color.green_background));
        }else{
            btn_program.setEnabled(false);
            btn_program.setTextColor(getColor(R.color.grey_sup));
        }


        btn_program.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ServicesActivity.class);
                i.putExtra("supplier",cardChef);
                startActivity(i);
            }
        });

       /* btn_program.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    btn_program.setTextColor(getColor(R.color.green_background));
                    Intent i = new Intent(getApplicationContext(),ServicesActivity.class);
                    i.putExtra("supplier",cardChef);
                    startActivity(i);
                }else{
                    btn_program.setTextColor(getColor(R.color.grey_sup));
                }



            }
        });*/

    }


    private void getDataStakeholder(int stakeholder_id) {
        String url=IP + "/api/stakeholder/"+stakeholder_id+"/edit";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject data=new JSONObject(response);

                    followers = Integer.parseInt(data.getJSONObject("results").getString("follower"));

                    tv_services.setText(data.getJSONObject("results").getString("services"));
                    tv_follower.setText(String.valueOf(followers));

                    if(data.getString("user_like").equals("true")){
                        btn_follow.setChecked(true);
                        btn_follow.setTextColor(getResources().getColor(R.color.mainBackground));
                    }else{
                        btn_follow.setChecked(false);
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
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + Utils.AccessToken(activity));
                params.put("Accept", "application/json");
                return params;
            }
        };


        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    public void showToolbar(String titulo,boolean upButton){
        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void onClick(View v){

        android.support.v4.app.FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putSerializable("supplier",cardChef);

       switch (v.getId()){
            case R.id.btnInfo:
                btn_program.setVisibility(View.GONE);
                btnInfo.setChecked(true);
                btnInfo.setTextColor(getResources().getColor(R.color.white));
                btnMenu.setChecked(false);
                btnComment.setChecked(false);
                btnPhoto.setChecked(false);
                btnMenu.setTextColor(getResources().getColor(R.color.mainBackground));
                btnComment.setTextColor(getResources().getColor(R.color.mainBackground));
                btnPhoto.setTextColor(getResources().getColor(R.color.mainBackground));
                fragmentInfo.setArguments(args);
                transaction.replace(R.id.container_fragment,fragmentInfo);
                break;
            case R.id.btnPhoto:
                btn_program.setVisibility(View.GONE);
                btnPhoto.setChecked(true);
                btnPhoto.setTextColor(getResources().getColor(R.color.white));
                btnMenu.setChecked(false);
                btnComment.setChecked(false);
                btnInfo.setChecked(false);
                btnComment.setTextColor(getResources().getColor(R.color.mainBackground));
                btnMenu.setTextColor(getResources().getColor(R.color.mainBackground));
                btnInfo.setTextColor(getResources().getColor(R.color.mainBackground));
                fragmentPhoto.setArguments(args);
                transaction.replace(R.id.container_fragment,fragmentPhoto);
                break;
            case R.id.btnComment:
                btn_program.setVisibility(View.GONE);
                btnComment.setChecked(true);
                btnMenu.setChecked(false);
                btnPhoto.setChecked(false);
                btnInfo.setChecked(false);
                btnMenu.setTextColor(getResources().getColor(R.color.mainBackground));
                btnPhoto.setTextColor(getResources().getColor(R.color.mainBackground));
                btnComment.setTextColor(getResources().getColor(R.color.white));
                btnInfo.setTextColor(getResources().getColor(R.color.mainBackground));
                fragmentComment.setArguments(args);
                transaction.replace(R.id.container_fragment,fragmentComment);
                break;
            case R.id.btnMenu:
                btn_program.setVisibility(View.VISIBLE);
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
           case R.id.btn_follow:

               if(btn_follow.isChecked()){
                   followers++;
                   tv_follower.setText(String.valueOf(followers));
                   btn_follow.setTextColor(getResources().getColor(R.color.mainBackground));
               }else{
                   followers--;
                   tv_follower.setText(String.valueOf(followers));
                   btn_follow.setTextColor(getResources().getColor(R.color.white));
               }

               LikeOrdisLike(stakeholder_id);
               break;


        }

        transaction.commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void LikeOrdisLike(int stakeholder_id) {
        String url=IP + "/api/like-stakeholder/" + stakeholder_id;

        stringRequest=new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);

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
                params.put("Authorization", "Bearer " + Utils.AccessToken(activity));
                params.put("Accept", "application/json");
                return params;
            }
        };


        requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
