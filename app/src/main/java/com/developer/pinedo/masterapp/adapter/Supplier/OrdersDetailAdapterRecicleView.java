package com.developer.pinedo.masterapp.adapter.Supplier;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.pinedo.masterapp.models.CardOrders;
import com.developer.pinedo.masterapp.models.CardOrdersDetail;

import java.util.ArrayList;
import java.util.List;


public class OrdersDetailAdapterRecicleView extends RecyclerView.Adapter<OrdersDetailAdapterRecicleView.ChefViewHolder>{
    private static final String TAG = "ChefAdapterRecicleView";
    private List<CardOrdersDetail> listItem,filterList;
    private int resource;
    private Activity activity;

    public OrdersDetailAdapterRecicleView(ArrayList<CardOrdersDetail> listItem, int resource, Activity activity) {

        this.listItem = listItem;
        this.resource = resource;
        this.activity = activity;
        this.filterList=new ArrayList<CardOrdersDetail>();
        this.filterList.addAll(this.listItem);
    }

    @Override
    public ChefViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ChefViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChefViewHolder holder, int position) {
        final CardOrdersDetail chef = filterList.get(position);

    /*    holder.card_name.setText(chef.getSupplier());
        holder.card_status.setText(chef.getStatus());

        //String price = "$ "+NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(chef.getCard_price()));
       holder.card_price.setText("$"+chef.getSubtotal());

        holder.card_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(activity,ReceiveClientActivity.class);
                Bundle b=new Bundle();
                b.putSerializable("order",chef);
                b.putString("id",String.valueOf(chef.getId()));
                i.putExtras(b);
                activity.startActivity(i);
            }
        });
*/
        /*holder.schedule_card.setText(chef.getSchedule_card());
        holder.number_commnet_card.setText(chef.getNumber_commnet_card());
        holder.number_point_card.setText(chef.getNumber_point_card());
        holder.number_point_card.setText(chef.getNumber_point_card());*/
//        holder.like_number.setText(chef.getLike_number());

        //Picasso.with(activity).load(picture.getPicture()).into(holder.pictureCard);

//        Glide
//                .with(this.activity.getApplicationContext())
//                .load(chef.getPhoto_supplier())
//                .centerCrop()
//                .placeholder(R.drawable.profile)
//                .into(holder.card_photo);

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


       /* private TextView card_name;
        private CircleImageView card_photo;
        private TextView card_status;
        private TextView card_price;
        private CardView card_complete;
*/
        public ChefViewHolder(View itemView) {
            super(itemView);

            /*card_name = itemView.findViewById(R.id.card_name);
            card_photo = itemView.findViewById(R.id.card_photo);
            card_status= itemView.findViewById(R.id.card_status);
            card_price = itemView.findViewById(R.id.card_price);
            card_complete = itemView.findViewById(R.id.card_complete);*/
        }
    }
}

