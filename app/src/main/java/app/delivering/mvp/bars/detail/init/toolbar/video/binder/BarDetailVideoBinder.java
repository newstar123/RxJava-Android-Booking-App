package app.delivering.mvp.bars.detail.init.toolbar.video.binder;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.bar.detail.photo.BarDetailPhotoFragment;
import app.delivering.component.bar.detail.photo.adapter.BarsDetailPhotosAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.events.OnInitializePlayerEvent;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.presenter.AboutBarVideoInitPresenter;
import app.delivering.mvp.bars.detail.init.toolbar.configuration.events.BarDetailConfigurationEvent;
import app.delivering.mvp.bars.detail.init.toolbar.controll.events.OnChangePhotoViewerStateEvent;
import app.delivering.mvp.bars.detail.init.toolbar.init.model.AboutBarViewTypeModel;
import app.delivering.mvp.bars.detail.init.toolbar.video.events.OnStopPlayerEvent;
import app.delivering.mvp.bars.detail.init.toolbar.video.model.OnInitToolBarPlayer;
import app.delivering.mvp.bars.detail.init.toolbar.video.model.OnReleaseToolBarPlayer;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailVideoBinder extends BaseBinder implements ExoPlayer.EventListener {
    @BindViews({R.id.bar_detail_item_play_video, R.id.bar_detail_expand_video}) List<View> videoControls;
    @BindView(R.id.bar_detail_item_play_video) ImageView playIcon;
    @BindView(R.id.bar_detail_item_image_progress) MaterialProgressBar progressBar;
    @BindView(R.id.bar_detail_expand_video) ImageView expandButton;
    @BindView(R.id.bar_detail_item_video_player) SimpleExoPlayerView playerView;
    @BindView(R.id.bar_detail_video_time_line) TextView timeLineText;
    @BindView(R.id.promo_label) TextView promotion;
    @BindView(R.id.bar_detail_item_video_player_container) RelativeLayout playerContainer;
    private AboutBarVideoInitPresenter videoInitPresenter;
    private SimpleExoPlayer player;
    private ArrayList<AboutBarViewTypeModel> typeModels;
    private String videoUrl;
    private final String promotionValue;
    private int position;
    private int resumeWindow;
    private long resumePosition;
    private BarDetailConfigurationEvent configurationEvent;

    public BarDetailVideoBinder(BarDetailPhotoFragment barDetailPhotoFragment) {
        super(barDetailPhotoFragment.getBaseActivity());
        typeModels = new ArrayList<>();
        Bundle arguments = barDetailPhotoFragment.getArguments();
        typeModels = arguments.getParcelableArrayList(BarsDetailPhotosAdapter.BAR_DETAIL_PHOTO_MODEL);
        videoUrl = arguments.getString(BarsDetailPhotosAdapter.BAR_DETAIL_VIDEO_URL, "");
        position = arguments.getInt(BarsDetailPhotosAdapter.BAR_DETAIL_PHOTO_POSITION, 0);
        promotionValue = getPromotionValue(arguments);
        videoInitPresenter = new AboutBarVideoInitPresenter(getActivity());
    }

    private String getPromotionValue(Bundle arguments) {
        String value = arguments.getString(BarsDetailPhotosAdapter.BAR_DETAIL_PROMOTION, "");
        return TextUtils.isEmpty(value) ? value : value.substring(0,1).toUpperCase() + value.substring(1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pause(OnChangePhotoViewerStateEvent event) {
        if (expandButton != null) {
            if (event.getExpand())
                expandButton.setBackground(getActivity().getDrawable(R.drawable.inset_collapse));
            else
                expandButton.setBackground(getActivity().getDrawable(R.drawable.inset_expand));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initPlayer(OnInitToolBarPlayer initToolBarPlayer) {
        if (isVideo()) {
            ButterKnife.apply(videoControls, ViewActionSetter.VISIBLE);
            OnInitializePlayerEvent event = new OnInitializePlayerEvent();
            event.setPlayer(player);
            event.setWindow(resumeWindow);
            event.setPosition(resumePosition);
            event.setVideoUrl(videoUrl);
            videoInitPresenter.process(event)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setUpPlayer, this::showError, () -> {});
        }
        promotion.setVisibility(isPromotionAvailable() ? View.VISIBLE : View.GONE);
        promotion.setText(promotionValue);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void configurationChanged(BarDetailConfigurationEvent configurationEvent) {
        this.configurationEvent = configurationEvent;
        promotion.setVisibility((isPromotionAvailable()
                && configurationEvent.isPortraitState()
                && !configurationEvent.isExpandState()) ? View.VISIBLE : View.GONE);
        promotion.setText(promotionValue);
    }

    private boolean isPromotionAvailable() {
        return position == 0 && !TextUtils.isEmpty(promotionValue);
    }

    private void setUpPlayer(SimpleExoPlayer simpleExoPlayer) {
        this.player = simpleExoPlayer;
        playerView.setPlayer(player);
        playerView.requestFocus();
        playerView.setUseController(false);
        playerView.setResizeMode(0);
        player.addListener(this);
        player.setVolume(0);
        startPlayer();
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> updateTimeLine(player.getCurrentPosition()/1000), e -> {}, () -> {});
    }

    private void updateTimeLine(long time) {
        timeLineText.setText(String.format(getString(R.string.time_format), time/60, time%60));
    }

    private void showError(Throwable e) {
        Toast.makeText(getActivity(), getString(R.string.broken_video), Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pause(OnStopPlayerEvent event) {
        if (player != null && player.getPlayWhenReady() && isVideo())
            pausePlayer();
    }

    @OnClick(R.id.bar_detail_item_play_video) void onPlayVideo() {
        if (isVideo() && player != null) {
            startPlayer();
            player.setVolume(1);
        }
    }

    private void startPlayer() {
        setVisibility(progressBar, View.VISIBLE);
        setVisibility(playerContainer, View.VISIBLE);
        setVisibility(timeLineText, View.VISIBLE);
        setVisibility(playIcon, View.GONE);
        player.setPlayWhenReady(true);
    }

    @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (isVideo())
            switch (playbackState) {
                case ExoPlayer.STATE_READY:
                    setVisibility(progressBar, View.GONE);
                    setVisibility(promotion, View.GONE);
                    break;
                case ExoPlayer.STATE_ENDED:
                    restorePlayerState();
                    promotion.setVisibility(isPromotionAvailable() ? View.VISIBLE : View.GONE);
                    break;
            }
    }

    private void restorePlayerState() {
        pausePlayer();
        setVisibility(playerContainer, View.GONE);
        if (player != null)
            player.seekTo(0);
    }

    private void pausePlayer() {
        if (player != null && isVideo())
            player.setPlayWhenReady(false);
        setVisibility(playIcon, View.VISIBLE);
        setVisibility(progressBar, View.GONE);
        setVisibility(timeLineText, View.GONE);
        promotion.setVisibility(isPromotionAvailable() ? View.VISIBLE : View.GONE);
    }

    private void setVisibility(View view, int visibility) {
        if (view != null)
            view.setVisibility(visibility);
    }

    private boolean isVideo() {
        return typeModels.get(position).isVideoUrl();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void releasePlayer(OnReleaseToolBarPlayer event) {
        if (player != null && isVideo())
            updateResumePosition();
    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition()) : C.TIME_UNSET;
    }

    @Override public void onPlayerError(ExoPlaybackException error) {}

    @Override public void onPositionDiscontinuity() {}

    @Override public void onTimelineChanged(Timeline timeline, Object manifest) {}

    @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

    @Override public void onLoadingChanged(boolean isLoading) {}
}
