package app.core.bars.list;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.bars.list.clear.ClearCacheBarListInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.core.bars.list.get.interactor.GetBarListInteractor;
import app.delivering.component.BaseActivity;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RefreshBarListInteractor implements BaseOutputInteractor<Observable<List<BarModel>>> {
    private ClearCacheBarListInteractor clearCacheBarListInteractor;
    private GetBarListInteractor getBarListInteractor;

    public RefreshBarListInteractor(BaseActivity activity) {
        getBarListInteractor = new GetBarListInteractor(activity);
        clearCacheBarListInteractor = new ClearCacheBarListInteractor();
    }

    @Override public Observable<List<BarModel>> process() {
        return clearCacheBarListInteractor.process()
                .observeOn(Schedulers.io())
                .concatMap(emptyResponse -> getBarListInteractor.process());
    }
}
