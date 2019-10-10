package com.developer.pinedo.masterapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;

import com.developer.pinedo.masterapp.models.DataUserProfile;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public static final int LIST=1;
    public static final int GRID=2;
    public static final String MY_PREFS_NAME="masterapp";

    public static int visualization = LIST;
    //public static String IP = "http://192.168.0.60";
   // public static String IP = "http://192.168.1.51";
    public static String IP = "https://masterapp.com.co";
    public static CircleImageView imageProfile;
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    public static String restoredText,access_token,provider;


    public static void redirecTo(Context activity,Class <?> destiny) {
        Intent i=new Intent(activity,destiny);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
    }

    public  static String imageToString(Bitmap bitmap){
        String imageString="";
        if(bitmap!=null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imageByte = byteArrayOutputStream.toByteArray();
            imageString = Base64.encodeToString(imageByte, Base64.DEFAULT);

        }
        return imageString;
    }

    public static String capitilize(String input){
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    public static String formatNumber(String number){
        return NumberFormat.getNumberInstance(Locale.US).format(Float.valueOf(number));
    }

    public static String AccessToken(Activity activity) {
        prefs = activity.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("access_token", null);
        if (restoredText != null) {
            access_token = prefs.getString("access_token", "");//"No name defined" is the default value.
        } else {
            Utils.redirecTo(activity, LoginActivity.class);
        }

        return access_token;
    }


    public static String getPhotoProfile(Activity activity) {

        prefs = activity.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("photo", null);
        if (restoredText != null) {
            access_token = prefs.getString("photo", "");//"No name defined" is the default value.
        }

        return access_token;
    }

    public static DataUserProfile getDataUser(Activity activity){
        DataUserProfile user = new DataUserProfile();

        prefs = activity.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        restoredText = prefs.getString("photo", null);
        if (restoredText != null) {
            user.setPhoto(prefs.getString("photo", ""));
        }

        restoredText = prefs.getString("token", null);
        if (restoredText != null) {
            user.setToken(prefs.getString("token", ""));
        }

        restoredText = prefs.getString("name", null);
        if (restoredText != null) {
            user.setName(prefs.getString("name", ""));
        }

        return user;
    }

    public static void setItem(Activity activity,String key,String value){
        editor = activity.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getItem(Activity activity,String key){
        prefs = activity.getSharedPreferences(Utils.MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(key, "");
    }

}
