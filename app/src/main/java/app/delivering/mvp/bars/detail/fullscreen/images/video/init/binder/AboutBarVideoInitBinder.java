package app.delivering.mvp.bars.detail.fullscreen.images.video.init.binder;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.about.video.AboutBarVideoActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.events.OnInitializePlayerEvent;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.events.OnReleasePlayerEvent;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.presenter.AboutBarVideoInitPresenter;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class AboutBarVideoInitBinder extends BaseBinder implements ExoPlayer.EventListener {
    @BindView(R.id.about_bar_video_player) SimpleExoPlayerView playerView;
    @BindView(R.id.about_bar_video_progress) MaterialProgressBar progress;
    @BindView(R.id.about_bar_video_poster) ImageView poster;
    @BindView(R.id.about_bar_play_video) ImageView playIcon;
    private SimpleExoPlayer player;
    private int resumeWindow;
    private long resumePosition;
    private AboutBarVideoInitPresenter presenter;

    public AboutBarVideoInitBinder(BaseActivity activity) {
        super(activity);
        resumeWindow = getActivity().getIntent().getIntExtra(AboutBarVideoActivity.START_PLAYER_WINDOW, 0);
        resumePosition = getActivity().getIntent().getLongExtra(AboutBarVideoActivity.START_PLAYER_POSITION, 0);
        presenter = new AboutBarVideoInitPresenter(getActivity());
    }

    @Override public void afterViewsBounded() {
        progress.setVisibility(View.VISIBLE);
        playerView.requestFocus();
        loadPoster();
    }

    private void loadPoster() {
        Picasso.with(getActivity())
                .load(getActivity().getIntent().getStringExtra(AboutBarVideoActivity.PLAYER_VIDEO_POSTER))
                .into(poster, new Callback() {
                    @Override public void onSuccess() {
                        setVisibility(progress, View.GONE);
                    }

                    @Override public void onError() {
                        setVisibility(progress, View.GONE);
                    }
                });
    }

    @OnClick(R.id.about_bar_play_video) void play(){
        setVisibility(playIcon, View.GONE);
        setVisibility(progress, View.GONE);
        player.setPlayWhenReady(true);

    }

    @OnClick(R.id.about_bar_video_container) void pause(){
        pausePlayer();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initializePlayer(OnInitializePlayerEvent event) {
        setVisibility(progress, View.VISIBLE);
        event.setPlayer(player);
        event.setWindow(resumeWindow);
        event.setPosition(resumePosition);
        event.setVideoUrl(getActivity().getIntent().getStringExtra(AboutBarVideoActivity.ABOUT_BAR_VIDEO_URL));
        presenter.process(event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::play, this::showError, ()->{});
    }

    private void play(SimpleExoPlayer simpleExoPlayer) {
        this.player = simpleExoPlayer;
        player.addListener(this);
        if (playerView.getPlayer() == null)
            playerView.setPlayer(player);
        playerView.setResizeMode(0);
        playerView.setUseController(false);
        playIcon.performClick();
    }

    private void showError(Throwable e) {
        Toast.makeText(getActivity(), getString(R.string.broken_video), Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void releasePlayer(OnReleasePlayerEvent event) {
        if (player != null) {
            updateResumePosition();
            player.release();
            player = null;
            presenter.release();
        }
    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition()) : C.TIME_UNSET;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_READY:
                setVisibility(progress, View.GONE);
                break;
            case ExoPlayer.STATE_ENDED:
                restorePlayerState();
                if (player != null) {
                    player.setPlayWhenReady(false);
                    player.seekTo(0);
                }
                break;
        }
    }

    private void restorePlayerState() {
        pausePlayer();
        setVisibility(playerView, View.GONE);
        if (player != null)
            player.seekTo(0);
    }

    private void pausePlayer() {
        if (player != null)
            player.setPlayWhenReady(false);
        setVisibility(playIcon, View.VISIBLE);
        setVisibility(progress, View.GONE);
    }

    private void setVisibility(View view, int visibility) {
        if (view != null)
            view.setVisibility(visibility);
    }

    @Override public void onLoadingChanged(boolean isLoading) {}

    @Override public void onPositionDiscontinuity() {}

    @Override public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

    @Override public void onPlayerError(ExoPlaybackException e) {}

}
