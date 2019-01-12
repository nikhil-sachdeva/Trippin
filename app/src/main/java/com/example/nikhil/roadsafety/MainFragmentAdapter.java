package com.example.nikhil.roadsafety;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainFragmentAdapter extends FragmentStatePagerAdapter {
    private static final int TAB_1 = 0;
    private static final int TAB_2 = 1;
    private static final int TAB_3 = 2;
    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case TAB_1:
                NavigationFragment tab1 = new NavigationFragment();
                return tab1;

            case TAB_2:
                DrivingModeFragment tab2 = new DrivingModeFragment();
                return tab2;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
       switch (position) {
           case 0: return "Maps";
           case 1: return "Driving Mode";
           case 2: return "Help";
       }
    return null;
    }

}
