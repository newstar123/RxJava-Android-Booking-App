package app.delivering.mvp.ride.order.route.apply.vendor.binder.map;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class PolylineAnimator {

    private static PolylineAnimator mapAnimator;
    private AnimatorSet firstRunAnimSet;
    private AnimatorSet secondLoopRunAnimSet;
    private Polyline backgroundPolyline;
    private Polyline foregroundPolyline;
    private PolylineOptions optionsForeground;
    private PolylineOptions optionsBackground;

    public static final int GREY = Color.parseColor("#FFA7A6A6");
    public static final int BLUE = Color.parseColor("#01ABDD");

    private PolylineAnimator() { }

    public static PolylineAnimator getInstance() {
        if (mapAnimator == null)
            mapAnimator = new PolylineAnimator();
        return mapAnimator;
    }

    public void animate(GoogleMap googleMap, List<LatLng> bangaloreRoute) {
        if (firstRunAnimSet == null) {
            firstRunAnimSet = new AnimatorSet();
        } else {
            firstRunAnimSet.removeAllListeners();
            firstRunAnimSet.end();
            firstRunAnimSet.cancel();

            firstRunAnimSet = new AnimatorSet();
        }
        if (secondLoopRunAnimSet == null) {
            secondLoopRunAnimSet = new AnimatorSet();
        } else {
            secondLoopRunAnimSet.removeAllListeners();
            secondLoopRunAnimSet.end();
            secondLoopRunAnimSet.cancel();

            secondLoopRunAnimSet = new AnimatorSet();
        }
        //Reset the polylines
        if (foregroundPolyline != null)
            foregroundPolyline.remove();
        if (backgroundPolyline != null)
            backgroundPolyline.remove();

        backgroundPolyline = googleMap.addPolyline(optionsBackground);
        foregroundPolyline = googleMap.addPolyline(optionsForeground);

        final ValueAnimator foregroundSizeAnimator = ValueAnimator.ofInt(0, 100);
        foregroundSizeAnimator.setDuration(2000);
        foregroundSizeAnimator.setInterpolator(new DecelerateInterpolator());
        foregroundSizeAnimator.addUpdateListener(animation -> {
            List<LatLng> foregroundPoints = backgroundPolyline.getPoints();
            int percentageValue = (int) animation.getAnimatedValue();
            int pointCount = foregroundPoints.size();
            int countTobeRemoved = (int) (pointCount * (percentageValue / 100.0f));
            List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
            subListTobeRemoved.clear();
            foregroundPolyline.setPoints(foregroundPoints);
        });

        foregroundSizeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                foregroundPolyline.setColor(GREY);
                List<LatLng> points = backgroundPolyline.getPoints();
                foregroundPolyline.setPoints(points);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ValueAnimator foregroundColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), GREY, BLUE);
        foregroundColorAnimation.setInterpolator(new AccelerateInterpolator());
        foregroundColorAnimation.setDuration(1200); // milliseconds

        foregroundColorAnimation.addUpdateListener(animator -> foregroundPolyline.setColor((int) animator.getAnimatedValue()));
        final ValueAnimator firstRouteAnimator = ValueAnimator.ofInt(0, 100);
        firstRouteAnimator.setDuration(2000);
        firstRouteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        firstRouteAnimator.addUpdateListener(animation -> {
            List<LatLng> foregroundPoints = new ArrayList<>(bangaloreRoute);
            int percentageValue = (int) animation.getAnimatedValue();
            int pointCount = foregroundPoints.size();
            int countTobeRemoved = (int) (pointCount * (percentageValue / 100.0f));
            List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
            foregroundPolyline.setPoints(subListTobeRemoved);
        });

        firstRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                List<LatLng> points = foregroundPolyline.getPoints();
                backgroundPolyline.setPoints(points);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        firstRunAnimSet.playSequentially(firstRouteAnimator, foregroundSizeAnimator);
        firstRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        secondLoopRunAnimSet.playSequentially(foregroundColorAnimation, foregroundSizeAnimator);
        secondLoopRunAnimSet.setStartDelay(200);
        secondLoopRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        firstRunAnimSet.start();
    }

    public PolylineOptions getOptionsForeground() {
        return optionsForeground;
    }

    public void setOptionsForeground(PolylineOptions optionsForeground) {
        this.optionsForeground = optionsForeground;
    }

    public PolylineOptions getOptionsBackground() {
        return optionsBackground;
    }

    public void setOptionsBackground(PolylineOptions optionsBackground) {
        this.optionsBackground = optionsBackground;
    }

    public void stop() {
        if (firstRunAnimSet != null) {
            firstRunAnimSet.removeAllListeners();
            firstRunAnimSet.end();
            firstRunAnimSet.cancel();
        }
        if (secondLoopRunAnimSet != null) {
            secondLoopRunAnimSet.removeAllListeners();
            secondLoopRunAnimSet.end();
            secondLoopRunAnimSet.cancel();
        }
    }
}

