package app.delivering.mvp.bars.detail.checkin.view.binder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.click.events.ViewTabEvent;
import app.delivering.mvp.bars.detail.init.get.model.BarDetailModel;
import app.delivering.mvp.tab.init.model.InitialTabActivityModel;

public class BarDetailCheckinViewTabBinder extends BaseBinder {
    private InitialTabActivityModel initialTabModel;

    public BarDetailCheckinViewTabBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onViewTab(ViewTabEvent viewTabEvent) {
        EventBus.getDefault().removeStickyEvent(viewTabEvent);
        if (initialTabModel != null)
            initialTabModel.setCheckInId(viewTabEvent.getCheckinId());
        TabActivity.launch(getActivity(), initialTabModel);
        getActivity().overridePendingTransition(R.anim.animation_slide_to_top, R.anim.animation_stay);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDetail(BarDetailModel detailModel) {
        initialTabModel = new InitialTabActivityModel();
        initialTabModel.setBarId(detailModel.getBarModel().getId());
        initialTabModel.setBarName(detailModel.getBarModel().getName());
    }
}
