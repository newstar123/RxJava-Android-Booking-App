package app.delivering.mvp.main.init.binder.background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import app.R;

public class InitialBackgroundStateListener {
    private static final int SHOW_BACKGROUND_DURATION = 2000;
    private static final int BLUR_BACKGROUND_DURATION = 1000;
    private static final int HIDE_LOGO_DURATION = 500;
    private BackgroundStateListener backgroundStateListener;
    private Context context;
    private Animation initialFadeBackgroundAnimation;
    private Animation showBlurredBackgroundAnimation;
    private Animation hideAppLogoAnimation;
    private final Target initialCityImageTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            backgroundStateListener.onInitialAppBackgroundLoaded(new BitmapDrawable(context.getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            backgroundStateListener.onFailedBackgroundLoading();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    private final Target blurredCityImageTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            BitmapDrawable image = new BitmapDrawable(context.getResources(), bitmap);
            image.setColorFilter(context.getResources().getColor(R.color.color_CC050D24), PorterDuff.Mode.DARKEN);
            backgroundStateListener.onBlurredAppBackgroundLoaded(image);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            backgroundStateListener.onFailedBackgroundLoading();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    public InitialBackgroundStateListener(BackgroundStateListener listener, Context context) {
        backgroundStateListener = listener;
        this.context = context;

        initialFadeBackgroundAnimation = AnimationUtils.loadAnimation(context, R.anim.animation_fade);
        initialFadeBackgroundAnimation.setDuration(SHOW_BACKGROUND_DURATION);
        initialFadeBackgroundAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
            }

            @Override public void onAnimationEnd(Animation animation) {
                backgroundStateListener.onInitialAppBackgroundAnimationEnded();
            }

            @Override public void onAnimationRepeat(Animation animation) { }
        });

        showBlurredBackgroundAnimation = AnimationUtils.loadAnimation(context, R.anim.animation_show);
        showBlurredBackgroundAnimation.setDuration(BLUR_BACKGROUND_DURATION);
        showBlurredBackgroundAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
            }

            @Override public void onAnimationEnd(Animation animation) {
                backgroundStateListener.onBlurredAppBackgroundAnimationEnded();
            }

            @Override public void onAnimationRepeat(Animation animation) { }
        });

        hideAppLogoAnimation = AnimationUtils.loadAnimation(context, R.anim.animation_fade);
        hideAppLogoAnimation.setDuration(HIDE_LOGO_DURATION);
        hideAppLogoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }

            @Override public void onAnimationEnd(Animation animation) {
                backgroundStateListener.onHideAppLogoAnimationEnded();
            }

            @Override public void onAnimationRepeat(Animation animation) { }
        });
    }

    public Target getInitialCityImageTarget() {
        return initialCityImageTarget;
    }

    public Target getBlurredCityImageTarget() {
        return blurredCityImageTarget;
    }

    public Animation getInitialFadeBackgroundAnimation() {
        return initialFadeBackgroundAnimation;
    }

    public Animation getShowBlurredBackgroundAnimation() {
        return showBlurredBackgroundAnimation;
    }

    public Animation getHideAppLogoAnimation() {
        return hideAppLogoAnimation;
    }

    public void onDestroy() {
        initialFadeBackgroundAnimation.reset();
        initialFadeBackgroundAnimation.cancel();
        showBlurredBackgroundAnimation.reset();
        showBlurredBackgroundAnimation.cancel();
        hideAppLogoAnimation.reset();
        hideAppLogoAnimation.cancel();
    }
}
