package app.delivering.mvp.main.init.binder;


import android.animation.Animator;
import android.text.TextUtils;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class InitLocationImageHandler {
    private ImageView imageView;

    public InitLocationImageHandler(ImageView imageView) {
        this.imageView = imageView;
    }

    public void showBackground(String backgroundImageUrl) {
        int finalHeight = imageView.getMeasuredHeight();
        int finalWidth = imageView.getMeasuredWidth();
        if (finalWidth != 0) {
            show(backgroundImageUrl);
        }else{
            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int finalHeight = imageView.getMeasuredHeight();
                    int finalWidth = imageView.getMeasuredWidth();
                    show(backgroundImageUrl);
                    return true;
                }
            });
        }
    }

    private void show(String backgroundImageUrl) {
        if (TextUtils.isEmpty(backgroundImageUrl)) return;
        Picasso picasso = Picasso.with(imageView.getContext());
        picasso.load(backgroundImageUrl)
                .resize(imageView.getMeasuredWidth(), imageView.getMeasuredHeight())
                .centerCrop()
                .into(imageView, new Callback() {
                    @Override public void onSuccess() {
                        animateImage(imageView);
                    }

                    @Override public void onError() {

                    }
                });
    }

    public static void animateImage(ImageView container) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && container.isAttachedToWindow()) {
            int x = container.getWidth();
            int y = container.getHeight();
            float radius = container.getWidth();
            Animator anim = ViewAnimationUtils.createCircularReveal(container, 1, 1, 0, radius);
            anim.setDuration(500);
            if (container.isAttachedToWindow())
                anim.start();
        }
    }
}
