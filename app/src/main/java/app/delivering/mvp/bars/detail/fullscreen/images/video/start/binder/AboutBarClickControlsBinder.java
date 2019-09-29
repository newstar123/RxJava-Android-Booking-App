package app.delivering.mvp.bars.detail.fullscreen.images.video.start.binder;

import android.content.Intent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.about.video.AboutBarVideoActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.video.start.events.AboutBarClickVideoEvent;

public class AboutBarClickControlsBinder extends BaseBinder {

    public AboutBarClickControlsBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartVideo(AboutBarClickVideoEvent event) {
        Intent intent = new Intent(getActivity(), AboutBarVideoActivity.class)
                .putExtra(AboutBarVideoActivity.ABOUT_BAR_VIDEO_URL, event.getVideoUrl())
                .putExtra(AboutBarVideoActivity.EXTENSION_EXTRA, "mpb")
                .putExtra(AboutBarVideoActivity.START_PLAYER_WINDOW, event.getWindow())
                .putExtra(AboutBarVideoActivity.START_PLAYER_POSITION, event.getPosition())
                .putExtra(AboutBarVideoActivity.PLAYER_VIDEO_POSTER, event.getPoster())
                .setAction(AboutBarVideoActivity.ACTION_VIEW);
        getActivity().startActivity(intent);
    }
}
