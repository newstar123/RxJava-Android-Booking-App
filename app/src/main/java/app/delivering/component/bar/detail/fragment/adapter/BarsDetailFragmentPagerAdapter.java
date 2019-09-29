package app.delivering.component.bar.detail.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.fragment.feature.BarDetailFeaturesFragment;
import app.delivering.component.bar.detail.fragment.hours.BarDetailHoursFragment;
import app.delivering.component.bar.detail.fragment.insidetips.BarDetailInsideTipsFragment;

public class BarsDetailFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragments;
    private final List<String> titleList;

    public BarsDetailFragmentPagerAdapter(BaseActivity activity) {
        super(activity.getSupportFragmentManager());
        fragments = new ArrayList<>();
        titleList = new ArrayList<>();
        titleList.add(activity.getString(R.string.word_tips));
        fragments.add(new BarDetailInsideTipsFragment());
        titleList.add(activity.getString(R.string.word_features));
        fragments.add(new BarDetailFeaturesFragment());
        titleList.add(activity.getString(R.string.word_hours));
        fragments.add(new BarDetailHoursFragment());
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
