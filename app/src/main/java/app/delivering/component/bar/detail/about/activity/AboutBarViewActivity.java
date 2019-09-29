package app.delivering.component.bar.detail.about.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.bars.detail.fullscreen.images.actionbar.init.binder.BarDetailFullScreenActionBarBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.grid.binder.AboutBarGridViewImageBinder;
import app.delivering.mvp.bars.detail.fullscreen.images.video.start.binder.AboutBarClickControlsBinder;

public class AboutBarViewActivity extends BaseActivity {
    public static final String ABOUT_BAR_URLS_MODEL = "about_bar_urls_model";
    public static final String ABOUT_BAR_URLS_LIST = "about_bar_urls_list";
    public static final String BAR_VIEW_PAGER_POSITION = "bar_image_view_pager_position";

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_bar_detail_views);
        init();
    }

    private void init() {
        BarDetailFullScreenActionBarBinder actionBarBinder = new BarDetailFullScreenActionBarBinder(this);
        addItemForViewsInjection(actionBarBinder);
        AboutBarClickControlsBinder startVideoBinder = new AboutBarClickControlsBinder(this);
        addToEventBus(startVideoBinder);
        AboutBarGridViewImageBinder gridViewImageBinder = new AboutBarGridViewImageBinder(this);
        addToEventBusAndViewInjection(gridViewImageBinder);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_carousel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_bar_detail_photos_view_style:
                onBackPressed();
                break;
            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void finish() {
        super.finish();
        overridePendingTransition(R.anim.animation_stay, R.anim.animation_slide_to_down);
    }
}
