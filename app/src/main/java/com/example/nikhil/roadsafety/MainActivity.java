package com.example.nikhil.roadsafety;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;

import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nikhil.roadsafety.Intro.IntroSlider;
import com.example.nikhil.roadsafety.Posts.PostActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements DangerFragment.OnFragmentInteractionListener
        ,NavigationFragment.OnFragmentInteractionListener {

    private android.support.v7.widget.Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    boolean isLogin=false;
    FloatingActionButton fab;
    FirebaseUser user;
    NavigationView navigationView;
    DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            isLogin = true;
        }
        viewPager = findViewById(R.id.pager);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        tabLayout = findViewById(R.id.tab_layout);
        navigationView=findViewById(R.id.nav_view);
        tabLayout.setupWithViewPager(viewPager);

       View header = navigationView.getHeaderView(0);
       TextView navText = header.findViewById(R.id.nav_text);
       TextView emailText=header.findViewById(R.id.nav_email);
        ImageView img = header.findViewById(R.id.img);
       if(user!=null) {
           navText.setText("Hi! " + user.getDisplayName());
           Picasso.get().load(user.getPhotoUrl()).into(img);
           emailText.setText(user.getEmail());

       }
        MainFragmentAdapter adapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.map_selector));
        tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.drive_selector));
    //    tabLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.ic_priority_high_black_24dp));


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               if(menuItem.getItemId()==R.id.posts){
                   startActivity(new Intent(MainActivity.this,PostActivity.class));
               }
               if(menuItem.getItemId()==R.id.nav_logout){
                   AuthUI.getInstance()
                           .signOut(MainActivity.this)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               public void onComplete(@NonNull Task<Void> task) {
                                   Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(MainActivity.this,MainActivity.class));
                               }
                           });
                   if(menuItem.getItemId()==R.id.help_nav){
                        startActivity(new Intent(MainActivity.this,HelpActivity.class));
                   }
               }

                return false;
            }
        });


        if (!isLogin) {
            startActivity(new Intent(MainActivity.this, IntroSlider.class));
        }
//
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(user!=null) {
            getMenuInflater().inflate(R.menu.menu_main_logout, menu);
        }
        else getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.logout){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,MainActivity.class));
                        }
                    });
        }
        if(id==R.id.login){
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(providers)
//                            .build(),
//                    RC_SIGN_IN);
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mDrawerLayout.closeDrawers();
    }
}

