package app.delivering.mvp.bars.detail.fullscreen.map.actionbar.binder;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class BarDetailFullScreenMapActionBarBinder extends BaseBinder {
    @BindView(R.id.full_screen_map_toolbar) Toolbar toolBar;

    public BarDetailFullScreenMapActionBarBinder(BaseFragment fragment) {
        super(fragment.getBaseActivity());
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setHomeAsUpIndicator(R.drawable.inset_arrow_back_black);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle("");
    }
}
