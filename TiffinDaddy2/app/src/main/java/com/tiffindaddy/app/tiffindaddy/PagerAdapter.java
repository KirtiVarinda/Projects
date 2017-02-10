package com.tiffindaddy.app.tiffindaddy;

/**
 * Created by vikas on 2/3/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinCategories;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private TiffinCategories tiffinCategories[];


    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        tiffinCategories=ProductPage.tiffinCategories;
    }

    @Override
    public Fragment getItem(int position) {


        if(tiffinCategories.length == 3){
            switch (position) {
                case 0:
                    BreakfastFragment tab1 = new BreakfastFragment();
                    return tab1;
                case 1:
                    LunchFragment tab2 = new LunchFragment();
                    return tab2;
                case 2:
                    DinnerFragment tab3 = new DinnerFragment();
                    return tab3;
                case 3:
                    AddonsFragment tab4 = new AddonsFragment();
                    return tab4;
                default:
                    return null;
            }

        }else if(tiffinCategories.length == 2){
            switch (position) {
                case 0:
                    LunchFragment tab1 = new LunchFragment();
                    return tab1;
                case 1:
                    DinnerFragment tab2 = new DinnerFragment();
                    return tab2;
                case 2:
                    AddonsFragment tab3 = new AddonsFragment();
                    return tab3;
                default:
                    return null;
            }

        }else if(tiffinCategories.length == 1){
            switch (position) {
                case 0:
                    DinnerFragment tab1 = new DinnerFragment();
                    return tab1;
                case 1:
                    AddonsFragment tab2 = new AddonsFragment();
                    return tab2;
                default:
                    return null;
            }

        }else{
            return  null;
        }


    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}