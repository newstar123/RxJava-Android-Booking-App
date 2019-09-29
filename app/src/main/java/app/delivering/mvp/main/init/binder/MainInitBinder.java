package app.delivering.mvp.main.init.binder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.R;
import app.core.bars.locations.entity.exceptions.TooFarLocationException;
import app.core.beacon.bluetooth.entity.BluetoothNotAvailableException;
import app.core.beacon.bluetooth.entity.BluetoothStateException;
import app.core.init.token.entity.Token;
import app.core.login.facebook.entity.LoginResponse;
import app.core.permission.entity.LocationSettingsException;
import app.core.permission.entity.NetworkSettingsException;
import app.core.starttour.entity.StartTourShowingError;
import app.core.version.entity.OutdatedAppVersionException;
import app.delivering.component.bar.lists.floating.CustomFloatingButton;
import app.delivering.component.locationblocker.LocationBlockerActivity;
import app.delivering.component.main.MainActivity;
import app.delivering.component.main.bluetooth.EnableBluetoothActivity;
import app.delivering.component.main.version.TimeToUpdateActivity;
import app.delivering.component.profile.age.InitialCheckAgeActivity;
import app.delivering.component.starttour.activity.StartTourActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.freeride.events.UpdateFreeRideListEvent;
import app.delivering.mvp.bars.list.item.branchclick.events.ClickByBranchEvent;
import app.delivering.mvp.coach.profile.check.events.CheckSecondLaunchEvent;
import app.delivering.mvp.coach.profile.init.events.UpdateBackgroundEvent;
import app.delivering.mvp.dialog.base.binder.BaseDialogBinder;
import app.delivering.mvp.dialog.base.events.ShowNotificationDialogEvent;
import app.delivering.mvp.main.init.binder.background.BackgroundStateListener;
import app.delivering.mvp.main.init.binder.background.InitialBackgroundStateListener;
import app.delivering.mvp.main.init.binder.subbinder.MainInitCheckStartTypeSubBinder;
import app.delivering.mvp.main.init.binder.subbinder.MainInitLoginSubBinder;
import app.delivering.mvp.main.init.events.GpsPermissionDialogHiddenEvent;
import app.delivering.mvp.main.init.events.OnStartMainActivityEvent;
import app.delivering.mvp.main.init.events.UpdateMainActionBarEvent;
import app.delivering.mvp.main.init.presenter.MainInitPresenter;
import app.delivering.mvp.main.service.init.events.ActivateRootServicesEvent;
import app.delivering.mvp.network.events.InternetConnectedEvent;
import app.delivering.mvp.network.events.InternetErrorConnectionEvent;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.profile.age.binder.InitialCheckAgeBinder;
import app.delivering.mvp.profile.age.exception.InitialCheckAgeError;
import butterknife.BindView;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainInitBinder extends BaseBinder implements BackgroundStateListener {
    private static final int BLUR_LOGO_ANIMATION_DURATION = 1500;
    private static final int WAITING_DURATION = 2000;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.bar_list_view_pager) ViewPager viewPagerBarLis;
    @BindView(R.id.splash_screen) RelativeLayout splashScreen;
    @BindView(R.id.city_background) ImageView splashBackground;
    @BindView(R.id.splash_screen_logo) ImageView splashScreenLogo;
    @BindView(R.id.to_map_floating_button) CustomFloatingButton floatingActionButton;
    private final InitExceptionHandler initExceptionHandler;
    private final MainInitPresenter presenter;
    private final InitialBackgroundStateListener backgroundStateListener;
    private final MainInitLoginSubBinder loginSubBinder;
    private Subscription subscription;
    private boolean isGPSPermissionDialogShown;
    private boolean isBluetoothEnableActivityShown;
    private String savedImageUrl;
    private boolean isAuthorizedMode;

    public MainInitBinder(MainActivity activity) {
        super(activity);
        presenter = new MainInitPresenter(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
        backgroundStateListener = new InitialBackgroundStateListener(this, getActivity());
        loginSubBinder = new MainInitLoginSubBinder(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(OnStartMainActivityEvent event) {
        if (isGPSPermissionDialogShown)
            return;
        if (subscription == null || subscription.isUnsubscribed()) {
            showProgress();
            subscription = presenter.process()
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(this::addBackgroundFrom, this::showError, this::hideProgress);
        }
    }

    private void addBackgroundFrom(String imageUrl) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        RequestCreator request = Picasso.with(getActivity()).load(imageUrl)
                .centerCrop().resize(size.x, size.y);
        if (TextUtils.isEmpty(savedImageUrl))
            request.into(backgroundStateListener.getInitialCityImageTarget());
        else {
            request.transform(new BlurTransformation(getActivity(), 10))
                    .into(backgroundStateListener.getBlurredCityImageTarget());
        }
        savedImageUrl = imageUrl;
    }

    @Override
    public void onInitialAppBackgroundLoaded(BitmapDrawable drawable) {
        getActivity().getWindow().getDecorView().setBackground(drawable);
        if (splashScreen != null && splashScreen.isAttachedToWindow())
            splashBackground.startAnimation(backgroundStateListener.getInitialFadeBackgroundAnimation());
    }

    @Override
    public void onInitialAppBackgroundAnimationEnded() {
        splashBackground.setVisibility(View.GONE);
        Observable.timer(WAITING_DURATION, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(tick -> addBackgroundFrom(savedImageUrl));
    }

    @Override
    public void onBlurredAppBackgroundLoaded(BitmapDrawable drawable) {
        if (splashScreen != null && splashScreen.getVisibility() == View.VISIBLE)
            animateBlur(drawable);
        else
            EventBus.getDefault().post(new UpdateFreeRideListEvent(false));
        Observable.timer(BLUR_LOGO_ANIMATION_DURATION, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(tick -> getActivity().getWindow().getDecorView().setBackground(drawable));
    }

    private void animateBlur(BitmapDrawable image) {
        splashBackground.setVisibility(View.VISIBLE);
        if (splashScreen != null && splashScreen.isAttachedToWindow()) {
            BitmapDrawable drawable = new BitmapDrawable(getActivity().getResources(), getCorrectBitmap(image));
            drawable.setColorFilter(getActivity().getResources().getColor(R.color.color_CC050D24), PorterDuff.Mode.DARKEN);
            splashBackground.setBackground(drawable);
            splashBackground.startAnimation(backgroundStateListener.getShowBlurredBackgroundAnimation());
        }
    }

    private Bitmap getCorrectBitmap(BitmapDrawable image) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        //ready for drawing window size without a bottom navigation/key board
        Point workWindowSize = new Point();
        display.getSize(workWindowSize);
        try {
            Point appSize = new Point(splashBackground.getMeasuredWidth(), splashBackground.getMeasuredHeight());
            return Bitmap.createBitmap(image.getBitmap(), 0,
                    workWindowSize.y - appSize.y,
                    appSize.x,   //width available for the app
                    appSize.y); //height available for the app
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image.getBitmap();
    }

    @Override
    public void onBlurredAppBackgroundAnimationEnded() {
        loginSubBinder.checkUserLoggedIn(getActivity(), isBluetoothEnableActivityShown);
    }

    @Override
    public void onFailedBackgroundLoading() {
        loginSubBinder.checkUserLoggedIn(getActivity(), isBluetoothEnableActivityShown);
    }

    @Override
    public void onLoginStateVerified(Token token) {
        hideProgress();
        isAuthorizedMode = !TextUtils.isEmpty(token.getAuthToken()) && !token.getAuthToken().equals(LoginResponse.GUEST_TOKEN);
        splashScreenLogo.startAnimation(backgroundStateListener.getHideAppLogoAnimation());
    }

    @Override
    public void onLoginStateVerificationError(Throwable e) {
        hideProgress();
        showError(e);
    }

    @Override
    public void onHideAppLogoAnimationEnded() {
        splashScreen.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.VISIBLE);
        viewPagerBarLis.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.VISIBLE);
        if (isAuthorizedMode) {
            EventBus.getDefault().post(new UpdateMainActionBarEvent());
            EventBus.getDefault().post(new ActivateRootServicesEvent());
            EventBus.getDefault().post(new UpdateFreeRideListEvent(false));
            EventBus.getDefault().post(new CheckSecondLaunchEvent());
        }
        EventBus.getDefault().post(new ClickByBranchEvent());
        MainInitCheckStartTypeSubBinder.checkStartIntentType(getActivity());
    }

    private void showError(Throwable throwable) {
        hideProgress();
        if (throwable instanceof OutdatedAppVersionException)
            getActivity().startActivity(new Intent(getActivity(), TimeToUpdateActivity.class));
        else if (throwable instanceof NetworkSettingsException)
            EventBus.getDefault().postSticky(new InternetErrorConnectionEvent());
        else if (throwable instanceof InitialCheckAgeError)
            getActivity().startActivityForResult(new Intent(getActivity(), InitialCheckAgeActivity.class), InitialCheckAgeBinder.AGE_CHECKING);
        else if (throwable instanceof StartTourShowingError)
            getActivity().startActivity(new Intent(getActivity(), StartTourActivity.class));
        else if (throwable instanceof LocationSettingsException) {
            isGPSPermissionDialogShown = true;
            onStartEvent(new OnStartMainActivityEvent());
        } else if (throwable instanceof BluetoothStateException) {
            getActivity().startActivity(new Intent(getActivity(), EnableBluetoothActivity.class));
            isBluetoothEnableActivityShown = true;
        } else if (throwable instanceof BluetoothNotAvailableException) {
            isBluetoothEnableActivityShown = true;
            HashMap map = new HashMap();
            map.put(QorumNotifier.MESSAGE, getString(R.string.error_bluetooth_not_available));
            new BaseDialogBinder(getActivity()).onShowDialogEvent(new ShowNotificationDialogEvent(NotificationType.DEF, map), view -> onStartEvent(new OnStartMainActivityEvent()));
        } else if (throwable instanceof TooFarLocationException) {
            getActivity().startActivity(new Intent(getActivity(), LocationBlockerActivity.class));
        } else
            initExceptionHandler.showError(throwable, view -> onStartEvent(new OnStartMainActivityEvent()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGPSPermissionDialogHidden(GpsPermissionDialogHiddenEvent e) {
        isGPSPermissionDialogShown = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionChangedEvent(InternetConnectedEvent event) {
        initExceptionHandler.dismiss();
        onStartEvent(new OnStartMainActivityEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateBackground(UpdateBackgroundEvent event) {
        getActivity().loadCityImage();
    }

    public void onDestroy() {
        backgroundStateListener.onDestroy();
    }
}
