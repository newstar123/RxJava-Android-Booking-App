package app.delivering.mvp.advert.progress.binder.player;


import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import rx.Observable;
import rx.Subscriber;

public class ExoPlayerObservable {
    private SimpleExoPlayerView view;

    public ExoPlayerObservable(SimpleExoPlayerView view) {
        this.view = view;
    }

    public Observable<SimpleExoPlayer> get(){
        return Observable.create(new Observable.OnSubscribe<SimpleExoPlayer>() {
            @Override public void call(Subscriber<? super SimpleExoPlayer> subscriber) {
                SimpleExoPlayer player = view.getPlayer();
                if (player == null)
                    throw new RuntimeException();
                player.addListener(new ExoPlayer.EventListener() {
                    @Override public void onTimelineChanged(Timeline timeline, Object manifest) {

                    }

                    @Override public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playbackState == ExoPlayer.STATE_READY) {
                            subscriber.onNext(player);
                            subscriber.onCompleted();
                        }
                    }

                    @Override public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override public void onPositionDiscontinuity() {

                    }
                });
            }
        });
    }
}
