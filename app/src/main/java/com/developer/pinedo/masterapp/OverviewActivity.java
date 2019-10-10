package com.developer.pinedo.masterapp;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.developer.pinedo.masterapp.adapter.OverviewAdapterRecicleView;
import com.developer.pinedo.masterapp.database.App;
import com.developer.pinedo.masterapp.entities.Product;
import com.developer.pinedo.masterapp.models.CardOverView;

import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {
    private static final String TAG = "OverviewActivity";
    private OverviewAdapterRecicleView overviewAdapterRecicleView;
    RecyclerView picturesRecycler;
    ArrayList<CardOverView> pictures=new ArrayList<>();

    App db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        showToolbar("Tus pedidos",true);


        db = Room.databaseBuilder(getApplicationContext(),App.class,"production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        picturesRecycler=findViewById(R.id.overviewRecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        picturesRecycler.setLayoutManager(linearLayoutManager);

        overviewAdapterRecicleView = new OverviewAdapterRecicleView(buildPictures(),R.layout.card_view_overview,this);
        picturesRecycler.setAdapter(overviewAdapterRecicleView);
    }


    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(0x000000);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

    }


    public ArrayList<CardOverView> buildPictures(){
        List<Product> products = db.productDao().getAllProduct();

        for(Product row:products){
            pictures.add(new CardOverView(row.getId(),row.getImage(),row.getTitle(), row.getQuantity(),row.getPrice(),row.getProduct_id()));

        }

        return pictures;
    }
}
