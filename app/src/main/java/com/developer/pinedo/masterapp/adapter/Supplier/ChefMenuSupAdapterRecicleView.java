package com.developer.pinedo.masterapp.adapter.Supplier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Supplier.ProductActivity;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.models.CardMenuChef;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ChefMenuSupAdapterRecicleView extends RecyclerView.Adapter<ChefMenuSupAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "ChefMenuAdapterRecicleView";
    private ArrayList<CardMenuChef> cards;
    private int resource;
    private Activity activity;

    public ChefMenuSupAdapterRecicleView(ArrayList<CardMenuChef> cards, int resource, Activity activity) {
        this.cards = cards;
        this.resource = resource;
        this.activity = activity;
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final CardMenuChef menu = cards.get(position);

        holder.card_name.setText(Utils.capitilize(menu.getCard_name()));
        holder.card_price.setText(Utils.formatNumber(menu.getCard_price()));

        holder.card_description.setText(menu.getCard_description());

        Intent i =new Intent(activity,ProductActivity.class);
        Bundle b=new Bundle();
        b.putSerializable("menu",menu);
        i.putExtras(b);

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(i);
            }
        });

        /*holder.card_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        public ChefViewHolder(View itemView) {
            super(itemView);

            card_photo = itemView.findViewById(R.id.card_photo);
            card_name = itemView.findViewById(R.id.card_name);
            card_price = itemView.findViewById(R.id.card_price);
            card_description= itemView.findViewById(R.id.card_description);
            card_complete = itemView.findViewById(R.id.card_complete);
            btn_add = itemView.findViewById(R.id.btn_add);

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

