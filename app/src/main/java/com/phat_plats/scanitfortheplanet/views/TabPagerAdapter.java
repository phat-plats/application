package com.phat_plats.scanitfortheplanet.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.phat_plats.scanitfortheplanet.fragments.CommentsFragment;
import com.phat_plats.scanitfortheplanet.fragments.ProductInfoFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private final Bundle data;

    private String tabTitles[] = new String[] { "Product Info", "Comments" };
    public TabPagerAdapter(FragmentManager fm, Bundle data) {
        super(fm);
        this.data = data;
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
        current.setArguments(data);
        return current;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
