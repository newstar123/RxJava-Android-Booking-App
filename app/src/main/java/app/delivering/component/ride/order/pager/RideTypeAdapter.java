package app.delivering.component.ride.order.pager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import app.delivering.component.BaseActivity;
import app.delivering.component.BaseFragment;
import app.delivering.component.ride.order.type.OrderRideTypeFragment;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;
import rx.Observable;

public class RideTypeAdapter extends FragmentStatePagerAdapter {
    private List<BaseFragment> fragments;
    private List<RideCategory> data;

    public RideTypeAdapter(BaseActivity activity) {
        super(activity.getSupportFragmentManager());
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_UNCHANGED;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        RideCategory rideCategory = data.get(position);
        return rideCategory.getName();
    }

    public void setData(List<RideCategory> data) {
        this.data = data;
        fragments = new ArrayList<>();
        Observable.from(data).forEach(this::addFragment);
    }

    private void addFragment(RideCategory rideCategory) {
        OrderRideTypeFragment fragment = new OrderRideTypeFragment();
        Bundle args = new Bundle();
        args.putString(OrderRideTypeFragment.ORDER_TYPE_ID, rideCategory.getName());
        fragment.setArguments(args);
        addFragment(fragment);
    }

    private void addFragment(int index, BaseFragment fragment) {
        fragments.add(index, fragment);
        notifyDataSetChanged();
    }

    private void addFragment(BaseFragment fragment) {
        fragments.add(fragment);
        notifyDataSetChanged();
    }
}
