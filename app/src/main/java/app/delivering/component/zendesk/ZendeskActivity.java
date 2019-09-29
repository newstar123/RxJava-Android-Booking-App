package app.delivering.component.zendesk;

import android.os.Bundle;
import android.support.annotation.Nullable;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.zendesk.binder.InitZendeskBinder;

public class ZendeskActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zendesk);
        initUseCases();
    }

    private void initUseCases() {
        InitZendeskBinder initZendeskBinder = new InitZendeskBinder(this);
        addToEventBusAndViewInjection(initZendeskBinder);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.animation_stay, R.anim.animation_right_to_left);
    }
}
