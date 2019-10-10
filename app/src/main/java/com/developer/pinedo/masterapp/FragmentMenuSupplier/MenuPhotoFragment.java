package com.developer.pinedo.masterapp.FragmentMenuSupplier;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuPhotoFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MenuPhotoFragment";

    private static final String IP = Utils.IP;

    private String mParam1;
    private String mParam2;

    PhotoAdapterRecicleView chefAdapterRecicleView;
    RecyclerView picturesRecycler;
    View view;

    ProgressDialog progressDialog;
    StringRequest stringRequest;
    RequestQueue requestQueue;

    ArrayList<CardPhoto> pictures = new ArrayList<>();




    private OnFragmentInteractionListener mListener;

    public MenuPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuPhotoFragment newInstance(String param1, String param2) {
        MenuPhotoFragment fragment = new MenuPhotoFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_photo, container, false);


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
                    String category="",price_st="",img="";
                    try {
                        pictures.removeAll(pictures);

                        for (int i=0;i<jsonArray.length();i++) {
                            //img = IP + "/images/default-product.jpg";
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);
                            img = IP +"/" +jsonObject.getString("url_image");
                            pictures.add(new CardPhoto(Integer.parseInt(jsonObject.getString("id")),img,jsonObject.getString("title"),
                                    jsonObject.getString("price"),
                                    jsonObject.getString("description"),
                                    jsonObject.getString("supplier"),
                                    jsonObject.getString("photo")
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
