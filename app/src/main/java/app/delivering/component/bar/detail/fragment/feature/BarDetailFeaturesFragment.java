package app.delivering.component.bar.detail.fragment.feature;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.bars.detail.init.tablist.list.feature.binder.BarDetailFeatureListBinder;

public class BarDetailFeaturesFragment extends BaseFragment {

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_detail_feature_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        BarDetailFeatureListBinder barDetailFeatureListBinder = new BarDetailFeatureListBinder(this);
        addToEventBusAndViewInjection(barDetailFeatureListBinder, view);
    }
}
