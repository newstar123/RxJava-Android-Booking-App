package app.delivering.mvp.bars.detail.init.tablist.list.hours.binder;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.component.bar.detail.fragment.hours.adapter.BarDetailHoursAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.tablist.height.model.BarDetailHoursModel;
import app.delivering.mvp.bars.detail.init.tablist.list.hours.presenter.BarDetailHoursPresenter;
import butterknife.BindView;

public class BarDetailHoursListBinder extends BaseBinder {
    private BarDetailHoursPresenter hoursPresenter;
    private final BarDetailHoursAdapter adapter;
    @BindView(R.id.bar_detail_feature_list) RecyclerView recyclerView;

    public BarDetailHoursListBinder(BaseFragment fragment) {
        super(fragment.getBaseActivity());
        adapter = new BarDetailHoursAdapter();
        hoursPresenter = new BarDetailHoursPresenter(getActivity());
    }

    @Override public void afterViewsBounded() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadList(BarDetailHoursModel listModel) {
        hoursPresenter.process(listModel)
                .subscribe(adapter::setModels);
    }
}
