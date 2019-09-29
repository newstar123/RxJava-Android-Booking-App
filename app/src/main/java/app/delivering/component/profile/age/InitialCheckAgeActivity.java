package app.delivering.component.profile.age;

import android.os.Bundle;
import android.support.annotation.Nullable;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.profile.age.binder.InitialCheckAgeBinder;

public class InitialCheckAgeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_check_age);
        initUseCases();
    }

    private void initUseCases() {
        InitialCheckAgeBinder checkAgeBinder = new InitialCheckAgeBinder(this);
        addItemForViewsInjection(checkAgeBinder);
    }

    @Override
    public void onBackPressed() {
    }
}
