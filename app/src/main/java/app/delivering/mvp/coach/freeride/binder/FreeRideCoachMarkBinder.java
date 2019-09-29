package app.delivering.mvp.coach.freeride.binder;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.freeride.events.UpdateFreeRideListEvent;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.OnClick;

public class FreeRideCoachMarkBinder extends BaseBinder {

    public FreeRideCoachMarkBinder(BaseActivity activity) {
        super(activity);
    }

    @OnClick(R.id.free_ride_coach_got_it) void gotIt(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.free_ride_coach_button) void onClickFreeRide(){
        EventBus.getDefault().postSticky(new UpdateFreeRideListEvent(true));
        getActivity().onBackPressed();
    }

    @Override
    public void afterViewsBounded() {
        super.afterViewsBounded();
        QorumSharedCache.checkUberFreeRideMark()
                .save(BaseCacheType.BOOLEAN, true);
    }
}
