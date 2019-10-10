package com.developer.pinedo.masterapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.developer.pinedo.masterapp.ExperienceActivity;
import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.StudyActivity;
import com.developer.pinedo.masterapp.models.CardExtraInfo;

import java.util.ArrayList;
import java.util.List;


public class ExtraInfoAdapterRecicleView extends RecyclerView.Adapter<ExtraInfoAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "ChefAdapterRecicleView";
    private List<CardExtraInfo> listItem,filterList;
    private int resource;
    private Activity activity;
    View view;

    public ExtraInfoAdapterRecicleView(ArrayList<CardExtraInfo> listItem, int resource, Activity activity) {

        this.listItem = listItem;
        this.resource = resource;
        this.activity = activity;
        this.filterList=new ArrayList<CardExtraInfo>();
        this.filterList.addAll(this.listItem);
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final CardExtraInfo chef = filterList.get(position);

        holder.card_title.setText(chef.getCard_title());
        holder.card_description.setText(chef.getCard_description());
        holder.card_business.setText(chef.getCard_business());
        holder.card_date_init.setText(chef.getCard_date_init());
        holder.card_date_end.setText(chef.getCard_date_end());

        if (holder.editStudy!= null){
            holder.editStudy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putSerializable("study",chef);

                    if(chef.getCard_type_information_id() == 1){
                        Intent i =new Intent(activity, StudyActivity.class);
                        i.putExtras(b);
                        activity.startActivity(i);
                    }else{
                        Intent i =new Intent(activity, ExperienceActivity.class);
                        i.putExtras(b);
                        activity.startActivity(i);
                    }
                }
            });
        }


      /*  holder.card_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*Intent i =new Intent(activity,MenuChefActivity.class);
                Bundle b=new Bundle();
                b.putSerializable("chef",chef);
                i.putExtras(b);
                activity.startActivity(i);*//*

                Fragment fragment = new SubcategoryFragment();
                //Fragment fragment = new ListChefFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", chef.getCard_id());
                bundle.putString("title", chef.getCard_title());
                fragment.setArguments(bundle);
                ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();

            }
        });*/

        /*holder.schedule_card.setText(chef.getSchedule_card());
        holder.number_commnet_card.setText(chef.getNumber_commnet_card());
        holder.number_point_card.setText(chef.getNumber_point_card());
        holder.number_point_card.setText(chef.getNumber_point_card());*/
//        holder.like_number.setText(chef.getLike_number());


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

    public void filter(final String text){

        new Thread(new Runnable() {
            @Override
            public void run() {
                filterList.clear();
                if(TextUtils.isEmpty(text)){
                    filterList.addAll(listItem);
                }else{

                    for (CardExtraInfo item: listItem){
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
                    for (CardExtraInfo item: listItem){
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
                    for (CardExtraInfo item: listItem){
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

        private TextView card_title,card_description,card_business,card_date_init,card_date_end,editStudy;

        public ChefViewHolder(View itemView) {
            super(itemView);
            card_title = itemView.findViewById(R.id.card_title);
            card_description = itemView.findViewById(R.id.card_description);
            card_business = itemView.findViewById(R.id.card_business);
            card_date_init = itemView.findViewById(R.id.card_date_init);
            card_date_end = itemView.findViewById(R.id.card_date_end);
            editStudy = itemView.findViewById(R.id.editStudy);
        }
    }
}

