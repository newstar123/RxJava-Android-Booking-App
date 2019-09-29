package app.delivering.mvp.advert.init.binder;


import android.net.Uri;
import android.support.v7.widget.Toolbar;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.advert.entity.AdvertResponse;
import app.core.advert.interactor.GetAdvertInteractor;
import app.core.uber.start.entity.RideDirection;
import app.delivering.component.BaseActivity;
import app.delivering.component.advert.VideoAdvertActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.advert.init.events.AdvertVideoOnStartEvent;
import app.delivering.mvp.advert.progress.events.ProgressVideoAdvertEvent;
import app.delivering.mvp.advert.ride.start.binder.subbinder.PrepareRideDirection;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class InitVideoAdvertBinder extends BaseBinder {
    private final InitExceptionHandler initExceptionHandler;
    @BindView(R.id.video_advert_player) SimpleExoPlayerView videoAdvertPlayer;
    @BindView(R.id.video_advert_player_progress) MaterialProgressBar videoProgress;
    @BindView(R.id.advert_video_toolbar) Toolbar toolBar;
    private final GetAdvertInteractor initVideoAdvertInteractor;
    private ReplaySubject<AdvertResponse> replaySubject;
    private Subscription subscription;

    public InitVideoAdvertBinder(BaseActivity activity) {
        super(activity);
        initVideoAdvertInteractor = new GetAdvertInteractor(activity);
        replaySubject = ReplaySubject.create();
        initExceptionHandler = new InitExceptionHandler(activity);
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        loadAdvertUrl();
        initToolbar();
    }

    private void initToolbar() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().getSupportActionBar().setHomeButtonEnabled(false);
        getActivity().getSupportActionBar().setTitle(R.string.requesting);
    }

    private void loadAdvertUrl() {
        subscription = initVideoAdvertInteractor.process()
                .subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(AdvertVideoOnStartEvent event) {
        showProgress();
        if (subscription != null && !subscription.isUnsubscribed())
            showProgress();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(AdvertResponse advertResponse) {
        String videoUrl = getVideoUrl(advertResponse);
        playVideo(videoUrl);
        replaySubject = ReplaySubject.create();
    }

    private String getVideoUrl(AdvertResponse advertResponse) {
        double discount = getActivity().getIntent().getDoubleExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, 0);
        if (PrepareRideDirection.get(getActivity(), VideoAdvertActivity.RIDE_DIRECTION_KEY) == RideDirection.FROM_BAR && discount <= 0)
            return advertResponse.getData().getAttributes().getVideoUrl2();
        else if (PrepareRideDirection.get(getActivity(), VideoAdvertActivity.RIDE_DIRECTION_KEY) == RideDirection.FROM_BAR && discount > 0)
            return advertResponse.getData().getAttributes().getVideoUrl3();
        else
            return advertResponse.getData().getAttributes().getVideoUrl();
    }

    private void playVideo(String url) {
        SimpleExoPlayer player = createPlayer(url);
        player.setPlayWhenReady(true);
        videoAdvertPlayer.setPlayer(player);
        videoAdvertPlayer.setTag(url);
        videoAdvertPlayer.requestFocus();
        videoAdvertPlayer.setUseController(false);
        EventBus.getDefault().post(new ProgressVideoAdvertEvent());
    }

    private SimpleExoPlayer createPlayer(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), "Qorum"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        Uri video = Uri.parse(url);
        MediaSource videoSource = new ExtractorMediaSource(video,
                dataSourceFactory, extractorsFactory, null, null);
        player.prepare(videoSource);
        return player;
    }

    private void showError(Throwable throwable) {
        hideProgress();
        initExceptionHandler.showError(throwable, view -> repeat());
    }

    private void repeat() {
        replaySubject = ReplaySubject.create();
        onStartEvent(new AdvertVideoOnStartEvent());
        loadAdvertUrl();
    }

}
