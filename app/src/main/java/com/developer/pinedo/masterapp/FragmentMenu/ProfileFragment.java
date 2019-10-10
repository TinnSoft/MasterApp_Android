package com.developer.pinedo.masterapp.FragmentMenu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.LoginActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProfileFragment";
    private static final String IP = Utils.IP;

    private String mParam1;
    private String mParam2;

    CircleImageView profile_image_card;
    String path;
    Bitmap bitmap;
    ProgressDialog progress_bar;
    final int COD_SELECTED=10;
    final int COD_PHOTO=20;
    InputMethodManager imm;

    StringRequest stringRequest;
    RequestQueue requestQueue;

    private final String FOLDER_ROOT="misImagenes/";
    private final String FOLDER_IMAGE=FOLDER_ROOT + "misFotos";
    ViewGroup viewContainer;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String restoredText,access_token,provider;
    TextView tv_email,tv_name,tv_main,tv_description;
    Button   btn_convert;
    AlertDialog dialog;
    CardView profile_name;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {

    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        viewContainer = container;
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        profile_image_card = view.findViewById(R.id.profile_image_card);
        btn_convert = view.findViewById(R.id.btn_convert);
        tv_description = view.findViewById(R.id.tv_description);
        tv_email= view.findViewById(R.id.tv_email);
        tv_name= view.findViewById(R.id.tv_name);
        tv_main= view.findViewById(R.id.tv_main);
        profile_image_card.setOnClickListener(this);

        getDataUser();

        profile_name = view.findViewById(R.id.profile_name);

        profile_name.setOnClickListener(this);
        btn_convert.setOnClickListener(this);

        return view;
    }

    private void getDataUser() {
        String url=IP + "/api/user";

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data=new JSONObject(response);
                    tv_name.setText(data.getString("name"));
                    tv_email.setText(data.getString("email"));
                    tv_description.setText(data.getString("description"));

                    if(data.getString("stakeholder_id") != null){
                        btn_convert.setText("cambiar Modo Proveedor");
                        Glide
                                .with(getActivity().getApplicationContext())
                                .load(IP+"/"+data.getString("photo"))
                                .centerCrop()
                                //.placeholder(R.drawable.loading_spinner)
                                .into(profile_image_card);
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

    @Override
    public void onStart() {
        super.onStart();
        imm.hideSoftInputFromWindow(viewContainer.getWindowToken(), 0);


        prefs = getActivity().getSharedPreferences(Utils.MY_PREFS_NAME, getActivity().MODE_PRIVATE);
        restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            access_token = prefs.getString("access_token", "");//"No name defined" is the default value.
            provider = prefs.getString("provider", "");
            tv_name.setText(prefs.getString("name",null));
            tv_main.setText(prefs.getString("name",null));

            if(prefs.getString("photo",null).equals(null) ){

                if(bitmap==null){
                    profile_image_card.setImageDrawable(getResources().getDrawable(R.drawable.imagen_no_disponible));
                }
            }else{
                //Picasso.get().load(IP+"/"+prefs.getString("photo",null)).into(profile_image_card);
            }
        }else{
            Utils.redirecTo(getContext(),LoginActivity.class);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profile_image_card:
                if(permissionCamera()) {
                    ShowOptions();
                }
                break;
            case R.id.btn_convert:
                //Utils.redirecTo(getContext(),C.class);
                break;

            case R.id.profile_name:
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(getContext());


                View mView=getLayoutInflater().inflate(R.layout.dialog_edit_field,null);
//                btnCancel= mView.findViewById(R.id.btnCancelRestriction);
                final EditText etDescription = mView.findViewById(R.id.etDescription);
                etDescription.setHint("Nombres");
                etDescription.setText(tv_name.getText().toString());


                mBuilder.setView(mView);
                mBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateName(etDescription.getText().toString(),1);
                    }
                });

                mBuilder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.cancel();
                    }
                });

                dialog=mBuilder.create();
                dialog.setTitle("Editar");
                dialog.show();

                break;
        }
    }

    public boolean permissionCamera(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }


        if((getActivity().checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED) &&
                (getActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((getActivity().shouldShowRequestPermissionRationale(CAMERA)) ||
                (getActivity().shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            loadDialogRecomendation();
        }else{
            getActivity().requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            switch (requestCode){
                case  COD_SELECTED:
                    Uri path_selected=data.getData();
//                    content_image.setImageURI(path_selected);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),path_selected);
                        profile_image_card.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case COD_PHOTO:
                    MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });

                    bitmap= BitmapFactory.decodeFile(path);
                    profile_image_card.setImageBitmap(bitmap);
                    setPhotoProfile(bitmap);
            }
        }
    }


    public void loadDialogRecomendation(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
        dialog.setTitle("Permisos Desactivados");
        dialog.setMessage("Debe aceptar los permisos para el correcto funcionamiento del App");
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialog.show();
    }

    private void updateName(final String name,int user_id) {
            String url=IP + "/api/user/" + user_id;

            stringRequest=new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data=new JSONObject(response);
                        JSONArray jsonArray=data.optJSONArray("data");
                        tv_name.setText(name);
                        tv_main.setText(name);

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
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params=new HashMap<String,String>();
                    params.put("name",name);

                    return params;
                }

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

    public void ShowOptions(){
        final CharSequence[] options={"Tomar foto","Cargar imagen","Cancelar"};
        final AlertDialog.Builder alerBuilder=new AlertDialog.Builder(getActivity());
        alerBuilder.setTitle("Seleccione un opcion");
        alerBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(options[which].equals("Tomar foto")){
                    takePhoto();
                }else{
                    if(options[which].equals("Cargar imagen")){
                        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/");
                        startActivityForResult(i.createChooser(i,"Seleccione la acplicion"),COD_SELECTED);
                    }else{
                        dialog.dismiss();
                    }
                }
            }
        });

        alerBuilder.show();

    }

    public void takePhoto(){
        File fileImage=new File(Environment.getExternalStorageDirectory(),FOLDER_IMAGE);
        boolean is_created = fileImage.exists();

        String name_image="";

        if(is_created==false){
            is_created=fileImage.mkdirs();
        }

        if(is_created==true){
            name_image = (System.currentTimeMillis()/1000)+".jpg";
        }

        path = Environment.getExternalStorageDirectory()+File.separator+FOLDER_IMAGE+File.separator+name_image;


        File image=new File(path);

        Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {
            String authorities=getActivity().getApplicationContext().getPackageName()+".provider";
            Uri imageUri=FileProvider.getUriForFile(getActivity(),authorities,image);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else {
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        }

        startActivityForResult(i,COD_PHOTO);

    }

    private void setPhotoProfile(final Bitmap bitmap) {

        progress_bar=new ProgressDialog(getActivity());
        progress_bar.setMessage("Espere por favor");
        progress_bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress_bar.setIndeterminate(true);
        progress_bar.setProgress(0);
        progress_bar.show();
        String url=IP+"/api/user/update-photo";


        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress_bar.hide();

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject user = obj.getJSONObject("user");

                    Glide
                            .with(getActivity().getApplicationContext())
                            .load(IP  + "/" + user.getString("photo"))
                            .centerCrop()
                            //.placeholder(R.drawable.loading_spinner)
                            .into(Utils.imageProfile);
                    Glide
                            .with(getActivity().getApplicationContext())
                            .load(IP  + "/" + user.getString("photo"))
                            .centerCrop()
                            //.placeholder(R.drawable.loading_spinner)
                            .into(profile_image_card);

//                    Picasso.get().load(IP  + "/" + user.getString("photo")).into(profile_image_card);
//                    Picasso.get().load(IP + "/" + user.getString("photo")).into(Utils.imageProfile);

                    Toast.makeText(getActivity(),"Foto Actualizada",Toast.LENGTH_LONG).show();
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
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + access_token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = Utils.imageToString(bitmap);
                Map<String, String> params = new HashMap<String,String>();
                params.put("image",image);
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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
