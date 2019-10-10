package com.developer.pinedo.masterapp.FragmentsMenuChef;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.PhotoAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PhotoFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PhotoFragment";
    private static final String IP = Utils.IP;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    PhotoAdapterRecicleView chefAdapterRecicleView;
    RecyclerView picturesRecycler;
    View view;

    ProgressDialog progressDialog;
    StringRequest stringRequest;
    RequestQueue requestQueue;


    ArrayList<CardPhoto> pictures = new ArrayList<>();

    public PhotoFragment() {

    }

    public static PhotoFragment newInstance(String param1, String param2) {
        PhotoFragment fragment = new PhotoFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_photo, container, false);

        ImageView btnGrid = view.findViewById(R.id.btnGrid);
        ImageView btnOne = view.findViewById(R.id.btnOne);

        btnGrid.setOnClickListener(this);
        btnOne.setOnClickListener(this);

        getRecycler(view);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGrid:
                Utils.visualization=Utils.GRID;
                break;
            case R.id.btnOne:
                Utils.visualization=Utils.LIST;
                break;
        }

        getRecycler(view);
    }


    public void getRecycler(View view){
        picturesRecycler=view.findViewById(R.id.photoRecycler);

        if(Utils.visualization==Utils.LIST){
//            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext());
//            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            picturesRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }else{
            picturesRecycler.setLayoutManager(new GridLayoutManager(view.getContext(),2));
        }
        listPhotos();
    }

    public void listPhotos(){
        String url=IP + "/api/photos/"+Utils.getItem(getActivity(),"stakeholder_id");


        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("results");

                    String img="",img_sup="";
                    try {
                        pictures.removeAll(pictures);

                        for (int i=0;i<jsonArray.length();i++) {
                            //img = IP + "/images/default-product.jpg";
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);
                            img = IP +"/" +jsonObject.getString("url_image");
                            img_sup = IP +"/" +jsonObject.getString("photo");

                            pictures.add(new CardPhoto(Integer.parseInt(jsonObject.getString("id")),img,jsonObject.getString("title"),
                                    jsonObject.getString("price"),
                                    jsonObject.getString("description"),
                                    jsonObject.getString("supplier"),
                                    img_sup
                                    ));

                        }


                        chefAdapterRecicleView = new PhotoAdapterRecicleView(pictures,R.layout.card_item_photo,getActivity());
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




    // TODO: Rename method, update argument and hook method into UI event
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
