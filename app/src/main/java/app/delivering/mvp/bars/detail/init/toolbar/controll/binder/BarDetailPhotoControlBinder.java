package app.delivering.mvp.bars.detail.init.toolbar.controll.binder;

import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.toolbar.configuration.events.OnConfigurationChangedEvent;
import app.delivering.mvp.bars.detail.init.toolbar.controll.events.OnChangePhotoViewerStateEvent;
import app.delivering.mvp.bars.detail.init.toolbar.controll.events.RevertPagerStateEvent;
import butterknife.BindView;
import butterknife.OnClick;

public class BarDetailPhotoControlBinder extends BaseBinder {
    @BindView(R.id.bar_detail_expand_video) ImageView icon;
    private boolean isExpand;

    public BarDetailPhotoControlBinder(BaseFragment fragment) {
        super(fragment.getBaseActivity());
    }

    @OnClick(R.id.bar_detail_expand_video) void onChangePhotoViewState() {
        isExpand = !isExpand;
        sendViewerStateEvent(isExpand);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revert(RevertPagerStateEvent event) {
      //onChangePhotoViewState();
        sendViewerStateEvent(false);
    }

    private void sendViewerStateEvent(boolean isExpand) {
        EventBus.getDefault().post(new OnChangePhotoViewerStateEvent(isExpand));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfigurationChanged(OnConfigurationChangedEvent event) {
        boolean isPortraitState = event.getNewConfig().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (isPortraitState)
            icon.setVisibility(View.VISIBLE);
        else
            icon.setVisibility(View.GONE);
    }

}
