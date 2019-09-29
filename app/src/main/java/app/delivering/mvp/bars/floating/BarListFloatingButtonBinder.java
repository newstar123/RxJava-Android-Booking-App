package app.delivering.mvp.bars.floating;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.lists.floating.CustomFloatingButton;
import app.delivering.component.bar.map.BarMapFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.floating.events.ClickListMapButtonEvent;
import app.delivering.mvp.bars.floating.events.HideBarListFloatingEvent;
import app.delivering.mvp.bars.floating.events.ShowBarListFloatingEvent;
import app.delivering.mvp.bars.map.lifecycle.binder.BarListMapLifecycleBinder;
import app.delivering.mvp.bars.map.lifecycle.events.OnMapRevertAnimationEvent;
import app.delivering.mvp.main.init.events.UpdateMainActionBarEvent;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BarListFloatingButtonBinder extends BaseBinder {
    public static final String STATE_BAR_LIST = "open_list";
    public static final String STATE_BAR_MAP = "open_map";
    private final Animation fabOpen;
    private final Animation fabClose;
    private final Animation fabRotateStart;
    private final Animation fabRotateEnd;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.main_collapsing_toolbar) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.toolbar) Toolbar toolBar;
    @BindView(R.id.to_map_floating_button) CustomFloatingButton floatingButton;
    @BindView(R.id.bar_list_view_pager) ViewPager pager;
    @BindView(R.id.main_container) CoordinatorLayout container;
    @BindView(R.id.tab_layout) TabLayout tabLayout;

    public BarListFloatingButtonBinder(BaseActivity activity) {
        super(activity);
        fabOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.floating_button_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(), R.anim.floating_button_close);
        fabRotateStart = AnimationUtils.loadAnimation(getActivity(), R.anim.floating_button_rotate_start);
        fabRotateEnd = AnimationUtils.loadAnimation(getActivity(), R.anim.floating_button_rotate_end);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void autoClickButton(ClickListMapButtonEvent event) {
        if (floatingButton != null && floatingButton.getContentDescription().equals(STATE_BAR_MAP)) floatingButton.performClick();
    }

    @OnClick(R.id.to_map_floating_button) void floatingToMapClick() {
        floatingButton.setClickable(false);
        if (floatingButton != null && (TextUtils.isEmpty(floatingButton.getContentDescription()) ||
                floatingButton.getContentDescription().equals(STATE_BAR_LIST))) {
            changeStateTo(STATE_BAR_MAP);
            appBarLayout.setExpanded(true);
            changeAppBarScrolling(0);
            showMap();
            toolBar.bringToFront();
            tabLayout.setVisibility(View.GONE);
        } else {
            hideMap();
            changeAppBarScrolling(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
            tabLayout.setVisibility(View.VISIBLE);
            changeStateTo(STATE_BAR_LIST);
            EventBus.getDefault().post(new OnMapRevertAnimationEvent());
        }
        EventBus.getDefault().post(new UpdateMainActionBarEvent());
        restoreButtonState();
    }

    private void hideMap() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.flip_animation_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation) {
                Animation animationIn = AnimationUtils.loadAnimation(getActivity(), R.anim.flip_animation_in);
                pager.setVisibility(View.VISIBLE);
                pager.startAnimation(animationIn);
                appBarLayout.startAnimation(animationIn);
            }

            @Override public void onAnimationRepeat(Animation animation) {}
        });
        appBarLayout.startAnimation(animation);
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                }, e->{

                });
    }

    private void showMap() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.flip_animation_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
                Observable.timer(450, TimeUnit.MILLISECONDS)
                        .subscribe(aLong -> {
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.flip_animation_in, R.anim.flip_animation_out, R.anim.flip_animation_in, R.anim.flip_animation_out);
                            fragmentTransaction.replace(R.id.main_container, new BarMapFragment());
                            fragmentTransaction.addToBackStack(BarMapFragment.class.getSimpleName());
                            fragmentTransaction.commit();
                            Animation animationIn = AnimationUtils.loadAnimation(getActivity(), R.anim.flip_animation_in);
                            appBarLayout.startAnimation(animationIn);
                        }, e->{

                        });
            }

            @Override public void onAnimationEnd(Animation animation) {
                pager.setVisibility(View.GONE);
            }

            @Override public void onAnimationRepeat(Animation animation) { }
        });
        appBarLayout.startAnimation(animation);
        pager.startAnimation(animation);
    }

    private void restoreButtonState() {
        Observable.timer(BarListMapLifecycleBinder.MAP_ANIMATION_DURATION, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .doAfterTerminate(() -> floatingButton.setClickable(true))
                .subscribe(aLong -> {});
    }

    private void changeAppBarScrolling(int state) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbarLayout.getLayoutParams();
        params.setScrollFlags(state);
    }

    private void changeStateTo(String stateDescription) {
        if (stateDescription.equals(STATE_BAR_LIST))
            onAnimatedChangeButton(stateDescription, R.drawable.inset_map_white, R.string.word_map);
        else
            onAnimatedChangeButton(stateDescription, R.drawable.inset_list_white, R.string.word_list);
    }

    private void onAnimatedChangeButton(String stateDescription, int iconId, int titleId) {
        floatingButton.setEnabled(false);
        floatingButton.startAnimation(fabRotateStart);
        floatingButton.setContentDescription(stateDescription);
        Observable.just(true)
                .delay(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(isStartAnimationComplete -> {
                    floatingButton.setCompoundDrawablesWithIntrinsicBounds(null, getActivity().getDrawable(iconId), null, null);
                    floatingButton.setText(getString(titleId));
                    floatingButton.startAnimation(fabRotateEnd);
                    floatingButton.setEnabled(true);
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showButton(ShowBarListFloatingEvent event) {
        if (floatingButton != null && floatingButton.getVisibility() == View.GONE) {
            floatingButton.setVisibility(View.VISIBLE);
            floatingButton.startAnimation(fabOpen);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hideButton(HideBarListFloatingEvent event) {
        if (floatingButton != null && floatingButton.getVisibility() == View.VISIBLE) {
            floatingButton.startAnimation(fabClose);
            floatingButton.setVisibility(View.GONE);
        }
    }
}
