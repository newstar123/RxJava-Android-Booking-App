package app.delivering.mvp.bars.market.actionbar.binder;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.Toolbar;

import com.trello.rxlifecycle.android.ActivityEvent;

import app.R;
import app.core.login.check.entity.GuestUserException;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.market.actionbar.presenter.GetAccountMarkPresenter;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class SearchMarketActionBarBinder extends BaseBinder {
    @BindView(R.id.toolbar) Toolbar toolBar;
    private final GetAccountMarkPresenter presenter;

    public SearchMarketActionBarBinder(BaseActivity activity) {
        super(activity);
        presenter = new GetAccountMarkPresenter(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setTitle("");
        getActivity().getSupportActionBar().setHomeAsUpIndicator(R.drawable.inset_account_white);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(android.R.color.transparent)));
        checkAccountIconState();
    }

    private void checkAccountIconState() {
        presenter.process()
                .compose(getActivity().bindUntilEvent(ActivityEvent.PAUSE))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isVerified -> toolBar.setNavigationIcon(isVerified ? R.drawable.inset_account_white
                        : R.drawable.layer_account_attention), e -> {
                    if (e instanceof GuestUserException)
                        toolBar.setNavigationIcon(R.drawable.layer_account_attention);
                }, ()->{});
    }

}
