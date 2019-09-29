package app.qamode.mvp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.qamode.component.QaBackgroundTabFragment;
import app.qamode.component.QaEnvironmentTabFragment;

public class QaModeTopicsPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragments;
    private final List<String> titleList;

    public QaModeTopicsPagerAdapter(BaseActivity activity) {
        super(activity.getSupportFragmentManager());
        fragments = new ArrayList<>();
        titleList = new ArrayList<>();
        titleList.add(activity.getString(R.string.qa_mode_environment));
        fragments.add(new QaEnvironmentTabFragment());
        titleList.add(activity.getString(R.string.qa_mode_background));
        fragments.add(new QaBackgroundTabFragment());
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
