package com.example.nikhil.trippinauth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder>{
    private LayoutInflater inflater;
    ArrayList<Post> results=new ArrayList<>();
    Context context;
    DatabaseReference database= FirebaseDatabase.getInstance().getReference("posts");


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


        if(results.get(position).getResolved()=="true"){
            holder.resolveBtn.setVisibility(View.INVISIBLE);

        }
        holder.caption.setText(results.get(position).getCaption());
        Picasso.get().load(results.get(position).getImgURI()).into(holder.image_post);
        String loc = results.get(position).getLocation();
        holder.place.setText(loc);
        holder.name.setText(results.get(position).getName());
        if (results!=null) {
            if (results.get(position).getResolved().equals("true")) {
                holder.resolvedIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.resolved_green_24dp));
            }
            if (results.get(position).getResolved().equals("false")) {
                holder.resolvedIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.unresolved_red));
            }
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
        Button resolveBtn;
        private Holder(View itemView) {
            super(itemView);
            caption=itemView.findViewById(R.id.caption);
            image_post=itemView.findViewById(R.id.image_post);
            place=itemView.findViewById(R.id.place);
            name=itemView.findViewById(R.id.row_name);
            intentIcon = itemView.findViewById(R.id.intent_icon);
            resolveBtn=itemView.findViewById(R.id.btn_resolved);
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



            resolveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("onCLick", "onClick: N"+results.get(getAdapterPosition()).getN());
                    if(results.get(getAdapterPosition()).getResolved().equals("false")){
                        Log.d("update", "onClick: "+results.get(getAdapterPosition()));
                        resolveBtn.setVisibility(View.INVISIBLE);
                        resolvedIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.resolved_green_24dp));
                        results.get(getAdapterPosition()).setResolved("true");
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(String.valueOf(results.get(getAdapterPosition()).getN()), results.get(getAdapterPosition()).toMap());
                        database.updateChildren(childUpdates);
                         }
                    if(results.get(getAdapterPosition()).getResolved().equals("true")){
                        resolveBtn.setVisibility(View.INVISIBLE);
                    }
                }
            });



        }


    }
}
