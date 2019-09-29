package app.delivering.component.tab.close;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.tab.close.advert.binder.ClosedTabAdvertBinder;
import app.delivering.mvp.tab.close.advert.events.OnStartClosedTabEvent;
import app.delivering.mvp.tab.close.click.CloseTabClickBinder;
import app.delivering.mvp.tab.close.init.binder.ClosedTabInitBinder;
import app.delivering.mvp.tab.close.init.model.FillCloseTabActivityModel;
import app.delivering.mvp.tab.close.ride.uber.TabClosedUberButtonBinder;

public class CloseTabActivity extends BaseActivity {
    private static final String CLOSE_TAB_MODEL = "app.delivering.component.tab.close.CLOSE_TAB_MODEL";

    public static Intent startCloseTabActivity(Context context, FillCloseTabActivityModel model) {
        Intent intent = new Intent(context, CloseTabActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CLOSE_TAB_MODEL, model);
        intent.putExtras(bundle);
        return intent;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_closed);
        initViews();
    }

    private void initViews() {
        ClosedTabInitBinder initBinder = new ClosedTabInitBinder(this);
        addItemForViewsInjection(initBinder);
        ClosedTabAdvertBinder advertBinder = new ClosedTabAdvertBinder(this);
        addItemForViewsInjection(advertBinder);
        TabClosedUberButtonBinder uberButtonBinder = new TabClosedUberButtonBinder(this);
        addItemForViewsInjection(uberButtonBinder);
        CloseTabClickBinder closeTabClickBinder = new CloseTabClickBinder(this);
        addItemForViewsInjection(closeTabClickBinder);
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault().post(new OnStartClosedTabEvent());
    }

    public FillCloseTabActivityModel getCloseTabActivityModel() {
        return getIntent().getExtras() == null ? new FillCloseTabActivityModel() : getIntent().getExtras().getParcelable(CLOSE_TAB_MODEL);
    }
}
