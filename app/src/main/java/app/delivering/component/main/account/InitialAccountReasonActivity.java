package app.delivering.component.main.account;

import android.os.Bundle;
import android.support.annotation.Nullable;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.main.account.InitialAccountReasonBinder;

public class InitialAccountReasonActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_account_reason);
        initUseCases();
    }

    private void initUseCases() {
        InitialAccountReasonBinder binder = new InitialAccountReasonBinder(this);
        addItemForViewsInjection(binder);
    }

    @Override
    public void onBackPressed() {
    }

}
