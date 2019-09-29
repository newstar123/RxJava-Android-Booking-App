package app.delivering.component.coach.tab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.coach.tab.init.InitTabCoachMarkBinder;

public class TabCoachMarkFragment extends BaseFragment {

    private InitTabCoachMarkBinder initBinder;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_coach_mark, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        initBinder = new InitTabCoachMarkBinder(getBaseActivity());
        addItemForViewsInjection(initBinder, view);
    }

    @Override
    public void onDestroyView() {
        initBinder.setUpCoachMarkState();
        super.onDestroyView();
    }

}
