package com.example.mysmartnutrition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    ArrayList product_name, product_manufacture, product_kcal;

    CustomAdapter(Context context,
                  ArrayList product_name, ArrayList product_manufacture, ArrayList product_kcal) {
        this.context = context;
        this.product_name = product_name;
        this.product_manufacture = product_manufacture;
        this.product_kcal = product_kcal;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.product_name_txt.setText(String.valueOf(product_name.get(position)));
        holder.product_manufacture_txt.setText(String.valueOf(product_manufacture.get(position)));
        holder.product_kcal_txt.setText(String.valueOf(product_kcal.get(position)));
    }

    @Override
    public int getItemCount() {
        return product_name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView product_name_txt, product_manufacture_txt, product_kcal_txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            product_manufacture_txt = itemView.findViewById(R.id.product_manufacture_txt);
            product_kcal_txt = itemView.findViewById(R.id.product_kcal_txt);
            product_name_txt = itemView.findViewById(R.id.product_name_txt);


        }
    }


}
