package vn.edu.uit.mmlab.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import vn.edu.uit.mmlab.fragment.AlgorithmFragment;

public class AlgorithmPager extends FragmentPagerAdapter {
    private final String[] ALGORITHM = {"Face Detection", "Face Recognition", "Image Search", "Object Detection", "Personal attribute", "Upload Gallery"};

    public AlgorithmPager(FragmentManager fm) {
        super(fm);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return ALGORITHM[position];
    }
    @Override
    public Fragment getItem(int position) {
        return AlgorithmFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return ALGORITHM.length;
    }
}
