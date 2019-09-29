package app.delivering.mvp.advert.progress.binder;


import android.view.View;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.advert.init.events.AdvertVideoOnStartEvent;
import app.delivering.mvp.advert.progress.binder.scale.ScaleLimit;
import app.delivering.mvp.advert.progress.binder.scale.ScaleLimits;
import app.delivering.mvp.advert.progress.events.OnStartUberRideEvent;
import app.delivering.mvp.advert.progress.events.ProgressVideoAdvertEvent;
import app.delivering.mvp.advert.ride.start.events.ShowRideRequestResultEvent;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ProgressVideoAdvertBinder extends BaseBinder implements ExoPlayer.EventListener {
    @BindView(R.id.video_advert_player) SimpleExoPlayerView videoAdvertPlayer;
    @BindView(R.id.video_advert_player_progress) MaterialProgressBar videoProgress;
    private long lastPosition;
    private boolean isStarted;

    public ProgressVideoAdvertBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(ProgressVideoAdvertEvent event) {
        applyCallback();
    }

    private void applyCallback() {
        videoProgress.setVisibility(View.INVISIBLE);
        SimpleExoPlayer player = videoAdvertPlayer.getPlayer();
        if (player != null)
            player.addListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(AdvertVideoOnStartEvent event) {
        applyCallback();
    }


    @Override public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override public void onLoadingChanged(boolean isLoading) {

    }

    @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY) {
            hideProgress();
            SimpleExoPlayer player = videoAdvertPlayer.getPlayer();
            player.setPlayWhenReady(true);
            player.seekTo(lastPosition);
            videoProgress.setMax(100);
            Observable.interval(300, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(time -> updateProgress(player))
                    .doOnNext(time -> sendNotificationStartingRide(player))
                    .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(r -> {}, e -> showError(e));
        }
        if (playbackState == ExoPlayer.STATE_ENDED){
            EventBus.getDefault().post(new ShowRideRequestResultEvent());
        }
    }

    @Override public void onPlayerError(ExoPlaybackException error) {

    }

    @Override public void onPositionDiscontinuity() {

    }

    private void updateProgress(SimpleExoPlayer player) {
        if (player.getDuration() > 0)
            updatePlayProgress(player);
    }

    private void updatePlayProgress(SimpleExoPlayer player) {
        videoProgress.setVisibility(View.VISIBLE);
        long duration = player.getDuration();
        videoProgress.setMax((int) duration);
        lastPosition = player.getCurrentPosition();
        videoProgress.setProgress((int) lastPosition);
        ScaleLimit base = new ScaleLimit();
        base.setMin(0);
        base.setMax(duration);
        ScaleLimits scaleLimits = new ScaleLimits(ScaleLimits.SCALE_TO_0_100, base);
        double percentageInMillis = scaleLimits.scale(player.getBufferedPercentage());
        videoProgress.setSecondaryProgress((int) percentageInMillis);
    }

    private void sendNotificationStartingRide(SimpleExoPlayer player) {
        long duration = player.getDuration();
        long currentPosition = player.getCurrentPosition();
        if (duration - currentPosition < 4000 && !isStarted) {
            isStarted = true;
            EventBus.getDefault().post(new OnStartUberRideEvent());
        }
    }

    private void showError(Throwable e) {
        showDialogMessage(R.string.something_progress);
        e.printStackTrace();
    }
}
