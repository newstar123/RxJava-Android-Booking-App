package app.delivering.mvp.main.show.binder;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.core.bars.locations.entity.LocationsModel;
import app.core.bars.locations.entity.event.LocationsModelEvent;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.market.SearchMarketActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.delivering.mvp.main.show.events.UpdateMapPositionHintEvent;
import app.delivering.mvp.main.show.presenter.SearchBarByCityPresenter;
import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class SearchBarsByCityBinder extends BaseBinder{

    @BindView(R.id.selected_market) TextView title;
    @BindView(R.id.expand_hint_button) ImageView hintButton;

    private SearchBarByCityPresenter searchBarByCityPresenter;
    private List<LocationsModel> initialLocationList;
    private String currentMarketName;

    public SearchBarsByCityBinder(BaseActivity activity) {
        super(activity);
        searchBarByCityPresenter = new SearchBarByCityPresenter(getActivity());
        initialLocationList = new ArrayList<>();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(LocationsModelEvent event) {
        if (initialLocationList.isEmpty())
            searchBarByCityPresenter.process(event.getModel())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(city -> {
                        EventBus.getDefault().postSticky(city);
                        initialLocationList.addAll(event.getModel());
                    }, e -> { }, () -> {});
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCitySelected(CitiesModel citiesModel) {
        currentMarketName = citiesModel.getSelectCityName();
        if (citiesModel.isManualRefreshing()) {
            initialLocationList.clear();
            onStartEvent(new LocationsModelEvent(citiesModel.getCities()));
        }
        title.setText(citiesModel.getSelectCityName());
    }

    @OnClick(R.id.selected_market) void openSearchMarketScreen(){
        Intent intent = new Intent(getActivity(), SearchMarketActivity.class);
        intent.putExtra(SearchMarketActivity.CURRENT_MARKET_NAME, currentMarketName);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.animation_show, R.anim.animation_stay);
    }

    @OnClick(R.id.expand_hint_button) void onClickHint() {
        EventBus.getDefault().post(new UpdateMapPositionHintEvent());
    }

}
