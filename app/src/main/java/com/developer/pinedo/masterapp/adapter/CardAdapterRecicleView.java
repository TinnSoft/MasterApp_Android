package com.developer.pinedo.masterapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.models.CardClient;

import java.util.ArrayList;
import java.util.List;


public class CardAdapterRecicleView extends RecyclerView.Adapter<CardAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "CardAdapterRecicleView";
    private List<CardClient> listItem,filterList;
    private int resource;
    private Activity activity;
    View view;

    public CardAdapterRecicleView(ArrayList<CardClient> listItem, int resource, Activity activity) {

        this.listItem = listItem;
        this.resource = resource;
        this.activity = activity;
        this.filterList=new ArrayList<CardClient>();
        this.filterList.addAll(this.listItem);
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final CardClient chef = filterList.get(position);



        //holder.card_number.setText(chef.getCard_number());

    /*    holder.card_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*Intent i =new Intent(activity,MenuChefActivity.class);
                Bundle b=new Bundle();
                b.putSerializable("chef",chef);
                i.putExtras(b);
                activity.startActivity(i);*//*

                *//*Fragment fragment = new SubcategoryFragment();
                //Fragment fragment = new ListChefFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", chef.getCard_id());
                bundle.putString("title", chef.getCard_title());
                fragment.setArguments(bundle);
                ((FragmentActivity)activity).getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();*//*

            }
        });
*/
        /*holder.schedule_card.setText(chef.getSchedule_card());
        holder.number_commnet_card.setText(chef.getNumber_commnet_card());
        holder.number_point_card.setText(chef.getNumber_point_card());
        holder.number_point_card.setText(chef.getNumber_point_card());*/
//        holder.like_number.setText(chef.getLike_number());

        //Picasso.with(activity).load(picture.getPicture()).into(holder.pictureCard);
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

        private TextView card_number;

        public ChefViewHolder(View itemView) {
            super(itemView);

            card_number = itemView.findViewById(R.id.card_number);
       /*     card_complete = itemView.findViewById(R.id.card_complete);
            card_photo = itemView.findViewById(R.id.card_photo);*/
        }
    }
}

