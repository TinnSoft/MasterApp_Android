package com.developer.pinedo.masterapp.FragmentMenuSupplier;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.ExperienceActivity;
import com.developer.pinedo.masterapp.StudyActivity;
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

public class MenuInfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView btn_add_study,btn_add_experience;
    ProgressBar progress_bar;
    StringRequest stringRequest;
    private static final String IP = Utils.IP;
    ArrayList<CardExtraInfo> pictures = new ArrayList<>();
    ExtraInfoAdapterRecicleView chefAdapterRecicleView,ExperienceAdapterRecicleView;
    RecyclerView picturesRecycler,experienceRecycler;

    RequestQueue requestQueue;
    private OnFragmentInteractionListener mListener;

    public MenuInfoFragment() {
        // Required empty public constructor
    }


    public static MenuInfoFragment newInstance(String param1, String param2) {
        MenuInfoFragment fragment = new MenuInfoFragment();
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
        View view=inflater.inflate(R.layout.fragment_menu_info, container, false);

        btn_add_study = view.findViewById(R.id.btn_add_study);
        btn_add_experience = view.findViewById(R.id.btn_add_experience);

        progress_bar=view.findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);

        picturesRecycler = view.findViewById(R.id.studyRecycler);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);

        experienceRecycler = view.findViewById(R.id.experienceRecycler);
        LinearLayoutManager linearLayoutManagerExp=new LinearLayoutManager(view.getContext());
        linearLayoutManagerExp.setOrientation(LinearLayoutManager.VERTICAL);
        experienceRecycler.setLayoutManager(linearLayoutManagerExp);


        btn_add_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.VISIBLE);
                btn_add_study.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(),StudyActivity.class));
            }
        });

        btn_add_experience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.VISIBLE);
                btn_add_experience.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(),ExperienceActivity.class));
            }
        });

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

                        ExperienceAdapterRecicleView = new ExtraInfoAdapterRecicleView(pictures,R.layout.card_view_study_sup,getActivity());
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
                    Log.e("JORGE",response);

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

                        chefAdapterRecicleView = new ExtraInfoAdapterRecicleView(pictures,R.layout.card_view_study_sup,getActivity());
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
