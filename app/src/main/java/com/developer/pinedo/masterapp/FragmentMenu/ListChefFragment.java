package com.developer.pinedo.masterapp.FragmentMenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.LoginActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.adapter.ChefAdapterRecicleView;
import com.developer.pinedo.masterapp.adapter.FilterAdapterRecicleView;
import com.developer.pinedo.masterapp.models.CardChef;
import com.developer.pinedo.masterapp.models.CardFilters;

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

import static android.content.Context.MODE_PRIVATE;


public class ListChefFragment extends Fragment implements SearchView.OnQueryTextListener,View.OnClickListener{

    private static final String TAG = "ListChefFragment";
    private OnFragmentInteractionListener mListener;
    ToggleButton btnBurger,btnChicken,btnPasta,btnPizza;
    ImageView btnFilter;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    List<String> catetories=new ArrayList<>();
    RecyclerView picturesRecycler,filterRecycler;

    List<String> f=new ArrayList();
    TextView main_title;
    SharedPreferences prefs;

    private ChefAdapterRecicleView chefAdapterRecicleView;

    private FilterAdapterRecicleView filterAdapterRecicleView;

    StringRequest stringRequest;
    ArrayList<CardChef> pictures = new ArrayList<>();
    ArrayList<CardFilters> filters = new ArrayList<>();

    InputMethodManager imm;
    ViewGroup viewContainer;
    String restoredText,access_token,provider,slug;

    private static final String IP = Utils.IP;

    public ListChefFragment() {

    }


    public static ListChefFragment newInstance(String param1, String param2) {
        ListChefFragment fragment = new ListChefFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public  void listCategories(){

        String url=IP + "/api/filter/"+slug;

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);

                    JSONArray jsonArray=data.optJSONArray("results");

                    int cat=0;
                    String img="";
                    try {

                        for (int i=0;i<jsonArray.length();i++) {
                            img = IP + "/images/default-user.png";
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            if(!jsonObject.getString("url").equals(null)){
                                img = IP+"/"+jsonObject.getString("url");
                            }


                            filters.add(new CardFilters(
                                    jsonObject.getString("id"),
                                    img,jsonObject.getString("title"),
                                    jsonObject.getString("slug")
                                                        )
                                    );
                        }

                        filterAdapterRecicleView = new FilterAdapterRecicleView(filters,R.layout.cardview_filter,getActivity());
                        filterRecycler.setAdapter(filterAdapterRecicleView);


                    } catch (JSONException e) {
                        Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(),"error2",Toast.LENGTH_SHORT).show();
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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " +access_token);
                return params;
            }};

        requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);



       /* catetories.add("pizza");
        catetories.add("burger");
        catetories.add("pasta");
        catetories.add("chicken");*/
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = this.getArguments();
        String title="Chef",id="";

        if (bundle != null) {
            id = bundle.getString("id", "0");
            title = bundle.getString("title", "Chef");
            slug = bundle.getString("slug", "");
        }

        View view=inflater.inflate(R.layout.fragment_list_chef, container, false);
        btnBurger = view.findViewById(R.id.btn_burger);
        main_title = view.findViewById(R.id.main_title);
        main_title.setText(title);

        /*btnBurger.setOnClickListener(this);
        btnChicken = view.findViewById(R.id.btn_chicken);
        btnChicken.setOnClickListener(this);
        btnPasta = view.findViewById(R.id.btn_pasta);
        btnPasta.setOnClickListener(this);
        btnPizza= view.findViewById(R.id.btn_pizza);
        btnPizza.setOnClickListener(this);*/

        btnFilter= view.findViewById(R.id.btn_filter);
        listCategories();
        requestQueue= Volley.newRequestQueue(getActivity());
        profiles(slug);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        filterRecycler=view.findViewById(R.id.filterRecycler);
        LinearLayoutManager linearLayoutManagerF=new LinearLayoutManager(getActivity());
        linearLayoutManagerF.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterRecycler.setLayoutManager(linearLayoutManagerF);


        picturesRecycler=view.findViewById(R.id.chefRecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);

        SearchView txtsearch = view.findViewById(R.id.txtsearch);
        txtsearch.setIconifiedByDefault(false);
        txtsearch.setOnQueryTextListener((SearchView.OnQueryTextListener) this);
        txtsearch.setSubmitButtonEnabled(true);
        txtsearch.setQueryHint("Que estas buscando");

        viewContainer = container;

        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        return view;
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void profiles(String id){

        String url=IP + "/api/stakeholder/"+id+"/editAll";


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
                            img = IP + "/images/default-user.png";
                            JSONObject jsonObject = null;
                            jsonObject = jsonArray.getJSONObject(i);

                            numero = (int)(Math.random()*5+1);

                            double random = 0 + Math.random() * (5 - 0);
                            Random rand = new Random();
                            price = 1000 + rand.nextInt((100000 - 1000) + 1);

                            price_st = "$ "+NumberFormat.getNumberInstance(Locale.US).format(price);
                            DecimalFormat df = new DecimalFormat("#.0");

                            if(!jsonObject.getString("photo").equals(null)){
                                img = IP+"/"+jsonObject.getString("photo");
                            }

                            pictures.add(new CardChef(Integer.parseInt(jsonObject.getString("id")),
                                    img,
                                    jsonObject.getString("name"),
                                    String.valueOf(df.format(random)), jsonObject.getString("slug"),
                                    price_st,jsonObject.getString("slug")));
                        }



                        chefAdapterRecicleView = new ChefAdapterRecicleView(pictures,R.layout.card_view_chef,getActivity());
                        picturesRecycler.setAdapter(chefAdapterRecicleView);

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(),"error2",Toast.LENGTH_SHORT).show();
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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " +access_token);
                return params;
            }};

        requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    public void onClick(View view){
        String filter="";

        switch (view.getId()){
            case R.id.btn_burger:
                if(btnBurger.isChecked()){
                    f.add("burger");
                }else{
                    f.remove("burger");
                }
                break;

            case R.id.btn_chicken:
                if(btnChicken.isChecked()){
                    f.add("chicken");
                }else{
                    f.remove("chicken");
                }
                break;
            case R.id.btn_pasta:
                if(btnPasta.isChecked()){
                    f.add("pasta");
                }else{
                    f.remove("pasta");
                }
                break;
            case R.id.btn_pizza:
                if(btnPizza.isChecked()){
                    f.add("pizza");
                }else{
                    f.remove("pizza");
                }
                break;
        }

        chefAdapterRecicleView.filterCategoryList(f);

    }

    @Override
    public void onStart() {
        super.onStart();
        imm.hideSoftInputFromWindow(viewContainer.getWindowToken(), 0);

        prefs = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            access_token = prefs.getString("access_token", "");//"No name defined" is the default value.
            provider = prefs.getString("provider", "");

        }else{
            Utils.redirecTo(getActivity(),LoginActivity.class);
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
        if(chefAdapterRecicleView!=null){
            chefAdapterRecicleView.filter(newText);
            return true;
        }else{
            return  false;
        }
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
