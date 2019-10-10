package com.developer.pinedo.masterapp.FragmentsMenuChef;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.ServicesActivity;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.ChefMenuAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardMenuChef;

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

import okhttp3.internal.Util;


public class MenuFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MenuFragment";

    private String mParam1;
    private String mParam2;

    ProgressDialog progressDialog;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    private static final String IP = Utils.IP;

    List<String> catetories=new ArrayList<>();
    RecyclerView picturesRecycler;
    List<String> f=new ArrayList();
    ArrayList<CardMenuChef> pictures = new ArrayList<>();
    ChefMenuAdapterRecicleView chefAdapterRecicleView;

    SharedPreferences prefs;

    JsonObjectRequest jsonObjectRequest;
    String restoredText,access_token,provider;
    View view;
    Button btn_program;

    private OnFragmentInteractionListener mListener;
    private FragmentActivity myContext;

    public MenuFragment() {

    }

    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public  void listCategories(){
        catetories.add("pizza");
        catetories.add("burger");
        catetories.add("pasta");
        catetories.add("chicken");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_menu, container, false);

        listCategories();

        picturesRecycler = view.findViewById(R.id.menuRecycler);
        btn_program= view.findViewById(R.id.btn_program);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);
        listProducts();

        return view;
    }

    public void MenuPhotos(){
        String url="";
    }



    public void listProducts(){


        //String url=IP + "/api/product";
        String url=IP + "/api/product-stakeholder/"+Utils.getItem(getActivity(),"stakeholder_id")+"/"+Utils.getItem(getActivity(),"slug");

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0,price=0,cat=0;
                    String category="", price_st="", img="";

                    try {
                        pictures.removeAll(pictures);

                        for (int i=0;i<jsonArray.length();i++) {
                            img = IP + "/images/default-product.jpg";
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            numero = (int)(Math.random()*5+1);

                            double random = 0 + Math.random() * (5 - 0);
                            Random rand = new Random();
                            price = 1000 + rand.nextInt((100000 - 1000) + 1);
                            cat = 0 + rand.nextInt((3 - 0) + 1);
                            category=catetories.get(cat).toString();
                            price_st = "$ "+NumberFormat.getNumberInstance(Locale.US).format(price);
                            DecimalFormat df = new DecimalFormat("#.0");


                            if(jsonObject.getString("url_image")!=null){
                                img = IP+"/"+jsonObject.getString("url_image");
                            }

                            pictures.add(new CardMenuChef(
                                    Integer.parseInt(jsonObject.getString("id")),
                                    img,
                                    jsonObject.getString("title"),
                                    "1.5",
                                    jsonObject.getString("description"),
                                    jsonObject.getString("price")));
                        }

                        chefAdapterRecicleView = new ChefMenuAdapterRecicleView(pictures,R.layout.card_item_product,getActivity());
                        picturesRecycler.setAdapter(chefAdapterRecicleView);

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
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + Utils.AccessToken(getActivity()));
                params.put("Accept", "application/json");
                return params;
            }
        };

        requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public ArrayList<CardMenuChef> buildPictures(){
        ArrayList<CardMenuChef> pictures = new ArrayList<>();
        pictures.add(new CardMenuChef("http://www.novalandtours.com/images/guide/guilin.jpg",
                "Otra comida", "4.6", "Pasta",
                "$12.000"));

        pictures.add(new CardMenuChef(
                "http://www.novalandtours.com/images/guide/guilin.jpg",
                "Paella", "1.5", "Arroz con pollo",
                "$60.000"));

        pictures.add(new CardMenuChef(
                "http://www.novalandtours.com/images/guide/guilin.jpg",
                "Lasagna", "2.5", "Lasagna",
                "$30.000"));

        pictures.add(new CardMenuChef(
                "http://www.novalandtours.com/images/guide/guilin.jpg",
                "Papas", "5.0", "Papas",
                "$50.000"));
        pictures.add(new CardMenuChef(
                "http://www.novalandtours.com/images/guide/guilin.jpg",
                "Otra cosa", "4.5", "Otra cosa",
                "$10.000"));

        return pictures;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onClick(View view){
        Intent i = new Intent(myContext, ServicesActivity.class);
        startActivity(i);
        //Toast.makeText(myContext.getParent(),"tes",Toast.LENGTH_LONG).show();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
