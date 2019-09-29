package app.delivering.component.bar.detail.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.transition.Visibility;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.bars.detail.checkin.open.model.ActiveCheckInVendorId;
import app.delivering.mvp.tab.init.model.InitialTabActivityModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CheckInSplashScreenActivity extends BaseActivity {
    public static final String CHECK_IN_RESULT_KEY = "qorum.CHECK_IN_RESULT_KEY";
    private boolean isClosingByUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Fade fade = new Fade();
        fade.setMode(Visibility.MODE_IN);
        fade.setDuration(500);
        getWindow().setEnterTransition(fade);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_splash_screen);
        findViewById(R.id.splash_back_button).setOnClickListener(v -> {
            isClosingByUser = true;
            onResult();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(time -> {
                    if (!isClosingByUser) onResult();
                }, e -> { if (!isClosingByUser) onResult(); }, () -> {});
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isClosingByUser = true;
        onResult();
    }

    private void onResult() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            InitialTabActivityModel model = bundle.getParcelable(CHECK_IN_RESULT_KEY);
            TabActivity.launch(this, model);
            if (model != null) {
                startForeground(model.getCheckInId());
                EventBus.getDefault().post(new ActiveCheckInVendorId(model.getBarId()));
            }
        }
        finish();
    }

    private void startForeground(long checkInID) {
        Intent startIntent = new Intent(this, TabStatusForegroundService.class);
        startIntent.setAction(TabStatusForegroundService.START_FOREGROUND_ACTION);
        startIntent.putExtra(TabActivity.TAB_CHECK_IN_ID, checkInID);
        startService(startIntent);
    }
}
