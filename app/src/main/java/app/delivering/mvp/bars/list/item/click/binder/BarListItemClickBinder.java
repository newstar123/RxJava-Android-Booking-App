package app.delivering.mvp.bars.list.item.click.binder;

import android.app.ActivityOptions;
import android.content.Intent;
import android.util.Pair;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.list.init.enums.BarListFilterType;
import app.delivering.mvp.bars.list.item.click.events.OnBarItemClickEvent;
import app.delivering.mvp.bars.list.item.presenter.VenueListItemClickPresenter;

public class BarListItemClickBinder extends BaseBinder {
    public static final String DETAIL_BAR_ID = "DETAIL_BAR_ID";

    private BarListFilterType filterType;
    private final VenueListItemClickPresenter clickPresenter;


    public BarListItemClickBinder(BaseFragment fragment, BarListFilterType filterType) {
        super(fragment.getBaseActivity());
        this.filterType = filterType;
        clickPresenter = new VenueListItemClickPresenter(getActivity());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadList(OnBarItemClickEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (event.getFilterType() == filterType)
            clickPresenter.process(event)
                    .subscribe(intent -> startDetail(intent, event));
    }

    private void startDetail(Intent intent, OnBarItemClickEvent event) {
        if (event.isClickFromMap())
            getActivity().startActivity(intent);
        else
            startWithTransition(intent, event);
    }

    private void startWithTransition(Intent intent, OnBarItemClickEvent event) {
        Pair<View, String> photo = Pair.create(event.getStartedView(), getString(R.string.bar_detail_photo_transition));
        Pair<View, String> typeIndicator = Pair.create(event.getTypeIndicator(), getString(R.string.bar_detail_type_indicator_transition));
        Pair<View, String> type = Pair.create(event.getType(), getString(R.string.bar_detail_type_transition));
        Pair<View, String> name = Pair.create(event.getName(), getString(R.string.bar_detail_name_transition));
        Pair<View, String> discount = Pair.create(event.getDiscount(), getString(R.string.bar_detail_discount_transition));
        Pair<View, String> route = Pair.create(event.getRoute(), getString(R.string.bar_detail_route_transition));
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), photo, typeIndicator, type, name, discount, route);
        getActivity().startActivity(intent, options.toBundle());
    }
}
