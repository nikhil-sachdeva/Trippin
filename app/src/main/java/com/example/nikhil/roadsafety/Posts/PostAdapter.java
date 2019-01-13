package com.example.nikhil.roadsafety.Posts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikhil.roadsafety.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PostAdapter  extends RecyclerView.Adapter<PostAdapter.Holder>{
    private LayoutInflater inflater;
    ArrayList<Post> results=new ArrayList<>();
    Context context;


    public PostAdapter(Context context, ArrayList<Post> results) {

        this.context=context;
        this.results=results;
        inflater = LayoutInflater.from(context);

    }




    @NonNull
    @Override
    public PostAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.post_view, parent, false);
        final Holder holder = new Holder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {

        holder.caption.setText(results.get(position).getCaption());
        Picasso.get().load(results.get(position).getImgURI()).into(holder.image_post);
        String loc = results.get(position).getLocation();
        holder.place.setText(loc);
        holder.name.setText(results.get(position).getName());
        if(results.get(position).getResolved()=="true"){
            holder.resolvedIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.resolved_green_24dp));
        }
        if(results.get(position).getResolved()=="false"){
            holder.resolvedIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.unresolved_red));
        }



    }





    @Override
    public int getItemCount() {
        return results.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView caption;
        ImageView image_post;
        TextView place,name;
        ImageView intentIcon,resolvedIcon;
        private Holder(View itemView) {
            super(itemView);
            caption=itemView.findViewById(R.id.caption);
            image_post=itemView.findViewById(R.id.image_post);
            place=itemView.findViewById(R.id.place);
            name=itemView.findViewById(R.id.row_name);
            intentIcon = itemView.findViewById(R.id.intent_icon);
            resolvedIcon=itemView.findViewById(R.id.resolved_icon);
            intentIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mapUri = "geo:"+results.get(getAdapterPosition()).getLatitude()+","+results.get(getAdapterPosition()).getLongitude();
                    Uri gmmIntentUri = Uri.parse(mapUri);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    }
                }
            });



        }


    }
}
