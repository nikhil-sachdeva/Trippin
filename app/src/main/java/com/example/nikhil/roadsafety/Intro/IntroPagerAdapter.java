package com.example.nikhil.roadsafety.Intro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class IntroPagerAdapter extends FragmentPagerAdapter {
    public IntroPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        if(i==0){
            fragment=new Slide1();
        }
        if(i==1){
            fragment = new Slide2();
        }
        if(i==2){
            fragment = new Slide3();
        }
        if(i==3){
            fragment = new Slide4();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }


}
