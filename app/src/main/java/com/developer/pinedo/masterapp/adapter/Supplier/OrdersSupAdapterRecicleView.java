package com.developer.pinedo.masterapp.adapter.Supplier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.Client.ChatActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.ReceiveActivity;
import com.developer.pinedo.masterapp.Supplier.ReceiveSupplierActivity;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.models.CardOrders;
import com.developer.pinedo.masterapp.models.Chat;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class OrdersSupAdapterRecicleView extends RecyclerView.Adapter<OrdersSupAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "ChefAdapterRecicleView";
    private static final String IP = Utils.IP;

    private List<CardOrders> listItem,filterList;
    private int resource;
    private Activity activity;

    public OrdersSupAdapterRecicleView(ArrayList<CardOrders> listItem, int resource, Activity activity) {

        this.listItem = listItem;
        this.resource = resource;
        this.activity = activity;
        this.filterList=new ArrayList<CardOrders>();
        this.filterList.addAll(this.listItem);
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final CardOrders chef = filterList.get(position);

       holder.card_name.setText(chef.getClient());
       holder.card_status.setText(chef.getStatus());

       if(chef.getStatus_id().equals("2")){

           holder.card_status.setTextColor(this.activity.getResources().getColor(R.color.orange));
       }

        //String price = "$ "+NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(chef.get));
       holder.card_price.setText("$"+chef.getSubtotal());
       holder.card_date.setText(chef.getDate_service_formated());

        holder.btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity,ReceiveSupplierActivity.class);
                Bundle b=new Bundle();
                b.putSerializable("order",chef);
                b.putString("id",String.valueOf(chef.getId()));
                i.putExtras(b);
                activity.startActivity(i);
            }
        });

        holder.btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity, ChatActivity.class);
                Bundle b=new Bundle();
                b.putSerializable("order",chef);
                b.putString("id",String.valueOf(chef.getId()));
                b.putInt("type_stakeholder",2);
                i.putExtras(b);
                activity.startActivity(i);
            }
        });
        /*holder.schedule_card.setText(chef.getSchedule_card());
        holder.number_commnet_card.setText(chef.getNumber_commnet_card());
        holder.number_point_card.setText(chef.getNumber_point_card());
        holder.number_point_card.setText(chef.getNumber_point_card());*/
//        holder.like_number.setText(chef.getLike_number());

        //Picasso.with(activity).load(picture.getPicture()).into(holder.pictureCard);

        Glide
                .with(this.activity.getApplicationContext())
                .load(IP+"/"+chef.getPhoto_supplier())
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(holder.card_photo);


        //Picasso.get().load(chef.getCard_photo()).into(holder.card_photo);

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


        private TextView card_name,card_date,card_status,card_price;
        private Button btn_review,btn_help;
        private CircleImageView card_photo;

        public ChefViewHolder(View itemView) {
            super(itemView);

            card_name = itemView.findViewById(R.id.card_name);
            btn_help= itemView.findViewById(R.id.btn_help);
            card_status= itemView.findViewById(R.id.card_status);
            card_price = itemView.findViewById(R.id.card_price);
            card_date = itemView.findViewById(R.id.card_date);
            btn_review = itemView.findViewById(R.id.btn_review);
            card_photo = itemView.findViewById(R.id.card_photo);
        }
    }
}

