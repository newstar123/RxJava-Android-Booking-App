package app.delivering.mvp.bars.detail.init.menu.root.actionbar.binder;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.bar.detail.menu.BarDetailRootMenuActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class BarMenuActionBarBinder extends BaseBinder {
    @BindView(R.id.bar_root_menu_toolbar) Toolbar toolBar;

    public BarMenuActionBarBinder(BarDetailRootMenuActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle(getString(R.string.menu_title));
    }

}
