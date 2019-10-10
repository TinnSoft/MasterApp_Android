package com.developer.pinedo.masterapp.Client.New_client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.pinedo.masterapp.Client.MainActivity;
import com.developer.pinedo.masterapp.LoginActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewClientActivity extends AppCompatActivity implements
        PageOneFragment.OnFragmentInteractionListener,
        PageTwoFragment.OnFragmentInteractionListener,
        PageThirdFragment.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    GoogleApiClient googleApiClient;

    private ViewPager mViewPager;


    StringRequest stringRequest;
    RequestQueue requestQueue;

    private static final String IP = Utils.IP;


    ProgressDialog progress_bar;

    private static final int SIGN_IN_CODE=777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(item, smoothScroll);
    }

    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            Utils.redirecTo(getApplicationContext(),MainActivity.class);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE){
            GoogleSignInResult result= Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignrResult(result);
        }
    }

    private void handleSignrResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account=result.getSignInAccount();
            goMainScreen(account);
        }else{
            Toast.makeText(getApplicationContext(),"No se pudo iniciar sesión",Toast.LENGTH_LONG).show();
        }
    }

    private void goMainScreen(GoogleSignInAccount account) {
        registerUserGoogle(account);
    }

    private void registerUserGoogle(final GoogleSignInAccount account) {
        progress_bar = new ProgressDialog(this);
        progress_bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress_bar.setIndeterminate(true);
        progress_bar.setProgress(0);
        progress_bar.show();

        String url=IP+"/api/social_auth";

        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject obj = new JSONObject(response);

                    if(obj.getString("token_type").equals("Bearer")){

                        SharedPreferences.Editor editor = getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("access_token", obj.getString("access_token"));
                        editor.putString("provider", "google");
                        editor.apply();
                        Utils.redirecTo(NewClientActivity.this,MainActivity.class);
                    }else{
                        Toast.makeText(getApplicationContext(),"Error: "+obj.getString("message"),Toast.LENGTH_LONG).show();
                        //Snackbar.make(view, obj.getString("message"), Snackbar.LENGTH_LONG).show();
                    }
                    progress_bar.hide();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress_bar.hide();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                //return super.getHeaders();
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String,String>();
                params.put("name",account.getDisplayName());
                params.put("email",account.getEmail());
                params.put("photo",account.getPhotoUrl().toString());
                params.put("provider","google");
                params.put("provider_user_id",account.getId());
                return params;

            }

        };
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }




    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment=null;
            switch (sectionNumber){
                case 1:
                    fragment = new PageOneFragment();
                    break;
                case 2:
                    fragment = new PageTwoFragment();
                    break;
                case 3:
                    fragment = new PageThirdFragment();
                    break;
            }

            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_client, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/




            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
