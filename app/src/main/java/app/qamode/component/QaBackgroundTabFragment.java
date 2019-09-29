package app.qamode.component;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import app.delivering.component.BaseFragment;
import app.qamode.mvp.background.checkout.AutoCheckoutDistanceVisibilityBinder;
import app.qamode.mvp.background.logs.QaModeLogsInitBinder;
import app.qamode.mvp.background.venuelocation.QaModeFakeVenueLocationBinder;

public class QaBackgroundTabFragment extends BaseFragment {

    @Nullable
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa_mode_background, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        QaModeLogsInitBinder initBinder = new QaModeLogsInitBinder(getBaseActivity());
        addItemForViewsInjection(initBinder, view);
        QaModeFakeVenueLocationBinder fakeVenueLocationBinder = new QaModeFakeVenueLocationBinder(getBaseActivity());
        addItemForViewsInjection(fakeVenueLocationBinder, view);
        AutoCheckoutDistanceVisibilityBinder checkoutDistanceVisibilityBinder = new AutoCheckoutDistanceVisibilityBinder(getBaseActivity());
        addItemForViewsInjection(checkoutDistanceVisibilityBinder, view);
    }
}
