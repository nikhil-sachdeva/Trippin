package com.example.nikhil.trippinauth;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Post> posts = new ArrayList<>();
    RecyclerView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=findViewById(R.id.list);
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


        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        PostsTask postsTask = new PostsTask(MainActivity.this,list);
        postsTask.execute();
    }


}
