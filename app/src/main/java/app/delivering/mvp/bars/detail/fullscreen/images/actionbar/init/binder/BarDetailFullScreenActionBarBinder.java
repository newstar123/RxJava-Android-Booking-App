package app.delivering.mvp.bars.detail.fullscreen.images.actionbar.init.binder;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class BarDetailFullScreenActionBarBinder extends BaseBinder {
    @BindView(R.id.about_bar_view_toolbar) Toolbar toolBar;

    public BarDetailFullScreenActionBarBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setHomeAsUpIndicator(R.drawable.inset_x_white);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle("");
    }
}
