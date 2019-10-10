package com.developer.pinedo.masterapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.models.CardFilters;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FilterAdapterRecicleView extends RecyclerView.Adapter<FilterAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "FilterAdapter";
    private List<CardFilters> listItem,filterList;
    private int resource;
    private Activity activity;
    View view;

    public FilterAdapterRecicleView(ArrayList<CardFilters> listItem, int resource, Activity activity) {
        this.listItem = listItem;
        this.resource = resource;
        this.activity = activity;
        this.filterList=new ArrayList<CardFilters>();
        this.filterList.addAll(this.listItem);
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final CardFilters chef = filterList.get(position);

       Glide
                .with(this.activity.getApplicationContext())
                .load(chef.getCard_photo())
               //.centerInside()
                //.centerCrop()
                .placeholder(R.drawable.camera)
                .into(holder.card_image);


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

                    for (CardFilters item: listItem){
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
                    for (CardFilters item: listItem){
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
                    for (CardFilters item: listItem){
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

        private CircleImageView card_image;

        public ChefViewHolder(View itemView) {
            super(itemView);
            card_image = itemView.findViewById(R.id.card_image);

        }
    }
}

