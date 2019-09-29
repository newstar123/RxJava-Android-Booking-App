package app.delivering.component.bar.detail.menu;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.bars.detail.init.menu.root.actionbar.binder.BarMenuActionBarBinder;
import app.delivering.mvp.bars.detail.init.menu.root.click.RootBarMenuClickBinder;
import app.delivering.mvp.bars.detail.init.menu.root.init.binder.RootBarMenuBinder;

public class BarDetailRootMenuActivity extends BaseActivity {
    public static final String BAR_ID_FOR_MENU = "BAR_ID_FOR_MENU";
    public static final String BAR_NAME_FOR_MENU = "BAR_NAME_FOR_MENU";

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_bar_menu);
        initViews();
    }

    private void initViews() {
        BarMenuActionBarBinder actionBarBinder = new BarMenuActionBarBinder(this);
        addItemForViewsInjection(actionBarBinder);
        RootBarMenuBinder menuBinder = new RootBarMenuBinder(this);
        addItemForViewsInjection(menuBinder);
        RootBarMenuClickBinder clickBinder = new RootBarMenuClickBinder(this);
        addToEventBus(clickBinder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    onBackPressed();
                else
                    checkFinish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}
