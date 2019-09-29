package app.delivering.mvp.bars.detail.init.tablist.list.feature.binder;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.component.bar.detail.fragment.feature.adapter.BarDetailFeaturesAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.init.tablist.height.model.BarDetailFeatureModel;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.presenter.BarDetailFeaturePresenter;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailFeatureListBinder extends BaseBinder {
    private BarDetailFeaturePresenter featurePresenter;
    private final BarDetailFeaturesAdapter adapter;
    @BindView(R.id.bar_detail_feature_list) RecyclerView recyclerView;

    public BarDetailFeatureListBinder(BaseFragment fragment) {
        super(fragment.getBaseActivity());
        adapter = new BarDetailFeaturesAdapter();
        featurePresenter = new BarDetailFeaturePresenter(getActivity());
    }

    @Override public void afterViewsBounded() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadList(BarDetailFeatureModel listModel) {
        featurePresenter.process(listModel.getList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::setModels, e->{ }, ()->{

                });
    }
}
