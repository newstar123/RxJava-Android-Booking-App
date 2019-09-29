package app.delivering.mvp.bars.detail.init.toolbar.configuration.binder;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.adapter.pager.ViewPagerWithBlocking;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ScreenSizeInterface;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.bars.detail.init.toolbar.configuration.events.BarDetailConfigurationEvent;
import app.delivering.mvp.bars.detail.init.toolbar.configuration.events.OnConfigurationChangedEvent;
import app.delivering.mvp.bars.detail.init.toolbar.controll.events.OnChangePhotoViewerStateEvent;
import app.delivering.mvp.bars.detail.init.toolbar.controll.events.OpenPhotosGridEvent;
import app.delivering.mvp.bars.detail.init.toolbar.controll.events.RevertPagerStateEvent;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BarDetailConfigurationBinder extends BaseBinder implements ScreenSizeInterface {
    @BindView(R.id.bar_detail_photos_view_pager) ViewPagerWithBlocking barScreensViewPager;
    @BindView(R.id.bar_detail_expand_photos_action_bar) RelativeLayout photoActionBar;
    @BindView(R.id.bar_detail_scroll_view) NestedScrollView scrollView;
    @BindViews({R.id.bar_detail_photos_spring_indicator,
            R.id.bar_detail_name_container,
            R.id.bar_detail_action_button,
            R.id.bar_detail_action_bar}) List<View> views;
    private boolean isPortraitState;
    private boolean isExpandState;
    private BarDetailModel detailModel;

    public BarDetailConfigurationBinder(BaseActivity activity) {
        super(activity);
        isPortraitState = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        this.detailModel = detailModel;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changePhotoViewerState(OnChangePhotoViewerStateEvent event) {
        isExpandState = event.getExpand();
        changePager();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfigurationChanged(OnConfigurationChangedEvent event) {
        isPortraitState = event.getNewConfig().orientation == Configuration.ORIENTATION_PORTRAIT;
        changePager();
    }

    private void changePager() {
        changeViewsState();
        setPagerHeight();
    }

    private void changeViewsState() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (isPortraitState && isExpandState) {
            ButterKnife.apply(views, ViewActionSetter.GONE);
            photoActionBar.setVisibility(View.VISIBLE);
        } else if (!isPortraitState && isExpandState) {
            ButterKnife.apply(views, ViewActionSetter.GONE);
            photoActionBar.setVisibility(View.VISIBLE);
        } else {
            ButterKnife.apply(views, ViewActionSetter.VISIBLE);
            EventBus.getDefault().post(detailModel);
            photoActionBar.setVisibility(View.GONE);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        scrollView.setOnTouchListener((v, event) -> isExpandState);
        EventBus.getDefault().post(new BarDetailConfigurationEvent(isPortraitState, isExpandState));
    }

    private void setPagerHeight() {
        ViewGroup.LayoutParams layoutParams = barScreensViewPager.getLayoutParams();
        if (isPortraitState && isExpandState) {
            layoutParams.height = getFullDisplayHeight();
        }
        else
            layoutParams.height = getActivity().getResources().getDimensionPixelSize(R.dimen.dip345);

        barScreensViewPager.requestLayout();
    }

    private int getFullDisplayHeight() {
        Point size = this.getDisplaySize(getActivity());
        int statusBarHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.dip20);
        int height = size.y - statusBarHeight;
        return height;
    }

    @OnClick(R.id.bar_detail_expand_back) public void onBackForExpandState() {
        EventBus.getDefault().post(new RevertPagerStateEvent());
    }

    @OnClick(R.id.bar_detail_expand_grid) void onGridClick() {
        EventBus.getDefault().post(new OpenPhotosGridEvent());
        if (isExpandState && !isPortraitState) {
            Configuration newConfig = new Configuration();
            newConfig.orientation = Configuration.ORIENTATION_PORTRAIT;
            EventBus.getDefault().post(new OnConfigurationChangedEvent(newConfig));
            EventBus.getDefault().post(new OnChangePhotoViewerStateEvent(false));
        }
    }

    public boolean isExpandState(){
        return isExpandState;
    }

}