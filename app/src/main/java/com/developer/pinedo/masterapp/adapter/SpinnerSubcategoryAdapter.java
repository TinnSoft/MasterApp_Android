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

public class SpinnerSubcategoryAdapter extends ArrayAdapter<SpinnerCategory> {

    public SpinnerSubcategoryAdapter(@NonNull Context context, ArrayList<SpinnerCategory> categoryList) {
        super(context, 0,categoryList);
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
            converView=LayoutInflater.from(getContext()).inflate(R.layout.spinner_category,parent,false);
        }

        TextView text_card=converView.findViewById(R.id.text_card);

        SpinnerCategory currentItem = getItem(position);

        if(currentItem!=null) {
            text_card.setText(currentItem.getTitle());
        }

        return converView;

    }
}
