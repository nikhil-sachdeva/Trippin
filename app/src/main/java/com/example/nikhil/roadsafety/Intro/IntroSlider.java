package com.example.nikhil.roadsafety.Intro;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nikhil.roadsafety.R;

public class IntroSlider extends AppCompatActivity {
    private ViewPager viewPager;
    Button btnNext,btnSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);


        viewPager=findViewById(R.id.view_pager);
        btnNext=findViewById(R.id.btn_next);
        btnSkip=findViewById(R.id.btn_skip);
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
//
//        ActionBar actionBar = getActionBar();
//        if(actionBar!=null) {
//            actionBar.hide();
//        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        IntroPagerAdapter introPagerAdapter = new IntroPagerAdapter(fragmentManager);
        viewPager.setAdapter(introPagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos=viewPager.getCurrentItem();
                viewPager.setCurrentItem(pos+1);
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3);
            }
        });
    }



}
