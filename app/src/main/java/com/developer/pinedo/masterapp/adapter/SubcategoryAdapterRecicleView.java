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
import com.developer.pinedo.masterapp.ListChefActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.Utils;
import com.developer.pinedo.masterapp.models.CardCategory;

import java.util.ArrayList;
import java.util.List;


public class SubcategoryAdapterRecicleView extends RecyclerView.Adapter<SubcategoryAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "ChefAdapterRecicleView";
    private List<CardCategory> listItem,filterList;
    private int resource;
    private Activity activity;
    View view;

    public SubcategoryAdapterRecicleView(ArrayList<CardCategory> listItem, int resource, Activity activity) {

        this.listItem = listItem;
        this.resource = resource;
        this.activity = activity;
        this.filterList=new ArrayList<CardCategory>();
        this.filterList.addAll(this.listItem);
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final CardCategory chef = filterList.get(position);

        holder.card_title.setText(chef.getCard_title());

        holder.card_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity,ListChefActivity.class);
                Bundle b=new Bundle();

                Utils.setItem(activity,"title",chef.getCard_title());
                Utils.setItem(activity,"slug",chef.getCard_slug());


                b.putSerializable("chef",chef);
                i.putExtras(b);
                activity.startActivity(i);

//                Fragment fragment = new ListChefFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("id", chef.getCard_id());
//                bundle.putString("title", chef.getCard_title());
//                bundle.putString("slug", chef.getCard_slug());
//                fragment.setArguments(bundle);
//                ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();

            }
        });

        Glide
                .with(this.activity.getApplicationContext())
                .load(chef.getCard_photo())
                //.fitCenter()
                //.centerCrop()
                //.placeholder(R.drawable.loading_spinner)
                .into(holder.pictureCard);


        /*holder.schedule_card.setText(chef.getSchedule_card());
        holder.number_commnet_card.setText(chef.getNumber_commnet_card());
        holder.number_point_card.setText(chef.getNumber_point_card());
        holder.number_point_card.setText(chef.getNumber_point_card());*/
//        holder.like_number.setText(chef.getLike_number());

        //Picasso.with(activity).load(picture.getPicture()).into(holder.pictureCard);
//        Picasso.get().load(chef.getCard_photo()).into(holder.card_photo);

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

    public void filter(final String text){

        new Thread(new Runnable() {
            @Override
            public void run() {
                filterList.clear();
                if(TextUtils.isEmpty(text)){
                    filterList.addAll(listItem);
                }else{

                    for (CardCategory item: listItem){
                        if(item.getCard_title().toLowerCase().contains(text.toLowerCase())){

                            filterList.add(item);
                        }
                    }
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void filterCategory(final String category){

        new Thread(new Runnable() {
            @Override
            public void run() {
                filterList.clear();
                    for (CardCategory item: listItem){
                        /*if(item.getCard_category().toLowerCase().contains(category)){
                            filterList.add(item);
                        }*/
                    }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public void filterCategoryList(final List<String> categories){

        new Thread(new Runnable() {
            @Override
            public void run() {
                filterList.clear();

                if(categories.size()==0) {
                    filterList.addAll(listItem);
                }else{
                    for (CardCategory item: listItem){
                        for (String i : categories) {
//                            if (item.getCard_category().toLowerCase().contains(i)) {
//                                filterList.add(item);
//                            }
                        }

                    }
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }




    public class ChefViewHolder extends RecyclerView.ViewHolder{

        private ImageView card_photo;
        private TextView card_id;
        private TextView card_title;
        private CardView card_complete;
        private ImageView pictureCard;

        public ChefViewHolder(View itemView) {
            super(itemView);

            card_complete = itemView.findViewById(R.id.card_complete);
            card_title = itemView.findViewById(R.id.card_title);
            card_photo = itemView.findViewById(R.id.card_photo);
            pictureCard = itemView.findViewById(R.id.pictureCard);
        }
    }
}

