package app.delivering.component.bar.detail.about.video;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.binder.AboutBarVideoInitBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.events.OnInitializePlayerEvent;
import app.delivering.mvp.bars.detail.fullscreen.images.video.init.events.OnReleasePlayerEvent;

public class AboutBarVideoActivity extends BaseActivity {
    public static final String ABOUT_BAR_VIDEO_URL = "ABOUT_BAR_VIDEO_URL";
    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    public static final String START_PLAYER_WINDOW = "app.delivering.component.bar.detail.about.video.START_PLAYER_WINDOW";
    public static final String START_PLAYER_POSITION = "app.delivering.component.bar.detail.about.video.START_PLAYER_POSITION";
    public static final String PLAYER_VIDEO_POSTER = "app.delivering.component.bar.detail.about.video.PLAYER_VIDEO_POSTER";
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER)
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        setContentView(R.layout.activity_about_bar_detail_video);
        init();
    }

    private void init() {
        AboutBarVideoInitBinder videoInitBinder = new AboutBarVideoInitBinder(this);
        addToEventBusAndViewInjection(videoInitBinder);
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) EventBus.getDefault().post(new OnInitializePlayerEvent());}

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) EventBus.getDefault().post(new OnInitializePlayerEvent());}

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) EventBus.getDefault().post(new OnReleasePlayerEvent());}

    @Override
    public void onStop() {
        if (Util.SDK_INT > 23) EventBus.getDefault().post(new OnReleasePlayerEvent());
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            EventBus.getDefault().post(new OnInitializePlayerEvent());
        else
            finish();
    }

}
