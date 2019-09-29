package app.delivering.mvp.ride.order.animation.binder;


import android.view.Gravity;

import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;

public class OrderRideAnimation {

    public static void run(BaseActivity activity){
        TransitionManager.beginDelayedTransition(activity.findViewById(R.id.main_container),
                OrderRideAnimation.configure());
    }

    private static TransitionSet configure() {
        List<Integer> changeBoundsViews = Arrays.asList(R.id.order_ride_map,
                R.id.order_ride_information_section_align_for_map,
                R.id.order_ride_map_container,
                R.id.order_ride_information_section,
                R.id.order_ride_address_prediction,
                R.id.order_ride_powered_by_google,
                R.id.order_ride_pickup_estimate,
                R.id.progress,
                R.id.order_ride_address_divider_horizontal,
                R.id.order_ride_address,
                R.id.order_ride_bar);
        List<Integer>  bottomViews = Arrays.asList(R.id.order_ride_commit_ride_type,
                R.id.order_ride_start_ride);
        List<Integer> endViews = Arrays.asList(R.id.order_ride_pickup_estimate,
                R.id.order_ride_address_divider_horizontal,
                R.id.order_ride_address,
                R.id.root_order_ride_bar,
                R.id.order_ride_type_pager_indicator,
                R.id.order_ride_type_pager);
        List<Integer> topViews = Collections.singletonList(R.id.order_ride_advert_container);
        Transition slideTop = createSlide(Gravity.TOP, topViews);
        Transition slideEnd = createSlide(Gravity.END, endViews);
        Transition slideBottom = createSlide(Gravity.BOTTOM, bottomViews);
        Transition changeBounds = createChangeBounds(changeBoundsViews);
        return new TransitionSet()
                .addTransition(slideEnd)
                .addTransition(slideTop)
                .addTransition(changeBounds)
                .addTransition(slideBottom);
    }

    private static Transition createSlide(int slideType, List<Integer> views) {
        Slide slide = new Slide(slideType);
        for (Integer view : views)
            slide = (Slide) slide.addTarget(view);
        return slide;
    }

    private static Transition createChangeBounds(List<Integer> views) {
        ChangeBounds changeBounds = new ChangeBounds();
        for (Integer view : views)
            changeBounds = (ChangeBounds) changeBounds.addTarget(view);
        return changeBounds;
    }
}
