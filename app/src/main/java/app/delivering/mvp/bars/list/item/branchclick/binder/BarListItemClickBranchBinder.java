package app.delivering.mvp.bars.list.item.branchclick.binder;

import android.content.Intent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.item.branchclick.events.ClickByBranchEvent;
import app.delivering.mvp.bars.list.item.branchclick.presenter.BarListItemClickBranchPresenter;
import rx.android.schedulers.AndroidSchedulers;

public class BarListItemClickBranchBinder extends BaseBinder {
    private final BarListItemClickBranchPresenter barListItemClickBranchPresenter;

    public BarListItemClickBranchBinder(BaseActivity activity) {
        super(activity);
        barListItemClickBranchPresenter = new BarListItemClickBranchPresenter(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadList(ClickByBranchEvent event) {
        barListItemClickBranchPresenter.process()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setUpBranchClickEventParams, this::onError, () -> {});
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void setUpBranchClickEventParams(Intent intent) {
        getActivity().startActivity(intent);
    }
}
