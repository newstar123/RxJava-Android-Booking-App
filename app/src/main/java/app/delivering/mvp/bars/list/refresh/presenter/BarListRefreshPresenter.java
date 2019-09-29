package app.delivering.mvp.bars.list.refresh.presenter;

import java.util.List;

import app.core.bars.list.RefreshBarListInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BarListRefreshPresenter extends BaseOutputPresenter<Observable<List<BarModel>>> {
    private RefreshBarListInteractor refreshBarListInteractor;

    public BarListRefreshPresenter(BaseActivity activity) {
        super(activity);
        refreshBarListInteractor = new RefreshBarListInteractor(getActivity());
    }

    @Override public Observable<List<BarModel>> process() {
        return refreshBarListInteractor.process()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
