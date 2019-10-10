package com.developer.pinedo.masterapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.MenuChefActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.models.CardChef;
import com.developer.pinedo.masterapp.models.OrdersReview;

import java.util.ArrayList;
import java.util.List;

import static com.developer.pinedo.masterapp.Utils.IP;


public class OrderReviewAdapterRecicleView extends RecyclerView.Adapter<OrderReviewAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "ChefAdapterRecicleView";
    private List<OrdersReview> listItem,filterList;
    private int resource;
    private Activity activity;

    public OrderReviewAdapterRecicleView(ArrayList<OrdersReview> listItem, int resource, Activity activity) {

        this.listItem = listItem;
        this.resource = resource;
        this.activity = activity;
        this.filterList=new ArrayList<OrdersReview>();
        this.filterList.addAll(this.listItem);
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final OrdersReview chef = filterList.get(position);

        holder.card_name.setText(chef.getProduct());
        holder.card_units.setText(chef.getUnits()+" Unidades");
        holder.card_price.setText(chef.getSubtotal());


        Glide
                .with(this.activity.getApplicationContext())
                .load(chef.getImage())
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(holder.card_photo);


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
        return filterList.size();
    }






    public class ChefViewHolder extends RecyclerView.ViewHolder{

        private ImageView card_photo;
        private TextView card_name;
        private TextView card_units;
        private TextView card_price;

        public ChefViewHolder(View itemView) {
            super(itemView);

            card_photo = itemView.findViewById(R.id.card_photo);
            card_name = itemView.findViewById(R.id.card_name);
            card_units = itemView.findViewById(R.id.card_units);
            card_price = itemView.findViewById(R.id.card_price);
        }
    }
}

