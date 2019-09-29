package app.delivering.mvp.bars.detail.init.action.click.binder;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.component.bar.detail.action.CustomBottomActionButton;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.action.click.events.ActionOpenTabEvent;
import app.delivering.mvp.bars.detail.init.action.click.events.ActionViewTabEvent;
import app.delivering.mvp.bars.detail.init.action.click.events.CallUberToTheVenueEvent;
import butterknife.BindView;
import butterknife.OnClick;

public class BarDetailClickActionButtonBinder extends BaseBinder {
    @BindView(R.id.bar_detail_action_button) CustomBottomActionButton actionButton;

    public BarDetailClickActionButtonBinder(BarDetailActivity activity) {
        super(activity);
    }

    @OnClick(R.id.bar_detail_action_button) void action(CustomBottomActionButton button){
        switch (button.barDetailActionState()){
            case UBER_CALL:
                EventBus.getDefault().post(new CallUberToTheVenueEvent(actionButton.getUberEstimation()));
                break;
            case OPEN_TAB:
                EventBus.getDefault().post(new ActionOpenTabEvent());
                break;
            case VIEW_TAB:
                EventBus.getDefault().post(new ActionViewTabEvent());
                break;
        }
    }
}
