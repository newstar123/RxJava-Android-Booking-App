package app.delivering.mvp.main.nearest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.delivering.mvp.main.show.events.UpdateMapPositionHintEvent;

public class NearestMarketBinder extends BaseBinder {

    private CitiesModel cities;

    public NearestMarketBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadList(CitiesModel cities) {
        this.cities = cities;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateMapPosition(UpdateMapPositionHintEvent event) {
        this.cities.setManualRefreshing(true);
        EventBus.getDefault().postSticky(cities);
    }

}
