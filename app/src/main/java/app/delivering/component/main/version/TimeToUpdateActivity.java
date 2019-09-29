package app.delivering.component.main.version;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

import app.CustomApplication;
import app.R;
import app.delivering.component.BaseActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class TimeToUpdateActivity extends BaseActivity {
    private static final String APP_DETAIL_MARKET_URL = "market://details?id=%s";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_to_update_lock);
        Button updateButton = findViewById(R.id.update_app_button);
        updateButton.setOnClickListener(v -> {
                    updateButton.setEnabled(false);
                    Observable.timer(700, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(i->updateButton.setEnabled(true));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getCorrectAppDetailUrl())));
                });
    }

    private String getCorrectAppDetailUrl() {
        return String.format(APP_DETAIL_MARKET_URL, CustomApplication.get().getPackageName());
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
