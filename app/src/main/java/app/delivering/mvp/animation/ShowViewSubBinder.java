package app.delivering.mvp.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import app.R;

public class ShowViewSubBinder {


    public static void animateShow(ImageView image, int duration) {
        Animation animation = AnimationUtils.loadAnimation(image.getContext(), R.anim.animation_show);
        animation.setDuration(duration);
        image.setVisibility(View.VISIBLE);
        image.startAnimation(animation);
    }
}
