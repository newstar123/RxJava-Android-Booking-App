package app.qamode.component;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.qamode.mvp.actionbar.QaActionBarBinder;
import app.qamode.mvp.exit.binder.ExitQaModeBinder;
import app.qamode.mvp.exit.event.OnExitQaModeEvent;

public class QaModeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa_mode);
        init();
    }

    private void init() {
        QaActionBarBinder actionBarBinder = new QaActionBarBinder(this);
        addItemForViewsInjection(actionBarBinder);
        ExitQaModeBinder exitQaModeBinder = new ExitQaModeBinder(this);
        addToEventBus(exitQaModeBinder);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_exit_qa_mode, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit_qa_mode:
                EventBus.getDefault().post(new OnExitQaModeEvent());
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
