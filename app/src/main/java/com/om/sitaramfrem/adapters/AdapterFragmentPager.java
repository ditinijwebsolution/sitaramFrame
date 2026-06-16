package com.om.sitaramfrem.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdapterFragmentPager extends FragmentStateAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public AdapterFragmentPager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public AdapterFragmentPager(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}
