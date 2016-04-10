package com.phat_plats.scanitfortheplanet.views;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.phat_plats.scanitfortheplanet.fragments.CommentsFragment;
import com.phat_plats.scanitfortheplanet.fragments.ProductInfoFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Product Info", "Comments" };
    private Context context;

    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment current;
        switch (position) {
            case 0:
                current = ProductInfoFragment.newInstance();
                break;
            case 1:
                current = CommentsFragment.newInstance();
                break;
            default:
                current = ProductInfoFragment.newInstance();
                break;
        }
        return current;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
