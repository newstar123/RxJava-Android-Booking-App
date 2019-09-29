package app.delivering.component.bar.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.lists.BarsListByDiscountFragment;
import app.delivering.component.bar.lists.BarsListByDistanceFragment;
import app.delivering.component.bar.lists.BarsListByNameFragment;

public class BarsViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragments;
    private final List<String> titleList;

    public BarsViewPagerAdapter(BaseActivity activity) {
        super(activity.getSupportFragmentManager());
        fragments = new ArrayList<>();
        titleList = new ArrayList<>();
        titleList.add(activity.getString(R.string.word_nearby));
        fragments.add(new BarsListByDistanceFragment());
        titleList.add(activity.getString(R.string.word_name));
        fragments.add(new BarsListByNameFragment());
        titleList.add(activity.getString(R.string.word_discount));
        fragments.add(new BarsListByDiscountFragment());
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
