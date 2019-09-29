package app.qamode.component;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import app.delivering.component.BaseFragment;
import app.qamode.mvp.environment.server.binder.CustomEnvironmentBinder;
import app.qamode.mvp.environment.uber.UberApiUsageTypeBinder;

public class QaEnvironmentTabFragment extends BaseFragment {

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa_mode_environment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        CustomEnvironmentBinder customEnvironmentBinder = new CustomEnvironmentBinder(getBaseActivity());
        addToEventBusAndViewInjection(customEnvironmentBinder, view);
        UberApiUsageTypeBinder uberApiUsageTypeBider = new UberApiUsageTypeBinder(getBaseActivity());
        addItemForViewsInjection(uberApiUsageTypeBider, view);
    }
}
