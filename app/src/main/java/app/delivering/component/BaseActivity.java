package app.delivering.component;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.main.location.InitialLocationReasonActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ScreenSizeInterface;
import app.delivering.mvp.dialog.base.binder.BaseDialogBinder;
import app.delivering.mvp.main.init.events.GpsPermissionDialogHiddenEvent;
import app.delivering.mvp.network.binder.BlockByNetworkStateDialogBinder;
import app.gateway.location.settings.GoogleLocationSettingsGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.activation.QaModeActivationListener;
import app.qamode.activation.QaModeListenerInterface;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BaseActivity extends RxAppCompatActivity implements ScreenSizeInterface {
    private List<BaseBinder> eventBusItems;
    private List<Unbinder> unBindersForViewInjections;
    private boolean isStart;
    private ImageView imageView;
    private QaModeListenerInterface qaModeActivationListener;

    private Target target = new Target() {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            drawable.setColorFilter(getResources().getColor(R.color.color_CC050D24), PorterDuff.Mode.DARKEN);
            getWindow().setBackgroundDrawable(drawable);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            imageView.setTag(null);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            imageView.setTag(null);
        }
    };

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBusItems = new ArrayList<>();
        unBindersForViewInjections = new ArrayList<>();
        addToEventBus(new BlockByNetworkStateDialogBinder(this));
        addToEventBus(new BaseDialogBinder(this));

        imageView = new ImageView(getBaseContext());
        qaModeActivationListener = new QaModeActivationListener(this);
    }

    @Override protected void onStart() {
        super.onStart();
        loadCityImage();
        Observable.from(eventBusItems).subscribe(binder -> EventBus.getDefault().register(binder));
        isStart = true;
        qaModeActivationListener.onStart();
    }

    public void loadCityImage() {
        imageView.setTag(target);
        Point size = this.getDisplaySize(getBaseContext());
        int width = size.x;
        int height = size.y;
        Observable.just((String)QorumSharedCache.checkLocationImage().get(BaseCacheType.STRING))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.STOP))
                .subscribe(s -> Picasso.with(getBaseContext()).load(s)
                        .centerCrop()
                            .resize(width, height)
                            .transform(new BlurTransformation(getBaseContext(), 10))
                            .into(target), e->{}, ()->{});
    }

    public void start(Fragment fragment) {
        start(fragment, android.R.id.content);
    }

    public void start(Fragment fragment, int container) {
        getSupportFragmentManager().beginTransaction()
                .replace(container, fragment, ((Object) fragment).getClass().getSimpleName())
                .addToBackStack(((Object) fragment).getClass().getSimpleName())
                .commit();
    }

    public void add(Fragment fragment) {
        add(fragment, android.R.id.content);
    }

    public void add(Fragment fragment, int container) {
        getSupportFragmentManager().beginTransaction()
                .add(container, fragment, ((Object) fragment).getClass().getSimpleName())
                .addToBackStack(((Object) fragment).getClass().getSimpleName())
                .commit();
    }

    @Override protected void onStop() {
        super.onStop();
        isStart = false;
        Observable.from(eventBusItems).subscribe(binder -> EventBus.getDefault().unregister(binder));
        qaModeActivationListener.onStop();
    }

    protected void addToEventBusAndViewInjection(BaseBinder binder){
        addItemForViewsInjection(binder);
        addToEventBus(binder);
    }

    protected void addToEventBus(BaseBinder binder) {
        eventBusItems.add(binder);
    }

    protected void addItemForViewsInjection(BaseBinder binder){
        Unbinder unbinder = ButterKnife.bind(binder, this);
        binder.afterViewsBounded();
        unBindersForViewInjections.add(unbinder);
    }

    protected void addItemForViewsInjection(BaseActivity activity) {
        Unbinder unbinder = ButterKnife.bind(activity, this);
        unBindersForViewInjections.add(unbinder);
    }

    @Override protected void onDestroy() {
        Observable.from(unBindersForViewInjections).subscribe(Unbinder::unbind);
        super.onDestroy();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GoogleLocationSettingsGateway.CHECK_GPS_ACTIVE == requestCode) {
            EventBus.getDefault().post(new GpsPermissionDialogHiddenEvent());
            if (resultCode != RESULT_OK) {
                Intent intent = new Intent(this, InitialLocationReasonActivity.class);
                intent.putExtra(InitialLocationReasonActivity.IS_CHECK_GPS_RESULT_CANCELED, true);
                startActivity(intent);
            }
        }
    }

    public boolean isStart() {
        return isStart;
    }
}
