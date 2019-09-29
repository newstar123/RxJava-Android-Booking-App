package app.delivering.mvp.bars.detail.init.menu.root.init.presenter;

import app.core.bars.menu.entity.BarMenuRestModel;
import app.core.bars.menu.interactor.GetBarMenuInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class BarRootMenuPresenter extends BasePresenter<Long, Observable<BarMenuRestModel>> {
    private GetBarMenuInteractor menuInteractor;

    public BarRootMenuPresenter(BaseActivity activity) {
        super(activity);
        menuInteractor = new GetBarMenuInteractor(getActivity());
    }

    @Override public Observable<BarMenuRestModel> process(Long barId) {
        return menuInteractor.process(barId);
    }
}
