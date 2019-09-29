package app.delivering.component.bar.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.bars.market.actionbar.binder.SearchMarketActionBarBinder;
import app.delivering.mvp.bars.market.init.binder.SearchMarketBinder;
import app.delivering.mvp.profile.drawer.init.events.OpenNavigationDrawerEvent;

public class SearchMarketActivity extends BaseActivity {

    public static final String CURRENT_MARKET_NAME = "SearchMarketActivity.CURRENT_MARKET_NAME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_market);
        initViews();
    }

    private void initViews() {
        SearchMarketActionBarBinder actionBarBinder = new SearchMarketActionBarBinder(this);
        addItemForViewsInjection(actionBarBinder);
        String currentMarketName = getIntent().getStringExtra(CURRENT_MARKET_NAME);
        SearchMarketBinder searchMarketBinder = new SearchMarketBinder(this, currentMarketName);
        addItemForViewsInjection(searchMarketBinder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EventBus.getDefault().postSticky(new OpenNavigationDrawerEvent());
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.animation_stay, R.anim.animation_fade);
    }
}
