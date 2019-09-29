package app.delivering.mvp.bars.detail.checkin.click.binder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.click.events.OpenTabClickEvent;
import app.delivering.mvp.bars.detail.checkin.click.events.ViewTabEvent;
import app.delivering.mvp.bars.detail.checkin.click.presenter.BarDetailCheckinClickButtonPresenter;
import app.delivering.mvp.bars.detail.init.action.click.events.ActionOpenTabEvent;
import app.delivering.mvp.bars.detail.init.action.click.events.ActionViewTabEvent;

public class BarDetailCheckinClickButtonBinder extends BaseBinder {
    private final long currentBarId;
    private BarDetailCheckinClickButtonPresenter clickButtonPresenter;

    public BarDetailCheckinClickButtonBinder(BaseActivity activity) {
        super(activity);
        currentBarId = ((BarDetailActivity)getActivity()).getInitialModel().getBarId();
        clickButtonPresenter = new BarDetailCheckinClickButtonPresenter(getActivity());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void open(ActionOpenTabEvent openTabEvent) {
        EventBus.getDefault().removeStickyEvent(openTabEvent);
        clickButtonPresenter.process().subscribe(barCheckinIdsModel -> checkActionType());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void open(ActionViewTabEvent viewTabEvent) {
        EventBus.getDefault().removeStickyEvent(viewTabEvent);
        clickButtonPresenter.process().subscribe(barCheckinIdsModel -> EventBus.getDefault().postSticky(new ViewTabEvent(barCheckinIdsModel.getCheckinId())));
    }

    private void checkActionType() {
        OpenTabClickEvent event = new OpenTabClickEvent();
        event.setCurrentBarId(currentBarId);
        EventBus.getDefault().post(event);
    }

}
