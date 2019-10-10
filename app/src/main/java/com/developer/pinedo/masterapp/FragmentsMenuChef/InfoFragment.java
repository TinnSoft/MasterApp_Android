package com.developer.pinedo.masterapp.FragmentsMenuChef;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.ExtraInfoAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardExtraInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "InfoFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog progressDialog;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    private static final String IP = Utils.IP;
    ArrayList<CardExtraInfo> pictures = new ArrayList<>();
    ExtraInfoAdapterRecicleView chefAdapterRecicleView,ExperienceAdapterRecicleView;
    RecyclerView picturesRecycler,experienceRecycler;

    private OnFragmentInteractionListener mListener;

    public InfoFragment() {

    }

    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public  void typeView(View view){

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
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        picturesRecycler = view.findViewById(R.id.studyRecycler);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);

        experienceRecycler = view.findViewById(R.id.experienceRecycler);
        LinearLayoutManager linearLayoutManagerExp=new LinearLayoutManager(view.getContext());
        linearLayoutManagerExp.setOrientation(LinearLayoutManager.VERTICAL);
        experienceRecycler.setLayoutManager(linearLayoutManagerExp);


        listStudy();
        listExperience();
        return view;
    }


    public void listExperience(){
        String url=IP + "/api/experience";

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

                            pictures.add(new CardExtraInfo(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("description"),
                                    jsonObject.getString("business"),
                                    jsonObject.getString("date_init"),
                                    jsonObject.getString("date_end"),
                                    jsonObject.getString("finished"),
                                    Integer.parseInt(jsonObject.getString("type_information_id"))
                            ));
                        }

                        ExperienceAdapterRecicleView = new ExtraInfoAdapterRecicleView(pictures,R.layout.card_view_study,getActivity());
                        experienceRecycler.setAdapter(ExperienceAdapterRecicleView);

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

    public void listStudy(){
        String url=IP + "/api/study";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0;
                    String img="";
                    try {
                        pictures.removeAll(pictures);

                        for (int i=0;i<jsonArray.length();i++) {
                            img = IP + "/images/default-product.jpg";
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            pictures.add(new CardExtraInfo(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("description"),
                                    jsonObject.getString("business"),
                                    jsonObject.getString("date_init"),
                                    jsonObject.getString("date_end"),
                                    jsonObject.getString("finished"),
                                    Integer.parseInt(jsonObject.getString("type_information_id"))
                                    ));
                        }

                        chefAdapterRecicleView = new ExtraInfoAdapterRecicleView(pictures,R.layout.card_view_study,getActivity());
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
