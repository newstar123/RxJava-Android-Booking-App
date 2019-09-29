package app.delivering.mvp.verify.binder.actionbar;

import android.support.v7.widget.Toolbar;
import android.view.View;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class VerifyCodeActionBarBinder extends BaseBinder {
    @BindView(R.id.verification_code_toolbar) Toolbar toolBar;

    public VerifyCodeActionBarBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle("");
        setUpOnClickListener();
    }

    private void setUpOnClickListener() {
        toolBar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

}
