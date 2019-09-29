package app.delivering.mvp.profile.edit.actionbar.init;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class ProfileActionBarBinder extends BaseBinder{
    @BindView(R.id.profile_toolbar) Toolbar toolBar;

    public ProfileActionBarBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle(getActivity().getString(R.string.edit_account));
    }

}
