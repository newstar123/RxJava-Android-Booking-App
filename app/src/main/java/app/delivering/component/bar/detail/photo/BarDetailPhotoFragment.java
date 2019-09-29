package app.delivering.component.bar.detail.photo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.bars.detail.init.toolbar.controll.binder.BarDetailPhotoControlBinder;
import app.delivering.mvp.bars.detail.init.toolbar.photo.binder.BarDetailPhotoBinder;
import app.delivering.mvp.bars.detail.init.toolbar.video.binder.BarDetailVideoBinder;
import app.delivering.mvp.bars.detail.init.toolbar.video.model.OnInitToolBarPlayer;
import app.delivering.mvp.bars.detail.init.toolbar.video.model.OnReleaseToolBarPlayer;
import app.delivering.mvp.bars.detail.init.toolbar.video.events.OnStopPlayerEvent;

public class BarDetailPhotoFragment extends BaseFragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_detail_photo, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        BarDetailVideoBinder videoBinder = new BarDetailVideoBinder(this);
        addToEventBusAndViewInjection(videoBinder, view);
        BarDetailPhotoBinder detailPhotoBinder = new BarDetailPhotoBinder(this);
        addItemForViewsInjection(detailPhotoBinder, view);
        BarDetailPhotoControlBinder controllBinder = new BarDetailPhotoControlBinder(this);
        addToEventBusAndViewInjection(controllBinder, view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) EventBus.getDefault().post(new OnInitToolBarPlayer());}

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) EventBus.getDefault().post(new OnInitToolBarPlayer());}

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) EventBus.getDefault().post(new OnReleaseToolBarPlayer());}

    @Override public void onStop() {
        if (Util.SDK_INT > 23) EventBus.getDefault().post(new OnReleaseToolBarPlayer());
        EventBus.getDefault().post(new OnStopPlayerEvent());
        super.onStop();
    }
}
