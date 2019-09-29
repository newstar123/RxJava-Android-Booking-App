package app.delivering.mvp.main.init.binder.background;

import android.graphics.drawable.BitmapDrawable;

import app.core.init.token.entity.Token;

public interface BackgroundStateListener {

    void onInitialAppBackgroundLoaded(BitmapDrawable drawable);

    void onBlurredAppBackgroundLoaded(BitmapDrawable drawable);

    void onInitialAppBackgroundAnimationEnded();

    void onBlurredAppBackgroundAnimationEnded();

    void onLoginStateVerified(Token token);

    void onLoginStateVerificationError(Throwable e);

    void onHideAppLogoAnimationEnded();

    void onFailedBackgroundLoading();
}
