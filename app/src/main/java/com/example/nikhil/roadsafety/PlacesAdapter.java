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


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesHolder> {


    ArrayList<PlaceModel> list;
    Context ctx;

    public PlacesAdapter(ArrayList<PlaceModel> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public PlacesAdapter.PlacesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(ctx);
        final View myOwnView = myInflater.inflate(R.layout.places_row, parent, false);

        return new PlacesHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.PlacesHolder holder, int position) {
        holder.list_name.setText(list.get(position).getPlaceName());
        holder.list_vicinity.setText(list.get(position).getVicinity());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PlacesHolder extends RecyclerView.ViewHolder{
        TextView list_name;
        TextView list_vicinity;


        public PlacesHolder(final View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.row_name);
            list_vicinity = itemView.findViewById(R.id.row_vicinity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mapUri = "geo:"+list.get(getAdapterPosition()).getLatitude()+","+list.get(getAdapterPosition()).getLongitude();
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
