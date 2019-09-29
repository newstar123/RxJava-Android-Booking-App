package app.delivering.mvp.bars.detail.init.tablist.tab.binder;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.fragment.adapter.BarsDetailFragmentPagerAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.tablist.tab.events.BarDetailOnTabItemSelectedEvent;
import butterknife.BindView;

public class BarDetailTabsBinder extends BaseBinder implements TabLayout.OnTabSelectedListener{
    private BarsDetailFragmentPagerAdapter fragmentPagerAdapter;
    @BindView(R.id.bar_detail_lists_pager) ViewPager viewPagerBarLis;
    @BindView(R.id.bar_details_tab_layout) TabLayout tabLayout;

    public BarDetailTabsBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        fragmentPagerAdapter = new BarsDetailFragmentPagerAdapter(getActivity());
        viewPagerBarLis.setAdapter(fragmentPagerAdapter);
        viewPagerBarLis.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPagerBarLis);
        tabLayout.setOnTabSelectedListener(this);
    }

    @Override public void onTabSelected(TabLayout.Tab tab) {
        EventBus.getDefault().post(new BarDetailOnTabItemSelectedEvent(tab.getPosition()));
    }

    @Override public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override public void onTabReselected(TabLayout.Tab tab) {

    }
}
