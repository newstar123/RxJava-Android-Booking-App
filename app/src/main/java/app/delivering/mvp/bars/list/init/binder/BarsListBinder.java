package app.delivering.mvp.bars.list.init.binder;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.component.bar.lists.adapter.BarItemViewAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ScreenSizeInterface;
import app.delivering.mvp.bars.floating.events.ShowBarListFloatingEvent;
import app.delivering.mvp.bars.list.init.enums.BarListFilterType;
import app.delivering.mvp.bars.list.init.model.SortedBarListModel;
import app.delivering.mvp.bars.list.init.presenter.GetBarListPresenter;
import app.delivering.mvp.bars.list.refresh.events.RefreshBarListEvent;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.show.model.CitiesModel;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class BarsListBinder extends BaseBinder implements ScreenSizeInterface {
    private final BarItemViewAdapter adapter;
    @BindView(R.id.bars_list) ParallaxRecyclerView barsRecyclerView;
    @BindView(R.id.bar_list_swipe_refresh) SwipeRefreshLayout refreshLayout;
    private GetBarListPresenter presenter;
    private final LinearLayoutManager linearLayoutManager;
    private final InitExceptionHandler initExceptionHandler;

    public BarsListBinder(BaseFragment fragment, BarListFilterType filterType) {
        super(fragment.getBaseActivity());
        this.presenter = new GetBarListPresenter(getActivity(), filterType);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        adapter = new BarItemViewAdapter(filterType);
        linearLayoutManager = new LinearLayoutManager(getActivity());
    }

    @Override public void afterViewsBounded() {
        adapter.setDisplaySize(this.getDisplaySize(getActivity()));
        barsRecyclerView.setLayoutManager(linearLayoutManager);
        barsRecyclerView.setHasFixedSize(true);
        barsRecyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this::sendUpdatesToRefreshList);
        refreshLayout.setColorSchemeResources(
                R.color.musicVenue,
                R.color.pub,
                R.color.neighborhood,
                R.color.beerGarden,
                R.color.gastroPub,
                R.color.lounge);
    }

    private void sendUpdatesToRefreshList() {
        EventBus.getDefault().post(new RefreshBarListEvent());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadList(CitiesModel cities) {
        if (!cities.isManualRefreshing())
            showProgress();
        presenter.process(cities)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(this::show, this::onError, ()->refreshLayout.setRefreshing(false));
    }

    private void show(SortedBarListModel sortedBarListModel) {
        hideProgress();
        adapter.setModels(sortedBarListModel);
        refreshLayout.setRefreshing(false);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.list_layout_animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                EventBus.getDefault().post(new ShowBarListFloatingEvent());
                barsRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        barsRecyclerView.startAnimation(animation);
    }

    private void onError(Throwable throwable) {
        hideProgress();
        refreshLayout.setRefreshing(false);
        initExceptionHandler.showError(throwable, view -> loadList(new CitiesModel()));
    }

}