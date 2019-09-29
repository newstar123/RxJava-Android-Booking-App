package app.delivering.mvp.bars.market.init.binder;

import android.app.Activity;
import android.graphics.Point;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import app.R;
import app.core.bars.locations.entity.LocationsModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.market.adapter.MarketListAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ScreenSizeInterface;
import app.delivering.mvp.bars.market.init.presenter.SearchMarketPresenter;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class SearchMarketBinder extends BaseBinder implements MarketListAdapter.ClickListener, ScreenSizeInterface {
    @BindView(R.id.market_recycler) RecyclerView marketList;
    @BindView(R.id.search_market) EditText searchField;
    @BindView(R.id.main_container) CoordinatorLayout rootLayout;

    private final SearchMarketPresenter searchMarketPresenter;
    private final MarketListAdapter marketListAdapter;
    private String currentMarketName;
    private CitiesModel citiesModel;

    private int rootHeight = 0;
    private int contentBottom = 0;
    private int keypadHeight = 0;

    public SearchMarketBinder(BaseActivity activity, String currentMarketName) {
        super(activity);
        searchMarketPresenter = new SearchMarketPresenter(activity);
        this.currentMarketName = currentMarketName;
        marketListAdapter = new MarketListAdapter(this);
    }

    @Override
    public void afterViewsBounded() {
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Point size = this.getDisplaySize(getActivity());
            rootHeight = size.y;
            contentBottom = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getBottom();
            keypadHeight = rootHeight - contentBottom;
        });

        marketList.setLayoutManager(new LinearLayoutManager(getActivity()));
        marketList.setHasFixedSize(true);
        marketList.setAdapter(marketListAdapter);
        searchMarketPresenter.process(currentMarketName)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(marketListAdapter::setModels, e -> {}, () -> {});

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                marketListAdapter.filterBy(query.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkKeyboardVisibility(boolean isMarketClicked) {
        if (keypadHeight >= rootHeight * 0.15) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            sendCitiesModelUpdate(isMarketClicked);
        } else
            getActivity().onBackPressed();
    }

    private void sendCitiesModelUpdate(boolean isMarketClicked) {
        Observable.timer(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    if (isMarketClicked)
                        EventBus.getDefault().postSticky(citiesModel);
                    getActivity().onBackPressed();
                }, err -> {}, () -> {});
    }

    @OnClick(R.id.cancel_searching) void cancel() {
        checkKeyboardVisibility(false);
    }

    @Override
    public void onItemClicked(List<LocationsModel> filteredModels, CitiesModel citiesModel, int pos) {
        this.citiesModel = citiesModel;
        checkKeyboardVisibility(true);
        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.
                getSearchLocationEvent(filteredModels.get(pos).getLabel()));
    }
}
