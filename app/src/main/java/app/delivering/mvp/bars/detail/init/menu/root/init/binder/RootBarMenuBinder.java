package app.delivering.mvp.bars.detail.init.menu.root.init.binder;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.trello.rxlifecycle.android.ActivityEvent;

import app.R;
import app.core.bars.menu.entity.BarMenuRestModel;
import app.delivering.component.bar.detail.menu.BarDetailRootMenuActivity;
import app.delivering.component.bar.detail.menu.adapter.BarRootMenuAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.menu.root.init.presenter.BarRootMenuPresenter;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class RootBarMenuBinder extends BaseBinder {
    private final BarRootMenuAdapter adapter;
    @BindView(R.id.bar_root_menu_container) View container;
    @BindView(R.id.bar_root_menu_recycler) RecyclerView recyclerView;
    @BindView(R.id.bar_root_menu_progress) MaterialProgressBar progressBar;
    @BindView(R.id.menu_list_swipe_refresh) SwipeRefreshLayout refreshLayout;
    private BarRootMenuPresenter presenter;
    private final InitExceptionHandler initExceptionHandler;

    public RootBarMenuBinder(BarDetailRootMenuActivity activity) {
        super(activity);
        this.presenter = new BarRootMenuPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        adapter = new BarRootMenuAdapter();
    }

    @Override public void afterViewsBounded() {
        setProgress(progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        loadMenu();
    }

    private void loadMenu() {
        showProgress();
        presenter.process(getBarId())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError);
    }

    private Long getBarId() {
        long barId = (getActivity()).getIntent().getLongExtra(BarDetailRootMenuActivity.BAR_ID_FOR_MENU, 0);
        return barId;
    }

    private void show(BarMenuRestModel menuRestModel) {
        adapter.setModels(menuRestModel.getMenu());
        hideProgress();
    }

    private void setUpSwipeRefreshLayout(boolean isRefreshing) {
        refreshLayout.setColorSchemeResources(R.color.musicVenue,
                R.color.pub,
                R.color.neighborhood,
                R.color.beerGarden,
                R.color.gastroPub,
                R.color.lounge);

        refreshLayout.setRefreshing(isRefreshing);
    }

    private void onError(Throwable throwable) {
        hideProgress();
        setUpSwipeRefreshLayout(true);
    }
}
