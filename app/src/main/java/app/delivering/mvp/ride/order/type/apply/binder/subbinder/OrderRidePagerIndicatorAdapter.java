package app.delivering.mvp.ride.order.type.apply.binder.subbinder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import java.util.List;

import app.R;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;


public class OrderRidePagerIndicatorAdapter extends CommonNavigatorAdapter {
    private List<RideCategory> rideCategories;
    private ViewPager orderRideTypeViewPager;

    public OrderRidePagerIndicatorAdapter(List<RideCategory> rideCategories, ViewPager orderRideTypeViewPager) {
        this.rideCategories = rideCategories;
        this.orderRideTypeViewPager = orderRideTypeViewPager;
    }

    @Override
    public int getCount() {
        return rideCategories.size();
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
        clipPagerTitleView.setText(rideCategories.get(index).getName().toUpperCase());
        clipPagerTitleView.setTextColor(Color.GRAY);
        clipPagerTitleView.setClipColor(Color.WHITE);

        Resources resources = orderRideTypeViewPager.getContext().getResources();
        clipPagerTitleView.setTextSize(resources.getDimension(R.dimen.sp16));
        clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderRideTypeViewPager.setCurrentItem(index);
            }
        });
        return clipPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        return null;
    }
}
