package com.example.nikhil.roadsafety.Posts;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.nikhil.roadsafety.R;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "ss";
    RecyclerView list;
    ArrayList<Post> posts = new ArrayList<>();
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        list=(RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true);
        fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,CreatePost.class));
            }
        });
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        PostsTask postsTask = new PostsTask(PostActivity.this,list);
        postsTask.execute();

    }
}
