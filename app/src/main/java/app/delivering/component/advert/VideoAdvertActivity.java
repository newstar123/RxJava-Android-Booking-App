package app.delivering.component.advert;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.advert.init.binder.InitVideoAdvertBinder;
import app.delivering.mvp.advert.init.events.AdvertVideoOnStartEvent;
import app.delivering.mvp.advert.progress.binder.ProgressVideoAdvertBinder;
import app.delivering.mvp.advert.ride.delete.binder.ApplyPromoErrorBinder;
import app.delivering.mvp.advert.ride.start.binder.StartRideBinder;
import app.delivering.mvp.advert.ride.start.events.OnCancelOrderEvent;

public class VideoAdvertActivity extends BaseActivity{
    public final static String DEPARTURE_KEY = "DEPARTURE_KEY";
    public final static String DESTINATION_KEY = "DESTINATION_KEY";
    public final static String PRODUCT_ID_KEY = "PRODUCT_ID_KEY";
    public final static String FARE_ID_KEY = "FARE_ID_KEY";
    public static final String CAPACITY_KEY = "CAPACITY_KEY";
    public static final String DEPARTURE_ADDRESS_KEY = "DEPARTURE_ADDRESS_KEY";
    public static final String DESTINATION_ADDRESS_KEY = "DESTINATION_ADDRESS_KEY";
    public static final String RIDE_DIRECTION_KEY = "RIDE_DIRECTION_KEY";
    public static final String FARE_EXPIRED_AT_KEY = "FARE_EXPIRED_AT_KEY";

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_advert);
        initUseCases();
    }

    private void initUseCases() {
        InitVideoAdvertBinder initVideoAdvertBinder = new InitVideoAdvertBinder(this);
        addToEventBusAndViewInjection(initVideoAdvertBinder);
        ProgressVideoAdvertBinder progressVideoAdvertBinder = new ProgressVideoAdvertBinder(this);
        addToEventBusAndViewInjection(progressVideoAdvertBinder);
        StartRideBinder startRideBinder = new StartRideBinder(this);
        addToEventBusAndViewInjection(startRideBinder);
        ApplyPromoErrorBinder applyPromoErrorBinder = new ApplyPromoErrorBinder(this);
        addToEventBusAndViewInjection(applyPromoErrorBinder);
    }

    @Override public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault().post(new AdvertVideoOnStartEvent());
    }

    @Override protected void onStop() {
        pausePlayer();
        super.onStop();
    }

    private void pausePlayer() {
        SimpleExoPlayer player = ((SimpleExoPlayerView) findViewById(R.id.video_advert_player)).getPlayer();
        if(player != null)
            player.setPlayWhenReady(false);
    }

    @Override protected void onDestroy() {
        SimpleExoPlayer player = ((SimpleExoPlayerView) findViewById(R.id.video_advert_player)).getPlayer();
        if(player != null)
            player.release();
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_advert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cancel:
                EventBus.getDefault().post(new OnCancelOrderEvent());
                setResult(Activity.RESULT_OK);
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
