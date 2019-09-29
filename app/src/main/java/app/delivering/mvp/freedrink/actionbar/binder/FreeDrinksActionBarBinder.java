package app.delivering.mvp.freedrink.actionbar.binder;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class FreeDrinksActionBarBinder extends BaseBinder{
    @BindView(R.id.free_drink_toolbar) Toolbar toolBar;

    public FreeDrinksActionBarBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle("");
    }

}
