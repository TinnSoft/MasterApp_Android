package com.developer.pinedo.masterapp.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.developer.pinedo.masterapp.entities.Product;

import java.util.List;

@Dao
public interface ProductDAO {

    @Query("SELECT * from product")
    List<Product> getAllProduct();

    @Query("SELECT * FROM product WHERE product_id = :product_id")
    Product getProductId(int product_id);

    @Insert
    void insertAll(Product... products);

    @Update
    public void updateProducts(Product... products);


    @Query("DELETE FROM product")
    public void  deleteAll();

    @Query("DELETE FROM product where product_id = :product_id")
    public void  deleteId(int product_id);

}
