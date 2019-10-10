package com.developer.pinedo.masterapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.developer.pinedo.masterapp.dao.ProductDAO;
import com.developer.pinedo.masterapp.entities.Product;


@Database(entities = {Product.class},version = 3)
public abstract class App extends RoomDatabase {
    public abstract ProductDAO productDao();
}
