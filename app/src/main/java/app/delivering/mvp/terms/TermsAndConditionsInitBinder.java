package app.delivering.mvp.terms;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class TermsAndConditionsInitBinder extends BaseBinder {
    @BindView(R.id.terms_and_conditions_toolbar) Toolbar toolBar;

    public TermsAndConditionsInitBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle(getString(R.string.terms_and_conditions));
    }
}
