package com.developer.pinedo.masterapp.FragmentMenu;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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


public class CategoriesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG="CAT";

    private String mParam1;
    private String mParam2;


    List<String> catetories=new ArrayList<>();
    ToggleButton btnBurger,btnChicken,btnPasta,btnPizza;

    //private OnFragmentInteractionListener mListener;
    ImageView btnFilter;
    RequestQueue requestQueue;
    RecyclerView picturesRecycler,petRecycler,bellezaRecycler;
    ProgressDialog progressDialog;
    StringRequest stringRequest;
    ArrayList<CardCategory> pictures,pet,belleza;
    LinearLayoutManager linearLayoutManager,linearLayoutManagerPet,linearLayoutManagerBel;

    private SubcategoryAdapterRecicleView chefAdapterRecicleView,chefAdapterRecicleViewPet,chefAdapterRecicleViewBel;

    private static final String IP = Utils.IP;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
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
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        btnFilter = view.findViewById(R.id.btn_filter);
        requestQueue = Volley.newRequestQueue(getActivity());
        profiles("cook");
/*        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        progressDialog = new ProgressDialog(getActivity());


        progressDialog.setMessage("Espere por favor...");
        //progressDialog.show();

        picturesRecycler=view.findViewById(R.id.chefRecycler);
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);

        pet("pet");
        petRecycler=view.findViewById(R.id.petRecycler);
        linearLayoutManagerPet=new LinearLayoutManager(getActivity());
        linearLayoutManagerPet.setOrientation(LinearLayoutManager.HORIZONTAL);
        petRecycler.setLayoutManager(linearLayoutManagerPet);


        belleza();
        bellezaRecycler = view.findViewById(R.id.belleza_recycler);
        linearLayoutManagerBel = new LinearLayoutManager(getActivity());
        linearLayoutManagerBel.setOrientation(LinearLayoutManager.HORIZONTAL);
        bellezaRecycler.setLayoutManager(linearLayoutManagerBel);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return view;
    }


    public void profiles(String slug){
        pictures = new ArrayList<>();

        String url=IP + "/api/subcategory/"+slug+"/edit";


        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);

                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0,price=0,cat=0;
                    String category="",price_st="";
                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            pictures.add(new CardCategory(jsonObject.getString("id"),jsonObject.getString("url"),jsonObject.getString("title"),jsonObject.getString("slug")));
                        }

                        chefAdapterRecicleView=new SubcategoryAdapterRecicleView(pictures,R.layout.card_view_subcategory,getActivity());
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
                        progressDialog.hide();
                    }
                });

        requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    public void pet(String petParam){
        pet = new ArrayList<>();

        String url=IP + "/api/subcategory/"+petParam+"/edit";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);


                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0,price=0,cat=0;
                    String category="",price_st="";
                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            pet.add(new CardCategory(jsonObject.getString("id"),jsonObject.getString("url"),jsonObject.getString("title"),jsonObject.getString("slug")));

                        }

                        chefAdapterRecicleViewPet=new SubcategoryAdapterRecicleView(pet,R.layout.card_view_subcategory,getActivity());
                        petRecycler.setAdapter(chefAdapterRecicleViewPet);

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

    public void belleza(){
        belleza = new ArrayList<>();

        String url=IP + "/api/subcategory/belleza/edit";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);


                    JSONArray jsonArray=data.optJSONArray("results");
                    int numero=0,price=0,cat=0;
                    String category="",price_st="";
                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            belleza.add(new CardCategory(jsonObject.getString("id"),jsonObject.getString("url"),jsonObject.getString("title"),jsonObject.getString("slug")));

                        }

                        chefAdapterRecicleViewBel=new SubcategoryAdapterRecicleView(belleza,R.layout.card_view_subcategory,getActivity());
                        bellezaRecycler.setAdapter(chefAdapterRecicleViewBel);

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
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

  /*  @Override
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
*/
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
