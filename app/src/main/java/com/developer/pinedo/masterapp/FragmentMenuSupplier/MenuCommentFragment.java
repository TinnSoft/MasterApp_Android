package com.developer.pinedo.masterapp.FragmentMenuSupplier;

import android.app.Activity;
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
import com.developer.pinedo.masterapp.adapter.Supplier.CommentSupAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardChef;
import com.developer.pinedo.masterapp.models.CardComment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuCommentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "MenuCommentFragment";

    private String mParam1;
    private String mParam2;

    private static final String IP = Utils.IP;
    CommentSupAdapterRecicleView chefAdapterRecicleView;

    StringRequest stringRequest;
    RequestQueue requestQueue;

    RecyclerView picturesRecycler;
    Activity activity;

    ArrayList<CardComment> pictures = new ArrayList<>();

    CardChef cardChef=null;

    private OnFragmentInteractionListener mListener;

    public MenuCommentFragment() {
        // Required empty public constructor
    }


    public static MenuCommentFragment newInstance(String param1, String param2) {
        MenuCommentFragment fragment = new MenuCommentFragment();
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
        final View view=inflater.inflate(R.layout.fragment_menu_comment, container, false);
        activity=getActivity();


       /* ImageView btn_plus = view.findViewById(R.id.btn_plus);
        btn_plus.setOnClickListener(new View.OnClickListener() {
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


    public void listComments(){


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

                        for (int i=0;i<jsonArray.length();i++) {
                            img = IP + "/images/default-product.jpg";
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);
                            user=jsonObject.getString("user");
                            user=user.substring(0,1).toUpperCase().concat(user.substring(1, user.length()));
                            pictures.add(new CardComment(user,
                                    jsonObject.getString("title"),
                                    jsonObject.getString("description")));

                        }

                        chefAdapterRecicleView = new CommentSupAdapterRecicleView(pictures,R.layout.card_item_comment,getActivity());
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
