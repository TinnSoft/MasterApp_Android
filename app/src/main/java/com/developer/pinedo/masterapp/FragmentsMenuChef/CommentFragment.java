package com.developer.pinedo.masterapp.FragmentsMenuChef;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.dialogs.CommentDialogFragment;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.CommentAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardChef;
import com.developer.pinedo.masterapp.models.CardComment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CommentFragment extends Fragment implements CommentDialogFragment.CommentDialogListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CommentFragment";

    private String mParam1;
    private String mParam2;

    ProgressDialog progressDialog;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    ArrayList<CardComment> pictures = new ArrayList<>();
    private static final String IP = Utils.IP;
    CommentAdapterRecicleView chefAdapterRecicleView;
    RecyclerView picturesRecycler;
    Activity activity;
    TextView tv_message;
    ProgressBar progressBar;

    CardChef cardChef=null;

    private OnFragmentInteractionListener mListener;

    public CommentFragment() {

    }

    public static CommentFragment newInstance(String param1, String param2) {
        CommentFragment fragment = new CommentFragment();
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
        final View view =inflater.inflate(R.layout.fragment_comment, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        tv_message = view.findViewById(R.id.tv_message);
        activity = getActivity();

       /* btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                CommentDialogFragment newFrament = new CommentDialogFragment();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFrament).addToBackStack(null).commit();
            }
        });*/

        picturesRecycler=view.findViewById(R.id.commentRecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);
        listComments();

        Bundle param=getActivity().getIntent().getExtras();

        if(param != null) {
            cardChef = (CardChef) param.getSerializable("cardChef");
        }

        return view;
    }


    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    public void listComments(){

        progressBar.setVisibility(View.VISIBLE);
        String url=IP + "/api/comment-stakeholder/"+Utils.getItem(getActivity(),"stakeholder_id");

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject data=new JSONObject(response);
                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0,price=0,cat=0;
                    String category="",price_st="",img="",user="";
                    try {
                        pictures.removeAll(pictures);
                        if(jsonArray.length()>0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                img = IP + "/images/default-product.jpg";
                                JSONObject jsonObject = null;
                                jsonObject = jsonArray.getJSONObject(i);
                                user = jsonObject.getString("user");
                                user = user.substring(0, 1).toUpperCase().concat(user.substring(1, user.length()));
                                pictures.add(new CardComment(user,
                                        jsonObject.getString("title"),
                                        jsonObject.getString("description")));

                            }
                        }else{
                            tv_message.setVisibility(View.VISIBLE);
                        }

                        progressBar.setVisibility(View.GONE);
                        chefAdapterRecicleView = new CommentAdapterRecicleView(pictures,R.layout.card_item_comment,getActivity());
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
                params.put("Authorization", "Bearer " + Utils.AccessToken(activity));
                params.put("Accept", "application/json");
                return params;
            }
        };;

        requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }


    public ArrayList<CardComment> buildPictures(){
        ArrayList<CardComment> pictures = new ArrayList<>();
        pictures.add(new CardComment("Daniel polanco","Experiencia Pesima","orem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and m"));
        pictures.add(new CardComment("Natalia Molina","Buenisimo","orem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and m"));
        pictures.add(new CardComment("Juana","Ricoo","orem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and m"));

        return pictures;
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

    @Override
    public void applyTexts(String title, String body) {
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
