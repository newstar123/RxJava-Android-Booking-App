package app.delivering.component;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ScreenSizeInterface;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BaseFragment extends RxFragment implements ScreenSizeInterface {
    protected Unbinder bind;
    private List<BaseBinder> eventBusItems;
    private List<Unbinder> unbindersForViewInjections;
    private View containerView;
    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (containerView != null && isVisible()) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                drawable.setColorFilter(getResources().getColor(R.color.color_CC050D24), PorterDuff.Mode.DARKEN);
                containerView.setBackground(drawable);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        eventBusItems = new ArrayList<>();
        unbindersForViewInjections = new ArrayList<>();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onStart() {
        super.onStart();
        Observable.from(eventBusItems).subscribe(binder -> EventBus.getDefault().register(binder));
    }

    public void loadCityImage(View view) {
        this.containerView = view;
        Point size = this.getDisplaySize(getActivity());
        int width = size.x;
        int height = size.y;
        Observable.just((String)QorumSharedCache.checkLocationImage().get(BaseCacheType.STRING))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Picasso.with(getActivity().getBaseContext()).load(s)
                        .centerCrop()
                        .resize(width, height)
                        .transform(new BlurTransformation(getActivity().getBaseContext(), 15))
                        .into(target), e->{}, ()->{});
    }

    @Override
    public void onStop() {
        super.onStop();
        Observable.from(eventBusItems).subscribe(binder -> EventBus.getDefault().unregister(binder));
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (bind != null)
            bind.unbind();
        unbindersForViewInjections.clear();
    }

    @Override public void onDestroy() {
        Observable.from(unbindersForViewInjections).subscribe(Unbinder::unbind);
        super.onDestroy();
    }

    protected void addToEventBus(BaseBinder binder) {
        eventBusItems.add(binder);
    }

    protected void addItemForViewsInjection(BaseBinder binder, View view){
        Unbinder unbinder = ButterKnife.bind(binder, view);
        binder.afterViewsBounded();
        unbindersForViewInjections.add(unbinder);
    }

    protected void addToEventBusAndViewInjection(BaseBinder binder, View view){
        addItemForViewsInjection(binder, view);
        addToEventBus(binder);
    }
}
