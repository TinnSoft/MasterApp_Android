package com.developer.pinedo.masterapp.FragmentMenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.SubcategoryAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubcategoryFragment extends Fragment implements SearchView.OnQueryTextListener,View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "SUB";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    List<String> catetories=new ArrayList<>();
    ToggleButton btnBurger,btnChicken,btnPasta,btnPizza;

    ImageView btnFilter;
    RequestQueue requestQueue;
    RecyclerView picturesRecycler;
    ProgressDialog progressDialog;
    StringRequest stringRequest;
    ArrayList<CardCategory> pictures = new ArrayList<>();

    private SubcategoryAdapterRecicleView chefAdapterRecicleView;
    private static final String IP = Utils.IP;

    public SubcategoryFragment() {

    }


    public static SubcategoryFragment newInstance(String param1, String param2) {
        SubcategoryFragment fragment = new SubcategoryFragment();
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

    public  void listCategories(){
        catetories.add("pizza");
        catetories.add("burger");
        catetories.add("pasta");
        catetories.add("chicken");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subcategory, container, false);
        btnBurger = view.findViewById(R.id.btn_burger);
        btnBurger.setOnClickListener(this);
        btnChicken = view.findViewById(R.id.btn_chicken);
        btnChicken.setOnClickListener(this);
        btnPasta = view.findViewById(R.id.btn_pasta);
        btnPasta.setOnClickListener(this);
        btnPizza= view.findViewById(R.id.btn_pizza);
        btnPizza.setOnClickListener(this);

        btnFilter= view.findViewById(R.id.btn_filter);
        listCategories();
        requestQueue= Volley.newRequestQueue(getActivity());
        profiles();
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        picturesRecycler=view.findViewById(R.id.chefRecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);

        SearchView txtsearch = view.findViewById(R.id.txtsearch);
        txtsearch.setIconifiedByDefault(false);
        txtsearch.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        txtsearch.setSubmitButtonEnabled(true);
        txtsearch.setQueryHint("Que estas buscando");
        return view;
    }

    public void profiles(){
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Espere por favor...");
        progressDialog.show();

        String url=IP + "/api/subcategory";


        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);


                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0,price=0,cat=0;
                    String category="",price_st="",img="";
                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);
                            img = IP + "/"+jsonObject.getString("url");

                            pictures.add(new CardCategory(jsonObject.getString("id"),img,jsonObject.getString("title"),jsonObject.getString("slug")));

                        }

                        chefAdapterRecicleView=new SubcategoryAdapterRecicleView(pictures,R.layout.card_view_subcategory,getActivity());
                        picturesRecycler.setAdapter(chefAdapterRecicleView);
                        progressDialog.hide();

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
                        progressDialog.hide();

                    }
                });

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        chefAdapterRecicleView.filter(newText);
        return true;
    }

    @Override
    public void onClick(View view) {

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
