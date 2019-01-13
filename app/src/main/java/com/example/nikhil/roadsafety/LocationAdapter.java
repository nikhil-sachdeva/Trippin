package com.example.nikhil.roadsafety;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationHolder> {


    ArrayList<LocationData> list;
    Context ctx;

    public LocationAdapter(ArrayList<LocationData> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public LocationAdapter.LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(ctx);
        final View myOwnView = myInflater.inflate(R.layout.location_row, parent, false);

        return new LocationHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.LocationHolder holder, int position) {
        holder.list_name.setText(list.get(position).getName());
        holder.list_distance.setText(new DecimalFormat("##.#").format(list.get(position).getDistance())+" km");
        holder.list_index.setText(new DecimalFormat("##.##").format(list.get(position).getScore())+"");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LocationHolder extends RecyclerView.ViewHolder{
        TextView list_index;
        TextView list_name;
        TextView list_distance;


        public LocationHolder(final View itemView) {
            super(itemView);
            list_index = itemView.findViewById(R.id.row_index);
            list_name = itemView.findViewById(R.id.row_name);
            list_distance = itemView.findViewById(R.id.row_distance);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mapUri = "geo:"+list.get(getAdapterPosition()).getLat()+","+list.get(getAdapterPosition()).getLang();
                    Uri gmmIntentUri = Uri.parse(mapUri);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(ctx.getPackageManager()) != null) {
                        ctx.startActivity(mapIntent);
                    }
                }
            });

        }
    }

}
