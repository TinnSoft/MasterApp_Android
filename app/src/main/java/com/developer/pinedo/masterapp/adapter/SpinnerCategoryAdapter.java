package com.developer.pinedo.masterapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.developer.pinedo.masterapp.R;
import com.developer.pinedo.masterapp.models.SpinnerCategory;

import java.util.ArrayList;

public class SpinnerCategoryAdapter extends ArrayAdapter<SpinnerCategory> {

    public  Context context;

    public SpinnerCategoryAdapter(@NonNull Context context, ArrayList<SpinnerCategory> categoryList) {
        super(context, 0,categoryList);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    public View initView(int position, View converView,ViewGroup parent){
        if(converView==null){
            converView=LayoutInflater.from(getContext()).inflate(R.layout.spinner_category_image,parent,false);
        }

        //ImageView imageView=converView.findViewById(R.id.img_card);
        TextView text_card=converView.findViewById(R.id.text_card);

        SpinnerCategory currentItem = getItem(position);

        if(currentItem!=null) {

            text_card.setText(currentItem.getTitle().toUpperCase());
            /*Glide
                    .with(this.context)
                    .load(Utils.IP+"/"+currentItem.getUrl())
                    .centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .into(imageView);*/
            //Picasso.get().load(Utils.IP+"/"+currentItem.getUrl()).into(imageView);
        }

        return converView;

    }
}
