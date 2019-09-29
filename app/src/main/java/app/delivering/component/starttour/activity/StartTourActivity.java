package app.delivering.component.starttour.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.starttour.init.binder.StartTourPagerBinder;

public class StartTourActivity extends BaseActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tour);
        getWindow().setBackgroundDrawable(getDrawable(R.drawable.gray_window_background));
        initUseCase();
    }

    private void initUseCase() {
        StartTourPagerBinder pagerBinder = new StartTourPagerBinder(this);
        addToEventBusAndViewInjection(pagerBinder);
    }

    @Override public void onBackPressed() {
    }
}
