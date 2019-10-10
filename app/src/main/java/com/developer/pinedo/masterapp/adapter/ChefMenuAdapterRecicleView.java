package com.developer.pinedo.masterapp.adapter;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.database.App;
import com.developer.pinedo.masterapp.entities.Product;
import com.developer.pinedo.masterapp.models.CardMenuChef;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ChefMenuAdapterRecicleView extends RecyclerView.Adapter<ChefMenuAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "ChefMenuAdapterRecicleView";
    private ArrayList<CardMenuChef> cards;
    private int resource;
    private Activity activity;
    App db;
    Product row = null;
    ToggleButton btn_program;

    public ChefMenuAdapterRecicleView(ArrayList<CardMenuChef> cards, int resource, Activity activity) {
        this.cards = cards;
        this.resource = resource;
        this.activity = activity;

        db = Room.databaseBuilder(activity.getApplicationContext(),App.class,"production")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();


    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final CardMenuChef menu = cards.get(position);
        holder.card_name.setText(menu.getCard_name());


        String price_st = "$ "+NumberFormat.getNumberInstance(Locale.US).format(Float.valueOf(menu.getCard_price()));
        holder.card_price.setText(price_st);

        holder.card_description.setText(menu.getCard_description());


        holder.group_bottom.setVisibility(View.GONE);
        row = db.productDao().getProductId(menu.getCard_id());

        if(row!=null){
            holder.txt_total.setText(row.getQuantity()+"");
            holder.btn_add.setText(row.getQuantity()+"");
        }

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int total=0;

                row = db.productDao().getProductId(menu.getCard_id());

                holder.group_bottom.setVisibility(View.VISIBLE);

                total++;

                if(row != null){
                    total = row.getQuantity();
                }
                holder.txt_total.setText(""+total);


                /*Intent i =new Intent(activity,DetailMenuActivity.class);
                Bundle b=new Bundle();
                b.putSerializable("menu",menu);
                i.putExtras(b);
                activity.startActivity(i);*/
            }
        });

        holder.btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total=0;
                total = Integer.parseInt(holder.txt_total.getText().toString());
                total++;
                holder.txt_total.setText(""+total);
            }
        });

        if(holder.txt_total.getText().toString().equals("'0")){
            holder.btn_quit.setEnabled(false);
        }

        btn_program = activity.findViewById(R.id.btn_program);

        holder.btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total=0;
                total = Integer.parseInt(holder.txt_total.getText().toString());
                total--;
                holder.txt_total.setText(""+total);

                if(total<1){
                    db.productDao().deleteId(menu.getCard_id());
                    holder.group_bottom.setVisibility(View.GONE);
                    holder.btn_add.setText("Agregar");

                    btn_program.setEnabled(false);
                    btn_program.setChecked(false);
                    btn_program.setTextColor(activity.getColor(R.color.grey_sup));
                }else{
                    holder.btn_quit.setEnabled(true);
                }
            }
        });

        holder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total=0;
                row = db.productDao().getProductId(menu.getCard_id());

                total = Integer.parseInt(holder.txt_total.getText().toString());

                if(row == null){
                    row = new Product(menu.getCard_name(),menu.getCard_description(),menu.getCard_price(),menu.getCard_photo(),total,menu.getCard_id());
                }else{
                    row.setQuantity(total);
                }

                if(row.getId() == 0){
                    db.productDao().insertAll(row);
                    row = db.productDao().getProductId(row.getProduct_id());
                }else{
                    db.productDao().updateProducts(row);
                }

                btn_program.setEnabled(true);
                btn_program.setChecked(true);
                btn_program.setTextColor(activity.getColor(R.color.white));

                holder.btn_add.setText(row.getQuantity()+"");
                holder.group_bottom.setVisibility(View.GONE);

            }
        });

      /*  holder.card_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity,DetailMenuActivity.class);
                Bundle b=new Bundle();
                b.putSerializable("menu",menu);
                i.putExtras(b);
                activity.startActivity(i);
            }
        });*/

        Glide
                .with(this.activity.getApplicationContext())
                .load(menu.getCard_photo())
                .centerCrop()
                //.placeholder(R.drawable.loading_spinner)
                .into(holder.card_photo);

        //Picasso.get().load(menu.getCard_photo()).into(holder.card_photo);
        /*holder.schedule_card.setText(chef.getSchedule_card());
        holder.number_commnet_card.setText(chef.getNumber_commnet_card());
        holder.number_point_card.setText(chef.getNumber_point_card());
        holder.number_point_card.setText(chef.getNumber_point_card());*/
//        holder.like_number.setText(chef.getLike_number());

//        Picasso.with(activity).load(picture.getPicture()).into(holder.pictureCard);
//
       /*/ holder.pictureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(activity, DetailChefActivity.class);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    Explode explode = new Explode();
                    explode.setDuration(1000);
                    activity.getWindow().setExitTransition(explode);
                    activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, activity.getString(R.string.transitionname_picture)).toBundle());
                } else {
                    activity.startActivity(intent);
                }
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    public class ChefViewHolder extends RecyclerView.ViewHolder{

        private ImageView card_photo;
        private TextView card_name;
        private TextView card_price;
        private TextView card_description;
        private CardView card_complete;
        private Button btn_add;
        private Button btn_add_product;
        private Button btn_quit;
        private Button btn_ok;
        private LinearLayout group_bottom;
        private TextView txt_total;

        public ChefViewHolder(View itemView) {
            super(itemView);

            card_photo = itemView.findViewById(R.id.card_photo);
            card_name = itemView.findViewById(R.id.card_name);
            card_price = itemView.findViewById(R.id.card_price);
            card_description= itemView.findViewById(R.id.card_description);
            card_complete = itemView.findViewById(R.id.card_complete);
            btn_add = itemView.findViewById(R.id.btn_add);
            group_bottom = itemView.findViewById(R.id.group_bottom);
            btn_add_product=itemView.findViewById(R.id.btn_add_product);
            btn_quit=itemView.findViewById(R.id.btn_quit);
            txt_total = itemView.findViewById(R.id.txt_total);
            btn_ok = itemView.findViewById(R.id.btn_ok);

           /* pictureCard = (ImageView) itemView.findViewById(R.id.pictureCard);
            name_chef_card = (TextView) itemView.findViewById(R.id.name_chef_card);
            name_business_card = (TextView) itemView.findViewById(R.id.name_business_card);
            schedule_card = (TextView) itemView.findViewById(R.id.schedule_card);
            number_commnet_card = (TextView) itemView.findViewById(R.id.number_commnet_card);
            number_point_card = (TextView) itemView.findViewById(R.id.number_point_card);*/
//            likenumberCard= (TextView) itemView.findViewById(R.id.likenumber_card);


        }
    }
}

