package app.delivering.component.locationblocker;

import android.os.Bundle;
import android.support.annotation.Nullable;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.locationblocker.init.binder.InitLocationBlockerBinder;

public class LocationBlockerActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_blocker);
        initUseCases();

    }

    private void initUseCases() {
        InitLocationBlockerBinder init = new InitLocationBlockerBinder(this);
        addItemForViewsInjection(init);
    }

    @Override
    public void onBackPressed() {

    }
}
