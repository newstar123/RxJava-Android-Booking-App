package app.delivering.mvp.bars.detail.fullscreen.images.video.init.presenter;

import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.about.video.EventLogger;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.events.OnInitializePlayerEvent;
import rx.Observable;

public class AboutBarVideoInitPresenter extends BasePresenter<OnInitializePlayerEvent, Observable<SimpleExoPlayer>> {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DefaultTrackSelector trackSelector;
    private Handler mainHandler;
    private EventLogger eventLogger;
    private DataSource.Factory mediaDataSourceFactory;

    public AboutBarVideoInitPresenter(BaseActivity activity) {
        super(activity);
        mediaDataSourceFactory = buildDataSourceFactory();
        mainHandler = new Handler();
    }

    @Override public Observable<SimpleExoPlayer> process(OnInitializePlayerEvent event) {
        return Observable.create(subscriber -> {
            SimpleExoPlayer player = event.getPlayer();
            if (player == null) {
                TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
                trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
                eventLogger = new EventLogger(trackSelector);
                player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, new DefaultLoadControl(new DefaultAllocator(true, 10000), 5000, 10000, 10000, 10000));
                player.addListener(eventLogger);
                player.setAudioDebugListener(eventLogger);
                player.setVideoDebugListener(eventLogger);
                player.setMetadataOutput(eventLogger);
            }
            boolean haveResumePosition = event.getWindow() != C.INDEX_UNSET;
            if (haveResumePosition) player.seekTo(event.getWindow(), event.getPosition());
            try {
                Uri uri = Uri.parse(event.getVideoUrl());
                player.prepare(buildMediaSource(uri, "mpb"), !haveResumePosition, false);
                subscriber.onNext(player);
                subscriber.onCompleted();
            } catch (Exception e){
                subscriber.onError(e);
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri.toString()) : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(), new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(), new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(), mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory() {
        return new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "Qorum"), BANDWIDTH_METER);
    }

    public void release() {
        trackSelector = null;
        eventLogger = null;
    }
}
