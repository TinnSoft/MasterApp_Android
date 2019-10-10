package com.developer.pinedo.masterapp.Managers;

import android.content.SharedPreferences;

public class SupplierManager {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static SupplierManager INSTANCE = null;

    private SupplierManager(SharedPreferences preferences){
        this.preferences=preferences;
        this.editor = preferences.edit();
    }

    public static synchronized SupplierManager getInstance(SharedPreferences preferences){
        if(INSTANCE == null){
            INSTANCE = new SupplierManager(preferences);
        }
        return INSTANCE;
    }

    public void saveIdSupplier(Supplier supplier){
        editor.putString("ID_SUPPLIER",supplier.getId()).commit();
    }

    public void saveNameSupplier(Supplier supplier){
        editor.putString("NAME_SUPPLIER",supplier.getName()).commit();
    }

    public void saveDescriptionSupplier(Supplier supplier){
        editor.putString("NAME_SUPPLIER",supplier.getDescription()).commit();
    }

    public void savePhotoSupplier(Supplier supplier){
        editor.putString("PHOTO_SUPPLIER",supplier.getPhoto()).commit();
    }

    public void saveSuppplier(Supplier supplier){
        editor.putString("ID_SUPPLIER",supplier.getId()).commit();
        editor.putString("NAME_SUPPLIER",supplier.getName()).commit();
        editor.putString("DESCRIPTION_SUPPLIER",supplier.getDescription()).commit();
        editor.putString("PHOTO_SUPPLIER",supplier.getPhoto()).commit();
    }


    public Supplier getSupplier(){
        Supplier supplier = new Supplier();
        supplier.setId(preferences.getString("ID_SUPPLIER",null));
        supplier.setName(preferences.getString("NAME_SUPPLIER",null));
        supplier.setDescription(preferences.getString("DESCRIPTION_SUPPLIER",null));
        supplier.setPhoto(preferences.getString("PHOTO_SUPPLIER",null));
        return supplier;
    }


    public void deleteSupplier(){
        editor.remove("ID_SUPPLIER").commit();
        editor.remove("NAME_SUPPLIER").commit();
        editor.remove("DESCRIPTION_SUPPLIER").commit();
        editor.remove("NAME_SUPPLIER").commit();
    }
}
