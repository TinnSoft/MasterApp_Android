package com.developer.pinedo.masterapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.developer.pinedo.masterapp.models.CardOrders;
import com.developer.pinedo.masterapp.models.Chat;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatAdapterRecicleView extends RecyclerView.Adapter<ChatAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "ChefAdapterRecicleView";
    private List<Chat> listItem,filterList;
    private int resource;
    private Activity activity;

    public ChatAdapterRecicleView(ArrayList<Chat> listItem, int resource, Activity activity) {
        this.listItem = listItem;
        this.resource = resource;
        this.activity = activity;
        this.filterList=new ArrayList<Chat>();
        this.filterList.addAll(this.listItem);
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final Chat row = filterList.get(position);

        Log.d("JORGE",row.getType_stakeholder()+"");
        if(row.getType_stakeholder() == 1){
            holder.card_message.setText(row.getDescription());
            holder.date_client.setText(row.getCreated_at());

            holder.card_response.setVisibility(View.GONE);
            holder.date_supplier.setVisibility(View.GONE);
            Glide
                    .with(this.activity.getApplicationContext())
                    .load(row.getNode_id())
                    .centerCrop()
                    .into(holder.image_supplier);
        }else{
            holder.card_message.setVisibility(View.GONE);
            holder.date_client.setVisibility(View.GONE);

                 Glide
                .with(this.activity.getApplicationContext())
                .load(row.getNode_id())
                .centerCrop()
                .into(holder.image_client);

            holder.card_response.setText(row.getDescription());
            holder.date_supplier.setText(row.getCreated_at());
        }


        /*holder.card_name.setText(chef.getSupplier_short());
        holder.card_status.setText(chef.getStatus());

        if(chef.getStatus_id().equals("1")){
            holder.card_status.setTextColor(this.activity.getResources().getColor(R.color.px_mp_blue));
        }else if(chef.getStatus_id().equals("2")){
            holder.card_status.setTextColor(this.activity.getResources().getColor(R.color.orange));
        }else if(chef.getStatus_id().equals("3")){
            holder.btn_help.setVisibility(View.GONE);
            holder.card_status.setTextColor(this.activity.getResources().getColor(R.color.green_background));
        }

        //String price = "$ "+NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(chef.get));
        holder.card_price.setText("$"+chef.getSubtotal());
        holder.card_date.setText(chef.getDate_service_formated());

        holder.btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity,ReceiveActivity.class);
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
                i.putExtras(b);
                activity.startActivity(i);
            }
        });*//*holder.card_name.setText(chef.getSupplier_short());
        holder.card_status.setText(chef.getStatus());

        if(chef.getStatus_id().equals("1")){
            holder.card_status.setTextColor(this.activity.getResources().getColor(R.color.px_mp_blue));
        }else if(chef.getStatus_id().equals("2")){
            holder.card_status.setTextColor(this.activity.getResources().getColor(R.color.orange));
        }else if(chef.getStatus_id().equals("3")){
            holder.btn_help.setVisibility(View.GONE);
            holder.card_status.setTextColor(this.activity.getResources().getColor(R.color.green_background));
        }

        //String price = "$ "+NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(chef.get));
        holder.card_price.setText("$"+chef.getSubtotal());
        holder.card_date.setText(chef.getDate_service_formated());

        holder.btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity,ReceiveActivity.class);
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
                i.putExtras(b);
                activity.startActivity(i);
            }
        });*/

       /* holder.card_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity,ReceiveActivity.class);
                Bundle b=new Bundle();
                b.putSerializable("order",chef);
                b.putString("id",String.valueOf(chef.getId()));
                i.putExtras(b);
                activity.startActivity(i);
            }
        });*/

        /*holder.schedule_card.setText(chef.getSchedule_card());
        holder.number_commnet_card.setText(chef.getNumber_commnet_card());
        holder.number_point_card.setText(chef.getNumber_point_card());
        holder.number_point_card.setText(chef.getNumber_point_card());*/
//        holder.like_number.setText(chef.getLike_number());

        //Picasso.with(activity).load(picture.getPicture()).into(holder.pictureCard);

     /*   Glide
                .with(this.activity.getApplicationContext())
                .load(chef.getPhoto_supplier())
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(holder.card_photo);*/

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


        private CircleImageView image_supplier,image_client;
        private TextView card_message,card_response,date_client,date_supplier;


        public ChefViewHolder(View itemView) {
            super(itemView);

            image_supplier = itemView.findViewById(R.id.image_supplier);
            image_client = itemView.findViewById(R.id.image_client);
            card_message = itemView.findViewById(R.id.card_message);
            card_response= itemView.findViewById(R.id.card_response);
            date_client = itemView.findViewById(R.id.date_client);
            date_supplier = itemView.findViewById(R.id.date_supplier);
            date_client = itemView.findViewById(R.id.date_client);
            date_supplier = itemView.findViewById(R.id.date_supplier);
        }
    }
}

