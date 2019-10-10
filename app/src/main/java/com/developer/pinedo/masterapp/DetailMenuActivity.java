package com.developer.pinedo.masterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.entities.Product;
import com.developer.pinedo.masterapp.models.CardPhoto;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailMenuActivity extends AppCompatActivity {

    private static final String TAG = "DetailMenuActivity";
    int total=1;

    TextView txt_total,name_supplier;
    Button btn_quit;
    ImageView img_main;
    int product_id;
    Product row = null;
    CircleImageView card_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_menu);
        txt_total=findViewById(R.id.txt_total);
        btn_quit =findViewById(R.id.btn_quit);
        name_supplier= findViewById(R.id.name_supplier);
        card_photo= findViewById(R.id.card_photo);

        Bundle param=getIntent().getExtras();
        CardPhoto cardChef=null;
        showToolbar("",true);

        img_main=findViewById(R.id.img_main);


        if(param != null) {
            cardChef = (CardPhoto) param.getSerializable("menu");

            TextView card_description = findViewById(R.id.card_description);
//            TextView card_point = findViewById(R.id.card_point);
            card_description.setText(cardChef.getCard_description());
            //card_point.setText(cardChef.getCard_star());
            Glide
                    .with(getApplicationContext())
                    .load(cardChef.getCard_photo())
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(img_main);



        }


        name_supplier.setText(cardChef.getCard_supplier());

        Glide
                .with(getApplicationContext())
                .load(Utils.IP+"/"+cardChef.getCard_photo_supplier())
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(card_photo);

        //Picasso.get().load(Utils.IP+"/"+Utils.getItem(this,"photo")).into(card_photo);




        /*cardChef= (CardChef) param.getSerializable("menu");
        Toast.makeText(this,cardChef.getCard_name(),Toast.LENGTH_LONG).show();*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_cart,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_seach:
//                Intent i=new Intent(this,ServicesActivity.class);
//                startActivity(i);

                Intent i=new Intent(this,OverviewActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToolbar(String tittle, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(0x000000);
        toolbar.setNavigationIcon(R.drawable.ic_backgreen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

    }

}
