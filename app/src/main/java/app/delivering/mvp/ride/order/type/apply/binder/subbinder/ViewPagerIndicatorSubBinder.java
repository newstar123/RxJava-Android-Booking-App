package app.delivering.mvp.ride.order.type.apply.binder.subbinder;


import android.support.v4.view.ViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;

public class ViewPagerIndicatorSubBinder {
    private BaseActivity activity;

    public ViewPagerIndicatorSubBinder(BaseActivity activity) {
        this.activity = activity;
    }

    public void apply(List<RideCategory> rideCategories) {
        MagicIndicator orderRideTypePagerIndicator = (MagicIndicator) activity.findViewById(R.id.order_ride_type_pager_indicator);
        ViewPager orderRideTypeViewPager = (ViewPager) activity.findViewById(R.id.order_ride_type_pager);
        UberStyleNavigator commonNavigator = new UberStyleNavigator(activity);
        commonNavigator.setSkimOver(true);
        int padding = UIUtil.getScreenWidth(activity) / 2;
        commonNavigator.setRightPadding(padding);
        commonNavigator.setLeftPadding(padding);
        OrderRidePagerIndicatorAdapter adapter = new OrderRidePagerIndicatorAdapter(rideCategories,
                orderRideTypeViewPager);
        commonNavigator.setAdapter(adapter);
        orderRideTypePagerIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(orderRideTypePagerIndicator, orderRideTypeViewPager);
    }
}
