package com.example.nikhil.roadsafety.Posts;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostsTask extends AsyncTask {
    ArrayList<Post> posts = new ArrayList<>();
    Context context;
    RecyclerView list;
    ProgressDialog progressDialog;

    PostsTask(Context context, RecyclerView list){
       this.context=context;
        progressDialog = new ProgressDialog(context);
       this.list=list;
   }

    @Override
    protected void onPreExecute() {


        progressDialog.setMessage("Fetching routes, please wait");
        progressDialog.show();

    }

    @Override
    protected ArrayList<Post> doInBackground(Object...objects) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Post post =postSnapshot.getValue(Post.class);
                    posts.add(post);
                    Log.d("post", "onDataChange: "+post.toMap()+posts.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    return posts;
}

    @Override
    protected void onPostExecute(Object o) {
        PostAdapter postAdapter = new PostAdapter(context,posts);
        list.setAdapter(postAdapter);
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }


    }

}
